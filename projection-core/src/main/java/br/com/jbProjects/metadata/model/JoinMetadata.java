package br.com.jbProjects.metadata.model;

import jakarta.persistence.criteria.JoinType;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Immutable metadata container for a projection join configuration.</p>
 *
 * <p>{@code JoinMetadata} represents a single join definition declared in a
 * projection class via {@link br.com.jbProjects.annotations.ProjectionJoin}.
 * It contains the resolved path to the relationship and the type of join to
 * be used when constructing the JPA Criteria query.
 *
 * <p>Join metadata is extracted during projection metadata creation and cached,
 * allowing the query builder to efficiently construct the appropriate JOINs
 * without re-parsing annotations on every query execution.
 *
 * <p><b>Path Resolution:</b>
 * The {@code path} field contains the fully resolved path after alias substitution.
 * This ensures that any aliases defined in the projection are properly expanded
 * before the join is created.
 *
 * <p><b>Join Types:</b>
 * <ul>
 *     <li>{@code INNER}: Standard INNER JOIN (default if not specified)</li>
 *     <li>{@code LEFT}: LEFT OUTER JOIN (for optional relationships)</li>
 *     <li>{@code RIGHT}: RIGHT OUTER JOIN (rarely used)</li>
 * </ul>
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * // Given a projection join annotation:
 * @ProjectionJoin(path = "secondaryAddress", type = JoinType.LEFT)
 *
 * // The corresponding JoinMetadata would be:
 * JoinMetadata joinMeta = new JoinMetadata(
 *     "secondaryAddress",  // path
 *     JoinType.LEFT        // type
 * );
 *
 * }</pre>
 *
 * <p><b>Automatic Join Creation:</b>
 * When projection fields reference nested properties (e.g., {@code "address.city.name"}),
 * the framework automatically creates the necessary INNER JOINs even if not explicitly
 * declared. The {@code JoinMetadata} is only needed when:
 * <ul>
 *     <li>Changing the join type (e.g., to LEFT JOIN)</li>
 *     <li>Defining an alias for the join</li>
 *     <li>Explicitly controlling join creation for optimization</li>
 * </ul>
 *
 * @param path The resolved path to the relationship property
 * @param type The JPA join type to use when creating the join
 *
 * @see ProjectionMetadata
 * @see br.com.jbProjects.annotations.ProjectionJoin
 */
public record JoinMetadata(
        String path,
        JoinType type
) {
}
