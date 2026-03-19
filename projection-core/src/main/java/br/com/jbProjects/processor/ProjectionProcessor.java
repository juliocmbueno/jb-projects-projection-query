package br.com.jbProjects.processor;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.builder.ProjectionTypedQueryBuilder;
import br.com.jbProjects.mapper.ProjectionMappers;
import br.com.jbProjects.processor.pageable.ProjectionPage;
import br.com.jbProjects.processor.query.ProjectionQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
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

    private final ProjectionTypedQueryBuilder queryBuilder = new ProjectionTypedQueryBuilder();
    private final EntityManager entityManager;

    /**
     * Constructs a ProjectionProcessor with the given EntityManager.
     *
     * @param entityManager The EntityManager used for executing queries.
     */
    public ProjectionProcessor(EntityManager entityManager){
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager must not be null");;
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
        TypedQuery<Tuple> typedQuery = queryBuilder.build(projectionQuery, entityManager);

        long start = System.nanoTime();
        List<Tuple> tuples = typedQuery.getResultList();
        long elapsed = (System.nanoTime() - start) / 1_000_000;

        log.info(
                "ProjectionQuery executed in {} ms ({} results)",
                elapsed,
                tuples.size()
        );

        return mapTuplesToProjectionClass(tuples, projectionQuery.toClass());
    }

    /**
     * Executes a pageable projection query and returns a paginated result.
     *
     * @param projectionQuery The projection query containing all configurations including paging.
     * @param <FROM>          The source entity type.
     * @param <TO>            The target projection type.
     * @return A {@link ProjectionPage} containing the paginated results.
     * @throws IllegalStateException if the projection query does not have paging configured.
     */
    public <FROM, TO> ProjectionPage<TO> executePageable(ProjectionQuery<FROM, TO> projectionQuery){
        if(!projectionQuery.hasPaging()){
            throw new IllegalStateException("ProjectionQuery must have paging to execute pageable.");
        }

        List<TO> items = execute(projectionQuery);
        if(items.isEmpty()){
            return ProjectionPage.empty(projectionQuery.getPaging());
        }

        TypedQuery<Long> typedQuery = queryBuilder.buildCountQuery(projectionQuery, entityManager);

        long start = System.nanoTime();
        Long singleResult = typedQuery.getSingleResult();
        long elapsed = (System.nanoTime() - start) / 1_000_000;

        log.info(
                "ProjectionQuery Pageable count executed in {} ms",
                elapsed
        );

        ProjectionPage<TO> page = ProjectionPage.of(items, singleResult, projectionQuery.getPaging());

        log.debug(
                "Page created: pageNumber={}, pageSize={}, totalElements={}, totalPages={}, hasNext={}, hasPrevious={}",
                page.pageNumber(),
                page.pageSize(),
                page.totalElements(),
                page.totalPages(),
                page.hasNext(),
                page.hasPrevious()
        );

        return page;
    }

    private <T> List<T> mapTuplesToProjectionClass(List<Tuple> tuples, Class<T> projectionClass) {
        return tuples
                .stream()
                .map(tuple -> ProjectionMappers.tupleToObject(tuple, projectionClass))
                .collect(Collectors.toList());
    }
}
