package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 12/01/2026.
 * <p>Handler responsible for generating AVG expressions in projection queries</p>
 */
public class AvgHandler implements ProjectionSelectOperatorHandler {

    /**
     * <p>Default constructor for AvgHandler.</p>
     */
    public AvgHandler() {}

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
     * <p>Generates an AVG expression for the specified field.</p>
     *
     * @param pathResolver PathResolver to resolve the field path
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply AVG on
     * @return Expression representing AVG(fieldName)
     */
    @Override
    public Expression<?> apply(PathResolver pathResolver, CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.avg(pathResolver.resolve(root, fieldName));
    }
}
