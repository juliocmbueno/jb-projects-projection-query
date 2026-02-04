package br.com.jbProjects.processor.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by julio.bueno on 04/02/2026.
 */
class ProjectionPagingTest {

    @Test
    public void pageNumber(){
        ProjectionPaging paging = new ProjectionPaging(0, 10);
        assertEquals(0, paging.pageNumber());

        paging = new ProjectionPaging(10, 10);
        assertEquals(1, paging.pageNumber());

        paging = new ProjectionPaging(25, 10);
        assertEquals(2, paging.pageNumber());

        paging = new ProjectionPaging(0, 0);
        assertEquals(0, paging.pageNumber());
    }

}
