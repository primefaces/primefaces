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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.html.SelectOneMenu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTable040Test extends AbstractDataTableTest {

    @Test
    @DisplayName("p:dataTable: Asign null value")
    void p(Page page) throws InterruptedException {
        SelectOneMenu menu = PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:datatable:4:price"));

        menu.select("Sold");

        assertEquals("Sold", menu.getSelectedLabel());
    }

    @Test
    @DisplayName("h:dataTable: Asign null value")
    void h(Page page) throws InterruptedException {
        SelectOneMenu menu = PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:hDatatable:4:price"));

        menu.select("Sold");

        assertEquals("Sold", menu.getSelectedLabel());
    }

    public static class Page extends AbstractPrimePage {

        @Override
        public String getLocation() {
            return "datatable/dataTable040.xhtml";
        }
    }
}
