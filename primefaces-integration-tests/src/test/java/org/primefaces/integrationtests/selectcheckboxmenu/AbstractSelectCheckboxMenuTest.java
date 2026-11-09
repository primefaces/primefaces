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
package org.primefaces.integrationtests.selectcheckboxmenu;

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.SelectCheckboxMenu;

import org.json.JSONObject;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractSelectCheckboxMenuTest extends AbstractPrimePageTest {

    protected void assertSelected(SelectCheckboxMenu menu, int count) {
        assertEquals(count, menu.getSelectedCheckboxes().size());
    }

    protected void assertValue(WebElement checkbox, String value) {
        assertEquals(value, checkbox.getAttribute("value"));
    }

    protected void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertFalse(cfg.getBoolean("dynamic"));
        assertTrue(cfg.getBoolean("showHeader"));
        assertEquals("@(body)", cfg.get("appendTo"));
        System.out.println("SelectCheckboxMenu Config = " + cfg);
    }
}
