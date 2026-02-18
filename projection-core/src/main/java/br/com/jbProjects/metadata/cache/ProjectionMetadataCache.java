package br.com.jbProjects.metadata.cache;

import br.com.jbProjects.metadata.factory.ProjectionMetadataFactory;
import br.com.jbProjects.metadata.model.ProjectionMetadata;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by julio.bueno on 18/02/2026.
 */
public class ProjectionMetadataCache {

    private static final ConcurrentHashMap<Class<?>, ProjectionMetadata> CACHE = new ConcurrentHashMap<>();

    public static ProjectionMetadata get(Class<?> projectionClass) {
        return CACHE.computeIfAbsent(projectionClass, ProjectionMetadataFactory::of);
    }
}
