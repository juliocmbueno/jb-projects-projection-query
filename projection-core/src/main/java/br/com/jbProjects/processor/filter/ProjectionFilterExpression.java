package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 19/01/2026.
 *
 * <p>The <code>ProjectionFilterExpression</code> interface represents a <b>logical filter expression</b> that can be converted into a JPA {@link Predicate} within a projection-based query.</p>
 * <p>It is the root abstraction for all filtering logic in the ProjectionQuery engine, supporting both <b>simple filters</b> and <b>compound (grouped) filters</b>.</p>
 *
 */
public interface ProjectionFilterExpression {

    /**
     * <p>Converts this filter expression into a JPA {@link Predicate} using the provided criteria context.</p>
     *
     * @param cb CriteriaBuilder instance for building predicates
     * @param query The current CriteriaQuery being constructed
     * @param root The root entity of the query
     * @param pathResolver PathResolver for resolving entity paths
     * @param <FROM> The type of the root entity
     * @return A JPA Predicate representing this filter expression
     */
    <FROM> Predicate toPredicate(
            CriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<FROM> root,
            PathResolver pathResolver
    );

}
