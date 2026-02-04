package br.com.jbProjects.processor.query;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Represents pagination parameters for projections.</p>
 * @param first The index of the first record to retrieve.
 * @param size The maximum number of records to retrieve.
 */
public record ProjectionPaging(int first, int size) {

    /**
     * Calculates the current page number based on the first record index and page size.
     *
     * @return The current page number (0-based).
     */
    public int pageNumber() {
        return size > 0 ? first / size : 0;
    }
}
