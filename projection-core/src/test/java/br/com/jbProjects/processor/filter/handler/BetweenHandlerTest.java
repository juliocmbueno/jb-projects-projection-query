package br.com.jbProjects.processor.filter.handler;

import br.com.jbProjects.processor.filter.BetweenValues;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by julio.bueno on 25/11/2025.
 */
@SuppressWarnings({"rawtypes", "CastCanBeRemovedNarrowingVariableType", "unchecked"})
class BetweenHandlerTest {

    private final BetweenHandler handler = new BetweenHandler();

    @Test
    public void toPredicate(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);
        BetweenValues betweenValues = BetweenValues.of(1, 2);

        Predicate expected = Mockito.mock(Predicate.class);
        Mockito
                .doReturn(expected)
                .when(criteriaBuilder).between(
                        (Path<Comparable>) path,
                        (Comparable) betweenValues.start(),
                        (Comparable) betweenValues.end()
                );

        Predicate predicate = handler.toPredicate(criteriaBuilder, path, betweenValues);

        assertEquals(expected, predicate);
    }

    @Test
    public void toPredicate_withOutBetweenValues(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        try{
            handler.toPredicate(criteriaBuilder, path, 1);
            fail("Value must be BetweenValues");

        }catch (Exception e){
            assertEquals("Between operator requires BetweenValues. Provided: 1", e.getMessage());
        }
    }
}
