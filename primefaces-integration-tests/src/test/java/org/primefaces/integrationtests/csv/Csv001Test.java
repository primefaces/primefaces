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
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.InputNumber;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

public class Csv001Test extends AbstractColorPickerTest {

    @Test
    @Order(1)
    @DisplayName("CSV: InputText")
    public void inputText(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgNumber.getText());

        // Act
        page.inputTextName.setValue("x");
        page.inputTextName.clear();

        // Assert
        Assertions.assertEquals("Name (3-50 characters): Validation Error: Value is required.",
                page.msgName.getText());

        // Act
        page.inputTextName.setValue("aa");

        // Assert
        assertConfiguration();
        Assertions.assertEquals("Name (3-50 characters): Validation Error: Length is less than allowable minimum of '3'.",
                page.msgName.getText());

        // Act
        page.inputTextName.setValue("aaa");

        // Assert
        Assertions.assertEquals("", page.msgName.getText());
    }

    @Test
    @Order(2)
    @DisplayName("CSV: InputNumber - Integer")
    public void inputNumberInteger(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgNumber.getText());

        // Act
        page.inputNumber.getInput().sendKeys("1");
        page.inputNumber.clear();

        // Assert
        Assertions.assertEquals("Number: Validation Error: Value is required.",
                page.msgNumber.getText());

        // Act
        page.inputNumber.getInput().sendKeys("99");

        // Assert
        assertConfiguration();
        Assertions.assertEquals("Number: Validation Error: Specified attribute is not between the expected values of 100 and 9999.",
                page.msgNumber.getText());

        // Act
        page.inputNumber.clear();
        page.inputNumber.getInput().sendKeys("100");

        // Assert
        Assertions.assertEquals("", page.msgNumber.getText());

        // Act
        page.inputNumber.clear();
        page.inputNumber.getInput().sendKeys("10000");

        // Assert
        Assertions.assertEquals("Number: Validation Error: Specified attribute is not between the expected values of 100 and 9999.",
                page.msgNumber.getText());
    }

    @Test
    @Order(3)
    @DisplayName("CSV: InputText - Double")
    public void inputTextDouble(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgDouble.getText());

        // Act
        page.inputTextDouble.setValue("x");
        page.inputTextDouble.clear();

        // Assert
        Assertions.assertEquals("Double (5.5 - 8.5): Validation Error: Value is required.",
                page.msgDouble.getText());

        // Act
        page.inputTextDouble.setValue("5");

        // Assert
        assertConfiguration();
        Assertions.assertEquals("Double (5.5 - 8.5): Validation Error: Specified attribute is not between the expected values of 5.5 and 8.5.",
                page.msgDouble.getText());

        // Act
        page.inputTextDouble.setValue("6");

        // Assert
        Assertions.assertEquals("", page.msgDouble.getText());

        // Act
        page.inputTextDouble.setValue("9");

        // Assert
        Assertions.assertEquals("Double (5.5 - 8.5): Validation Error: Specified attribute is not between the expected values of 5.5 and 8.5.",
                page.msgDouble.getText());
    }

    @Test
    @Order(4)
    @DisplayName("CSV: InputText - RegEx")
    public void inputTextRegEx(Page page) {
        // Arrange

        // Assert
        Assertions.assertEquals("", page.msgRegex.getText());

        // Act
        page.inputTextRegex.getInput().sendKeys("a");
        page.inputTextRegex.clear();

        // Assert
        Assertions.assertEquals("Regex (^[a-zA-Z]+$): Validation Error: Value is required.",
                page.msgRegex.getText());

        // Act
        page.inputTextRegex.getInput().sendKeys("aa1a");

        // Assert
        assertConfiguration();
//        Assertions.assertEquals("Regex (^[a-zA-Z]+$): Validation Error: Value does not match pattern.",
//                page.msgRegex.getText());
        // why without label?
        Assertions.assertEquals("Value does not match pattern.",
                page.msgRegex.getText());

        // Act
        page.inputTextRegex.clear();
        page.inputTextRegex.getInput().sendKeys("aaa");

        // Assert
        Assertions.assertEquals("", page.msgRegex.getText());
    }

    // TODO: more classic CSV-IT´s

    @Test
    @Order(100)
    @DisplayName("CSV / CommandButton-enabledByValidateClient: Initial state after page-load")
    public void commandButtonEnabledByInitialState(Page page) {
        // Arrange

        // Act

        // Assert
        assertConfiguration();
        Assertions.assertFalse(page.bntNonAjax.isEnabled());
        Assertions.assertFalse(page.bntAjax.isEnabled());
        Assertions.assertFalse(page.bntPartial.isEnabled());
        Assertions.assertFalse(page.btnDisabled.isEnabled());
        Assertions.assertFalse(page.btnRemoteCommand.isEnabled());
    }

    @Test
    @Order(101)
    @DisplayName("CSV / CommandButton-enabledByValidateClient: updated state after (partial) ajax-update")
    public void testCommandButtonEnabledByAjaxRefresh(Page page) {
        // Arrange

        // Act
        page.btnModifyNameNumber.click();

        // Assert
        assertConfiguration();
        Assertions.assertFalse(page.bntNonAjax.isEnabled());
        Assertions.assertFalse(page.bntAjax.isEnabled());
        Assertions.assertTrue(page.bntPartial.isEnabled());
        Assertions.assertFalse(page.btnDisabled.isEnabled());
        Assertions.assertFalse(page.btnRemoteCommand.isEnabled());
    }

    @Test
    @Order(102)
    @DisplayName("CSV / CommandButton-enabledByValidateClient: fill form")
    public void commandButtonEnabledByFillForm(Page page) {
        // Arrange

        // Assert
        Assertions.assertFalse(page.bntNonAjax.isEnabled());
        Assertions.assertFalse(page.bntAjax.isEnabled());
        Assertions.assertFalse(page.bntPartial.isEnabled());
        Assertions.assertFalse(page.btnDisabled.isEnabled());
        Assertions.assertFalse(page.btnRemoteCommand.isEnabled());

        // Act
        page.btnModifyNameNumber.click();
        page.inputTextDouble.setValue("6");
        page.inputTextRegex.setValue("ab");
        page.datePicker.setDate(LocalDateTime.now());

        // Assert
        Assertions.assertFalse(page.bntNonAjax.isEnabled());
        Assertions.assertFalse(page.bntAjax.isEnabled());
        Assertions.assertTrue(page.bntPartial.isEnabled());
        Assertions.assertFalse(page.btnDisabled.isEnabled());
        Assertions.assertFalse(page.btnRemoteCommand.isEnabled());

        page.selectOneMenu.select("Faces");

        // Assert
        assertConfiguration();
        Assertions.assertTrue(page.bntNonAjax.isEnabled());
        Assertions.assertTrue(page.bntAjax.isEnabled());
        Assertions.assertTrue(page.bntPartial.isEnabled());
        Assertions.assertTrue(page.btnDisabled.isEnabled());
        Assertions.assertTrue(page.btnRemoteCommand.isEnabled());

        // Act
        page.inputTextName.setValue("ab");

        // Assert
        Assertions.assertFalse(page.bntNonAjax.isEnabled());
        Assertions.assertFalse(page.bntAjax.isEnabled());
        Assertions.assertFalse(page.bntPartial.isEnabled());
        Assertions.assertFalse(page.btnDisabled.isEnabled());
        Assertions.assertFalse(page.btnRemoteCommand.isEnabled());
    }

    private void assertConfiguration() {
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:name")
        InputText inputTextName;

        @FindBy(id = "form:msgName")
        Messages msgName;

        @FindBy(id = "form:number")
        InputNumber inputNumber;

        @FindBy(id = "form:msgNumber")
        Messages msgNumber;

        @FindBy(id = "form:double")
        InputText inputTextDouble;

        @FindBy(id = "form:msgDouble")
        Messages msgDouble;

        @FindBy(id = "form:regex")
        InputText inputTextRegex;

        @FindBy(id = "form:msgRegex")
        Messages msgRegex;

        @FindBy(id = "form:localDate")
        DatePicker datePicker;

        @FindBy(id = "form:msgLocalDate")
        Messages msgDatePicker;

        @FindBy(id = "form:selectOneMenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:msgSelectOneMenu")
        Messages msgSelectOneMenu;

        @FindBy(id = "form:btnNonAjax")
        CommandButton bntNonAjax;

        @FindBy(id = "form:btnAjax")
        CommandButton bntAjax;

        @FindBy(id = "form:btnPartial")
        CommandButton bntPartial;

        @FindBy(id = "form:btnDisabled")
        CommandButton btnDisabled;

        @FindBy(id = "form:btnRemoteCommand")
        CommandButton btnRemoteCommand;

        @FindBy(id = "form:btnModifyNameNumber")
        CommandButton btnModifyNameNumber;

        @FindBy(id = "form:btnModifyNumber")
        CommandButton btnModifyNumber;

        @FindBy(id = "form:btnClearValues")
        CommandButton btnClearValues;

        @Override
        public String getLocation() {
            return "csv/csv001.xhtml";
        }
    }
}
