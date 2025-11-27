package br.com.jbProjects.domain.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.domain.Customer;

/**
 * Basic example demonstrating the use of {@link Projection} on a standard Java Record.
 * <p>This example highlights that projections can be defined on both classes and records.</p>
 *
 * <p>Simplified example of the SQL generated:</p>
 *
 * <pre>{@code
 * select
 *   c.id as id,
 *   c.name as name,
 *   c.email as contactEmail
 * from Customer c
 * }</pre>
 */
@Projection(of = Customer.class)
public record CustomerBasicDataRecord(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("email") String contactEmail,
        String noMappingField
) {
}
