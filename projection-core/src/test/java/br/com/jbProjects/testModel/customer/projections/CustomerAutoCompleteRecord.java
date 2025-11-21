package br.com.jbProjects.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.testModel.customer.domain.Customer;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Projection(of = Customer.class)
public record CustomerAutoCompleteRecord(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("email") String customerEmail,
        String notProjectedField
) { }
