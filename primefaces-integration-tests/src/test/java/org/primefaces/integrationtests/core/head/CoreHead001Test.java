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
package org.primefaces.integrationtests.core.head;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreHead001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Core: HeadRenderer ensure order of scripts and prevent duplicates")
    void headRendererOrdering(Page page) {
        // Arrange
        WebElement head = page.head;
        WebElement body = page.body;
        List<WebElement> headElements = head.findElements(By.tagName("*"));
        List<WebElement> scriptElements = body.findElements(By.cssSelector("script[src]"));
        // Act & Assert

        // <f:facet name="first">
        WebElement viewportMeta = headElements.get(0);
        assertEquals(viewportMeta.getTagName(), "meta");
        assertEquals(viewportMeta.getDomAttribute("name"), "viewport");
        assertEquals(viewportMeta.getDomAttribute("content"), "width=device-width, initial-scale=1.0");

        // CSS in order
        WebElement themeCss = headElements.get(1);
        assertEquals(themeCss.getTagName(), "link");
        assertTrue(themeCss.getDomAttribute("href").contains("theme.css"));

        WebElement primeiconsCss = headElements.get(2);
        assertEquals(primeiconsCss.getTagName(), "link");
        assertTrue(primeiconsCss.getDomAttribute("href").contains("primeicons.css"));

        WebElement componentsCss = headElements.get(3);
        assertEquals(componentsCss.getTagName(), "link");
        assertTrue(componentsCss.getDomAttribute("href").contains("components.css"));

        // Title
        WebElement titleTag =  headElements.get(4);
        assertEquals(titleTag.getTagName(), "title");

        // <f:facet name="last">
        WebElement webMeta = headElements.get(5);
        assertEquals(webMeta.getTagName(), "meta");
        assertEquals(webMeta.getDomAttribute("name"), "mobile-web-app-capable");
        assertEquals(webMeta.getDomAttribute("content"), "yes");

        // Scripts in order
        assertEquals(10, scriptElements.size());
        assertTrue(scriptElements.get(0).getDomAttribute("src").contains("jquery/jquery.js"));
        assertTrue(scriptElements.get(1).getDomAttribute("src").contains("jquery/jquery-plugins.js"));
        assertTrue(scriptElements.get(2).getDomAttribute("src").contains("core.js"));
        assertTrue(scriptElements.get(3).getDomAttribute("src").contains("components.js"));
        assertTrue(scriptElements.get(4).getDomAttribute("src").contains("inputmask/inputmask.js"));
        assertTrue(scriptElements.get(5).getDomAttribute("src").contains("datepicker/datepicker.js"));
        assertTrue(scriptElements.get(6).getDomAttribute("src").contains("locales/locale-de.js"));
        assertTrue(scriptElements.get(7).getDomAttribute("src").contains("moment/moment.js"));
        assertTrue(scriptElements.get(8).getDomAttribute("src").contains("locales/locale-en.js"));
        assertTrue(scriptElements.get(9).getDomAttribute("src").contains("locales/locale-fr.js"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(tagName = "head")
        WebElement head;

        @FindBy(tagName = "body")
        WebElement body;

        @Override
        public String getLocation() {
            return "core/head/head001.xhtml";
        }
    }
}
