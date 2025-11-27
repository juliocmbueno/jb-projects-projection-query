package br.com.jbProjects.domain.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.domain.Customer;
import lombok.Getter;
import lombok.Setter;

/**
 * Basic example demonstrating the use of {@link Projection} on a standard Java class.
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
@Getter
@Setter
@Projection(of = Customer.class)
public class CustomerBasicDataClass{

    /**
     * Maps the entity attribute with the same name ("id").
     * When no value is specified, the field name is used as the default source path.
     */
    @ProjectionField
    private Long id;

    /**
     * Maps the entity attribute with the same name ("name").
     */
    @ProjectionField
    private String name;

    /**
     * Custom attribute mapping: maps {@code Customer.email} into {@code contactEmail}.
     */
    @ProjectionField("email")
    private String contactEmail;

    /**
     * This field is not annotated with {@link ProjectionField} and therefore
     * will be ignored entirely in the generated projection.
     */
    private String noMappingField;

}
