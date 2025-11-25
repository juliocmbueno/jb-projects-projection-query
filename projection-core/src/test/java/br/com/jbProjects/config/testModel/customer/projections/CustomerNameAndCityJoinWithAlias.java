package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.testModel.customer.domain.Customer;

/**
 * Created by julio.bueno on 24/11/2025.
 */
@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "mainAddress", alias = "address"),
                @ProjectionJoin(path = "address.city.state", alias = "cityState")
        }
)
public record CustomerNameAndCityJoinWithAlias(
        @ProjectionField String name,
        @ProjectionField("address.city.id") Long cityId,
        @ProjectionField("address.city.name") String cityName,
        @ProjectionField("cityState.name") String state
) {
}


