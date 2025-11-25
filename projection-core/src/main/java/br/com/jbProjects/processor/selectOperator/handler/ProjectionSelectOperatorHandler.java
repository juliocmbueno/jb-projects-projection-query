package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;


public interface ProjectionSelectOperatorHandler {
    boolean supports(ProjectionField annotation);
    Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName);
}
