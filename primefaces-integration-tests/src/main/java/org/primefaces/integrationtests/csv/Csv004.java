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
package org.primefaces.integrationtests.csv;

import java.io.Serializable;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Csv004 implements Serializable {

    private static final long serialVersionUID = 3759332540993079653L;

    private String name;

    private boolean nameRequired;

    private boolean acceptTermnsAndCondition;


    public void doAjax() {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Ajax-action", "Hello from the server side!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void doNonAjax() {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Non-Ajax-action", "Hello from the server side!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void doNonAjaxWithoutCsv() {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Non-Ajax-action", "Hello from the server side, we skipped the CSV!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
