package br.com.jbProjects.processor.joinResolver;

import br.com.jbProjects.annotations.ProjectionJoin;
import jakarta.persistence.criteria.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultPathResolver implements PathResolver {
    
    private final Map<String, Join<?,?>> joinCache = new HashMap<>();
    private final Map<String, JoinType> annotationJoins;
    private final Map<String, String> pathsByAlias;

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
