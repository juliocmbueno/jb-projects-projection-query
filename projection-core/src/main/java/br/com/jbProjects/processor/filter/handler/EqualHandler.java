package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating Predicate for EQUAL operations in projection queries</p>
 */
public class EqualHandler implements ProjectionFilterOperatorHandler {

    /**
     * <p>Handler responsible for generating Predicate for EQUAL operations in projection queries.</p>
     *
     * @param cb CriteriaBuilder used to create the Predicate
     * @param path Target property path
     * @param value Value for comparison
     * @return Predicate equivalent to "path = value"
     */
    @Override
    public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
        return cb.equal(path, value);
    }

}
