package br.com.jbProjects.processor.joinResolver;

/**
 * Created by julio.bueno on 05/01/2026.
 * <p>Interface for resolving if an attribute is an identifier of an entity.</p>
 */
public interface IdentifierResolver {

    /**
     * Checks if the given attribute of the entity type is an identifier.
     *
     * @param entityType the entity class type
     * @param attribute the attribute name to check
     * @return true if the attribute is an identifier, false otherwise
     */
    boolean isIdentifier(Class<?> entityType, String attribute);

}
