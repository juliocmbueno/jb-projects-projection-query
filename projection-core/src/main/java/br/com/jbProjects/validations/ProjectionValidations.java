package br.com.jbProjects.validations;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionJoin;
import jakarta.persistence.Entity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Utility class for validating projection classes and their configurations.</p>
 */
public class ProjectionValidations {

    private ProjectionValidations() {}

    /**
     * Validates a projection class to ensure it meets the necessary criteria.
     *
     * @param clazz the projection class to validate
     * @throws IllegalArgumentException if the class is null, lacks the @Projection annotation,
     *                                  or if the 'of' attribute is not an entity class, or if there are alias issues
     */
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

    /**
     * Validates that aliases in ProjectionJoin annotations are unique and do not conflict with paths.
     *
     * @param clazz the projection class to validate
     * @throws IllegalArgumentException if duplicate aliases are found or if an alias matches a path
     */
    private static void validateAliases(Class<?> clazz) {
        Projection projection = clazz.getAnnotation(Projection.class);

        if (projection.joins().length == 0) {
            return;
        }

        validateAliases(Arrays.stream(projection.joins()).toList());
    }

    /**
     * Validates that aliases in a list of ProjectionJoin annotations are unique and do not conflict with paths.
     *
     * @param joins the list of ProjectionJoin annotations to validate
     * @throws IllegalArgumentException if duplicate aliases are found or if an alias matches a path
     */
    public static void validateAliases(List<ProjectionJoin> joins) {
        Set<String> allPaths = joins.stream().map(ProjectionJoin::path).collect(Collectors.toSet());
        Map<String, String> aliasToPath = new HashMap<>();

        for (ProjectionJoin join : joins) {
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
