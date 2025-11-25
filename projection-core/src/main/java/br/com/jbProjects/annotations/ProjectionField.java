package br.com.jbProjects.annotations;

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
     * Aggregation functions to be applied to the field.
     *
     * @return boolean indicating if sum aggregation is applied
     */
    boolean sum() default false;

    /**
     * The maximum value of the field.
     *
     * @return boolean indicating if max aggregation is applied
     */
    boolean max() default false;

    /**
     * The minimum value of the field.
     *
     * @return boolean indicating if min aggregation is applied
     */
    boolean min() default false;

    /**
     * The count of the field.
     *
     * @return boolean indicating if count aggregation is applied
     */
    boolean count() default false;

}
