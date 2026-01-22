package br.com.jbProjects.processor.selectOperator;

import br.com.jbProjects.processor.selectOperator.handler.*;
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

        CountHandler countHandler = new CountHandler();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                provider.register(countHandler)
        );

        Assertions.assertEquals("Operator already registered: "+countHandler.getClass().getName(), exception.getMessage());
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
    public void get_withOperator(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();
        ProjectionSelectOperatorHandler handler = provider.get(MinHandler.class);

        Assertions.assertNotNull(handler, "Handler for MIN should not be null");
        Assertions.assertInstanceOf(MinHandler.class, handler, "Handler should be an instance of MinHandler");
    }

    @Test
    public void availableOperators(){
        ProjectionSelectOperatorProvider provider = ProjectionSelectOperatorProvider.getInstance();
        var operators = provider.availableOperators();

        Assertions.assertNotNull(operators, "Available operators should not be null");
        Assertions.assertTrue(operators.contains(CountHandler.class.getName()));
        Assertions.assertTrue(operators.contains(MinHandler.class.getName()));
        Assertions.assertTrue(operators.contains(MaxHandler.class.getName()));
        Assertions.assertTrue(operators.contains(SumHandler.class.getName()));
    }

    @Test
    public void operators(){
        List<ProjectionSelectOperatorHandler> operators = ProjectionSelectOperatorProvider.getInstance().operators();
        Assertions.assertEquals(7, operators.size());
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(DefaultSelectOperatorHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(CountHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(MinHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(MaxHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(SumHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(AbsHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(AvgHandler.class)));
    }

}
