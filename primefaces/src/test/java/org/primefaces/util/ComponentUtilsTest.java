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

import org.primefaces.component.accordionpanel.AccordionPanel;

import jakarta.faces.component.TransientStateHelper;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ComponentUtilsTest {

    @Test
    void escapeSelector() {
        String id = "test";

        assertEquals("test", ComponentUtils.escapeSelector(id));

        id = "form:test";
        assertEquals("form\\\\:test", ComponentUtils.escapeSelector(id));
    }

    @Test
    void createContentDisposition() {
        assertEquals("attachment;filename=\"Test%20Spaces.txt\"; filename*=UTF-8''Test%20Spaces.txt",
                ComponentUtils.createContentDisposition("attachment", "Test Spaces.txt"));
    }

    @Test
    public void shouldRenderFacet_nullFacet() {
        assertFalse(FacetUtils.shouldRenderFacet(null));
    }

    @Test
    void shouldRenderFacet_facetRenderedFalse() {
        UIComponent component = mock(AccordionPanel.class);
        when(component.isRendered()).thenReturn(false);
        assertFalse(FacetUtils.shouldRenderFacet(component));
    }

    @Test
    void shouldRenderFacet_facetSingleChild() {
        UIComponent component = mock(AccordionPanel.class);
        when(component.isRendered()).thenReturn(true);
        assertTrue(FacetUtils.shouldRenderFacet(component));
    }

    @Test
    void shouldRenderFacet_facetChildrenRenderedFalse() {
        UIComponent children = mock(AccordionPanel.class);
        when(children.isRendered()).thenReturn(false);
        when(children.getTransientStateHelper()).thenReturn(new TransientStateHelperMock());

        UIComponent facet = new UIPanel();
        facet.setRendered(true);
        facet.getChildren().add(children);

        assertFalse(FacetUtils.shouldRenderFacet(facet));
    }

    @Test
    void shouldRenderFacet_facetChildrenRenderedTrue() {
        UIComponent children = mock(AccordionPanel.class);
        when(children.isRendered()).thenReturn(true);
        when(children.getTransientStateHelper()).thenReturn(new TransientStateHelperMock());

        UIComponent facet = new UIPanel();
        facet.setRendered(true);
        facet.getChildren().add(children);

        assertTrue(FacetUtils.shouldRenderFacet(facet));
    }

    @Test
    void shouldRenderFacet_facetChildrenFacetRenderedFalse() {
        UIComponent children = mock(AccordionPanel.class);
        when(children.isRendered()).thenReturn(true);
        when(children.getTransientStateHelper()).thenReturn(new TransientStateHelperMock());

        UIComponent facet = new UIPanel();
        facet.setRendered(false);
        facet.getChildren().add(children);

        assertFalse(FacetUtils.shouldRenderFacet(facet));
    }

    class TransientStateHelperMock implements TransientStateHelper {

        @Override
        public Object getTransient(Object o) {
            return null;
        }

        @Override
        public Object getTransient(Object o, Object o1) {
            return null;
        }

        @Override
        public Object putTransient(Object o, Object o1) {
            return null;
        }

        @Override
        public Object saveTransientState(FacesContext facesContext) {
            return null;
        }

        @Override
        public void restoreTransientState(FacesContext facesContext, Object o) {

        }
    }
}
