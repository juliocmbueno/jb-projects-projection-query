package br.com.jbProjects.metadata.resolver;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.validations.ProjectionValidations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 18/02/2026.
 * <p>Resolver for handling alias substitution in projection paths.</p>
 *
 * <p>The {@code ProjectionAliasResolver} is responsible for transforming aliased
 * property paths into their full, expanded forms. This enables developers to use
 * shorter, more readable aliases in their projection definitions while maintaining
 * the correct navigation paths to entity properties.
 *
 * <p><b>Recursive Resolution:</b>
 * The resolver supports chained aliases, where one alias can reference another.
 * It recursively resolves all aliases until the full path is obtained, ensuring
 * that complex alias chains are properly expanded.
 *
 * <p><b>Example Scenarios:</b>
 *
 * <p><b>1. Simple Alias:</b>
 * <pre>{@code
 * @ProjectionJoin(path = "address", alias = "addr")
 * @ProjectionField("addr.city.name") String cityName;
 *
 * // Resolution: "addr.city.name" → "address.city.name"
 * }</pre>
 *
 * <p><b>2. Chained Aliases:</b>
 * <pre>{@code
 * @ProjectionJoin(path = "address", alias = "addr")
 * @ProjectionJoin(path = "addr.city", alias = "mainCity")
 * @ProjectionField("mainCity.state.name") String stateName;
 *
 * // Resolution process:
 * // 1st pass: "mainCity.state.name" → "addr.city.state.name"
 * // 2nd pass: "addr.city.state.name" → "address.city.state.name"
 * // Result: "address.city.state.name"
 * }</pre>
 *
 * <p><b>3. Multiple Aliases in Same Path:</b>
 * <pre>{@code
 * @ProjectionJoin(path = "customer", alias = "c")
 * @ProjectionJoin(path = "c.address", alias = "a")
 * @ProjectionField("c.name") String customerName;
 * @ProjectionField("a.street") String street;
 *
 * // "c.name" → "customer.name"
 * // "a.street" → "customer.address.street"
 * }</pre>
 *
 * <p><b>Thread Safety:</b>
 * Instances of this resolver are immutable after construction. The alias map
 * is built once during creation and cannot be modified, making it safe for
 * concurrent use across multiple threads.
 *
 * <p><b>Performance:</b>
 * Resolution is performed during metadata extraction (at application startup),
 * not during query execution. This ensures zero runtime overhead from alias
 * resolution.
 *
 * @see ProjectionStructureResolver
 * @see br.com.jbProjects.annotations.ProjectionJoin
 */
public class ProjectionAliasResolver {

    /**
     * Immutable map storing alias-to-path mappings.
     * Key: alias name, Value: actual path
     */
    private final Map<String, String> aliasMap;

    /**
     * Private constructor to enforce factory method usage.
     *
     * <p>Directly accepts a pre-built alias map, allowing for flexible
     * construction in cases where the map is already available or needs to
     * be constructed differently.
     *
     * @param aliasMap Map of aliases to their corresponding paths
     */
    private ProjectionAliasResolver(Map<String, String> aliasMap) {
        ProjectionValidations.validadeAlias(aliasMap);
        this.aliasMap = aliasMap;
    }

    /**
     * Private constructor to enforce factory method usage.
     *
     * <p>Builds the alias map by extracting all non-empty aliases from the
     * provided join declarations.
     *
     * @param declaredJoins List of join declarations containing aliases
     */
    private ProjectionAliasResolver(List<ProjectionJoin> declaredJoins){
        ProjectionValidations.validateAliases(declaredJoins);
        this.aliasMap = declaredJoins
                .stream()
                .filter(join -> !join.alias().isBlank())
                .collect(Collectors.toMap(
                        ProjectionJoin::alias,
                        ProjectionJoin::path)
                );
    }

    /**
     * Factory method for creating a new ProjectionAliasResolver.
     *
     * <p>This method constructs an immutable resolver instance with all
     * aliases extracted from the provided join declarations.
     *
     * @param declaredJoins List of projection joins to extract aliases from
     * @return A new ProjectionAliasResolver instance
     */
    public static ProjectionAliasResolver of(List<ProjectionJoin> declaredJoins) {
        return new ProjectionAliasResolver(declaredJoins);
    }

    /**
     * Factory method for creating a new ProjectionAliasResolver with a pre-built alias map.
     *
     * <p>This method allows for direct construction of the resolver when an alias map
     * is already available, providing flexibility in how the resolver is created.
     *
     * @param aliasMap Map of aliases to their corresponding paths
     * @return A new ProjectionAliasResolver instance
     */
    public static ProjectionAliasResolver of(Map<String, String> aliasMap) {
        if(aliasMap == null) {
            throw new IllegalArgumentException("Alias map cannot be null");
        }

        return new ProjectionAliasResolver(aliasMap);
    }

    /**
     * Resolves a property path by recursively substituting all aliases.
     *
     * <p>This method applies alias substitution iteratively until no more
     * substitutions can be made (fixpoint). This handles chained aliases
     * where one alias references another.
     *
     * <p><b>Algorithm:</b>
     * <ol>
     *     <li>Apply single alias substitution</li>
     *     <li>If path changed, repeat from step 1</li>
     *     <li>If path unchanged, return (no more aliases to resolve)</li>
     * </ol>
     *
     * <p><b>Example with chained aliases:</b>
     * <pre>{@code
     * // Given: {"addr" → "address", "mainCity" → "addr.city"}
     * // Input: "mainCity.state.name"
     *
     * // Iteration 1:
     * // "mainCity.state.name" → "addr.city.state.name"
     *
     * // Iteration 2:
     * // "addr.city.state.name" → "address.city.state.name"
     *
     * // Iteration 3:
     * // No change → return "address.city.state.name"
     * }</pre>
     *
     * @param fullPath The path potentially containing aliases
     * @return The fully resolved path with all aliases substituted
     */
    public String resolve(String fullPath){
        while (true) {
            String resolved = resolveSingleAlias(fullPath);
            if (resolved.equals(fullPath)) {
                return resolved;
            }
            fullPath = resolved;
        }
    }

    /**
     * Performs a single pass of alias resolution on a path.
     *
     * <p>This method checks if the first segment of the path is an alias
     * and substitutes it with the corresponding full path. If the first
     * segment is not an alias, the path is returned unchanged.
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * // Given alias map: {"addr" → "address", "city" → "address.city"}
     *
     * resolveSingleAlias("addr.street")
     * // → "address.street"
     *
     * resolveSingleAlias("address.city.name")
     * // → "address.city.name" (no alias in first segment)
     *
     * resolveSingleAlias("city")
     * // → "address.city"
     *
     * resolveSingleAlias("unknownAlias.field")
     * // → "unknownAlias.field" (alias not found, return as-is)
     * }</pre>
     *
     * @param fullPath The path to resolve
     * @return The path with the first alias substituted, or original if no alias found
     */
    private String resolveSingleAlias(String fullPath) {
        String[] parts = fullPath.split("\\.");
        String first = parts[0];

        if (!aliasMap.containsKey(first)) {
            return fullPath;
        }

        String mapped = aliasMap.get(first);

        if (parts.length == 1) {
            return mapped;
        }

        return mapped + "." + String.join(".", Arrays.copyOfRange(parts, 1, parts.length));
    }

    /**
     * Retrieves an unmodifiable copy of the alias map.
     *
     * <p>This method returns a defensive copy of the internal alias map to
     * prevent external modification. The returned map is immutable, ensuring
     * thread safety and encapsulation.
     *
     * @return An unmodifiable Map containing alias-to-path mappings
     */
    public Map<String, String> getAliasMap() {
        return Map.copyOf(aliasMap);
    }
}
