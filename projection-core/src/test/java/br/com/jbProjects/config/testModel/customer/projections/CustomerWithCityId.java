package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.testModel.customer.domain.Customer;

/**
 * Created by julio.bueno on 07/01/2026.
 */
@Projection(
        of = Customer.class,
        joins = {
            @ProjectionJoin(path = "mainAddress.city")
        }
)
public record CustomerWithCityId(
        @ProjectionField
        Long id,
        @ProjectionField
        String name,
        @ProjectionField("mainAddress.city.id")
        Long cityId,
        @ProjectionField("secondaryAddress.city.id")
        Long secondaryCityId
) {
}
