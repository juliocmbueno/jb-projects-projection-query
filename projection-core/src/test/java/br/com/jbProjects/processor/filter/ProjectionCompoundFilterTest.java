package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.joinResolver.PathResolver;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by julio.bueno on 19/01/2026.
 */
class ProjectionCompoundFilterTest {

    @Test
    public void constructor_nullOperator() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ProjectionCompoundFilter(null, List.of(new ProjectionFilter("field", "equal", "value")));
        });
        assertEquals("CompoundOperator must not be null", exception.getMessage());
    }

    @Test
    public void constructor_emptyFilters() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ProjectionCompoundFilter(CompoundOperator.AND, List.of());
        });
        assertEquals("ProjectionCompoundFilter requires at least one filter expression", exception.getMessage());
    }

    @Test
    public void constructor_nullFilters() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ProjectionCompoundFilter(CompoundOperator.OR, null);
        });
        assertEquals("ProjectionCompoundFilter requires at least one filter expression", exception.getMessage());
    }

    @Test
    public void constructor_validInputs() {
        ProjectionFilterExpression filter1 = new ProjectionFilter("age", "greaterThan", 30);
        ProjectionFilterExpression filter2 = new ProjectionFilter("name", "like", "John%");
        List<ProjectionFilterExpression> filters = List.of(filter1, filter2);

        ProjectionCompoundFilter compoundFilter = new ProjectionCompoundFilter(CompoundOperator.AND, filters);

        assertEquals(CompoundOperator.AND, compoundFilter.operator());
        assertEquals(filters, compoundFilter.filters());
    }

    @Test
    public void toPredicate(){
        // Mocks
        CriteriaBuilder cb = Mockito.mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = Mockito.mock(CriteriaQuery.class);
        Root<?> root = Mockito.mock(Root.class);
        Path<?> path = Mockito.mock(Path.class);
        Predicate predicateEquals = Mockito.mock(Predicate.class);
        Predicate predicateOr = Mockito.mock(Predicate.class);
        PathResolver pathResolver = Mockito.mock(PathResolver.class);

        // Stubbing
        Mockito.doReturn(predicateEquals).when(cb).equal(Mockito.eq(path), Mockito.anyInt());
        Mockito.doReturn(path).when(pathResolver).resolve(Mockito.eq(root), Mockito.anyString());
        Mockito.doReturn(predicateOr).when(cb).or(Mockito.any());

        // Test
        ProjectionCompoundFilter filter = new ProjectionCompoundFilter(
                CompoundOperator.OR,
                List.of(
                        new ProjectionFilter("path_1", "EQUAL", 10),
                        new ProjectionFilter("path_2", "EQUAL", 20)
                )
        );
        Predicate filterPredicate = filter.toPredicate(cb, query, root, pathResolver);
        assertEquals(predicateOr, filterPredicate);
    }

    @Test
    public void of(){
        ProjectionFilterExpression filter1 = new ProjectionFilter("field1", "equal", "value1");
        ProjectionFilterExpression filter2 = new ProjectionFilter("field2", "greaterThan", 10);

        ProjectionCompoundFilter compoundFilter = ProjectionCompoundFilter.of(
                CompoundOperator.AND,
                filter1,
                filter2
        );

        assertEquals(CompoundOperator.AND, compoundFilter.operator());
        assertEquals(2, compoundFilter.filters().size());
        assertTrue(compoundFilter.filters().contains(filter1));
        assertTrue(compoundFilter.filters().contains(filter2));
    }

    @Test
    public void and(){
        ProjectionFilterExpression filter1 = new ProjectionFilter("field1", "equal", "value1");
        ProjectionFilterExpression filter2 = new ProjectionFilter("field2", "greaterThan", 10);

        ProjectionCompoundFilter compoundFilter = ProjectionCompoundFilter.and(
                filter1,
                filter2
        );

        assertEquals(CompoundOperator.AND, compoundFilter.operator());
        assertEquals(2, compoundFilter.filters().size());
        assertTrue(compoundFilter.filters().contains(filter1));
        assertTrue(compoundFilter.filters().contains(filter2));
    }

    @Test
    public void or(){
        ProjectionFilterExpression filter1 = new ProjectionFilter("field1", "equal", "value1");
        ProjectionFilterExpression filter2 = new ProjectionFilter("field2", "greaterThan", 10);

        ProjectionCompoundFilter compoundFilter = ProjectionCompoundFilter.or(
                filter1,
                filter2
        );

        assertEquals(CompoundOperator.OR, compoundFilter.operator());
        assertEquals(2, compoundFilter.filters().size());
        assertTrue(compoundFilter.filters().contains(filter1));
        assertTrue(compoundFilter.filters().contains(filter2));
    }

    @Test
    public void toLogString(){
        ProjectionFilterExpression filter1 = new ProjectionFilter("age", "greater_than", 30);
        ProjectionFilterExpression filter2 = new ProjectionFilter("name", "like", "John%");

        ProjectionCompoundFilter compoundFilter = new ProjectionCompoundFilter(
                CompoundOperator.AND,
                List.of(filter1, filter2)
        );

        String logString = compoundFilter.toLogString();
        assertEquals("AND (age greater_than, name like)", logString);
    }
}
