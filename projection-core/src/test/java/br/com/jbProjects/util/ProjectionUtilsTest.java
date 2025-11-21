package br.com.jbProjects.util;

import br.com.jbProjects.annotations.ProjectionField;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by julio.bueno on 21/11/2025.
 */
class ProjectionUtilsTest {

    @Test
    void getProjectionFieldsAnnotations() {
        List<Field> field = ProjectionUtils.getProjectionFieldsAnnotations(ProjectionUtilsTestField.class);
        assertNotNull(field);
        assertEquals(2, field.size());
        assertEquals("name", field.get(0).getName());
        assertEquals("description", field.get(1).getName());
        assertFalse(field.stream().anyMatch(f -> f.getName().equals("quantity")));
    }
}

class ProjectionUtilsTestField{

    @ProjectionField
    private String name;

    @ProjectionField
    private String description;

    private int quantity;

}
