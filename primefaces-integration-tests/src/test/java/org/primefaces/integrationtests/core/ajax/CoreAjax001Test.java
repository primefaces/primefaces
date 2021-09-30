/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.core.ajax;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.SelectOneMenu;

public class CoreAjax001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Core-AJAX: keep vertical scroll-position after ajax-update - https://github.com/primefaces/primefaces/issues/6700")
    public void testAjaxScrollPosition(Page page) {
        if (PrimeSelenium.isSafari()) {
            System.out.println(
                        "Test disabled on Safari until Safari fixes the bug: https://github.com/primefaces-extensions/primefaces-integration-tests/issues/100");
            return;
        }
        // Arrange
        Long scrollTop = PrimeSelenium.executeScript("return $(document).scrollTop();");
        Assertions.assertEquals(0, scrollTop);
        PrimeSelenium.executeScript("$(document).scrollTop(200);");

        // Act
        PrimeSelenium.disableAnimations();
        page.selectOneMenu.select(2);

        // Assert
        scrollTop = PrimeSelenium.executeScript("return $(document).scrollTop();");
        Assertions.assertEquals(200, scrollTop);
        assertNoJavascriptErrors();

        // Act
        page.button1.click();

        // Assert
        scrollTop = PrimeSelenium.executeScript("return $(document).scrollTop();");
        Assertions.assertEquals(200, scrollTop);
        assertNoJavascriptErrors();

        // Act
        page.button2.click(); //does PrimeFaces.current().focus("form:inputtext"); --> vertical scroll position changes

        // Assert
        //PrimeFaces.focus() has a 50ms setTimeout() so we need to delay here
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(page.inputtext.getInput()));

        PrimeSelenium.enableAnimations();
        Assertions.assertEquals(page.inputtext.getInput().getAttribute("id"), page.getWebDriver().switchTo().activeElement().getAttribute("id"));
        scrollTop = PrimeSelenium.executeScript("return $(document).scrollTop();");
        Assertions.assertTrue(scrollTop > 200);
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputtext")
        InputText inputtext;

        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:button1")
        CommandButton button1;

        @FindBy(id = "form:button2")
        CommandButton button2;

        @Override
        public String getLocation() {
            return "core/ajax/coreAjax001.xhtml";
        }
    }
}
