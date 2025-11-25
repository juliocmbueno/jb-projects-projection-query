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
@SuppressWarnings("unchecked")
class MinHandlerTest {

    private final MinHandler handler = new MinHandler();

    @Test
    public void supports(){
        ProjectionField fieldSupported = Mockito.mock(ProjectionField.class);
        Mockito.doReturn(true).when(fieldSupported).min();
        Assertions.assertTrue(handler.supports(fieldSupported));

        ProjectionField fieldNotSupported = Mockito.mock(ProjectionField.class);
        Assertions.assertFalse(handler.supports(fieldNotSupported));
    }

    @Test
    public void apply(){
        Root<?> root = Mockito.mock(Root.class);
        Path<Number> pathAge = Mockito.mock(Path.class);

        Mockito.doReturn(pathAge).when(root).get("age");

        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Expression<?> expression = Mockito.mock(Expression.class);
        Mockito.doReturn(expression).when(cb).min(pathAge);

        Expression<?> result = handler.apply(cb, root, "age");
        Assertions.assertEquals(expression, result);
    }

}
