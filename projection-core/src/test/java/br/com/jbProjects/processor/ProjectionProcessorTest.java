package br.com.jbProjects.processor;

import br.com.jbProjects.helper.BaseJpaTest;
import br.com.jbProjects.testModel.customer.domain.Customer;
import br.com.jbProjects.testModel.customer.projections.CustomerAutoCompleteClass;
import br.com.jbProjects.testModel.customer.projections.CustomerAutoCompleteRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 */
class ProjectionProcessorTest extends BaseJpaTest {

    private Customer customer;

    @Override
    protected void onBeforeAll() {
        persistCustomer();
    }

    private void persistCustomer() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        persist(customer);
    }

    @Override
    protected void onAfterAll() {
        remove(customer);
    }

    @Test
    void execute_withClass() {
        ProjectionProcessor projectionProcessor = new ProjectionProcessor(entityManager);
        List<CustomerAutoCompleteClass> customers = projectionProcessor.execute(CustomerAutoCompleteClass.class);
        Assertions.assertEquals(1, customers.size());

        CustomerAutoCompleteClass result = customers.getFirst();
        Assertions.assertEquals(result.getId(), customer.getId());
        Assertions.assertEquals(result.getName(), customer.getName());
        Assertions.assertEquals(result.getCustomerEmail(), customer.getEmail());
        Assertions.assertNull(result.getNonProjectedField());
    }

    @Test
    void execute_withRecord() {
        ProjectionProcessor projectionProcessor = new ProjectionProcessor(entityManager);
        List<CustomerAutoCompleteRecord> customers = projectionProcessor.execute(CustomerAutoCompleteRecord.class);
        Assertions.assertEquals(1, customers.size());

        CustomerAutoCompleteRecord result = customers.getFirst();
        Assertions.assertEquals(result.id(), customer.getId());
        Assertions.assertEquals(result.name(), customer.getName());
        Assertions.assertEquals(result.customerEmail(), customer.getEmail());
        Assertions.assertNull(result.notProjectedField());
    }
}
