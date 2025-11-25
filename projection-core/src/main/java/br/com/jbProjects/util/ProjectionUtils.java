package br.com.jbProjects.util;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Utilities for handling projection-related annotations.</p>
 */
public class ProjectionUtils {

    private ProjectionUtils() {}

    /**
     * Retrieves all fields from the specified class that are annotated with @ProjectionField.
     *
     * @param clazz the class to inspect
     * @return a list of fields annotated with @ProjectionField
     */
    public static List<Field> getProjectionFieldsAnnotations(Class<?> clazz) {
        return FieldUtils.getFieldsListWithAnnotation(clazz, ProjectionField.class);
    }

    /**
     * Gets the column name for a given field based on the @ProjectionField annotation.
     * If the annotation specifies a value, that value is returned; otherwise, the field's name is returned.
     *
     * @param field the field to inspect
     * @return the column name associated with the field
     */
    public static String getFieldColumnName(Field field) {
        ProjectionField projectionField = field.getAnnotation(ProjectionField.class);
        if (!projectionField.value().isEmpty()) {
            return projectionField.value();
        }
        return field.getName();
    }

    /**
     * Retrieves the list of ProjectionJoin annotations declared on the specified class.
     *
     * @param clazz the class to inspect
     * @return a list of ProjectionJoin annotations, or an empty list if none are present
     */
    public static List<ProjectionJoin> getDeclaredJoins(Class<?> clazz) {
        Projection annotation = clazz.getAnnotation(Projection.class);
        if(annotation == null){
            return List.of();
        }

        return Arrays
                .stream(clazz.getAnnotation(Projection.class).joins())
                .toList();
    }
}
