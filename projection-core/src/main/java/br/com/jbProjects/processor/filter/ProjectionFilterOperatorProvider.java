package br.com.jbProjects.processor.filter;

import br.com.jbProjects.processor.filter.handler.*;
import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public class ProjectionFilterOperatorProvider {

    private static final ProjectionFilterOperatorProvider INSTANCE = new ProjectionFilterOperatorProvider();

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

    private void register(ProjectionFilterOperator operator, @NonNull ProjectionFilterOperatorHandler handler){
        register(operator.name(), handler);
    }

    public void register(String operator, @NonNull ProjectionFilterOperatorHandler handler){
        if(operators.containsKey(operator)){
            throw new IllegalArgumentException("Operator already registered: " + operator);
        }

        operators.put(operator.toUpperCase(), handler);
    }

    public ProjectionFilterOperatorHandler get(ProjectionFilterOperator operator){
        return get(operator.name());
    }

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

    public Set<String> availableOperators(){
        return Collections.unmodifiableSet(operators.keySet());
    }

}
