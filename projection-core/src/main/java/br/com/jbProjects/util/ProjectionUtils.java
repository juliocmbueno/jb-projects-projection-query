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
 */
public class ProjectionUtils {

    private ProjectionUtils() {}

    public static List<Field> getProjectionFieldsAnnotations(Class<?> clazz) {
        return FieldUtils.getFieldsListWithAnnotation(clazz, ProjectionField.class);
    }

    public static String getFieldColumnName(Field field) {
        ProjectionField projectionField = field.getAnnotation(ProjectionField.class);
        if (!projectionField.value().isEmpty()) {
            return projectionField.value();
        }
        return field.getName();
    }

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
