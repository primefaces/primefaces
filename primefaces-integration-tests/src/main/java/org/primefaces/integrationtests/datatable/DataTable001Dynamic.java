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
package org.primefaces.integrationtests.datatable;

import lombok.Data;
import org.primefaces.component.datatable.DataTable;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
@Data
public class DataTable001Dynamic implements Serializable {

    private static final long serialVersionUID = 8906909722127350633L;

    private List<ProgrammingLanguage> progLanguages;
    private List<ProgrammingLanguage> filteredProgLanguages;
    private boolean globalFilterOnly;

    private List<ColumnModel> columns = new ArrayList<ColumnModel>();

    @Inject
    private ProgrammingLanguageService service;

    @PostConstruct
    public void init() {
        progLanguages = service.getLangs();
        globalFilterOnly = false;
        populateColumns();
    }

    public void populateColumns() {
        columns.add(new ColumnModel("ID", "id", "startsWith"));
        columns.add(new ColumnModel("Type", "type", "startsWith"));
        columns.add(new ColumnModel("Name", "name", "contains"));
        columns.add(new ColumnModel("First appeared", "firstAppeared", "gte"));
    }

    public void resetTable() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:datatable");
        dataTable.reset();

        progLanguages = service.getLangs(); //progLanguages may have been sorted from DataTable
    }

    public void toggleGlobalFilter() {
        setGlobalFilterOnly(!isGlobalFilterOnly());
    }

    public static class ColumnModel implements Serializable {
        private String header;
        private String property;
        private String filterMatchMode;

        public ColumnModel(String header, String property, String filterMatchMode) {
            this.header = header;
            this.property = property;
            this.filterMatchMode = filterMatchMode;
        }
        public String getHeader() {
            return header;
        }
        public String getProperty() {
            return property;
        }
        public String getFilterMatchMode() {
            return filterMatchMode;
        }
    }

    public Object justReturn(Object value) {
        return value;
    }
}
