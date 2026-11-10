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

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:keyboard}.
 */
public abstract class Keyboard extends AbstractInputComponent {

    private static final String KEYPAD_NOT_ANIMATED = "return $('.keypad-popup').filter(':animated').length === 0;";

    /**
     * Gets the current value of the input.
     *
     * @return the value
     */
    public String getValue() {
        return getInput().getDomProperty("value");
    }

    /**
     * Gets the keypad overlay. All keyboards of a page share the same overlay element.
     *
     * @return the {@code div.keypad-popup} element
     */
    public WebElement getKeypad() {
        return getWebDriver().findElement(By.cssSelector("div.keypad-popup"));
    }

    /**
     * Is the keypad overlay shown?
     *
     * @return true if visible
     */
    public boolean isKeypadVisible() {
        return PrimeSelenium.isElementDisplayed(getKeypad());
    }

    /**
     * Can the value only be entered with the keypad, so the input is read-only?
     *
     * @return true if keypad only
     */
    public boolean isKeypadOnly() {
        return getWidgetConfiguration().optBoolean("keypadOnly", true);
    }

    /**
     * Shows the keypad overlay, if hidden: clicks the input (shows the keypad on focus), or the trigger button if the
     * keypad is shown on button click only.
     */
    public void showKeypad() {
        if (!isKeypadVisible()) {
            if ("button".equals(getWidgetConfiguration().optString("showOn"))) {
                findElement(By.xpath("./following-sibling::*[contains(@class, 'keypad-trigger')]")).click();
            }
            else if (Boolean.TRUE.equals(PrimeSelenium.executeScript("return document.activeElement === arguments[0];", getInput()))) {
                // already focused, so a click does not fire the focus event which shows the keypad
                PrimeSelenium.executeScript("$(arguments[0]).keypad('show');", getInput());
            }
            else {
                getInput().click();
            }
            PrimeSelenium.waitGui().until(ExpectedConditions.and(
                    PrimeExpectedConditions.visibleAndAnimationComplete(getKeypad()),
                    PrimeExpectedConditions.script(KEYPAD_NOT_ANIMATED)));
        }
    }

    /**
     * Hides the keypad overlay via the widget API, if shown.
     */
    public void hideKeypad() {
        if (isKeypadVisible()) {
            PrimeSelenium.executeScript("$(arguments[0]).keypad('hide');", getInput());
            waitKeypadHidden();
        }
    }

    /**
     * Gets the labels of the character keys of the keypad (the special keys like "close" or "back" are not included).
     *
     * @return the key labels in rendered order
     */
    public List<String> getKeyLabels() {
        return getKeypad().findElements(By.cssSelector("button.keypad-key")).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Clicks the character key with the given label on the shown keypad.
     *
     * @param key the label of the key, e.g. "a" or "1"
     */
    public void pressKey(String key) {
        WebElement button = getKeypad().findElements(By.cssSelector("button.keypad-key")).stream()
                .filter(b -> key.equals(b.getText()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no keypad key '" + key + "'"));
        clickKey(button, true);
    }

    /**
     * Enters the text by clicking the character keys on the shown keypad, one key per character.
     *
     * @param text the text to enter
     */
    public void typeWithKeypad(String text) {
        for (int i = 0; i < text.length(); i++) {
            pressKey(String.valueOf(text.charAt(i)));
        }
    }

    /**
     * Gets a special key of the shown keypad.
     *
     * @param name the name of the special key: "close", "clear", "back", "shift", "spacebar", "enter" or "tab"
     * @return the button
     */
    public WebElement getSpecialKey(String name) {
        return getKeypad().findElement(By.cssSelector("button.keypad-special.keypad-" + name));
    }

    /**
     * Clicks a special key of the shown keypad. Waits until the keypad is hidden for "close". Guards AJAX only for keys
     * that change the value: never for "close" and "shift", for "clear" and "back" only if the input is not empty.
     *
     * @param name the name of the special key: "close", "clear", "back", "shift", "spacebar", "enter" or "tab"
     */
    public void pressSpecialKey(String name) {
        boolean changesValue;
        switch (name) {
            case "close":
            case "shift":
                changesValue = false;
                break;
            case "clear":
            case "back":
                changesValue = !getValue().isEmpty();
                break;
            default:
                changesValue = true;
                break;
        }
        clickKey(getSpecialKey(name), changesValue);
        if ("close".equals(name)) {
            waitKeypadHidden();
        }
    }

    private void clickKey(WebElement button, boolean changesValue) {
        if (changesValue && isOnchangeAjaxified()) {
            PrimeSelenium.guardAjax(button).click();
        }
        else {
            button.click();
        }
    }

    private void waitKeypadHidden() {
        PrimeSelenium.waitGui().until(ExpectedConditions.and(
                PrimeExpectedConditions.invisibleAndAnimationComplete(getKeypad()),
                PrimeExpectedConditions.script(KEYPAD_NOT_ANIMATED)));
    }
}
