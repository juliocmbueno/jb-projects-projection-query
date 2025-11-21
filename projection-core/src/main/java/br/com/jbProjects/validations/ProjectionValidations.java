package br.com.jbProjects.validations;

import br.com.jbProjects.annotations.Projection;
import jakarta.persistence.Entity;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public class ProjectionValidations {

    private ProjectionValidations() {}

    public static void validateProjectionClass(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Projection class cannot be null");
        }

        if (!clazz.isAnnotationPresent(Projection.class)) {
            throw new IllegalArgumentException("Projection class needs to have @Projection");
        }

        Projection projection = clazz.getAnnotation(Projection.class);
        Class<?> ofClass = projection.of();
        if (!ofClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Projection 'of' attribute must be an entity class");
        }
    }
}
