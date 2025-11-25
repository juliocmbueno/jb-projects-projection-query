package br.com.jbProjects.processor.joinResolver;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Interface for resolving property paths in JPA Criteria queries.</p>
 */
public interface PathResolver {

    /**
     * Resolves the given property path starting from the provided root entity.
     *
     * @param root The root entity from which to start the path resolution.
     * @param path The property path to resolve.
     * @return The resolved Path object.
     */
    Path<?> resolve(Root<?> root, String path);
}
