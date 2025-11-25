package br.com.jbProjects.processor.filter;

/**
 * Created by julio.bueno on 25/11/2025.
 *
 * @param path    The field path to apply the filter on.
 * @param operator The operator to use for filtering (e.g., EQUAL, LESS_THAN).
 * @param value    The value to compare against.
 */
public record ProjectionFilter(
        String path,
        String operator,
        Object value
) {
}
