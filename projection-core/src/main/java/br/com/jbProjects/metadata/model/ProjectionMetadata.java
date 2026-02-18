package br.com.jbProjects.metadata.model;

import java.util.List;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Immutable container for projection metadata.</p>
 *
 * <p>{@code ProjectionMetadata} is the central data structure that holds all
 * information about a projection class extracted during metadata processing.
 * It serves as a cached snapshot of the projection's structure, eliminating
 * the need for repeated reflection operations.
 *
 * <p>Being a {@code record}, this class is:
 * <ul>
 *     <li><b>Immutable:</b> Safe for concurrent access and caching</li>
 *     <li><b>Value-based:</b> Equality based on content, not identity</li>
 *     <li><b>Compact:</b> Minimal memory footprint (~400-600 bytes)</li>
 * </ul>
 *
 * <p>This metadata is typically accessed through
 * {@link br.com.jbProjects.metadata.cache.ProjectionMetadataCache} and used
 * internally by {@link br.com.jbProjects.processor.query.ProjectionSelectInfo}
 * to construct JPA Criteria queries efficiently.
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerDTO.class);
 *
 * // Access projection information
 * Class<?> projectionClass = metadata.projectionClass(); // CustomerDTO.class
 * Class<?> entityClass = metadata.entityClass();         // Customer.class
 *
 * // Iterate over fields
 * for (FieldMetadata field : metadata.fields()) {
 *     System.out.println("Field: " + field.projectionFieldName());
 *     System.out.println("Path: " + field.value());
 *     System.out.println("Handler: " + field.selectHandler().getSimpleName());
 * }
 *
 * // Check configured joins
 * for (JoinMetadata join : metadata.joins()) {
 *     System.out.println("Join: " + join.path() + " (" + join.type() + ")");
 * }
 * }</pre>
 *
 * @param projectionClass The projection class (DTO) that defines the structure
 * @param entityClass The JPA entity class this projection maps from
 * @param joins List of configured joins for this projection
 * @param fields List of fields to be selected in the projection
 *
 * @see FieldMetadata
 * @see JoinMetadata
 * @see ProjectionStructure
 */
public record ProjectionMetadata(
        Class<?> projectionClass,
        Class<?> entityClass,
        List<JoinMetadata> joins,
        List<FieldMetadata> fields
) {
}
