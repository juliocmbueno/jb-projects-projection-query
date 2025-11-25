package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public class InHandler implements ProjectionFilterOperatorHandler {
    @Override
    public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
        if (!(value instanceof Iterable<?> iterable)) {
            throw new IllegalArgumentException("Value for IN operator must be an instance of Iterable.");
        }

        CriteriaBuilder.In<Object> in = cb.in(path);
        for (Object v : iterable) {
            in.value(v);
        }

        return in;
    }
}
