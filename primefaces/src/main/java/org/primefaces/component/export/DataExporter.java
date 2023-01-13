/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.export.DataTableExporterFactory;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.export.TreeTableExporterFactory;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.ResourceUtils;

public class DataExporter implements ActionListener, StateHolder {

    private ValueExpression target;
    private ValueExpression type;
    private ValueExpression fileName;
    private ValueExpression encoding;
    private ValueExpression pageOnly;
    private ValueExpression selectionOnly;
    private ValueExpression visibleOnly;
    private ValueExpression exportHeader;
    private ValueExpression exportFooter;
    private ValueExpression useLocale;
    private MethodExpression preProcessor;
    private MethodExpression postProcessor;
    private ValueExpression options;
    private MethodExpression onTableRender;
    private ValueExpression exporter;

    public DataExporter() {
        ResourceUtils.addComponentResource(FacesContext.getCurrentInstance(), "filedownload/filedownload.js");
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

        boolean isVisibleOnly = false;
        if (visibleOnly != null) {
            isVisibleOnly = visibleOnly.isLiteralText()
                        ? Boolean.parseBoolean(visibleOnly.getValue(context.getELContext()).toString())
                        : (Boolean) visibleOnly.getValue(context.getELContext());
        }

        boolean isExportHeader = true;
        if (exportHeader != null) {
            isExportHeader = exportHeader.isLiteralText()
                        ? Boolean.parseBoolean(exportHeader.getValue(context.getELContext()).toString())
                        : (Boolean) exportHeader.getValue(context.getELContext());
        }

        boolean isExportFooter = true;
        if (exportFooter != null) {
            isExportFooter = exportFooter.isLiteralText()
                        ? Boolean.parseBoolean(exportFooter.getValue(context.getELContext()).toString())
                        : (Boolean) exportFooter.getValue(context.getELContext());
        }

        boolean isUseLocale = true;
        if (useLocale != null) {
            isUseLocale = useLocale.isLiteralText()
                        ? Boolean.parseBoolean(useLocale.getValue(context.getELContext()).toString())
                        : (Boolean) useLocale.getValue(context.getELContext());
        }

        ExporterOptions exporterOptions = null;
        if (options != null) {
            exporterOptions = (ExporterOptions) options.getValue(elContext);
        }

        Object customExporterInstance = null;
        if (exporter != null) {
            customExporterInstance = exporter.getValue(elContext);
        }

        try {
            List<UIComponent> components = SearchExpressionFacade.resolveComponents(context, event.getComponent(), tables);
            Class<? extends UIComponent> targetClass = guessTargetClass(components);
            Exporter exporterInstance = getExporter(exportAs, exporterOptions, customExporterInstance, targetClass);
            ExportConfiguration config = ExportConfiguration.builder()
                        .outputFileName(outputFileName)
                        .encodingType(encodingType)
                        .pageOnly(isPageOnly)
                        .selectionOnly(isSelectionOnly)
                        .visibleOnly(isVisibleOnly)
                        .exportHeader(isExportHeader)
                        .useLocale(isUseLocale)
                        .exportFooter(isExportFooter)
                        .options(exporterOptions)
                        .preProcessor(preProcessor)
                        .postProcessor(postProcessor)
                        .onTableRender(onTableRender)
                        .build();

            ExternalContext externalContext = context.getExternalContext();
            String filenameWithExtension = config.getOutputFileName() + exporterInstance.getFileExtension();
            OutputStream outputStream;

            String contentType = exporterInstance.getContentType();
            if (contentType.startsWith("text/") && LangUtils.isNotBlank(config.getEncodingType())) {
                contentType += "; charset=" + config.getEncodingType();
            }

            if (PrimeFaces.current().isAjaxRequest()) {
                outputStream = new ByteArrayOutputStream();
            }
            else {
                outputStream = context.getExternalContext().getResponseOutputStream();
                externalContext.setResponseContentType(contentType);
                setResponseHeader(externalContext, ComponentUtils.createContentDisposition("attachment", filenameWithExtension));
                addResponseCookie(context);
            }

            exporterInstance.setExportConfiguration(config);
            exporterInstance.export(context, components, outputStream, config);

            if (PrimeFaces.current().isAjaxRequest()) {
                ajaxDownload(filenameWithExtension, ((ByteArrayOutputStream) outputStream).toByteArray(), contentType, context);
            }
            else {
                context.responseComplete();
            }
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    protected Class<? extends UIComponent> guessTargetClass(List<UIComponent> targets) {
        Class<? extends UIComponent> targetClass = null;
        if (targets != null) {
            for (UIComponent current : targets) {
                if (current instanceof DataTable) {
                    targetClass = DataTable.class;
                }
                else if (current instanceof TreeTable) {
                    targetClass = TreeTable.class;
                }
            }
        }
        return targetClass;
    }

    protected Exporter getExporter(String exportAs, ExporterOptions exporterOptions, Object customExporterInstance, Class<? extends UIComponent> targetClass) {

        if (customExporterInstance != null) {
            if (customExporterInstance instanceof Exporter) {
                return (Exporter) customExporterInstance;
            }
            else {
                throw new FacesException("Component " + getClass().getName() + " customExporterInstance="
                            + customExporterInstance.getClass().getName() + " does not implement Exporter!");
            }
        }

        if (targetClass != null && TreeTable.class.isAssignableFrom(targetClass)) {
            return TreeTableExporterFactory.getExporter(exportAs, exporterOptions);
        }

        return DataTableExporterFactory.getExporter(exportAs, exporterOptions);
    }

    private void ajaxDownload(String filenameWithExtension, byte[] content, String contentType, FacesContext context) {
        String base64 = Base64.getEncoder().withoutPadding().encodeToString(content);
        String data = "data:" + contentType + ";base64," + base64;

        String monitorKeyCookieName = ResourceUtils.getMonitorKeyCookieName(context, null);
        PrimeFaces.current().executeScript(String.format("PrimeFaces.download('%s', '%s', '%s', '%s')",
                    data, contentType, filenameWithExtension, monitorKeyCookieName));
    }

    protected void setResponseHeader(ExternalContext externalContext, String contentDisposition) {
        ResourceUtils.addNoCacheControl(externalContext);
        externalContext.setResponseHeader("Content-disposition", contentDisposition);
    }

    protected void addResponseCookie(FacesContext context) {
        String monitorKeyCookieName = ResourceUtils.getMonitorKeyCookieName(context, null);
        ResourceUtils.addResponseCookie(context, monitorKeyCookieName, "true", null);
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {
        // NOOP
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;

        target = (ValueExpression) values[0];
        type = (ValueExpression) values[1];
        fileName = (ValueExpression) values[2];
        pageOnly = (ValueExpression) values[3];
        selectionOnly = (ValueExpression) values[4];
        visibleOnly = (ValueExpression) values[5];
        exportHeader = (ValueExpression) values[6];
        exportFooter = (ValueExpression) values[7];
        useLocale = (ValueExpression) values[8];
        preProcessor = (MethodExpression) values[9];
        postProcessor = (MethodExpression) values[10];
        encoding = (ValueExpression) values[11];
        options = (ValueExpression) values[12];
        onTableRender = (MethodExpression) values[13];
        exporter = (ValueExpression) values[14];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[15];

        values[0] = target;
        values[1] = type;
        values[2] = fileName;
        values[3] = pageOnly;
        values[4] = selectionOnly;
        values[5] = visibleOnly;
        values[6] = exportHeader;
        values[7] = exportFooter;
        values[8] = useLocale;
        values[9] = preProcessor;
        values[10] = postProcessor;
        values[11] = encoding;
        values[12] = options;
        values[13] = onTableRender;
        values[14] = exporter;

        return (values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ValueExpression target;
        private ValueExpression type;
        private ValueExpression fileName;
        private ValueExpression encoding;
        private ValueExpression pageOnly;
        private ValueExpression selectionOnly;
        private ValueExpression visibleOnly;
        private ValueExpression exportHeader;
        private ValueExpression exportFooter;
        private ValueExpression useLocale;
        private MethodExpression preProcessor;
        private MethodExpression postProcessor;
        private ValueExpression options;
        private MethodExpression onTableRender;
        private ValueExpression exporter;

        Builder() {
        }

        public Builder target(ValueExpression target) {
            this.target = target;
            return this;
        }

        public Builder type(ValueExpression type) {
            this.type = type;
            return this;
        }

        public Builder fileName(ValueExpression fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder encoding(ValueExpression encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder pageOnly(ValueExpression pageOnly) {
            this.pageOnly = pageOnly;
            return this;
        }

        public Builder selectionOnly(ValueExpression selectionOnly) {
            this.selectionOnly = selectionOnly;
            return this;
        }

        public Builder visibleOnly(ValueExpression visibleOnly) {
            this.visibleOnly = visibleOnly;
            return this;
        }

        public Builder exportHeader(ValueExpression exportHeader) {
            this.exportHeader = exportHeader;
            return this;
        }

        public Builder exportFooter(ValueExpression exportFooter) {
            this.exportFooter = exportFooter;
            return this;
        }

        public Builder useLocale(ValueExpression useLocale) {
            this.useLocale = useLocale;
            return this;
        }

        public Builder preProcessor(MethodExpression preProcessor) {
            this.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(MethodExpression postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        public Builder options(ValueExpression options) {
            this.options = options;
            return this;
        }

        public Builder onTableRender(MethodExpression onTableRender) {
            this.onTableRender = onTableRender;
            return this;
        }

        public Builder exporter(ValueExpression exporter) {
            this.exporter = exporter;
            return this;
        }

        public DataExporter build() {
            DataExporter exporter = new DataExporter();
            exporter.target = this.target;
            exporter.type = this.type;
            exporter.fileName = this.fileName;
            exporter.encoding = this.encoding;
            exporter.pageOnly = this.pageOnly;
            exporter.selectionOnly = this.selectionOnly;
            exporter.visibleOnly = this.visibleOnly;
            exporter.exportHeader = this.exportHeader;
            exporter.exportFooter = this.exportFooter;
            exporter.useLocale = this.useLocale;
            exporter.preProcessor = this.preProcessor;
            exporter.postProcessor = this.postProcessor;
            exporter.options = this.options;
            exporter.onTableRender = this.onTableRender;
            exporter.exporter = this.exporter;
            return exporter;
        }

        @Override
        public String toString() {
            return "DataExporter.DataExporterBuilder(target=" + this.target + ", type=" + this.type + ", fileName=" + this.fileName + ", encoding="
                        + this.encoding + ", pageOnly=" + this.pageOnly + ", selectionOnly=" + this.selectionOnly + ", visibleOnly=" + this.visibleOnly
                        + ", exportHeader=" + this.exportHeader + ", exportFooter=" + this.exportFooter + ", preProcessor=" + this.preProcessor
                        + ", postProcessor=" + this.postProcessor + ", options=" + this.options + ", onTableRender=" + this.onTableRender + ", exporter="
                        + this.exporter + ")";
        }
    }
}
