package br.com.jbProjects.processor.filter.handler;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Interface for handlers that generate Predicate for different filter operations in projection queries</p>
 */
public interface ProjectionFilterOperatorHandler {

    Predicate toPredicate(CriteriaBuilder cb, Path<?> path, Object value);

}
