package br.com.jbProjects.processor.joinResolver;

import br.com.jbProjects.annotations.ProjectionJoin;
import jakarta.persistence.criteria.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultPathResolver implements PathResolver {
    
    private final Map<String, Join<?,?>> joinCache = new HashMap<>();
    private final Map<String, JoinType> annotationJoins;

    public DefaultPathResolver(List<ProjectionJoin> definedJoins) {
        annotationJoins = definedJoins
                .stream()
                .collect(Collectors.toMap(
                        ProjectionJoin::path,
                        ProjectionJoin::type)
                );
    }

    @Override
    public Path<?> resolve(Root<?> root, String path) {
        String[] parts = path.split("\\.");
        From<?, ?> current = root;

        for (int i = 0; i < parts.length - 1; i++) {
            current = joinPart(current, path, parts[i]);
        }

        return current.get(parts[parts.length - 1]);
    }

    private Join<?,?> joinPart(From<?,?> root, String path, String attribute) {
        String joinKey = buildJoinKey(root, attribute);
        if (joinCache.containsKey(joinKey)) {
            return joinCache.get(joinKey);
        }

        JoinType joinType = resolveJoinType(path);

        Join<?,?> join = root.join(attribute, joinType);
        joinCache.put(joinKey, join);

        return join;
    }

    private JoinType resolveJoinType(String fullPath) {
        if(annotationJoins.containsKey(fullPath)) {
            return annotationJoins.get(fullPath);
        }

        // Verifica parte pai
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
