package br.com.jbProjects.processor.selectOperator;

import br.com.jbProjects.processor.selectOperator.handler.*;

import java.util.*;

/**
 * Created by julio.bueno on 24/11/2025.
 */
public class ProjectionSelectOperatorProvider {

    private static final ProjectionSelectOperatorProvider INSTANCE = new ProjectionSelectOperatorProvider();

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

    public void register(String operator, ProjectionSelectOperatorHandler sumHandler) {
        if(operators.containsKey(operator)){
            throw new IllegalArgumentException("Operator already registered: " + operator);
        }

        operators.put(operator.toUpperCase(), sumHandler);
    }

    public ProjectionSelectOperatorHandler get(ProjectionSelectOperator operator){
        return get(operator.name());
    }

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

    public List<ProjectionSelectOperatorHandler> operators(){
        return new ArrayList<>(operators.values());
    }

    public Set<String> availableOperators(){
        return Collections.unmodifiableSet(operators.keySet());
    }

}
