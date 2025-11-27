package br.com.jbProjects.spring;

import br.com.jbProjects.processor.ProjectionProcessor;
import br.com.jbProjects.processor.filter.ProjectionFilterOperatorProvider;
import br.com.jbProjects.processor.selectOperator.ProjectionSelectOperatorProvider;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Created by julio.bueno on 27/11/2025.
 *
 * <p>Autoconfiguration responsible for registering the core beans used by the
 * ProjectionQuery mechanism within Spring Boot applications.</p>
 *
 * <p>This class is automatically loaded by Spring Boot when present on the
 * classpath, thanks to the {@link org.springframework.boot.autoconfigure.AutoConfiguration}
 * annotation. It provides default implementations for the essential projection
 * components, ensuring that applications using JPA have immediate support for
 * projection processing.</p>
 *
 * <p>Each bean is only registered if no other implementation is already defined
 * in the application context, allowing full customization when needed.</p>
 *
 * <h2>Registered Beans</h2>
 *
 * <ul>
 *   <li><strong>{@link ProjectionFilterOperatorProvider}</strong> –
 *       Provider responsible for supplying the operators used in filter
 *       expressions.</li>
 *
 *   <li><strong>{@link ProjectionSelectOperatorProvider}</strong> –
 *       Provider responsible for supplying the operators used in select
 *       expressions.</li>
 *
 *   <li><strong>{@link ProjectionProcessor}</strong> –
 *       Central component that builds and executes projection-based queries,
 *       integrating the Criteria API with the EntityManager.</li>
 * </ul>
 *
 * <p>This configuration simplifies integration with Spring Data, allowing modules
 * across the application to use ProjectionQuery automatically, consistently,
 * and with minimal setup.</p>
 */

@AutoConfiguration
public class ProjectionQueryAutoConfiguration {

    /**
     * Default constructor for ProjectionQueryAutoConfiguration.
     */
    public ProjectionQueryAutoConfiguration(){}

    /**
     * Registers the default ProjectionFilterOperatorProvider bean if none is
     * already defined in the application context.
     *
     * @return the singleton instance of ProjectionFilterOperatorProvider
     */
    @Bean
    @ConditionalOnMissingBean
    public ProjectionFilterOperatorProvider projectionFilterOperatorProvider() {
        return ProjectionFilterOperatorProvider.getInstance();
    }

    /**
     * Registers the default ProjectionSelectOperatorProvider bean if none is
     * already defined in the application context.
     *
     * @return the singleton instance of ProjectionSelectOperatorProvider
     */
    @Bean
    @ConditionalOnMissingBean
    public ProjectionSelectOperatorProvider projectionSelectOperatorProvider() {
        return ProjectionSelectOperatorProvider.getInstance();
    }

    /**
     * Registers the ProjectionProcessor bean if none is already defined in
     * the application context.
     *
     * @param entityManager the EntityManager used for JPA operations
     * @return a new instance of ProjectionProcessor
     */
    @Bean
    @ConditionalOnMissingBean
    public ProjectionProcessor projectionProcessor(EntityManager entityManager) {
        return new ProjectionProcessor(entityManager);
    }

}
