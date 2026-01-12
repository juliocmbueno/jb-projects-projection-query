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
 * <p>Class responsible for mapping JPA Tuples to projection objects (classes or records).</p>
 */
public class ProjectionMappers {

    private ProjectionMappers(){}

    /**
     * Maps a JPA Tuples to a projection object.
     *
     * @param tuple                 JPA Tuples to be mapped.
     * @param projectionObject      Class of the projection object (can be a class or a record).
     * @param <T>                   Type of the projection object.
     * @return                      An instance of the projection object populated with data from the Tuples.
     */
    public static <T> T tupleToObject(Tuple tuple, Class<T> projectionObject){
        if(projectionObject.isRecord()){
            return tupleToRecord(tuple, projectionObject);
        }

        return tupleToClass(tuple, projectionObject);
    }

    /**
     * Maps a JPA Tuples to a projection class instance.
     *
     * @param tuple                 JPA Tuples to be mapped.
     * @param projectionClass       Class of the projection object.
     * @param <T>                   Type of the projection object.
     * @return                      An instance of the projection class populated with data from the Tuples.
     */
    private static <T> T tupleToClass(Tuple tuple, Class<T> projectionClass){
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

    /**
     * Maps a JPA Tuples to a projection record instance.
     *
     * @param tuple                 JPA Tuples to be mapped.
     * @param projectionClass       Class of the projection record.
     * @param <T>                   Type of the projection record.
     * @return                      An instance of the projection record populated with data from the Tuples.
     */
    private static <T> T tupleToRecord(Tuple tuple, Class<T> projectionClass){
        try{
            RecordComponent[] components = projectionClass.getRecordComponents();
            Constructor<T> constructor = projectionClass.getDeclaredConstructor(
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

            try{
                return constructor.newInstance(args);
            }catch (IllegalArgumentException e){
                List<String> expectedTypes = Arrays.stream(projectionClass.getRecordComponents())
                        .map(rc -> rc.getType().getSimpleName())
                        .toList();

                List<String> resultTypes = Arrays.stream(args)
                        .map(obj -> obj == null ? "null" : obj.getClass().getSimpleName())
                        .toList();

                throw new RuntimeException(
                        """
                        Error mapping query result to projection record: %s
        
                        Expected constructor types:
                          %s
        
                        Query result types:
                          %s
                        """.formatted(
                                projectionClass.getSimpleName(),
                                expectedTypes,
                                resultTypes
                        ),
                        e
                );
            }
        }
        catch (Exception e){
            throw new RuntimeException("Error creating projection record instance", e);
        }
    }
}
