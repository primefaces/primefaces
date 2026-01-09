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

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.faces.application.Application;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.PreRenderViewEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable038 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private boolean _isPopulated;

    public void doRegisterPopulateDataTable(ComponentSystemEvent e) {
        FacesContext.getCurrentInstance().getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class, new SystemEventListener() {
            @Override
            public void processEvent(SystemEvent event) throws AbortProcessingException {
                if (!_isPopulated) {
                    doPopulateDataTable((DataTable) e.getSource());
                    _isPopulated = true;
                }
            }

            @Override
            public boolean isListenerForSource(Object source) {
                return source instanceof UIViewRoot;
            }
        });
    }

    private void doPopulateDataTable(DataTable dataTable) {
        System.out.println("DataTable #" + dataTable.getId() + " populated with an additional column");

        final FacesContext fctx = FacesContext.getCurrentInstance();
        final Application app = fctx.getApplication();

        // // remove a static column and re-add it later
        // final UIComponent x = dataTable.getChildren().remove(1);

        // add a new purely dynamic column
        final Column uiColumn = (Column) app.createComponent(Column.COMPONENT_TYPE);
        uiColumn.setId("col_d");
        uiColumn.setHeaderText("prog. column");

        dataTable.getChildren().add(uiColumn);

        final HtmlOutputText uiOutputText = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
        uiOutputText.setId("ot_d");
        uiOutputText.setValueExpression("value", app.getExpressionFactory().createValueExpression(fctx.getELContext(), "d#{rowValue}", String.class));

        uiColumn.getChildren().add(uiOutputText);

        // dataTable.getChildren().add(x);
    }

    public List<String> getRowValues() {
        return IntStream.range(1, 100)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());
    }
}
