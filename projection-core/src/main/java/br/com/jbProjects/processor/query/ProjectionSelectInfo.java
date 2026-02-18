package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.metadata.cache.ProjectionMetadataCache;
import br.com.jbProjects.metadata.model.FieldMetadata;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import br.com.jbProjects.processor.joinResolver.PathResolver;
import br.com.jbProjects.processor.selectOperator.ProjectionSelectOperatorProvider;
import br.com.jbProjects.processor.selectOperator.handler.ProjectionSelectOperatorHandler;
import jakarta.persistence.criteria.*;
import lombok.Getter;

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
        ProjectionMetadata metaData = ProjectionMetadataCache.get(projectionQuery.toClass());
        pathResolver = projectionQuery.getPathResolver();
        selections = processSelections(metaData.fields(), criteriaBuilder, from);
        groupByFields = processGroupByFields(metaData.fields(), from);
    }

    private Selection<?>[] processSelections(List<FieldMetadata> fields, CriteriaBuilder criteriaBuilder, Root<?> from) {
        return fields.stream()
                .map(field -> {
                    ProjectionSelectOperatorHandler handler = ProjectionSelectOperatorProvider
                            .getInstance()
                            .get(field.selectHandler());

                    Expression<?> expression = handler.apply(pathResolver, criteriaBuilder, from, field.value());

                    return expression.alias(field.projectionFieldName());
                })
                .toArray(Selection<?>[]::new);
    }

    private Path<?>[] processGroupByFields(List<FieldMetadata> fields, Root<?> from) {
        boolean hasAggregate = fields
                .stream()
                .map(FieldMetadata::selectHandler)
                .map(handlerClass ->
                        ProjectionSelectOperatorProvider
                                .getInstance()
                                .get(handlerClass))
                .anyMatch(ProjectionSelectOperatorHandler::aggregate);

        if (!hasAggregate) {
            return new Path<?>[0];
        }

        return fields.stream()
                .filter(field -> {
                    ProjectionSelectOperatorHandler handler =
                            ProjectionSelectOperatorProvider
                                    .getInstance()
                                    .get(field.selectHandler());

                    return !handler.aggregate();
                })
                .map(field ->
                        from.get(field.value()))
                .toArray(Path<?>[]::new);
    }
}
