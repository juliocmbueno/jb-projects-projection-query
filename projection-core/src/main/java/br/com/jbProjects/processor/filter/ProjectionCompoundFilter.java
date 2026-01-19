package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Created by julio.bueno on 19/01/2026.
 * <p>A record representing a compound filter that combines multiple filter expressions using a logical operator.</p>
 *
 * @param operator The {@link CompoundOperator} to combine the filters (e.g., AND, OR).
 * @param filters  The list of filter expressions to be combined.
 */
public record ProjectionCompoundFilter(
        CompoundOperator operator,
        List<ProjectionFilterExpression> filters
) implements ProjectionFilterExpression{

    /**
     * Constructs a ProjectionCompoundFilter.
     *
     * @param operator The {@link CompoundOperator} to combine the filters.
     * @param filters  The list of filter expressions to be combined.
     * @throws IllegalArgumentException if operator is null or filters is null/empty.
     */
    public ProjectionCompoundFilter {
        if (operator == null) {
            throw new IllegalArgumentException("CompoundOperator must not be null");
        }

        if (filters == null || filters.isEmpty()) {
            throw new IllegalArgumentException(
                    "ProjectionCompoundFilter requires at least one filter expression"
            );
        }
    }

    /**
     * Creates a ProjectionCompoundFilter instance.
     * @param operator The {@link CompoundOperator} to combine the filters.
     * @param filters The filter expressions to be combined.
     * @return A new ProjectionCompoundFilter instance.
     */
    public static ProjectionCompoundFilter of(
            CompoundOperator operator,
            ProjectionFilterExpression... filters
    ) {
        return new ProjectionCompoundFilter(operator, List.of(filters));
    }

    @Override
    public <FROM> Predicate toPredicate(CriteriaBuilder cb, CriteriaQuery<?> query, Root<FROM> root, PathResolver pathResolver) {
        List<Predicate> predicates = filters.stream()
                .map(e -> e.toPredicate(cb, query, root, pathResolver))
                .toList();

        return operator.toPredicate(cb, predicates);
    }

}
