package br.com.jbProjects.processor.order;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

public enum OrderDirection {
    ASC {
        @Override
        public Order toOrder(CriteriaBuilder cb, Path<?> path) {
            return cb.asc(path);
        }
    },
    DESC {
        @Override
        public Order toOrder(CriteriaBuilder cb, Path<?> path) {
            return cb.desc(path);
        }
    };

    public abstract Order toOrder(CriteriaBuilder cb, Path<?> path);
}
