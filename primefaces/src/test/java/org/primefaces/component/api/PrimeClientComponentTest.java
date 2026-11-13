/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.api;

import org.primefaces.component.panel.Panel;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PrimeClientComponentTest {

    private final FacesContext context = mock(FacesContext.class);

    @Test
    void resolveRef() {
        Panel panel = new Panel();
        panel.setRef("myPanel");

        assertEquals("myPanel", panel.resolveRef(context));
    }

    @Test
    void resolveDeprecatedWidgetVar() {
        Panel panel = new Panel();
        panel.setWidgetVar("myPanel");

        assertEquals("myPanel", panel.resolveRef(context));
    }

    @Test
    void refWinsOverWidgetVar() {
        Panel panel = new Panel();
        panel.setRef("byRef");
        panel.setWidgetVar("byWidgetVar");

        assertEquals("byRef", panel.resolveRef(context));
    }
}
