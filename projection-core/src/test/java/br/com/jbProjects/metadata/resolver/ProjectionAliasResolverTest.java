package br.com.jbProjects.metadata.resolver;

import br.com.jbProjects.annotations.Projection;
import br.com.jbProjects.annotations.ProjectionField;
import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityJoinWithAlias;
import br.com.jbProjects.util.ProjectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by julio.bueno on 18/02/2026.
 */
@SuppressWarnings("unchecked")
class ProjectionAliasResolverTest {

    @Test
    public void ofWithProjectionJoin(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        ProjectionAliasResolver aliasResolver = ProjectionAliasResolver.of(declaredJoins);
        assertNotNull(aliasResolver);

        Map<String, String> aliasMap = aliasResolver.getAliasMap();
        assertNotNull(aliasMap);
        assertEquals(2, aliasMap.size());
        assertEquals("mainAddress", aliasMap.get("address"));
        assertEquals("address.city.state", aliasMap.get("cityState"));
    }

    @Test
    public void ofWithMap(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        Map<String, String> aliasMapOrigin = ProjectionAliasResolver.of(declaredJoins).getAliasMap();

        ProjectionAliasResolver aliasResolver = ProjectionAliasResolver.of(aliasMapOrigin);
        assertNotNull(aliasResolver);

        Map<String, String> aliasMap = aliasResolver.getAliasMap();
        assertEquals(aliasMap.size(), aliasMapOrigin.size());
        aliasMap.forEach((key, value) -> assertEquals(aliasMapOrigin.get(key), value));
    }

    @Test
    public void ofWithNullMap(){
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> ProjectionAliasResolver.of((Map<String, String>) null));
        Assertions.assertEquals("Alias map cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    public void ofWithInvalidMap(){
        Map<String, String> invalidMap = Map.of(
                "address", "mainAddress",
                "mainAddress", "address"
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> ProjectionAliasResolver.of(invalidMap), "The resolver should detect circular alias definitions and throw an exception");
    }

    @Test
    public void resolve(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        ProjectionAliasResolver aliasResolver = ProjectionAliasResolver.of(declaredJoins);

        String resolved = aliasResolver.resolve("name");
        assertEquals("name", resolved);

        resolved = aliasResolver.resolve("address.city.id");
        assertEquals("mainAddress.city.id", resolved);

        resolved = aliasResolver.resolve("address.city.name");
        assertEquals("mainAddress.city.name", resolved);

        resolved = aliasResolver.resolve("cityState.name");
        assertEquals("mainAddress.city.state.name", resolved);
    }

    @Test
    public void resolveWithCircularAlias(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerWithCircularAlias.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> ProjectionAliasResolver.of(declaredJoins));
    }

    @Test
    public void resolveSingleAlias(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        ProjectionAliasResolver aliasResolver = ProjectionAliasResolver.of(declaredJoins);

        String resolved = ReflectionTestUtils.invokeMethod(aliasResolver, "resolveSingleAlias", "cityState.name");
        assertEquals("address.city.state.name", resolved);

        resolved = ReflectionTestUtils.invokeMethod(aliasResolver, "resolveSingleAlias", "address");
        assertEquals("mainAddress", resolved);

        resolved = ReflectionTestUtils.invokeMethod(aliasResolver, "resolveSingleAlias", "id");
        assertEquals("id", resolved);
    }
}

@Projection(
        of = Customer.class,
        joins = {
                @ProjectionJoin(path = "mainAddress", alias = "address"),
                @ProjectionJoin(path = "address.city.state", alias = "cityState"),
                @ProjectionJoin(path = "circularAlias", alias = "mainAddress")
        }
)
record CustomerWithCircularAlias(
   @ProjectionField String name
){ }
