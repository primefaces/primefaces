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
package org.primefaces.integrationtests.timeline;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Timeline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GitHub #15167: the timeline puts the event it renders in the request map under its var and the group under its
 * varGroup, so neither may outlive the render.
 */
class Timeline004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Timeline: GitHub #15167 var and varGroup do not leak into what renders after the timeline")
    void varsDoNotLeakAfterRender(Page page) {
        // Arrange
        assertTrue(page.timeline.isDisplayed());

        // Assert
        assertEquals("afterEvent=[]", page.afterEvent.getText());
        assertEquals("afterGroup=[]", page.afterGroup.getText());

        // Act
        PrimeSelenium.guardAjax(page.buttonUpdate).click();

        // Assert
        assertEquals("afterEvent=[]", page.afterEvent.getText());
        assertEquals("afterGroup=[]", page.afterGroup.getText());

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:timeline")
        Timeline timeline;

        @FindBy(id = "form:afterEvent")
        WebElement afterEvent;

        @FindBy(id = "form:afterGroup")
        WebElement afterGroup;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "timeline/timeline004.xhtml";
        }
    }
}
