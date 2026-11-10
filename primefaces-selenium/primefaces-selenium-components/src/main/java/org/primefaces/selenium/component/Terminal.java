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
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:terminal}.
 */
public abstract class Terminal extends AbstractComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindBy(className = "p-terminal-content")
    private WebElement content;

    @FindBy(className = "p-terminal-prompt")
    private WebElement prompt;

    /**
     * Gets the command input.
     *
     * @return the input element
     */
    public WebElement getInput() {
        return input;
    }

    /**
     * Gets the content with the entered commands and their responses.
     *
     * @return the content element
     */
    public WebElement getContent() {
        return content;
    }

    /**
     * Gets the prompt in front of the command input.
     *
     * @return the prompt element
     */
    public WebElement getPrompt() {
        return prompt;
    }

    /**
     * Gets the welcome message.
     *
     * @return the welcome message, null if none is rendered
     */
    public String getWelcomeMessage() {
        List<WebElement> welcome = findElements(By.xpath("./div[following-sibling::div[contains(@class, 'p-terminal-content')]]"));
        return welcome.isEmpty() ? null : welcome.get(0).getText();
    }

    /**
     * Gets the current text of the command input.
     *
     * @return the text
     */
    public String getValue() {
        return input.getDomProperty("value");
    }

    /**
     * Replaces the text of the command input without executing it.
     *
     * @param text the new text
     */
    public void setValue(String text) {
        ComponentUtils.replaceText(input, text);
    }

    /**
     * Executes the text of the command input (ENTER) and waits until the response is shown.
     */
    public void execute() {
        PrimeSelenium.guardAjax(input).sendKeys(Keys.ENTER);
    }

    /**
     * Enters and executes the command and waits until the response is shown.
     *
     * @param command the command with its arguments, e.g. "greet World"
     */
    public void sendCommand(String command) {
        setValue(command);
        execute();
    }

    /**
     * Requests the autocompletion of the text of the command input (TAB) and waits until it is applied. A single
     * match replaces the text, several matches are listed in the content.
     */
    public void autoComplete() {
        PrimeSelenium.guardAjax(input).sendKeys(Keys.TAB);
    }

    /**
     * Gets the entries of the content, each one an entered command with its response or autocomplete matches.
     *
     * @return the entry {@code div} elements
     */
    public List<WebElement> getEntries() {
        return content.findElements(By.xpath("./div"));
    }

    /**
     * Gets the entered commands shown in the content.
     *
     * @return the commands in entered order
     */
    public List<String> getCommands() {
        return getEntries().stream()
                .map(e -> e.findElement(By.className("p-terminal-command")).getText())
                .collect(Collectors.toList());
    }

    /**
     * Gets the responses shown in the content.
     *
     * @return the responses in entered order, an empty text for an entry without response
     */
    public List<String> getResponses() {
        return getEntries().stream()
                .map(e -> e.findElements(By.xpath("./div[not(contains(@class, 'p-terminal-autocomplete'))]")))
                .map(r -> r.isEmpty() ? "" : r.get(0).getText())
                .collect(Collectors.toList());
    }

    /**
     * Gets the response of the last command.
     *
     * @return the response, null if no command was entered
     */
    public String getLastResponse() {
        List<String> responses = getResponses();
        return responses.isEmpty() ? null : responses.get(responses.size() - 1);
    }

    /**
     * Gets the autocomplete matches listed for the last autocompletion with several matches.
     *
     * @return the matches, empty if none are listed
     */
    public List<String> getAutoCompleteMatches() {
        return getAutoCompleteMatchLinks().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Clicks a listed autocomplete match, which replaces the text of the command input.
     *
     * @param match the match
     */
    public void selectAutoCompleteMatch(String match) {
        getAutoCompleteMatchLinks().stream()
                .filter(a -> match.equals(a.getText()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no autocomplete match '" + match + "'"))
                .click();
    }

    /**
     * Shows the previous command of the history in the command input (ARROW UP).
     */
    public void previousCommand() {
        input.sendKeys(Keys.ARROW_UP);
    }

    /**
     * Shows the next command of the history in the command input (ARROW DOWN), an empty input after the last one.
     */
    public void nextCommand() {
        input.sendKeys(Keys.ARROW_DOWN);
    }

    /**
     * Clears the content and the command input via the widget API.
     */
    @Override
    public void clear() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".clear();");
    }

    private List<WebElement> getAutoCompleteMatchLinks() {
        List<WebElement> lists = content.findElements(By.className("p-terminal-autocomplete"));
        return lists.isEmpty() ? List.of() : lists.get(lists.size() - 1).findElements(By.tagName("a"));
    }
}
