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

    /** The path to the field in the entity to be projected. */
    String value() default "";

    /** Aggregation functions to be applied to the field. */
    boolean sum() default false;

    /** The maximum value of the field. */
    boolean max() default false;

    /** The minimum value of the field. */
    boolean min() default false;

    /** The count of the field. */
    boolean count() default false;

}
