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
package org.primefaces.showcase.view.misc;

import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeApplicationContext;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class CspView implements Serializable {

    private boolean cspEnabled;
    private String userSuppliedInput;

    public CspView() {
        cspEnabled = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig().isCsp();
        userSuppliedInput = "<b>Huhu</b><script>window.cspScriptExecuted=true;alert('XSS');</script>";
    }

    public void check() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Success", "PASS"));
    }

    public String getUserSuppliedInput() {
        return userSuppliedInput;
    }

    public void setUserSuppliedInput(String userSuppliedInput) {
        this.userSuppliedInput = userSuppliedInput;
    }

    public boolean isCspEnabled() {
        return cspEnabled;
    }

    public void executeScript() {
        PrimeFaces.current().executeScript("alert('PASS');");
    }
}
