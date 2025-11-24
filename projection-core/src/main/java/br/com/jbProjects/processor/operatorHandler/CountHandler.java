package br.com.jbProjects.processor.operatorHandler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class CountHandler implements ProjectionOperatorHandler {

    @Override
    public boolean supports(ProjectionField annotation) {
        return annotation.count();
    }

    @Override
    public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
        return cb.count(root.get(fieldName));
    }
}
