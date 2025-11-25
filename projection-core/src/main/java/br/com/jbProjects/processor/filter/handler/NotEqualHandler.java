package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public class NotEqualHandler implements ProjectionFilterOperatorHandler {
    @Override
    public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
        return cb.notEqual(path, value);
    }
}
