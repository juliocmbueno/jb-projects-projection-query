package br.com.jbProjects.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Projection {
    Class<?> of();
}
