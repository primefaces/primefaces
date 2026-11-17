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
package org.primefaces.integrationtests.picklist;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Dialog;
import org.primefaces.selenium.component.PickList;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PickList003Test extends AbstractPrimePageTest {

    private static final String CUSTOM_LABEL = "Custom move to target";

    @Test
    @Order(1)
    @DisplayName("PickList: GitHub #15525 a PickList on the page gets the menu role and the labels of clientPreConstruct")
    void pickListOnPage(Page page) {
        assertEquals("menu", source(page.pagePickList).getAttribute("role"));
        assertEquals(CUSTOM_LABEL, addButton(page.pagePickList).getAttribute("title"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("PickList: GitHub #15525 a PickList updated in a hidden dialog keeps the menu role and the labels of clientPreConstruct")
    void pickListInDialog(Page page) {
        // Act
        openDialog(page);

        // Assert - the list role was removed because the items were not :visible while the dialog was hidden
        assertEquals("menu", source(page.dialogPickList).getAttribute("role"));
        assertNull(target(page.dialogPickList).getAttribute("role"));
        // the ajax update refreshes the component, clientPreConstruct must be called for it as well
        WebElement add = addButton(page.dialogPickList);
        assertEquals(CUSTOM_LABEL, add.getAttribute("title"));
        assertEquals(CUSTOM_LABEL, add.findElement(By.className("p-button-text")).getAttribute("textContent"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("PickList: GitHub #15525 the focus stays in the PickList when moving all items disables the focused button")
    void moveAllKeepsFocus(Page page) {
        // Arrange
        openDialog(page);
        WebElement addAll = page.dialogPickList.findElement(By.className("p-picklist-button-add-all"));
        WebElement removeAll = page.dialogPickList.findElement(By.className("p-picklist-button-remove-all"));
        PrimeSelenium.executeScript("arguments[0].focus();", addAll);

        // Act
        addAll.sendKeys(Keys.ENTER);

        // Assert - not lost to the document body, the emptied source list cannot take it, so the target list does
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), page.dialogPickList.getTargetListLabels());
        assertTrue(addAll.getAttribute("class").contains("p-state-disabled"));
        assertTrue(isActive(target(page.dialogPickList)), "target list must have the focus");
        assertNull(source(page.dialogPickList).getAttribute("role"));
        assertEquals("menu", target(page.dialogPickList).getAttribute("role"));

        // Act - and back
        PrimeSelenium.executeScript("arguments[0].focus();", removeAll);
        removeAll.sendKeys(Keys.ENTER);

        // Assert
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), page.dialogPickList.getSourceListLabels());
        assertTrue(isActive(source(page.dialogPickList)), "source list must have the focus");
        assertEquals("menu", source(page.dialogPickList).getAttribute("role"));
        assertNull(target(page.dialogPickList).getAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("PickList: GitHub #15525 the focus moves to the source list, not to another button, after moving the selected item")
    void moveSelectedKeepsFocus(Page page) {
        // Arrange
        openDialog(page);
        WebElement add = addButton(page.dialogPickList);
        PrimeSelenium.executeScript("arguments[0].click();", source(page.dialogPickList).findElements(By.tagName("li")).get(0));
        PrimeSelenium.executeScript("arguments[0].focus();", add);

        // Act
        add.sendKeys(Keys.ENTER);

        // Assert - pressing Enter again must not hit e.g. "Move All to Target"
        assertEquals(List.of("Test 1"), page.dialogPickList.getTargetListLabels());
        assertTrue(isActive(source(page.dialogPickList)), "source list must have the focus");
        assertEquals("menu", target(page.dialogPickList).getAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("PickList: GitHub #15525 keyboard navigation keeps the focus indicator on the selected item")
    void keyboardNavigation(Page page) {
        // Arrange
        openDialog(page);
        WebElement source = source(page.dialogPickList);
        // the dialog focuses its close icon, the source list is next
        new Actions(page.getWebDriver()).sendKeys(Keys.TAB).perform();
        assertTrue(isActive(source), "source must have the focus");
        PrimeSelenium.waitGui().until(d -> !source.findElements(By.className("p-picklist-outline")).isEmpty());

        // Act
        source.sendKeys(Keys.ARROW_DOWN);
        source.sendKeys(Keys.ARROW_DOWN);

        // Assert
        List<WebElement> items = source.findElements(By.tagName("li"));
        WebElement second = items.get(1);
        assertTrue(second.getAttribute("class").contains("p-state-highlight"), "second item must be selected");
        assertTrue(second.getAttribute("class").contains("p-picklist-outline"), "second item must have the focus indicator");
        assertEquals(1, source.findElements(By.className("p-picklist-outline")).size());
        assertTrue(isActive(source), "source must have the focus");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("PickList: GitHub #15525 the focus moves to the target list after moving all selected items and the transfer is announced")
    void moveAllSelectedKeepsFocus(Page page) {
        // Arrange
        openDialog(page);
        List<WebElement> items = source(page.dialogPickList).findElements(By.tagName("li"));
        new Actions(page.getWebDriver()).click(items.get(0)).keyDown(Keys.SHIFT).click(items.get(2)).keyUp(Keys.SHIFT).perform();
        WebElement add = addButton(page.dialogPickList);
        PrimeSelenium.executeScript("arguments[0].focus();", add);

        // Act
        add.sendKeys(Keys.ENTER);

        // Assert - the source list is empty after the transfer, not while it runs
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), page.dialogPickList.getTargetListLabels());
        assertTrue(isActive(target(page.dialogPickList)), "target list must have the focus");
        assertEquals("menu", target(page.dialogPickList).getAttribute("role"));
        // the announcement of the transfer is not replaced by the item the target list focuses
        PrimeSelenium.wait(300);
        assertEquals("Moved to Target", page.dialogPickList.getAriaRegion().getAttribute("textContent"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("PickList: GitHub #15525 keyboard navigation continues after moving an item with the Enter key")
    void keyboardNavigationAfterTransfer(Page page) {
        // Arrange
        openDialog(page);
        WebElement source = source(page.dialogPickList);
        new Actions(page.getWebDriver()).sendKeys(Keys.TAB).perform();
        assertTrue(isActive(source), "source must have the focus");
        PrimeSelenium.waitGui().until(d -> !source.findElements(By.className("p-picklist-outline")).isEmpty());
        source.sendKeys(Keys.ARROW_DOWN);

        // Act
        source.sendKeys(Keys.ENTER);
        source.sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertEquals(List.of("Test 1"), page.dialogPickList.getTargetListLabels());
        WebElement next = source.findElements(By.tagName("li")).get(0);
        assertEquals("Test 2", next.getAttribute("data-item-label"));
        assertTrue(next.getAttribute("class").contains("p-state-highlight"), "next item must be selected");
        assertTrue(next.getAttribute("class").contains("p-picklist-outline"), "next item must have the focus indicator");
        assertTrue(isActive(source), "source must keep the focus");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("PickList: GitHub #15525 moving the last item with the Enter key moves the focus to the other list")
    void keyboardTransferOfLastItem(Page page) {
        // Arrange
        openDialog(page);
        WebElement source = source(page.dialogPickList);
        new Actions(page.getWebDriver()).sendKeys(Keys.TAB).perform();
        assertTrue(isActive(source), "source must have the focus");
        PrimeSelenium.waitGui().until(d -> !source.findElements(By.className("p-picklist-outline")).isEmpty());

        // Act
        for (int i = 0; i < 3; i++) {
            source.sendKeys(Keys.ARROW_DOWN);
            source.sendKeys(Keys.ENTER);
        }

        // Assert - not left on the emptied source list, where the keys do nothing
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), page.dialogPickList.getTargetListLabels());
        assertTrue(isActive(target(page.dialogPickList)), "target list must have the focus");
        assertNoJavascriptErrors();
    }

    private void openDialog(Page page) {
        page.open.click();
        PrimeSelenium.waitGui().until(d -> page.dialog.isVisible());
        // the dialog focuses its first focusable element when it is shown
        PrimeSelenium.waitGui().until(d -> isActive(page.dialog.findElement(By.className("p-dialog-titlebar-close"))));
    }

    private boolean isActive(WebElement element) {
        return Boolean.TRUE.equals(PrimeSelenium.executeScript("return document.activeElement === arguments[0];", element));
    }

    private WebElement source(PickList pickList) {
        return pickList.findElement(By.cssSelector("ul.p-picklist-source"));
    }

    private WebElement target(PickList pickList) {
        return pickList.findElement(By.cssSelector("ul.p-picklist-target"));
    }

    private WebElement addButton(PickList pickList) {
        return pickList.findElement(By.className("p-picklist-button-add"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:open")
        CommandButton open;

        @FindBy(id = "form:dialog")
        Dialog dialog;

        @FindBy(id = "form:pagePickList")
        PickList pagePickList;

        @FindBy(id = "form:dialogPickList")
        PickList dialogPickList;

        @Override
        public String getLocation() {
            return "picklist/picklist003.xhtml";
        }
    }
}
