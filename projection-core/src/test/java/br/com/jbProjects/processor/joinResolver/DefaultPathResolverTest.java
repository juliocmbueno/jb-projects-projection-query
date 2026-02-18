package br.com.jbProjects.processor.joinResolver;

import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.city.domain.City;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityAttributes;
import br.com.jbProjects.config.testModel.customer.projections.CustomerNameAndCityJoinWithAlias;
import br.com.jbProjects.config.testModel.customer.projections.CustomerWithCityId;
import br.com.jbProjects.metadata.cache.ProjectionMetadataCache;
import br.com.jbProjects.metadata.model.ProjectionMetadata;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

        Map<String, JoinType> annotationJoins = (Map<String, JoinType>) ReflectionTestUtils.getField(resolver, "annotationJoins");
        assertNotNull(annotationJoins);
        assertEquals(2, annotationJoins.size());

        JoinType joinType = annotationJoins.get("mainAddress");
        assertNotNull(joinType);
        assertEquals(JoinType.INNER, joinType);

        joinType = annotationJoins.get("mainAddress.city.state");
        assertNotNull(joinType);
        assertEquals(JoinType.INNER, joinType);
    }

    @Test
    public void buildJoinKey(){
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityJoinWithAlias.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

        From<?, ?> from = Mockito.mock(From.class);
        Mockito.doReturn("to-string").when(from).toString();

        String key = ReflectionTestUtils.invokeMethod(resolver, "buildJoinKey", from, "city");
        assertEquals("to-string.city", key);
    }

    @Test
    public void resolveJoinType(){
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

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
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

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
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

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
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

        Root<?> root = Mockito.mock(Root.class);
        Path<?> pathName = Mockito.mock(Path.class);
        Mockito.doReturn(pathName).when(root).get("name");

        Path<?> resolve = resolver.resolve(root, "name");
        assertEquals(pathName, resolve);
    }

    @Test
    public void resolve_withJoin(){
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerNameAndCityAttributes.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

        Root<?> root = Mockito.mock(Root.class);
        Join<?, ?> joinMainAddress = Mockito.mock(Join.class);
        Join<?, ?> joinCity = Mockito.mock(Join.class);
        Path<?> pathId = Mockito.mock(Path.class);
        Path<?> pathName = Mockito.mock(Path.class);

        // configure root returns
        Mockito.doReturn(joinMainAddress).when(root).join("mainAddress", JoinType.INNER);
        Mockito.doReturn("root-to-string").when(root).toString();

        // configure mainAddress join returns
        Mockito.doReturn(joinCity).when(joinMainAddress).join("city", JoinType.INNER);
        Mockito.doReturn(joinCity).when(joinMainAddress).get("city");
        Mockito.doReturn("join-main-address-to-string").when(joinMainAddress).toString();

        // configure city join returns
        Mockito.doReturn(pathId).when(joinCity).get("id");
        Mockito.doReturn(pathName).when(joinCity).get("name");
        Mockito.doReturn(City.class).when(joinCity).getJavaType();
        Mockito.doReturn("join-city-to-string").when(joinCity).toString();

        // configure id path returns
        Mockito.doReturn("path-id-to-string").when(pathId).toString();

        // configure pathName path returns
        Mockito.doReturn("path-name-to-string").when(pathName).toString();

        //
        Path<?> resolve = resolver.resolve(root, "mainAddress.city.id");
        assertEquals(pathId, resolve);

        resolve = resolver.resolve(root, "mainAddress.city.name");
        assertEquals(pathName, resolve);
    }

    @Test
    public void resolve_withExplicitJoin(){
        ProjectionMetadata metadata = ProjectionMetadataCache.get(CustomerWithCityId.class);
        DefaultPathResolver resolver = new DefaultPathResolver(metadata);

        Root<?> root = Mockito.mock(Root.class);
        Join<?, ?> joinMainAddress = Mockito.mock(Join.class);
        Join<?, ?> joinSecondaryAddress = Mockito.mock(Join.class);
        Join<?, ?> joinCity = Mockito.mock(Join.class);
        Path<?> pathId = Mockito.mock(Path.class);

        // configure root returns
        Mockito.doReturn(joinMainAddress).when(root).join("mainAddress", JoinType.INNER);
        Mockito.doReturn(joinSecondaryAddress).when(root).join("secondaryAddress", JoinType.INNER);
        Mockito.doReturn("root-to-string").when(root).toString();

        // configure mainAddress join returns
        Mockito.doReturn(joinCity).when(joinMainAddress).join("city", JoinType.INNER);
        Mockito.doReturn("join-main-address-to-string").when(joinMainAddress).toString();

        // configure secondaryAddress join returns
        Mockito.doReturn("join-secondary-address-to-string").when(joinSecondaryAddress).toString();
        Mockito.doReturn(joinCity).when(joinSecondaryAddress).get("city");

        // configure city join returns
        Mockito.doReturn(pathId).when(joinCity).get("id");
        Mockito.doReturn(City.class).when(joinCity).getJavaType();
        Mockito.doReturn("join-city-to-string").when(joinCity).toString();

        // configure id path returns
        Mockito.doReturn("path-id-to-string").when(pathId).toString();

        //
        Path<?> resolve = resolver.resolve(root, "mainAddress.city.id");
        assertEquals(pathId, resolve);

        resolve = resolver.resolve(root, "secondaryAddress.city.id");
        assertEquals(pathId, resolve);
    }
}
