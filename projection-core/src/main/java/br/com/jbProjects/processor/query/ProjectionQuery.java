package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.processor.filter.ProjectionFilter;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.joinResolver.DefaultPathResolver;
import br.com.jbProjects.processor.joinResolver.PathResolver;
import br.com.jbProjects.processor.order.OrderDirection;
import br.com.jbProjects.processor.order.ProjectionOrder;
import br.com.jbProjects.util.ProjectionUtils;
import br.com.jbProjects.validations.ProjectionValidations;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Represents a projection query between a source entity and a target DTO.</p>
 * <p>
 * {@code ProjectionQuery} serves as the entry point for the projection engine,
 * allowing you to define filters, specifications, ordering, and pagination
 * in a fluent way. It encapsulates all the configuration needed to execute a projection.
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * ProjectionQuery<Author, AuthorDTO> query = ProjectionQuery.fromTo(Author.class, AuthorDTO.class)
 *     .filter("name", ProjectionFilterOperator.LIKE, "Einstein")
 *     .order("birthDate", OrderDirection.ASC)
 *     .paging(0, 20)
 *     .distinct();
 * }</pre>
 *
 * @param <FROM> Type of the source entity
 * @param <TO>   Type of the target DTO
 */
@Getter
public class ProjectionQuery<FROM, TO> {

    @Accessors(fluent = true)
    private final Class<FROM> fromClass;

    @Accessors(fluent = true)
    private final Class<TO> toClass;

    private final PathResolver pathResolver;
    private final List<ProjectionSpecification<FROM>> specifications = new ArrayList<>();
    private final List<ProjectionFilter> filters = new ArrayList<>();
    private final List<ProjectionOrder> orders = new ArrayList<>();

    private boolean distinct = false;
    private ProjectionPaging paging;

    private ProjectionQuery(Class<FROM> fromClass, Class<TO> toClass) {
        ProjectionValidations.validateProjectionClass(toClass);
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.pathResolver = new DefaultPathResolver(getDeclaredJoins());
    }

    /**
     * <p>Creates a new ProjectionQuery instance for the specified source and target classes.</p>
     *
     * @param fromClass Source entity class
     * @param toClass   Target class (with @Projection annotation)
     * @param <FROM>    Type of the source entity
     * @param <TO>      Type of the target object
     * @return New ProjectionQuery instance
     */
    public static <FROM, TO> ProjectionQuery<FROM, TO> fromTo(Class<FROM> fromClass, Class<TO> toClass) {
        return new ProjectionQuery<>(fromClass, toClass);
    }

    /**
     * <p>Sets pagination parameters for the projection query.</p>
     *
     * @param first Index of the first result to retrieve
     * @param size  Maximum number of results to retrieve
     * @return The current ProjectionQuery instance for method chaining
     */
    public ProjectionQuery<FROM, TO> paging(int first, int size) {
        this.paging = new ProjectionPaging(first, size);
        return this;
    }

    /**
     * <p>Adds a specification to the projection query.</p>
     *
     * @param specification Specification to add
     * @return The current ProjectionQuery instance for method chaining
     */
    public ProjectionQuery<FROM, TO> specification(ProjectionSpecification<FROM> specification) {
        this.specifications.add(specification);
        return this;
    }

    /**
     * <p>Adds a filter to the projection query.</p>
     * <p>It's possible to use nested paths to access fields in related entities.</p>
     * <p> ex: "name" or "address.city.name" </p>
     * @param path     Property path to filter on
     * @param operator Filter operator
     * @param value    Value to filter by
     * @return The current ProjectionQuery instance for method chaining
     */
    public ProjectionQuery<FROM, TO> filter(String path, ProjectionFilterOperator operator, Object value) {
        filter(path, operator.name(), value);
        return this;
    }

    /**
     * <p>Adds a filter to the projection query.</p>
     * <p>It's possible to use nested paths to access fields in related entities.</p>
     * <p> ex: "name" or "address.city.name" </p>
     * @param path     Property path to filter on
     * @param operator Filter operator as String
     * @param value    Value to filter by
     * @return The current ProjectionQuery instance for method chaining
     */
    public ProjectionQuery<FROM, TO> filter(String path, String operator, Object value) {
        this.filters.add(new ProjectionFilter(path, operator, value));
        return this;
    }

    /**
     * <p>Sets the distinct flag for the projection query.</p>
     *
     * @return The current ProjectionQuery instance for method chaining
     */
    public ProjectionQuery<FROM, TO> distinct(){
        this.distinct = true;
        return this;
    }

    /**
     * <p>Adds an order to the projection query.</p>
     * <p>It's possible to use nested paths to access fields in related entities.</p>
     * <p> ex: "name" or "address.city.name" </p>
     * @param path      Property path to order by
     * @param direction Order direction (ASC or DESC)
     * @return The current ProjectionQuery instance for method chaining
     */
    public ProjectionQuery<FROM, TO> order(String path, OrderDirection direction){
        this.orders.add(new ProjectionOrder(path, direction));
        return this;
    }

    /**
     * <p>Checks if pagination is set for the projection query.</p>
     *
     * @return true if pagination is set, false otherwise
     */
    public boolean hasPaging(){
        return this.paging != null;
    }

    /**
     * <p>Retrieves the declared joins from the target projection class.</p>
     *
     * @return List of ProjectionJoin annotations
     */
    public List<ProjectionJoin> getDeclaredJoins(){
        return ProjectionUtils.getDeclaredJoins(toClass);
    }

    /**
     * <p>Resolves a property path to a JPA Path object using the configured PathResolver.</p>
     *
     * @param from Root entity in the criteria query
     * @param path Property path to resolve
     * @return Resolved JPA Path object
     */
    public Path<?> resolvePath(Root<FROM> from, String path){
        return this.pathResolver.resolve(from, path);
    }
}
