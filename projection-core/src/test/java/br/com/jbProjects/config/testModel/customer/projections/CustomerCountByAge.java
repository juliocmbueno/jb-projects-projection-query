package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.config.testModel.customer.domain.Customer;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Projection(of = Customer.class)
public record CustomerCountByAge(
        @ProjectionField(value = "id", count = true) Long quantity,
        @ProjectionField Integer age
) {
}
