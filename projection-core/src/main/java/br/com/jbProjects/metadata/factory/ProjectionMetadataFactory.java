package br.com.jbProjects.metadata.factory;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import br.com.jbProjects.metadata.model.ProjectionStructure;
import br.com.jbProjects.metadata.resolver.ProjectionStructureResolver;
import br.com.jbProjects.validations.ProjectionValidations;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Factory responsible for creating {@link ProjectionMetadata} instances from projection classes.</p>
 *
 * <p>The {@code ProjectionMetadataFactory} serves as the entry point for extracting
 * and processing projection metadata through reflection. It coordinates the entire
 * metadata extraction process, delegating specific tasks to specialized resolvers
 * while ensuring proper validation and composition of the final metadata object.
 *
 * <p><b>Metadata Extraction Process:</b>
 * <ol>
 *     <li>Validates that the projection class is properly annotated</li>
 *     <li>Extracts the {@link Projection} annotation</li>
 *     <li>Resolves the projection structure (fields and joins)</li>
 *     <li>Composes all information into an immutable {@link ProjectionMetadata} object</li>
 * </ol>
 *
 * <p>This factory is stateless and all methods are static, making it safe for
 * concurrent use without synchronization. The actual caching of metadata is
 * handled by {@link br.com.jbProjects.metadata.cache.ProjectionMetadataCache}.
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * // Direct creation (typically used internally by the cache)
 * ProjectionMetadata metadata = ProjectionMetadataFactory.of(CustomerDTO.class);
 *
 * // Access projection details
 * Class<?> entityClass = metadata.entityClass();
 * List<FieldMetadata> fields = metadata.fields();
 * List<JoinMetadata> joins = metadata.joins();
 * }</pre>
 *
 * @see ProjectionMetadata
 * @see ProjectionStructureResolver
 * @see br.com.jbProjects.metadata.cache.ProjectionMetadataCache
 */
public class ProjectionMetadataFactory {

    /**
     * Creates a {@link ProjectionMetadata} instance for the specified projection class.
     *
     * <p>This method performs the complete metadata extraction process:
     * <ul>
     *     <li>Validates the projection class structure</li>
     *     <li>Extracts entity class from {@link Projection} annotation</li>
     *     <li>Resolves all projection fields with their configurations</li>
     *     <li>Resolves all declared joins with their types</li>
     *     <li>Processes alias mappings for nested property access</li>
     * </ul>
     *
     * <p><b>Note:</b> This method uses reflection and is relatively expensive
     * (~200µs). It should be called through {@link br.com.jbProjects.metadata.cache.ProjectionMetadataCache}
     * to benefit from caching.
     *
     * @param projectionClass The projection class to extract metadata from
     * @return ProjectionMetadata containing all extracted information
     * @throws IllegalArgumentException if the projection class is not properly annotated or does not meet the required structure
     */
    public static ProjectionMetadata of(Class<?> projectionClass) {
        ProjectionValidations.validateProjectionClass(projectionClass);

        Projection projection = projectionClass.getAnnotation(Projection.class);

        ProjectionStructure structure = ProjectionStructureResolver.resolve(projectionClass);

        return new ProjectionMetadata(
                projectionClass,
                projection.of(),
                structure.joins(),
                structure.fields()
        );
    }
}
