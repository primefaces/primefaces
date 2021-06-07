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

import org.openqa.selenium.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.primefaces.selenium.internal.ConfigProvider;
import org.primefaces.selenium.internal.Guard;
import org.primefaces.selenium.spi.PrimePageFactory;
import org.primefaces.selenium.spi.PrimePageFragmentFactory;
import org.primefaces.selenium.spi.PrimeSeleniumAdapter;
import org.primefaces.selenium.spi.WebDriverProvider;

public final class PrimeSelenium {

    private static final String HEADLESS_MODE_SYSPROP_NAME = "webdriver.headless";

    private static final String HEADLESS_MODE_SYSPROP_VAL_DEFAULT = "false";

    private PrimeSelenium() {
        super();
    }

    /**
     * Gets the current Selenium WebDriver.
     *
     * @return the {@link WebDriver} currently being used
     */
    public static WebDriver getWebDriver() {
        return WebDriverProvider.get();
    }

    /**
     * Creates the PrimeFaces Selenium component for the selector.
     *
     * @param fragmentClass the component class to create like InputText.class
     * @param by the selector to find the component by
     * @param <T> the type of component returned
     * @return the component
     */
    public static <T extends WebElement> T createFragment(Class<T> fragmentClass, By by) {
        WebElement element = getWebDriver().findElement(by);
        return createFragment(fragmentClass, element);
    }

    /**
     * Creates the PrimeFaces Selenium component for the element.
     *
     * @param fragmentClass the component class to create like InputText.class
     * @param element the WebElement to bind this component class to
     * @param <T> the type of component returned
     * @return the component
     */
    public static <T extends WebElement> T createFragment(Class<T> fragmentClass, WebElement element) {
        return PrimePageFragmentFactory.create(fragmentClass, element);
    }

    /**
     * Goto a particular page.
     *
     * @param pageClass the Page class to go to
     * @param <T> the {@link AbstractPrimePage} type
     * @return the {@link AbstractPrimePage} page created
     */
    public static <T extends AbstractPrimePage> T goTo(Class<T> pageClass) {
        WebDriver driver = WebDriverProvider.get();

        T page = PrimePageFactory.create(pageClass, driver);
        driver.get(getUrl(page));

        return page;
    }

    /**
     * Goto a particular page.
     *
     * @param page the {@link AbstractPrimePage} to go to
     */
    public static void goTo(AbstractPrimePage page) {
        WebDriver driver = WebDriverProvider.get();
        driver.get(getUrl(page));
        if (isSafari()) {
            /*
             * Safari has sometimes weird timing issues. (At least on Github Actions.) So wait a bit.
             */
            wait(500);
        }
    }

    /**
     * Gets the URL of the page.
     *
     * @param page the {@link AbstractPrimePage}
     * @return the URL of the page
     */
    public static String getUrl(AbstractPrimePage page) {
        PrimeSeleniumAdapter adapter = ConfigProvider.getInstance().getAdapter();

        String baseLocation = page.getBaseLocation();
        if (adapter != null) {
            baseLocation = adapter.getBaseUrl();
        }

        return baseLocation + page.getLocation();
    }

    /**
     * Gets the URL of the page.
     *
     * @param url the URL to construct
     * @return the full URL
     */
    public static String getUrl(String url) {
        PrimeSeleniumAdapter adapter = ConfigProvider.getInstance().getAdapter();
        return adapter.getBaseUrl() + url;
    }

    /**
     * Checks a WebElement if it has a CSS class or classes. If more than one is listed then ALL must be found on the element.
     *
     * @param element the element to check
     * @param cssClass the CSS class or classes to look for
     * @return true if this element has the CSS class
     */
    public static boolean hasCssClass(WebElement element, String... cssClass) {
        String elementClass = element.getAttribute("class");
        if (elementClass == null) {
            return false;
        }

        String[] elementClasses = elementClass.split(" ");

        boolean result = true;
        for (String expected : cssClass) {
            boolean found = false;
            for (String actual : elementClasses) {
                if (actual.equalsIgnoreCase(expected)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
     * Is the Element present on the page?
     *
     * @param by the selector
     * @return true if present
     */
    public static boolean isElementPresent(By by) {
        try {
            getWebDriver().findElement(by);
            return true;
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Is the Element present on the page?
     *
     * @param element the WebElement to check
     * @return true if present
     */
    public static boolean isElementPresent(WebElement element) {
        try {
            element.isDisplayed(); // just any method to check if NoSuchElementException will be thrown
            return true;
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Is the Element displayed on the page?
     *
     * @param by the selector
     * @return true if displayed
     */
    public static boolean isElementDisplayed(By by) {
        try {
            return getWebDriver().findElement(by).isDisplayed();
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Is the Element displayed on the page?
     *
     * @param element the WebElement to check
     * @return true if displayed
     */
    public static boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Is the Element enabled on the page?
     *
     * @param by the selector
     * @return true if enabled
     */
    public static boolean isElementEnabled(By by) {
        try {
            return getWebDriver().findElement(by).isEnabled();
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Is the Element enabled on the page?
     *
     * @param element the WebElement to check
     * @return true if enabled
     */
    public static boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled() && !hasCssClass(element, "ui-state-disabled");
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Is this element clickable?
     *
     * @param element the WebElement to check for clickable
     * @return true if clickable false if not
     */
    public static boolean isElementClickable(WebElement element) {
        return isElementDisplayed(element) && isElementEnabled(element) && !hasCssClass(element, "ui-state-disabled");
    }

    /**
     * Guard the HTTP request which means wait until it has completed before returning.
     *
     * @param target the target to guard
     * @param <T> the type
     * @return the type
     */
    public static <T> T guardHttp(T target) {
        return Guard.http(target);
    }

    /**
     * Guard the AJAX request which means wait until it has completed before returning.
     *
     * @param target the element to guard
     * @param <T> the type of element
     * @return the element
     */
    public static <T> T guardAjax(T target) {
        return Guard.ajax(target);
    }

    /**
     * Guard the AJAX request which means wait until it has completed before returning. This introduces a delay because some client side activity uses
     * "setTimeout" Javascript to delay the execution of AJAX.
     *
     * @param target the element to guard
     * @param delayInMilliseconds how long to delay before expecting an AJAX event
     * @param <T> the element type
     * @return the element
     */
    public static <T> T guardAjax(T target, int delayInMilliseconds) {
        return Guard.ajax(target, delayInMilliseconds);
    }

    /**
     * Guard the widget script which fires an AJAX request and means wait until it has completed before returning.
     *
     * @param script the script to execute
     * @param args any arguments to the script
     * @param <T> the return type
     * @return the result of running the JavaScript
     */
    public static <T> T guardAjax(String script, Object... args) {
        return Guard.ajax(script, args);
    }

    /**
     * Executes JavaScript in the browser.
     *
     * @param script the script to execute
     * @param args any arguments to the script
     * @param <T> the return type
     * @return the result of running the JavaScript
     */
    public static <T> T executeScript(String script, Object... args) {
        JavascriptExecutor executor = (JavascriptExecutor) getWebDriver();
        T t = (T) executor.executeScript(script, args);
        if (isSafari()) {
            /*
             * Safari has sometimes weird timing issues. (At least on Github Actions.) So wait a bit.
             */
            wait(50);
        }
        return t;
    }

    /**
     * Executes JavaScript in the browser and will wait if the request is an AJAX request.
     *
     * @param isAjaxified true if this is an AJAX request, false if regular script
     * @param script the script to execute
     * @param args any arguments to the script
     * @param <T> the return type
     * @return the result of running the JavaScript
     */
    public static <T> T executeScript(boolean isAjaxified, String script, Object... args) {
        if (isAjaxified) {
            return guardAjax(script, args);
        }
        else {
            return executeScript(script, args);
        }
    }

    /**
     * Wait will ignore instances of NotFoundException that are encountered (thrown) by default in the 'until' condition, and immediately propagate all others.
     * You can add more to the ignore list by calling ignoring(exceptions to add).
     *
     * @return the {@link WebDriverWait}
     */
    public static WebDriverWait waitGui() {
        ConfigProvider config = ConfigProvider.getInstance();
        WebDriver driver = WebDriverProvider.get();
        WebDriverWait wait = new WebDriverWait(driver, config.getGuiTimeout(), 100);
        return wait;
    }

    /**
     * Wait until the document is loaded.
     *
     * @return the {@link WebDriverWait}
     */
    public static WebDriverWait waitDocumentLoad() {
        ConfigProvider config = ConfigProvider.getInstance();
        WebDriver driver = WebDriverProvider.get();

        WebDriverWait wait = new WebDriverWait(driver, config.getDocumentLoadTimeout(), 100);
        wait.until(PrimeExpectedConditions.documentLoaded());

        return wait;
    }

    /**
     * Globally disable all CSS and jQuery animations.
     */
    public static void disableAnimations() {
        executeScript("if (window.PrimeFaces) { $(function() { PrimeFaces.utils.disableAnimations(); }); }");
    }

    /**
     * Globally enable all CSS and jQuery animations.
     */
    public static void enableAnimations() {
        executeScript("if (window.PrimeFaces) { $(function() { PrimeFaces.utils.enableAnimations(); }); }");
    }

    /**
     * Sets a value to a hidden input.
     *
     * @param input the WebElement input to set
     * @param value the value to set
     * @see <a href="https://stackoverflow.com/questions/11858366/how-to-type-some-text-in-hidden-field-in-selenium-webdriver-using-java">Stack Overflow</a>
     */
    public static void setHiddenInput(WebElement input, String value) {
        executeScript(" document.getElementById('" + input.getAttribute("id") + "').value='" + value + "'");
    }

    /**
     * Clears the input field of text.
     *
     * @param input the WebElement input to set
     * @param isAjaxified true if using AJAX
     * @see <a href="https://stackoverflow.com/a/64067604/502366">Safari Hack</a>
     */
    public static void clearInput(WebElement input, boolean isAjaxified) {
        if (PrimeSelenium.isSafari()) {
            // Safari hack https://stackoverflow.com/a/64067604/502366
            String inputText = input.getAttribute("value");
            if (inputText != null && inputText.length() > 0) {
                CharSequence[] clearText = new CharSequence[inputText.length()];
                for (int i = 0; i < inputText.length(); i++) {
                    clearText[i] = Keys.BACK_SPACE;
                }
                if (isAjaxified) {
                    guardAjax(input).sendKeys(clearText);
                }
                else {
                    input.sendKeys(clearText);
                }
            }
        }
        else {
            // CTRL+A then BACKSPACE
            Keys command = PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
            input.sendKeys(Keys.chord(command, "a"));
            if (isAjaxified) {
                guardAjax(input).sendKeys(Keys.BACK_SPACE);
            }
            else {
                input.sendKeys(Keys.BACK_SPACE);
            }
        }
    }

    /**
     * Is the current WebDriver a Chrome driver?
     *
     * @return true if Chrome, false if any other browser
     */
    public static boolean isChrome() {
        Capabilities cap = ((EventFiringWebDriver) getWebDriver()).getCapabilities();
        return "Chrome".equalsIgnoreCase(cap.getBrowserName());
    }

    /**
     * Is the current WebDriver a Firefox driver?
     *
     * @return true if Firefox, false if any other browser
     */
    public static boolean isFirefox() {
        Capabilities cap = ((EventFiringWebDriver) getWebDriver()).getCapabilities();
        return "Firefox".equalsIgnoreCase(cap.getBrowserName());
    }

    /**
     * Is the current WebDriver a Safari driver?
     *
     * @return true if Safari, false if any other browser
     */
    public static boolean isSafari() {
        Capabilities cap = ((EventFiringWebDriver) getWebDriver()).getCapabilities();
        return "Safari".equalsIgnoreCase(cap.getBrowserName());
    }

    /**
     * Are we running on MacOS?
     *
     * @return true if MacOS
     */
    public static boolean isMacOs() {
        String os = System.getProperty("os.name").toUpperCase();
        return (os.contains("DARWIN")) || (os.contains("MAC"));
    }

    /**
     * Is this driver running headless? Meaning without a UI.
     *
     * @return true if headless, false if not
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty(HEADLESS_MODE_SYSPROP_NAME, HEADLESS_MODE_SYSPROP_VAL_DEFAULT));
    }

    /**
     * Waits specified amount of milliseconds.
     *
     * @param milliseconds how many milliseconds to wait
     */
    public static void wait(int milliseconds) {
        if (milliseconds > 0) {
            try {
                Thread.sleep(milliseconds);
            }
            catch (InterruptedException ex) {
                System.err.println("Wait was interrupted!");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }
    }
}
