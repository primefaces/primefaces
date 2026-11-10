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
package org.primefaces.integrationtests.datascroller;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.DataScroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataScroller001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataScroller: inline mode loads the next chunk when scrolled to the end")
    void inline(Page page) {
        // Arrange
        DataScroller scroller = page.inline;
        assertEquals(25, scroller.getTotalSize());
        assertEquals(10, scroller.getItems().size());

        // Act
        scroller.loadMore();

        // Assert
        assertEquals(20, scroller.getItems().size());

        // Act
        scroller.loadMore();

        // Assert
        assertEquals(25, scroller.getItems().size());
        assertEquals("Item 25", scroller.getItems().get(24).getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataScroller: the loader button loads the next chunk")
    void manual(Page page) {
        // Arrange
        DataScroller scroller = page.manual;
        assertEquals(10, scroller.getItems().size());

        // Act
        scroller.loadMore();

        // Assert
        assertEquals(20, scroller.getItems().size());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:inline")
        DataScroller inline;

        @FindBy(id = "form:manual")
        DataScroller manual;

        @Override
        public String getLocation() {
            return "datascroller/dataScroller001.xhtml";
        }
    }
}
