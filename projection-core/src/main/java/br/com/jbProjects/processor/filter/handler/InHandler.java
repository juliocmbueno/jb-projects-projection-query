package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating Predicate for IN operations in projection queries</p>
 */
public class InHandler implements ProjectionFilterOperatorHandler {

    /**
     * Created by julio.bueno on 25/11/2025.
     * <p>Handler responsible for generating Predicate for IN operations in projection queries.</p>
     * <p>Requires that the received value be an Iterable {@link}. Each item will be converted into an equality expression using the CriteriaBuilder.</p>
     *
     * @param cb CriteriaBuilder used to create the Predicate
     * @param path Target property path
     * @param value List of values for comparison
     * @return Predicate equivalent to "path IN (value...)"
     * @throws IllegalArgumentException if the value is not an Iterable
     */
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
