/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

    @FindBy(css = ".ui-message-error-icon")
    private WebElement iconError;
    @FindBy(css = ".ui-message-info-icon")
    private WebElement iconInfo;
    @FindBy(css = ".ui-message-warn-icon")
    private WebElement iconWarn;
    @FindBy(css = ".ui-message-fatal-icon")
    private WebElement iconFatal;
    @FindBy(css = ".ui-message-error-summary")
    private WebElement summaryError;
    @FindBy(css = ".ui-message-info-summary")
    private WebElement summaryInfo;
    @FindBy(css = ".ui-message-warn-summary")
    private WebElement summaryWarn;
    @FindBy(css = ".ui-message-fatal-summary")
    private WebElement summaryFatal;
    @FindBy(css = ".ui-message-error-detail")
    private WebElement detailError;
    @FindBy(css = ".ui-message-info-detail")
    private WebElement detailInfo;
    @FindBy(css = ".ui-message-warn-detail")
    private WebElement detailWarn;
    @FindBy(css = ".ui-message-fatal-detail")
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
