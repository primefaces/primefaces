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
package org.primefaces.integrationtests.outputlabel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.OutputLabel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OutputLabel001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("OutputLabel: required=true")
    void required(Page page) {
        // Arrange
        OutputLabel label = page.required;

        // Act and Assert
        assertLabel(label, "Required*", true);
        WebElement input = label.getFor();
        assertNotNull(input);
        assertEquals("form:inputrequired", input.getAttribute("id"));
    }

    @Test
    @Order(2)
    @DisplayName("OutputLabel: indicaterequired=false")
    void indicateRequired_False(Page page) {
        // Arrange
        OutputLabel label = page.notrequired;

        // Act and Assert
        assertLabel(label, "Not Required", false);
    }

    @Test
    @Order(3)
    @DisplayName("OutputLabel: indicaterequired=true")
    void indicateRequired_True(Page page) {
        // Arrange
        OutputLabel label = page.indicaterequired;

        // Act and Assert
        assertLabel(label, "Indicate Required*", true);
    }

    @Test
    @Order(4)
    @DisplayName("OutputLabel: No bean annotations on Java bean")
    void noAnnotation(Page page) {
        // Arrange
        OutputLabel label = page.noannotations;

        // Act and Assert
        assertLabel(label, "Label with no annotations:", false);
    }

    @Test
    @Order(5)
    @DisplayName("OutputLabel: @NotNull bean validation")
    void beanValidation_NotNull(Page page) {
        // Arrange
        OutputLabel label = page.notnull;

        // Act and Assert
        assertLabel(label, "Label which considers @NotNull:*", true);
    }

    @Test
    @Order(5)
    @DisplayName("OutputLabel: @NotBlank bean validation")
    void beanValidation_NotBlank(Page page) {
        // Arrange
        OutputLabel label = page.notblank;

        // Act and Assert
        assertLabel(label, "Label which considers @NotBlank:*", true);
    }

    @Test
    @Order(5)
    @DisplayName("OutputLabel: @NotEmpty bean validation")
    void beanValidation_NotEmpty(Page page) {
        // Arrange
        OutputLabel label = page.notempty;

        // Act and Assert
        assertLabel(label, "Label which considers @NotEmpty:*", true);
    }

    @Test
    @Order(6)
    @DisplayName("OutputLabel: Label for input wrapped in composite not required")
    void composite1(Page page) {
        // Arrange
        OutputLabel label = page.composite1;

        // Act and Assert
        assertLabel(label, "Label for input wrapped in composite not required:", false);
    }

    @Test
    @Order(7)
    @DisplayName("OutputLabel: Label for input wrapped in composite, considering @NotNull")
    void composite2(Page page) {
        // Arrange
        OutputLabel label = page.composite2;

        // Act and Assert
        assertLabel(label, "Label for input wrapped in composite, considering @NotNull:*", true);
    }

    @Test
    @Order(8)
    @DisplayName("OutputLabel: Label for input wrapped in composite required")
    void composite3(Page page) {
        // Arrange
        OutputLabel label = page.composite3;

        // Act and Assert
        assertLabel(label, "Label for input wrapped in composite required:*", true);
    }

    @Test
    @Order(9)
    @DisplayName("OutputLabel: disabled=true")
    void disabled(Page page) {
        // Arrange
        OutputLabel label = page.disabled;

        // Act and Assert
        assertLabel(label, "Disabled*", true);
    }

    @Test
    @Order(10)
    @DisplayName("OutputLabel: readonly=true")
    void readOnly(Page page) {
        // Arrange
        OutputLabel label = page.readonly;

        // Act and Assert
        assertLabel(label, "Read Only*", true);
    }

    @Test
    @Order(11)
    @DisplayName("OutputLabel: disabled=true indicateRequired=autoSkipDisabled")
    void disabledSkipped(Page page) {
        // Arrange
        OutputLabel label = page.disabledSkipped;

        // Act and Assert
        assertLabel(label, "Disabled Skipped", false);
    }

    @Test
    @Order(12)
    @DisplayName("OutputLabel: readonly=true indicateRequired=autoSkipDisabled")
    void readOnlySkipped(Page page) {
        // Arrange
        OutputLabel label = page.readonlySkipped;

        // Act and Assert
        assertLabel(label, "Read Only Skipped", false);
    }

    @Test
    @Order(13)
    @DisplayName("OutputLabel: disabled=false indicateRequired=autoSkipDisabled")
    void enabledNotSkipped(Page page) {
        // Arrange
        OutputLabel label = page.enabledNotSkipped;

        // Act and Assert
        assertLabel(label, "Enabled Not Skipped*", true);
    }

    @Test
    @Order(14)
    @DisplayName("OutputLabel: readonly=false indicateRequired=autoSkipDisabled")
    void readOnlyNotSkipped(Page page) {
        // Arrange
        OutputLabel label = page.readonlyNotSkipped;

        // Act and Assert
        assertLabel(label, "Read Only Not Skipped*", true);
    }


    private void assertLabel(OutputLabel label, String text, boolean required) {
        assertEquals(required, label.hasRequiredIndicator());
        assertEquals(text, label.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:required")
        OutputLabel required;

        @FindBy(id = "form:notrequired")
        OutputLabel notrequired;

        @FindBy(id = "form:indicaterequired")
        OutputLabel indicaterequired;

        @FindBy(id = "form:disabled")
        OutputLabel disabled;

        @FindBy(id = "form:readonly")
        OutputLabel readonly;

        @FindBy(id = "form:disabledSkipped")
        OutputLabel disabledSkipped;

        @FindBy(id = "form:readonlySkipped")
        OutputLabel readonlySkipped;

        @FindBy(id = "form:enabledNotSkipped")
        OutputLabel enabledNotSkipped;

        @FindBy(id = "form:readonlyNotSkipped")
        OutputLabel readonlyNotSkipped;

        @FindBy(id = "form:noannotations")
        OutputLabel noannotations;

        @FindBy(id = "form:notnull")
        OutputLabel notnull;

        @FindBy(id = "form:notblank")
        OutputLabel notblank;

        @FindBy(id = "form:notempty")
        OutputLabel notempty;

        @FindBy(id = "form:composite1")
        OutputLabel composite1;

        @FindBy(id = "form:composite2")
        OutputLabel composite2;

        @FindBy(id = "form:composite3")
        OutputLabel composite3;

        @FindBy(id = "form:composite4")
        OutputLabel composite4;

        @Override
        public String getLocation() {
            return "outputlabel/outputLabel001.xhtml";
        }
    }
}
