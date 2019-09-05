/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.export;

import java.io.IOException;
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

import org.primefaces.component.datatable.export.DataTableExporterFactory;
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

    private ValueExpression options;

    private MethodExpression onTableRender;

    private ValueExpression customExporter;

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

        Object customExporterInstance = null;
        if (customExporter != null) {
            customExporterInstance = customExporter.getValue(elContext);
        }

        try {
            Exporter exporter = getExporter(exportAs, exporterOptions, customExporterInstance);
            List<UIComponent> components = SearchExpressionFacade.resolveComponents(context, event.getComponent(), tables);
            ExportConfiguration config = new ExportConfiguration()
                    .setOutputFileName(outputFileName)
                    .setEncodingType(encodingType)
                    .setPageOnly(isPageOnly)
                    .setSelectionOnly(isSelectionOnly)
                    .setOptions(exporterOptions);

            exporter.export(context, components, config);

            context.responseComplete();
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    protected Exporter getExporter(String exportAs, ExporterOptions exporterOptions, Object customExporterInstance) {

        if (customExporterInstance == null) {
            return DataTableExporterFactory.getExporter(exportAs, exporterOptions);
        }

        if (customExporterInstance instanceof Exporter) {
            return (Exporter) customExporterInstance;
        }
        else {
            throw new FacesException("Component " + this.getClass().getName() + " customExporterInstance="
                   + customExporterInstance.getClass().getName() + " does not extend Exporter!");
        }

    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {
        // NOOP
    }

    public ValueExpression getCustomExporter() {
        return customExporter;
    }

    public void setCustomExporter(ValueExpression customExporter) {
        this.customExporter = customExporter;
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
        options = (ValueExpression) values[8];
        onTableRender = (MethodExpression) values[9];
        customExporter = (ValueExpression) values[10];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[12];

        values[0] = target;
        values[1] = type;
        values[2] = fileName;
        values[3] = pageOnly;
        values[4] = selectionOnly;
        values[5] = preProcessor;
        values[6] = postProcessor;
        values[7] = encoding;
        values[8] = options;
        values[9] = onTableRender;
        values[10] = customExporter;

        return (values);
    }
}
