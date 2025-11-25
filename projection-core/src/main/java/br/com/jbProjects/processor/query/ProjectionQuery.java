package br.com.jbProjects.processor.query;

import br.com.jbProjects.annotations.ProjectionJoin;
import br.com.jbProjects.processor.filter.ProjectionFilter;
import br.com.jbProjects.processor.filter.ProjectionFilterOperator;
import br.com.jbProjects.processor.joinResolver.DefaultPathResolver;
import br.com.jbProjects.processor.joinResolver.PathResolver;
import br.com.jbProjects.util.ProjectionUtils;
import br.com.jbProjects.validations.ProjectionValidations;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julio.bueno on 21/11/2025.
 */
@Getter
public class ProjectionQuery<FROM, TO> {

    @Accessors(fluent = true)
    private final Class<FROM> fromClass;

    @Accessors(fluent = true)
    private final Class<TO> toClass;

    private final PathResolver pathResolver;
    private final List<ProjectionSpecification<FROM>> specifications = new ArrayList<>();
    private final List<ProjectionFilter> filters = new ArrayList<>();

    private boolean distinct = false;
    private ProjectionPaging paging;

    private ProjectionQuery(Class<FROM> fromClass, Class<TO> toClass) {
        ProjectionValidations.validateProjectionClass(toClass);
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.pathResolver = new DefaultPathResolver(getDeclaredJoins());
    }

    public static <FROM, TO> ProjectionQuery<FROM, TO> fromTo(Class<FROM> fromClass, Class<TO> toClass) {
        return new ProjectionQuery<>(fromClass, toClass);
    }

    public ProjectionQuery<FROM, TO> paging(int first, int size) {
        this.paging = new ProjectionPaging(first, size);
        return this;
    }

    public ProjectionQuery<FROM, TO> specification(ProjectionSpecification<FROM> specification) {
        this.specifications.add(specification);
        return this;
    }

    public ProjectionQuery<FROM, TO> filter(String path, ProjectionFilterOperator operator, Object value) {
        filter(path, operator.name(), value);
        return this;
    }

    public ProjectionQuery<FROM, TO> filter(String path, String operator, Object value) {
        this.filters.add(new ProjectionFilter(path, operator, value));
        return this;
    }

    public ProjectionQuery<FROM, TO> distinct(){
        this.distinct = true;
        return this;
    }

    public boolean hasPaging(){
        return this.paging != null;
    }

    public List<ProjectionJoin> getDeclaredJoins(){
        return ProjectionUtils.getDeclaredJoins(toClass);
    }

    public Path<?> resolvePath(Root<FROM> from, String path){
        return this.pathResolver.resolve(from, path);
    }
}
