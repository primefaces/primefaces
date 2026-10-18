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
package org.primefaces.component.panel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PanelTest {

    private Panel panel;

    @BeforeEach
    void setup() {
        panel = new Panel();
    }

    @Test
    void defaultIcons() {
        assertEquals(Panel.DEFAULT_CLOSE_ICON, panel.getCloseIcon());
        assertEquals(Panel.DEFAULT_COLLAPSED_ICON, panel.getCollapsedIcon());
        assertEquals(Panel.DEFAULT_EXPANDED_ICON, panel.getExpandedIcon());
        assertEquals(Panel.DEFAULT_MENU_ICON, panel.getMenuIcon());
    }

    @Test
    void customIcons() {
        panel.setCloseIcon("pi pi-times");
        panel.setCollapsedIcon("pi pi-chevron-down");
        panel.setExpandedIcon("pi pi-chevron-up");
        panel.setMenuIcon("pi pi-bars");

        assertEquals("pi pi-times", panel.getCloseIcon());
        assertEquals("pi pi-chevron-down", panel.getCollapsedIcon());
        assertEquals("pi pi-chevron-up", panel.getExpandedIcon());
        assertEquals("pi pi-bars", panel.getMenuIcon());
    }
}
