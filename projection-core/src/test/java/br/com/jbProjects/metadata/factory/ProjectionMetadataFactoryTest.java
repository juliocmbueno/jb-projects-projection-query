package br.com.jbProjects.metadata.factory;

import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityJoinWithAlias;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import org.junit.jupiter.api.Test;

/**
 * Created by julio.bueno on 18/02/2026.
 */
class ProjectionMetadataFactoryTest {

    @Test
    public void test() {
        ProjectionMetadata metadata = ProjectionMetadataFactory.of(CustomerNameAndCityJoinWithAlias.class);
        System.out.println(metadata);
    }

}
