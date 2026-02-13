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

import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.Property;

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

@FacesTagHandler("DataExporter is handy for exporting data listed using a PrimeFaces Datatable to various formats such as excel, pdf, csv and xml.")
public class DataExporterTagHandler extends TagHandler {

    @Property(description = "Search expression to resolve one or multiple target components.", required = true, type = String.class)
    private final TagAttribute target;

    @Property(description = "Export type: \"xls\", \"xlsx\", \"xlsxstream\", \"pdf\", \"csv\", \"xml\".", required = true, type = String.class)
    private final TagAttribute type;

    @Property(description = "Filename of the generated export file", type = String.class, implicitDefaultValue = "The target component id.", required = true)
    private final TagAttribute fileName;

    @Property(description = "Exports only current page instead of whole dataset.", type = Boolean.class)
    private final TagAttribute pageOnly;

    @Property(description = "When enabled, only selection would be exported.", type = Boolean.class)
    private final TagAttribute selectionOnly;

    @Property(description = "When enabled, only visible data would be exported.", defaultValue = "false", type = Boolean.class)
    private final TagAttribute visibleOnly;

    @Property(description = "When enabled, the header will be exported.", defaultValue = "true", type = Boolean.class)
    private final TagAttribute exportHeader;

    @Property(description = "When enabled, the footer will be exported..", defaultValue = "true", type = Boolean.class)
    private final TagAttribute exportFooter;

    @Property(description = "PreProcessor for the exported document.", type = jakarta.el.MethodExpression.class)
    private final TagAttribute preProcessor;

    @Property(description = "PostProcessor for the exported document.", type = jakarta.el.MethodExpression.class)
    private final TagAttribute postProcessor;

    @Property(description = "Character encoding to use.", type = String.class)
    private final TagAttribute encoding;

    @Property(description = "Options object to customize document.", type = org.primefaces.component.export.ExporterOptions.class)
    private final TagAttribute options;

    @Property(description = "OnTableRender to be used to set the options of exported table.", type = jakarta.el.MethodExpression.class)
    private final TagAttribute onTableRender;

    @Property(description = "Row processor for the exported document.", type = jakarta.el.MethodExpression.class)
    private final TagAttribute onRowExport;

    @Property(description = "Control how many items are fetched at a time when DataTable#lazy is enabled."
            + " Retrieve the entire underlying dataset in smaller, manageable chunks rather than all at once.",
            type = Integer.class)
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
