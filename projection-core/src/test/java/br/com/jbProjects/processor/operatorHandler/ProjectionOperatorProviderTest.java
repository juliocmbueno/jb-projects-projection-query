package br.com.jbProjects.processor.operatorHandler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class ProjectionOperatorProviderTest {

    @Test
    public void operators(){
        List<ProjectionOperatorHandler> operators = ProjectionOperatorProvider.operators();
        Assertions.assertEquals(4, operators.size());
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(CountHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(MinHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(MaxHandler.class)));
        Assertions.assertTrue(operators.stream().anyMatch(item -> item.getClass().equals(SumHandler.class)));
    }

}
