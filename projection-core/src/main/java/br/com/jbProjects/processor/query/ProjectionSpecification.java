package br.com.jbProjects.processor.query;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Interface for defining specifications that can be converted into JPA Criteria API Predicates for projection queries.</p>
 * @param <FROM> The entity type from which the projection is made
 */
public interface ProjectionSpecification<FROM> {

    /**
     * <p>Converts the specification into a JPA Criteria API Predicate.</p>
     *
     * @param criteriaBuilder CriteriaBuilder used to create the Predicate
     * @param query The CriteriaQuery being constructed
     * @param root The root type in the "from" clause
     * @return A Predicate representing the specification
     */
    Predicate toPredicate(
            CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Tuple> query,
            Root<FROM> root
    );

}
