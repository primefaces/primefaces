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
package org.primefaces.integrationtests.csv;

import org.primefaces.integrationtests.colorpicker.AbstractColorPickerTest;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanCheckbox;
import org.primefaces.selenium.component.SelectManyCheckbox;
import org.primefaces.selenium.component.SelectOneRadio;
import org.primefaces.selenium.component.Spinner;
import org.primefaces.selenium.component.ToggleSwitch;
import org.primefaces.selenium.component.TriStateCheckbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Csv002Test extends AbstractColorPickerTest {

    @Test
    @Order(1)
    @DisplayName("CSV: SelectBooleanCheckbox")
    public void selectBooleanCheckbox(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgSelectBooleanCheckbox.getText());

        // Act
        page.selectBooleanCheckbox.check();
        page.selectBooleanCheckbox.uncheck();

        // Assert
        Assertions.assertEquals("yes/no: must be true.",
                page.msgSelectBooleanCheckbox.getText());

        // Act
        page.selectBooleanCheckbox.check();

        // Assert
        Assertions.assertEquals("", page.msgSelectBooleanCheckbox.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("CSV: TriStateCheckbox")
    @Disabled("Because required=true does not seem to have impact on TriStateCheckbox beside adding a star to the label")
    public void triStateCheckbox(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgTriStateCheckbox.getText());

        // Act
        page.triStateCheckbox.setValue(Boolean.FALSE);
        page.triStateCheckbox.setValue(null);

        // Assert
        Assertions.assertEquals("yes/no/maybe: Validation Error: Value is required.",
                page.msgTriStateCheckbox.getText());

        // Act
        page.triStateCheckbox.setValue(Boolean.TRUE);

        // Assert
        Assertions.assertEquals("", page.msgTriStateCheckbox.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("CSV: SelectManyCheckbox")
    public void selectManyCheckbox(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgSelectManyCheckbox.getText());

        // Act
        page.selectManyCheckbox.select(0);
        page.selectManyCheckbox.deselect(0);

        // Assert
        Assertions.assertEquals("Country: Validation Error: Value is required.",
                page.msgSelectManyCheckbox.getText());

        // Act
        page.selectManyCheckbox.select(1);

        // Assert
        Assertions.assertEquals("", page.msgSelectManyCheckbox.getText());
        assertNoJavascriptErrors();
    }

    // TODO: Spinner

    @Test
    @Order(5)
    @DisplayName("CSV: SelectOneListbox")
    public void selectOneListbox(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgSelectOneListbox.getText());

        // Act
        page.selectOneListbox.findElement(By.cssSelector("ul li.ui-selectlistbox-item[aria-label=\" \"]")).click();

        // Assert
        Assertions.assertEquals("Select UI-library: Validation Error: Value is required.",
                page.msgSelectOneListbox.getText());

        // Act
        page.selectOneListbox.findElement(By.cssSelector("ul li.ui-selectlistbox-item[aria-label=\"Faces\"]")).click();

        // Assert
        Assertions.assertEquals("", page.msgSelectOneListbox.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("CSV: SelectOneRadio")
    public void selectOneRadio(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgSelectOneRadio.getText());

        // Act
        page.selectOneRadio.select(3);
        page.selectOneRadio.select(3); // = deselect

        // Assert
        Assertions.assertEquals("Select programming language: Validation Error: Value is required.",
                page.msgSelectOneRadio.getText());

        // Act
        page.selectOneRadio.select(1);

        // Assert
        Assertions.assertEquals("", page.msgSelectOneRadio.getText());
        assertNoJavascriptErrors();
    }

    // TODO: Toggleswitch

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:selectBooleanCheckbox")
        SelectBooleanCheckbox selectBooleanCheckbox;

        @FindBy(id = "form:msgSelectBooleanCheckbox")
        Messages msgSelectBooleanCheckbox;

        @FindBy(id = "form:triStateCheckbox")
        TriStateCheckbox triStateCheckbox;

        @FindBy(id = "form:msgTriStateCheckbox")
        Messages msgTriStateCheckbox;

        @FindBy(id = "form:selectManyCheckbox")
        SelectManyCheckbox selectManyCheckbox;

        @FindBy(id = "form:msgSelectManyCheckbox")
        Messages msgSelectManyCheckbox;

        @FindBy(id = "form:spinner")
        Spinner spinner;

        @FindBy(id = "form:msgSpinner")
        Messages msgSpinner;

        @FindBy(id = "form:selectOneListbox")
        WebElement selectOneListbox; // TODO: provide PrimeFaces Selenium abstraction for SelectOneListbox

        @FindBy(id = "form:msgSelectOneListbox")
        Messages msgSelectOneListbox;

        @FindBy(id = "form:selectOneRadio")
        SelectOneRadio selectOneRadio;

        @FindBy(id = "form:msgSelectOneRadio")
        Messages msgSelectOneRadio;

        @FindBy(id = "form:toggleSwitch")
        ToggleSwitch toggleSwitch;

        @FindBy(id = "form:msgToggleSwitch")
        Messages msgToggleSwitch;

        @FindBy(id = "form:btnAjax")
        CommandButton bntAjax;

        @Override
        public String getLocation() {
            return "csv/csv002.xhtml";
        }
    }
}
