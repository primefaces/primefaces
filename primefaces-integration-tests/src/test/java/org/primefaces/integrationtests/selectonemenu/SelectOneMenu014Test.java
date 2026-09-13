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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SelectOneMenu014Test extends AbstractPrimePageTest {

    /** The localized default of the {@code nullLabel} ARIA label. */
    private static final String NULL_LABEL = "Not Selected";

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #15221 an item with an empty label is announced with the nullLabel ARIA label")
    void emptyItemIsAnnounced(Page page) {
        // Arrange
        page.menu.show();

        // Act
        WebElement emptyItem = page.menu.getItems().findElement(By.id("form:selectonemenu_0"));
        WebElement jaItem = page.menu.getItems().findElement(By.id("form:selectonemenu_1"));

        // Assert - the blank item carries an accessible name, the labelled ones are announced by their text
        assertEquals(NULL_LABEL, emptyItem.getDomAttribute("aria-label"));
        assertNull(jaItem.getDomAttribute("aria-label"));
        assertEquals("Ja", jaItem.getText());

        page.menu.hide();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #15222 selecting the empty item must not keep announcing the previous item")
    void emptySelectionIsAnnounced(Page page) {
        // Arrange
        WebElement label = page.menu.getLabel();
        page.menu.select("Ja");
        assertEquals("Ja", label.getDomAttribute("aria-label"));

        // Act - select the empty item the way a user does
        page.menu.show();
        page.menu.getItems().findElement(By.id("form:selectonemenu_0")).click();

        // Assert - the stale "Ja" must be gone
        assertEquals(NULL_LABEL, label.getDomAttribute("aria-label"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #15222 with a placeholder the placeholder is announced for the empty item")
    void emptySelectionAnnouncesPlaceholder(Page page) {
        // Arrange
        WebElement label = page.placeholder.getLabel();
        page.placeholder.select("Nein");
        assertEquals("Nein", label.getDomAttribute("aria-label"));

        // Act
        page.placeholder.show();
        page.placeholder.getItems().findElement(By.id("form:placeholder_0")).click();

        // Assert - the visible text is the placeholder, so that is what must be announced
        assertEquals("Choose One", label.getText());
        assertEquals("Choose One", label.getDomAttribute("aria-label"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: GitHub #15221 a label which is only blank after escaping is announced too")
    void blankItemLabelIsAnnounced(Page page) {
        // Arrange
        page.blankLabel.show();

        // Act
        WebElement blankItem = page.blankLabel.getItems().findElement(By.id("form:blankLabel_0"));

        // Assert
        assertEquals(NULL_LABEL, blankItem.getDomAttribute("aria-label"));

        // Act - selecting it must announce the same, not the previously selected item
        blankItem.click();

        // Assert
        assertEquals(NULL_LABEL, page.blankLabel.getLabel().getDomAttribute("aria-label"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: GitHub #15221 an empty label on a noSelectionOption item is announced too")
    void emptyNoSelectionOptionIsAnnounced(Page page) {
        // Arrange
        page.noSelection.show();

        // Act
        WebElement emptyItem = page.noSelection.getItems().findElement(By.id("form:noSelection_0"));

        // Assert
        assertEquals(NULL_LABEL, emptyItem.getDomAttribute("aria-label"));

        // Act
        emptyItem.click();

        // Assert
        assertEquals(NULL_LABEL, page.noSelection.getLabel().getDomAttribute("aria-label"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("SelectOneMenu: hideNoSelectionOption drops the empty item, so there is nothing to announce")
    void hiddenNoSelectionOptionIsNotRendered(Page page) {
        // Arrange
        page.hideNoSelection.show();

        // Act
        List<WebElement> items = page.hideNoSelection.getItems().findElements(By.cssSelector("li[role='option']"));

        // Assert - the empty no-selection item is not offered at all, the labelled ones are untouched
        assertEquals(List.of("Ja", "Nein"), items.stream().map(WebElement::getText).toList());
        assertNull(items.get(0).getDomAttribute("aria-label"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu menu;

        @FindBy(id = "form:placeholder")
        SelectOneMenu placeholder;

        @FindBy(id = "form:blankLabel")
        SelectOneMenu blankLabel;

        @FindBy(id = "form:noSelection")
        SelectOneMenu noSelection;

        @FindBy(id = "form:hideNoSelection")
        SelectOneMenu hideNoSelection;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu014.xhtml";
        }
    }
}
