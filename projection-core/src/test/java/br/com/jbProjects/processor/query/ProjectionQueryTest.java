package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerName;
import br.com.jbProjects.processor.filter.*;
import br.com.jbProjects.processor.order.OrderDirection;
import br.com.jbProjects.processor.order.ProjectionOrder;
import br.com.jbProjects.util.ProjectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by julio.bueno on 25/11/2025.
 */
@SuppressWarnings("unchecked")
class ProjectionQueryTest {

    @Test
    public void fromTo(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerName.class);
        assertNotNull(projectionQuery);
        assertEquals(Customer.class, projectionQuery.fromClass());
        assertEquals(CustomerName.class, projectionQuery.toClass());
        assertFalse(projectionQuery.isDistinct());
        assertFalse(projectionQuery.hasPaging());

        List<ProjectionSpecification<Customer>> specifications = (List<ProjectionSpecification<Customer>>) ReflectionTestUtils.getField(projectionQuery, "specifications");
        assertTrue(specifications.isEmpty());
    }

    @Test
    public void paging(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerName.class);
        projectionQuery.paging(10, 20);
        assertTrue(projectionQuery.hasPaging());

        ProjectionPaging paging = projectionQuery.getPaging();
        assertNotNull(paging);
        assertEquals(10, paging.first());
        assertEquals(20, paging.size());
    }

    @Test
    public void specification(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerName.class);
        ProjectionSpecification<Customer> specification = (criteriaBuilder, query, root,pathResolver) ->
                criteriaBuilder.equal(pathResolver.resolve(root, "id"), 1);

        projectionQuery.specification(specification);

        List<ProjectionSpecification<Customer>> specifications = (List<ProjectionSpecification<Customer>>) ReflectionTestUtils.getField(projectionQuery, "specifications");
        assertEquals(1, specifications.size());
        assertTrue(specifications.contains(specification));
    }

    @Test
    public void distinct(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerName.class);
        assertFalse(projectionQuery.isDistinct());

        projectionQuery.distinct();

        assertTrue(projectionQuery.isDistinct());
    }

    @Test
    public void getDeclaredJoins(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerName.class);

        List<ProjectionJoin> declaredJoins = projectionQuery.getDeclaredJoins();
        List<ProjectionJoin> expected = ProjectionUtils.getDeclaredJoins(Customer.class);
        assertEquals(expected, declaredJoins);
    }

    @Test
    public void order(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .order("name", OrderDirection.ASC);

        List<ProjectionOrder> orders = (List<ProjectionOrder>) ReflectionTestUtils.getField(projectionQuery, "orders");
        assertEquals(1, orders.size());
        ProjectionOrder order = orders.get(0);
        assertEquals("name", order.path());
        assertEquals(OrderDirection.ASC, order.direction());
    }

    @Test
    public void filter_enumOperator(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .filter("name", ProjectionFilterOperator.EQUAL, "John Doe");

        List<ProjectionFilter> filters = (List<ProjectionFilter>) ReflectionTestUtils.getField(projectionQuery, "filters");
        assertEquals(1, filters.size());
        ProjectionFilter filter = filters.get(0);
        assertEquals("name", filter.path());
        assertEquals("EQUAL", filter.operator());
        assertEquals("John Doe", filter.value());
    }

    @Test
    public void filter_stringOperator(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .filter("name", "EQUAL", "John Doe");

        List<ProjectionFilter> filters = (List<ProjectionFilter>) ReflectionTestUtils.getField(projectionQuery, "filters");
        assertEquals(1, filters.size());
        ProjectionFilter filter = filters.get(0);
        assertEquals("name", filter.path());
        assertEquals("EQUAL", filter.operator());
        assertEquals("John Doe", filter.value());
    }

    @Test
    public void filter_compound_with_operator(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .filter(
                        CompoundOperator.AND,
                        ProjectionFilter.of("name", "EQUAL", "John Doe"),
                        ProjectionFilter.of("age", "GREATER_THAN", 18)
                );

        List<Object> filters = (List<Object>) ReflectionTestUtils.getField(projectionQuery, "filters");
        assertEquals(1, filters.size());
        Object filterObj = filters.get(0);
        assertInstanceOf(ProjectionCompoundFilter.class, filterObj);
        ProjectionCompoundFilter compoundFilter = (ProjectionCompoundFilter) filterObj;
        assertEquals(CompoundOperator.AND, compoundFilter.operator());
        assertEquals(2, compoundFilter.filters().size());
    }

    @Test
    public void filter_withProjectionFilterExpression(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .filter(
                        ProjectionCompoundFilter.and(
                                ProjectionFilter.of("name", "EQUAL", "John Doe"),
                                ProjectionFilter.of("age", "GREATER_THAN", 18)
                        )
                );

        List<Object> filters = (List<Object>) ReflectionTestUtils.getField(projectionQuery, "filters");
        assertEquals(1, filters.size());
        Object filterObj = filters.get(0);
        assertInstanceOf(ProjectionCompoundFilter.class, filterObj);
        ProjectionCompoundFilter compoundFilter = (ProjectionCompoundFilter) filterObj;
        assertEquals(CompoundOperator.AND, compoundFilter.operator());
        assertEquals(2, compoundFilter.filters().size());
    }

    @Test
    public void filter_withMultipleFilters(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .filter(
                        ProjectionFilters.equal("name", "John Doe"),
                        ProjectionFilters.greaterThan("age", 18)
                );

        List<Object> filters = (List<Object>) ReflectionTestUtils.getField(projectionQuery, "filters");
        assertEquals(2, filters.size());

        ProjectionFilter filter1 = (ProjectionFilter) filters.get(0);
        assertEquals("name", filter1.path());
        assertEquals("EQUAL", filter1.operator());
        assertEquals("John Doe", filter1.value());

        ProjectionFilter filter2 = (ProjectionFilter) filters.get(1);
        assertEquals("age", filter2.path());
        assertEquals("GREATER_THAN", filter2.operator());
        assertEquals(18, filter2.value());
    }

    @Test
    public void copy(){
        ProjectionQuery<Customer, CustomerName> projectionQuery = ProjectionQuery
                .fromTo(Customer.class, CustomerName.class)
                .distinct()
                .paging(5, 10)
                .order("name", OrderDirection.DESC)
                .filter("age", ProjectionFilterOperator.GREATER_THAN, 21)
                .specification((criteriaBuilder, query, root, pathResolver) ->
                        criteriaBuilder.equal(pathResolver.resolve(root, "id"), 1));

        ProjectionQuery<Customer, CustomerName> copy = projectionQuery.copy();

        assertEquals(projectionQuery.fromClass(), copy.fromClass());
        assertEquals(projectionQuery.toClass(), copy.toClass());
        assertEquals(projectionQuery.isDistinct(), copy.isDistinct());
        assertEquals(projectionQuery.hasPaging(), copy.hasPaging());
        if (projectionQuery.hasPaging()) {
            assertEquals(projectionQuery.getPaging().first(), copy.getPaging().first());
            assertEquals(projectionQuery.getPaging().size(), copy.getPaging().size());
        }

        List<ProjectionOrder> originalOrders = (List<ProjectionOrder>) ReflectionTestUtils.getField(projectionQuery, "orders");
        List<ProjectionOrder> copiedOrders = (List<ProjectionOrder>) ReflectionTestUtils.getField(copy, "orders");
        assertEquals(originalOrders, copiedOrders);

        List<Object> originalFilters = (List<Object>) ReflectionTestUtils.getField(projectionQuery, "filters");
        List<Object> copiedFilters = (List<Object>) ReflectionTestUtils.getField(copy, "filters");
        assertEquals(originalFilters, copiedFilters);

        List<ProjectionSpecification<Customer>> originalSpecifications = (List<ProjectionSpecification<Customer>>) ReflectionTestUtils.getField(projectionQuery, "specifications");
        List<ProjectionSpecification<Customer>> copiedSpecifications = (List<ProjectionSpecification<Customer>>) ReflectionTestUtils.getField(copy, "specifications");
        assertEquals(originalSpecifications, copiedSpecifications);
    }
}
