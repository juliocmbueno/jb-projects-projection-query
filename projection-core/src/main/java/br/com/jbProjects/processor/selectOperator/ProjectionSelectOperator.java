package br.com.jbProjects.processor.selectOperator;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>Enumeration of select operators that can be used in ProjectionSelect.</p>
 */
public enum ProjectionSelectOperator {
    /**
     * Count operator.
     * <p>equivalent to "COUNT(path)"</p>
     */
    COUNT,

    /**
     * Minimum operator.
     * <p>equivalent to "MIN(path)"</p>
     */
    MIN,

    /**
     * Maximum operator.
     * <p>equivalent to "MAX(path)"</p>
     */
    MAX,

    /**
     * Sum operator.
     * <p>equivalent to "SUM(path)"</p>
     */
    SUM
}
