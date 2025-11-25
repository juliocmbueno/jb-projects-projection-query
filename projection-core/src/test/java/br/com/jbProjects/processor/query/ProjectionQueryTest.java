package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerName;
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
        ProjectionSpecification<Customer> specification = (criteriaBuilder, query, root) -> criteriaBuilder.equal(root.get("id"), 1);

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
}
