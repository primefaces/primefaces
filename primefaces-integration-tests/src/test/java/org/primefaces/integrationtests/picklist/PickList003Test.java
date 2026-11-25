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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PickList003Test extends AbstractPrimePageTest {

    private static final String CUSTOM_LABEL = "Custom move to target";

    @Test
    @Order(1)
    @DisplayName("PickList: GitHub #15525 a PickList on the page gets the menu role and the labels of widgetPreConstruct")
    void pickListOnPage(Page page) {
        assertEquals("menu", page.pagePickList.getSourceList().getAttribute("role"));
        assertEquals(CUSTOM_LABEL, page.pagePickList.getAddButton().getAttribute("title"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("PickList: GitHub #15525 a PickList updated in a hidden dialog keeps the menu role and the labels of widgetPreConstruct")
    void pickListInDialog(Page page) {
        // Act
        openDialog(page);

        // Assert - the list role was removed because the items were not :visible while the dialog was hidden
        PickList pickList = page.dialogPickList;
        assertEquals("menu", pickList.getSourceList().getAttribute("role"));
        assertNull(pickList.getTargetList().getAttribute("role"));
        // the ajax update refreshes the widget, the labels of widgetPreConstruct must not be lost
        WebElement add = pickList.getAddButton();
        assertEquals(CUSTOM_LABEL, add.getAttribute("title"));
        assertEquals(CUSTOM_LABEL, add.getAttribute("textContent").trim());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("PickList: GitHub #15525 the focus stays in the PickList when moving all items disables the focused button")
    void moveAllKeepsFocus(Page page) {
        // Arrange
        openDialog(page);
        PickList pickList = page.dialogPickList;

        // Act
        pickList.getAddAllButton().sendKeys(Keys.ENTER);

        // Assert - not lost to the document body, the emptied source list cannot take it, so the target list does
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), pickList.getTargetListLabels());
        assertFalse(PrimeSelenium.isElementEnabled(pickList.getAddAllButton()));
        assertFocused(pickList.getTargetList());
        assertNull(pickList.getSourceList().getAttribute("role"));
        assertEquals("menu", pickList.getTargetList().getAttribute("role"));

        // Act - and back
        pickList.getRemoveAllButton().sendKeys(Keys.ENTER);

        // Assert
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), pickList.getSourceListLabels());
        assertFocused(pickList.getSourceList());
        assertEquals("menu", pickList.getSourceList().getAttribute("role"));
        assertNull(pickList.getTargetList().getAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("PickList: GitHub #15525 the focus moves to the source list, not to another button, after moving the selected item")
    void moveSelectedKeepsFocus(Page page) {
        // Arrange
        openDialog(page);
        PickList pickList = page.dialogPickList;
        pickList.select("Test 1");

        // Act
        pickList.getAddButton().sendKeys(Keys.ENTER);

        // Assert - pressing Enter again must not hit e.g. "Move All to Target"
        assertEquals(List.of("Test 1"), pickList.getTargetListLabels());
        assertFocused(pickList.getSourceList());
        assertEquals("menu", pickList.getTargetList().getAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("PickList: GitHub #15525 keyboard navigation keeps the focus indicator on the selected item")
    void keyboardNavigation(Page page) {
        // Arrange
        openDialog(page);
        PickList pickList = page.dialogPickList;
        WebElement source = tabToSourceList(page);

        // Act
        source.sendKeys(Keys.ARROW_DOWN);
        source.sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertTrue(pickList.isSelected("Test 2"), "second item must be selected");
        assertTrue(pickList.isFocused("Test 2"), "second item must have the focus indicator");
        assertFalse(pickList.isFocused("Test 1"));
        assertFalse(pickList.isFocused("Test 3"));
        assertFocused(source);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("PickList: GitHub #15525 the focus moves to the target list after moving all selected items and the transfer is announced")
    void moveAllSelectedKeepsFocus(Page page) {
        // Arrange
        openDialog(page);
        PickList pickList = page.dialogPickList;
        pickList.select("Test 1");
        pickList.toggleSelection("Test 2");
        pickList.toggleSelection("Test 3");

        // Act
        pickList.getAddButton().sendKeys(Keys.ENTER);

        // Assert - the source list is empty after the transfer, not while it runs
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), pickList.getTargetListLabels());
        assertFocused(pickList.getTargetList());
        assertEquals("menu", pickList.getTargetList().getAttribute("role"));
        // the announcement of the transfer is not replaced by the item the target list focuses, both are done when the
        // focused item has its indicator and the live region is filled
        WebElement ariaRegion = pickList.getAriaRegion();
        PrimeSelenium.waitGui().until(d -> pickList.isFocused("Test 1") && !ariaRegion.getAttribute("textContent").isEmpty());
        assertEquals("Moved to Target: 3", ariaRegion.getAttribute("textContent"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("PickList: GitHub #15525 keyboard navigation continues after moving an item with the Enter key")
    void keyboardNavigationAfterTransfer(Page page) {
        // Arrange
        openDialog(page);
        PickList pickList = page.dialogPickList;
        WebElement source = tabToSourceList(page);
        source.sendKeys(Keys.ARROW_DOWN);

        // Act
        source.sendKeys(Keys.ENTER);

        // Assert - the next item is selected right away, so Enter moves it as well
        assertEquals(List.of("Test 1"), pickList.getTargetListLabels());
        assertTrue(pickList.isSelected("Test 2"), "next item must be selected");
        assertTrue(pickList.isFocused("Test 2"), "next item must have the focus indicator");
        assertFalse(pickList.isSelected("Test 3"));
        assertFocused(source);

        // Act - Enter again
        source.sendKeys(Keys.ENTER);

        // Assert
        assertEquals(List.of("Test 1", "Test 2"), pickList.getTargetListLabels());
        assertFocused(source);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("PickList: GitHub #15525 moving the last item with the Enter key moves the focus to the other list")
    void keyboardTransferOfLastItem(Page page) {
        // Arrange
        openDialog(page);
        PickList pickList = page.dialogPickList;
        WebElement source = tabToSourceList(page);

        // Act - the first ArrowDown selects the focused item, after each transfer the next item is selected
        source.sendKeys(Keys.ARROW_DOWN);
        for (int i = 0; i < 3; i++) {
            source.sendKeys(Keys.ENTER);
        }

        // Assert - not left on the emptied source list, where the keys do nothing
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), pickList.getTargetListLabels());
        assertFocused(pickList.getTargetList());

        // Act - the keys work in the target list
        pickList.getTargetList().sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertTrue(pickList.isSelected("Test 1"), "first item of the target list must be selected");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(9)
    @DisplayName("PickList: GitHub #15525 an animated transfer by checkbox click updates the checkboxes, the selection and the roles")
    void animatedCheckboxTransfer(Page page) {
        // Arrange
        PickList pickList = page.checkboxPickList;

        // Act
        pickList.toggleCheckbox("Test 1");

        // Assert
        assertEquals(List.of("Test 2", "Test 3"), pickList.getSourceListLabels());
        assertEquals(List.of("Test 1"), pickList.getTargetListLabels());
        PrimeSelenium.waitGui().until(d -> "menu".equals(pickList.getTargetList().getAttribute("role")));
        assertTrue(pickList.isChecked("Test 1"), "checkbox must be checked");
        assertFalse(pickList.isSelected("Test 1"), "moved item must not be selected");
        assertEquals("menu", pickList.getSourceList().getAttribute("role"));

        // Act - and back
        pickList.toggleCheckbox("Test 1");

        // Assert
        assertEquals(List.of("Test 2", "Test 3", "Test 1"), pickList.getSourceListLabels());
        PrimeSelenium.waitGui().until(d -> pickList.getTargetList().getAttribute("role") == null);
        assertFalse(pickList.isChecked("Test 1"), "checkbox must be unchecked");
        assertFalse(pickList.isSelected("Test 1"), "moved item must not be selected");
        assertEquals("menu", pickList.getSourceList().getAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(10)
    @DisplayName("PickList: GitHub #15525 moving an item with the Enter key selects only its neighbour")
    void keyboardTransferSelectsNeighbour(Page page) {
        // Arrange - "Test 2" is the focused item, "Test 1" is selected as well
        openDialog(page);
        PickList pickList = page.dialogPickList;
        pickList.select("Test 1");
        pickList.toggleSelection("Test 2");
        WebElement source = pickList.getSourceList();
        assertFocused(source);

        // Act
        source.sendKeys(Keys.ENTER);

        // Assert - the item after the moved one, not the first item of the list, and not added to the selection
        assertEquals(List.of("Test 2"), pickList.getTargetListLabels());
        assertTrue(pickList.isSelected("Test 3"), "neighbour must be selected");
        assertTrue(pickList.isFocused("Test 3"), "neighbour must have the focus indicator");
        assertFalse(pickList.isSelected("Test 1"), "the former selection must be replaced");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(11)
    @DisplayName("PickList: GitHub #15525 the keys work in a list which got the focus while the moved items were hidden")
    void keyboardAfterTransferOfHiddenItems(Page page) {
        // Arrange - without an effect, the checked items are moved while they are hidden
        PickList pickList = page.noEffectPickList;
        pickList.toggleCheckbox("Test 1");
        pickList.toggleCheckbox("Test 2");
        pickList.toggleCheckbox("Test 3");

        // Act - the source list is empty, so the focus moves from the disabled button to the target list
        pickList.getAddButton().sendKeys(Keys.ENTER);
        assertEquals(List.of("Test 1", "Test 2", "Test 3"), pickList.getTargetListLabels());
        WebElement target = pickList.getTargetList();
        assertFocused(target);
        target.sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertTrue(pickList.isSelected("Test 1"), "first item must be selected");
        assertTrue(pickList.isFocused("Test 1"), "first item must have the focus indicator");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(12)
    @DisplayName("PickList: GitHub #15525 a mouse click on Move to Target keeps the scroll position of the source list")
    void mouseTransferKeepsScrollPosition(Page page) {
        // Arrange - the last item is selected at the bottom of the scrolled source list
        PickList pickList = page.scrollPickList;
        pickList.select("Item 20");
        WebElement source = pickList.getSourceList();
        assertTrue(scrollTop(source) > 0, "source list must be scrolled");

        // Act
        pickList.getAddButton().click();

        // Assert - the source list takes the focus, but is not scrolled back to its first item
        assertEquals(List.of("Item 20"), pickList.getTargetListLabels());
        assertFocused(source);
        PrimeSelenium.waitGui().until(d -> pickList.isFocused("Item 1"));
        assertTrue(scrollTop(source) > 0, "source list must keep its scroll position");
        assertNoJavascriptErrors();
    }

    private long scrollTop(WebElement element) {
        return ((Number) PrimeSelenium.executeScript("return arguments[0].scrollTop;", element)).longValue();
    }

    private void openDialog(Page page) {
        page.open.click();
        PrimeSelenium.waitGui().until(d -> page.dialog.isVisible());
        // the dialog focuses its first focusable element when it is shown
        assertFocused(page.dialog.getCloseButton());
    }

    /**
     * Moves the focus from the close icon of the dialog to the source list with the Tab key, like a keyboard user.
     */
    private WebElement tabToSourceList(Page page) {
        page.getWebDriver().switchTo().activeElement().sendKeys(Keys.TAB);
        WebElement source = page.dialogPickList.getSourceList();
        assertFocused(source);
        PrimeSelenium.waitGui().until(d -> page.dialogPickList.isFocused("Test 1"));
        return source;
    }

    private void assertFocused(WebElement element) {
        PrimeSelenium.waitGui().until(d -> element.equals(d.switchTo().activeElement()));
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

        @FindBy(id = "form:checkboxPickList")
        PickList checkboxPickList;

        @FindBy(id = "form:noEffectPickList")
        PickList noEffectPickList;

        @FindBy(id = "form:scrollPickList")
        PickList scrollPickList;

        @Override
        public String getLocation() {
            return "picklist/picklist003.xhtml";
        }
    }
}
