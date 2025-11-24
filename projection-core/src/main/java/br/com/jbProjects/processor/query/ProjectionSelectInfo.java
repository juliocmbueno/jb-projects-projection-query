package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.processor.operatorHandler.ProjectionOperatorProvider;
import br.com.jbProjects.util.ProjectionUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by julio.bueno on 24/11/2025.
 */
@Getter
public class ProjectionSelectInfo {

    private final Selection<?>[] selections;
    private final Path<?>[] groupByFields;

    public ProjectionSelectInfo(ProjectionQuery<?, ?> projectionQuery, CriteriaBuilder criteriaBuilder, Root<?> from){
        List<Field> fields = ProjectionUtils.getProjectionFieldsAnnotations(projectionQuery.toClass());
        selections = processSelections(fields, criteriaBuilder, from);
        groupByFields = processGroupByFields(fields, criteriaBuilder, from);
    }

    private Selection<?>[] processSelections(List<Field> fields, CriteriaBuilder criteriaBuilder, Root<?> from) {
        return fields
                .stream()
                .map(field -> {
                    ProjectionField projectionField = field.getAnnotation(ProjectionField.class);
                    String fieldColumnName = ProjectionUtils.getFieldColumnName(field);

                    return ProjectionOperatorProvider
                            .operators()
                            .stream()
                            .filter(operator -> operator.supports(projectionField))
                            .findFirst()
                            .map(operator -> operator.apply(criteriaBuilder, from, fieldColumnName))
                            .orElse(from.get(fieldColumnName))
                            .alias(field.getName());
                })
                .toArray(Selection<?>[]::new);
    }

    private Path<?>[] processGroupByFields(List<Field> fields, CriteriaBuilder criteriaBuilder, Root<?> from) {
        boolean hasAnyOperator = fields
                .stream()
                .map(field -> field.getAnnotation(ProjectionField.class))
                .anyMatch(projectionField -> ProjectionOperatorProvider
                        .operators()
                        .stream()
                        .anyMatch(operator -> operator.supports(projectionField))
                );

        if (!hasAnyOperator) {
            return new Path<?>[0];
        }

        return fields
                .stream()
                .filter(field -> {
                    boolean isOperator = ProjectionOperatorProvider
                            .operators()
                            .stream()
                            .anyMatch(operator -> operator.supports(field.getAnnotation(ProjectionField.class)));

                    // Normal fields (without operator)
                    return !isOperator;
                })
                .map(field -> from.get(ProjectionUtils.getFieldColumnName(field)))
                .toArray(Path<?>[]::new);
    }
}
