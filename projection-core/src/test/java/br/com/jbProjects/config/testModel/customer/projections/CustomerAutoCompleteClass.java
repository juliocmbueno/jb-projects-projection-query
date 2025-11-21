package br.com.jbProjects.config.testModel.customer.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Getter
@Setter
@Projection(of = Customer.class)
public class CustomerAutoCompleteClass {

    @ProjectionField
    private Long id;

    @ProjectionField
    private String name;

    @ProjectionField("email")
    private String customerEmail;

    private String nonProjectedField;

}
