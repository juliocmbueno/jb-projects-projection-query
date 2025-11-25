package br.com.jbProjects.processor;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.mapper.ProjectionMappers;
import br.com.jbProjects.processor.filter.ProjectionFilterOperatorProvider;
import br.com.jbProjects.processor.filter.handler.ProjectionFilterOperatorHandler;
import br.com.jbProjects.processor.query.ProjectionQuery;
import br.com.jbProjects.processor.query.ProjectionSelectInfo;
import br.com.jbProjects.processor.query.ProjectionSpecification;
import br.com.jbProjects.validations.ProjectionValidations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public class ProjectionProcessor {

    private final EntityManager entityManager;

    public ProjectionProcessor(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public <T> List<T> execute(Class<T> projectionClass){
        ProjectionValidations.validateProjectionClass(projectionClass);

        Projection projection = projectionClass.getAnnotation(Projection.class);

        return execute(ProjectionQuery.fromTo(projection.of(), projectionClass));
    }

    public <FROM, TO> List<TO> execute(ProjectionQuery<FROM, TO> projectionQuery){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<FROM> from = criteriaQuery.from(projectionQuery.fromClass());

        criteriaQuery.distinct(projectionQuery.isDistinct());
        addSelects(projectionQuery, criteriaQuery, from);
        applyFilters(projectionQuery, criteriaBuilder, criteriaQuery, from);
        applyOrders(projectionQuery, criteriaBuilder, criteriaQuery, from);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
        applyPaging(projectionQuery, typedQuery);
        List<Tuple> tuples = typedQuery.getResultList();

        return mapTuplesToProjectionClass(tuples, projectionQuery.toClass());
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
            predicates.add(spec.toPredicate(criteriaBuilder, criteriaQuery, from));
        }

        for (var filter : projectionQuery.getFilters()) {
            Path<?> path = projectionQuery.resolvePath(from, filter.path());
            ProjectionFilterOperatorHandler operator = ProjectionFilterOperatorProvider.getInstance().get(filter.operator());

            Predicate predicate = operator.toPredicate(criteriaBuilder, path, filter.value());
            predicates.add(predicate);
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
