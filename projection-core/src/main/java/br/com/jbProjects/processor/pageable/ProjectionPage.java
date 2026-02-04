package br.com.jbProjects.processor.pageable;

import br.com.jbProjects.processor.query.ProjectionPaging;

import java.util.List;

/**
 * Created by julio.bueno on 04/02/2026.
 * <p>Represents a paginated result of a {@link br.com.jbProjects.processor.query.ProjectionQuery} execution.</p>
 * <p>
 * This class encapsulates the content of the current page along with
 * pagination metadata such as total number of elements, page size,
 * current page number and navigation helpers.
 * </p>
 *
 * <p>
 * The pagination model is <strong>offset-based</strong>, meaning the page number
 * is derived from the {@code first} (offset) and {@code size} parameters
 * defined in {@link ProjectionPaging}. The page index is zero-based.
 * </p>
 *
 * <p>
 * Unlike Spring Data {@code Page}, this abstraction is framework-agnostic
 * and does not depend on Spring APIs, making it suitable for use in
 * core and infrastructure layers.
 * </p>
 *
 * @param <T> the type of the content elements
 *
 * @param content the list of elements in the current page (never {@code null})
 * @param totalElements the total number of elements matching the query
 * @param pageNumber the current page number (0-based)
 * @param pageSize the maximum number of elements per page
 */
public record ProjectionPage<T>(
        List<T> content,
        long totalElements,
        int pageNumber,
        int pageSize
) {

    /**
     * Constructs a ProjectionPage ensuring content is never null.
     *
     * @param content the list of elements in the current page
     * @param totalElements the total number of elements matching the query
     * @param pageNumber the current page number (0-based)
     * @param pageSize the maximum number of elements per page
     */
    public ProjectionPage {
        content = content == null ? List.of() : content;
    }

    /**
     * Calculates the total number of pages based on total elements and page size.
     *
     * @return The total number of pages.
     */
    public int totalPages() {
        return pageSize > 0
                ? (int) Math.ceil((double) totalElements / pageSize)
                : 0;
    }

    /**
     * Checks if there is a next page.
     *
     * @return true if there are more elements beyond the current page, false otherwise.
     */
    public boolean hasNext() {
        return (long) (pageNumber + 1) * pageSize < totalElements;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if the current page number is greater than 0, false otherwise.
     */
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    /**
     * Checks if the page content is empty.
     *
     * @return true if the content is empty, false otherwise.
     */
    public boolean isEmpty() {
        return content.isEmpty();
    }

    /**
     * Creates an empty ProjectionPage with default paging parameters (page number 0 and page size 0).
     *
     * @param <T> The type of the content.
     * @return An empty ProjectionPage.
     */
    public static <T> ProjectionPage<T> empty() {
        return new ProjectionPage<>(
                List.of(),
                0,
                0,
                0
        );
    }

    /**
     * Creates an empty ProjectionPage with the specified paging parameters.
     *
     * @param paging The paging parameters.
     * @param <T> The type of the content.
     * @return An empty ProjectionPage.
     */
    public static <T> ProjectionPage<T> empty(ProjectionPaging paging) {
        return new ProjectionPage<>(
                List.of(),
                0,
                paging.pageNumber(),
                paging.size()
        );
    }

    /**
     * Creates a ProjectionPage with the specified content, total elements, and paging parameters.
     *
     * @param content The list of content items.
     * @param totalElements The total number of elements across all pages.
     * @param first The offset of the first element in the current page.
     * @param size The maximum number of elements per page.
     * @param <T> The type of the content.
     * @return A ProjectionPage containing the specified content and pagination information.
     */
    public static <T> ProjectionPage<T> of(List<T> content, long totalElements, int first, int size) {
        return of(
                content,
                totalElements,
                new ProjectionPaging(first, size)
        );
    }

    /**
     * Creates a ProjectionPage with the specified content, total elements, and paging parameters.
     *
     * @param content The list of content items.
     * @param totalElements The total number of elements across all pages.
     * @param paging The paging parameters.
     * @param <T> The type of the content.
     * @return A ProjectionPage containing the specified content and pagination information.
     */
    public static <T> ProjectionPage<T> of(List<T> content, long totalElements, ProjectionPaging paging) {
        return new ProjectionPage<>(
                content,
                totalElements,
                paging.pageNumber(),
                paging.size()
        );
    }
}
