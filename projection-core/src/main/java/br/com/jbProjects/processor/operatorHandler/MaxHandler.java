package br.com.jbProjects.processor.operatorHandler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class MaxHandler implements ProjectionOperatorHandler {

    @Override
    public boolean supports(ProjectionField annotation) {
        return annotation.max();
    }

    @Override
    public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.max(root.get(fieldName));
    }
}
