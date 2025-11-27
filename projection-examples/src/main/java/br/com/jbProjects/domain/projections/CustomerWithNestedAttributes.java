package br.com.jbProjects.domain.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.domain.Customer;
import jakarta.persistence.criteria.JoinType;

/**
 * Projection example demonstrating explicit join configuration for nested attributes.
 *
 * <p>By declaring a {@code @ProjectionJoin} on {@code secondaryAddress} with
 * {@link JoinType#LEFT}, all joins derived from this root path will also use
 * LEFT JOIN automatically. This means that paths such as:</p>
 *
 * <ul>
 *   <li>{@code secondaryAddress}</li>
 *   <li>{@code secondaryAddress.city}</li>
 *   <li>{@code secondaryAddress.city.state}</li>
 * </ul>
 *
 * <p>will all be resolved using LEFT JOIN, unless explicitly overridden.</p>
 *
 * <p>This allows projections to safely traverse optional relationships
 * (e.g., nullable secondary address) without excluding records.</p>
 *
 * <p>Simplified example of the SQL generated:</p>
 *
 * <pre>{@code
 * select
 *  customer.id as id,
 *  customer.name as name,
 * from Customer customer
 * inner join Address mainAddress on customer.mainAddress = mainAddress.id
 * inner join City mainCity on mainAddress.city = mainCity.id
 * left join Address secondaryAddress on customer.secondaryAddress = secondaryAddress.id
 * left join City secondaryCity on secondaryAddress.city = secondaryCity.id
 * }</pre>
 */
@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "secondaryAddress", type = JoinType.LEFT)
        }
)
public record CustomerWithNestedAttributes(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("mainAddress.city.name") String mainCity,
        @ProjectionField("secondaryAddress.city.name") String secondaryCity
) {
}
