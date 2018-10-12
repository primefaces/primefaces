/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.expression.SearchExpressionFacade;

public class DataExporter implements ActionListener, StateHolder {

    private ValueExpression target;

    private ValueExpression type;

    private ValueExpression fileName;

    private ValueExpression encoding;

    private ValueExpression pageOnly;

    private ValueExpression selectionOnly;

    private MethodExpression preProcessor;

    private MethodExpression postProcessor;

    private ValueExpression repeat;

    private ValueExpression options;

    private MethodExpression onTableRender;

    public DataExporter() {
    }

    public DataExporter(ValueExpression target, ValueExpression type, ValueExpression fileName, ValueExpression pageOnly,
                        ValueExpression selectionOnly, ValueExpression encoding, MethodExpression preProcessor,
                        MethodExpression postProcessor, ValueExpression options, MethodExpression onTableRender) {
        this.target = target;
        this.type = type;
        this.fileName = fileName;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.preProcessor = preProcessor;
        this.postProcessor = postProcessor;
        this.encoding = encoding;
        this.options = options;
        this.onTableRender = onTableRender;
    }

    @Override
    public void processAction(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();

        String tables = (String) target.getValue(elContext);
        String exportAs = (String) type.getValue(elContext);
        String outputFileName = (String) fileName.getValue(elContext);

        String encodingType = "UTF-8";
        if (encoding != null) {
            encodingType = (String) encoding.getValue(elContext);
        }

        boolean repeating = false;
        if (repeat != null) {
            repeating = repeat.isLiteralText()
                        ? Boolean.parseBoolean(repeat.getValue(context.getELContext()).toString())
                        : (Boolean) repeat.getValue(context.getELContext());
        }

        boolean isPageOnly = false;
        if (pageOnly != null) {
            isPageOnly = pageOnly.isLiteralText()
                         ? Boolean.parseBoolean(pageOnly.getValue(context.getELContext()).toString())
                         : (Boolean) pageOnly.getValue(context.getELContext());
        }

        boolean isSelectionOnly = false;
        if (selectionOnly != null) {
            isSelectionOnly = selectionOnly.isLiteralText()
                              ? Boolean.parseBoolean(selectionOnly.getValue(context.getELContext()).toString())
                              : (Boolean) selectionOnly.getValue(context.getELContext());
        }

        ExporterOptions exporterOptions = null;
        if (options != null) {
            exporterOptions = (ExporterOptions) options.getValue(elContext);
        }

        try {
            Exporter exporter = ExporterFactory.getExporterForType(exportAs, exporterOptions);

            if (!repeating) {
                List components = SearchExpressionFacade.resolveComponents(context, event.getComponent(), tables);

                if (components.size() > 1) {
                    exporter.export(context, outputFileName, components, isPageOnly, isSelectionOnly,
                            encodingType, preProcessor, postProcessor, exporterOptions, onTableRender);
                }
                else {
                    UIComponent component = (UIComponent) components.get(0);
                    if (!(component instanceof DataTable)) {
                        throw new FacesException("Unsupported datasource target:\"" + component.getClass().getName()
                                + "\", exporter must target a PrimeFaces DataTable.");
                    }

                    DataTable table = (DataTable) component;
                    exporter.export(context, table, outputFileName, isPageOnly, isSelectionOnly, encodingType,
                            preProcessor, postProcessor, exporterOptions, onTableRender);
                }
            }
            else {
                String[] clientIds = tables.split("\\s+|,");
                exporter.export(context, Arrays.asList(clientIds), outputFileName, isPageOnly, isSelectionOnly, encodingType,
                        preProcessor, postProcessor, exporterOptions, onTableRender);
            }

            context.responseComplete();
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {
        //NoOp
    }

    public void setRepeat(ValueExpression ve) {
        repeat = ve;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;

        target = (ValueExpression) values[0];
        type = (ValueExpression) values[1];
        fileName = (ValueExpression) values[2];
        pageOnly = (ValueExpression) values[3];
        selectionOnly = (ValueExpression) values[4];
        preProcessor = (MethodExpression) values[5];
        postProcessor = (MethodExpression) values[6];
        encoding = (ValueExpression) values[7];
        repeat = (ValueExpression) values[8];
        options = (ValueExpression) values[9];
        onTableRender = (MethodExpression) values[10];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[11];

        values[0] = target;
        values[1] = type;
        values[2] = fileName;
        values[3] = pageOnly;
        values[4] = selectionOnly;
        values[5] = preProcessor;
        values[6] = postProcessor;
        values[7] = encoding;
        values[8] = repeat;
        values[9] = options;
        values[10] = onTableRender;

        return (values);
    }
}
