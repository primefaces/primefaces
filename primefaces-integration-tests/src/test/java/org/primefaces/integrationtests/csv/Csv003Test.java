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

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Message;
import org.primefaces.selenium.component.Messages;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

public class Csv003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CSV: BV Date @Past Invalid")
    public void datePastInvalid(Page page) {
        // Arrange
        assertMessage(page.msgDatePast, "");
        page.datePast.setValue(LocalTime.now(ZoneId.of("UTC")).plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDatePast, "Time (@Past): must be a past date.");
    }

    @Test
    @Order(2)
    @DisplayName("CSV: BV Date @Past Valid")
    public void datePastValid(Page page) {
        // Arrange
        assertMessage(page.msgDatePast, "");
        page.datePast.setValue(LocalTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDatePast, "");
    }

    @Test
    @Order(3)
    @DisplayName("CSV: BV Date @PastOrPresent Invalid")
    public void datePastOrPresentInvalid(Page page) {
        // Arrange
        assertMessage(page.msgDatePastOrPresent, "");
        page.datePastOrPresent.setValue(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDatePastOrPresent, "Date (@PastOrPresent): must be a date in the past or in the present.");
    }

    @Test
    @Order(4)
    @DisplayName("CSV: BV Date @PastOrPresent Past Valid")
    public void datePastOrPresentValidPast(Page page) {
        // Arrange
        assertMessage(page.msgDatePastOrPresent, "");
        page.datePastOrPresent.setValue(LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDatePastOrPresent, "");
    }

    @Test
    @Order(5)
    @DisplayName("CSV: BV Date @PastOrPresent Present Valid")
    public void datePastOrPresentValidPresent(Page page) {
        // Arrange
        assertMessage(page.msgDatePastOrPresent, "");
        page.datePastOrPresent.setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDatePastOrPresent, "");
    }

    @Test
    @Order(6)
    @DisplayName("CSV: BV Date @Future Invalid")
    public void dateFutureInvalid(Page page) {
        // Arrange
        assertMessage(page.msgDateFuture, "");
        page.dateFuture.setValue(LocalDateTime.now(ZoneId.of("UTC")).minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDateFuture, "DateTime (@Future): must be a future date.");
    }

    @Test
    @Order(7)
    @DisplayName("CSV: BV Date @Future Valid")
    public void dateFutureValid(Page page) {
        // Arrange
        assertMessage(page.msgDateFuture, "");
        page.dateFuture.setValue(LocalDateTime.now(ZoneId.of("UTC")).plusHours(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDateFuture, "");
    }

    @Test
    @Order(8)
    @DisplayName("CSV: BV Date @FutureOrPresent Invalid")
    public void dateFutureOrPresentInvalid(Page page) {
        // Arrange
        assertMessage(page.msgDateFutureOrPresent, "");
        page.dateFutureOrPresent.setValue(LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDateFutureOrPresent, "Date (@FutureOrPresent): must be a date in the present or in the future.");
    }

    @Test
    @Order(9)
    @DisplayName("CSV: BV Date @FutureOrPresent Past Valid")
    public void dateFutureOrPresentValidFuture(Page page) {
        // Arrange
        assertMessage(page.msgDateFutureOrPresent, "");
        page.dateFutureOrPresent.setValue(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDateFutureOrPresent, "");
    }

    @Test
    @Order(10)
    @DisplayName("CSV: BV Date @FutureOrPresent Present Valid")
    public void dateFutureOrPresentValidPresent(Page page) {
        // Arrange
        assertMessage(page.msgDateFutureOrPresent, "");
        page.dateFutureOrPresent.setValue(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Act
        page.btnSave.click();

        // Assert
        assertMessage(page.msgDateFutureOrPresent, "");
    }

    protected void assertMessage(Message message, String expected) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(message));
        Assertions.assertEquals(expected, message.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:datePast")
        InputText datePast;

        @FindBy(id = "form:msgDatePast")
        Message msgDatePast;

        @FindBy(id = "form:datePastOrPresent")
        InputText datePastOrPresent;

        @FindBy(id = "form:msgDatePastOrPresent")
        Message msgDatePastOrPresent;

        @FindBy(id = "form:dateFuture")
        InputText dateFuture;

        @FindBy(id = "form:msgDateFuture")
        Message msgDateFuture;

        @FindBy(id = "form:dateFutureOrPresent")
        InputText dateFutureOrPresent;

        @FindBy(id = "form:msgDateFutureOrPresent")
        Message msgDateFutureOrPresent;

        @FindBy(id = "form:btnSave")
        CommandButton btnSave;

        @Override
        public String getLocation() {
            return "csv/csv003.xhtml";
        }
    }
}
