package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import jakarta.persistence.criteria.JoinType;

/**
 * Created by julio.bueno on 24/11/2025.
 */
@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "secondaryAddress", type = JoinType.LEFT)
        }
)
public record CustomerNameAndCityAttributes(
        @ProjectionField String name,
        @ProjectionField("mainAddress.city.id") Long cityId,
        @ProjectionField("mainAddress.city.name") String cityName,
        @ProjectionField("mainAddress.city.state.name") String state,
        @ProjectionField("secondaryAddress.city.name") String secondaryCidy,
        @ProjectionField("secondaryAddress.city.state.name") String secondaryState
) {
}
