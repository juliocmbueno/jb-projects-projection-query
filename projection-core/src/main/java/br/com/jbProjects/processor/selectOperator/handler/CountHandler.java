package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 24/11/2025.
 * <p>Handler responsible for generating COUNT expressions in projection queries</p>
 */
public class CountHandler implements ProjectionSelectOperatorHandler {

    /**
     * <p>Default constructor for CountHandler.</p>
     */
    public CountHandler() {}

    /**
     * <p>Checks if the COUNT operation is supported based on the provided annotation.</p>
     *
     * @param annotation ProjectionField annotation containing operation details
     * @return true if COUNT operation is specified, false otherwise
     */
    @Override
    public boolean supports(ProjectionField annotation) {
        return annotation.count();
    }

    /**
     * <p>Generates a COUNT expression for the specified field.</p>
     *
     * @param cb CriteriaBuilder used to create the expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply COUNT on
     * @return Expression representing COUNT(fieldName)
     */
    @Override
    public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.count(root.get(fieldName));
    }
}
