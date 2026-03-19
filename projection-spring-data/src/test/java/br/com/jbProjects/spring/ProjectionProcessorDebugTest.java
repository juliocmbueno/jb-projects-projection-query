package br.com.jbProjects.spring;

import br.com.jbProjects.processor.ProjectionProcessorDebug;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.query.ProjectionQuery;
import br.com.jbProjects.spring.configTest.BaseSpringBootTest;
import br.com.jbProjects.spring.configTest.model.Customer;
import br.com.jbProjects.spring.configTest.model.CustomerProjectionData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by julio.bueno on 18/03/2026.
 */
public class ProjectionProcessorDebugTest extends BaseSpringBootTest {

    @Autowired
    private ProjectionProcessorDebug projectionProcessorDebug;

    @Test
    public void previewSQL(){
        String sql = projectionProcessorDebug.previewSQL(
                ProjectionQuery
                        .fromTo(Customer.class, CustomerProjectionData.class)
                        .filter("name", ProjectionFilterOperator.EQUAL, "John Doe")
        );

        Assertions.assertNotNull(sql);
        Assertions.assertFalse(sql.contains("criteria"));
    }

}
