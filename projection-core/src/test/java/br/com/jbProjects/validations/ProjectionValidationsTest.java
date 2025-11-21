package br.com.jbProjects.validations;

import br.com.jbProjects.annotations.Projection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by julio.bueno on 21/11/2025.
 */
class ProjectionValidationsTest {

    @Test
    void validateProjectionClass_cannotBeNull() {
        try{
            ProjectionValidations.validateProjectionClass(null);

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Projection class cannot be null", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_needProjectionAnnotation() {
        try{
            ProjectionValidations.validateProjectionClass(String.class);

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Projection class needs to have @Projection", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_ofNotEntity() {
        try{
            ProjectionValidations.validateProjectionClass(ProjectionValidationsNotEntityProjection.class);

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Projection 'of' attribute must be an entity class", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_ofEntity() {
        ProjectionValidations.validateProjectionClass(ProjectionValidationsEntityProjection.class);
    }
}

@Getter
@Setter
@Entity
class ProjectionValidationsEntity{

    @Id
    private Long id;

}

@Projection(of = ProjectionValidationsEntity.class)
class ProjectionValidationsEntityProjection{ }

class ProjectionValidationsNotEntity{ }

@Projection(of = ProjectionValidationsNotEntity.class)
class ProjectionValidationsNotEntityProjection{ }
