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
package org.primefaces.integrationtests.datatable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

public abstract class AbstractDataTableTest extends AbstractPrimePageTest {

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
        List<Row> rows = dataTable.getRows();
        assertRows(rows, langs);
    }

    protected void assertRows(List<Row> rows, List<ProgrammingLanguage> langs) {
        int expectedSize = langs.size();
        Assertions.assertNotNull(rows);
        if (expectedSize == 0) {
            expectedSize = 1; // No records found.
        }
        Assertions.assertEquals(expectedSize, rows.size());

        int row = 0;
        for (ProgrammingLanguage programmingLanguage : langs) {
            String rowText = rows.get(row).getCell(0).getText();
            if (!rowText.equalsIgnoreCase("No records found.")) {
                Assertions.assertEquals(programmingLanguage.getId(), Integer.parseInt(rowText));
                row++;
            }
        }
    }

    protected void assertHeaderSorted(WebElement header, String sortDirection, int sortPriority) {
        String directionClass = null;
        switch (sortDirection) {
            case "ASC":
                directionClass = "ui-icon-triangle-1-n";
                break;
            case "DESC":
                directionClass = "ui-icon-triangle-1-s";
                break;
            default:
                break;
        }
        Assertions.assertTrue(PrimeSelenium.hasCssClass(header.findElement(By.className("ui-sortable-column-icon")), directionClass));
        WebElement badge = header.findElement(By.className("ui-sortable-column-badge"));
        if (sortPriority > 0) {
            Assertions.assertEquals(sortPriority, Integer.parseInt(badge.getText()));
        }
        else {
            Assertions.assertEquals("", badge.getText());
        }
    }

    protected void logWebDriverCapabilites(DataTable003Test.Page page) {
        if (page.getWebDriver() instanceof HasCapabilities) {
            HasCapabilities hasCaps = (HasCapabilities) page.getWebDriver();
            System.out.println("BrowserName: " + hasCaps.getCapabilities().getBrowserName());
            System.out.println("Version: " + hasCaps.getCapabilities().getVersion());
            System.out.println("Platform: " + hasCaps.getCapabilities().getPlatform());
        }
        else {
            System.out.println("WebDriver does not implement HasCapabilities --> can´t show version etc");
        }
    }

    protected void filterGlobal(WebElement inputGlobalFilter, String filter) {
        // maybe we can move some of this to PF Selenium (InputText?, DataTable?)
        inputGlobalFilter.clear();
        inputGlobalFilter.sendKeys(filter);
        PrimeSelenium.guardAjax(inputGlobalFilter).sendKeys(Keys.TAB);
    }
}
