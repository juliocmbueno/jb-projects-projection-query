package br.com.jbProjects.processor.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 03/02/2026.
 */
class ProjectionFiltersTest {

    @Test
    public void of_withEnumOperator(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.of("field", ProjectionFilterOperator.EQUAL, "value");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.EQUAL.name(), filter.operator());
        assertEquals("value", filter.value());
    }

    @Test
    public void of_withStringOperator(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.of("field", "EQUAL", "value");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.EQUAL.name(), filter.operator());
        assertEquals("value", filter.value());
    }

    @Test
    public void and(){
        ProjectionCompoundFilter filter = (ProjectionCompoundFilter) ProjectionFilters.and(
                ProjectionFilters.of("field1", ProjectionFilterOperator.EQUAL, "value1"),
                ProjectionFilters.of("field2", ProjectionFilterOperator.GREATER_THAN, 10)
        );
        assertEquals(CompoundOperator.AND, filter.operator());
        Assertions.assertEquals(2, filter.filters().size());
    }

    @Test
    public void or() {
        ProjectionCompoundFilter filter = (ProjectionCompoundFilter) ProjectionFilters.or(
                ProjectionFilters.of("field1", ProjectionFilterOperator.EQUAL, "value1"),
                ProjectionFilters.of("field2", ProjectionFilterOperator.GREATER_THAN, 10)
        );
        assertEquals(CompoundOperator.OR, filter.operator());
        Assertions.assertEquals(2, filter.filters().size());
    }

    @Test
    public void equal(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.equal("field", "value");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.EQUAL.name(), filter.operator());
        assertEquals("value", filter.value());
    }

    @Test
    public void notEqual(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.notEqual("field", "value");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.NOT_EQUAL.name(), filter.operator());
        assertEquals("value", filter.value());
    }

    @Test
    public void greaterThan(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.greaterThan("field", 10);
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.GREATER_THAN.name(), filter.operator());
        assertEquals(10, filter.value());
    }

    @Test
    public void greaterThanOrEqual(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.greaterThanOrEqual("field", 10);
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.GREATER_THAN_OR_EQUAL.name(), filter.operator());
        assertEquals(10, filter.value());
    }

    @Test
    public void lessThan(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.lessThan("field", 10);
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.LESS_THAN.name(), filter.operator());
        assertEquals(10, filter.value());
    }

    @Test
    public void lessThanOrEqual() {
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.lessThanOrEqual("field", 10);
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.LESS_THAN_OR_EQUAL.name(), filter.operator());
        assertEquals(10, filter.value());
    }

    @Test
    public void like(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.like("field", "value%");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.LIKE.name(), filter.operator());
        assertEquals("value%", filter.value());
    }

    @Test
    public void in_values(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.in("field", "value1", "value2", "value3");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.IN.name(), filter.operator());

        List<?> values = (List<?>) filter.value();
        Assertions.assertArrayEquals(new Object[]{"value1", "value2", "value3"}, values.toArray());
    }

    @Test
    public void in_collection(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.in("field", List.of("value1", "value2", "value3"));
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.IN.name(), filter.operator());

        List<?> values = (List<?>) filter.value();
        Assertions.assertArrayEquals(new Object[]{"value1", "value2", "value3"}, values.toArray());
    }

    @Test
    public void notIn_values(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.notIn("field", "value1", "value2", "value3");
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.NOT_IN.name(), filter.operator());

        List<?> values = (List<?>) filter.value();
        Assertions.assertArrayEquals(new Object[]{"value1", "value2", "value3"}, values.toArray());
    }

    @Test
    public void notIn_collection(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.notIn("field", List.of("value1", "value2", "value3"));
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.NOT_IN.name(), filter.operator());

        List<?> values = (List<?>) filter.value();
        Assertions.assertArrayEquals(new Object[]{"value1", "value2", "value3"}, values.toArray());
    }

    @Test
    public void between(){
        ProjectionFilter filter = (ProjectionFilter) ProjectionFilters.between("field", 5, 15);
        assertEquals("field", filter.path());
        assertEquals(ProjectionFilterOperator.BETWEEN.name(), filter.operator());

        BetweenValues betweenValues = (BetweenValues) filter.value();
        assertEquals(5, betweenValues.start());
        assertEquals(15, betweenValues.end());
    }
}
