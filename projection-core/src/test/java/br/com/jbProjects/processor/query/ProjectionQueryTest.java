package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerName;
import br.com.jbProjects.processor.filter.ProjectionFilter;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
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
}
