package br.com.jbProjects.metadata.resolver;

import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.metadata.model.FieldMetadata;
import br.com.jbProjects.metadata.model.JoinMetadata;
import br.com.jbProjects.metadata.model.ProjectionStructure;
import br.com.jbProjects.util.ProjectionUtils;

import java.util.List;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public class ProjectionStructureResolver {

    /**
     * Private constructor to prevent instantiation, as this class is intended
     */
    private ProjectionStructureResolver() {}

    public static ProjectionStructure resolve(Class<?> projectionClass) {
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(projectionClass);

        ProjectionAliasResolver aliasResolver = ProjectionAliasResolver.of(declaredJoins);
        List<JoinMetadata> joins = resolveJoins(declaredJoins, aliasResolver);
        List<FieldMetadata> fields = resolveFields(projectionClass, aliasResolver);

        return new ProjectionStructure(joins, fields);
    }

    private static List<JoinMetadata> resolveJoins(List<ProjectionJoin> declaredJoins, ProjectionAliasResolver aliasResolver) {
        return declaredJoins
                .stream()
                .map(join -> new JoinMetadata(
                        aliasResolver.resolve(join.path()),
                        join.type()
                ))
                .toList();
    }

    private static List<FieldMetadata> resolveFields(Class<?> projectionClass, ProjectionAliasResolver aliasResolver) {
        return ProjectionUtils.getProjectionFieldsAnnotations(projectionClass)
                .stream()
                .map(field -> {
                    ProjectionField projectionField = field.getAnnotation(ProjectionField.class);

                    return new FieldMetadata(
                            field.getName(),
                            aliasResolver.resolve(ProjectionUtils.getFieldColumnName(field)),
                            projectionField.selectHandler()
                    );
                })
                .toList();
    }
}
