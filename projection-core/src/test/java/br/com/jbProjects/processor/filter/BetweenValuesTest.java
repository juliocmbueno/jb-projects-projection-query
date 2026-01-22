package br.com.jbProjects.processor.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by julio.bueno on 12/01/2026.
 */
class BetweenValuesTest {

    @Test
    public void create(){
        BetweenValues betweenValues = BetweenValues.of(1, 10);
        Assertions.assertEquals(1, betweenValues.start());
        Assertions.assertEquals(10, betweenValues.end());
    }

    @Test
    public void create_withStartValueNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> BetweenValues.of(null, 10));
    }

    @Test
    public void create_withEndValueNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> BetweenValues.of(10, null));
    }

}
