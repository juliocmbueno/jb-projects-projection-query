package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.processor.joinResolver.PathResolver;
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
     * Indicates whether the projection operation involves aggregation.
     *
     * @return true if the operation is an aggregation function, false otherwise
     */
    boolean aggregate();

    /**
     * Applies the projection operation defined by the handler.
     *
     * @param pathResolver PathResolver to resolve the field path
     * @param cb CriteriaBuilder used to create the Expression
     * @param root Root entity from which the field is selected
     * @param fieldName Name of the field to apply the projection on
     * @return Expression representing the projection operation
     */
    Expression<?> apply(PathResolver pathResolver, CriteriaBuilder cb, Root<?> root, String fieldName);
}
