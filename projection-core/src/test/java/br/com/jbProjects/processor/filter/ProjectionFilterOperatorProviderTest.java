package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.filter.handler.EqualHandler;
import br.com.jbProjects.processor.filter.handler.LessThanHandler;
import br.com.jbProjects.processor.filter.handler.ProjectionFilterOperatorHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class ProjectionFilterOperatorProviderTest {

    @Test
    public void getInstance(){
        ProjectionFilterOperatorProvider instance1 = ProjectionFilterOperatorProvider.getInstance();
        ProjectionFilterOperatorProvider instance2 = ProjectionFilterOperatorProvider.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "Instances should be the same (singleton)");
    }

    @Test
    public void register_duplicateOperator_throwsException(){
        ProjectionFilterOperatorProvider provider = ProjectionFilterOperatorProvider.getInstance();
        ProjectionFilterOperatorHandler dummyHandler = (cb, path, value) -> null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                provider.register("EQUAL", dummyHandler)
        );

        assertEquals("Operator already registered: EQUAL", exception.getMessage());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void register_with_null_ProjectionFilterOperatorHandler(){
        ProjectionFilterOperatorProvider provider = ProjectionFilterOperatorProvider.getInstance();
        ProjectionFilterOperatorHandler dummyHandler = (cb, path, value) -> null;

        assertThrows(NullPointerException.class, () -> provider.register("EQUAL", null));
    }

    @Test
    public void get_unknownOperator_throwsException(){
        ProjectionFilterOperatorProvider provider = ProjectionFilterOperatorProvider.getInstance();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                provider.get("UNKNOWN_OPERATOR")
        );

        assertTrue(exception.getMessage().contains("Operator not found: UNKNOWN_OPERATOR"));
        assertTrue(exception.getMessage().contains("Available operators:"));
    }

    @Test
    public void get_withString(){
        ProjectionFilterOperatorProvider provider = ProjectionFilterOperatorProvider.getInstance();
        ProjectionFilterOperatorHandler handler = provider.get("EQUAL");

        assertNotNull(handler, "Handler for EQUAL should not be null");
        assertInstanceOf(EqualHandler.class, handler, "Handler should be an instance of EqualHandler");
    }

    @Test
    public void get_withOperator(){
        ProjectionFilterOperatorProvider provider = ProjectionFilterOperatorProvider.getInstance();
        ProjectionFilterOperatorHandler handler = provider.get(ProjectionFilterOperator.LESS_THAN);

        assertNotNull(handler, "Handler for LESS_THAN should not be null");
        assertInstanceOf(LessThanHandler.class, handler, "Handler should be an instance of LessThanHandler");
    }

    @Test
    public void availableOperators(){
        ProjectionFilterOperatorProvider provider = ProjectionFilterOperatorProvider.getInstance();
        var operators = provider.availableOperators();

        assertNotNull(operators, "Available operators should not be null");
        assertTrue(operators.contains("EQUAL"), "Available operators should contain EQUAL");
        assertTrue(operators.contains("NOT_EQUAL"), "Available operators should contain NOT_EQUAL");
        assertTrue(operators.contains("GREATER_THAN"), "Available operators should contain GREATER_THAN");
        assertTrue(operators.contains("LESS_THAN"), "Available operators should contain LESS_THAN");
        assertTrue(operators.contains("GREATER_THAN_OR_EQUAL"), "Available operators should contain GREATER_THAN_OR_EQUAL");
        assertTrue(operators.contains("LESS_THAN_OR_EQUAL"), "Available operators should contain LESS_THAN_OR_EQUAL");
        assertTrue(operators.contains("LIKE"), "Available operators should contain LIKE");
        assertTrue(operators.contains("IN"), "Available operators should contain IN");
        assertTrue(operators.contains("NOT_IN"), "Available operators should contain NOT_IN");
        assertTrue(operators.contains("BETWEEN"), "Available operators should contain BETWEEN");
    }

}
