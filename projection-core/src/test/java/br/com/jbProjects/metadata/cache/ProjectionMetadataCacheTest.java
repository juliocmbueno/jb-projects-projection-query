package br.com.jbProjects.metadata.cache;

import br.com.jbProjects.config.testModel.customer.projections.CustomerName;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Created by julio.bueno on 18/02/2026.
 */
class ProjectionMetadataCacheTest {

    @AfterEach
    public void setup() {
        ProjectionMetadataCache.clearForTesting();
    }

    @Test
    public void testCacheReturnsSameInstance() {
        ProjectionMetadata meta1 = ProjectionMetadataCache.get(CustomerName.class);
        ProjectionMetadata meta2 = ProjectionMetadataCache.get(CustomerName.class);

        assertSame(meta1, meta2, "Expected the same instance from cache");
    }

}
