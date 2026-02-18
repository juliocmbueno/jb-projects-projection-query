package br.com.jbProjects.metadata.factory;

import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityJoinWithAlias;
import br.com.jbProjects.metadata.model.FieldMetadata;
import br.com.jbProjects.metadata.model.JoinMetadata;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import br.com.jbProjects.processor.selectOperator.handler.DefaultSelectOperatorHandler;
import jakarta.persistence.criteria.JoinType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by julio.bueno on 18/02/2026.
 */
class ProjectionMetadataFactoryTest {

    @Test
    public void ofWithNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProjectionMetadataFactory.of(null));
    }

    @Test
    public void of() {
        ProjectionMetadata metadata = ProjectionMetadataFactory.of(CustomerNameAndCityJoinWithAlias.class);
        Assertions.assertNotNull(metadata);

        Assertions.assertEquals(CustomerNameAndCityJoinWithAlias.class, metadata.projectionClass());
        Assertions.assertEquals(Customer.class, metadata.entityClass());
        Assertions.assertEquals(2, metadata.joins().size());
        Assertions.assertEquals(4, metadata.fields().size());
    }

    @Test
    public void ofValidatedJoins() {
        ProjectionMetadata metadata = ProjectionMetadataFactory.of(CustomerNameAndCityJoinWithAlias.class);
        Assertions.assertNotNull(metadata);

        Assertions.assertEquals(2, metadata.joins().size());

        JoinMetadata joinMetadata = metadata.joins().get(0);
        Assertions.assertEquals("mainAddress", joinMetadata.path());
        Assertions.assertEquals(JoinType.INNER, joinMetadata.type());

        joinMetadata = metadata.joins().get(1);
        Assertions.assertEquals("mainAddress.city.state", joinMetadata.path());
        Assertions.assertEquals(JoinType.INNER, joinMetadata.type());
    }

    @Test
    public void ofValidatedFields() {
        ProjectionMetadata metadata = ProjectionMetadataFactory.of(CustomerNameAndCityJoinWithAlias.class);
        Assertions.assertNotNull(metadata);

        Assertions.assertEquals(4, metadata.fields().size());

        FieldMetadata fieldMetadata = metadata.fields().get(0);
        Assertions.assertEquals("name", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("name", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());

        fieldMetadata = metadata.fields().get(1);
        Assertions.assertEquals("cityId", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("mainAddress.city.id", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());

        fieldMetadata = metadata.fields().get(2);
        Assertions.assertEquals("cityName", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("mainAddress.city.name", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());

        fieldMetadata = metadata.fields().get(3);
        Assertions.assertEquals("state", fieldMetadata.projectionFieldName());
        Assertions.assertEquals("mainAddress.city.state.name", fieldMetadata.value());
        Assertions.assertEquals(DefaultSelectOperatorHandler.class, fieldMetadata.selectHandler());
    }

}
