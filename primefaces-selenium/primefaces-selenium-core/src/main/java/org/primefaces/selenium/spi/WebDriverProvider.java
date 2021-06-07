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
package org.primefaces.selenium.spi;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.internal.ConfigProvider;
import org.primefaces.selenium.internal.OnloadScriptsEventListener;

public class WebDriverProvider {

    private static final ThreadLocal<WebDriver> WEB_DRIVER = new ThreadLocal<>();

    private static final int CREATE_WEBDRIVER_RETRIES = 3;

    private WebDriverProvider() {

    }

    public static void set(WebDriver driver) {
        WEB_DRIVER.set(driver);
    }

    public static WebDriver get() {
        return get(false);
    }

    public static WebDriver get(boolean create) {
        WebDriver driver = WEB_DRIVER.get();
        if (driver == null && create) {
            PrimeSeleniumAdapter adapter = ConfigProvider.getInstance().getAdapter();
            int fails = 0;

            do {
                /*
                 * Avoid issues like 2021-01-18T20:31:34.5805460Z org.openqa.selenium.WebDriverException: 2021-01-18T20:31:34.5810490Z
                 * java.net.ConnectException: Failed to connect to localhost/0:0:0:0:0:0:0:1:27231 which sometimes occur during Github Action jobs.
                 */
                try {
                    driver = adapter.createWebDriver();
                }
                catch (WebDriverException ex) {
                    fails++;
                    if (fails >= CREATE_WEBDRIVER_RETRIES) {
                        throw ex;
                    }
                }
            } while (driver == null);

            /*
             * Define window-size for headless-mode. Selenium WebDriver-default seems to be 800x600. This causes issues with modern themes (eg Saga) which use
             * more space for some components. (eg DatePicker-popup)
             */
            if (PrimeSelenium.isHeadless()) {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            }
            else {
                driver.manage().window().setSize(new Dimension(1280, 1000));
            }

            EventFiringWebDriver eventDriver = new EventFiringWebDriver(driver);
            eventDriver.register(new OnloadScriptsEventListener());

            set(eventDriver);

            driver = eventDriver;
        }
        return driver;
    }
}
