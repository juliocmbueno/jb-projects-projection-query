package br.com.jbProjects.metadata.factory;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import br.com.jbProjects.metadata.model.ProjectionStructure;
import br.com.jbProjects.metadata.resolver.ProjectionStructureResolver;
import br.com.jbProjects.validations.ProjectionValidations;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public class ProjectionMetadataFactory {

    public static ProjectionMetadata of(Class<?> projectionClass) {
        ProjectionValidations.validateProjectionClass(projectionClass);

        Projection projection = projectionClass.getAnnotation(Projection.class);

        ProjectionStructure structure = ProjectionStructureResolver.resolve(projectionClass);

        return new ProjectionMetadata(
                projectionClass,
                projection.of(),
                structure.joins(),
                structure.fields()
        );
    }
}
