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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-frozencolumns")
class DataTable063Test extends AbstractDataTableTest {

    private static final String FROZEN = "p-datatable-frozenlayout-left";
    private static final String SCROLLABLE = "p-datatable-frozenlayout-right";

    @Test
    @Order(1)
    @DisplayName("DataTable: frozen columns with frozenHeader / scrollableHeader column groups")
    void splitColumnGroups(Page page) {
        assertHeaderSplitAtFirstColumn(page, "split");
        assertEquals(List.of("Total:1x1"), getRows(page, "split", FROZEN, "tfoot"));
        assertEquals(List.of("5 languages:3x1"), getRows(page, "split", SCROLLABLE, "tfoot"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #9854 frozen columns split a plain header / footer column group")
    void plainColumnGroups(Page page) {
        // a plain column group renders like the split frozenHeader / scrollableHeader column groups of the "split" table
        assertHeaderSplitAtFirstColumn(page, "single");
        assertEquals(List.of("Total:1x1"), getRows(page, "single", FROZEN, "tfoot"));
        assertEquals(List.of("5 languages:3x1"), getRows(page, "single", SCROLLABLE, "tfoot"));
        assertHeaderRowsAligned(page, "single");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #9854 frozen columns split a column group cell spanning the frozen and the scrollable part")
    void plainColumnGroupsStraddlingCells(Page page) {
        assertEquals(List.of("ID:1x3|Language:1x1", "Info:1x1", "Name:1x1"), getRows(page, "straddle", FROZEN, "thead"));
        assertEquals(List.of(":2x1", ":1x1|Type:1x2", "First appeared:1x1"), getRows(page, "straddle", SCROLLABLE, "thead"));
        assertEquals(List.of("Total:1x1|5 languages:1x1"), getRows(page, "straddle", FROZEN, "tfoot"));
        assertEquals(List.of(":2x1"), getRows(page, "straddle", SCROLLABLE, "tfoot"));
        assertEquals(2, getBodyColumnCount(page, "straddle", FROZEN));
        assertEquals(2, getBodyColumnCount(page, "straddle", SCROLLABLE));
        assertHeaderRowsAligned(page, "straddle");
        assertUniqueCellIds(page, "straddle");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: GitHub #9854 frozen columns sort by a cell of a plain header column group")
    void plainColumnGroupsSort(Page page) {
        // Act
        WebElement name = getSide(page, "single", SCROLLABLE).findElements(By.cssSelector(".p-datatable-scrollable-header-box thead th")).stream()
                .filter(th -> "Name".equals(th.getText().trim()))
                .findFirst()
                .orElseThrow();
        PrimeSelenium.guardAjax(name).click();

        // Assert
        List<String> names = getSide(page, "single", SCROLLABLE).findElements(By.cssSelector(".p-datatable-scrollable-body tbody tr > td:first-child"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        assertEquals(List.of("C#", "Java", "JavaScript", "Python", "TypeScript"), names);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: GitHub #9854 frozen columns aligned right split a plain header / footer column group")
    void plainColumnGroupsFrozenRight(Page page) {
        // the scrollable part comes first, the frozen part has the last column
        assertEquals(List.of("ID:1x3|Language:2x1", "Info:2x1", "Name:1x1|First appeared:1x1"), getRows(page, "right", SCROLLABLE, "thead"));
        assertEquals(List.of(":1x1", "Type:1x2", ""), getRows(page, "right", FROZEN, "thead"));
        assertEquals(List.of("Total:1x1|5 languages:2x1"), getRows(page, "right", SCROLLABLE, "tfoot"));
        assertEquals(List.of(":1x1"), getRows(page, "right", FROZEN, "tfoot"));
        assertEquals(3, getBodyColumnCount(page, "right", SCROLLABLE));
        assertEquals(1, getBodyColumnCount(page, "right", FROZEN));
        assertHeaderRowsAligned(page, "right");
        assertUniqueCellIds(page, "right");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: GitHub #9854 frozen columns split a plain column group with columns wrapped in ui:fragment / ui:repeat")
    void plainColumnGroupsWrappedColumns(Page page) {
        assertHeaderSplitAtFirstColumn(page, "wrapped");
        assertHeaderRowsAligned(page, "wrapped");
        assertUniqueCellIds(page, "wrapped");

        // content of a row other than columns is encoded in one part only (the client clones the header into the body)
        WebElement wrapped = page.getWebDriver().findElement(By.id("form:wrapped"));
        assertEquals(1, wrapped.findElements(By.cssSelector(".p-datatable-scrollable-header-box [id='form:wrapped:note']")).size());
        assertEquals(1, page.getWebDriver().findElements(By.id("form:wrapped:idTooltip")).size());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: GitHub #9854 frozen columns split a plain column group with p:columns")
    void plainColumnGroupsDynamicColumns(Page page) {
        assertHeaderSplitAtFirstColumn(page, "dynamic");
        assertHeaderRowsAligned(page, "dynamic");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: GitHub #9854 frozen columns split a column group cell spanning both parts and two rows")
    void plainColumnGroupsSpanningRowsAndParts(Page page) {
        assertEquals(List.of("ID:1x1|Language:1x2", "#:1x1", "Key:1x1|Name:1x1"), getRows(page, "spanning", FROZEN, "thead"));
        assertEquals(List.of(":1x2|Type:1x3", "", "First appeared:1x1"), getRows(page, "spanning", SCROLLABLE, "thead"));
        assertHeaderRowsAligned(page, "spanning");
        assertUniqueCellIds(page, "spanning");

        // the width of the cut cell was meant for both of its columns
        WebElement language = getSide(page, "spanning", FROZEN).findElements(By.cssSelector(".p-datatable-scrollable-header-box thead th")).stream()
                .filter(th -> "Language".equals(th.getDomProperty("textContent").trim()))
                .findFirst()
                .orElseThrow();
        // neither the width attribute nor the width of its style (width:300px)
        String style = language.getAttribute("style");
        assertTrue(style == null || !style.contains("300"), "the cut cell must not get the width of the whole cell: " + style);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: GitHub #9854 a frozen table nested in a header cell keeps the outer column group split")
    void plainColumnGroupWithNestedTable(Page page) {
        // the cell after the one with the nested table is still placed in the scrollable part only
        List<WebElement> languages = page.getWebDriver().findElement(By.id("form:nested")).findElements(By.tagName("th")).stream()
                .filter(th -> "Language".equals(th.getDomProperty("textContent").trim()))
                .collect(Collectors.toList());
        assertFalse(languages.isEmpty());
        for (WebElement language : languages) {
            assertEquals("3", language.getAttribute("colspan"));
            assertTrue(language.findElements(By.xpath("ancestor::*[contains(@class,'" + FROZEN + "')]")).isEmpty(),
                    "Language must not be in the frozen part");
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: GitHub #9854 frozen columns do not split a plain column group if one dedicated group is defined")
    void plainColumnGroupWithOneDedicatedGroup(Page page) {
        assertEquals(List.of("Key:1x1"), getRows(page, "mixed", FROZEN, "thead"));
        assertEquals(List.of("Name:1x1|First appeared:1x1|Type:1x1"), getRows(page, "mixed", SCROLLABLE, "thead"));
        assertNoJavascriptErrors();
    }

    private void assertUniqueCellIds(Page page, String table) {
        List<String> ids = page.getWebDriver().findElement(By.id("form:" + table)).findElements(By.cssSelector("th, td")).stream()
                .map(cell -> cell.getAttribute("id"))
                .filter(id -> id != null && !id.isEmpty())
                .collect(Collectors.toList());
        assertTrue(ids.stream().noneMatch(id -> id.contains("undefined")), "cell ids: " + ids);
        assertEquals(ids.size(), ids.stream().distinct().count(), "duplicate cell ids: " + ids);

        // the column toggler skips static cells, it must not list the fillers
        page.getWebDriver().findElement(By.id("form:" + table)).findElements(By.cssSelector("th[id*='_filler'], td[id*='_filler']"))
                .forEach(filler -> assertTrue(filler.getAttribute("class").contains("p-static-column"),
                        "filler not static: " + filler.getAttribute("id")));
    }

    private void assertHeaderSplitAtFirstColumn(Page page, String table) {
        assertEquals(List.of("ID:1x3", "", ""), getRows(page, table, FROZEN, "thead"));
        assertEquals(List.of("Language:3x1", "Info:2x1|Type:1x2", "Name:1x1|First appeared:1x1"), getRows(page, table, SCROLLABLE, "thead"));
        assertEquals(1, getBodyColumnCount(page, table, FROZEN));
        assertEquals(3, getBodyColumnCount(page, table, SCROLLABLE));
    }

    private void assertHeaderRowsAligned(Page page, String table) {
        List<WebElement> frozenRows = getSide(page, table, FROZEN).findElements(By.cssSelector(".p-datatable-scrollable-header-box thead > tr"));
        List<WebElement> scrollableRows = getSide(page, table, SCROLLABLE).findElements(By.cssSelector(".p-datatable-scrollable-header-box thead > tr"));
        assertEquals(frozenRows.size(), scrollableRows.size());
        for (int i = 0; i < frozenRows.size(); i++) {
            int frozenTop = frozenRows.get(i).getRect().getY();
            int scrollableTop = scrollableRows.get(i).getRect().getY();
            assertTrue(Math.abs(frozenTop - scrollableTop) <= 1, "header row " + i + " misaligned: " + frozenTop + " vs " + scrollableTop);
        }
    }

    private WebElement getSide(Page page, String table, String side) {
        return page.getWebDriver().findElement(By.id("form:" + table)).findElement(By.className(side));
    }

    /**
     * @return each row of the side's header ("thead") or footer ("tfoot") as "text:colspanxrowspan" cells joined by "|"
     */
    private List<String> getRows(Page page, String table, String side, String section) {
        String box = "thead".equals(section) ? ".p-datatable-scrollable-header-box" : ".p-datatable-scrollable-footer-box";
        return getSide(page, table, side).findElements(By.cssSelector(box + " " + section + " > tr")).stream()
                .map(tr -> tr.findElements(By.cssSelector("th, td")).stream()
                        .map(cell -> cell.getDomProperty("textContent").trim() + ":" + span(cell, "colspan") + "x" + span(cell, "rowspan"))
                        .collect(Collectors.joining("|")))
                .collect(Collectors.toList());
    }

    private String span(WebElement cell, String attribute) {
        String value = cell.getAttribute(attribute);
        return value == null ? "1" : value;
    }

    private int getBodyColumnCount(Page page, String table, String side) {
        return getSide(page, table, side).findElements(By.cssSelector(".p-datatable-scrollable-body tbody > tr:first-child > td")).size();
    }

    public static class Page extends AbstractPrimePage {
        @Override
        public String getLocation() {
            return "datatable/dataTable063.xhtml";
        }
    }
}
