package br.com.jbProjects.processor.query;

import br.com.jbProjects.validations.ProjectionValidations;
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

    private final List<ProjectionSpecification<FROM>> specifications = new ArrayList<>();

    private boolean distinct = false;
    private ProjectionPaging paging;

    private ProjectionQuery(Class<FROM> fromClass, Class<TO> toClass) {
        ProjectionValidations.validateProjectionClass(toClass);
        this.fromClass = fromClass;
        this.toClass = toClass;
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

    public ProjectionQuery<FROM, TO> distinct(){
        this.distinct = true;
        return this;
    }

    public boolean hasPaging(){
        return this.paging != null;
    }
}
