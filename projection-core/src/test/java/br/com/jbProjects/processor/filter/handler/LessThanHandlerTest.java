package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class LessThanHandlerTest {

    private final LessThanHandler handler = new LessThanHandler();

    @Test
    public void toPredicate(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        Predicate expected = Mockito.mock(Predicate.class);
        Mockito
                .doReturn(expected)
                .when(criteriaBuilder)
                .lt((Path<? extends Number>) path, 1);

        Predicate predicate = handler.toPredicate(criteriaBuilder, path, 1);

        assertEquals(expected, predicate);
    }

}
