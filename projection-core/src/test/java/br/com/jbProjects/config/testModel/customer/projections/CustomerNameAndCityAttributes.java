package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.config.testModel.customer.domain.Customer;

/**
 * Created by julio.bueno on 24/11/2025.
 */
@Projection(
        of = Customer.class
)
public record CustomerNameAndCityAttributes(
        @ProjectionField String name,
        @ProjectionField("city.id") Integer cityId,
        @ProjectionField("city.name") String cityName,
        @ProjectionField("city.state.name") String state
) {
}
