package br.com.jbProjects.processor.operatorHandler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;


public interface ProjectionOperatorHandler {
    boolean supports(ProjectionField annotation);
    Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName);
}
