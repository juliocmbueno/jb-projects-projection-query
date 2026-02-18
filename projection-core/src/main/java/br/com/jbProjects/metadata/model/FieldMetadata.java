package br.com.jbProjects.metadata.model;

import br.com.jbProjects.processor.selectOperator.handler.ProjectionSelectOperatorHandler;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Immutable metadata container for a single projection field.</p>
 *
 * <p>{@code FieldMetadata} encapsulates all information needed to select and
 * process a field in a projection query. This includes the field's name in
 * the projection class, the resolved path to the entity property, and the
 * handler responsible for applying any transformations or aggregations.
 *
 * <p>Field metadata is extracted once during projection metadata creation
 * and cached for the lifetime of the application, avoiding repeated reflection
 * overhead when building queries.
 *
 * <p><b>Path Resolution:</b>
 * The {@code value} field contains the fully resolved path to the entity property,
 * with any aliases already substituted. For example:
 * <ul>
 *     <li>Simple field: {@code "name"}</li>
 *     <li>Nested property: {@code "address.city.name"}</li>
 *     <li>Aliased property: {@code "mainAddress.city.name"} (after alias resolution)</li>
 * </ul>
 *
 * <p><b>Select Handler:</b>
 * The handler determines how the field is selected in the SQL query:
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * // Given a projection field:
 * @ProjectionField(value = "address.city.name", selectHandler = DefaultSelectOperatorHandler.class)
 * String cityName;
 *
 * // The corresponding FieldMetadata would be:
 * FieldMetadata fieldMeta = new FieldMetadata(
 *     "cityName",                        // projectionFieldName
 *     "address.city.name",               // value (resolved path)
 *     DefaultSelectOperatorHandler.class // selectHandler
 * );
 *
 * // Used to build the query:
 * Expression<?> expr = handler.apply(pathResolver, cb, root, fieldMeta.value());
 * Selection<?> selection = expr.alias(fieldMeta.projectionFieldName());
 * }</pre>
 *
 * @param projectionFieldName The name of the field in the projection class
 * @param value The resolved path to the entity property (with aliases substituted)
 * @param selectHandler The handler class responsible for generating the selection expression
 *
 * @see ProjectionMetadata
 * @see br.com.jbProjects.annotations.ProjectionField
 * @see ProjectionSelectOperatorHandler
 */
public record FieldMetadata(
        String projectionFieldName,
        String value,
        Class<? extends ProjectionSelectOperatorHandler> selectHandler
) {
}
