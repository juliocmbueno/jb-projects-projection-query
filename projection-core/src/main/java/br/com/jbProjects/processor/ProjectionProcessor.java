package br.com.jbProjects.processor;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.mapper.ProjectionMappers;
import br.com.jbProjects.processor.query.ProjectionQuery;
import br.com.jbProjects.processor.query.ProjectionSelectInfo;
import br.com.jbProjects.processor.query.ProjectionSpecification;
import br.com.jbProjects.validations.ProjectionValidations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Core engine for executing projection queries.</p>
 * <p>
 * The {@code ProjectionProcessor} is responsible for transforming a {@link ProjectionQuery}
 * into a list of results of the target projection class. It handles filtering,
 * specifications, ordering, pagination, and mapping from database tuples to DTOs.
 *
 * <p>It can be used in two main ways:
 * <ul>
 *     <li>Directly executing a projection class annotated with {@link Projection}.</li>
 *     <li>Executing a fully configured {@link ProjectionQuery} with custom filters, orders, and paging.</li>
 * </ul>
 *
 * <p><b>Example using a projection class:</b>
 * <pre>{@code
 * List<AuthorDTO> authors = processor.execute(AuthorDTO.class);
 * }</pre>
 *
 * <p><b>Example using a fully configured ProjectionQuery:</b>
 * <pre>{@code
 * ProjectionQuery<Author, AuthorDTO> query = ProjectionQuery.fromTo(Author.class, AuthorDTO.class)
 *     .filter("name", ProjectionFilterOperator.LIKE, "Júlio")
 *     .order("birthDate", OrderDirection.ASC)
 *     .paging(0, 20)
 *     .distinct();
 *
 * List<AuthorDTO> authors = processor.execute(query);
 * }</pre>
 *
 * <p>All internal query transformations, such as selecting fields, resolving paths,
 * applying filters, and mapping tuples, are handled transparently by this class.
 */
@Slf4j
public class ProjectionProcessor {

    private final EntityManager entityManager;

    /**
     * Constructs a ProjectionProcessor with the given EntityManager.
     *
     * @param entityManager The EntityManager used for executing queries.
     */
    public ProjectionProcessor(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    /**
     * Executes a projection query based on the provided projection class.
     *
     * @param projectionClass The class annotated with {@link Projection} defining the projection.
     * @param <T>             The type of the projection class.
     * @return A list of results mapped to the projection class.
     * @throws IllegalArgumentException if the projection class is not properly annotated.
     */
    public <T> List<T> execute(Class<T> projectionClass){
        ProjectionValidations.validateProjectionClass(projectionClass);

        Projection projection = projectionClass.getAnnotation(Projection.class);

        return execute(ProjectionQuery.fromTo(projection.of(), projectionClass));
    }

    /**
     * Executes a fully configured projection query.
     *
     * @param projectionQuery The projection query containing all configurations.
     * @param <FROM>          The source entity type.
     * @param <TO>            The target projection type.
     * @return A list of results mapped to the target projection class.
     */
    public <FROM, TO> List<TO> execute(ProjectionQuery<FROM, TO> projectionQuery){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<FROM> from = criteriaQuery.from(projectionQuery.fromClass());

        criteriaQuery.distinct(projectionQuery.isDistinct());
        addSelects(projectionQuery, criteriaQuery, from);
        applyFilters(projectionQuery, criteriaBuilder, criteriaQuery, from);
        applyOrders(projectionQuery, criteriaBuilder, criteriaQuery, from);

        log.debug("teste 11111111111111");
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
        applyComment(typedQuery, projectionQuery);

        applyPaging(projectionQuery, typedQuery);
        List<Tuple> tuples = typedQuery.getResultList();

        return mapTuplesToProjectionClass(tuples, projectionQuery.toClass());
    }

    private <TO, FROM> void applyComment(TypedQuery<Tuple> typedQuery, ProjectionQuery<FROM, TO> projectionQuery) {
        try{
            org.hibernate.query.Query<?> hibernateQuery = typedQuery.unwrap(org.hibernate.query.Query.class);
            hibernateQuery.setComment("ProjectionQuery created from " + projectionQuery.fromClass().getSimpleName() + " to " + projectionQuery.toClass().getSimpleName());
        }catch (Exception ignored){}
    }

    private <TO, FROM> void applyOrders(ProjectionQuery<FROM, TO> projectionQuery, CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> criteriaQuery, Root<FROM> from) {
        List<Order> orders = projectionQuery
                .getOrders()
                .stream()
                .map(order -> {
                    Path<?> path = projectionQuery.resolvePath(from, order.path());
                    return order.direction().toOrder(criteriaBuilder, path);
                })
                .toList();

        if(!orders.isEmpty()){
            criteriaQuery.orderBy(orders);
        }
    }

    private <FROM, TO> void addSelects(ProjectionQuery<FROM, TO> projectionQuery, CriteriaQuery<Tuple> criteriaQuery, Root<?> from) {
        ProjectionSelectInfo selectInfo = new ProjectionSelectInfo(projectionQuery, entityManager.getCriteriaBuilder(), from);
        criteriaQuery.multiselect(selectInfo.getSelections());
        criteriaQuery.groupBy(selectInfo.getGroupByFields());
    }

    private static <FROM, TO> void applyFilters(ProjectionQuery<FROM, TO> projectionQuery, CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> criteriaQuery, Root<FROM> from) {
        List<Predicate> predicates = new ArrayList<>();

        for (ProjectionSpecification<FROM> spec : projectionQuery.getSpecifications()) {
            predicates.add(spec.toPredicate(criteriaBuilder, criteriaQuery, from, projectionQuery.getPathResolver()));
        }

        for (var filter : projectionQuery.getFilters()) {
            predicates.add(filter.toPredicate(criteriaBuilder, criteriaQuery, from, projectionQuery.getPathResolver()));
        }

        if(!predicates.isEmpty()){
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }
    }

    private static <FROM, TO> void applyPaging(ProjectionQuery<FROM, TO> projectionQuery, TypedQuery<Tuple> typedQuery) {
        if(projectionQuery.hasPaging()){
            int first = projectionQuery.getPaging().first();
            int size = projectionQuery.getPaging().size();
            typedQuery.setFirstResult(first);
            typedQuery.setMaxResults(size);
        }
    }

    private static <T> List<T> mapTuplesToProjectionClass(List<Tuple> tuples, Class<T> projectionClass) {
        return tuples
                .stream()
                .map(tuple -> ProjectionMappers.tupleToObject(tuple, projectionClass))
                .collect(Collectors.toList());
    }
}
