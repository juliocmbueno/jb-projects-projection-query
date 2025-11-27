package br.com.jbProjects.standalone;

import br.com.jbProjects.BaseApplicationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by julio.bueno on 27/11/2025.
 */
class ProjectionsStandaloneExampleTest extends BaseApplicationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void fetchCustomerBasicDataClass(){
        ProjectionsStandaloneExample examples = new ProjectionsStandaloneExample(entityManager);
        examples.fetchCustomerBasicDataClass();
    }
}
