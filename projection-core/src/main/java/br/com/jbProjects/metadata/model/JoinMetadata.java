package br.com.jbProjects.metadata.model;

import jakarta.persistence.criteria.JoinType;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public record JoinMetadata(
        String path,
        JoinType type
) {
}
