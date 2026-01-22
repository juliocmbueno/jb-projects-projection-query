package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.processor.selectOperator.handler.MinHandler;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Projection(of = Customer.class)
public record CustomerMinAge(
        @ProjectionField(value = "age", selectHandler = MinHandler.class) Integer minAge
) {
}
