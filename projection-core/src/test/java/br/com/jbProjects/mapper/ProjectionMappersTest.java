package br.com.jbProjects.mapper;

import br.com.jbProjects.config.testModel.customer.projections.CustomerAutoCompleteClass;
import br.com.jbProjects.config.testModel.customer.projections.CustomerAutoCompleteRecord;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class ProjectionMappersTest {

    @Test
    void tupleToObject_withClass() {
        Tuple tuple = Mockito.mock(Tuple.class);
        Mockito.doReturn(1L).when(tuple).get("id");
        Mockito.doReturn("Customer Name").when(tuple).get("name");
        Mockito.doReturn("customer@mail.com").when(tuple).get("customerEmail");

        CustomerAutoCompleteClass result = ProjectionMappers.tupleToObject(tuple, CustomerAutoCompleteClass.class);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("Customer Name", result.getName());
        Assertions.assertEquals("customer@mail.com", result.getCustomerEmail());
        Assertions.assertNull(result.getNonProjectedField());
    }

    @Test
    void tupleToObject_withClass_exception() {
        Assertions.assertThrowsExactly(RuntimeException.class, () ->
                ProjectionMappers.tupleToObject(null, CustomerAutoCompleteClass.class));
    }

    @Test
    void tupleToObject_withRecord() {
        Tuple tuple = Mockito.mock(Tuple.class);
        Mockito.doReturn(1L).when(tuple).get("id");
        Mockito.doReturn("Customer Name").when(tuple).get("name");
        Mockito.doReturn("customer@mail.com").when(tuple).get("customerEmail");

        CustomerAutoCompleteRecord result = ProjectionMappers.tupleToObject(tuple, CustomerAutoCompleteRecord.class);
        Assertions.assertEquals(1L, result.id());
        Assertions.assertEquals("Customer Name", result.name());
        Assertions.assertEquals("customer@mail.com", result.customerEmail());
        Assertions.assertNull(result.notProjectedField());
    }

    @Test
    void tupleToObject_withRecord_exception() {
        Assertions.assertThrowsExactly(RuntimeException.class, () ->
                ProjectionMappers.tupleToObject(null, CustomerAutoCompleteRecord.class));
    }

    @Test
    void tupleToObject_withRecord_IllegalArgumentException() {
        Tuple tuple = Mockito.mock(Tuple.class);
        Mockito.doReturn(1L).when(tuple).get("id");
        Mockito.doReturn(1L).when(tuple).get("name");
        Mockito.doReturn("customer@mail.com").when(tuple).get("customerEmail");

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> ProjectionMappers.tupleToObject(tuple, CustomerAutoCompleteRecord.class));

        String expectedMessage = """
                Error mapping query result to projection record: CustomerAutoCompleteRecord
                
                Expected constructor types:
                  [Long, String, String, String]
                
                Query result types:
                  [Long, Long, String, null]
                """;

        Assertions.assertEquals(expectedMessage, exception.getCause().getMessage());
    }
}
