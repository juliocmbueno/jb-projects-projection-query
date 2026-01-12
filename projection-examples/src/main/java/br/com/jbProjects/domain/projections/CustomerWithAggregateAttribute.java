package br.com.jbProjects.domain.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.domain.Customer;
import br.com.jbProjects.processor.selectOperator.handler.SumHandler;

/**
 * Projection example demonstrating the use of aggregated attributes.
 *
 * <p>This projection maps the {@code age} field normally while also defining
 * an aggregated attribute using {@code count = true}. In this case,
 * the projection engine will generate a {@code COUNT(id)} expression and map
 * the result into the {@code count} property.</p>
 *
 * <p>This type of projection is typically used in grouped queries, summaries,
 * dashboards, or statistical views where aggregated values need to be returned
 * alongside standard attributes.</p>
 *
 * <p>Example of the SQL generated (simplified):</p>
 *
 * <pre>{@code
 * select
 *   customer.age as age,
 *   count(customer.id) as count
 * from Customer customer
 * group by customer.age
 * }</pre>
 */
@Projection(of = Customer.class)
public record CustomerWithAggregateAttribute(
        @ProjectionField Integer age,
        @ProjectionField(value = "id", selectHandler = SumHandler.class) Long count
) {
}
