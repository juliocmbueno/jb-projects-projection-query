package br.com.jbProjects.processor.pageable;

import br.com.jbProjects.processor.query.ProjectionPaging;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by julio.bueno on 04/02/2026.
 */
class ProjectionPageTest {

    @Test
    public void create(){
        ProjectionPage<String> page = new ProjectionPage<>(List.of("a", "b", "c"), 100, 0, 10);
        assertEquals(3, page.content().size());
        assertEquals(100, page.totalElements());
        assertEquals(0, page.pageNumber());
        assertEquals(10, page.pageSize());
    }

    @Test
    public void createWithNullContent(){
        ProjectionPage<String> page = new ProjectionPage<>(null, 100, 0, 10);
        assertNotNull(page.content());
        assertTrue(page.content().isEmpty());
    }

    @Test
    public void totalPages(){
        ProjectionPage<String> page = new ProjectionPage<>(List.of("a", "b", "c"), 100, 0, 10);
        assertEquals(10, page.totalPages());

        ProjectionPage<String> page2 = new ProjectionPage<>(List.of("a", "b", "c"), 95, 0, 10);
        assertEquals(10, page2.totalPages());

        ProjectionPage<String> page3 = new ProjectionPage<>(List.of("a", "b", "c"), 101, 0, 10);
        assertEquals(11, page3.totalPages());

        ProjectionPage<String> page4 = new ProjectionPage<>(List.of("a", "b", "c"), 0, 0, 10);
        assertEquals(0, page4.totalPages());

        ProjectionPage<String> page5 = new ProjectionPage<>(List.of("a", "b", "c"), 0, 0, 0);
        assertEquals(0, page5.totalPages());
    }

    @Test
    public void hasNext(){
        ProjectionPage<String> page = new ProjectionPage<>(List.of("a", "b", "c"), 100, 0, 10);
        assertTrue(page.hasNext());

        ProjectionPage<String> lastPage = new ProjectionPage<>(List.of("a", "b", "c"), 100, 9, 10);
        assertFalse(lastPage.hasNext());
    }

    @Test
    public void hasPrevious(){
        ProjectionPage<String> page = new ProjectionPage<>(List.of("a", "b", "c"), 100, 0, 10);
        assertFalse(page.hasPrevious());

        ProjectionPage<String> secondPage = new ProjectionPage<>(List.of("a", "b", "c"), 100, 1, 10);
        assertTrue(secondPage.hasPrevious());
    }

    @Test
    public void isEmpty(){
        ProjectionPage<String> page = new ProjectionPage<>(List.of("a", "b", "c"), 100, 0, 10);
        assertFalse(page.isEmpty());

        ProjectionPage<String> emptyPage = new ProjectionPage<>(List.of(), 100, 0, 10);
        assertTrue(emptyPage.isEmpty());

        ProjectionPage<String> nullContentPage = new ProjectionPage<>(null, 100, 0, 10);
        assertTrue(nullContentPage.isEmpty());
    }

    @Test
    public void empty(){
        ProjectionPage<String> emptyPage = ProjectionPage.empty();
        assertNotNull(emptyPage.content());
        assertTrue(emptyPage.content().isEmpty());
        assertEquals(0, emptyPage.totalElements());
        assertEquals(0, emptyPage.pageNumber());
        assertEquals(0, emptyPage.pageSize());
    }

    @Test
    public void emptyWithPaging(){
        ProjectionPaging paging = new ProjectionPaging(1, 20);
        ProjectionPage<String> emptyPage = ProjectionPage.empty(paging);
        assertNotNull(emptyPage.content());
        assertTrue(emptyPage.content().isEmpty());
        assertEquals(0, emptyPage.totalElements());
        assertEquals(0, emptyPage.pageNumber());
        assertEquals(paging.size(), emptyPage.pageSize());
    }

    @Test
    public void ofWithFirstAndSize(){
        ProjectionPage<String> page = ProjectionPage.of(List.of("a", "b", "c"), 100, 0, 10);
        assertEquals(3, page.content().size());
        assertEquals(100, page.totalElements());
        assertEquals(0, page.pageNumber());
        assertEquals(10, page.pageSize());
    }

    @Test
    public void ofWithPaging(){
        ProjectionPage<String> page = ProjectionPage.of(List.of("a", "b", "c"), 100, new ProjectionPaging(0, 10));
        assertEquals(3, page.content().size());
        assertEquals(100, page.totalElements());
        assertEquals(0, page.pageNumber());
        assertEquals(10, page.pageSize());
    }

}
