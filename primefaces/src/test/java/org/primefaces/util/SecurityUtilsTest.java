package org.primefaces.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.mock.FacesContextMock;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilsTest {

    private ExternalContext externalContext;

    @BeforeEach
    public void setup() {
        FacesContext context = spy(FacesContextMock.class);
        externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
    }

    @AfterEach
    public void teardown() {
        externalContext = null;
    }

    @Test
    public void testIfGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        assertFalse(SecurityUtils.ifGranted("A"));

        when(externalContext.isUserInRole("B")).thenReturn(true);
        assertTrue(SecurityUtils.ifGranted("B"));
    }

    @Test
    public void testIfAllGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertFalse(SecurityUtils.ifAllGranted("A,B"));

        when(externalContext.isUserInRole("A")).thenReturn(true);
        when(externalContext.isUserInRole("B")).thenReturn(true);
        assertTrue(SecurityUtils.ifAllGranted("A,B"));

        when(externalContext.isUserInRole("A")).thenReturn(true);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertFalse(SecurityUtils.ifAllGranted("A,B"));
    }

    @Test
    public void testIfAnyGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertFalse(SecurityUtils.ifAnyGranted("A,B"));

        when(externalContext.isUserInRole("A")).thenReturn(true);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertTrue(SecurityUtils.ifAnyGranted("A,B"));
    }

    @Test
    public void testIfNoneGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertTrue(SecurityUtils.ifNoneGranted("A,B"));

        when(externalContext.isUserInRole("A")).thenReturn(true);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertFalse(SecurityUtils.ifNoneGranted("A,B"));
    }

    @Test
    public void testConvertRoles() {
        Consumer<Stream<String>> assertStream = s -> {
            assertNotNull(s);
            List<String> roles = s.collect(Collectors.toList());
            assertEquals(2, roles.size());
            assertEquals(Arrays.asList("A", "B"), roles);
        };

        // String
        Stream<String> stream = SecurityUtils.convertRoles("   A , B   ");
        assertStream.accept(stream);

        // String array
        stream = SecurityUtils.convertRoles(new String[] {"A", "B"});
        assertStream.accept(stream);

        // Object array
        stream = SecurityUtils.convertRoles(new Object[] {"A", "B"});
        assertStream.accept(stream);

        // Object array
        stream = SecurityUtils.convertRoles(new Object[] {"A", "B"});
        assertStream.accept(stream);

        // Collection
        stream = SecurityUtils.convertRoles(Arrays.asList("A", "B"));
        assertStream.accept(stream);

        // Null parameter
        assertThrows(NullPointerException.class, () -> SecurityUtils.convertRoles(null));
    }
}
