package br.com.jbProjects.validations;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionJoin;
import jakarta.persistence.Entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public class ProjectionValidations {

    private ProjectionValidations() {}

    public static void validateProjectionClass(Class<?> clazz) {
        projectionClassCanNotBeNull(clazz);
        needsToHaveProjectionAnnotation(clazz);
        ofAttributeMustBeEntity(clazz);
        validateAliases(clazz);
    }

    private static void projectionClassCanNotBeNull(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Projection class cannot be null");
        }
    }

    private static void needsToHaveProjectionAnnotation(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Projection.class)) {
            throw new IllegalArgumentException("Projection class needs to have @Projection");
        }
    }

    private static void ofAttributeMustBeEntity(Class<?> clazz) {
        Projection projection = clazz.getAnnotation(Projection.class);
        Class<?> ofClass = projection.of();
        if (!ofClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Projection 'of' attribute must be an entity class");
        }
    }

    public static void validateAliases(Class<?> clazz) {
        Projection projection = clazz.getAnnotation(Projection.class);

        if (projection.joins().length == 0) {
            return;
        }

        Set<String> allPaths = Arrays.stream(projection.joins()).map(ProjectionJoin::path).collect(Collectors.toSet());
        Map<String, String> aliasToPath = new HashMap<>();

        for (ProjectionJoin join : projection.joins()) {
            String alias = join.alias();
            String path = join.path();

            if (!alias.isBlank()) {
                if(allPaths.contains(alias)) {
                    throw new IllegalArgumentException("Alias '" + alias + "' cannot be equal a path");
                }

                if (aliasToPath.containsKey(alias)) {
                    throw new IllegalArgumentException( "Duplicate alias detected: '" + alias + "'");
                }

                aliasToPath.put(alias, path);
            }
        }
    }
}
