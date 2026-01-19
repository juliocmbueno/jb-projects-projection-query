package br.com.jbProjects.processor.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

/**
 * Created by julio.bueno on 19/01/2026.
 *
 * <p>The <code>CompoundOperator</code> enum defines how multiple predicates are logically grouped when building projection filters.</p>
 * <p>It is used by {@link ProjectionCompoundFilter} to combine several filter expressions into a single JPA Predicate.</p>
 *
 * <p>This abstraction allows the {@link br.com.jbProjects.processor.query.ProjectionQuery} engine to support grouped conditions such as:</p>
 *
 * <pre><code>(state = 1) AND (name LIKE 'sa%' OR name LIKE 'pa%')</code></pre>
 *
 * <p><b>Supported Operators</b></p>
 * <ul>
 *  <li>{@link CompoundOperator#AND}</li>
 *  <li>{@link CompoundOperator#OR}</li>
 * </ul>
 */
public enum CompoundOperator {
    /** Logical AND operator to combine predicates. */
    AND {
        @Override
        public Predicate toPredicate(CriteriaBuilder cb, List<Predicate> predicates) {
            return cb.and(predicates.toArray(new Predicate[0]));
        }
    },

    /** Logical OR operator to combine predicates. */
    OR {
        @Override
        public Predicate toPredicate(CriteriaBuilder cb, List<Predicate> predicates) {
            return cb.or(predicates.toArray(new Predicate[0]));
        }
    };

    /**
     * <p>Combines the given predicates using the logical operation represented by this operator.</p>
     *
     * @param criteriaBuilder JPA CriteriaBuilder instance
     * @param predicates List of predicates to be combined
     * @return A single Predicate representing the logical combination
     */
    public abstract Predicate toPredicate(CriteriaBuilder criteriaBuilder, List<Predicate> predicates);
}
