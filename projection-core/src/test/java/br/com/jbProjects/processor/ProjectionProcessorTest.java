package br.com.jbProjects.processor;

import br.com.jbProjects.config.helper.BaseJpaTest;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerAutoCompleteClass;
import br.com.jbProjects.config.testModel.customer.projections.CustomerAutoCompleteRecord;
import br.com.jbProjects.processor.query.ProjectionQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 */
class ProjectionProcessorTest extends BaseJpaTest {

    private Customer customer;
    private ProjectionProcessor processor;

    @Override
    protected void onBeforeAll() {
        this.processor = new ProjectionProcessor(entityManager);
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
        List<CustomerAutoCompleteClass> customers = processor.execute(CustomerAutoCompleteClass.class);
        Assertions.assertEquals(1, customers.size());

        CustomerAutoCompleteClass result = customers.getFirst();
        Assertions.assertEquals(result.getId(), customer.getId());
        Assertions.assertEquals(result.getName(), customer.getName());
        Assertions.assertEquals(result.getCustomerEmail(), customer.getEmail());
        Assertions.assertNull(result.getNonProjectedField());
    }

    @Test
    void execute_withRecord() {
        List<CustomerAutoCompleteRecord> customers = processor.execute(CustomerAutoCompleteRecord.class);
        Assertions.assertEquals(1, customers.size());

        CustomerAutoCompleteRecord result = customers.getFirst();
        Assertions.assertEquals(result.id(), customer.getId());
        Assertions.assertEquals(result.name(), customer.getName());
        Assertions.assertEquals(result.customerEmail(), customer.getEmail());
        Assertions.assertNull(result.notProjectedField());
    }

    @Test
    void execute_withProjectionQuerySpecification() {
        Customer otherCustomer = new Customer();
        otherCustomer.setName("Jane Smith");
        otherCustomer.setEmail("jane.smith@example.com");
        persist(otherCustomer);

        try{
            List<CustomerAutoCompleteRecord> customers = processor.execute(
                    ProjectionQuery
                            .fromTo(Customer.class, CustomerAutoCompleteRecord.class)
                            .specification((criteriaBuilder, query, root) -> criteriaBuilder.equal(root.get("name"), otherCustomer.getName()))
            );
            Assertions.assertEquals(1, customers.size());

            CustomerAutoCompleteRecord result = customers.getFirst();
            Assertions.assertEquals(result.id(), otherCustomer.getId());
            Assertions.assertEquals(result.name(), otherCustomer.getName());
            Assertions.assertEquals(result.customerEmail(), otherCustomer.getEmail());
            Assertions.assertNull(result.notProjectedField());

        }finally {
            remove(otherCustomer);

        }
    }

    @Test
    void execute_withProjectionQueryPaging() {
        List<Customer> customers = new ArrayList<>();
        for(int index = 1; index <= 15; index++){
            Customer otherCustomer = new Customer();
            otherCustomer.setName("Customer Paging " + index);
            otherCustomer.setEmail("customer_pagina"+index+"@example.com");
            persist(otherCustomer);
            customers.add(otherCustomer);
        }

        try{
            List<CustomerAutoCompleteRecord> result = processor.execute(
                    ProjectionQuery
                            .fromTo(Customer.class, CustomerAutoCompleteRecord.class)
                            .paging(0, 10)
            );
            Assertions.assertEquals(10, result.size());

        }finally {
            customers.forEach(this::remove);

        }
    }

}
