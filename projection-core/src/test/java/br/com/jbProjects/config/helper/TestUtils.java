package br.com.jbProjects.config.helper;

import org.junit.jupiter.api.Assertions;

/**
 * Created by julio.bueno on 18/03/2026.
 */
public class TestUtils {

    private TestUtils() {
        // Private constructor to prevent instantiation
    }

    public static void assertIgnoreWhitespace(String expected, String actual) {
        String normalizedExpected = expected.replaceAll("\\s+", " ").trim();
        String normalizedActual = actual.replaceAll("\\s+", " ").trim();
        Assertions.assertEquals(normalizedExpected, normalizedActual);
    }
}
