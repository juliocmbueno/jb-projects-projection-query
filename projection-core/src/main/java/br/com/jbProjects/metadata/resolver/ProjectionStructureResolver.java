package br.com.jbProjects.metadata.resolver;

import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.metadata.model.FieldMetadata;
import br.com.jbProjects.metadata.model.JoinMetadata;
import br.com.jbProjects.metadata.model.ProjectionStructure;
import br.com.jbProjects.util.ProjectionUtils;

import java.util.List;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Resolver responsible for extracting and processing projection structure.</p>
 *
 * <p>The {@code ProjectionStructureResolver} is the core component responsible for
 * analyzing a projection class and extracting all metadata about its fields and joins.
 * It coordinates the resolution process by delegating alias handling to
 * {@link ProjectionAliasResolver} while handling the actual extraction and
 * transformation of projection annotations.
 *
 * <p><b>Resolution Process:</b>
 * <ol>
 *     <li>Extract all {@link ProjectionJoin} annotations from the projection class</li>
 *     <li>Create a {@link ProjectionAliasResolver} to handle path transformations</li>
 *     <li>Resolve each join with alias substitution applied</li>
 *     <li>Resolve each field with alias substitution and handler extraction</li>
 *     <li>Package everything into a {@link ProjectionStructure}</li>
 * </ol>
 *
 * <p><b>Alias Resolution:</b>
 * One of the key responsibilities is ensuring that all aliases defined in
 * {@link ProjectionJoin} annotations are properly resolved in field paths.
 * This allows developers to use shorter, more readable aliases in their
 * field definitions while maintaining the correct navigation paths.
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * @Projection(
 *     of = Customer.class,
 *     joins = {
 *         @ProjectionJoin(path = "address", alias = "addr"),
 *         @ProjectionJoin(path = "addr.city", alias = "mainCity")
 *     }
 * )
 * public record CustomerDTO(
 *     @ProjectionField Long id,
 *     @ProjectionField("mainCity.state.name") String stateName
 * ) { }
 *
 * // Resolution process:
 * // 1. Extract joins: ["address" → alias "addr", "addr.city" → alias "mainCity"]
 * // 2. Create AliasResolver with these mappings
 * // 3. Resolve field "mainCity.state.name":
 * //    - First pass: "mainCity" → "addr.city"
 * //    - Second pass: "addr.city.state.name" → "address.city.state.name"
 * //    - Final: "address.city.state.name"
 * }</pre>
 *
 * <p>This resolver is stateless and thread-safe, using only static methods
 * for resolution operations.
 *
 * @see ProjectionStructure
 * @see ProjectionAliasResolver
 * @see br.com.jbProjects.metadata.factory.ProjectionMetadataFactory
 */
public class ProjectionStructureResolver {

    /**
     * Private constructor to prevent instantiation, as this class is intended
     */
    private ProjectionStructureResolver() {}

    /**
     * Resolves the complete structure of a projection class.
     *
     * <p>This method orchestrates the entire structure resolution process,
     * creating an alias resolver and using it to process both joins and fields
     * with proper path transformations.
     *
     * <p>The resolution is performed in a specific order to ensure aliases
     * defined in joins are available when processing field paths:
     * <ol>
     *     <li>Extract declared joins</li>
     *     <li>Build alias resolver from joins</li>
     *     <li>Resolve join paths with aliases</li>
     *     <li>Resolve field paths with aliases</li>
     * </ol>
     *
     * @param projectionClass The projection class to analyze
     * @return ProjectionStructure containing resolved joins and fields
     */
    public static ProjectionStructure resolve(Class<?> projectionClass) {
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(projectionClass);

        ProjectionAliasResolver aliasResolver = ProjectionAliasResolver.of(declaredJoins);
        List<JoinMetadata> joins = resolveJoins(declaredJoins, aliasResolver);
        List<FieldMetadata> fields = resolveFields(projectionClass, aliasResolver);

        return new ProjectionStructure(
                joins,
                fields,
                aliasResolver.getAliasMap()
        );
    }

    /**
     * Resolves all join declarations with alias substitution.
     *
     * <p>Transforms each {@link ProjectionJoin} annotation into a {@link JoinMetadata}
     * instance, applying alias resolution to the join paths. This ensures that
     * joins can reference previously defined aliases.
     *
     * @param declaredJoins List of join annotations from the projection class
     * @param aliasResolver Resolver for handling alias substitution
     * @return List of resolved join metadata
     */
    private static List<JoinMetadata> resolveJoins(List<ProjectionJoin> declaredJoins, ProjectionAliasResolver aliasResolver) {
        return declaredJoins
                .stream()
                .map(join -> new JoinMetadata(
                        aliasResolver.resolve(join.path()),
                        join.type()
                ))
                .toList();
    }

    /**
     * Resolves all field declarations with alias substitution and handler extraction.
     *
     * <p>For each field annotated with {@link ProjectionField}, this method:
     * <ul>
     *     <li>Extracts the field name from the projection class</li>
     *     <li>Resolves the field path with alias substitution</li>
     *     <li>Extracts the select handler class for the field</li>
     * </ul>
     *
     * <p>The resolved path represents the complete navigation path to the entity
     * property, with all aliases expanded to their full paths.
     *
     * @param projectionClass The projection class containing the fields
     * @param aliasResolver Resolver for handling alias substitution
     * @return List of resolved field metadata
     */
    private static List<FieldMetadata> resolveFields(Class<?> projectionClass, ProjectionAliasResolver aliasResolver) {
        return ProjectionUtils.getProjectionFieldsAnnotations(projectionClass)
                .stream()
                .map(field -> {
                    ProjectionField projectionField = field.getAnnotation(ProjectionField.class);

                    return new FieldMetadata(
                            field.getName(),
                            aliasResolver.resolve(ProjectionUtils.getFieldColumnName(field)),
                            projectionField.selectHandler()
                    );
                })
                .toList();
    }
}
