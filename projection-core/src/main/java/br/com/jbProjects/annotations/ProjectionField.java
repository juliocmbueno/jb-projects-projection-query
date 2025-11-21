package br.com.jbProjects.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProjectionField {

    String value() default "";

}
