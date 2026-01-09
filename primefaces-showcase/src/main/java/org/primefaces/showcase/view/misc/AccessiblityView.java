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
package org.primefaces.showcase.view.misc;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class AccessiblityView {

    private boolean binary;

    @PostConstruct
    public void init() {

    }

    public void someAction() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("some action was executed"));
    }

    public void someOtherAction() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("some other action was executed"));
    }

    public String getHtmlFragmentButton() {
        return "<h:commandButton value=\"Click\" action=\"#{accessiblityView.someAction()}\">";
    }

    public String getHtmlFragmentDivAsButton() {
        return "<p:remoteCommand name=\"rc\" update=\"@form\" action=\"#{accessiblityView.someOtherAction()}\"/>\n" +
                "<div class=\"fancy-button\" onclick=\"rc()\">Click</div>";
    }

    public String getHtmlFragmentDivAsButtonImproved() {
        return "<p:remoteCommand name=\"rc\" update=\"@form\" action=\"#{accessiblityView.someOtherAction()}\"/>\n" +
                "<div class=\"fancy-button\" onclick=\"rc()\" onkeydown=\"rc()\" tabindex=\"0\">Click</div>";
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

}
