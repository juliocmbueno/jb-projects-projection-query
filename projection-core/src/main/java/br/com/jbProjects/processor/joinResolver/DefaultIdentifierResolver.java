package br.com.jbProjects.processor.joinResolver;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by julio.bueno on 05/01/2026.
 * <p>Default implementation of {@code IdentifierResolver} responsible for
 * determining whether a given attribute represents the identifier of a
 * target entity.</p>
 *
 * <p>This resolver combines a convention-based approach with lightweight
 * reflection to identify entity identifiers without relying on JPA
 * {@code EntityManager} or Metamodel APIs.</p>
 *
 * <p>The attribute name {@code "id"} is treated as an implicit identifier
 * by convention. For other attribute names, the resolver inspects the entity
 * class hierarchy and detects identifier mappings based on
 * {@link jakarta.persistence.Id} and {@link jakarta.persistence.EmbeddedId}
 * annotations declared on fields or accessor methods.</p>
 *
 * <p>Resolved identifiers are cached per entity type to avoid repeated
 * reflection overhead during query construction, ensuring that this
 * resolution process remains efficient even when invoked multiple times
 * within the same query.</p>
 *
 * <p>This component is primarily used by the projection path resolution
 * mechanism to optimize join creation, allowing identifier attributes of
 * related entities to be accessed directly without requiring unnecessary
 * joins.</p>
 *
 * <b>Responsibilities:</b>
 * <ul>
 *   <li>Determine whether a given attribute corresponds to an entity
 *   identifier.</li>
 *   <li>Support identifier resolution via convention ({@code "id"}) and
 *   explicit JPA identifier annotations.</li>
 *   <li>Inspect fields and accessor methods, including inherited mappings
 *   from mapped superclasses.</li>
 *   <li>Cache resolved identifier attributes per entity type for
 *   performance optimization.</li>
 * </ul>
 *
 * <b>Typical Usage:</b>
 * Used internally by {@code DefaultPathResolver} to decide whether a
 * join operation can be safely skipped when navigating projection paths
 * that reference identifier attributes of related entities.
 */
public class DefaultIdentifierResolver implements IdentifierResolver {

    private final Map<Class<?>, Set<String>> cache = new ConcurrentHashMap<>();

    /**
     * Constructs a DefaultIdentifierResolver.
     */
    public DefaultIdentifierResolver(){}

    /**
     * Determines whether the given attribute represents an identifier of the
     * specified entity type.
     *
     * <p>The attribute name {@code "id"} is always treated as an identifier by
     * convention. For other attribute names, this method resolves identifier
     * mappings using reflection-based inspection of the entity class.</p>
     *
     * <p>Resolved identifier attributes are cached per entity type to avoid
     * repeated reflection during subsequent invocations.</p>
     *
     * @param entityType The entity class to which the attribute belongs
     * @param attribute  The attribute name to be evaluated
     * @return {@code true} if the attribute represents an entity identifier;
     *         {@code false} otherwise
     */
    @Override
    public boolean isIdentifier(Class<?> entityType, String attribute) {
        if ("id".equals(attribute)) {
            return true;
        }

        return cache
                .computeIfAbsent(entityType, this::resolveIds)
                .contains(attribute);
    }

    /**
     * Resolves all identifier attribute names declared by the given entity type.
     *
     * <p>This method inspects the entity class and its superclasses, collecting
     * identifier attributes declared via {@link jakarta.persistence.Id} or
     * {@link jakarta.persistence.EmbeddedId} annotations on fields or accessor
     * methods.</p>
     *
     * @param type The entity class to inspect
     * @return A set containing the names of all identifier attributes declared
     *         for the given entity type
     */
    private Set<String> resolveIds(Class<?> type) {
        Set<String> ids = new HashSet<>();

        for (Field field : type.getDeclaredFields()) {
            if (isAnnotatedWithId(field)) {
                ids.add(field.getName());
            }
        }

        for (Method method : type.getMethods()) {
            if (isAnnotatedWithId(method)) {
                ids.add(propertyName(method));
            }
        }

        return ids;
    }

    private boolean isAnnotatedWithId(AnnotatedElement element) {
        return element.isAnnotationPresent(Id.class) || element.isAnnotationPresent(EmbeddedId.class);
    }

    /**
     * Resolves the logical property name from an accessor method.
     *
     * <p>This method supports JavaBean-style accessor naming conventions
     * such as {@code getXxx()} and {@code isXxx()}.</p>
     *
     * @param method The accessor method
     * @return The corresponding property name derived from the method name
     */
    private String propertyName(Method method) {
        String name = method.getName();

        if (name.startsWith("get")) {
            return Character.toLowerCase(name.charAt(3)) + name.substring(4);
        }

        if (name.startsWith("is")) {
            return Character.toLowerCase(name.charAt(2)) + name.substring(3);
        }

        return name;
    }
}
