package br.com.jbProjects.processor.joinResolver;

import br.com.jbProjects.annotations.ProjectionJoin;
import jakarta.persistence.criteria.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 23/11/2025.
 * <p>Default implementation of {@code PathResolver} responsible for resolving
 * nested paths and performing the joins required to navigate attributes
 * during Criteria API projections.</p>
 *
 * <p>This resolver analyzes the projection metadata (for example via
 * {@link ProjectionJoin}) and ensures that every requested attribute can be
 * reached from the root entity, automatically creating the necessary
 * {@link jakarta.persistence.criteria.Join} structures when required.</p>
 *
 * <p>The class also caches previously resolved joins in order to avoid
 * duplicate navigation during the same query execution, improving performance
 * and preventing Hibernate from generating redundant SQL joins.</p>
 *
 * <p>This component is considered an internal utility used by the projection
 * engine and is not expected to be used directly by application code in most
 * scenarios.</p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Resolve property paths like {@code "address.city"} into valid
 *   Criteria API {@code Path} references.</li>
 *   <li>Create and manage joins transparently when walking nested attributes.</li>
 *   <li>Cache resolved joins per root to avoid unnecessary re-processing.</li>
 * </ul>
 *
 * <h3>Typical Usage:</h3>
 * Used internally by the projection processor when evaluating
 * {@code ProjectionField}, {@code ProjectionFilter}, or Specification-based
 * criteria.
 */
public class DefaultPathResolver implements PathResolver {
    
    private final Map<String, Join<?,?>> joinCache = new HashMap<>();
    private final Map<String, JoinType> annotationJoins;
    private final Map<String, String> pathsByAlias;

    /**
     * Constructs a DefaultPathResolver with the provided projection join definitions.
     *
     * @param definedJoins List of ProjectionJoin annotations defining join paths and types.
     */
    public DefaultPathResolver(List<ProjectionJoin> definedJoins) {
        annotationJoins = createAnnotationJoins(definedJoins);
        pathsByAlias = createPathByAlias(definedJoins);
    }

    private Map<String, JoinType> createAnnotationJoins(List<ProjectionJoin> definedJoins){
        return definedJoins
                .stream()
                .collect(Collectors.toMap(
                        ProjectionJoin::path,
                        ProjectionJoin::type)
                );
    }

    private Map<String, String> createPathByAlias(List<ProjectionJoin> definedJoins){
        return definedJoins
                .stream()
                .filter(join -> !join.alias().isBlank())
                .collect(Collectors.toMap(
                        ProjectionJoin::alias,
                        ProjectionJoin::path)
                );
    }

    /**
     * <p>Resolves the given path against the provided root, creating necessary joins.</p>
     * <p>It's possible to use nested paths to access fields in related entities.</p>
     * <p> ex: "name" or "address.city.name" </p>
     * @param root The root entity from which to resolve the path.
     * @param path The property path to resolve, potentially nested.
     * @return The resolved Path object representing the property.
     */
    @Override
    public Path<?> resolve(Root<?> root, String path) {
        String resolved = resolveAlias(path);

        String[] parts = resolved.split("\\.");
        From<?, ?> current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            current = joinPart(current, resolved, parts[i]);
        }

        return current.get(parts[parts.length - 1]);
    }

    private String resolveAlias(String fullPath){
        while (true) {
            String resolved = resolveSingleAlias(fullPath);
            if (resolved.equals(fullPath)) {
                return resolved;
            }
            fullPath = resolved;
        }
    }

    private String resolveSingleAlias(String fullPath) {
        String[] parts = fullPath.split("\\.");
        String first = parts[0];

        if (!pathsByAlias.containsKey(first)) {
            return fullPath;
        }

        String mapped = pathsByAlias.get(first);

        if (parts.length == 1) {
            return mapped;
        }

        return mapped + "." + String.join(".", Arrays.copyOfRange(parts, 1, parts.length));
    }

    private Join<?,?> joinPart(From<?,?> root, String fullPath, String attribute) {
        String joinKey = buildJoinKey(root, attribute);
        if (joinCache.containsKey(joinKey)) {
            return joinCache.get(joinKey);
        }

        JoinType joinType = resolveJoinType(fullPath);

        Join<?,?> join = root.join(attribute, joinType);
        joinCache.put(joinKey, join);

        return join;
    }

    private JoinType resolveJoinType(String fullPath) {
        if(annotationJoins.containsKey(fullPath)) {
            return annotationJoins.get(fullPath);
        }

        int dot = fullPath.lastIndexOf(".");
        while(dot > 0) {
            fullPath = fullPath.substring(0, dot);
            if(annotationJoins.containsKey(fullPath)) {
                return annotationJoins.get(fullPath);
            }
            dot = fullPath.lastIndexOf(".");
        }

        return JoinType.INNER;
    }

    private String buildJoinKey(From<?,?> root, String attribute) {
        return root.toString() + "." + attribute;
    }
}
