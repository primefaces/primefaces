/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.PickList;

public class PickList001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("PickList: pick 2 items by label")
    public void testPickByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};

        // Act
        page.standardPickList.pick(valuesToPick);

        // Assert
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListLabels());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("PickList: pick 2 items by value")
    public void testPickByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};

        // Act
        page.standardPickList.pick(valuesToPick);

        // Assert
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListValues());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("PickList: pick 2 items by label using checkboxes")
    public void testCheckPickByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};

        // Act
        page.checkPickList.pick(valuesToPick);

        // Assert
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListLabels());
        assertConfiguration(page.checkPickList.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("PickList: pick 2 items by value using checkboxes")
    public void testCheckPickByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};

        // Act
        page.checkPickList.pick(valuesToPick);

        // Assert
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListValues());
        assertConfiguration(page.checkPickList.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("PickList: pick 2 items by label using instant checkboxes")
    public void testInstantPickByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};

        // Act
        page.instantPickList.pick(valuesToPick);

        // Assert
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListLabels());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("PickList: pick 2 items by value using instant checkboxes")
    public void testInstantPickByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};

        // Act
        page.instantPickList.pick(valuesToPick);

        // Assert
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListValues());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("PickList: remove 2 items by label")
    public void testRemoveByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};
        page.standardPickList.pick(valuesToPick);
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListLabels());

        // Act
        page.standardPickList.remove(valuesToPick);

        // Assert
        Assertions.assertTrue(page.standardPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("PickList: remove 2 items by value")
    public void testRemoveByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};
        page.standardPickList.pick(valuesToPick);
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.standardPickList.getTargetListValues());

        // Act
        page.standardPickList.remove(valuesToPick);

        // Assert
        Assertions.assertTrue(page.standardPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("PickList: remove 2 items by label using checkboxes")
    public void testCheckRemoveByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};
        page.checkPickList.pick(valuesToPick);
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListLabels());

        // Act
        page.checkPickList.remove(valuesToPick);

        // Assert
        Assertions.assertTrue(page.checkPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("PickList: remove 2 items by value using checkboxes")
    public void testCheckRemoveByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};
        page.checkPickList.pick(valuesToPick);
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.checkPickList.getTargetListValues());

        // Act
        page.checkPickList.remove(valuesToPick);

        // Assert
        Assertions.assertTrue(page.checkPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("PickList: remove 2 items by label using instant checkboxes")
    public void testInstantRemoveByLabel(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Lewis", "Max"};
        page.instantPickList.pick(valuesToPick);
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListLabels());

        // Act
        page.instantPickList.remove(valuesToPick);

        // Assert
        Assertions.assertTrue(page.instantPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("PickList: remove 2 items by value using instant checkboxes")
    public void testInstantRemoveByValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"1", "2"};
        page.instantPickList.pick(valuesToPick);
        Assertions.assertEquals(Arrays.asList(valuesToPick), page.instantPickList.getTargetListValues());

        // Act
        page.instantPickList.remove(valuesToPick);

        // Assert
        Assertions.assertTrue(page.instantPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.instantPickList.getWidgetConfiguration());
    }

    @Test
    @Order(13)
    @DisplayName("PickList: pick all items")
    public void testPickAll(final Page page) {
        // Arrange
        Assertions.assertFalse(page.standardPickList.getSourceListElements().isEmpty());

        // Act
        page.standardPickList.pickAll();

        // Assert
        Assertions.assertTrue(page.standardPickList.getSourceListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(14)
    @DisplayName("PickList: remove all items")
    public void testRemoveAll(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        Assertions.assertTrue(page.standardPickList.getSourceListElements().isEmpty());
        Assertions.assertFalse(page.standardPickList.getTargetListElements().isEmpty());

        // Act
        page.standardPickList.removeAll();

        // Assert
        Assertions.assertTrue(page.standardPickList.getTargetListElements().isEmpty());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(15)
    @DisplayName("PickList: move up 1 item in the source list")
    public void testMoveSourceItemsUp(final Page page) {
        // Arrange
        final List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        Assertions.assertFalse(sourceLabels.isEmpty());
        Assertions.assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsUp("Max");

        // Assert
        Assertions.assertEquals("Max", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(16)
    @DisplayName("PickList: move down 1 item in the source list")
    public void testMoveSourceItemsDown(final Page page) {
        // Arrange
        final List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        Assertions.assertFalse(sourceLabels.isEmpty());
        Assertions.assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsDown("Lewis");

        // Assert
        Assertions.assertEquals("Max", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(17)
    @DisplayName("PickList: move 1 item in the source list to the top")
    public void testMoveSourceItemsToTop(final Page page) {
        // Arrange
        final List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        Assertions.assertFalse(sourceLabels.isEmpty());
        Assertions.assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsToTop("Lando");

        // Assert
        Assertions.assertEquals("Lando", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(18)
    @DisplayName("PickList: move 1 item in the source list to the bottom")
    public void testMoveSourceItemsToBottom(final Page page) {
        // Arrange
        List<String> sourceLabels = page.standardPickList.getSourceListLabels();
        Assertions.assertFalse(sourceLabels.isEmpty());
        Assertions.assertEquals("Lewis", sourceLabels.get(0));

        // Act
        page.standardPickList.moveSourceItemsToBottom("Lewis");

        // Assert
        sourceLabels = page.standardPickList.getSourceListLabels();
        Assertions.assertEquals("Lewis", sourceLabels.get(sourceLabels.size() - 1));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(19)
    @DisplayName("PickList: move up 1 item in the target list")
    public void testMoveTargetItemsUp(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        final List<String> targetLabels = page.standardPickList.getTargetListLabels();
        Assertions.assertFalse(targetLabels.isEmpty());
        Assertions.assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsUp("Max");

        // Assert
        Assertions.assertEquals("Max", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(20)
    @DisplayName("PickList: move down 1 item in the target list")
    public void testMoveTargetItemsDown(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        final List<String> targetLabels = page.standardPickList.getTargetListLabels();
        Assertions.assertFalse(targetLabels.isEmpty());
        Assertions.assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsDown("Lewis");

        // Assert
        Assertions.assertEquals("Max", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(21)
    @DisplayName("PickList: move 1 item in the target list to the top")
    public void testMoveTargetItemsToTop(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        final List<String> targetLabels = page.standardPickList.getTargetListLabels();
        Assertions.assertFalse(targetLabels.isEmpty());
        Assertions.assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsToTop("Lando");

        // Assert
        Assertions.assertEquals("Lando", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(22)
    @DisplayName("PickList: move 1 item in the target list to the bottom")
    public void testMoveTargetItemsToBottom(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        List<String> targetLabels = page.standardPickList.getTargetListLabels();
        Assertions.assertFalse(targetLabels.isEmpty());
        Assertions.assertEquals("Lewis", targetLabels.get(0));

        // Act
        page.standardPickList.moveTargetItemsToBottom("Lewis");

        // Assert
        targetLabels = page.standardPickList.getTargetListLabels();
        Assertions.assertEquals("Lewis", targetLabels.get(targetLabels.size() - 1));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(23)
    @DisplayName("PickList: filter the source list with the text 'Charles'")
    public void testFilterSource(final Page page) {
        // Act
        page.standardPickList.filterSourceList("Charles");

        // Assert
        Assertions.assertEquals(1, page.standardPickList.getSourceListElements().size());
        Assertions.assertEquals("Charles", page.standardPickList.getSourceListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(24)
    @DisplayName("PickList: clear the source list filter")
    public void testClearSourceFilter(final Page page) {
        // Arrange
        page.standardPickList.filterSourceList("Charles");
        Assertions.assertEquals(1, page.standardPickList.getSourceListElements().size());
        Assertions.assertEquals("Charles", page.standardPickList.getSourceListLabels().get(0));

        // Act
        page.standardPickList.clearSourceListFilter();

        // Assert
        Assertions.assertEquals(4, page.standardPickList.getSourceListElements().size());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(25)
    @DisplayName("PickList: filter the target list with the text 'Charles'")
    public void testFilterTarget(final Page page) {
        // Arrange
        page.standardPickList.pickAll();

        // Act
        page.standardPickList.filterTargetList("Charles");

        // Assert
        Assertions.assertEquals(1, page.standardPickList.getTargetListElements().size());
        Assertions.assertEquals("Charles", page.standardPickList.getTargetListLabels().get(0));
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    @Test
    @Order(26)
    @DisplayName("PickList: clear the filter from the target list")
    public void testClearTargetFilter(final Page page) {
        // Arrange
        page.standardPickList.pickAll();
        page.standardPickList.filterTargetList("Charles");
        Assertions.assertEquals(1, page.standardPickList.getTargetListElements().size());
        Assertions.assertEquals("Charles", page.standardPickList.getTargetListLabels().get(0));

        // Act
        page.standardPickList.clearTargetListFilter();

        // Assert
        Assertions.assertEquals(4, page.standardPickList.getTargetListElements().size());
        assertConfiguration(page.standardPickList.getWidgetConfiguration());
    }

    private JSONObject assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Picklist Config = " + cfg);
        Assertions.assertTrue(cfg.has("id"));
        Assertions.assertTrue(cfg.getBoolean("transferOnDblclick"));
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
