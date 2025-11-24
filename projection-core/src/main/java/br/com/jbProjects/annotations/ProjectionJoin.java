package br.com.jbProjects.annotations;

import jakarta.persistence.criteria.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ProjectionJoin {

    String path();

    String alias() default "";

    JoinType type() default JoinType.INNER;

}
