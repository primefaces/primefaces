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
package org.primefaces.integrationtests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.primefaces.selenium.spi.PrimeSeleniumAdapter;

public class PrimeFacesSeleniumTomEESafariAdapterImpl extends PrimeFacesSeleniumTomEEAdapter implements PrimeSeleniumAdapter {

    @Override
    public WebDriver createWebDriver() {
        SafariOptions safariOptions = new SafariOptions();
        /*
         * Safari does not support headless as of september 2020
         * https://github.com/SeleniumHQ/selenium/issues/5985
         * https://discussions.apple.com/thread/251837694
         */
        //safariOptions.setHeadless(PrimeFacesSeleniumTomEEAdapter.isHeadless());
        safariOptions.setCapability("safari:diagnose", "true");
        return new SafariDriver(safariOptions);
    }

}
