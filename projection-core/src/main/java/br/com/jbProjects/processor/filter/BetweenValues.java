package br.com.jbProjects.processor.filter;

/**
 * Created by julio.bueno on 25/11/2025.
 * <p>
 *     Represents a range with a start and end value for "between" comparisons.
 * </p>
 *
 * @param start The start value of the range.
 * @param end   The end value of the range.
 */
public record BetweenValues(
        Comparable<?> start,
        Comparable<?> end
) {
    /**
     * Constructor that validates the presence of both start and end values.
     * @param start The start value of the range.
     * @param end The end value of the range.
     * @throws IllegalArgumentException if either start or end is null.
     */
    public BetweenValues {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Between requires both start and end");
        }
    }

    /**
     * Factory method to create a BetweenValues instance.
     *
     * @param start The start value of the range.
     * @param end   The end value of the range.
     * @return A new BetweenValues instance.
     */
    public static BetweenValues of(Comparable<?> start, Comparable<?> end){
        return new BetweenValues(start, end);
    }
}
