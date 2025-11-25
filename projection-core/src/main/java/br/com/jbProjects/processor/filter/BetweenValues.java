package br.com.jbProjects.processor.filter;

/**
 * Created by julio.bueno on 25/11/2025.
 */
public record BetweenValues(
        Comparable<?> start,
        Comparable<?> end
) {
    public BetweenValues {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Between requires both start and end");
        }
    }

    public static BetweenValues of(Comparable<?> start, Comparable<?> end){
        return new BetweenValues(start, end);
    }
}
