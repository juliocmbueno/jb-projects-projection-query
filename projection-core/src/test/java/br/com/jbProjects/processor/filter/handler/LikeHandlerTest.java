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
class LikeHandlerTest {

    private final LikeHandler handler = new LikeHandler();

    @Test
    public void toPredicate(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        Predicate expected = Mockito.mock(Predicate.class);
        Mockito
                .doReturn(expected)
                .when(criteriaBuilder)
                .like((Path<String>) path, "abc");

        Predicate predicate = handler.toPredicate(criteriaBuilder, path, "abc");

        assertEquals(expected, predicate);
    }

}
