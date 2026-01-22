package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 12/01/2026.
 * <p>Handler responsible for generating ABS expressions in projection queries</p>
 */
public class AbsHandler implements ProjectionSelectOperatorHandler {

    /**
     * <p>Default constructor for AbsHandler.</p>
     */
    public AbsHandler() {}

    /**
     * Indicates whether the projection operation involves aggregation.
     *
     * @return true if the operation is an aggregation function, false otherwise
     */
    @Override
    public boolean aggregate() {
        return false;
    }

    /**
     * <p>Generates an ABS expression for the specified field.</p>
     *
     * @param pathResolver PathResolver to resolve the field path
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply ABS on
     * @return Expression representing ABS(fieldName)
     */
    @Override
    public Expression<?> apply(PathResolver pathResolver, CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.abs(pathResolver.resolve(root, fieldName));
    }
}
