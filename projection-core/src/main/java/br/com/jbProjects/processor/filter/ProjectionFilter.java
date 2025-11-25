package br.com.jbProjects.processor.filter;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public record ProjectionFilter(
        String path,
        String operator,
        Object value
) {
}
