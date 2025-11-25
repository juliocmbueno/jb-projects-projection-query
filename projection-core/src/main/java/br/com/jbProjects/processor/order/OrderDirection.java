package br.com.jbProjects.processor.order;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Enumeration representing the direction of ordering in projection queries.</p>
 */
public enum OrderDirection {
    /** Ascending order direction */
    ASC {
        @Override
        public Order toOrder(CriteriaBuilder cb, Path<?> path) {
            return cb.asc(path);
        }
    },

    /** Descending order direction */
    DESC {
        @Override
        public Order toOrder(CriteriaBuilder cb, Path<?> path) {
            return cb.desc(path);
        }
    };

    /**
     * <p>Converts the OrderDirection to a JPA Order object.</p>
     *
     * @param cb CriteriaBuilder used to create the Order
     * @param path Target property path
     * @return JPA Order object representing the ordering direction
     */
    public abstract Order toOrder(CriteriaBuilder cb, Path<?> path);
}
