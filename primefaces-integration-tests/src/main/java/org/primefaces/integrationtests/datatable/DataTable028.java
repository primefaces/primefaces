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
package org.primefaces.integrationtests.datatable;

import org.primefaces.integrationtests.datatable.dt028.Dt028Reference;
import org.primefaces.integrationtests.datatable.dt028.Dt028ReferenceService;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable028 implements Serializable {

    private static final long serialVersionUID = -2461536509834660314L;

    private List<Dt028Reference> references;
    private List<Dt028Reference> referencesFiltered;

    private String startData;
    private String resultData;

    @Inject
    private Dt028ReferenceService service;

    @PostConstruct
    public void init() {
        references = service.getReferences();
        startData = getDataAsString("Start:");
    }

    private String getDataAsString(String header) {
        StringBuilder builder = new StringBuilder();
        builder.append(header);
        builder.append("\n");
        for (Dt028Reference reference : references) {
            builder.append(reference.toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    public Object doSaveAll() {
        resultData = getDataAsString("Result:");
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Saved!"));
        return null;
    }

    public Object reset() {
        return "dataTable028.xhtml?faces-redirect=true";
    }

    public List<Dt028Reference> getReferences() {
        return references;
    }

    public String getStartData() {
        return startData;
    }

    public String getResultData() {
        return resultData;
    }
}
