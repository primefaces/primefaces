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
package org.primefaces.selenium.component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Component wrapper for the PrimeFaces {@code p:datePicker}.
 */
public abstract class DatePicker extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @Override
    public void click() {
        input.click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getPanel()));
    }

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getPanel() {
        return getWebDriver().findElement(By.id(getId() + "_panel"));
    }

    /**
     * Is this component AJAX enabled with "dateSelect"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isDateSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "dateSelect");
    }

    /**
     * Is this component AJAX enabled with "viewChange"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isViewChangeAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "viewChange");
    }

    /**
     * Is this component AJAX enabled with "close"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isCloseAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "close");
    }

    /**
     * Gets the Next Month link in the navigator.
     *
     * @return the Next Month link
     */
    public WebElement getNextMonthLink() {
        WebElement link = showPanel().findElement(By.className("ui-datepicker-next"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        if (isViewChangeAjaxified()) {
            link = PrimeSelenium.guardAjax(link);
        }
        return link;
    }

    /**
     * Gets the Previous Month link in the navigator.
     *
     * @return the Previous Month link
     */
    public WebElement getPreviousMonthLink() {
        WebElement link = showPanel().findElement(By.className("ui-datepicker-prev"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        if (isViewChangeAjaxified()) {
            link = PrimeSelenium.guardAjax(link);
        }
        return link;
    }

    /**
     * Selects a day in the overlay panel.
     *
     * @param day the day to select
     * @return the day selected
     */
    public WebElement selectDay(String day) {
        WebElement link = showPanel().findElement(By.linkText(day));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        if (isDateSelectAjaxified()) {
            link = PrimeSelenium.guardAjax(link);
        }
        link.click();
        return link;
    }

    /**
     * Gets the Clear button on the overlay panel.
     *
     * @return the Clear button
     */
    public WebElement getClearButton() {
        WebElement button = showPanel().findElement(By.className("ui-datepicker-buttonbar")).findElement(By.className("ui-clear-button"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        return button;
    }

    /**
     * Gets the Today button on the overlay panel.
     *
     * @return the Today button
     */
    public WebElement getTodayButton() {
        WebElement button = showPanel().findElement(By.className("ui-datepicker-buttonbar")).findElement(By.className("ui-today-button"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        return button;
    }

    public LocalDateTime getValue() {
        if (getWidgetDate() == null) {
            return null;
        }

        Number epoch = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getDate().getTime();");
        // Move epoch into server-timezone
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch.longValue()), ZoneId.systemDefault());
        return dateTime;
    }

    public LocalDate getValueAsLocalDate() {
        if (getWidgetDate() == null) {
            return null;
        }
        Long dayOfMonth = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getDate().getDate();");
        Long month = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getDate().getMonth();");
        Long year = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getDate().getFullYear();");
        return LocalDate.of(year.intValue(), month.intValue() + 1, dayOfMonth.intValue());
    }

    public void setValue(LocalDate localDate) {
        setValue(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public void setValue(LocalDateTime dateTime) {
        long millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        setValue(millis);
    }

    public void setValue(long millis) {
        setDate(millis);
    }

    public String millisAsFormattedDate(long millis) {
        return PrimeSelenium.executeScript(
                    "return " + getWidgetByIdScript() + ".jq.data().primeDatePicker.formatDateTime(new Date(" + millis + "));");
    }

    /**
     * Widget API call to set date to this LocalDateTime.
     *
     * @param dateTime the LocalDateTime to set to
     */
    public void setDate(LocalDateTime dateTime) {
        long millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        setDate(millis);
    }

    /**
     * Widget API call to set date to this epoch in millis.
     *
     * @param epoch epoch in milliseconds
     */
    public void setDate(long epoch) {
        PrimeSelenium.executeScript(isDateSelectAjaxified(), getWidgetByIdScript() + ".setDate(new Date(" + epoch + "));");
    }

    /**
     * Gets the JS date value from the widget.
     *
     * @return the JS date value or null
     */
    public String getWidgetDate() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getDate();");
    }

    /**
     * Widget API call to update the overlay popup to this epoch in millis.
     *
     * @param epoch epoch in milliseconds
     */
    public void updateViewDate(long epoch) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".jq.data().primeDatePicker.updateViewDate(null, new Date(" + epoch + "));");
    }

    /**
     * Shows the overlay panel.
     *
     * @return the panel shown
     */
    public WebElement showPanel() {
        if (isEnabled()) {
            PrimeSelenium.wait(110); // due to an async setTimeout in hideOverlay
            if (!getPanel().isDisplayed()) {
                PrimeSelenium.executeScript(getWidgetByIdScript() + ".show()");
            }
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getPanel()));
        }
        return getPanel();
    }

    /**
     * Hides the overlay panel.
     */
    public void hidePanel() {
        if (isEnabled()) {
            if (getPanel().isDisplayed()) {
                PrimeSelenium.executeScript(isCloseAjaxified(), getWidgetByIdScript() + ".hide();");
                PrimeSelenium.wait(110); // due to an async setTimeout in hideOverlay
            }
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getPanel()));
        }
    }

    /**
     * Gets the browser time zone offset.
     *
     * @return the browser time zone offset in milliseconds
     */
    public long getTimezoneOffset() {
        return (Long) PrimeSelenium.executeScript("return new Date().getTimezoneOffset();");
    }

}
