package br.com.jbProjects.processor.joinResolver;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityAttributes;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityJoinWithAlias;
import br.com.jbProjects.util.ProjectionUtils;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by julio.bueno on 25/11/2025.
 */
@SuppressWarnings("unchecked")
class DefaultPathResolverTest {

    @Test
    public void create_validateAnnotationJoins(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        Map<String, JoinType> annotationJoins = (Map<String, JoinType>) ReflectionTestUtils.getField(resolver, "annotationJoins");
        assertNotNull(annotationJoins);
        assertEquals(2, annotationJoins.size());

        JoinType joinType = annotationJoins.get("mainAddress");
        assertNotNull(joinType);
        assertEquals(JoinType.INNER, joinType);

        joinType = annotationJoins.get("address.city.state");
        assertNotNull(joinType);
        assertEquals(JoinType.INNER, joinType);
    }

    @Test
    public void create_validatePathsByAlias(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        Map<String, String> pathsByAlias = (Map<String, String>) ReflectionTestUtils.getField(resolver, "pathsByAlias");
        assertNotNull(pathsByAlias);
        assertEquals(2, pathsByAlias.size());

        assertEquals("mainAddress", pathsByAlias.get("address"));
        assertEquals("address.city.state", pathsByAlias.get("cityState"));
    }

    @Test
    public void resolveAlias(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        String resolved = ReflectionTestUtils.invokeMethod(resolver, "resolveAlias", "address.city.id");
        assertEquals("mainAddress.city.id", resolved);

        resolved = ReflectionTestUtils.invokeMethod(resolver, "resolveAlias", "address.city.name");
        assertEquals("mainAddress.city.name", resolved);

        resolved = ReflectionTestUtils.invokeMethod(resolver, "resolveAlias", "cityState.name");
        assertEquals("mainAddress.city.state.name", resolved);
    }

    @Test
    public void resolveSingleAlias(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        String resolved = ReflectionTestUtils.invokeMethod(resolver, "resolveSingleAlias", "cityState.name");
        assertEquals("address.city.state.name", resolved);

        resolved = ReflectionTestUtils.invokeMethod(resolver, "resolveSingleAlias", "address");
        assertEquals("mainAddress", resolved);

        resolved = ReflectionTestUtils.invokeMethod(resolver, "resolveSingleAlias", "id");
        assertEquals("id", resolved);
    }

    @Test
    public void buildJoinKey(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        From<?, ?> from = Mockito.mock(From.class);
        Mockito.doReturn("to-string").when(from).toString();

        String key = ReflectionTestUtils.invokeMethod(resolver, "buildJoinKey", from, "city");
        assertEquals("to-string.city", key);
    }

    @Test
    public void resolveJoinType(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        // mainAddress hierarchy
        JoinType joinType = ReflectionTestUtils.invokeMethod(resolver, "resolveJoinType", "mainAddress");
        assertEquals(JoinType.INNER, joinType);

        joinType = ReflectionTestUtils.invokeMethod(resolver, "resolveJoinType", "mainAddress.city");
        assertEquals(JoinType.INNER, joinType);

        joinType = ReflectionTestUtils.invokeMethod(resolver, "resolveJoinType", "mainAddress.city.state");
        assertEquals(JoinType.INNER, joinType);

        // secondaryAddress hierarchy
        joinType = ReflectionTestUtils.invokeMethod(resolver, "resolveJoinType", "secondaryAddress");
        assertEquals(JoinType.LEFT, joinType);

        joinType = ReflectionTestUtils.invokeMethod(resolver, "resolveJoinType", "secondaryAddress.city");
        assertEquals(JoinType.LEFT, joinType);

        joinType = ReflectionTestUtils.invokeMethod(resolver, "resolveJoinType", "secondaryAddress.city.state");
        assertEquals(JoinType.LEFT, joinType);
    }

    @Test
    public void joinPart(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        //
        From<?, ?> from = Mockito.mock(From.class);
        Join<?, ?> join = Mockito.mock(Join.class);
        Mockito.doReturn(join).when(from).join("city", JoinType.INNER);

        // Try creating the same join multiple times
        ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");
        ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");
        ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");

        // validated
        Join<?, ?> joinResult = ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");
        assertEquals(join, joinResult);

        Mockito
                .verify(from, Mockito.times(1).description("Only need to create the join once"))
                .join("city", JoinType.INNER);


    }

    @Test
    public void joinPart_validatedJoinCache(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        //
        From<?, ?> from = Mockito.mock(From.class);
        Join<?, ?> join = Mockito.mock(Join.class);
        Mockito.doReturn(join).when(from).join("city", JoinType.INNER);

        // Try creating the same join multiple times
        ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");
        ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");
        ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");

        // validated
        Join<?, ?> joinResult = ReflectionTestUtils.invokeMethod(resolver, "joinPart", from, "mainAddress.city.state.name", "city");
        assertEquals(join, joinResult);

        Map<String, Join<?,?>> joinCache = (Map<String, Join<?, ?>>) ReflectionTestUtils.getField(resolver, "joinCache");
        assertEquals(1, joinCache.size());
        assertEquals(join, joinCache.entrySet().iterator().next().getValue());
    }

    @Test
    public void resolve(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(declaredJoins);

        Root<?> root = Mockito.mock(Root.class);
        Path<?> pathName = Mockito.mock(Path.class);
        Mockito.doReturn(pathName).when(root).get("name");

        Path<?> resolve = resolver.resolve(root, "name");
        assertEquals(pathName, resolve);
    }

    @Test
    public void resolve_withJoin(){
        List<ProjectionJoin> declaredJoins = ProjectionUtils.getDeclaredJoins(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = Mockito.spy(new DefaultPathResolver(declaredJoins));

        Root<?> root = Mockito.mock(Root.class);
        Join<?, ?> joinMainAddress = Mockito.mock(Join.class);
        Join<?, ?> joinCity = Mockito.mock(Join.class);
        Path<?> pathId = Mockito.mock(Path.class);

        Mockito.doReturn(joinMainAddress).when(root).join("mainAddress", JoinType.INNER);
        Mockito.doReturn(joinCity).when(joinMainAddress).join("city", JoinType.INNER);
        Mockito.doReturn(pathId).when(joinCity).get("id");
        Mockito.doReturn("root-to-string").when(root).toString();

        //
        Path<?> resolve = resolver.resolve(root, "mainAddress.city.id");
        assertEquals(pathId, resolve);
    }
}
