package br.com.jbProjects.processor.operatorHandler;

import java.util.List;

/**
 * Created by julio.bueno on 24/11/2025.
 */
public class ProjectionOperatorProvider {

    private static final List<ProjectionOperatorHandler> operators = List.of(
            new CountHandler(),
            new MinHandler(),
            new MaxHandler(),
            new SumHandler()
    );

    private ProjectionOperatorProvider(){

    }

    public static List<ProjectionOperatorHandler> operators(){
        return operators;
    }

}
