package br.com.jbProjects.processor.joinResolver;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public interface PathResolver {
    Path<?> resolve(Root<?> root, String path);
}
