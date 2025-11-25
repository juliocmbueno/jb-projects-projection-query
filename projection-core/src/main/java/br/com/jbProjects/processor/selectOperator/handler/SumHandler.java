package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Handler responsible for generating SUM expressions in projection queries</p>
 */
public class SumHandler implements ProjectionSelectOperatorHandler {

    /**
     * <p>Checks if the handler supports SUM operation based on the ProjectionField annotation.</p>
     *
     * @param annotation ProjectionField annotation to check
     * @return true if SUM operation is supported, false otherwise
     */
    @Override
    public boolean supports(ProjectionField annotation) {
        return annotation.sum();
    }

    /**
     * <p>Generates a SUM expression for the specified field.</p>
     *
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply the SUM operation on
     * @return Expression representing the SUM of the specified field
     */
    @Override
    public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.sum(root.get(fieldName));
    }
}
