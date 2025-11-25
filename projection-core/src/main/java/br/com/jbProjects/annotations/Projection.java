package br.com.jbProjects.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio.bueno on 21/11/2025.
 * Indicates that the annotated class is a projection for a specific entity.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Projection {
    /**
     * The entity class that this projection maps to.
     * @return Class of the entity
     * */
    Class<?> of();

    /**
     * Specifies join conditions for the projection.
     * @return Array of ProjectionJoin annotations
     * */
    ProjectionJoin[] joins() default {};
}
