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
package org.primefaces.selenium.component.base;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractMessage extends AbstractComponent {

    @FindBy(css = ".p-message-error-icon")
    private WebElement iconError;
    @FindBy(css = ".p-message-info-icon")
    private WebElement iconInfo;
    @FindBy(css = ".p-message-warn-icon")
    private WebElement iconWarn;
    @FindBy(css = ".p-message-fatal-icon")
    private WebElement iconFatal;
    @FindBy(css = ".p-message-error-summary")
    private WebElement summaryError;
    @FindBy(css = ".p-message-info-summary")
    private WebElement summaryInfo;
    @FindBy(css = ".p-message-warn-summary")
    private WebElement summaryWarn;
    @FindBy(css = ".p-message-fatal-summary")
    private WebElement summaryFatal;
    @FindBy(css = ".p-message-error-detail")
    private WebElement detailError;
    @FindBy(css = ".p-message-info-detail")
    private WebElement detailInfo;
    @FindBy(css = ".p-message-warn-detail")
    private WebElement detailWarn;
    @FindBy(css = ".p-message-fatal-detail")
    private WebElement detailFatal;

    public WebElement getIconError() {
        return iconError;
    }
    public WebElement getIconInfo() {
        return iconInfo;
    }
    public WebElement getIconWarn() {
        return iconWarn;
    }
    public WebElement getIconFatal() {
        return iconFatal;
    }
    public WebElement getDetailError() {
        return detailError;
    }
    public WebElement getDetailInfo() {
        return detailInfo;
    }
    public WebElement getDetailWarn() {
        return detailWarn;
    }
    public WebElement getDetailFatal() {
        return detailFatal;
    }
    public WebElement getSummaryError() {
        return summaryError;
    }
    public WebElement getSummaryInfo() {
        return summaryInfo;
    }
    public WebElement getSummaryWarn() {
        return summaryWarn;
    }
    public WebElement getSummaryFatal() {
        return summaryFatal;
    }

}
