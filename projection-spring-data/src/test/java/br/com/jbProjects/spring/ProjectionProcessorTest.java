package br.com.jbProjects.spring;

import br.com.jbProjects.processor.ProjectionProcessor;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.query.ProjectionQuery;
import br.com.jbProjects.spring.configTest.BaseSpringBootTest;
import br.com.jbProjects.spring.configTest.DataAccessObject;
import br.com.jbProjects.spring.configTest.model.Customer;
import br.com.jbProjects.spring.configTest.model.CustomerProjectionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by julio.bueno on 27/11/2025.
 */
public class ProjectionProcessorTest extends BaseSpringBootTest {

    @Autowired
    private ProjectionProcessor projectionProcessor;

    @Autowired
    protected DataAccessObject dataAccessObject;

    private static Customer customerJohn;
    private static Customer customerJane;

    @Override
    public void onBeforeClass() {
        persistCustomerJohn();
        persistCustomerJane();
    }

    public void persistCustomerJohn() {
        customerJohn = new Customer("John Doe", "john.doe@mail.com", 30);
        dataAccessObject.persist(customerJohn);
    }

    public void persistCustomerJane() {
        customerJane = new Customer("Jane Doe", "jane@doe.com", 30);
        dataAccessObject.persist(customerJane);
    }

    @Override
    public void onAfterClass() {
        dataAccessObject.remove(customerJane);
        dataAccessObject.remove(customerJohn);
    }

    @Test
    public void execute(){
        List<CustomerProjectionData> customers = projectionProcessor.execute(CustomerProjectionData.class);
        Assertions.assertEquals(2, customers.size());

        CustomerProjectionData data = findById(customers, customerJohn.getId());
        Assertions.assertNotNull(data);
        Assertions.assertEquals(customerJohn.getName(), data.name());

        data = findById(customers, customerJane.getId());
        Assertions.assertNotNull(data);
        Assertions.assertEquals(customerJane.getName(), data.name());
    }

    private CustomerProjectionData findById( List<CustomerProjectionData> customers, Long id){
        return customers.stream().filter(item -> item.id().equals(id)).findFirst().orElse(null);
    }

    @Test
    public void execute_withProjectionQuery(){
        var query = ProjectionQuery
                .fromTo(Customer.class, CustomerProjectionData.class)
                .filter("name", ProjectionFilterOperator.EQUAL, customerJane.getName());

        List<CustomerProjectionData> customers = projectionProcessor.execute(query);
        Assertions.assertEquals(1, customers.size());

        CustomerProjectionData data = customers.getFirst();
        Assertions.assertEquals(customerJane.getId(), data.id());
        Assertions.assertEquals(customerJane.getName(), data.name());
    }

}
