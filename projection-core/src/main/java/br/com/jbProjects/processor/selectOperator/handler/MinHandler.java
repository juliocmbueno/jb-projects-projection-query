package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating MIN expressions in projection queries</p>
 */
public class MinHandler implements ProjectionSelectOperatorHandler {

    /**
     * <p>Default constructor for MinHandler.</p>
     */
    public MinHandler() {}

    /**
     * Indicates whether the projection operation involves aggregation.
     *
     * @return true if the operation is an aggregation function, false otherwise
     */
    @Override
    public boolean aggregate() {
        return true;
    }

    /**
     * <p>Generates a MIN expression for the specified field.</p>
     *
     * @param pathResolver PathResolver to resolve the field path
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply MIN on
     * @return Expression representing MIN(fieldName)
     */
    @Override
    public Expression<?> apply(PathResolver pathResolver, CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.min(pathResolver.resolve(root, fieldName));
    }
}
