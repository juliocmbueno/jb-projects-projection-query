package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.processor.joinResolver.PathResolver;
import br.com.jbProjects.processor.selectOperator.ProjectionSelectOperatorProvider;
import br.com.jbProjects.util.ProjectionUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 *
 * <p>Encapsulates the selections and group-by paths for a projection query.</p>
 * <p>
 * {@code ProjectionSelectInfo} analyzes the fields of a projection class annotated with
 * {@link ProjectionField} and constructs:
 * <ul>
 *     <li>{@link #selections} - the fields to be selected in the query, with aliases and operators applied</li>
 *     <li>{@link #groupByFields} - the fields that need to be included in GROUP BY clauses</li>
 * </ul>
 * <p>
 * This class uses the {@link PathResolver} from the {@link ProjectionQuery} to resolve field paths,
 * and it automatically applies any supported operators from {@link ProjectionSelectOperatorProvider}.
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * ProjectionSelectInfo selectInfo = new ProjectionSelectInfo(projectionQuery, criteriaBuilder, root);
 * criteriaQuery.multiselect(selectInfo.getSelections());
 * criteriaQuery.groupBy(selectInfo.getGroupByFields());
 * }</pre>
 */
@Getter
public class ProjectionSelectInfo {

    private final Selection<?>[] selections;
    private final Path<?>[] groupByFields;
    private final PathResolver pathResolver;

    /**
     * Constructs a {@code ProjectionSelectInfo} by analyzing the fields of the provided projection query.
     *
     * @param projectionQuery  The projection query containing the projection class and path resolver.
     * @param criteriaBuilder  The CriteriaBuilder used to create selections.
     * @param from             The root entity from which paths are resolved.
     */
    public ProjectionSelectInfo(
            ProjectionQuery<?, ?> projectionQuery,
            CriteriaBuilder criteriaBuilder,
            Root<?> from
    ){
        List<Field> fields = ProjectionUtils.getProjectionFieldsAnnotations(projectionQuery.toClass());
        pathResolver = projectionQuery.getPathResolver();
        selections = processSelections(fields, criteriaBuilder, from);
        groupByFields = processGroupByFields(fields, from);
    }

    private Selection<?>[] processSelections(List<Field> fields, CriteriaBuilder criteriaBuilder, Root<?> from) {
        return fields
                .stream()
                .map(field -> {
                    ProjectionField projectionField = field.getAnnotation(ProjectionField.class);
                    String fieldColumnName = ProjectionUtils.getFieldColumnName(field);

                    Path path = pathResolver.resolve(from, fieldColumnName);

                    return ProjectionSelectOperatorProvider
                            .getInstance()
                            .operators()
                            .stream()
                            .filter(operator -> operator.supports(projectionField))
                            .findFirst()
                            .map(operator -> operator.apply(criteriaBuilder, from, fieldColumnName))
                            .orElse(path)
                            .alias(field.getName());
                })
                .toArray(Selection<?>[]::new);
    }

    private Path<?>[] processGroupByFields(List<Field> fields, Root<?> from) {
        boolean hasAnyOperator = fields
                .stream()
                .map(field -> field.getAnnotation(ProjectionField.class))
                .anyMatch(projectionField -> ProjectionSelectOperatorProvider
                        .getInstance()
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
                    boolean isOperator = ProjectionSelectOperatorProvider
                            .getInstance()
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
