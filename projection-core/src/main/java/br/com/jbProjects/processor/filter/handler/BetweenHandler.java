package br.com.jbProjects.processor.filter.handler;

import br.com.jbProjects.processor.filter.BetweenValues;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating Predicate for BETWEEN operations in projection queries</p>
 */
public class BetweenHandler implements ProjectionFilterOperatorHandler {

    /**
     * <p>Default constructor for BetweenHandler.</p>
     */
    public BetweenHandler() {}

    /**
     * <p>Handler responsible for generating Predicate for BETWEEN operations in projection queries.</p>
     * <p>Requires that the received value be an instance of BetweenValues {@link BetweenValues} containing the start and end values for the range.</p>
     *
     * @param cb CriteriaBuilder used to create the Predicate
     * @param path Target property path
     * @param value BetweenValues object containing start and end values
     * @return Predicate equivalent to "path BETWEEN start AND end"
     * @throws IllegalArgumentException if the value is not an instance of BetweenValues
     */
    @SuppressWarnings({"unchecked", "PatternVariableCanBeUsed"})
    @Override
    public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
        if(!(value instanceof BetweenValues)){
            throw new IllegalArgumentException(
                    "Between operator requires BetweenValues. Provided: " + value
            );
        }

        var betweenValues = (BetweenValues) value;

        Path<? extends Comparable<Object>> comparablePath =
                (Path<? extends Comparable<Object>>) path;

        return cb.between(
                comparablePath,
                (Comparable<Object>) betweenValues.start(),
                (Comparable<Object>) betweenValues.end()
        );
    }
}
