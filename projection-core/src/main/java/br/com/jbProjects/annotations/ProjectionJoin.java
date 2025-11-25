package br.com.jbProjects.annotations;

import jakarta.persistence.criteria.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p> Indicates a join to be used in a projection. </p>
 * <p> Should be used when there is a need to change the join type or define an alias for the join</p>
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ProjectionJoin {

    /**
     * The path to the association to be joined.
     * <p>It's possible to use nested paths to access fields in related entities.</p>
     * <p> ex: "name" or "address.city.name" </p>
     * <p>If you define for example the following path "address.city.state" of type LEFT. The entire join hierarchy from address will be applied with LEFT</p>
     * */
    String path();

    /** The alias to be used for the joined association. */
    String alias() default "";

    /** The type of join to be performed. Default is INNER JOIN. */
    JoinType type() default JoinType.INNER;

}
