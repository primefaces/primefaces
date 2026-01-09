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
package org.primefaces.component.export;

import org.primefaces.PrimeFaces;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.StateHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

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
    private MethodExpression preProcessor;
    private MethodExpression postProcessor;
    private ValueExpression options;
    private MethodExpression onTableRender;
    private MethodExpression onRowExport;
    private ValueExpression bufferSize;

    public DataExporter() {
        ResourceUtils.addJavascriptResource(FacesContext.getCurrentInstance(), "filedownload/filedownload.js");
    }

    @Override
    public void processAction(ActionEvent event) {
        FacesContext context = event.getFacesContext();
        ELContext elContext = context.getELContext();

        String tables = target.getValue(elContext);
        String exportAs = type.getValue(elContext);
        String outputFileName = fileName.getValue(elContext);

        String encodingType = StandardCharsets.UTF_8.name();
        if (encoding != null) {
            encodingType = encoding.getValue(elContext);
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

        ExporterOptions exporterOptions = null;
        if (options != null) {
            exporterOptions = options.getValue(elContext);
        }

        Integer bufferSizeTmp = null;
        if (bufferSize != null) {
            bufferSizeTmp = bufferSize.getValue(elContext);
        }

        try {
            List<UIComponent> components = SearchExpressionUtils.contextlessResolveComponents(context, event.getComponent(), tables);
            Class<? extends UIComponent> targetClass = guessTargetClass(components);
            Exporter exporterInstance = DataExporters.get(targetClass, exportAs);

            ExternalContext externalContext = context.getExternalContext();
            String filenameWithExtension = outputFileName + exporterInstance.getFileExtension();
            OutputStream outputStream;

            String contentType = exporterInstance.getContentType();
            if (contentType.startsWith("text/") && LangUtils.isNotBlank(encodingType)) {
                contentType += "; charset=" + encodingType;
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

            ExportConfiguration config = ExportConfiguration.builder()
                    .encodingType(encodingType)
                    .pageOnly(isPageOnly)
                    .selectionOnly(isSelectionOnly)
                    .visibleOnly(isVisibleOnly)
                    .exportHeader(isExportHeader)
                    .exportFooter(isExportFooter)
                    .options(exporterOptions)
                    .preProcessor(preProcessor)
                    .postProcessor(postProcessor)
                    .onTableRender(onTableRender)
                    .onRowExport(onRowExport)
                    .outputStream(outputStream)
                    .bufferSize(bufferSizeTmp)
                    .build();

            exporterInstance.export(context, components, config);

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
        Objects.requireNonNull(targets, DataExporter.class.getSimpleName() + " expects at least one target");
        Set<Class<? extends UIComponent>> classes = targets.stream().map(UIComponent::getClass).collect(Collectors.toSet());

        if (classes.size() > 1) {
            throw new IllegalArgumentException(DataExporter.class.getSimpleName() + "#target should all be the same type");
        }

        return classes.iterator().next();
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
        preProcessor = (MethodExpression) values[8];
        postProcessor = (MethodExpression) values[9];
        encoding = (ValueExpression) values[10];
        options = (ValueExpression) values[11];
        onTableRender = (MethodExpression) values[12];
        onRowExport = (MethodExpression) values[13];
        bufferSize = (ValueExpression) values[14];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[16];

        values[0] = target;
        values[1] = type;
        values[2] = fileName;
        values[3] = pageOnly;
        values[4] = selectionOnly;
        values[5] = visibleOnly;
        values[6] = exportHeader;
        values[7] = exportFooter;
        values[8] = preProcessor;
        values[9] = postProcessor;
        values[10] = encoding;
        values[11] = options;
        values[12] = onTableRender;
        values[13] = onRowExport;
        values[14] = bufferSize;

        return (values);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final DataExporter exporter;

        Builder() {
            exporter = new DataExporter();
        }

        public Builder target(ValueExpression target) {
            exporter.target = target;
            return this;
        }

        public Builder type(ValueExpression type) {
            exporter.type = type;
            return this;
        }

        public Builder fileName(ValueExpression fileName) {
            exporter.fileName = fileName;
            return this;
        }

        public Builder encoding(ValueExpression encoding) {
            exporter.encoding = encoding;
            return this;
        }

        public Builder pageOnly(ValueExpression pageOnly) {
            exporter.pageOnly = pageOnly;
            return this;
        }

        public Builder selectionOnly(ValueExpression selectionOnly) {
            exporter.selectionOnly = selectionOnly;
            return this;
        }

        public Builder visibleOnly(ValueExpression visibleOnly) {
            exporter.visibleOnly = visibleOnly;
            return this;
        }

        public Builder exportHeader(ValueExpression exportHeader) {
            exporter.exportHeader = exportHeader;
            return this;
        }

        public Builder exportFooter(ValueExpression exportFooter) {
            exporter.exportFooter = exportFooter;
            return this;
        }

        public Builder preProcessor(MethodExpression preProcessor) {
            exporter.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(MethodExpression postProcessor) {
            exporter.postProcessor = postProcessor;
            return this;
        }

        public Builder options(ValueExpression options) {
            exporter.options = options;
            return this;
        }

        public Builder onTableRender(MethodExpression onTableRender) {
            exporter.onTableRender = onTableRender;
            return this;
        }

        public Builder onRowExport(MethodExpression onRowExport) {
            exporter.onRowExport = onRowExport;
            return this;
        }

        public Builder bufferSize(ValueExpression bufferSize) {
            exporter.bufferSize = bufferSize;
            return this;
        }

        public DataExporter build() {
            return exporter;
        }
    }
}
