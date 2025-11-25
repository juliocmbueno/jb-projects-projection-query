package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class MinHandler implements ProjectionSelectOperatorHandler {

    @Override
    public boolean supports(ProjectionField annotation) {
        return annotation.min();
    }

    @Override
    public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.min(root.get(fieldName));
    }
}
