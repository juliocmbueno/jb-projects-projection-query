package br.com.jbProjects.processor.query;

import br.com.jbProjects.config.helper.BaseJpaTest;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerAutoCompleteClass;
import br.com.jbProjects.config.testModel.customer.projections.CustomerCountByAge;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 25/11/2025.
 */
class ProjectionSelectInfoTest extends BaseJpaTest {

    @Test
    public void create(){
        ProjectionQuery<Customer, CustomerAutoCompleteClass> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerAutoCompleteClass.class);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Customer> root = criteriaQuery.from(projectionQuery.fromClass());

        ProjectionSelectInfo selectInfo = new ProjectionSelectInfo(projectionQuery, criteriaBuilder, root);
        assertEquals(0, selectInfo.getGroupByFields().length);

        Selection<?>[] selections = selectInfo.getSelections();
        assertEquals(3, selections.length);

        Selection<?> selection = selections[0];
        assertEquals("id", selection.getAlias());

        selection = selections[1];
        assertEquals("name", selection.getAlias());

        selection = selections[2];
        assertEquals("customerEmail", selection.getAlias());
    }

    @Test
    public void create_withGroupByField(){
        ProjectionQuery<Customer, CustomerCountByAge> projectionQuery = ProjectionQuery.fromTo(Customer.class, CustomerCountByAge.class);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<Customer> root = criteriaQuery.from(projectionQuery.fromClass());

        ProjectionSelectInfo selectInfo = new ProjectionSelectInfo(projectionQuery, criteriaBuilder, root);

        // select
        assertEquals(2, selectInfo.getSelections().length);
        Selection<?> selection = selectInfo.getSelections()[0];
        assertEquals("quantity", selection.getAlias());

        selection = selectInfo.getSelections()[1];
        assertEquals("age", selection.getAlias());

        // group by
        assertEquals(1, selectInfo.getGroupByFields().length);

        selection = selectInfo.getGroupByFields()[0];
        assertEquals("age", selection.getAlias());
    }

}
