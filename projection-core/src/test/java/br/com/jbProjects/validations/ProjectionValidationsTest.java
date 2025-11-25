package br.com.jbProjects.validations;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionJoin;
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
            Assertions.fail("Projection class is null");

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Projection class cannot be null", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_needProjectionAnnotation() {
        try{
            ProjectionValidations.validateProjectionClass(String.class);
            Assertions.fail("String class not have @Projection");

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Projection class needs to have @Projection", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_ofNotEntity() {
        try{
            ProjectionValidations.validateProjectionClass(ProjectionValidationsNotEntityProjection.class);
            Assertions.fail("'of' attribute is not an entity");

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Projection 'of' attribute must be an entity class", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_ofEntity() {
        ProjectionValidations.validateProjectionClass(ProjectionValidationsEntityProjection.class);
    }

    @Test
    void validateProjectionClass_aliasEqualsPath() {
        try{
            ProjectionValidations.validateProjectionClass(ProjectionValidationsAliasEqualsPath.class);
            Assertions.fail("Theres is an alias equals a path");

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Alias 'city' cannot be equal a path", e.getMessage());

        }
    }

    @Test
    void validateProjectionClass_aliasDuplicated() {
        try{
            ProjectionValidations.validateProjectionClass(ProjectionValidationsAliasDuplicated.class);
            Assertions.fail("Theres is an alias duplicated");

        }catch (IllegalArgumentException e){
            Assertions.assertEquals("Duplicate alias detected: 'mainCity'", e.getMessage());

        }
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


@Projection(
        of = ProjectionValidationsEntity.class,
        joins = {
                @ProjectionJoin(path = "city"),
                @ProjectionJoin(path = "city.state", alias = "city")
        }
)
class ProjectionValidationsAliasEqualsPath{ }

@Projection(
        of = ProjectionValidationsEntity.class,
        joins = {
                @ProjectionJoin(path = "city", alias = "mainCity"),
                @ProjectionJoin(path = "city.state", alias = "mainCity")
        }
)
class ProjectionValidationsAliasDuplicated{ }
