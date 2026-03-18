package br.com.jbProjects.processor;

import br.com.jbProjects.config.helper.BaseJpaTest;
import br.com.jbProjects.config.helper.JPAHelper;
import br.com.jbProjects.config.helper.TestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityAttributes;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.query.ProjectionQuery;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Created by julio.bueno on 18/03/2026.
 */
class ProjectionProcessorDebugTest extends BaseJpaTest {

    private ProjectionProcessorDebug processorDebug;

    @Override
    protected void onBeforeAll() {
        processorDebug = new ProjectionProcessorDebug(JPAHelper.entityManagerFactory());
    }

    @Test
    void testPreviewSQL() {
        ProjectionQuery<Customer, CustomerNameAndCityAttributes> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerNameAndCityAttributes.class)
                .filter("id", ProjectionFilterOperator.EQUAL, -1L)
                .paging(0, 10);

        String sql = processorDebug.previewSQL(projectionQuery);

        String expectedSQL = """
                select
                    c1_0.name,
                    ma1_0.city_id,
                    c2_0.name,
                    s1_0.name,
                    c3_0.name,
                    s2_0.name
                from
                    Customer c1_0
                join
                    Address ma1_0
                        on ma1_0.id=c1_0.mainAddress_id
                join
                    City c2_0
                        on c2_0.id=ma1_0.city_id
                join
                    State s1_0
                        on s1_0.id=c2_0.state_id
                left join
                    Address sa1_0
                        on sa1_0.id=c1_0.secondaryAddress_id
                left join
                    City c3_0
                        on c3_0.id=sa1_0.city_id
                left join
                    State s2_0
                        on s2_0.id=c3_0.state_id
                where
                    c1_0.id=?
                offset
                    ? rows
                fetch
                    first ? rows only""";

        TestUtils.assertIgnoreWhitespace(expectedSQL, sql);
    }

    @Test
    void testPreviewSQLWithoutHypersistence() {
        ProjectionProcessorDebug spyProcessorDebug = Mockito.spy(processorDebug);
        Mockito
                .doAnswer(invocationOnMock -> {
                    throw new NoClassDefFoundError("Hypersistence Utils not found");
                })
                .when(spyProcessorDebug)
                .extractSQLWithHypersistence(Mockito.any());

        ProjectionQuery<Customer, CustomerNameAndCityAttributes> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerNameAndCityAttributes.class)
                .filter("id", ProjectionFilterOperator.EQUAL, -1L)
                .paging(0, 10);

        String sql = spyProcessorDebug.previewSQL(projectionQuery);

        String expectedSQL = """
                SQL preview generated via logging
                
                ┌───────────────────────────────────────────────────────────────┐
                │  Current Mode: Query Execution (LIMIT 1)                      │
                │  • Fetches 1 row from database                                │
                │  • Minimal performance impact                                 │
                │  • Requires active database connection                        │
                ├───────────────────────────────────────────────────────────────┤
                │  For Zero-Cost Preview:                                       │
                │  Add Hypersistence Utils dependency                           │
                │                                                               │
                │  <dependency>                                                 │
                │    <groupId>io.hypersistence</groupId>                        │
                │    <artifactId>hypersistence-utils-hibernate-63</artifactId>  │
                │    <version>3.15.2</version>                                  │
                │  </dependency>                                                │
                ├───────────────────────────────────────────────────────────────┤
                │  To See SQL in Logs:                                          │
                │  logging.level.org.hibernate.SQL=DEBUG                        │
                │  or                                                           │
                │  spring.jpa.properties.hibernate.format_sql=true              │
                └───────────────────────────────────────────────────────────────┘""";

        TestUtils.assertIgnoreWhitespace(expectedSQL, sql);
    }

}
