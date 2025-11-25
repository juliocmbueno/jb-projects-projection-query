package br.com.jbProjects.processor.operatorHandler;

import br.com.jbProjects.annotations.ProjectionField;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class SumHandlerTest {

    private final SumHandler handler = new SumHandler();

    @Test
    public void supports(){
        ProjectionField fieldSupported = Mockito.mock(ProjectionField.class);
        Mockito.doReturn(true).when(fieldSupported).sum();
        Assertions.assertTrue(handler.supports(fieldSupported));

        ProjectionField fieldNotSupported = Mockito.mock(ProjectionField.class);
        Assertions.assertFalse(handler.supports(fieldNotSupported));
    }

    @Test
    public void apply(){
        Root<?> root = Mockito.mock(Root.class);
        Path<Number> pathAge = Mockito.mock(Path.class);

        Mockito.doReturn(pathAge).when(root).get("price");

        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Expression<?> expression = Mockito.mock(Expression.class);
        Mockito.doReturn(expression).when(cb).sum(pathAge);

        Expression<?> result = handler.apply(cb, root, "price");
        Assertions.assertEquals(expression, result);
    }

}
