package br.com.jbProjects.processor.selectOperator;

import br.com.jbProjects.processor.selectOperator.handler.*;

import java.util.*;

/**
 * Created by julio.bueno on 24/11/2025.
 * <p>Provider for Projection Select Operators and their corresponding handlers.</p>
 * <p>By default, the class is created with all operations:</p>
 * <ul>
 *  <li>{@link DefaultSelectOperatorHandler}</li>
 *  <li>{@link CountHandler}</li>
 *  <li>{@link MinHandler}</li>
 *  <li>{@link MaxHandler}</li>
 *  <li>{@link SumHandler}</li>
 *  <li>{@link AvgHandler}</li>
 *  <li>{@link AbsHandler}</li>
 * </ul>
 *
 *
 * You can register new operators using the {@link #register(ProjectionSelectOperatorHandler)} method.
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
        register(new DefaultSelectOperatorHandler());
        register(new CountHandler());
        register(new MinHandler());
        register(new MaxHandler());
        register(new SumHandler());
        register(new AvgHandler());
        register(new AbsHandler());
    }

    /**
     * <p>Registers a new ProjectionSelectOperatorHandler for the specified operator.</p>
     *
     * @param operatorHandler The handler to be registered
     * @throws IllegalArgumentException if the operator is already registered
     */
    public void register(ProjectionSelectOperatorHandler operatorHandler) {
        String name = operatorHandler.getClass().getName();
        if(operators.containsKey(name)){
            throw new IllegalArgumentException("Operator already registered: " + name);
        }

        operators.put(name, operatorHandler);
    }

    /**
     * <p>Retrieves the ProjectionSelectOperatorHandler for the specified operator.</p>
     *
     * @param operator The operator
     * @return The corresponding ProjectionSelectOperatorHandler
     * @throws IllegalArgumentException if the operator is not found
     */
    public ProjectionSelectOperatorHandler get(Class<? extends ProjectionSelectOperatorHandler> operator){
        return get(operator.getName());
    }

    /**
     * <p>Retrieves the ProjectionSelectOperatorHandler for the specified operator.</p>
     *
     * @param name The operator as a String
     * @return The corresponding ProjectionSelectOperatorHandler
     * @throws IllegalArgumentException if the operator is not found
     */
    public ProjectionSelectOperatorHandler get(String name) {
        ProjectionSelectOperatorHandler handler = operators.get(name);
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
