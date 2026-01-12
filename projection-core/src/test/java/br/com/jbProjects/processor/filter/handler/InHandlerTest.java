package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 25/11/2025.
 */
@SuppressWarnings("unchecked")
class InHandlerTest {

    private final InHandler handler = new InHandler();

    @Test
    public void toPredicate(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        CriteriaBuilder.In<Object> in = Mockito.mock(CriteriaBuilder.In.class);
        Mockito.doReturn(in).when(criteriaBuilder).in(path);

        Predicate predicate = handler.toPredicate(criteriaBuilder, path, List.of(1,2,3,4));

        assertEquals(in, predicate);

        Mockito.verify(in, Mockito.times(1)).value(1);
        Mockito.verify(in, Mockito.times(1)).value(2);
        Mockito.verify(in, Mockito.times(1)).value(3);
        Mockito.verify(in, Mockito.times(1)).value(4);
    }

    @Test
    public void toPredicate_value_isNot_Iterable(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        try {
            handler.toPredicate(criteriaBuilder, path, "notIterable");
        } catch (IllegalArgumentException e){
            assertEquals("Value for IN operator must be an instance of Iterable.", e.getMessage());
        }
    }
}
