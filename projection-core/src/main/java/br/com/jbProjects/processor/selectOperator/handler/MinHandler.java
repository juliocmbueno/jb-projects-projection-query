package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.annotations.ProjectionField;
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
     * <p>Checks if the MIN operation is supported based on the provided annotation.</p>
     *
     * @param annotation ProjectionField annotation containing operation details
     * @return true if MIN operation is specified, false otherwise
     */
    @Override
    public boolean supports(ProjectionField annotation) {
        return annotation.min();
    }

    /**
     * <p>Generates a MIN expression for the specified field.</p>
     *
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply MIN on
     * @return Expression representing MIN(fieldName)
     */
    @Override
    public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.min(root.get(fieldName));
    }
}
