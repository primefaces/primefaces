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
import org.primefaces.selenium.internal.ConfigProvider;
import org.primefaces.selenium.internal.Guard;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:confirmDialog}.
 */
public abstract class ConfirmDialog extends Dialog {

    @FindBy(className = "p-confirm-dialog-severity")
    private WebElement icon;

    @FindBy(className = "p-confirm-dialog-message")
    private WebElement message;

    @FindBy(className = "p-confirmdialog-yes")
    private CommandButton yesButton;

    @FindBy(className = "p-confirmdialog-no")
    private CommandButton noButton;

    public WebElement getMessage() {
        return message;
    }

    public WebElement getIcon() {
        return icon;
    }

    public CommandButton getYesButton() {
        return yesButton;
    }

    public CommandButton getNoButton() {
        return noButton;
    }

    /**
     * Answers the dialog with yes. For a global dialog the confirmed command (AJAX request, full page submit or JavaScript
     * function) runs within the click, so it waits until no request is pending and the dialog is hidden.
     * For a non-global dialog the yes button is a command of the page: it guards its own request, hiding the dialog is up to the page.
     */
    public void confirm() {
        if (!getWidgetConfiguration().optBoolean("global", false)) {
            getYesButton().click();
        }
        else {
            guardConfirmedCommand(getYesButton()).click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
        }
    }

    /**
     * Answers the dialog with no.
     */
    public void cancel() {
        getNoButton().click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
    }

    /**
     * Guards the yes button of a global confirm dialog or popup: after the click it waits until whatever the confirmed
     * command started (AJAX request, full page submit or nothing) has completed. No request has to be sent at all,
     * unlike {@link PrimeSelenium#guardAjax(Object)}.
     *
     * @param yesButton the yes button
     * @param <T> the type of the button
     * @return the guarded button
     */
    static <T> T guardConfirmedCommand(T yesButton) {
        return Guard.custom(yesButton, ConfigProvider.getInstance().getTimeoutAjax(),
                    PrimeExpectedConditions.documentLoaded(), PrimeExpectedConditions.notNavigating(),
                    PrimeExpectedConditions.notSubmitting(), PrimeExpectedConditions.ajaxQueueEmpty(),
                    PrimeExpectedConditions.animationNotActive());
    }
}
