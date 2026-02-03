package br.com.jbProjects.processor.filter;

import java.util.Collection;
import java.util.List;

/**
 * <p>Created by julio.bueno on 03/02/2026.</p>
 * <p>Utility class that provides a fluent DSL for creating projection filter expressions.</p>
 * <p>
 *  This class acts as the main entry point for building both simple and compound filters
 *  used by {@link br.com.jbProjects.processor.query.ProjectionQuery}. It abstracts the internal distinction between
 *  simple filters and compound filters, allowing users to focus only on expressing
 *  query conditions.
 * </p>
 * <p>
 *  The use of this class is the recommended way to create filters, as it offers
 *  better readability and a more expressive API compared to using filter classes directly.
 * </p>
 * <b>Examples</b>
 *
 * <pre>{@code
 * ProjectionQuery
 *     .fromTo(Customer.class, CustomerDTO.class)
 *     .filter(ProjectionFilters.equal("status", "ACTIVE"))
 *     .filter(
 *         ProjectionFilters.or(
 *             ProjectionFilters.equal("age", 18),
 *             ProjectionFilters.greaterThan("age", 65)
 *         )
 *     );
 * }</pre>
 *
 * @see ProjectionFilterExpression
 * @see ProjectionFilter
 * @see ProjectionCompoundFilter
 */
public final class ProjectionFilters {

    /** Private constructor to prevent instantiation. */
    private ProjectionFilters(){}

    /**
     * Creates a ProjectionFilterExpression instance.
     * @param path The field path to apply the filter on.
     * @param operator The {@link ProjectionFilterOperator} to use for filtering.
     * @param value  The value to compare against.
     * @return A new ProjectionFilterExpression instance.
     */
    public static ProjectionFilterExpression of(String path, ProjectionFilterOperator operator, Object value) {
        return ProjectionFilter.of(path, operator, value);
    }

    /**
     * Creates a ProjectionFilterExpression instance.
     * @param path The field path to apply the filter on.
     * @param operator The operator to use for filtering as a String.
     * @param value  The value to compare against.
     * @return A new ProjectionFilterExpression instance.
     */
    public static ProjectionFilterExpression of(String path, String operator, Object value) {
        return ProjectionFilter.of(path, operator, value);
    }

    /**
     * Creates a ProjectionFilterExpression that represents a logical AND of the provided expressions.
     * @param expressions The expressions to combine with AND.
     * @return A new ProjectionFilterExpression representing the AND operation.
     */
    public static ProjectionFilterExpression and(ProjectionFilterExpression... expressions) {
        return ProjectionCompoundFilter.and(expressions);
    }

    /**
     * Creates a ProjectionFilterExpression that represents a logical OR of the provided expressions.
     * @param expressions The expressions to combine with OR.
     * @return A new ProjectionFilterExpression representing the OR operation.
     */
    public static ProjectionFilterExpression or(ProjectionFilterExpression... expressions) {
        return ProjectionCompoundFilter.or(expressions);
    }

    /**
     * Creates a ProjectionFilterExpression for equality comparison.
     * @param path The field path to apply the filter on.
     * @param value The value to compare against.
     * @return A new ProjectionFilterExpression representing the equality comparison.
     */
    public static ProjectionFilterExpression equal(String path, Object value) {
        return of(path, ProjectionFilterOperator.EQUAL, value);
    }

    /**
     * Creates a ProjectionFilterExpression for inequality comparison.
     * @param path The field path to apply the filter on.
     * @param value The value to compare against.
     * @return A new ProjectionFilterExpression representing the inequality comparison.
     */
    public static ProjectionFilterExpression notEqual(String path, Object value) {
        return of(path, ProjectionFilterOperator.NOT_EQUAL, value);
    }

    /**
     * Creates a ProjectionFilterExpression for greater than comparison.
     * @param path The field path to apply the filter on.
     * @param value The value to compare against.
     * @return A new ProjectionFilterExpression representing the greater than comparison.
     */
    public static ProjectionFilterExpression greaterThan(String path, Object value) {
        return of(path, ProjectionFilterOperator.GREATER_THAN, value);
    }

    /**
     * Creates a ProjectionFilterExpression for greater than or equal comparison.
     * @param path The field path to apply the filter on.
     * @param value The value to compare against.
     * @return A new ProjectionFilterExpression representing the greater than or equal comparison.
     */
    public static ProjectionFilterExpression greaterThanOrEqual(String path, Object value) {
        return of(path, ProjectionFilterOperator.GREATER_THAN_OR_EQUAL, value);
    }

    /**
     * Creates a ProjectionFilterExpression for less than comparison.
     * @param path The field path to apply the filter on.
     * @param value The value to compare against.
     * @return A new ProjectionFilterExpression representing the less than comparison.
     */
    public static ProjectionFilterExpression lessThan(String path, Object value) {
        return of(path, ProjectionFilterOperator.LESS_THAN, value);
    }

    /**
     * Creates a ProjectionFilterExpression for less than or equal comparison.
     * @param path The field path to apply the filter on.
     * @param value The value to compare against.
     * @return A new ProjectionFilterExpression representing the less than or equal comparison.
     */
    public static ProjectionFilterExpression lessThanOrEqual(String path, Object value) {
        return of(path, ProjectionFilterOperator.LESS_THAN_OR_EQUAL, value);
    }

    /**
     * Creates a ProjectionFilterExpression for LIKE pattern matching.
     * @param path The field path to apply the filter on.
     * @param pattern The pattern to match against.
     * @return A new ProjectionFilterExpression representing the LIKE comparison.
     */
    public static ProjectionFilterExpression like(String path, String pattern) {
        return of(path, ProjectionFilterOperator.LIKE, pattern);
    }

    /**
     * Creates a ProjectionFilterExpression for IN comparison.
     * @param path The field path to apply the filter on.
     * @param values The values to check inclusion against.
     * @return A new ProjectionFilterExpression representing the IN comparison.
     */
    public static ProjectionFilterExpression in(String path, Object... values) {
        return in(path, List.of(values));
    }

    /**
     * Creates a ProjectionFilterExpression for IN comparison.
     * @param path The field path to apply the filter on.
     * @param value The collection of values to check inclusion against.
     * @return A new ProjectionFilterExpression representing the IN comparison.
     */
    public static ProjectionFilterExpression in(String path, Collection<?> value) {
        return of(path, ProjectionFilterOperator.IN, value);
    }

    /**
     * Creates a ProjectionFilterExpression for NOT IN comparison.
     * @param path The field path to apply the filter on.
     * @param values The collection of values to check exclusion against.
     * @return A new ProjectionFilterExpression representing the NOT IN comparison.
     */
    public static ProjectionFilterExpression notIn(String path, Object... values) {
        return notIn(path, List.of(values));
    }

    /**
     * Creates a ProjectionFilterExpression for NOT IN comparison.
     * @param path The field path to apply the filter on.
     * @param value The collection of values to check exclusion against.
     * @return A new ProjectionFilterExpression representing the NOT IN comparison.
     */
    public static ProjectionFilterExpression notIn(String path, Collection<?> value) {
        return of(path, ProjectionFilterOperator.NOT_IN, value);
    }

    /**
     * Creates a ProjectionFilterExpression for BETWEEN comparison.
     * @param path The field path to apply the filter on.
     * @param startValue The start value of the range.
     * @param endValue The end value of the range.
     * @param <T> The type of the comparable values.
     * @return A new ProjectionFilterExpression representing the BETWEEN comparison.
     */
    public static <T> ProjectionFilterExpression between(String path, Comparable<T> startValue, Comparable<T> endValue) {
        return of(path, ProjectionFilterOperator.BETWEEN, BetweenValues.of(startValue, endValue));
    }
}
