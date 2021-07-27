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
package org.primefaces.selenium.component;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.internal.ConfigProvider;
import org.primefaces.selenium.spi.WebDriverProvider;

/**
 * Component wrapper for the PrimeFaces {@code p:fileUpload}.
 */
public abstract class FileUpload extends AbstractInputComponent {

    @Override
    public WebElement getInput() {
        // in case of mode=simple skinSimple=false the input element is this element
        boolean isInputFile = "input".equals(getTagName()) && "file".equals(getAttribute("type"));
        return isInputFile ? this : findElement(By.id(getId() + "_input"));
    }

    /**
     * Returns the input´s value.
     * @return the file name
     */
    public String getValue() {
        return getInput().getAttribute("value");
    }

    /**
     * Sets the input's value.
     * This should be the files absolute path.
     * @param value the file name to set
     */
    public void setValue(Serializable value) {
        if (getInput() != this && !PrimeSelenium.isChrome()) {
            // input file cannot be cleared if skinSimple=false or in Chrome
            getInput().clear();
        }
        // ComponentUtils.sendKeys will break tests in Chrome
        getInput().sendKeys(value.toString());
    }

    /**
     * Sets the input's value from given files.
     * @param values the file name(s) to set
     */
    public void setValue(File... values) {
        // several references in the WEB state that (at least) Firefox and Chrome accept
        // multiple filenames separated by a new line character
        String paths = Arrays.stream(values)
                .map(f -> f.getAbsolutePath())
                .collect(Collectors.joining("\n"));
        setValue(paths);
    }

    /**
     * Gets the Upload button of the widget.
     * The button is only rendered in advanced mode.
     * @return the widget's upload button
     */
    public WebElement getAdvancedUploadButton() {
        return findElement(By.cssSelector(".ui-fileupload-buttonbar button.ui-fileupload-upload"));
    }

    /**
     * Gets the Cancel button of the widget.
     * The button is only rendered in advanced mode.
     * @return the widget's cancel button
     */
    public WebElement getAdvancedCancelButton() {
        return findElement(By.cssSelector(".ui-fileupload-buttonbar button.ui-fileupload-cancel"));
    }

    /**
     * Gets the file Cancel button of the widget.
     * The button is only rendered in advanced mode.
     * @param fileName the file name for which to return the cancel button
     * @return the widget's cancel button
     */
    public WebElement getAdvancedCancelButton(String fileName) {
        for (WebElement row : findElements(By.cssSelector(".ui-fileupload-files .ui-fileupload-row"))) {
            WebElement fn = row.findElement(By.cssSelector(".ui-fileupload-filename"));
            if (fn.getText().contains(fileName)) {
                return row.findElement(By.cssSelector("button.ui-fileupload-cancel"));
            }
        }
        throw new NoSuchElementException("cancel button for " + fileName + " not found");
    }

    /**
     * Waits until all selected files are uploaded.
     * This only works in advanced mode.
     * @param additionalElementToWaitFor element to wait for stable GUI
     */
    public void waitAdvancedUntilAllFilesAreUploaded(WebElement additionalElementToWaitFor) {
        ConfigProvider config = ConfigProvider.getInstance();
        WebDriver driver = WebDriverProvider.get();

        // todo: Should there be a separate configuration entry for file upload?
        WebDriverWait wait = new WebDriverWait(driver, config.getAjaxTimeout(), 100);
        // all files are uploaded if widgets file array is empty
        String jsScript = "if (" + getWidgetByIdScript() + ".files.length === 0) return true;";
        wait.until(ExpectedConditions.jsReturnsValue(jsScript));
        PrimeSelenium.guardAjax(this);
        PrimeSelenium.wait(200);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(additionalElementToWaitFor));
    }

    /**
     * Gets the value displayed by the widget.
     *
     * @return the widget's value
     */
    public String getWidgetValue() {
        return findElement(By.className("ui-fileupload-filename")).getText();
    }

    /**
     * Gets the values displayed by the widget.
     *
     * @return the widget's values
     */
    public List<String> getWidgetValues() {
        return findElements(By.className("ui-fileupload-filename")).stream()
                .map(e -> e.getText()).collect(Collectors.toList());
    }

    /**
     * Gets the values displayed by the widget.
     *
     * @return the widget's error messages
     */
    public List<String> getWidgetErrorMessages() {
        return findElements(By.className("ui-messages-error-summary")).stream()
                .map(e -> e.getText()).collect(Collectors.toList());
    }
}
