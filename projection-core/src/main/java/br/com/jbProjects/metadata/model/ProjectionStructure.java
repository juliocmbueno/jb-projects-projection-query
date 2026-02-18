package br.com.jbProjects.metadata.model;

import java.util.List;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public record ProjectionStructure(
        List<JoinMetadata> joins,
        List<FieldMetadata> fields
) {
}
