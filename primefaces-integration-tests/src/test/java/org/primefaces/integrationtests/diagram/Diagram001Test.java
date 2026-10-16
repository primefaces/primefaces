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
package org.primefaces.integrationtests.diagram;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandLink;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Diagram001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Diagram: GitHub #14626 action of a CommandLink inside the element facet must be executed")
    void commandLinkInElementFacet(Page page) {
        // Arrange
        assertNoJavascriptErrors();

        // Act
        page.linkElementA.click();

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("Selected", page.messages.getMessage(0).getSummary());
        assertEquals("A", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Diagram: GitHub #14626 each element has its own CommandLink")
    void commandLinkInElementFacetSecondElement(Page page) {
        // Arrange
        assertNoJavascriptErrors();

        // Act
        page.linkElementB.click();

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("Selected", page.messages.getMessage(0).getSummary());
        assertEquals("B", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Diagram: a CommandLink outside of the diagram is not affected")
    void commandLinkOutside(Page page) {
        // Arrange
        assertNoJavascriptErrors();

        // Act
        page.linkOutside.click();

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("Outside", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:diagram:0:link")
        CommandLink linkElementA;

        @FindBy(id = "form:diagram:1:link")
        CommandLink linkElementB;

        @FindBy(id = "form:linkOutside")
        CommandLink linkOutside;

        @FindBy(id = "form:messages")
        Messages messages;

        @Override
        public String getLocation() {
            return "diagram/diagram001.xhtml";
        }
    }

}
