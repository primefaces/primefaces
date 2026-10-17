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
package org.primefaces.showcase.view.theming;

import java.io.Serializable;
import java.util.List;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming-next/overlays.xhtml demo page (dialogs, overlays and messages).
 */
@Named("themingNextOverlaysView")
@ViewScoped
@Getter
@Setter
public class ThemingNextOverlaysView implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> severities = List.of("info", "success", "warn", "error", "fatal");
    private String name;
    private String email;

    /**
     * Adds one message per Faces severity for the given client id or message key.
     */
    private void addAll(String clientId, String suffix) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info" + suffix, "Message content of an info message"));
        context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warn" + suffix, "Message content of a warn message"));
        context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + suffix, "Message content of an error message"));
        context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal" + suffix, "Message content of a fatal message"));
    }

    public void showMessages() {
        addAll("messages-key", "");
    }

    public void showMultipleMessages() {
        addAll("messages-key", " 1");
        addAll("messages-key", " 2");
    }

    public void showGrowl() {
        addAll("growl-key", "");
    }

    public void showStickyGrowl() {
        addAll("growl-sticky-key", " (sticky)");
    }

    public void showInlineMessages() {
        FacesContext context = FacesContext.getCurrentInstance();
        String[] ids = {"msgInfo", "msgWarn", "msgError", "msgFatal"};
        FacesMessage.Severity[] levels = {
            FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_WARN, FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_FATAL
        };
        for (int i = 0; i < ids.length; i++) {
            String label = levels[i].toString().split(" ")[0];
            context.addMessage("form:" + ids[i], new FacesMessage(levels[i], label, label + " detail"));
            context.addMessage("form:" + ids[i] + "Icon", new FacesMessage(levels[i], label, label + " detail"));
            context.addMessage("form:" + ids[i] + "Text", new FacesMessage(levels[i], label, label + " detail"));
        }
    }

    public void confirm() {
        FacesContext.getCurrentInstance().addMessage("growl-key", new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmed", "You have accepted"));
    }

    public void delete() {
        FacesContext.getCurrentInstance().addMessage("growl-key", new FacesMessage(FacesMessage.SEVERITY_WARN, "Deleted", "Record deleted"));
    }
}
