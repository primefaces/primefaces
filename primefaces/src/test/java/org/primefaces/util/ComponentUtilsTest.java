/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.Arrays;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.primefaces.component.accordionpanel.AccordionPanel;

public class ComponentUtilsTest {

    @Test
    public void escapeSelector() {
        String id = "test";

        assertEquals("test", ComponentUtils.escapeSelector(id));

        id = "form:test";
        assertEquals("form\\\\:test", ComponentUtils.escapeSelector(id));
    }

    @Test
    public void createContentDisposition() {
        assertEquals("attachment;filename=\"Test%20Spaces.txt\"; filename*=UTF-8''Test%20Spaces.txt", ComponentUtils.createContentDisposition("attachment", "Test Spaces.txt"));
    }

    @Test
    public void shouldRenderFacet_nullFacet() {
        Assertions.assertFalse(ComponentUtils.shouldRenderFacet(null));
    }

    @Test
    public void shouldRenderFacet_facetRenderedFalse() {
        UIComponent component = mock(AccordionPanel.class);
        when(component.isRendered()).thenReturn(false);
        Assertions.assertFalse(ComponentUtils.shouldRenderFacet(component));
    }

    @Test
    public void shouldRenderFacet_facetSingleChild() {
        UIComponent component = mock(AccordionPanel.class);
        when(component.isRendered()).thenReturn(true);
        Assertions.assertTrue(ComponentUtils.shouldRenderFacet(component));
    }

    @Test
    public void shouldRenderFacet_facetChildrenRenderedFalse() {
        UIComponent children = mock(AccordionPanel.class);
        when(children.isRendered()).thenReturn(false);

        UIComponent facet = new UIPanel();
        facet.setRendered(true);
        facet.getChildren().add(children);

        Assertions.assertFalse(ComponentUtils.shouldRenderFacet(facet));
    }

    @Test
    public void shouldRenderFacet_facetChildrenRenderedTrue() {
        UIComponent children = mock(AccordionPanel.class);
        when(children.isRendered()).thenReturn(true);

        UIComponent facet = new UIPanel();
        facet.setRendered(true);
        facet.getChildren().add(children);

        Assertions.assertTrue(ComponentUtils.shouldRenderFacet(facet));
    }

    @Test
    public void shouldRenderFacet_facetChildrenFacetRenderedFalse() {
        UIComponent children = mock(AccordionPanel.class);
        when(children.isRendered()).thenReturn(true);

        UIComponent facet = new UIPanel();
        facet.setRendered(false);
        facet.getChildren().add(children);

        Assertions.assertFalse(ComponentUtils.shouldRenderFacet(facet));
    }
}
