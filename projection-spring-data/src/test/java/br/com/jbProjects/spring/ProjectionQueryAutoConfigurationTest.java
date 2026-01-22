package br.com.jbProjects.spring;

import br.com.jbProjects.processor.ProjectionProcessor;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.filter.ProjectionFilterOperatorProvider;
import br.com.jbProjects.processor.selectOperator.ProjectionSelectOperatorProvider;
import br.com.jbProjects.spring.configTest.BaseSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by julio.bueno on 27/11/2025.
 */
public class ProjectionQueryAutoConfigurationTest extends BaseSpringBootTest {

    @Autowired
    private ProjectionProcessor projectionProcessor;

    @Autowired
    private ProjectionFilterOperatorProvider filterOperatorProvider;

    @Autowired
    private ProjectionSelectOperatorProvider selectOperatorProvider;

    @Test
    public void verify(){
        Assertions.assertNotNull(projectionProcessor);
        Assertions.assertNotNull(filterOperatorProvider);
        Assertions.assertNotNull(selectOperatorProvider);
    }

    @Test
    public void filterOperatorProvider(){
        Assertions.assertEquals(ProjectionFilterOperator.values().length, filterOperatorProvider.availableOperators().size());
    }

    @Test
    public void selectOperatorProvider(){
        Assertions.assertEquals(7, selectOperatorProvider.availableOperators().size());
    }
}
