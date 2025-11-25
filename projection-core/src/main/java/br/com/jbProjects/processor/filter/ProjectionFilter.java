package br.com.jbProjects.processor.filter;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>A record representing a filter to be applied on a specific field path.</p>
 * <p>It's possible to use nested paths to access fields in related entities in the path.</p>
 * <p> ex: "name" or "address.city.name" </p>
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
