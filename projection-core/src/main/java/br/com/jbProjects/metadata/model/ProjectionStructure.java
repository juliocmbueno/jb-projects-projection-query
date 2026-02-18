package br.com.jbProjects.metadata.model;

import java.util.List;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Immutable container for the resolved structure of a projection.</p>
 *
 * <p>{@code ProjectionStructure} is an intermediate data structure used during
 * metadata extraction to group together the resolved joins and fields of a
 * projection. It serves as a bridge between the raw reflection data and the
 * final {@link ProjectionMetadata} object.
 *
 * <p>This record is primarily used internally by
 * {@link br.com.jbProjects.metadata.resolver.ProjectionStructureResolver}
 * to organize extracted metadata before it is composed into the complete
 * projection metadata.
 *
 * <p><b>Note:</b> This record is part of the internal metadata extraction pipeline
 * and is typically not accessed directly by library users. The complete metadata
 * is accessed through {@link ProjectionMetadata}.
 *
 * @param joins List of resolved join metadata with aliases substituted
 * @param fields List of resolved field metadata with aliases substituted
 *
 * @see ProjectionMetadata
 * @see br.com.jbProjects.metadata.resolver.ProjectionStructureResolver
 */
public record ProjectionStructure(
        List<JoinMetadata> joins,
        List<FieldMetadata> fields
) {
}
