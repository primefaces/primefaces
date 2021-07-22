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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.primefaces.selenium.internal.junit.BootstrapExtension;
import org.primefaces.selenium.internal.junit.PageInjectionExtension;
import org.primefaces.selenium.internal.junit.WebDriverExtension;
import org.primefaces.selenium.spi.WebDriverProvider;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(BootstrapExtension.class)
@ExtendWith(WebDriverExtension.class)
@ExtendWith(PageInjectionExtension.class)
public abstract class AbstractPrimePageTest {

    protected void assertPresent(WebElement element) {
        if (!PrimeSelenium.isElementPresent(element)) {
            Assertions.fail("Element should be present!");
        }
    }

    protected void assertPresent(By by) {
        if (!PrimeSelenium.isElementPresent(by)) {
            Assertions.fail("Element should be present!");
        }
    }

    protected void assertNotPresent(WebElement element) {
        if (PrimeSelenium.isElementPresent(element)) {
            Assertions.fail("Element should not be present!");
        }
    }

    protected void assertNotPresent(By by) {
        if (PrimeSelenium.isElementPresent(by)) {
            Assertions.fail("Element should not be present!");
        }
    }

    protected void assertDisplayed(WebElement element) {
        if (!PrimeSelenium.isElementDisplayed(element)) {
            Assertions.fail("Element should be displayed!");
        }
    }

    protected void assertDisplayed(By by) {
        if (!PrimeSelenium.isElementDisplayed(by)) {
            Assertions.fail("Element should be displayed!");
        }
    }

    protected void assertNotDisplayed(WebElement element) {
        if (PrimeSelenium.isElementDisplayed(element)) {
            Assertions.fail("Element should not be displayed!");
        }
    }

    protected void assertNotDisplayed(By by) {
        if (PrimeSelenium.isElementDisplayed(by)) {
            Assertions.fail("Element should not be displayed!");
        }
    }

    protected void assertEnabled(WebElement element) {
        if (!PrimeSelenium.isElementEnabled(element)) {
            Assertions.fail("Element should be enabled!");
        }
    }

    protected void assertEnabled(By by) {
        if (!PrimeSelenium.isElementEnabled(by)) {
            Assertions.fail("Element should be enabled!");
        }
    }

    protected void assertNotEnabled(WebElement element) {
        if (PrimeSelenium.isElementEnabled(element)) {
            Assertions.fail("Element should not be enabled!");
        }
    }

    protected void assertNotEnabled(By by) {
        if (PrimeSelenium.isElementEnabled(by)) {
            Assertions.fail("Element should not be enabled!");
        }
    }

    protected void assertDisabled(WebElement element) {
        if (PrimeSelenium.isElementEnabled(element)) {
            Assertions.fail("Element should be disabled!");
        }
    }

    protected void assertDisabled(By by) {
        if (PrimeSelenium.isElementEnabled(by)) {
            Assertions.fail("Element should be disabled!");
        }
    }

    protected void assertNotDisabled(WebElement element) {
        if (!PrimeSelenium.isElementEnabled(element)) {
            Assertions.fail("Element should not be disabled!");
        }
    }

    protected void assertNotDisabled(By by) {
        if (!PrimeSelenium.isElementEnabled(by)) {
            Assertions.fail("Element should not be disabled!");
        }
    }

    protected void assertIsAt(AbstractPrimePage page) {
        assertIsAt(page.getLocation());
    }

    protected void assertClickable(WebElement element) {
        if (!PrimeSelenium.isElementClickable(element)) {
            Assertions.fail("Element should be clickable!");
        }
    }

    protected void assertNotClickable(WebElement element) {
        if (PrimeSelenium.isElementClickable(element)) {
            Assertions.fail("Element should not be clickable!");
        }
    }

    protected void assertIsAt(Class<? extends AbstractPrimePage> pageClass) {
        String location;
        try {
            location = PrimeSelenium.getUrl((AbstractPrimePage) pageClass.getDeclaredConstructor().newInstance());
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        assertIsAt(location);
    }

    /**
     * Checks the browse console and asserts there are no SEVERE level messages.
     */
    protected void assertNoJavascriptErrors() {
        LogEntries logEntries = getLogsForType(LogType.BROWSER);
        if (logEntries == null) {
            return;
        }
        List<LogEntry> severe = logEntries.getAll().stream()
                    .filter(l -> l.getLevel() == Level.SEVERE)
                    .collect(Collectors.toList());
        Assertions.assertTrue(severe.isEmpty(), "Javascript errors were detected in the browser console.\r\n" + severe.toString());
    }

    /**
     * Dumps to System.out or System.err any messages found in the browser console.
     */
    protected void printConsole() {
        LogEntries logEntries = getLogsForType(LogType.BROWSER);
        if (logEntries == null) {
            return;
        }
        for (LogEntry log : logEntries) {
            if (log.getLevel() == Level.SEVERE) {
                System.err.println(log.getMessage());
            }
            else {
                System.out.println(log.getMessage());
            }
        }
    }

    /**
     * Utility method for checking the browser console for a specific type of message.
     *
     * @param type the {@link LogType} you are searching for
     * @return either NULL if not available or the {@link LogEntries}
     */
    protected LogEntries getLogsForType(String type) {
        // Firefox does not support https://github.com/mozilla/geckodriver/issues/284
        // Safari does not support https://github.com/SeleniumHQ/selenium/issues/7580
        if (PrimeSelenium.isFirefox() || PrimeSelenium.isSafari()) {
            return null;
        }

        Logs logs = getWebDriver().manage().logs();
        if (logs == null) {
            return null;
        }
        Set<String> types = logs.getAvailableLogTypes();
        if (!types.contains(LogType.BROWSER)) {
            return null;
        }
        return logs.get(type);
    }

    protected void assertIsAt(String relativePath) {
        Assertions.assertTrue(getWebDriver().getCurrentUrl().contains(relativePath));
    }

    protected <T extends AbstractPrimePage> T goTo(Class<T> pageClass) {
        return PrimeSelenium.goTo(pageClass);
    }

    protected WebDriver getWebDriver() {
        return WebDriverProvider.get();
    }

    /**
     * Asserts text of a web element and cleanses it of whitespace issues due to different WebDriver results.
     *
     * @param element the element to check its text
     * @param text the text expected in the element
     */
    protected void assertText(WebElement element, String text) {
        String actual = normalizeSpace(element.getText()).trim();
        String expected = normalizeSpace(text).trim();
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Checks a WebElement if it has a CSS class or classes. If more than one is listed then ALL must be found on the element.
     *
     * @param element the element to check
     * @param cssClasses the CSS class or classes to look for
     */
    protected void assertCss(WebElement element, String... cssClasses) {
        String elementClass = element.getAttribute("class");
        if (elementClass == null) {
            Assertions.fail("Element did not have CSS 'class' attribute.");
            return;
        }

        String[] elementClasses = elementClass.split(" ");

        boolean result = true;
        for (String expected : cssClasses) {
            boolean found = false;
            for (String actual : elementClasses) {
                if (actual.equalsIgnoreCase(expected)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                Assertions.fail("Element expected CSS class '" + expected + "' but was not found in '" + elementClass + "'.");
                break;
            }
        }

        // success
    }

    /**
     * <p>
     * Similar to <a href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize -space</a>
     * </p>
     * <p>
     * The function returns the argument string with whitespace normalized by using to remove leading and trailing whitespace and then replacing sequences of
     * whitespace characters by a single space.
     * </p>
     * In XML Whitespace characters are the same as those allowed by the <a href="http://www.w3.org/TR/REC-xml/#NT-S">S</a> production, which is S ::= (#x20 |
     * #x9 | #xD | #xA)+
     * <p>
     * Java's regexp pattern \s defines whitespace as [ \t\n\x0B\f\r]
     * <p>
     * For reference:
     * </p>
     * <ul>
     * <li>\x0B = vertical tab</li>
     * <li>\f = #xC = form feed</li>
     * <li>#x20 = space</li>
     * <li>#x9 = \t</li>
     * <li>#xA = \n</li>
     * <li>#xD = \r</li>
     * </ul>
     * <p>
     * The difference is that Java's whitespace includes vertical tab and form feed, which this functional will also normalize. Additionally removes control
     * characters (char &lt;= 32) from both ends of this String.
     * </p>
     *
     * @param str the source String to normalize whitespaces from, may be null
     * @return the modified string with whitespace normalized, {@code null} if null String input
     * @see Pattern
     * @see <a href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize-space</a>
     * @since 3.0
     */
    public static String normalizeSpace(final String str) {
        // LANG-1020: Improved performance significantly by normalizing manually instead of using regex
        // See https://github.com/librucha/commons-lang-normalizespaces-benchmark for performance test
        if (str == null || str.length() == 0) {
            return str;
        }
        final int size = str.length();
        final char[] newChars = new char[size];
        int count = 0;
        int whitespacesCount = 0;
        boolean startWhitespaces = true;
        for (int i = 0; i < size; i++) {
            final char actualChar = str.charAt(i);
            final boolean isWhitespace = Character.isWhitespace(actualChar);
            if (isWhitespace) {
                if (whitespacesCount == 0 && !startWhitespaces) {
                    newChars[count++] = " ".charAt(0);
                }
                whitespacesCount++;
            }
            else {
                startWhitespaces = false;
                newChars[count++] = (actualChar == 160 ? 32 : actualChar);
                whitespacesCount = 0;
            }
        }
        if (startWhitespaces) {
            return "";
        }
        return new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0)).trim();
    }

}
