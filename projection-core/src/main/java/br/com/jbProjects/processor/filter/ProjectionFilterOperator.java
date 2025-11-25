package br.com.jbProjects.processor.filter;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Enumeration of filter operators that can be used in ProjectionFilter.</p>
 */
public enum ProjectionFilterOperator {
    /**
     * Equal operator.
     * <p>equivalent to "path = value"</p>
     */
    EQUAL,

    /**
     * Not equal operator.
     * <p>equivalent to "path <> value"</p>
     */
    NOT_EQUAL,

    /**
     * Greater than operator.
     * <p>equivalent to "path > value"</p>
     */
    GREATER_THAN,

    /**
     * Less than operator.
     * <p>equivalent to "path < value"</p>
     */
    LESS_THAN,

    /**
     * Greater than or equal operator.
     * <p>equivalent to "path >= value"</p>
     */
    GREATER_THAN_OR_EQUAL,

    /**
     * Less than or equal operator.
     * <p>equivalent to "path <= value"</p>
     */
    LESS_THAN_OR_EQUAL,

    /**
     * Like operator for pattern matching.
     * <p>equivalent to "path LIKE value"</p>
     */
    LIKE,

    /**
     * In operator for checking inclusion in a set.
     * <p>equivalent to "path IN (value1, value2, ...)"</p>
     */
    IN,

    /**
     * Not In operator for checking exclusion from a set.
     * <p>equivalent to "path NOT IN (value1, value2, ...)"</p>
     */
    NOT_IN,

    /**
     * Between operator for range checking.
     * <p>equivalent to "path BETWEEN value1 AND value2"</p>
     */
    BETWEEN
}
