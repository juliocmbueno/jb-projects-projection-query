package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 19/01/2026.
 */
class ProjectionFilterTest {

    @Test
    public void toPredicate(){
        // Mocks
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = Mockito.mock(CriteriaQuery.class);
        Root<?> root = Mockito.mock(Root.class);
        Path<?> path = Mockito.mock(Path.class);
        Predicate predicate = Mockito.mock(Predicate.class);
        PathResolver pathResolver = Mockito.mock(PathResolver.class);

        // Stubbing
        Mockito.doReturn(predicate).when(cb).equal(path, 10);
        Mockito.doReturn(path).when(pathResolver).resolve(root, "pathString");

        // Test
        ProjectionFilter filter = new ProjectionFilter("pathString", "EQUAL", 10);
        Predicate filterPredicate = filter.toPredicate(cb, query, root, pathResolver);
        assertEquals(filterPredicate, predicate);
    }

    @Test
    public void of_withEnumOperator(){
        ProjectionFilter filter = ProjectionFilter.of("field", ProjectionFilterOperator.EQUAL, 5);
        assertEquals("field", filter.path());
        assertEquals("EQUAL", filter.operator());
        assertEquals(5, filter.value());
    }

    @Test
    public void of_withStringOperator(){
        ProjectionFilter filter = ProjectionFilter.of("field", "LESS_THAN", 20);
        assertEquals("field", filter.path());
        assertEquals("LESS_THAN", filter.operator());
        assertEquals(20, filter.value());
    }

    @Test
    public void toLogString() {
        ProjectionFilter filter = new ProjectionFilter("age", "GREATER_THAN", 30);
        String logString = filter.toLogString();
        assertEquals("age GREATER_THAN", logString);
    }
}
