package br.com.jbProjects.processor.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Created by julio.bueno on 19/01/2026.
 */
class CompoundOperatorTest {

    @Test
    public void testAndOperator(){
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        CompoundOperator operator = CompoundOperator.AND;
        operator.toPredicate(cb, List.of());
        Mockito.verify(cb, Mockito.times(1)).and(Mockito.any());
    }

    @Test
    public void testOrOperator() {
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        CompoundOperator operator = CompoundOperator.OR;
        operator.toPredicate(cb, List.of());
        Mockito.verify(cb, Mockito.times(1)).or(Mockito.any());
    }

}
