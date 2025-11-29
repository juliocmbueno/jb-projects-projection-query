package br.com.jbProjects.processor;

import br.com.jbProjects.config.helper.BaseJpaTest;
import br.com.jbProjects.config.testModel.address.domain.Address;
import br.com.jbProjects.config.testModel.city.domain.City;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.*;
import br.com.jbProjects.config.testModel.state.domain.State;
import br.com.jbProjects.processor.filter.BetweenValues;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.order.OrderDirection;
import br.com.jbProjects.processor.query.ProjectionQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by julio.bueno on 21/11/2025.
 */
class ProjectionProcessorTest extends BaseJpaTest {

    private State state;
    private City cityGoiania;
    private City cityAnapolis;
    private Address mainAddressGoiania;
    private Address secondaryAddressAnapolis;
    private Customer customer;
    private ProjectionProcessor processor;

    @Override
    protected void onBeforeAll() {
        this.processor = new ProjectionProcessor(entityManager);
        persistState();
        persistCityGoiania();
        persistCityAnapolis();
        persistMainAddress();
        persisSecondaryAddress();
        persistCustomer();
    }

    private void persistState() {
        state = new State();
        state.setName("Goiás");
        persist(state);
    }

    private void persistCityGoiania() {
        cityGoiania = new City();
        cityGoiania.setName("Goiânia");
        cityGoiania.setState(state);
        persist(cityGoiania);
    }


    private void persistCityAnapolis() {
        cityAnapolis = new City();
        cityAnapolis.setName("Anapolis");
        cityAnapolis.setState(state);
        persist(cityAnapolis);
    }

    private void persistMainAddress() {
        mainAddressGoiania = new Address();
        mainAddressGoiania.setCity(cityGoiania);
        persist(mainAddressGoiania);
    }

    private void persisSecondaryAddress() {
        secondaryAddressAnapolis = new Address();
        secondaryAddressAnapolis.setCity(cityAnapolis);
        persist(secondaryAddressAnapolis);
    }

    private void persistCustomer() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setMainAddress(mainAddressGoiania);
        persist(customer);
    }

    @Override
    protected void onAfterAll() {
        remove(customer);
        remove(mainAddressGoiania);
        remove(secondaryAddressAnapolis);
        remove(cityGoiania);
        remove(cityAnapolis);
        remove(state);
    }

    @Test
    void execute_withClass() {
        List<CustomerAutoCompleteClass> customers = processor.execute(CustomerAutoCompleteClass.class);
        Assertions.assertEquals(1, customers.size());

        CustomerAutoCompleteClass result = customers.get(0);
        Assertions.assertEquals(result.getId(), customer.getId());
        Assertions.assertEquals(result.getName(), customer.getName());
        Assertions.assertEquals(result.getCustomerEmail(), customer.getEmail());
        Assertions.assertNull(result.getNonProjectedField());
    }

    @Test
    void execute_withRecord() {
        List<CustomerAutoCompleteRecord> customers = processor.execute(CustomerAutoCompleteRecord.class);
        Assertions.assertEquals(1, customers.size());

        CustomerAutoCompleteRecord result = customers.get(0);
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

            CustomerAutoCompleteRecord result = customers.get(0);
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
            Assertions.assertEquals(result.get(0).name(), otherCustomer.getName());

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
            Assertions.assertEquals(5, result.get(0).quantity());

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
            Assertions.assertEquals(1, result.get(0).minAge());

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
            Assertions.assertEquals(3, result.get(0).maxAge());

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
            Assertions.assertEquals(expected, result.get(0).sumId());

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

            CustomerCountByAge countByAge = results.get(0);
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

    @Test
    void execute_withProjectionQuery_nextEntityAttributes() {
        List<CustomerNameAndCityAttributes> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerNameAndCityAttributes.class)
                                .specification((criteriaBuilder, query, root) ->
                                        criteriaBuilder.equal(root.get("id"), customer.getId()))
                );

        Assertions.assertEquals(1, results.size());
        CustomerNameAndCityAttributes result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());
        Assertions.assertEquals(customer.getMainAddress().getCity().getId(), result.cityId());
        Assertions.assertEquals(customer.getMainAddress().getCity().getName(), result.cityName());
        Assertions.assertEquals(customer.getMainAddress().getCity().getState().getName(), result.state());
        Assertions.assertNull(result.secondaryCidy());
        Assertions.assertNull(result.secondaryState());
    }

    @Test
    void execute_withProjectionQuery_joinWithAlias() {
        List<CustomerNameAndCityJoinWithAlias> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerNameAndCityJoinWithAlias.class)
                                .specification((criteriaBuilder, query, root) ->
                                        criteriaBuilder.equal(root.get("id"), customer.getId()))
                );

        Assertions.assertEquals(1, results.size());
        CustomerNameAndCityJoinWithAlias result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());
        Assertions.assertEquals(customer.getMainAddress().getCity().getId(), result.cityId());
        Assertions.assertEquals(customer.getMainAddress().getCity().getName(), result.cityName());
        Assertions.assertEquals(customer.getMainAddress().getCity().getState().getName(), result.state());
    }

    @Test
    void execute_withProjectionQuery_filterEqual() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("name", ProjectionFilterOperator.EQUAL, customer.getName())
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("name", ProjectionFilterOperator.EQUAL, UUID.randomUUID().toString())
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterBetween() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.BETWEEN, BetweenValues.of(0, customer.getId()))
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.BETWEEN, BetweenValues.of(-1, 0))
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterGreaterThen() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.GREATER_THAN, customer.getId() - 1)
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.GREATER_THAN, customer.getId())
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterGreaterThenOrEqual() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.GREATER_THAN_OR_EQUAL, customer.getId())
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.GREATER_THAN_OR_EQUAL, customer.getId() + 1)
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterIn() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.IN, List.of(customer.getId()))
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.IN, List.of(-1L, -2L))
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterLessThan() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.LESS_THAN, customer.getId() + 1)
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.LESS_THAN, customer.getId())
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterLessThanOrEqual() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.LESS_THAN_OR_EQUAL, customer.getId())
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.LESS_THAN_OR_EQUAL, customer.getId() - 1)
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterLike() {
        String value = customer.getName().substring(0, 4);

        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("name", ProjectionFilterOperator.LIKE, value+"%")
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("name", ProjectionFilterOperator.LIKE, "%"+value)
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterNotEqual() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("name", ProjectionFilterOperator.NOT_EQUAL, UUID.randomUUID().toString())
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("name", ProjectionFilterOperator.NOT_EQUAL, customer.getName())
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_filterNotIn() {
        // filter valid
        List<CustomerName> results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.NOT_IN, List.of(-1L, -2L))
                );

        Assertions.assertEquals(1, results.size());
        CustomerName result = results.get(0);
        Assertions.assertEquals(customer.getName(), result.name());

        // filter not valid
        results = processor
                .execute(
                        ProjectionQuery
                                .fromTo(Customer.class, CustomerName.class)
                                .filter("id", ProjectionFilterOperator.NOT_IN, List.of(customer.getId()))
                );

        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void execute_withProjectionQuery_order() {
        Customer customerFromGoiania = new Customer();
        customerFromGoiania.setName("Customer from Goiania");
        customerFromGoiania.setMainAddress(mainAddressGoiania);
        persist(customerFromGoiania);

        Customer customerFromAnapolis = new Customer();
        customerFromAnapolis.setName("Customer from Anapolis");
        customerFromAnapolis.setMainAddress(secondaryAddressAnapolis);
        persist(customerFromAnapolis);

        try{
            // ASC
            List<CustomerNameAndCityAttributes> results = processor
                    .execute(
                            ProjectionQuery
                                    .fromTo(Customer.class, CustomerNameAndCityAttributes.class)
                                    .filter("id", ProjectionFilterOperator.IN, List.of(customerFromGoiania.getId(), customerFromAnapolis.getId()))
                                    .order("mainAddress.city.name", OrderDirection.ASC)
                    );

            Assertions.assertEquals(2, results.size());
            CustomerNameAndCityAttributes result = results.get(0);
            Assertions.assertEquals(customerFromAnapolis.getName(), result.name());

            result = results.get(1);
            Assertions.assertEquals(customerFromGoiania.getName(), result.name());

            // DESC
            results = processor
                    .execute(
                            ProjectionQuery
                                    .fromTo(Customer.class, CustomerNameAndCityAttributes.class)
                                    .filter("id", ProjectionFilterOperator.IN, List.of(customerFromGoiania.getId(), customerFromAnapolis.getId()))
                                    .order("mainAddress.city.name", OrderDirection.DESC)
                    );

            Assertions.assertEquals(2, results.size());
            result = results.get(0);
            Assertions.assertEquals(customerFromGoiania.getName(), result.name());

            result = results.get(1);
            Assertions.assertEquals(customerFromAnapolis.getName(), result.name());

        }finally {
            remove(customerFromAnapolis);
            remove(customerFromGoiania);
        }

    }
}
