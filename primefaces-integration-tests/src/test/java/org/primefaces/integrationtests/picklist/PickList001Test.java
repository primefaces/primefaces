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
package org.primefaces.integrationtests.picklist;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.PickList;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class PickList001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("PickList: pick 2 items by label")
    void pickByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};

        // Act
        page.standardPickList.pick(valuesToPick);

        // Assert
        assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListLabels());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("PickList: pick 2 items by value")
    void pickByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};

        // Act
        page.standardPickList.pick(valuesToPick);

        // Assert
        assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListValues());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("PickList: pick 2 items by label using checkboxes")
    void checkPickByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};

        // Act
        page.checkPickList.pick(valuesToPick);

        // Assert
        assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListLabels());
        assertConfiguration(page.checkPickList.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("PickList: pick 2 items by value using checkboxes")
    void checkPickByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};

        // Act
        page.checkPickList.pick(valuesToPick);

        // Assert
        assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListValues());
        assertConfiguration(page.checkPickList.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("PickList: pick 2 items by label using instant checkboxes")
    void instantPickByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};

        // Act
        page.instantPickList.pick(valuesToPick);

        // Assert
        assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListLabels());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("PickList: pick 2 items by value using instant checkboxes")
    void instantPickByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};

        // Act
        page.instantPickList.pick(valuesToPick);

        // Assert
        assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListValues());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("PickList: remove 2 items by label")
    void removeByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};
        page.standardPickList.pick(valuesToPick);
        assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListLabels());

        // Act
        page.standardPickList.remove(valuesToPick);

        // Assert
        assertTrue(page.standardPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("PickList: remove 2 items by value")
    void removeByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};
        page.standardPickList.pick(valuesToPick);
        assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListValues());

        // Act
        page.standardPickList.remove(valuesToPick);

        // Assert
        assertTrue(page.standardPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("PickList: remove 2 items by label using checkboxes")
    void checkRemoveByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};
        page.checkPickList.pick(valuesToPick);
        assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListLabels());

        // Act
        page.checkPickList.remove(valuesToPick);

        // Assert
        assertTrue(page.checkPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("PickList: remove 2 items by value using checkboxes")
    void checkRemoveByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};
        page.checkPickList.pick(valuesToPick);
        assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListValues());

        // Act
        page.checkPickList.remove(valuesToPick);

        // Assert
        assertTrue(page.checkPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("PickList: remove 2 items by label using instant checkboxes")
    void instantRemoveByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};
        page.instantPickList.pick(valuesToPick);
        assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListLabels());

        // Act
        page.instantPickList.remove(valuesToPick);

        // Assert
        assertTrue(page.instantPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("PickList: remove 2 items by value using instant checkboxes")
    void instantRemoveByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};
        page.instantPickList.pick(valuesToPick);
        assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListValues());

        // Act
        page.instantPickList.remove(valuesToPick);

        // Assert
        assertTrue(page.instantPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(13)
    @DisplayName("PickList: pick all items")
    void pickAll(final Page page) {
        // Arrange
        assertFalse(page.standardPickList.getSourceListElements().isEmpty());

        // Act
        page.standardPickList.pickAll();

        // Assert
        assertTrue(page.standardPickList.getSourceListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(14)
    @DisplayName("PickList: remove all items")
    void removeAll(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        assertTrue(page.standardPickList.getSourceListElements().isEmpty());
        assertFalse(page.standardPickList.getTargetListElements().isEmpty());

        // Act
        page.standardPickList.removeAll();

        // Assert
        assertTrue(page.standardPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(15)
    @DisplayName("PickList: move up 1 item in the source list")
    void moveSourceItemsUp(final Page page) {
        // Arrange
        final List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        assertFalse(sourceLabels.isEmpty());
        assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsUp("Max");

        // Assert
        assertEquals("Max", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(16)
    @DisplayName("PickList: move down 1 item in the source list")
    void moveSourceItemsDown(final Page page) {
        // Arrange
        final List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        assertFalse(sourceLabels.isEmpty());
        assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsDown("Lewis");

        // Assert
        assertEquals("Max", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(17)
    @DisplayName("PickList: move 1 item in the source list to the top")
    void moveSourceItemsToTop(final Page page) {
        // Arrange
        final List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        assertFalse(sourceLabels.isEmpty());
        assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsToTop("Lando");

        // Assert
        assertEquals("Lando", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(18)
    @DisplayName("PickList: move 1 item in the source list to the bottom")
    void moveSourceItemsToBottom(final Page page) {
        // Arrange
        List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        assertFalse(sourceLabels.isEmpty());
        assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsToBottom("Lewis");

        // Assert
        sourceLabels = page.standardPickList.getSourceListLabels();
        assertEquals("Lewis", sourceLabels.get(sourceLabels.size() - 1));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(19)
    @DisplayName("PickList: move up 1 item in the target list")
    void moveTargetItemsUp(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        final List<String> targetLabels = page.standardPickList.getTargetListLabels();
        assertFalse(targetLabels.isEmpty());
        assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsUp("Max");

        // Assert
        assertEquals("Max", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(20)
    @DisplayName("PickList: move down 1 item in the target list")
    void moveTargetItemsDown(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        final List<String> targetLabels = page.standardPickList.getTargetListLabels();
        assertFalse(targetLabels.isEmpty());
        assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsDown("Lewis");

        // Assert
        assertEquals("Max", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(21)
    @DisplayName("PickList: move 1 item in the target list to the top")
    void moveTargetItemsToTop(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        final List<String> targetLabels = page.standardPickList.getTargetListLabels();
        assertFalse(targetLabels.isEmpty());
        assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsToTop("Lando");

        // Assert
        assertEquals("Lando", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(22)
    @DisplayName("PickList: move 1 item in the target list to the bottom")
    void moveTargetItemsToBottom(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        List<String> targetLabels = page.standardPickList.getTargetListLabels();
        assertFalse(targetLabels.isEmpty());
        assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsToBottom("Lewis");

        // Assert
        targetLabels = page.standardPickList.getTargetListLabels();
        assertEquals("Lewis", targetLabels.get(targetLabels.size() - 1));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(23)
    @DisplayName("PickList: filter the source list with the text 'Charles'")
    void filterSource(final Page page) {
        // Act
        page.standardPickList.filterSourceList("Charles");

        // Assert
        assertEquals(1, page.standardPickList.getSourceListElements().size());
        assertEquals("Charles", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(24)
    @DisplayName("PickList: clear the source list filter")
    void clearSourceFilter(final Page page) {
        // Arrange
        page.standardPickList.filterSourceList("Charles");
        assertEquals(1, page.standardPickList.getSourceListElements().size());
        assertEquals("Charles", page.standardPickList.getSourceListLabels().get(0));

        // Act
        page.standardPickList.clearSourceListFilter();

        // Assert
        assertEquals(4, page.standardPickList.getSourceListElements().size());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(25)
    @DisplayName("PickList: filter the target list with the text 'Charles'")
    void filterTarget(final Page page) {
        // Arrange
        page.standardPickList.pickAll();

        // Act
        page.standardPickList.filterTargetList("Charles");

        // Assert
        assertEquals(1, page.standardPickList.getTargetListElements().size());
        assertEquals("Charles", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(26)
    @DisplayName("PickList: clear the filter from the target list")
    void clearTargetFilter(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        page.standardPickList.filterTargetList("Charles");
        assertEquals(1, page.standardPickList.getTargetListElements().size());
        assertEquals("Charles", page.standardPickList.getTargetListLabels().get(0));

        // Act
        page.standardPickList.clearTargetListFilter();

        // Assert
        assertEquals(4, page.standardPickList.getTargetListElements().size());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    private JSONObject assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Picklist Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.getBoolean("transferOnDblclick"));
        return cfg;
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:standardPickList")
        PickList standardPickList;

        @FindBy(id = "form:checkPickList")
        PickList checkPickList;

        @FindBy(id = "form:instantPickList")
        PickList instantPickList;

        @FindBy(id = "form:btnSubmit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "picklist/picklist001.xhtml";
        }
    }

}
