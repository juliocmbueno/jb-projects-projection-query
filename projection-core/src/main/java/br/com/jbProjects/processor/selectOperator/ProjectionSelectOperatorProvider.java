package br.com.jbProjects.processor.selectOperator;

import br.com.jbProjects.processor.selectOperator.handler.*;

import java.util.*;

/**
 * Created by julio.bueno on 24/11/2025.
 * <p>Provider for Projection Select Operators and their corresponding handlers.</p>
 * <p>By default, the class is created with all operations of {@link ProjectionSelectOperator}</p>
 */
public class ProjectionSelectOperatorProvider {

    private static final ProjectionSelectOperatorProvider INSTANCE = new ProjectionSelectOperatorProvider();

    /**
     * <p>Returns the singleton instance of ProjectionSelectOperatorProvider.</p>
     *
     * @return Singleton instance of ProjectionSelectOperatorProvider
     */
    public static ProjectionSelectOperatorProvider getInstance() {
        return INSTANCE;
    }

    private final Map<String, ProjectionSelectOperatorHandler> operators = new HashMap<>();

    private ProjectionSelectOperatorProvider(){
        register(ProjectionSelectOperator.COUNT, new CountHandler());
        register(ProjectionSelectOperator.MIN, new MinHandler());
        register(ProjectionSelectOperator.MAX, new MaxHandler());
        register(ProjectionSelectOperator.SUM, new SumHandler());
    }

    private void register(ProjectionSelectOperator projectionSelectOperator, ProjectionSelectOperatorHandler handler) {
        register(projectionSelectOperator.name(), handler);
    }

    /**
     * <p>Registers a new ProjectionSelectOperatorHandler for the specified operator.</p>
     *
     * @param operator The operator as a String
     * @param sumHandler The handler to be registered
     * @throws IllegalArgumentException if the operator is already registered
     */
    public void register(String operator, ProjectionSelectOperatorHandler sumHandler) {
        if(operators.containsKey(operator)){
            throw new IllegalArgumentException("Operator already registered: " + operator);
        }

        operators.put(operator.toUpperCase(), sumHandler);
    }

    /**
     * <p>Retrieves the ProjectionSelectOperatorHandler for the specified operator.</p>
     *
     * @param operator The operator
     * @return The corresponding ProjectionSelectOperatorHandler
     * @throws IllegalArgumentException if the operator is not found
     */
    public ProjectionSelectOperatorHandler get(ProjectionSelectOperator operator){
        return get(operator.name());
    }

    /**
     * <p>Retrieves the ProjectionSelectOperatorHandler for the specified operator.</p>
     *
     * @param name The operator as a String
     * @return The corresponding ProjectionSelectOperatorHandler
     * @throws IllegalArgumentException if the operator is not found
     */
    public ProjectionSelectOperatorHandler get(String name) {
        ProjectionSelectOperatorHandler handler = operators.get(name.toUpperCase());
        if(handler == null){
            throw new IllegalArgumentException(
                    "Operator not found: " + name +
                            "\nAvailable operators: " + operators.keySet()
            );
        }
        return handler;
    }

    /**
     * <p>Returns a list of all registered ProjectionSelectOperatorHandlers.</p>
     *
     * @return List of registered ProjectionSelectOperatorHandlers
     */
    public List<ProjectionSelectOperatorHandler> operators(){
        return new ArrayList<>(operators.values());
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
