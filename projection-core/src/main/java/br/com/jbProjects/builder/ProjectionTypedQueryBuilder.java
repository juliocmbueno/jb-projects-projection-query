package br.com.jbProjects.builder;

import br.com.jbProjects.processor.query.ProjectionQuery;
import br.com.jbProjects.processor.query.ProjectionSelectInfo;
import br.com.jbProjects.processor.query.ProjectionSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio.bueno on 18/03/2026.
 * <p>Builder responsible for constructing JPA Criteria queries from ProjectionQuery definitions.</p>
 *
 * <p>This class encapsulates all the logic for translating a high-level {@link ProjectionQuery}
 * into executable JPA {@link TypedQuery} instances. It handles:
 * <ul>
 *   <li>Field selection with custom handlers and aggregations</li>
 *   <li>JOIN resolution and path navigation</li>
 *   <li>Filter and specification application</li>
 *   <li>Ordering and sorting</li>
 *   <li>Pagination (offset and limit)</li>
 *   <li>DISTINCT queries</li>
 *   <li>COUNT queries for pagination metadata</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b> This class is stateless and thread-safe. It can be safely
 * shared across multiple threads or used as a singleton.
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * EntityManager entityManager = // ... obtain EntityManager
 * ProjectionTypedQueryBuilder builder = new ProjectionTypedQueryBuilder();
 *
 * ProjectionQuery<Customer, CustomerDTO> query = ProjectionQuery
 *     .fromTo(Customer.class, CustomerDTO.class)
 *     .filter("status", ProjectionFilterOperator.EQUAL, "ACTIVE")
 *     .order("name", OrderDirection.ASC)
 *     .paging(0, 20);
 *
 * // Build executable query
 * TypedQuery<Tuple> typedQuery = builder.build(query, entityManager);
 *
 * // Execute
 * List<Tuple> results = typedQuery.getResultList();
 * }</pre>
 *
 * <p><b>Logging:</b> This class logs detailed information about query construction:
 * <ul>
 *   <li>INFO: Query creation summary (from/to classes, distinct, paging)</li>
 *   <li>DEBUG: Filters, orders, and paging details</li>
 *   <li>TRACE: Individual filter and order additions</li>
 * </ul>
 *
 * @see br.com.jbProjects.processor.ProjectionProcessor
 * @see br.com.jbProjects.processor.ProjectionProcessorDebug
 * @see ProjectionQuery
 */
@Slf4j
public class ProjectionTypedQueryBuilder {

    /**
     * Constructs a new ProjectionTypedQueryBuilder.
     *
     * <p>This builder is stateless and can be reused for multiple query
     * construction operations.
     */
    public ProjectionTypedQueryBuilder() {}

    /**
     * Builds a JPA TypedQuery from the given ProjectionQuery.
     *
     * <p>This method orchestrates the complete query building process:
     * <ol>
     *   <li>Creates a CriteriaQuery with proper distinct setting</li>
     *   <li>Adds field selections with handlers and aggregations</li>
     *   <li>Applies all filters and specifications</li>
     *   <li>Applies ordering</li>
     *   <li>Adds query comment for debugging</li>
     *   <li>Applies pagination (offset/limit)</li>
     * </ol>
     *
     * <p>The resulting TypedQuery is ready for execution via {@code getResultList()}
     * or {@code getSingleResult()}.
     *
     * <p><b>Query Comment:</b> The built query includes a Hibernate comment indicating
     * the source and target classes for easier identification in database logs:
     * <pre>
     * -- ProjectionQuery created from Customer to CustomerDTO
     * </pre>
     *
     * <p><b>Logging:</b> Logs comprehensive information about the query being built:
     * <ul>
     *   <li>INFO: Basic query structure (from, to, distinct, paging)</li>
     *   <li>DEBUG: Filter count, order count, pagination details</li>
     *   <li>TRACE: Each individual filter and order being added</li>
     * </ul>
     *
     * @param projectionQuery The projection query definition containing filters, orders, etc.
     * @param entityManager The EntityManager to create the query from
     * @param <FROM> The source entity type
     * @param <TO> The target projection type
     * @return TypedQuery ready for execution
     * @throws IllegalArgumentException if projectionQuery or entityManager is null
     *
     * @see #addSelects(ProjectionQuery, CriteriaBuilder, CriteriaQuery, Root)
     * @see #applyFilters(ProjectionQuery, CriteriaBuilder, CriteriaQuery, Root)
     * @see #applyOrders(ProjectionQuery, CriteriaBuilder, CriteriaQuery, Root)
     * @see #applyPaging(ProjectionQuery, TypedQuery)
     */
    public <FROM, TO> TypedQuery<Tuple> build(ProjectionQuery<FROM, TO> projectionQuery, EntityManager entityManager){
        log.info(
                "Creating Query [from={}, to={}, distinct={}, paging={}]",
                projectionQuery.fromClass().getSimpleName(),
                projectionQuery.toClass().getSimpleName(),
                projectionQuery.isDistinct(),
                projectionQuery.hasPaging()
        );

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<FROM> from = criteriaQuery.from(projectionQuery.fromClass());

        criteriaQuery.distinct(projectionQuery.isDistinct());
        addSelects(projectionQuery, criteriaBuilder, criteriaQuery, from);
        applyFilters(projectionQuery, criteriaBuilder, criteriaQuery, from);
        applyOrders(projectionQuery, criteriaBuilder, criteriaQuery, from);

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
        applyComment(typedQuery, projectionQuery);
        applyPaging(projectionQuery, typedQuery);
        return typedQuery;
    }

    /**
     * Builds a COUNT query for pagination metadata.
     *
     * <p>This method creates a separate COUNT query to determine the total number
     * of rows that match the filters, without applying pagination. This is essential
     * for implementing pagination with total page count.
     *
     * <p><b>Important Differences from Regular Query:</b>
     * <ul>
     *   <li>No ORDER BY clause (not needed for counting)</li>
     *   <li>No LIMIT/OFFSET (counting all matching rows)</li>
     *   <li>No field selections (only COUNT expression)</li>
     *   <li>Clears multiselect and groupBy before adding count</li>
     * </ul>
     *
     * <p><b>DISTINCT Handling:</b>
     * <ul>
     *   <li>If query is distinct: uses {@code COUNT(DISTINCT entity)}</li>
     *   <li>Otherwise: uses {@code COUNT(entity)}</li>
     * </ul>
     *
     * <p><b>Example SQL Generated:</b>
     * <pre>
     * -- Without distinct:
     * SELECT COUNT(c1_0) FROM customer c1_0 WHERE c1_0.status = ?
     *
     * -- With distinct:
     * SELECT COUNT(DISTINCT c1_0) FROM customer c1_0 WHERE c1_0.status = ?
     * </pre>
     *
     * <p><b>Usage Example:</b>
     * <pre>{@code
     * ProjectionQuery<Customer, CustomerDTO> query = ... with filters ... ;
     * TypedQuery<Long> countQuery = builder.buildCountQuery(query, entityManager);
     * Long totalRows = countQuery.getSingleResult();
     * }</pre>
     *
     * <p><b>Note:</b> This method creates a copy of the query to avoid
     * modifying the original, making it safe to use concurrently or reuse the
     * original query after calling this method.
     *
     * @param projectionQuery The projection query to count results for (will not be modified)
     * @param entityManager The EntityManager to create the query from
     * @param <FROM> The source entity type
     * @param <TO> The target projection type (not used in count query)
     * @return TypedQuery that returns the total count as Long
     *
     * @see br.com.jbProjects.processor.ProjectionProcessor#executePageable(ProjectionQuery)
     */
    public <FROM, TO> TypedQuery<Long> buildCountQuery(ProjectionQuery<FROM, TO> projectionQuery, EntityManager entityManager){
        ProjectionQuery<FROM, TO> projectionCount = projectionQuery.copy();

        log.info(
                "Creating Query Pageable count [from={}, to={}, distinct={}]",
                projectionCount.fromClass().getSimpleName(),
                projectionCount.toClass().getSimpleName(),
                projectionCount.isDistinct()
        );

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<FROM> from = criteriaQuery.from(projectionCount.fromClass());

        addSelects(projectionCount, criteriaBuilder, criteriaQuery, from);
        applyFilters(projectionCount, criteriaBuilder, criteriaQuery, from);

        criteriaQuery.multiselect(List.of());
        criteriaQuery.groupBy(List.of());

        Expression<Long> countExpression = projectionCount.isDistinct() ? criteriaBuilder.countDistinct(from) : criteriaBuilder.count(from);
        criteriaQuery.select(countExpression);

        return entityManager.createQuery(criteriaQuery);
    }

    /**
     * Adds field selections to the criteria query.
     *
     * <p>This method delegates to {@link ProjectionSelectInfo} to process all
     * {@link br.com.jbProjects.annotations.ProjectionField} annotations and:
     * <ul>
     *   <li>Resolve field paths with automatic JOIN creation</li>
     *   <li>Apply custom handlers (COUNT, SUM, AVG, etc.)</li>
     *   <li>Set proper aliases for tuple mapping</li>
     *   <li>Configure GROUP BY clauses for aggregations</li>
     * </ul>
     *
     * <p><b>Automatic JOIN Resolution:</b>
     * Fields like {@code "address.city.name"} automatically create:
     * <pre>
     * INNER JOIN address ON customer.address_id = address.id
     * INNER JOIN city ON address.city_id = city.id
     * </pre>
     *
     * <p><b>GROUP BY Handling:</b>
     * When aggregate functions are used (COUNT, SUM, etc.), non-aggregated
     * fields are automatically added to GROUP BY clause.
     *
     * @param projectionQuery The projection query definition
     * @param criteriaBuilder JPA CriteriaBuilder
     * @param criteriaQuery The criteria query being built
     * @param from The root entity
     * @param <FROM> The source entity type
     * @param <TO> The target projection type
     *
     * @see ProjectionSelectInfo
     * @see br.com.jbProjects.annotations.ProjectionField
     */
    private <FROM, TO> void addSelects(ProjectionQuery<FROM, TO> projectionQuery, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery, Root<?> from) {
        ProjectionSelectInfo selectInfo = new ProjectionSelectInfo(projectionQuery, criteriaBuilder, from);
        criteriaQuery.multiselect(selectInfo.getSelections());
        criteriaQuery.groupBy(selectInfo.getGroupByFields());
    }

    /**
     * Applies all filters and specifications to the criteria query.
     *
     * <p><b>Logging:</b>
     * <ul>
     *   <li>TRACE: Each individual filter being added</li>
     *   <li>DEBUG: Summary of total specifications and filters</li>
     * </ul>
     *
     * @param projectionQuery The projection query with filters
     * @param criteriaBuilder JPA CriteriaBuilder
     * @param criteriaQuery The criteria query being built
     * @param from The root entity
     * @param <FROM> The source entity type
     * @param <TO> The target projection type
     *
     * @see ProjectionSpecification
     * @see br.com.jbProjects.processor.filter.ProjectionFilterExpression
     */
    private <FROM, TO> void applyFilters(ProjectionQuery<FROM, TO> projectionQuery, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> criteriaQuery, Root<FROM> from) {
        List<Predicate> predicates = new ArrayList<>();

        for (ProjectionSpecification<FROM> spec : projectionQuery.getSpecifications()) {
            predicates.add(spec.toPredicate(criteriaBuilder, criteriaQuery, from, projectionQuery.getPathResolver()));
        }

        for (var filter : projectionQuery.getFilters()) {
            predicates.add(filter.toPredicate(criteriaBuilder, criteriaQuery, from, projectionQuery.getPathResolver()));
            log.trace("ProjectionQuery filter added: {}", filter.toLogString());
        }

        log.debug(
                "ProjectionQuery filters summary: {} specifications, {} filters",
                projectionQuery.getSpecifications().size(),
                projectionQuery.getFilters().size()
        );

        if(!predicates.isEmpty()){
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }
    }

    /**
     * Applies ordering to the criteria query.
     *
     * <p>This method processes all {@link br.com.jbProjects.processor.order.ProjectionOrder}
     * instances defined in the query, resolving field paths and creating appropriate
     * JPA {@link Order} instances.
     *
     * <p><b>Logging:</b>
     * <ul>
     *   <li>TRACE: Each individual order being added (path and direction)</li>
     *   <li>DEBUG: Total number of orders applied</li>
     * </ul>
     *
     * @param projectionQuery The projection query with orders
     * @param criteriaBuilder JPA CriteriaBuilder
     * @param criteriaQuery The criteria query being built
     * @param from The root entity
     * @param <TO> The target projection type
     * @param <FROM> The source entity type
     *
     * @see br.com.jbProjects.processor.order.ProjectionOrder
     * @see br.com.jbProjects.processor.order.OrderDirection
     */
    private <TO, FROM> void applyOrders(ProjectionQuery<FROM, TO> projectionQuery, CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> criteriaQuery, Root<FROM> from) {
        List<Order> orders = projectionQuery
                .getOrders()
                .stream()
                .map(order -> {
                    Path<?> path = projectionQuery.resolvePath(from, order.path());
                    log.trace("ProjectionQuery order added: {} {}", order.path(), order.direction());
                    return order.direction().toOrder(criteriaBuilder, path);
                })
                .toList();

        if(!orders.isEmpty()){
            criteriaQuery.orderBy(orders);
        }

        log.debug("ProjectionQuery orders applied: {}", orders.size());
    }

    /**
     * Adds a Hibernate comment to the query for easier debugging.
     *
     * <p>This method attempts to add a SQL comment to the generated query indicating
     * the source and target classes. This comment appears in database logs and helps
     * identify queries when debugging or analyzing performance.
     *
     * <p><b>Example Comment:</b>
     * <pre>
     * -- ProjectionQuery created from Customer to CustomerDTO
     * SELECT c1_0.id, c1_0.name FROM customer c1_0 WHERE ...
     * </pre>
     *
     * <p><b>Hibernate-Specific:</b>
     * This feature requires Hibernate. If running with a different JPA provider,
     * the unwrap operation will fail silently and no comment will be added.
     *
     * <p><b>Error Handling:</b>
     * Any exceptions during unwrap or comment setting are caught and ignored,
     * as this is a best-effort debugging feature that should not break query execution.
     *
     * @param typedQuery The query to add comment to
     * @param projectionQuery The projection query (for class names)
     * @param <TO> The target projection type
     * @param <FROM> The source entity type
     */
    private <TO, FROM> void applyComment(TypedQuery<?> typedQuery, ProjectionQuery<FROM, TO> projectionQuery) {
        try{
            org.hibernate.query.Query<?> hibernateQuery = typedQuery.unwrap(org.hibernate.query.Query.class);
            hibernateQuery.setComment("ProjectionQuery created from " + projectionQuery.fromClass().getSimpleName() + " to " + projectionQuery.toClass().getSimpleName());
        }catch (Exception ignored){}
    }

    /**
     * Applies pagination (offset and limit) to the query.
     *
     * <p>This method sets the {@code OFFSET} and {@code LIMIT} clauses on the
     * TypedQuery based on the pagination configuration in the ProjectionQuery.
     *
     * <p><b>Pagination Parameters:</b>
     * <ul>
     *   <li><b>first (offset):</b> Number of rows to skip</li>
     *   <li><b>size (limit):</b> Maximum number of rows to return</li>
     * </ul>
     *
     * <p><b>Behavior:</b>
     * <ul>
     *   <li>If no paging is configured: No LIMIT/OFFSET applied (returns all rows)</li>
     *   <li>If paging is configured: Both OFFSET and LIMIT are applied</li>
     * </ul>
     *
     * <p><b>Logging:</b>
     * DEBUG level log entry showing first and size values when pagination is applied.
     *
     * @param projectionQuery The projection query with pagination config
     * @param typedQuery The query to apply pagination to
     * @param <FROM> The source entity type
     * @param <TO> The target projection type
     *
     * @see br.com.jbProjects.processor.query.ProjectionPaging
     */
    private <FROM, TO> void applyPaging(ProjectionQuery<FROM, TO> projectionQuery, TypedQuery<Tuple> typedQuery) {
        if(projectionQuery.hasPaging()){
            int first = projectionQuery.getPaging().first();
            int size = projectionQuery.getPaging().size();
            typedQuery.setFirstResult(first);
            typedQuery.setMaxResults(size);
            log.debug("ProjectionQuery paging applied: first={}, size={}", first, size);
        }
    }
}
