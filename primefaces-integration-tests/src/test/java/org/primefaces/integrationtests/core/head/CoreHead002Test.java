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

public class CoreHead002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Core: HeadRenderer ensure moment.js is not loaded twice on page load")
    void headRendererInitalPageLoad(Page page) {
        // Arrange
        WebElement head = page.head;
        WebElement body = page.body;
        List<WebElement> headElements = head.findElements(By.tagName("*"));
        List<WebElement> headScriptElements = head.findElements(By.cssSelector("script[src]"));
        List<WebElement> scriptElements = body.findElements(By.cssSelector("script[src]"));

        // Act & Assert
        assertEquals(6, headElements.size());
        // <f:facet name="first">
        WebElement viewportMeta = headElements.get(0);
        assertEquals("meta", viewportMeta.getTagName());
        assertEquals("viewport", viewportMeta.getDomAttribute("name"));
        assertEquals("width=device-width, initial-scale=1.0", viewportMeta.getDomAttribute("content"));

        // CSS in order
        WebElement themeCss = headElements.get(1);
        assertEquals("link", themeCss.getTagName());
        assertTrue(themeCss.getDomAttribute("href").contains("theme.css"));

        WebElement primeiconsCss = headElements.get(2);
        assertEquals("link", primeiconsCss.getTagName());
        assertTrue(primeiconsCss.getDomAttribute("href").contains("primeicons.css"));

        // Title
        WebElement titleTag = headElements.get(3);
        assertEquals("title", titleTag.getTagName());


        // <f:facet name="last">
        WebElement webMeta = headElements.get(4);
        assertEquals("meta", webMeta.getTagName());
        assertEquals("mobile-web-app-capable", webMeta.getDomAttribute("name"));
        assertEquals("yes", webMeta.getDomAttribute("content"));

        // Scripts in order
        System.out.println("Head Script sources: " + headScriptElements.size());
        for (WebElement script : headScriptElements) {
            System.out.println(script.getDomAttribute("src"));
        }
        System.out.println("Body Script sources: " + scriptElements.size());
        for (WebElement script : scriptElements) {
            System.out.println(script.getDomAttribute("src"));
        }
        assertEquals(6, scriptElements.size());
        assertTrue(scriptElements.get(0).getDomAttribute("src").contains("jquery/jquery.js"));
        assertTrue(scriptElements.get(1).getDomAttribute("src").contains("core.js"));
        assertTrue(scriptElements.get(2).getDomAttribute("src").contains("components.js"));
        assertTrue(scriptElements.get(3).getDomAttribute("src").contains("moment/moment.js"));
        assertTrue(scriptElements.get(4).getDomAttribute("src").contains("chart/chart.js"));
        assertTrue(scriptElements.get(5).getDomAttribute("src").contains("locales/locale-en.js"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(tagName = "head")
        WebElement head;

        @FindBy(tagName = "body")
        WebElement body;

        @Override
        public String getLocation() {
            return "core/head/head002.xhtml";
        }
    }
}
