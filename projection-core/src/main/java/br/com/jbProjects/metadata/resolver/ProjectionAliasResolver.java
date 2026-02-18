package br.com.jbProjects.metadata.resolver;

import br.com.jbProjects.annotations.ProjectionJoin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public class ProjectionAliasResolver {

    private final Map<String, String> aliasMap;

    private ProjectionAliasResolver(List<ProjectionJoin> declaredJoins){
        this.aliasMap = declaredJoins
                .stream()
                .filter(join -> !join.alias().isBlank())
                .collect(Collectors.toMap(
                        ProjectionJoin::alias,
                        ProjectionJoin::path)
                );
    }

    public static ProjectionAliasResolver of(List<ProjectionJoin> declaredJoins) {
        return new ProjectionAliasResolver(declaredJoins);
    }

    public String resolve(String fullPath){
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

        if (!aliasMap.containsKey(first)) {
            return fullPath;
        }

        String mapped = aliasMap.get(first);

        if (parts.length == 1) {
            return mapped;
        }

        return mapped + "." + String.join(".", Arrays.copyOfRange(parts, 1, parts.length));
    }
}
