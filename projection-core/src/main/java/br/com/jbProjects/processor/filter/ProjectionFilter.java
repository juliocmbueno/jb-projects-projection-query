package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.filter.handler.ProjectionFilterOperatorHandler;
import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.*;

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
) implements ProjectionFilterExpression{

    /**
     * Creates a ProjectionFilter instance.
     * @param path The field path to apply the filter on.
     * @param operator The {@link ProjectionFilterOperator} to use for filtering.
     * @param value  The value to compare against.
     * @return A new ProjectionFilter instance.
     */
    public static ProjectionFilter of(String path, ProjectionFilterOperator operator, Object value) {
        return new ProjectionFilter(path, operator.name(), value);
    }

    /**
     * Creates a ProjectionFilter instance.
     * @param path The field path to apply the filter on.
     * @param operator The operator to use for filtering as a String.
     * @param value  The value to compare against.
     * @return A new ProjectionFilter instance.
     */
    public static ProjectionFilter of(String path, String operator, Object value) {
        return new ProjectionFilter(path, operator, value);
    }

    @Override
    public <FROM> Predicate toPredicate(CriteriaBuilder cb, CriteriaQuery<?> query, Root<FROM> root, PathResolver pathResolver) {
        Path<?> resolvedPath = pathResolver.resolve(root, path);
        ProjectionFilterOperatorHandler handler = ProjectionFilterOperatorProvider.getInstance().get(operator);

        return handler.toPredicate(cb, resolvedPath, value);
    }

    @Override
    public String toLogString() {
        return String.format("%s %s", path, operator);
    }
}
