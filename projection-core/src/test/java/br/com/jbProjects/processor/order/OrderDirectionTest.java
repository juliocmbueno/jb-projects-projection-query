package br.com.jbProjects.processor.order;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class OrderDirectionTest {

    @Test
    public void ASC(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        Order order = Mockito.mock(Order.class);
        Mockito.doReturn(order).when(criteriaBuilder).asc(path);

        Order expected = OrderDirection.ASC.toOrder(criteriaBuilder, path);
        assertEquals(order, expected);
    }

    @Test
    public void DESC(){
        CriteriaBuilder criteriaBuilder = Mockito.mock(CriteriaBuilder.class);
        Path<?> path = Mockito.mock(Path.class);

        Order order = Mockito.mock(Order.class);
        Mockito.doReturn(order).when(criteriaBuilder).desc(path);

        Order expected = OrderDirection.DESC.toOrder(criteriaBuilder, path);
        assertEquals(order, expected);
    }
}
