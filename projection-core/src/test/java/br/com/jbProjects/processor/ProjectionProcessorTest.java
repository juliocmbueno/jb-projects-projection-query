package br.com.jbProjects.processor;

import br.com.jbProjects.config.helper.BaseJpaTest;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.*;
import br.com.jbProjects.processor.query.ProjectionQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
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

    @Test
    void execute_withProjectionQueryDistinct() {
        Customer otherCustomer = new Customer();
        otherCustomer.setName(customer.getName());
        persist(otherCustomer);

        try{
            List<CustomerName> result = processor.execute(
                    ProjectionQuery
                            .fromTo(Customer.class, CustomerName.class)
                            .distinct()
            );

            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(result.getFirst().name(), otherCustomer.getName());

        }finally {
            remove(otherCustomer);

        }
    }

    @Test
    void execute_withProjectionQuery_countResult() {
        List<Customer> customers = new ArrayList<>();
        for(int index = 1; index <= 5; index++){
            Customer otherCustomer = new Customer();
            otherCustomer.setName("Customer Count " + index);
            persist(otherCustomer);
            customers.add(otherCustomer);
        }

        try{
            List<CustomerCount> result = processor.execute(
                    ProjectionQuery
                            .fromTo(Customer.class, CustomerCount.class)
                            .specification((criteriaBuilder, query, root) ->
                                    criteriaBuilder.like(root.get("name"), "Customer Count%"))
            );

            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(5, result.getFirst().quantity());

        }finally {
            customers.forEach(this::remove);

        }
    }

    @Test
    void execute_withProjectionQuery_minResult() {
        Customer customer_age_1 = new Customer();
        customer_age_1.setAge(1);
        persist(customer_age_1);

        Customer customer_age_2 = new Customer();
        customer_age_2.setAge(2);
        persist(customer_age_2);

        Customer customer_age_3 = new Customer();
        customer_age_3.setAge(3);
        persist(customer_age_3);

        try{
            List<CustomerMinAge> result = processor.execute(CustomerMinAge.class);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1, result.getFirst().minAge());

        }finally {
            remove(customer_age_3);
            remove(customer_age_2);
            remove(customer_age_1);

        }
    }

    @Test
    void execute_withProjectionQuery_maxResult() {
        Customer customer_age_1 = new Customer();
        customer_age_1.setAge(1);
        persist(customer_age_1);

        Customer customer_age_2 = new Customer();
        customer_age_2.setAge(2);
        persist(customer_age_2);

        Customer customer_age_3 = new Customer();
        customer_age_3.setAge(3);
        persist(customer_age_3);

        try{
            List<CustomerMaxAge> result = processor.execute(CustomerMaxAge.class);
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(3, result.getFirst().maxAge());

        }finally {
            remove(customer_age_3);
            remove(customer_age_2);
            remove(customer_age_1);

        }
    }

    @Test
    void execute_withProjectionQuery_sumResult() {
        Customer customer_1 = new Customer();
        customer_1.setName("sum id");
        persist(customer_1);

        Customer customer_2 = new Customer();
        customer_2.setName("sum id");
        persist(customer_2);

        Customer customer_3 = new Customer();
        customer_3.setName("sum id");
        persist(customer_3);

        try{
            List<CustomerSumId> result = processor.execute(
                    ProjectionQuery.fromTo(Customer.class, CustomerSumId.class)
                            .specification((criteriaBuilder, query, root) ->
                                    criteriaBuilder.equal(root.get("name"), "sum id"))
            );
            Assertions.assertEquals(1, result.size());

            Long expected = customer_1.getId() + customer_2.getId() + customer_3.getId();
            Assertions.assertEquals(expected, result.getFirst().sumId());

        }finally {
            remove(customer_3);
            remove(customer_2);
            remove(customer_1);

        }
    }

    @Test
    void execute_withProjectionQuery_countByAgeResult() {
        Customer customer_1 = new Customer();
        customer_1.setName("count by age");
        customer_1.setAge(10);
        persist(customer_1);

        Customer customer_2 = new Customer();
        customer_2.setName("count by age");
        customer_2.setAge(10);
        persist(customer_2);

        Customer customer_3 = new Customer();
        customer_3.setName("count by age");
        customer_3.setAge(20);
        persist(customer_3);

        try{
            List<CustomerCountByAge> results = processor
                    .execute(
                            ProjectionQuery
                                    .fromTo(Customer.class, CustomerCountByAge.class)
                                    .specification((criteriaBuilder, query, root) ->
                                            criteriaBuilder.equal(root.get("name"), "count by age"))
                    )
                    .stream()
                    .sorted(Comparator.comparing(CustomerCountByAge::age))
                    .toList();

            Assertions.assertEquals(2, results.size());

            CustomerCountByAge countByAge = results.getFirst();
            Assertions.assertEquals(10, countByAge.age());
            Assertions.assertEquals(2, countByAge.quantity(), "Exists two customers with age equals 10");

            countByAge = results.get(1);
            Assertions.assertEquals(20, countByAge.age());
            Assertions.assertEquals(1, countByAge.quantity(), "Exists two customers with age equals 1");

        }finally {
            remove(customer_3);
            remove(customer_2);
            remove(customer_1);

        }
    }
}
