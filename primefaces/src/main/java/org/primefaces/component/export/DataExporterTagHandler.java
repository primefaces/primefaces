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

import jakarta.el.ELException;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.component.ActionSource;
import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;

public class DataExporterTagHandler extends TagHandler {

    private final TagAttribute target;
    private final TagAttribute type;
    private final TagAttribute fileName;
    private final TagAttribute pageOnly;
    private final TagAttribute selectionOnly;
    private final TagAttribute visibleOnly;
    private final TagAttribute exportHeader;
    private final TagAttribute exportFooter;
    private final TagAttribute preProcessor;
    private final TagAttribute postProcessor;
    private final TagAttribute encoding;
    private final TagAttribute options;
    private final TagAttribute onTableRender;
    private final TagAttribute onRowExport;
    private final TagAttribute bufferSize;

    public DataExporterTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        target = getRequiredAttribute("target");
        type = getRequiredAttribute("type");
        fileName = getRequiredAttribute("fileName");
        pageOnly = getAttribute("pageOnly");
        selectionOnly = getAttribute("selectionOnly");
        visibleOnly = getAttribute("visibleOnly");
        exportHeader = getAttribute("exportHeader");
        exportFooter = getAttribute("exportFooter");
        encoding = getAttribute("encoding");
        preProcessor = getAttribute("preProcessor");
        postProcessor = getAttribute("postProcessor");
        options = getAttribute("options");
        onTableRender = getAttribute("onTableRender");
        onRowExport = getAttribute("onRowExport");
        bufferSize = getAttribute("bufferSize");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws ELException {
        if (!ComponentHandler.isNew(parent)) {
            return;
        }

        ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
        ValueExpression typeVE = type.getValueExpression(faceletContext, Object.class);
        ValueExpression fileNameVE = fileName.getValueExpression(faceletContext, Object.class);
        ValueExpression pageOnlyVE = null;
        ValueExpression selectionOnlyVE = null;
        ValueExpression visibleOnlyVE = null;
        ValueExpression exportHeaderVE = null;
        ValueExpression exportFooterVE = null;
        ValueExpression encodingVE = null;
        MethodExpression preProcessorME = null;
        MethodExpression postProcessorME = null;
        ValueExpression optionsVE = null;
        MethodExpression onTableRenderME = null;
        ValueExpression exporterVE = null;
        MethodExpression onRowExportME = null;
        ValueExpression bufferSizeVE = null;

        if (encoding != null) {
            encodingVE = encoding.getValueExpression(faceletContext, Object.class);
        }
        if (pageOnly != null) {
            pageOnlyVE = pageOnly.getValueExpression(faceletContext, Object.class);
        }
        if (selectionOnly != null) {
            selectionOnlyVE = selectionOnly.getValueExpression(faceletContext, Object.class);
        }
        if (visibleOnly != null) {
            visibleOnlyVE = visibleOnly.getValueExpression(faceletContext, Object.class);
        }
        if (exportHeader != null) {
            exportHeaderVE = exportHeader.getValueExpression(faceletContext, Object.class);
        }
        if (exportFooter != null) {
            exportFooterVE = exportFooter.getValueExpression(faceletContext, Object.class);
        }
        if (preProcessor != null) {
            preProcessorME = preProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
        }
        if (postProcessor != null) {
            postProcessorME = postProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
        }
        if (options != null) {
            optionsVE = options.getValueExpression(faceletContext, Object.class);
        }
        if (onTableRender != null) {
            onTableRenderME = onTableRender.getMethodExpression(faceletContext, null, new Class[]{Object.class, Object.class});
        }
        if (onRowExport != null) {
            onRowExportME = onRowExport.getMethodExpression(faceletContext, null, new Class[]{Object.class});
        }
        if (bufferSize != null) {
            bufferSizeVE = bufferSize.getValueExpression(faceletContext, Integer.class);
        }
        ActionSource actionSource = (ActionSource) parent;
        DataExporter dataExporter = DataExporter.builder()
                    .target(targetVE)
                    .type(typeVE)
                    .fileName(fileNameVE)
                    .encoding(encodingVE)
                    .exportFooter(exportFooterVE)
                    .exportHeader(exportHeaderVE)
                    .onTableRender(onTableRenderME)
                    .options(optionsVE)
                    .pageOnly(pageOnlyVE)
                    .postProcessor(postProcessorME)
                    .preProcessor(preProcessorME)
                    .selectionOnly(selectionOnlyVE)
                    .visibleOnly(visibleOnlyVE)
                    .onRowExport(onRowExportME)
                    .bufferSize(bufferSizeVE)
                    .build();
        actionSource.addActionListener(dataExporter);
    }

}
