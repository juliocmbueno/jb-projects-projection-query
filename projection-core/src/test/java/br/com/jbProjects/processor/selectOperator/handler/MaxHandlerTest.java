package br.com.jbProjects.processor.selectOperator.handler;

import br.com.jbProjects.processor.joinResolver.PathResolver;
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
class MaxHandlerTest {

    private final MaxHandler handler = new MaxHandler();

    @Test
    public void aggregate(){
        Assertions.assertTrue(handler.aggregate());
    }

    @Test
    public void apply(){
        Root<?> root = Mockito.mock(Root.class);
        Path<Number> pathAge = Mockito.mock(Path.class);

        PathResolver pathResolver = Mockito.mock(PathResolver.class);
        Mockito.doReturn(pathAge).when(pathResolver).resolve(root, "age");

        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        Expression<?> expression = Mockito.mock(Expression.class);
        Mockito.doReturn(expression).when(cb).max(pathAge);

        Expression<?> result = handler.apply(pathResolver, cb, root, "age");
        Assertions.assertEquals(expression, result);
    }

}
