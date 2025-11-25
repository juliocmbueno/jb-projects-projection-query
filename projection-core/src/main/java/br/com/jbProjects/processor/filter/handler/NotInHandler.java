package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating Predicate for NOT IN operations in projection queries</p>
 */
public class NotInHandler implements ProjectionFilterOperatorHandler {

    private final InHandler inHandler = new InHandler();

    /**
     * <p>Default constructor for NotInHandler.</p>
     */
    public NotInHandler() {}

    /**
     * <p>Handler responsible for generating Predicate for NOT IN operations in projection queries.</p>
     *
     * @param cb CriteriaBuilder used to create the Predicate
     * @param path Target property path
     * @param value Value for comparison
     * @return Predicate equivalent to "path NOT IN value"
     */
    @Override
    public Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value) {
        return cb.not(inHandler.toPredicate(cb, path, value));
    }
}
