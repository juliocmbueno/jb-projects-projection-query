package br.com.jbProjects.annotations;

import br.com.jbProjects.processor.selectOperator.handler.DefaultSelectOperatorHandler;
import br.com.jbProjects.processor.selectOperator.handler.ProjectionSelectOperatorHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by julio.bueno on 21/11/2025.
 * Indicates that the annotated field is a projection field with optional aggregation functions.
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectionField {

    /**
     * The path to the field in the entity to be projected.
     * <p>It's possible to use nested paths to access fields in related entities.</p>
     * <p> ex: "name" or "address.city.name" </p>
     * @return String path;
     */
    String value() default "";

    /**
     * Custom select operator handler class to be used for this field.
     * @return An implementation of {@link ProjectionSelectOperatorHandler}. Default is {@link DefaultSelectOperatorHandler}
     */
    Class<? extends ProjectionSelectOperatorHandler> selectHandler() default DefaultSelectOperatorHandler.class;

}
