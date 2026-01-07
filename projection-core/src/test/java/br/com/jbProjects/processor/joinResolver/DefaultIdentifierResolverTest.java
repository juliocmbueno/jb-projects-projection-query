package br.com.jbProjects.processor.joinResolver;

import br.com.jbProjects.config.helper.ReflectionTestUtils;
import br.com.jbProjects.config.testModel.customer.domain.Customer;
import br.com.jbProjects.config.testModel.customer.domain.CustomerWithCodeId;
import br.com.jbProjects.config.testModel.customer.domain.CustomerWithMethodId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by julio.bueno on 07/01/2026.
 */
class DefaultIdentifierResolverTest {

    @Test
    public void isIdentifier_byConvention() {
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        boolean result = resolver.isIdentifier(Customer.class, "id");
        assertTrue(result, "'id' should be recognized as an identifier by convention");

        result = resolver.isIdentifier(Customer.class, "name");
        assertFalse(result, "'name' should be not recognized as an identifier");
    }

    @Test
    public void isIdentifier_byAnnotation() {
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        boolean result = resolver.isIdentifier(CustomerWithCodeId.class, "code");
        assertTrue(result, "'code' should be recognized as an identifier by @Id annotation");

        result = resolver.isIdentifier(CustomerWithCodeId.class, "email");
        assertFalse(result, "'email' should be not recognized as an identifier");
    }

    @Test
    public void isIdentifier_byMethodAnnotation() {
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        boolean result = resolver.isIdentifier(CustomerWithMethodId.class, "code");
        assertTrue(result, "'code' should be recognized as an identifier by @Id annotation on getter method");

        result = resolver.isIdentifier(CustomerWithMethodId.class, "name");
        assertFalse(result, "'name' should be not recognized as an identifier");
    }

    @Test
    public void propertyName_startWithGet(){
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        Method method = Mockito.mock(Method.class);
        Mockito.doReturn("getId").when(method).getName();

        String result = ReflectionTestUtils.invokeMethod(resolver, "propertyName", method);
        assertEquals("id", result);
    }

    @Test
    public void propertyName_startWithIs(){
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        Method method = Mockito.mock(Method.class);
        Mockito.doReturn("isActive").when(method).getName();

        String result = ReflectionTestUtils.invokeMethod(resolver, "propertyName", method);
        assertEquals("active", result);
    }

    @Test
    public void propertyName_other() {
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        Method method = Mockito.mock(Method.class);
        Mockito.doReturn("calculateValue").when(method).getName();

        String result = ReflectionTestUtils.invokeMethod(resolver, "propertyName", method);
        assertEquals("calculateValue", result);
    }

    @Test
    public void isAnnotatedWithId_IdClass() {
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        Method method = Mockito.mock(Method.class);
        Mockito.doReturn(true).when(method).isAnnotationPresent(jakarta.persistence.Id.class);

        boolean result = ReflectionTestUtils.invokeMethod(resolver, "isAnnotatedWithId", method);
        assertTrue(result);
    }

    @Test
    public void isAnnotatedWithId_EmbeddedIdClass() {
        DefaultIdentifierResolver resolver = new DefaultIdentifierResolver();

        Method method = Mockito.mock(Method.class);
        Mockito.doReturn(true).when(method).isAnnotationPresent(jakarta.persistence.EmbeddedId.class);

        boolean result = ReflectionTestUtils.invokeMethod(resolver, "isAnnotatedWithId", method);
        assertTrue(result);
    }
}
