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

import org.primefaces.integrationtests.AbstractTableTest;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractDataTableTest extends AbstractTableTest {

    /**
     * Summary of the selection message of our ProgrammingLanguage beans.
     */
    protected static final String SELECTED_LANGUAGES = "Selected ProgrammingLanguage(s)";

    protected final List<ProgrammingLanguage> languages = new ProgrammingLanguageService().getLangs();
    protected final ProgrammingLanguageLazyDataModel model = new ProgrammingLanguageLazyDataModel();

    public List<ProgrammingLanguage> sortByNoLimit(final Comparator comparator) {
        return (List<ProgrammingLanguage>) languages.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<ProgrammingLanguage> sortBy(final Comparator comparator) {
        return (List<ProgrammingLanguage>) languages.stream()
                .sorted(comparator)
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<ProgrammingLanguage> filterByName(final String name) {
        return languages.stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .filter(l -> l.getName().startsWith(name))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<ProgrammingLanguage> filterById(final int id) {
        return languages.stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getId))
                .filter(l -> l.getId().equals(id))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<ProgrammingLanguage> filterByType(final ProgrammingLanguage.ProgrammingLanguageType type) {
        return languages.stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getType))
                .filter(l -> l.getType().equals(type))
                .limit(3)
                .collect(Collectors.toList());
    }

    protected void assertRows(DataTable dataTable, List<ProgrammingLanguage> langs) {
        assertRows(dataTable, langs, ProgrammingLanguage::getId);
    }

    protected void assertRows(List<Row> rows, List<ProgrammingLanguage> langs) {
        assertRows(rows, langs, ProgrammingLanguage::getId);
    }

    /**
     * Asserts that the table's rows match the expected items, comparing the first cell of each row
     * (an id rendered as text) against the id extracted from each item. Model-agnostic so any
     * backing type (ProgrammingLanguage, Employee, ...) can reuse it.
     */
    protected <T> void assertRows(DataTable dataTable, List<T> items, ToIntFunction<T> idExtractor) {
        assertRows(dataTable.getRows(), items, idExtractor);
    }

    protected <T> void assertRows(List<Row> rows, List<T> items, ToIntFunction<T> idExtractor) {
        assertNotNull(rows);
        assertEquals(items.size(), rows.size());

        int row = 0;
        for (T item : items) {
            String rowText = rows.get(row).getCell(0).getText();
            assertEquals(idExtractor.applyAsInt(item), Integer.parseInt(rowText.trim()));
            row++;
        }
    }

    protected void assertEmployeeRows(DataTable dataTable, List<Employee> employees) {
        assertRows(dataTable, employees, Employee::getId);
    }

    /**
     * Asserts the first message, which our selection beans render as summary and the selected ids as detail.
     *
     * @param messages the messages
     * @param summary the expected summary like "Selected ProgrammingLanguage(s)", contained in the actual summary
     * @param selections the expected comma separated ids
     */
    protected void assertSelections(Messages messages, String summary, String selections) {
        Msg message = messages.getMessage(0);
        assertTrue(message.getSummary().contains(summary));
        assertEquals(selections, message.getDetail());
    }

    /**
     * Asserts the first message of a ProgrammingLanguage selection, see {@link #assertSelections(Messages, String, String)}.
     *
     * @param messages the messages
     * @param selections the expected comma separated ids
     */
    protected void assertSelections(Messages messages, String selections) {
        assertSelections(messages, SELECTED_LANGUAGES, selections);
    }

    /**
     * Gets the history of the {@link LazyDataModelCallTracker} of the page,
     * rendered as hidden input {@code form:fullHistory}.
     *
     * @return the calls like "count() | load(first=0, size=3)"
     */
    protected String getLazyCallHistory() {
        return getWebDriver().findElement(By.id("form:fullHistory")).getDomProperty("value");
    }

    /**
     * Gets the number of calls of the {@link LazyDataModelCallTracker} of
     * the page, rendered as hidden input {@code form:loadCallCount}.
     *
     * @return the number of calls
     */
    protected int getLazyCallCount() {
        return Integer.parseInt(getWebDriver().findElement(By.id("form:loadCallCount")).getDomProperty("value"));
    }

    /**
     * Asserts the full history (order and duplicates) of the {@link LazyDataModelCallTracker}
     * of the page.
     *
     * @param expectedCalls the expected calls, e.g. {@link PageLoad}s or strings like "count()", "getRowData(rowKey=1)"
     */
    protected void assertLazyCalls(Object... expectedCalls) {
        String history = getLazyCallHistory();
        assertEquals(expectedCalls.length, getLazyCallCount(),
                "Expected " + expectedCalls.length + " lazy calls but got " + getLazyCallCount() + ":\n" + history);

        String expectedHistory = Arrays.stream(expectedCalls).map(String::valueOf).collect(Collectors.joining(" | "));
        assertEquals(expectedHistory, history, "Compare of full history of expected lazy calls failed");
    }

    /**
     * Represents a lazy data load call with first offset and page size, as recorded by the
     * {@link LazyDataModelCallTracker}.
     * Provides predefined constants for common page loads to improve test readability.
     */
    public static class PageLoad {

        // Predefined page loads for common test scenarios (rows=3)
        public static final PageLoad PAGE_1 = new PageLoad(0, 3);
        public static final PageLoad PAGE_2 = new PageLoad(3, 3);
        public static final PageLoad PAGE_3 = new PageLoad(6, 3);
        // all 75 ProgrammingLanguages
        public static final PageLoad ALL_DATA = new PageLoad(0, 75);

        private final int first;
        private final int pageSize;

        public PageLoad(int first, int pageSize) {
            this.first = first;
            this.pageSize = pageSize;
        }

        public int getFirst() {
            return first;
        }

        public int getPageSize() {
            return pageSize;
        }

        @Override
        public String toString() {
            return String.format("load(first=%d, size=%d)", first, pageSize);
        }
    }
}
