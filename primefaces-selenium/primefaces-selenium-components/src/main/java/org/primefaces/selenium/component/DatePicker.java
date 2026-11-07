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
package org.primefaces.selenium.component;

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;
import org.primefaces.selenium.internal.ConfigProvider;
import org.primefaces.selenium.internal.Guard;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Component wrapper for the PrimeFaces {@code p:datePicker}.
 */
public abstract class DatePicker extends AbstractInputComponent {

    /**
     * XPath of the months displayed with {@code numberOfMonths}, relative to the panel.
     */
    private static final String MONTHS = ".//div[contains(concat(' ', normalize-space(@class), ' '), ' p-datepicker-group ')]";

    /**
     * XPath of the day cells of all displayed months without the days of other months, relative to the panel or a month.
     */
    private static final String DAY_CELLS = ".//table[contains(@class, 'p-datepicker-calendar')]"
                + "//td[@role='gridcell' and not(contains(@class, 'p-datepicker-other-month'))]";

    /**
     * XPath of the day cells of other months shown to fill the weeks of the displayed months, relative to the panel.
     */
    private static final String OTHER_MONTH_DAY_CELLS = ".//table[contains(@class, 'p-datepicker-calendar')]"
                + "//td[@role='gridcell' and contains(@class, 'p-datepicker-other-month')]";

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
        WebElement link = showPanel().findElement(By.className("p-datepicker-next"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        return guardViewChange(link);
    }

    /**
     * Gets the Previous Month link in the navigator.
     *
     * @return the Previous Month link
     */
    public WebElement getPreviousMonthLink() {
        WebElement link = showPanel().findElement(By.className("p-datepicker-prev"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        return guardViewChange(link);
    }

    /**
     * Guards the target if changing the displayed month sends a request: with a "viewChange" behavior or with a lazy
     * model, which loads its date metadata also without a behavior.
     *
     * @param target the target which changes the displayed month
     * @param <T> the type of the target
     * @return the guarded or the original target
     */
    private <T> T guardViewChange(T target) {
        return isViewChangeAjaxified() || isLazy() ? PrimeSelenium.guardAjax(target) : target;
    }

    /**
     * Selects a day of the (first) displayed month in the overlay panel. Waits until the requests of the selection
     * completed and, if the selection closes the panel (e.g. {@code focusOnSelect} or the week view), until the panel is
     * hidden.
     *
     * @param day the day to select
     * @return the day selected
     */
    public WebElement selectDay(String day) {
        return selectDay(0, day);
    }

    /**
     * Selects a day of the (first) displayed month in the overlay panel, see {@link #selectDay(String)}.
     *
     * @param day the day of the month to select
     * @return the day selected
     */
    public WebElement selectDay(int day) {
        return selectDay(Integer.toString(day));
    }

    /**
     * Selects a day of a displayed month in the overlay panel, see {@link #selectDay(String)}.
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month to select
     * @return the day selected
     */
    public WebElement selectDay(int monthIndex, String day) {
        showPanel();
        WebElement link = getDay(monthIndex, day);
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(link));
        // a disabled day ignores the click
        if (!isDayDisabled(monthIndex, day)) {
            link = guardSelection(link);
        }
        link.click();
        return link;
    }

    /**
     * Selects a day of a displayed month in the overlay panel, see {@link #selectDay(String)}.
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month to select
     * @return the day selected
     */
    public WebElement selectDay(int monthIndex, int day) {
        return selectDay(monthIndex, Integer.toString(day));
    }

    /**
     * Gets a day of the (first) displayed month, days of other months shown in the panel are ignored. The panel is not
     * shown, use {@link #showPanel()} before clicking the day or use {@link #selectDay(String)}.
     *
     * @param day the day of the month, e.g. "5"
     * @return the day element, a link if the day is selectable
     */
    public WebElement getDay(String day) {
        return getDay(0, day);
    }

    /**
     * Gets a day of the (first) displayed month, see {@link #getDay(String)}.
     *
     * @param day the day of the month
     * @return the day element, a link if the day is selectable
     */
    public WebElement getDay(int day) {
        return getDay(Integer.toString(day));
    }

    /**
     * Gets a day of a displayed month, see {@link #getDay(String)}.
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month, e.g. "5"
     * @return the day element, a link if the day is selectable
     */
    public WebElement getDay(int monthIndex, String day) {
        return getDayCell(monthIndex, day).findElement(By.xpath("./*"));
    }

    /**
     * Gets a day of a displayed month, see {@link #getDay(String)}.
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month
     * @return the day element, a link if the day is selectable
     */
    public WebElement getDay(int monthIndex, int day) {
        return getDay(monthIndex, Integer.toString(day));
    }

    /**
     * Is the day of the (first) displayed month selected, also as part of a selected range or week?
     *
     * @param day the day of the month, e.g. "5"
     * @return true if selected
     */
    public boolean isDaySelected(String day) {
        return isDaySelected(0, day);
    }

    /**
     * Is the day of the (first) displayed month selected, also as part of a selected range or week?
     *
     * @param day the day of the month
     * @return true if selected
     */
    public boolean isDaySelected(int day) {
        return isDaySelected(Integer.toString(day));
    }

    /**
     * Is the day of a displayed month selected, also as part of a selected range or week?
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month, e.g. "5"
     * @return true if selected
     */
    public boolean isDaySelected(int monthIndex, String day) {
        return "true".equals(getDayCell(monthIndex, day).getDomAttribute("aria-selected"));
    }

    /**
     * Is the day of a displayed month selected, also as part of a selected range or week?
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month
     * @return true if selected
     */
    public boolean isDaySelected(int monthIndex, int day) {
        return isDaySelected(monthIndex, Integer.toString(day));
    }

    /**
     * Is the day of the (first) displayed month disabled, e.g. by min / max date or disabled dates?
     *
     * @param day the day of the month, e.g. "5"
     * @return true if disabled
     */
    public boolean isDayDisabled(String day) {
        return isDayDisabled(0, day);
    }

    /**
     * Is the day of the (first) displayed month disabled, e.g. by min / max date or disabled dates?
     *
     * @param day the day of the month
     * @return true if disabled
     */
    public boolean isDayDisabled(int day) {
        return isDayDisabled(Integer.toString(day));
    }

    /**
     * Is the day of a displayed month disabled, e.g. by min / max date or disabled dates?
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month, e.g. "5"
     * @return true if disabled
     */
    public boolean isDayDisabled(int monthIndex, String day) {
        return "true".equals(getDayCell(monthIndex, day).getDomAttribute("aria-disabled"));
    }

    /**
     * Is the day of a displayed month disabled, e.g. by min / max date or disabled dates?
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @param day the day of the month
     * @return true if disabled
     */
    public boolean isDayDisabled(int monthIndex, int day) {
        return isDayDisabled(monthIndex, Integer.toString(day));
    }

    /**
     * Gets all days of all displayed months (see {@code numberOfMonths}), without the days of other months which fill
     * the weeks.
     *
     * @return the day elements
     */
    public List<WebElement> getDays() {
        return getPanel().findElements(By.xpath(DAY_CELLS + "/*"));
    }

    /**
     * Gets the selectable days of all displayed months, without the days of other months, see {@link #getDays()}.
     *
     * @return the day elements
     */
    public List<WebElement> getSelectableDays() {
        return getPanel().findElements(By.xpath(DAY_CELLS + "[not(@aria-disabled='true')]/*"));
    }

    /**
     * Gets the disabled days of all displayed months, without the days of other months, see {@link #getDays()}.
     *
     * @return the day elements
     */
    public List<WebElement> getDisabledDays() {
        return getPanel().findElements(By.xpath(DAY_CELLS + "[@aria-disabled='true']/*"));
    }

    /**
     * Gets the selected days of all displayed months, also as part of a selected range or week, without the days of
     * other months, see {@link #getDays()}.
     *
     * @return the day elements
     */
    public List<WebElement> getSelectedDays() {
        return getPanel().findElements(By.xpath(DAY_CELLS + "[@aria-selected='true']/*"));
    }

    /**
     * Gets the days of other months which fill the first and last week of the displayed months. They are hidden unless
     * {@code showOtherMonths} is set and disabled unless {@code selectOtherMonths} is set.
     *
     * @return the day elements
     */
    public List<WebElement> getOtherMonthDays() {
        return getPanel().findElements(By.xpath(OTHER_MONTH_DAY_CELLS + "/*"));
    }

    /**
     * Gets the disabled days of other months, see {@link #getOtherMonthDays()}.
     *
     * @return the day elements
     */
    public List<WebElement> getDisabledOtherMonthDays() {
        return getPanel().findElements(By.xpath(OTHER_MONTH_DAY_CELLS + "[@aria-disabled='true']/*"));
    }

    /**
     * Gets the displayed months of the date view, one element per month with {@code numberOfMonths} greater than 1. Each
     * contains the header with the title and the grid of the days.
     *
     * @return the month elements
     */
    public List<WebElement> getDisplayedMonths() {
        return getPanel().findElements(By.xpath(MONTHS));
    }

    /**
     * Gets the month in the title of the (first) displayed month: a text element, or a dropdown with {@code monthNavigator}.
     *
     * @return the month element of the title
     */
    public WebElement getTitleMonth() {
        return getPanel().findElement(By.cssSelector(".p-datepicker-title > .p-datepicker-month"));
    }

    /**
     * Gets the month in the title of a displayed month, see {@link #getTitleMonth()}.
     *
     * @param monthIndex the 0-based index of the displayed month, with {@code numberOfMonths} greater than 1
     * @return the month element of the title
     */
    public WebElement getTitleMonth(int monthIndex) {
        return getDisplayedMonths().get(monthIndex).findElement(By.cssSelector(".p-datepicker-title > .p-datepicker-month"));
    }

    /**
     * Gets the year in the title of the (first) displayed month: a text element, or an input or dropdown with
     * {@code yearNavigator}.
     *
     * @return the year element of the title
     */
    public WebElement getTitleYear() {
        return getPanel().findElement(By.cssSelector(".p-datepicker-title > .p-datepicker-year"));
    }

    private WebElement getDayCell(int monthIndex, String day) {
        return getPanel().findElement(By.xpath("(" + MONTHS + ")[" + (monthIndex + 1) + "]" + DAY_CELLS.substring(1)
                    + "[normalize-space(.)='" + day + "']"));
    }

    /**
     * Gets the Clear button on the overlay panel. Its click waits until the requests completed and the panel is hidden.
     *
     * @return the Clear button
     */
    public WebElement getClearButton() {
        WebElement button = showPanel().findElement(By.className("p-datepicker-buttonbar")).findElement(By.className("p-clear-button"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        return guardSelection(button);
    }

    /**
     * Gets the Today button on the overlay panel. Its click waits until the requests completed and, if selecting today
     * closes the panel (e.g. {@code focusOnSelect}), until the panel is hidden.
     *
     * @return the Today button
     */
    public WebElement getTodayButton() {
        WebElement button = showPanel().findElement(By.className("p-datepicker-buttonbar")).findElement(By.className("p-today-button"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        return guardSelection(button);
    }

    /**
     * Guards a target which selects a date or clears the selection. Its requests ("viewChange", "dateSelect", "change",
     * lazy model) are sent within the click. If the selection closes the panel, the widget does so 100ms later, which
     * sends further requests ("close", "viewChange" or lazy model). So it waits out that delay and then until the AJAX
     * queue is empty and no animation is running. No request has to be sent at all, unlike
     * {@link PrimeSelenium#guardAjax(Object)}.
     *
     * @param target the target to guard
     * @param <T> the type of the target
     * @return the guarded target
     */
    private <T> T guardSelection(T target) {
        return Guard.custom(target, 200, ConfigProvider.getInstance().getTimeoutAjax(),
                    PrimeExpectedConditions.animationNotActive(), PrimeExpectedConditions.ajaxQueueEmpty());
    }

    public LocalDateTime getValue() {
        if (hasDate() == false) {
            return null;
        }

        Number epoch = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getDate().getTime();");
        // Move epoch into server-timezone
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch.longValue()), ZoneId.systemDefault());
        return dateTime;
    }

    public LocalDate getValueAsLocalDate() {
        if (hasDate() == false) {
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
                    "return " + getWidgetByIdScript() + ".formatDateTime(new Date(" + millis + "));");
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
     * Checks whether a date is selected
     *
     * @return true if a date is selected.
     */
    public boolean hasDate() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".hasDate();");
    }

    /**
     * Is this a lazy datepicker.
     *
     * @return true if lazy
     */
    public boolean isLazy() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript()
                + ".cfg.lazyModel === undefined ? false : " + getWidgetByIdScript() + ".cfg.lazyModel");
    }

    /**
     * Gets the input value from the widget.
     *
     * @return the input value or null
     */
    public String getInputValue() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".input.val();");
    }

    /**
     * Widget API call to update the overlay popup to this epoch in millis.
     *
     * @param epoch epoch in milliseconds
     */
    public void updateViewDate(long epoch) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".updateViewDate(null, new Date(" + epoch + "));");
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

    /**
     * Open the month select dropdown.
     */
    public void toggleMonthDropdown() {
        WebElement monthDropDown = showPanel().findElement(By.cssSelector("select.p-datepicker-month"));
        monthDropDown.click();
    }

    /**
     * Select a month from the dropdown.
     *
     * @param month the month to select
     */
    public void selectMonthDropdown(int month) {
        Select monthDropDown = new Select(showPanel().findElement(By.cssSelector("select.p-datepicker-month")));
        String value = Integer.toString(month);
        // selecting the displayed month again fires no change event, so there is no request to guard
        if (!value.equals(monthDropDown.getFirstSelectedOption().getDomAttribute("value"))) {
            guardViewChange(monthDropDown).selectByValue(value);
        }
    }

    /**
     * Select a year.
     *
     * @param year the year to select
     */
    public void selectYear(int year) {
        WebElement panel = showPanel();
        try {
            WebElement yearInput = panel.findElement(By.cssSelector("input.p-datepicker-year"));
            yearInput.sendKeys(Integer.toString(year));
        }
        catch (NoSuchElementException e) {
            Select yearDropDown = new Select(showPanel().findElement(By.cssSelector("select.p-datepicker-year")));
            yearDropDown.selectByValue(Integer.toString(year));
        }
    }

    /**
     * Increment the years by count.
     *
     * @param count the number of years to increment
     */
    public void incrementYear(int count) {
        for (int i = 0; i <  count; i++) {
            stepYear(Keys.ARROW_UP);
        }
    }

    /**
     * Decrement the years by count.
     *
     * @param count the number of years to decrement
     */
    public void decrementYear(int count) {
        for (int i = 0; i <  count; i++) {
            stepYear(Keys.ARROW_DOWN);
        }
    }

    private void stepYear(Keys key) {
        // found again for each step, the change of the year renders the panel again
        WebElement yearInput = showPanel().findElement(By.cssSelector("input.p-datepicker-year"));
        yearInput.sendKeys(key);
        // the change of the year displays the year with its request ("viewChange", lazy model) sent within the key event
        PrimeSelenium.waitAjax().until(ExpectedConditions.and(PrimeExpectedConditions.animationNotActive(), PrimeExpectedConditions.ajaxQueueEmpty()));
    }

    /**
     * Gets the hour displayed in the time picker of the panel.
     *
     * @return the hour, with the 12 hour format without AM / PM
     */
    public int getHour() {
        return getTimeValue("p-hour-picker");
    }

    /**
     * Gets the minute displayed in the time picker of the panel.
     *
     * @return the minute
     */
    public int getMinute() {
        return getTimeValue("p-minute-picker");
    }

    /**
     * Gets the second displayed in the time picker of the panel.
     *
     * @return the second
     */
    public int getSecond() {
        return getTimeValue("p-second-picker");
    }

    /**
     * Gets the millisecond displayed in the time picker of the panel.
     *
     * @return the millisecond
     */
    public int getMillisecond() {
        return getTimeValue("p-millisecond-picker");
    }

    /**
     * Gets the AM / PM text displayed in the time picker of the panel with the 12 hour format.
     *
     * @return the localized AM / PM text
     */
    public String getAmPm() {
        return getTimePicker("p-ampm-picker").findElement(By.xpath("./span")).getDomProperty("textContent").trim();
    }

    /**
     * Increments the time by one step of the unit with the up button of the time picker.
     *
     * @param unit {@link ChronoUnit#HOURS}, {@link ChronoUnit#MINUTES}, {@link ChronoUnit#SECONDS} or {@link ChronoUnit#MILLIS}
     */
    public void incrementTime(ChronoUnit unit) {
        clickTimeButton(getTimePickerClass(unit), "p-picker-up");
    }

    /**
     * Decrements the time by one step of the unit with the down button of the time picker.
     *
     * @param unit {@link ChronoUnit#HOURS}, {@link ChronoUnit#MINUTES}, {@link ChronoUnit#SECONDS} or {@link ChronoUnit#MILLIS}
     */
    public void decrementTime(ChronoUnit unit) {
        clickTimeButton(getTimePickerClass(unit), "p-picker-down");
    }

    /**
     * Toggles between AM and PM in the time picker with the 12 hour format.
     */
    public void toggleAmPm() {
        clickTimeButton("p-ampm-picker", "p-picker-down");
    }

    /**
     * Gets the number input of a time segment, which only exists with {@code timeInput="true"}.
     *
     * @param unit the time segment: HOURS, MINUTES, SECONDS or MILLIS
     * @return the input
     */
    public WebElement getTimeInput(ChronoUnit unit) {
        return getTimePicker(getTimePickerClass(unit)).findElement(By.tagName("input"));
    }

    private WebElement getTimePicker(String pickerClass) {
        return getPanel().findElement(By.cssSelector(".p-timepicker > ." + pickerClass));
    }

    private int getTimeValue(String pickerClass) {
        WebElement picker = getTimePicker(pickerClass);
        // with timeInput the value is in a number input, otherwise in a span
        List<WebElement> inputs = picker.findElements(By.tagName("input"));
        String value = inputs.isEmpty()
                    ? picker.findElement(By.xpath("./span")).getDomProperty("textContent")
                    : inputs.get(0).getDomProperty("value");
        return Integer.parseInt(value.trim());
    }

    private void clickTimeButton(String pickerClass, String buttonClass) {
        WebElement button = showPanel().findElement(By.cssSelector(".p-timepicker > ." + pickerClass + " > ." + buttonClass));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        // a time change fires the change event and the "dateSelect" behavior
        if (isDateSelectAjaxified() || isOnchangeAjaxified()) {
            button = PrimeSelenium.guardAjax(button);
        }
        button.click();
    }

    private static String getTimePickerClass(ChronoUnit unit) {
        switch (unit) {
            case HOURS:
                return "p-hour-picker";
            case MINUTES:
                return "p-minute-picker";
            case SECONDS:
                return "p-second-picker";
            case MILLIS:
                return "p-millisecond-picker";
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }
}
