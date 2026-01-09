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
package org.primefaces.integrationtests.datepicker;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.model.Severity;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

public class DatePicker011Test extends AbstractDatePickerTest {

    public static final int SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS = 250;

    private enum DatePickerBehaviour {
        _none, dateSelect, viewChange, close;
    }

    @Test
    @Order(1)
    @DisplayName("DatePicker: meta data model without behaviour")
    void metaDataNoBehaviour1(Page page) {
        testDatePickerPart1(page.datePicker0, page, DatePickerBehaviour._none);
        assertConfiguration(page.datePicker0.getWidgetConfiguration(), DatePickerBehaviour._none, 3);
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: meta data model with dateSelect behaviour")
    void metaDataDateSelectBehaviour1(Page page) {
        testDatePickerPart1(page.datePicker1, page, DatePickerBehaviour.dateSelect);
        assertConfiguration(page.datePicker1.getWidgetConfiguration(), DatePickerBehaviour.dateSelect, 3);
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: meta data model with viewChange behaviour")
    void metaDataViewChangeBehaviour1(Page page) {
        testDatePickerPart1(page.datePicker2, page, DatePickerBehaviour.viewChange);
        assertConfiguration(page.datePicker2.getWidgetConfiguration(), DatePickerBehaviour.viewChange, 3);
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: meta data model with close behaviour")
    void metaDataCloseBehaviour1(Page page) {
        testDatePickerPart1(page.datePicker3, page, DatePickerBehaviour.close);
        assertConfiguration(page.datePicker3.getWidgetConfiguration(), DatePickerBehaviour.close, 3);
    }

    @Test
    @Order(5)
    @DisplayName("DatePicker: meta data model without behaviour")
    void metaDataNoBehaviour2(Page page) {
        testDatePickerPart2(page.datePicker0, page, DatePickerBehaviour._none);
    }

    @Test
    @Order(6)
    @DisplayName("DatePicker: meta data model with dateSelect behaviour")
    void metaDataDateSelectBehaviour2(Page page) {
        testDatePickerPart2(page.datePicker1, page, DatePickerBehaviour.dateSelect);
    }

    @Test
    @Order(7)
    @DisplayName("DatePicker: meta data model with viewChange behaviour")
    void metaDataViewChangeBehaviour2(Page page) {
        testDatePickerPart2(page.datePicker2, page, DatePickerBehaviour.viewChange);
    }

    @Test
    @Order(8)
    @DisplayName("DatePicker: meta data model with close behaviour")
    void metaDataCloseBehaviour2(Page page) {
        testDatePickerPart2(page.datePicker3, page, DatePickerBehaviour.close);
    }

    @Test
    @Order(9)
    @DisplayName("DatePicker: meta data model without behaviour")
    void basicMetaBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker0, page, DatePickerBehaviour._none);
    }

    @Test
    @Order(10)
    @DisplayName("DatePicker: meta data model with dateSelect behaviour")
    void metaDataDateSelectBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker1, page, DatePickerBehaviour.dateSelect);
    }

    @Test
    @Order(11)
    @DisplayName("DatePicker: meta data model with viewChange behaviour")
    void metaDataViewChangeBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker2, page, DatePickerBehaviour.viewChange);
    }

    @Test
    @Order(12)
    @DisplayName("DatePicker: meta data model with close behaviour")
    void metaDataCloseBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker3, page, DatePickerBehaviour.close);
    }

    @Test
    @Order(13)
    @DisplayName("DatePicker: lazy meta data model without behaviour")
    void lazyMetaDataNoBehaviour1(Page page) {
        testDatePickerPart1(page.datePicker4, page, DatePickerBehaviour._none);
        assertConfiguration(page.datePicker4.getWidgetConfiguration(), DatePickerBehaviour._none, 0);
    }

    @Test
    @Order(15)
    @DisplayName("DatePicker: lazy meta data model with viewChange behaviour")
    void lazyMetaDataViewChangeBehaviour1(Page page) {
        testDatePickerPart1(page.datePicker6, page, DatePickerBehaviour.viewChange);
        assertConfiguration(page.datePicker6.getWidgetConfiguration(), DatePickerBehaviour.viewChange, 0);
    }

    @Test
    @Order(17)
    @DisplayName("DatePicker: lazy meta data model without behaviour")
    void lazyMetaDataNoBehaviour2(Page page) {
        testDatePickerPart2(page.datePicker4, page, DatePickerBehaviour._none);
    }

    @Test
    @Order(19)
    @Tag("SafariExclude") // need to investigate why safari doesn't work
    @DisplayName("DatePicker: lazy meta data model with viewChange behaviour")
    void lazyMetaDataViewChangeBehaviour2(Page page) {
        testDatePickerPart2(page.datePicker6, page, DatePickerBehaviour.viewChange);
    }

    @Test
    @Order(21)
    @DisplayName("DatePicker: lazy meta data model without behaviour")
    void lazyBasicMetaBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker4, page, DatePickerBehaviour._none);
    }

    @Test
    @Order(22)
    @DisplayName("DatePicker: lazy meta data model with dateSelect behaviour")
    void lazyMetaDataDateSelectBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker5, page, DatePickerBehaviour.dateSelect);
    }

    @Test
    @Order(23)
    @DisplayName("DatePicker: lazy meta data model with viewChange behaviour")
    void lazyMetaDataViewChangeBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker6, page, DatePickerBehaviour.viewChange);
    }

    @Test
    @Order(24)
    @DisplayName("DatePicker: lazy meta data model with close behaviour")
    void lazyMetaDataCloseBehaviour3(Page page) {
        testDatePickerPart3(page.datePicker7, page, DatePickerBehaviour.close);
    }

    private void testDatePickerPart1(DatePicker datePicker, Page page, DatePickerBehaviour behaviour) {
        // Assert initial state
        Messages messages = page.messages;
        assertEmptyMessages(messages);

        // Act - 1st show panel
        datePicker.showPanel();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(datePicker.getPanel()));
        switch (behaviour) {
            case viewChange:
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }
        assertCalendarDate(datePicker, ".tst-disabled", "1");
        assertCalendarDate(datePicker, ".tst-begin", "2");
        assertCalendarDate(datePicker, ".tst-end", "3");

        // Act - 2nd previous month
        datePicker.getPreviousMonthLink().click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }
        assertCalendarDate(datePicker, ".tst-disabled", "2");

        // Act - 3rd next month
        datePicker.getNextMonthLink().click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 4th next month
        datePicker.getNextMonthLink().click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }
        assertCalendarDate(datePicker, ".tst-disabled", "3");

        // Act - 5th previous month
        datePicker.getPreviousMonthLink().click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 6th next month via month drop down
        datePicker.selectMonthDropdown(LocalDate.now().getMonth().getValue() % 12);

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 7th next year via year drop down
        datePicker.incrementYear(1);

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 8th clear
        datePicker.getClearButton().click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
            case dateSelect:
            case close:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 9th select 5th of current month
        datePicker.showPanel();
        WebElement link = datePicker.getPanel().findElement(By.linkText("5"));
        switch (behaviour) {
            case viewChange:
                if (datePicker.isViewChangeAjaxified()) {
                    link = PrimeSelenium.guardAjax(link);
                }
                break;
            case dateSelect:
                if (datePicker.isDateSelectAjaxified()) {
                    link = PrimeSelenium.guardAjax(link);
                }
                break;
            case close:
                if (datePicker.isCloseAjaxified()) {
                    link = PrimeSelenium.guardAjax(link);
                }
                break;
            default:
                break;
        }
        link.click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            case dateSelect:
            case close:
                if (datePicker.isLazy()) {
                    assertEmptyMessages(messages);
                }
                else {
                    assertMessage(messages, behaviour);
                }
                break;
            default:
                break;
        }
    }

    private void testDatePickerPart2(DatePicker datePicker, Page page, DatePickerBehaviour behaviour) {
        // Assert initial state
        Messages messages = page.messages;
        assertEmptyMessages(messages);

        // Act - 1st show panel
        datePicker.click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(datePicker.getPanel()));
        switch (behaviour) {
            case viewChange:
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 2nd today
        WebElement button = datePicker.getTodayButton();
        switch (behaviour) {
            case viewChange:
                if (datePicker.isViewChangeAjaxified()) {
                    button = PrimeSelenium.guardAjax(button);
                }
                break;
            case dateSelect:
                if (datePicker.isDateSelectAjaxified()) {
                    button = PrimeSelenium.guardAjax(button);
                }
                break;
            case close:
                if (datePicker.isCloseAjaxified()) {
                    button = PrimeSelenium.guardAjax(button);
                }
                break;
            default:
                break;
        }
        button.click(); // may trigger 2 ajax-calls due to DatePicker internals; PrimeSelenium.guardAjax only covers the first one

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
            case dateSelect:
            case close:
                assertMessage(messages, behaviour);
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }
    }

    private void testDatePickerPart3(DatePicker datePicker, Page page, DatePickerBehaviour behaviour) {
        // Assert initial state
        Messages messages = page.messages;
        assertEmptyMessages(messages);

        // Act - 1st show panel
        datePicker.showPanel();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(datePicker.getPanel()));
        switch (behaviour) {
            case viewChange:
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }

        // Act - 2nd clear
        datePicker.getClearButton().click();

        // Assert
        PrimeSelenium.wait(SAFETY_WAIT_AFTER_DOUBLE_AJAX_CALL_MILLISECONDS);
        switch (behaviour) {
            case viewChange:
                assertMessage(messages, behaviour);
                break;
            default:
                break;
        }

        // Act - 3rd click outside panel
        if (DatePickerBehaviour.close == behaviour) {
            page.outside.click();
        }
        else {
            PrimeSelenium.guardAjax(page.outsideClean).click();
        }

        // Act - 4th show panel
        PrimeSelenium.guardAjax(page.outsideClean).click();
        datePicker.showPanel();

        // Assert
        switch (behaviour) {
            case viewChange:
                break;
            default:
                assertEmptyMessages(messages);
                break;
        }
    }

    private void assertMessage(Messages messages, DatePickerBehaviour behaviour) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(messages));
        List<Msg> msgs = messages.getMessagesBySeverity(Severity.INFO);
        assertEquals(1, msgs.size());
        assertTrue(msgs.get(0).getSummary().contains(behaviour.name()),
                    behaviour.name() + " not in " + messages.getAllMessages().toString());
    }

    private void assertEmptyMessages(Messages messages) {
        assertTrue(messages.getAllMessages().isEmpty(), messages.getAllMessages().toString());

    }

    private void assertCalendarDate(DatePicker datePicker, String styleClass, String day) {
        List<WebElement> days = datePicker.getPanel().findElements(
                    By.cssSelector(".ui-datepicker-calendar td:not(.ui-datepicker-other-month) " + styleClass));
        assertEquals(1, days.size(), days.toString());
        assertEquals(day, days.get(0).getText());
        if (".tst-disabled".equals(styleClass)) {
            assertTrue(days.get(0).getAttribute("class").contains("ui-state-disabled"));
        }
    }

    private void assertConfiguration(JSONObject cfg, DatePickerBehaviour behaviour, int disabledDateCount) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertEquals("yy-mm-dd", cfg.getString("dateFormat"));
        assertEquals(disabledDateCount, cfg.getJSONArray("disabledDates").length());
        assertFalse(cfg.getBoolean("inline"));
        if (behaviour != DatePickerBehaviour._none) {
            assertTrue(cfg.getJSONObject("behaviors").getString(behaviour.name()).contains(behaviour.name()),
                        "missing behaviour " + behaviour.name());
        }
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datepicker0")
        DatePicker datePicker0;
        @FindBy(id = "form:datepicker1")
        DatePicker datePicker1;
        @FindBy(id = "form:datepicker2")
        DatePicker datePicker2;
        @FindBy(id = "form:datepicker3")
        DatePicker datePicker3;
        @FindBy(id = "form:datepicker4")
        DatePicker datePicker4;
        @FindBy(id = "form:datepicker5")
        DatePicker datePicker5;
        @FindBy(id = "form:datepicker6")
        DatePicker datePicker6;
        @FindBy(id = "form:datepicker7")
        DatePicker datePicker7;

        @FindBy(id = "form:outside")
        WebElement outside;
        @FindBy(id = "form:clean")
        CommandButton outsideClean;

        @Override
        public String getLocation() {
            return "datepicker/datePicker011.xhtml";
        }
    }
}
