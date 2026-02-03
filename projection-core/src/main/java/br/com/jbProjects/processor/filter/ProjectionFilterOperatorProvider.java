package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.filter.handler.*;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Provider for Projection Filter Operator Handlers</p>
 * <p>By default, the class is created with all operations of {@link ProjectionFilterOperator}</p>
 * <p>New operators can be registered using the {@link #register(String, ProjectionFilterOperatorHandler)} method.</p>
 */
public class ProjectionFilterOperatorProvider {

    private static final ProjectionFilterOperatorProvider INSTANCE = new ProjectionFilterOperatorProvider();

    /**
     * <p>Returns the singleton instance of ProjectionFilterOperatorProvider.</p>
     *
     * @return Singleton instance of ProjectionFilterOperatorProvider
     */
    public static ProjectionFilterOperatorProvider getInstance() {
        return INSTANCE;
    }

    private final Map<String, ProjectionFilterOperatorHandler> operators = new HashMap<>();

    private ProjectionFilterOperatorProvider(){
        register(ProjectionFilterOperator.EQUAL, new EqualHandler());
        register(ProjectionFilterOperator.NOT_EQUAL, new NotEqualHandler());
        register(ProjectionFilterOperator.GREATER_THAN, new GreaterThanHandler());
        register(ProjectionFilterOperator.LESS_THAN, new LessThanHandler());
        register(ProjectionFilterOperator.GREATER_THAN_OR_EQUAL, new GreaterThanOrEqualHandler());
        register(ProjectionFilterOperator.LESS_THAN_OR_EQUAL, new LessThanOrEqualHandler());
        register(ProjectionFilterOperator.LIKE, new LikeHandler());
        register(ProjectionFilterOperator.IN, new InHandler());
        register(ProjectionFilterOperator.NOT_IN, new NotInHandler());
        register(ProjectionFilterOperator.BETWEEN, new BetweenHandler());
    }

    private void register(ProjectionFilterOperator operator, ProjectionFilterOperatorHandler handler){
        register(operator.name(), handler);
    }

    /**
     * <p>Registers a new ProjectionFilterOperatorHandler for the specified operator.</p>
     *
     * @param operator The operator as a String
     * @param handler The handler to be registered
     * @throws IllegalArgumentException if the operator is already registered
     */
    public void register(String operator, @NonNull ProjectionFilterOperatorHandler handler){
        if(operators.containsKey(operator)){
            throw new IllegalArgumentException("Operator already registered: " + operator);
        }

        operators.put(operator.toUpperCase(), handler);
    }

    /**
     * <p>Retrieves the ProjectionFilterOperatorHandler for the specified operator.</p>
     *
     * @param operator The operator as a ProjectionFilterOperator enum
     * @return The corresponding ProjectionFilterOperatorHandler
     * @throws IllegalArgumentException if the operator is not found
     */
    public ProjectionFilterOperatorHandler get(ProjectionFilterOperator operator){
        return get(operator.name());
    }

    /**
     * <p>Retrieves the ProjectionFilterOperatorHandler for the specified operator.</p>
     *
     * @param operator The operator as a String
     * @return The corresponding ProjectionFilterOperatorHandler
     * @throws IllegalArgumentException if the operator is not found
     */
    public ProjectionFilterOperatorHandler get(String operator){
        ProjectionFilterOperatorHandler handler = operators.get(operator.toUpperCase());
        if(handler == null){
            throw new IllegalArgumentException(
                    "Operator not found: " + operator +
                            "\nAvailable operators: " + operators.keySet()
            );
        }

        return handler;
    }

    /**
     * <p>Returns an unmodifiable set of available operator names.</p>
     *
     * @return Set of available operator names
     */
    public Set<String> availableOperators(){
        return Collections.unmodifiableSet(operators.keySet());
    }
}
