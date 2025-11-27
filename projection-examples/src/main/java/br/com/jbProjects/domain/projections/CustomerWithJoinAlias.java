package br.com.jbProjects.domain.projections;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.domain.Customer;
import jakarta.persistence.criteria.JoinType;

/**
 * Example demonstrating the use of join aliases inside a {@link Projection}.
 *
 * <p>This projection showcases different strategies for declaring aliases:
 * direct aliases, nested aliases, and aliases used to reference previously
 * defined join paths. Each alias created in {@link ProjectionJoin} can later be
 * used in {@link ProjectionField} to map attributes based on the aliased path.</p>
 *
 * <p>The aliases are **internal** to the projection engine and are **not directly reflected in the generated SQL**.
 * The engine resolves the full path of each field by internally replacing the
 * aliases until the complete attribute path is determined.</p>
 *
 * <p>In this example:</p>
 * <ul>
 *   <li><code>mainAddress</code> is joined using the alias <code>mainAddressAlias</code>.</li>
 *   <li><code>mainAddressAlias.city</code> is joined using the alias <code>mainCityAlias</code>.</li>
 *   <li><code>secondaryAddress.city</code> is joined using the alias <code>secondaryCityAlias</code>
 *       using a LEFT join.</li>
 * </ul>
 *
 * <p>These aliases are then used in the projection fields to reference
 * nested attributes directly from the aliased join paths.</p>
 *
 * <p>Simplified example of the SQL generated:</p>
 *
 * <pre>{@code
 * select
 *   customer.id as id,
 *   customer.name as name,
 *   mainCityAlias.name as mainCity,
 *   secondaryCityAlias.name as secondaryCity
 * from Customer customer
 * inner join Address mainAddress on mainAddress.id = customer.mainAddress
 * inner join City mainCity on mainCity.id = mainAddress.city
 * left join Address secondaryAddress on secondaryAddress.id = customer.secondaryAddress
 * left join City secondaryCity on secondaryCity.id = secondaryAddress.city
 * }</pre>
 */
@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "mainAddress", alias = "mainAddressAlias"),
                @ProjectionJoin(path = "mainAddressAlias.city", alias = "mainCityAlias"),
                @ProjectionJoin(path = "secondaryAddress.city", alias = "secondaryCityAlias", type = JoinType.LEFT)

        }
)
public record CustomerWithJoinAlias(
        @ProjectionField Long id,
        @ProjectionField String name,
        @ProjectionField("mainCityAlias.name") String mainCity,
        @ProjectionField("secondaryCityAlias.name") String secondaryCity
) {
}
