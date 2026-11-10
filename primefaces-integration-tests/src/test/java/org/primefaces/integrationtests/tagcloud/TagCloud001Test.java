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
package org.primefaces.integrationtests.tagcloud;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TagCloud;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class TagCloud001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TagCloud: tags with strength")
    void tags(Page page) {
        // Assert
        TagCloud tagCloud = page.tagCloud;
        assertEquals(Arrays.asList("Faces", "PrimeFaces", "Java"), tagCloud.getLabels());
        assertEquals(1, tagCloud.getStrength("Faces"));
        assertEquals(5, tagCloud.getStrength("PrimeFaces"));
        assertEquals(3, tagCloud.getStrength("Java"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TagCloud: select event via AJAX")
    void select(Page page) {
        // Arrange
        TagCloud tagCloud = page.tagCloud;
        assertTrue(tagCloud.isSelectAjaxified());

        // Act
        tagCloud.select("Java");

        // Assert
        assertEquals("Selected", page.messages.getMessage(0).getSummary());
        assertEquals("Java", page.messages.getMessage(0).getDetail());

        // Act
        tagCloud.select("PrimeFaces");

        // Assert
        assertEquals("PrimeFaces", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:tagCloud")
        TagCloud tagCloud;

        @Override
        public String getLocation() {
            return "tagcloud/tagCloud001.xhtml";
        }
    }
}
