package br.com.jbProjects.standalone;

import br.com.jbProjects.domain.projections.CustomerBasicDataClass;
import br.com.jbProjects.processor.ProjectionProcessor;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Created by julio.bueno on 27/11/2025.
 * <p>Demonstrates how to use {@link ProjectionProcessor} in a standalone (non-Spring) environment.</p>
 *
 * <p>This example differs from {@code ProjectionsSpringExample} only in how the
 * {@link ProjectionProcessor} is instantiated. Here, it is created manually using
 * the provided {@link EntityManager}, while in the Spring example it is obtained
 * as a managed Spring bean.</p>
 *
 * <p>For full usage examples of projection queries, see:
 * {@link br.com.jbProjects.spring.ProjectionsSpringExample ProjectionsSpringExample}.</p>
 */
@SuppressWarnings("FieldCanBeLocal")
public class ProjectionsStandaloneExample {

    private final ProjectionProcessor projectionProcessor;

    public ProjectionsStandaloneExample(EntityManager entityManager){
        projectionProcessor = new ProjectionProcessor(entityManager);
    }

    /**
     * Basic example of fetching projections using a projection class.
     *
     * @see CustomerBasicDataClass
     */
    public void fetchCustomerBasicDataClass(){
        List<CustomerBasicDataClass> example = projectionProcessor.execute(CustomerBasicDataClass.class);
    }

    /*
     * Additional projection query examples can be implemented here,
     * similar to those in {@code ProjectionsSpringExample}.
     */
}
