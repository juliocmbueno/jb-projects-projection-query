package br.com.jbProjects.spring.configTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by julio.bueno on 27/11/2025.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
public abstract class BaseSpringBootTest{

    @BeforeAll
    void beforeAll(){
        onBeforeClass();
    }

    public void onBeforeClass() {}

    @AfterAll
    void afterAll(){
        onAfterClass();
    }

    public void onAfterClass() {}

}
