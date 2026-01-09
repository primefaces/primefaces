/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.util;

import org.primefaces.mock.FacesContextMock;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilsTest {

    private ExternalContext externalContext;

    @BeforeEach
    void setup() {
        FacesContext context = spy(FacesContextMock.class);
        externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
    }

    @AfterEach
    void teardown() {
        externalContext = null;
    }

    @Test
    void ifGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        assertFalse(SecurityUtils.ifGranted("A"));

        when(externalContext.isUserInRole("B")).thenReturn(true);
        assertTrue(SecurityUtils.ifGranted("B"));
    }

    @Test
    void ifAllGranted() {
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
    void ifAnyGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertFalse(SecurityUtils.ifAnyGranted("A,B"));

        when(externalContext.isUserInRole("A")).thenReturn(true);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertTrue(SecurityUtils.ifAnyGranted("A,B"));
    }

    @Test
    void ifNoneGranted() {
        when(externalContext.isUserInRole("A")).thenReturn(false);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertTrue(SecurityUtils.ifNoneGranted("A,B"));

        when(externalContext.isUserInRole("A")).thenReturn(true);
        when(externalContext.isUserInRole("B")).thenReturn(false);
        assertFalse(SecurityUtils.ifNoneGranted("A,B"));
    }

    @Test
    void convertRoles() {
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
