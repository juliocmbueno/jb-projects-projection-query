package br.com.jbProjects.processor.order;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Record representing an ordering criterion for projections.</p>
 * <p>It's possible to use nested paths to access fields in related entities.</p>
 * <p> ex: "name" or "address.city.name" </p>
 * @param path     The path to the field in the entity to be ordered.
 * @param direction The direction of the ordering (ASCENDING or DESCENDING).
 */
public record ProjectionOrder(
        String path,
        OrderDirection direction
) {
}
