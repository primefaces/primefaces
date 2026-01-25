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
import org.primefaces.selenium.component.CommandButton;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreHead003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Core: HeadRenderer ensure moment.js is not loaded twice on component load")
    void headRendererOnDynamicComponentLoad(Page page) {
        // Arrange
        WebElement head = page.head;
        WebElement body = page.body;

        // Chart.js is not included on page load because the component doesn't exist
        // Moment.js is included on page load because of CSV enabled
        boolean isChartJsIncluded = false;
        assertHeader(head, body, isChartJsIncluded);

        // Act
        page.button.click();

        // Assert
        // Chart.js is included on page load because the component exists
        // Moment.js is not duplicated because it's already loaded
        isChartJsIncluded = true;
        assertHeader(head, body, isChartJsIncluded);
    }

    private void assertHeader(WebElement head, WebElement body, boolean isChartJsIncluded) {
        List<WebElement> headElements = head.findElements(By.tagName("*"));
        List<WebElement> headScriptElements = head.findElements(By.cssSelector("script[src]"));
        List<WebElement> scriptElements = body.findElements(By.cssSelector("script[src]"));
        int expectedHeadElementsSize = isChartJsIncluded ? 8 : 7;
        assertEquals(expectedHeadElementsSize, headElements.size(), "Header elements not expected size");

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

        WebElement componentsCss = headElements.get(3);
        assertEquals(componentsCss.getTagName(), "link");
        assertTrue(componentsCss.getDomAttribute("href").contains("components.css"));

        // Title
        WebElement titleTag = headElements.get(4);
        assertEquals("title", titleTag.getTagName());

         // <f:facet name="last">
        WebElement webMeta = headElements.get(5);
        assertEquals(webMeta.getTagName(), "meta");
        assertEquals(webMeta.getDomAttribute("name"), "mobile-web-app-capable");
        assertEquals(webMeta.getDomAttribute("content"), "yes");

        WebElement inlineStyle = headElements.get(6);
        assertEquals("style", inlineStyle.getTagName());

        if (isChartJsIncluded) {
            WebElement chartJs = headElements.get(7);
            assertEquals("script", chartJs.getTagName());
            assertTrue(chartJs.getDomAttribute("src").contains("chart/chart.js"));
        }

        // Scripts in order
        System.out.println("Head Script sources: " + headScriptElements.size());
        for (WebElement script : headScriptElements) {
            System.out.println(script.getDomAttribute("src"));
        }
        System.out.println("Body Script sources: " + scriptElements.size());
        for (WebElement script : scriptElements) {
            System.out.println(script.getDomAttribute("src"));
        }
        assertTrue(scriptElements.size() >= 6, "Script elements not expected size");
        assertTrue(scriptElements.get(0).getDomAttribute("src").contains("jquery/jquery.js"));
        assertTrue(scriptElements.get(1).getDomAttribute("src").contains("jquery/jquery-plugins.js"));
        assertTrue(scriptElements.get(2).getDomAttribute("src").contains("core.js"));
        assertTrue(scriptElements.get(3).getDomAttribute("src").contains("components.js"));
        assertTrue(scriptElements.get(4).getDomAttribute("src").contains("moment/moment.js"));
        assertTrue(scriptElements.get(5).getDomAttribute("src").contains("locales/locale-en.js"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(tagName = "head")
        WebElement head;

        @FindBy(tagName = "body")
        WebElement body;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "core/head/head003.xhtml";
        }
    }
}
