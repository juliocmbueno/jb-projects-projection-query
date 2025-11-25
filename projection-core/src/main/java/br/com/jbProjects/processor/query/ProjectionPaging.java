package br.com.jbProjects.processor.query;

/**
 * Created by julio.bueno on 21/11/2025.
 * <p>Represents pagination parameters for projections.</p>
 * @param first The index of the first record to retrieve.
 * @param size The maximum number of records to retrieve.
 */
public record ProjectionPaging(int first, int size) {
}
