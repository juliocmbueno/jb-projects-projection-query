package br.com.jbProjects.spring;

import br.com.jbProjects.domain.Customer;
import br.com.jbProjects.domain.projections.*;
import br.com.jbProjects.processor.ProjectionProcessor;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.order.OrderDirection;
import br.com.jbProjects.processor.query.ProjectionQuery;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by julio.bueno on 27/11/2025.
 */
@SuppressWarnings("unused")
@Component
public class ProjectionsSpringExample {

    private final ProjectionProcessor projectionProcessor;

    public ProjectionsSpringExample(ProjectionProcessor projectionProcessor){
        this.projectionProcessor = projectionProcessor;
    }

    /**
     * Basic example of fetching projections using a projection class.
     *
     * @see CustomerBasicDataClass
     */
    public void fetchCustomerBasicDataClass(){
        List<CustomerBasicDataClass> example = projectionProcessor.execute(CustomerBasicDataClass.class);
    }

    /**
     * Basic example of fetching projections using a projection record.
     *
     * @see CustomerBasicDataRecord
     */
    public void fetchCustomerBasicDataRecord(){
        List<CustomerBasicDataRecord> example = projectionProcessor.execute(CustomerBasicDataRecord.class);
    }

    /**
     * Example of fetching projections using a projection query.
     *
     * <p>Example of the SQL generated (simplified):</p>
     *
     * <pre>{@code
     * select
     *  c.id as id,
     *  c.name as name,
     *  c.email as contactEmail
     * from Customer c
     * where c.name like 'John%'
     * order by c.email asc
     * limit 10 offset 0
     * }</pre>
     *
     * @see CustomerBasicDataRecord
     */
    public void fetchWithProjectionQuery(){
        List<CustomerBasicDataRecord> example = projectionProcessor.execute(
                ProjectionQuery
                        .fromTo(Customer.class, CustomerBasicDataRecord.class)
                        .distinct()
                        .filter("name", ProjectionFilterOperator.LIKE, "John%")
                        .order("email", OrderDirection.ASC)
                        .paging(0, 10)
        );
    }

    /**
     * <p></p>Example of combining filter and specification.</p>
     *
     * <p>Note: Although the Specification is declared *after* the filter in the fluent API,
     * it is still applied FIRST internally. You can confirm this by checking the generated SQL.</p>
     *
     * <p>Example of the SQL generated (simplified):</p>
     *
     * <pre>{@code
     * select
     *  c.id as id,
     *  c.name as name,
     *  c.email as contactEmail
     * from Customer c
     * where
     *  c.age >= 18
     *  and c.name like 'John%'
     * }</pre>
     *
     * @see CustomerBasicDataRecord
     */
    public void fetchWithFilterAndSpecification(){
        List<CustomerBasicDataRecord> example = projectionProcessor.execute(
                ProjectionQuery
                        .fromTo(Customer.class, CustomerBasicDataRecord.class)
                        .filter("name", ProjectionFilterOperator.LIKE, "John%")
                        .specification((criteriaBuilder, query, root, pathResolver) ->
                                criteriaBuilder.ge(pathResolver.resolve(root, "age"), 18))
        );
    }

    /**
     * Example of fetching projections using a string-based filter operation.
     *
     * <p>This approach allows the use of any {@link ProjectionFilterOperator}
     * through its string representation. If an invalid or unregistered operator
     * is provided, an {@link IllegalArgumentException} will be thrown.
     * Additional operators can be registered programmatically via the
     * {@code ProjectionFilterOperatorProvider} bean using its {@code register} method.</p>
     *
     * <p>Example of the SQL generated (simplified):</p>
     *
     * <pre>{@code
     * select
     *   c.id   as id,
     *   c.name as name,
     *   c.email as contactEmail
     * from Customer c
     * where c.name like 'John%'
     * }</pre>
     *
     * @see CustomerBasicDataRecord
     */
    public void fetchWithStringFilterOperation(){
        List<CustomerBasicDataRecord> example = projectionProcessor.execute(
                ProjectionQuery
                        .fromTo(Customer.class, CustomerBasicDataRecord.class)
                        .filter("name", "like", "John%")
        );
    }

    /**
     * Example of fetching projections using filters applied to nested attributes.
     *
     * <p>This demonstrates how the projection engine resolves deep property paths
     * (e.g., {@code mainAddress.city.state.id}) and automatically creates the
     * required joins. If the projection class does not explicitly define the join
     * strategy, inner joins will be used by default.</p>
     *
     * <p>Example of the SQL generated (simplified):</p>
     *
     * <pre>{@code
     * select
     *   customer.id as id,
     *   customer.name as name,
     *   customer.email as contactEmail
     * from Customer customer
     * inner join Address address on customer.mainAddress = address.id
     * inner join State state on address.state = state.id
     * where state.id = 1
     * }</pre>
     *
     * @see CustomerBasicDataRecord
     */
    public void fetchWithNestedAttributeFilter(){
        List<CustomerBasicDataRecord> example = projectionProcessor.execute(
                ProjectionQuery
                        .fromTo(Customer.class, CustomerBasicDataRecord.class)
                        .filter("mainAddress.city.state.id", ProjectionFilterOperator.EQUAL, 1)
        );
    }

    /**
     * Example of fetching projections with nested attributes and custom join types.
     *
     * @see CustomerWithNestedAttributes
     */
    public void fetchWithNestedAttributesSelect(){
        List<CustomerWithNestedAttributes> example = projectionProcessor.execute(CustomerWithNestedAttributes.class);
    }

    /**
     * Example of fetching projections with aggregate attributes.
     *
     * @see CustomerWithAggregateAttribute
     */
    public void fetchWithAggregateAttributes(){
        List<CustomerWithAggregateAttribute> example = projectionProcessor.execute(CustomerWithAggregateAttribute.class);
    }

    /**
     * Example of fetching projections using join aliases.
     *
     * @see CustomerWithJoinAlias
     */
    public void fetchWithJoinAlias(){
        List<CustomerWithJoinAlias> example = projectionProcessor.execute(CustomerWithJoinAlias.class);
    }

    /**
     * Example of fetching projections using join aliases with filtering.
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
     * where mainCity.name = 'São Paulo'
     * }</pre>
     *
     * @see CustomerWithJoinAlias
     */
    public void fetchFilteringByAliasPath(){
        List<CustomerWithJoinAlias> example = projectionProcessor.execute(
                ProjectionQuery
                        .fromTo(Customer.class, CustomerWithJoinAlias.class)
                        .filter("mainCityAlias.name", ProjectionFilterOperator.EQUAL, "São Paulo")
        );
    }
}
