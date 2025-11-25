package br.com.jbProjects.processor.selectOperator;

import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.processor.selectOperator.handler.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class ProjectionSelectOperatorProviderTest {

    @Test
    public void getInstance(){
        ProjectionSelectOperatorProvider instance1 = ProjectionSelectOperatorProvider.getInstance();
        ProjectionSelectOperatorProvider instance2 = ProjectionSelectOperatorProvider.getInstance();

        Assertions.assertNotNull(instance1);
        Assertions.assertSame(instance1, instance2, "Instances should be the same (singleton)");
    }

    @Test
    public void register_duplicateOperator_throwsException(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();
        ProjectionSelectOperatorHandler dummyHandler = new ProjectionSelectOperatorHandler() {
            @Override
            public boolean supports(ProjectionField annotation) {
                return false;
            }

            @Override
            public Expression<?> apply(CriteriaBuilder cb, Root<?> root, String fieldName) {
                return null;
            }
        };

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                provider.register("COUNT", dummyHandler)
        );

        Assertions.assertEquals("Operator already registered: COUNT", exception.getMessage());
    }

    @Test
    public void get_unknownOperator_throwsException(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                provider.get("UNKNOWN_OPERATOR")
        );

        Assertions.assertTrue(exception.getMessage().contains("Operator not found: UNKNOWN_OPERATOR"));
        Assertions.assertTrue(exception.getMessage().contains("Available operators:"));
    }

    @Test
    public void get_withString(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();
        ProjectionSelectOperatorHandler handler = provider.get("COUNT");

        Assertions.assertNotNull(handler, "Handler for COUNT should not be null");
        Assertions.assertInstanceOf(CountHandler.class, handler, "Handler should be an instance of CountHandler");
    }

    @Test
    public void get_withOperator(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();
        ProjectionSelectOperatorHandler handler = provider.get(ProjectionSelectOperator.MIN);

        Assertions.assertNotNull(handler, "Handler for MIN should not be null");
        Assertions.assertInstanceOf(MinHandler.class, handler, "Handler should be an instance of MinHandler");
    }

    @Test
    public void availableOperators(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();
        var operators = provider.availableOperators();

        Assertions.assertNotNull(operators, "Available operators should not be null");
        Assertions.assertTrue(operators.contains("COUNT"));
        Assertions.assertTrue(operators.contains("MIN"));
        Assertions.assertTrue(operators.contains("MAX"));
        Assertions.assertTrue(operators.contains("SUM"));
    }

    @Test
    public void operators(){
        List<ProjectionSelectOperatorHandler> operators = ProjectionSelectOperatorProvider.getInstance().operators();
        Assertions.assertEquals(4, operators.size());
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(CountHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(MinHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(MaxHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(SumHandler.class)));
    }

}
