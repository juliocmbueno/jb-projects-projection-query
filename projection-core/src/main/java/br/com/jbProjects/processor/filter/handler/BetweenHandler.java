package br.com.jbProjects.processor.filter.handler;

import br.com.jbProjects.processor.filter.BetweenValues;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public class BetweenHandler implements ProjectionFilterOperatorHandler {
    @Override
    public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
        if(!(value instanceof BetweenValues(Comparable<?> start, Comparable<?> end))){
            throw new IllegalArgumentException(
                    "Between operator requires BetweenValues. Provided: " + value
            );
        }
        return cb.between(
                (Path<Comparable>) path,
                (Comparable) start,
                (Comparable) end
        );
    }
}
