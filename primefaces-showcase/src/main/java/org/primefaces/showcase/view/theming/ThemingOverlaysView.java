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

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Getter;
import lombok.Setter;

/**
 * Backing bean of the theming overlays section (theming/sections/overlays.xhtml) (dialogs, overlays and messages).
 */
@Named("themingOverlaysView")
@ViewScoped
@Getter
@Setter
public class ThemingOverlaysView implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public void showGrowl() {
        addAll("growl-key", "");
    }

    public void confirm() {
        FacesContext.getCurrentInstance().addMessage("growl-key", new FacesMessage(FacesMessage.SEVERITY_INFO, "Confirmed", "You have accepted"));
    }

    public void delete() {
        FacesContext.getCurrentInstance().addMessage("growl-key", new FacesMessage(FacesMessage.SEVERITY_WARN, "Deleted", "Record deleted"));
    }
}
