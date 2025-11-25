package br.com.jbProjects.util;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import jakarta.persistence.criteria.JoinType;
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

    @Test
    public void getFieldColumnName(){
        ProjectionUtilsTestField testeClass = new ProjectionUtilsTestField();

        Field field = ReflectionTestUtils.findField(testeClass.getClass(), "name");
        String fieldColumnName = ProjectionUtils.getFieldColumnName(field);
        assertEquals("name", fieldColumnName);

        field = ReflectionTestUtils.findField(testeClass.getClass(), "description");
        fieldColumnName = ProjectionUtils.getFieldColumnName(field);
        assertEquals("extraInfo", fieldColumnName);
    }

    @Test
    public void getDeclaredJoins(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(ProjectionUtilsTestField.class);
        assertEquals(1, declaredJoins.size());

        ProjectionJoin first = declaredJoins.getFirst();
        assertEquals("mainAddress", first.path());
        assertEquals(JoinType.LEFT, first.type());
    }

    @Test
    public void getDeclaredJoins_withOutProjectionJoin(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(this.getClass());
        assertTrue(declaredJoins.isEmpty());
    }
}

@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "mainAddress", type = JoinType.LEFT)
        }
)
class ProjectionUtilsTestField{

    @ProjectionField
    private String name;

    @ProjectionField("extraInfo")
    private String description;

    private int quantity;

}
