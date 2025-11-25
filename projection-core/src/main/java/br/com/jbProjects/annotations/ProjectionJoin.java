package br.com.jbProjects.annotations;

import jakarta.persistence.criteria.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>
 *     Indicates a join to be used in a projection.
 * </p>
 * <p>
 *     Should be used when there is a need to change the join type or define an alias for the join
 * </p>
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ProjectionJoin {

    String path();

    String alias() default "";

    JoinType type() default JoinType.INNER;

}
