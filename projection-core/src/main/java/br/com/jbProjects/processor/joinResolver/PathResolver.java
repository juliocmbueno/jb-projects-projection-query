package br.com.jbProjects.processor.joinResolver;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Interface for resolving property paths in JPA Criteria queries.</p>
 */
public interface PathResolver {
    Path<?> resolve(Root<?> root, String path);
}
