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
package org.primefaces.integrationtests.csv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.integrationtests.colorpicker.AbstractColorPickerTest;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.*;

import java.time.LocalDateTime;

public class Csv001Test extends AbstractColorPickerTest {

    @Test
    @Order(1)
    @DisplayName("CSV / CommandButton-enabledByValidateClient: Initial state after page-load")
    public void testCommandButtonEnabledByInitialState(Page page) {
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
    @Order(2)
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
    @Order(3)
    @DisplayName("CSV / CommandButton-enabledByValidateClient: fill form")
    public void testCommandButtonEnabledByFillForm(Page page) {
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

        page.selectOneMenu.select("JSF");

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


    // TODO: classic CSV-IT´s

    private void assertConfiguration() {
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:name")
        InputText inputTextName;

        @FindBy(id = "form:number")
        InputNumber inputNumber;

        @FindBy(id = "form:double")
        InputText inputTextDouble;

        @FindBy(id = "form:regex")
        InputText inputTextRegex;

        @FindBy(id = "form:localDate")
        DatePicker datePicker;

        @FindBy(id = "form:selectOneMenu")
        SelectOneMenu selectOneMenu;

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
