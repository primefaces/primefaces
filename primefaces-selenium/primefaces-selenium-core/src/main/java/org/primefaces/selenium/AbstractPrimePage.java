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
package org.primefaces.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.primefaces.selenium.spi.WebDriverProvider;

public abstract class AbstractPrimePage {

    private WebDriver webDriver;

    public String getBaseLocation() {
        return null;
    }

    public abstract String getLocation();

    public void goTo() {
        PrimeSelenium.goTo(this);
    }

    public boolean isAt() {
        return WebDriverProvider.get().getCurrentUrl().contains(getLocation());
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * Get WebStorage of WebDriver.
     *
     * @return Returns WebStorage of WebDriver when this feature is supported by the browser. Some browsers like Safari (as of january 2021) do not support
     *         WebStorage via WebDriver. In this case null is returned.
     */
    public WebStorage getWebStorage() {
        WebDriver webDriver = this.getWebDriver();

        if (webDriver instanceof EventFiringWebDriver) {
            EventFiringWebDriver driver = (EventFiringWebDriver) webDriver;
            webDriver = driver.getWrappedDriver();
        }

        if (webDriver instanceof WebStorage) {
            return (WebStorage) webDriver;
        }
        else {
            return null;
        }
    }
}
