package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * <p>Handler interface for processing projection select operations.</p>
 *
 * <p>Implementations of this interface should provide logic to handle
 * specific projection operations defined by the ProjectionField annotation.</p>
 */
public interface ProjectionSelectOperatorHandler {
    /**
     * Checks if the handler supports the given ProjectionField annotation.
     *
     * @param annotation the ProjectionField annotation to check
     * @return true if the handler supports the annotation, false otherwise
     */
    boolean supports(ProjectionField annotation);

    /**
     * Applies the projection operation defined by the handler.
     *
     * @param cb CriteriaBuilder used to create the Expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply the projection on
     * @return Expression representing the projection operation
     */
    Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName);
}
