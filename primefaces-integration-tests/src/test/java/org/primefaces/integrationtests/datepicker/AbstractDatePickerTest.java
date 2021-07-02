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
package org.primefaces.integrationtests.datepicker;

import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;

public class AbstractDatePickerTest extends AbstractPrimePageTest {

    protected void assertDate(WebElement panel, String month, String year) {
        Assertions.assertNotNull(panel);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(panel));
        WebElement title = panel.findElement(By.className("ui-datepicker-title"));
        WebElement monthElement = title.findElement(By.className("ui-datepicker-month"));
        WebElement yearElement = title.findElement(By.className("ui-datepicker-year"));
        Assertions.assertEquals(month.toUpperCase(), monthElement.getText().toUpperCase(Locale.ROOT));
        Assertions.assertEquals(year, yearElement.getText());
    }

    protected void assertTime(WebElement panel, String hours, String minutes, String seconds) {
        assertTime(panel, hours, minutes, seconds, null);
    }

    protected void assertTime(WebElement panel, String hours, String minutes, String seconds, String milliseconds) {
        Assertions.assertNotNull(panel);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(panel));
        WebElement timePicker = panel.findElement(By.className("ui-timepicker"));
        if (hours != null) {
            Assertions.assertEquals(Integer.parseInt(hours),
                        Integer.parseInt(timePicker.findElement(By.cssSelector("div.ui-hour-picker > span")).getText()));
        }
        if (minutes != null) {
            Assertions.assertEquals(Integer.parseInt(minutes),
                        Integer.parseInt(timePicker.findElement(By.cssSelector("div.ui-minute-picker > span")).getText()));
        }
        if (seconds != null) {
            Assertions.assertEquals(Integer.parseInt(seconds),
                        Integer.parseInt(timePicker.findElement(By.cssSelector("div.ui-second-picker > span")).getText()));
        }
        if (milliseconds != null) {
            Assertions.assertEquals(Integer.parseInt(milliseconds),
                        Integer.parseInt(timePicker.findElement(By.cssSelector("div.ui-millisecond-picker > span")).getText()));
        }
    }
}
