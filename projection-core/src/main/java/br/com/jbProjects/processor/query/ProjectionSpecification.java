package br.com.jbProjects.processor.query;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Created by julio.bueno on 21/11/2025.
 */
public interface ProjectionSpecification<FROM> {

    Predicate toPredicate(
            CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Tuple> query,
            Root<FROM> root
    );

}
