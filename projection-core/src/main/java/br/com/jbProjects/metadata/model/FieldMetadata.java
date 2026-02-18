package br.com.jbProjects.metadata.model;

import br.com.jbProjects.processor.selectOperator.handler.ProjectionSelectOperatorHandler;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public record FieldMetadata(
        String projectionFieldName,
        String value,
        Class<? extends ProjectionSelectOperatorHandler> selectHandler
) {
}
