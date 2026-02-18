package br.com.jbProjects.metadata.resolver;

import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityJoinWithAlias;
import br.com.jbProjects.metadata.model.FieldMetadata;
import br.com.jbProjects.metadata.model.JoinMetadata;
import br.com.jbProjects.metadata.model.ProjectionStructure;
import br.com.jbProjects.processor.selectOperator.handler.DefaultSelectOperatorHandler;
import jakarta.persistence.criteria.JoinType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by julio.bueno on 18/02/2026.
 */
class ProjectionStructureResolverTest {

    @Test
    public void resolve(){
        ProjectionStructure structure = ProjectionStructureResolver.resolve(CustomerNameAndCityJoinWithAlias.class);
        Assertions.assertNotNull(structure);
        Assertions.assertEquals(2, structure.joins().size());
        Assertions.assertEquals(4, structure.fields().size());
    }

    @Test
    public void resolveValidatedJoins(){
        ProjectionStructure structure = ProjectionStructureResolver.resolve(CustomerNameAndCityJoinWithAlias.class);
        Assertions.assertEquals(2, structure.joins().size());

        JoinMetadata joinMetadata = structure.joins().get(0);
        Assertions.assertEquals("mainAddress", joinMetadata.path());
        Assertions.assertEquals(JoinType.INNER, joinMetadata.type());

        joinMetadata = structure.joins().get(1);
        Assertions.assertEquals("mainAddress.city.state", joinMetadata.path());
        Assertions.assertEquals(JoinType.INNER, joinMetadata.type());
    }

    @Test
    public void resolveValidatedFields(){
        ProjectionStructure structure = ProjectionStructureResolver.resolve(CustomerNameAndCityJoinWithAlias.class);
        Assertions.assertEquals(4, structure.fields().size());

        FieldMetadata fieldMetadata = structure.fields().get(0);
        Assertions.assertEquals("name", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("name", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());

        fieldMetadata = structure.fields().get(1);
        Assertions.assertEquals("cityId", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("mainAddress.city.id", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());

        fieldMetadata = structure.fields().get(2);
        Assertions.assertEquals("cityName", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("mainAddress.city.name", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());

        fieldMetadata = structure.fields().get(3);
        Assertions.assertEquals("state", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("mainAddress.city.state.name", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());
    }
}
