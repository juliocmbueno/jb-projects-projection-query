package br.com.jbProjects.mapper;

import br.com.jbProjects.util.ProjectionUtils;
import jakarta.persistence.Tuple;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public class ProjectionMappers {

    private ProjectionMappers(){}

    public static <T> T tupleToClass(Tuple tuple, Class<T> projectionClass){
        try{
            T projectionInstance = projectionClass.getDeclaredConstructor().newInstance();
            List<Field> fields = ProjectionUtils.getProjectionFieldsAnnotations(projectionClass);

            for (var field : fields) {
                field.setAccessible(true);
                Object value = tuple.get(field.getName());
                field.set(projectionInstance, value);
            }

            return projectionInstance;
        } catch (Exception e) {
            throw new RuntimeException("Error creating projection instance", e);
        }

    }

    public static <T> T tupleToRecord(Tuple tuple, Class<T> projectionClass){
        try{
            RecordComponent[] components = projectionClass.getRecordComponents();
            Constructor<?> constructor = projectionClass.getDeclaredConstructor(
                    Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new)
            );
            Object[] args = Arrays.stream(components)
                    .map(rc -> {
                        try{
                            return tuple.get(rc.getName());
                        }catch (IllegalArgumentException e){
                            return null;
                        }
                    })
                    .toArray();
            return (T) constructor.newInstance(args);
        }catch (Exception e){
            throw new RuntimeException("Error creating projection record instance", e);
        }
    }
}
