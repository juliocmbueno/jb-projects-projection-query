package br.com.jbProjects.processor;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.mapper.ProjectionMappers;
import br.com.jbProjects.util.ProjectionUtils;
import br.com.jbProjects.validations.ProjectionValidations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public class ProjectionProcessor {

    private final EntityManager entityManager;

    public ProjectionProcessor(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public <T> List<T> execute(Class<T> projectionClass){
        ProjectionValidations.validateProjectionClass(projectionClass);

        Projection projection = projectionClass.getAnnotation(Projection.class);
        Class<?> entityClass = projection.of();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<?> from = criteriaQuery.from(entityClass);

        List<Field> fields = ProjectionUtils.getProjectionFieldsAnnotations(projectionClass);
        criteriaQuery.multiselect(
                fields.stream()
                        .map(field -> {
                            String fieldColumnName = ProjectionUtils.getFieldColumnName(field);
                            return from.get(fieldColumnName).alias(field.getName());
                        })
                        .toArray(Selection[]::new)
        );

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Tuple> tuples = typedQuery.getResultList();



        return tuples.stream().map(tuple -> {
            if(projectionClass.isRecord()){
                return ProjectionMappers.tupleToRecord(tuple, projectionClass);

            } else {
                return ProjectionMappers.tupleToClass(tuple, projectionClass);

            }
        }).collect(Collectors.toList());
    }
}
