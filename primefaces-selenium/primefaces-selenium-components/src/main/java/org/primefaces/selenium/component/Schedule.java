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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:schedule}.
 */
public abstract class Schedule extends AbstractComponent {

    /**
     * Rendered events of all views (dayGrid, timeGrid, list), without background events and the drag mirror.
     */
    private static final String EVENT_SELECTOR = ".fc-event:not(.fc-bg-event):not(.fc-event-mirror)";

    /**
     * Selects either a date or event or any other element in the schedule by its CSS class.
     *
     * @param cssClass the CSS class to select
     */
    public void select(String cssClass) {
        WebElement element = findElement(By.className(cssClass));
        PrimeSelenium.guardAjax(element).click();
    }

    /**
     * Updates and refreshes the schedule view refetching all events
     */
    public void update() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".update();");
    }

    public WebElement getTodayButton() {
        return getButton("fc-today-button");
    }

    public WebElement getMonthButton() {
        return getButton("fc-dayGridMonth-button");
    }

    public WebElement getWeekButton() {
        return getButton("fc-timeGridWeek-button");
    }

    public WebElement getDayButton() {
        return getButton("fc-timeGridDay-button");
    }

    public WebElement getButton(String buttonClass) {
        return findElement(By.className(buttonClass));
    }

    /**
     * Gets the events rendered in the current view (dayGrid, timeGrid and list views), background events excluded.
     *
     * @return the event elements in DOM order
     */
    public List<WebElement> getEventElements() {
        return findElements(By.cssSelector(EVENT_SELECTOR));
    }

    /**
     * Gets the events rendered in the day cell of today ({@code .fc-day-today}, as determined by the schedule) of a dayGrid or timeGrid view.
     *
     * @return the event elements of today in DOM order
     */
    public List<WebElement> getTodaysEventElements() {
        return findElements(By.cssSelector(".fc-day-today " + EVENT_SELECTOR));
    }

    /**
     * Gets the first rendered event with the given title.
     *
     * @param title the event title
     * @return the event element or {@code null} if no event with this title is rendered
     */
    public WebElement getEventElement(String title) {
        for (WebElement event : getEventElements()) {
            if (title.equals(getEventTitle(event))) {
                return event;
            }
        }
        return null;
    }

    /**
     * Gets the displayed title of an event.
     *
     * @param event the event element, see {@link #getEventElements()}
     * @return the title or {@code null} if the event shows no title
     */
    public String getEventTitle(WebElement event) {
        List<WebElement> title = event.findElements(By.cssSelector(".fc-event-title, .fc-list-event-title"));
        return title.isEmpty() ? null : title.get(0).getText();
    }

    /**
     * Gets the displayed time of an event, formatted by the schedule (locale, timeZone, clientTimeZone).
     *
     * @param event the event element, see {@link #getEventElements()}
     * @return the time or {@code null} if the event shows no time (e.g. all-day events)
     */
    public String getEventTime(WebElement event) {
        List<WebElement> time = event.findElements(By.cssSelector(".fc-event-time, .fc-list-event-time"));
        return time.isEmpty() ? null : time.get(0).getText();
    }

    /**
     * Selects (clicks) the event at the given index of {@link #getEventElements()}, waiting until it is rendered.
     *
     * @param index the index of the event in the current view
     * @see #selectEvent(WebElement)
     */
    public void selectEvent(int index) {
        WebElement event = PrimeSelenium.waitGui().ignoring(StaleElementReferenceException.class).until(d -> {
            List<WebElement> events = getEventElements();
            return events.size() > index ? events.get(index) : null;
        });
        selectEvent(event);
    }

    /**
     * Selects (clicks) the first event with the given title, waiting until it is rendered.
     *
     * @param title the event title
     * @see #selectEvent(WebElement)
     */
    public void selectEvent(String title) {
        WebElement event = PrimeSelenium.waitGui().ignoring(StaleElementReferenceException.class).until(d -> getEventElement(title));
        selectEvent(event);
    }

    /**
     * Selects (clicks) an event, guarded if the {@code eventSelect} AJAX behavior is attached. Waits until the widget's double-click
     * window (500 ms after the previous event click) is over first, otherwise the click is handled as {@code eventDblSelect}.
     *
     * @param event the event element, see {@link #getEventElements()}
     */
    public void selectEvent(WebElement event) {
        PrimeSelenium.waitGui().until(d -> Boolean.TRUE.equals(PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".doubleClick == null;")));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(event));

        // events with a URL open it instead of firing eventSelect
        if (event.getDomAttribute("href") == null && ComponentUtils.hasAjaxBehavior(getRoot(), "eventSelect")) {
            PrimeSelenium.guardAjax(event).click();
        }
        else {
            event.click();
        }
    }

}
