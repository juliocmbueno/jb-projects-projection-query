package br.com.jbProjects.spring.configTest.model;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;

/**
 * Created by julio.bueno on 27/11/2025.
 */
@Projection(of = Customer.class)
public record CustomerProjectionData(
        @ProjectionField Long id,
        @ProjectionField String name
) {
}
