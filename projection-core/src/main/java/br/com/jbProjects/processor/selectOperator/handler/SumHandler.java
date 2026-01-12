package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating SUM expressions in projection queries</p>
 */
public class SumHandler implements ProjectionSelectOperatorHandler {

    /**
     * <p>Default constructor for SumHandler.</p>
     */
    public SumHandler() {}

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
     * <p>Generates a SUM expression for the specified field.</p>
     *
     * @param pathResolver PathResolver to resolve the field path
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply the SUM operation on
     * @return Expression representing the SUM of the specified field
     */
    @Override
    public Expression<?> apply(PathResolver pathResolver, CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.sum(pathResolver.resolve(root, fieldName));
    }
}
