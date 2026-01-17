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
package org.primefaces.component.fileupload;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.StyleClassBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = FileUpload.DEFAULT_RENDERER, componentFamily = FileUpload.COMPONENT_FAMILY)
public class FileUploadRenderer extends CoreRenderer<FileUpload> {

    @Override
    public void decode(FacesContext context, FileUpload component) {
        if (component.isDisabled()) {
            return;
        }

        if (!context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            logDevelopmentWarning(context, this,
                    "Decoding FileUpload requires enctype \"multipart/form-data\" for clientId: " + component.getClientId(context));
            // skip further processing as servlet fileupload decoding would throw a exception because of wrong contentType
            return;
        }

        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);

        FileUploadDecoder decoder = applicationContext.getFileUploadDecoder();
        decoder.decode(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, FileUpload component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, FileUpload component) throws IOException {
        String update = component.getUpdate();
        String process = component.getProcess();
        WidgetBuilder wb = getWidgetBuilder(context);

        if (component.getMode().equals("advanced")) {
            PrimeApplicationContext pfContext = PrimeApplicationContext.getCurrentInstance(context);

            wb.init("FileUpload", component)
                    .attr("dnd", component.isDragDrop(), true)
                    .attr("previewWidth", component.getPreviewWidth(), 80)
                    .attr("sequentialUploads", component.isSequential(), false)
                    .attr("maxChunkSize", component.getMaxChunkSize(), 0)
                    .attr("maxRetries", component.getMaxRetries(), 30)
                    .attr("retryTimeout", component.getRetryTimeout(), 1000)
                    .attr("resumeContextPath", pfContext.getFileUploadResumeUrl(), null)
                    .callback("onAdd", "function(file, callback)", component.getOnAdd())
                    .callback("oncancel", "function()", component.getOncancel())
                    .callback("onupload", "function()", component.getOnupload());

        }
        else {
            wb.init("SimpleFileUpload", component)
                    .attr("skinSimple", component.isSkinSimple(), false)
                    .attr("displayFilename", component.isDisplayFilename())
                    .attr("messageTemplate", component.getMessageTemplate(), null);
        }

        wb.attr("mode", component.getMode())
                .attr("auto", component.isAuto(), false)
                .attr("update", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, update))
                .attr("process", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, process))
                .attr("global", component.isGlobal(), true)
                .attr("disabled", component.isDisabled(), false)
                .attr("dropZone", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getDropZone()))
                .attr("ignoreAutoUpdate", component.isIgnoreAutoUpdate(), false)
                .callback("onstart", "function()", component.getOnstart())
                .callback("onerror", "function()", component.getOnerror())
                .callback("oncomplete", "function(args)", component.getOncomplete())
                .callback("onvalidationfailure", "function(msg)", component.getOnvalidationfailure());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, FileUpload component) throws IOException {
        if (component.getMode().equals("simple")) {
            encodeSimpleMarkup(context, component);
        }
        else {
            encodeAdvancedMarkup(context, component);
        }
    }

    protected void encodeAdvancedMarkup(FacesContext context, FileUpload component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(FileUpload.CONTAINER_CLASS)
                .add(component.getStyleClass())
                .add(component.getDropZone() != null, FileUpload.WITHDROPZONE_CLASS)
                .build();
        boolean disabled = component.isDisabled();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }


        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.DRAG_OVERLAY_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.DRAG_OVERLAY_CONTENT_CLASS, null);
        writer.startElement("i", null);
        writer.writeAttribute("class", "pi pi-upload", null);
        writer.endElement("i");
        writer.endElement("div");

        writer.endElement("div");

        //buttonbar
        writer.startElement("div", component);
        writer.writeAttribute("class", FileUpload.BUTTON_BAR_CLASS, null);

        //choose button
        encodeChooseButton(context, component, disabled);

        if (!component.isAuto()) {
            StyleClassBuilder uploadCssClassBuilder = getStyleClassBuilder(context)
                    .add(FileUpload.UPLOAD_BUTTON_CLASS)
                    .add(component.getUploadButtonStyleClass());
            encodeButton(context, component.getUploadLabel(), uploadCssClassBuilder, component.getUploadIcon(), component.getUploadButtonTitle());

            StyleClassBuilder cancelCssClassBuilder = getStyleClassBuilder(context)
                    .add(FileUpload.CANCEL_BUTTON_CLASS)
                    .add(component.getCancelButtonStyleClass());
            encodeButton(context, component.getCancelLabel(), cancelCssClassBuilder, component.getCancelIcon(), component.getCancelButtonTitle());
        }

        writer.endElement("div");

        renderChildren(context, component);

        //content
        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.CONTENT_CLASS, null);

        UIComponent emptyFacet = component.getEmptyFacet();
        if (FacetUtils.shouldRenderFacet(emptyFacet)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", FileUpload.EMPTY_CLASS, null);

            emptyFacet.encodeAll(context);

            writer.endElement("div");
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.FILES_CLASS, null);
        if (FacetUtils.shouldRenderFacet(emptyFacet)) {
            writer.writeAttribute("style", "display: none", null);
        }
        writer.startElement("div", null);
        writer.endElement("div");
        writer.endElement("div");

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeSimpleMarkup(FacesContext context, FileUpload component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        String label = component.getLabel();

        if (component.isSkinSimple()) {
            styleClass = (styleClass == null) ? FileUpload.CONTAINER_CLASS_SIMPLE : FileUpload.CONTAINER_CLASS_SIMPLE + " " + styleClass;
            styleClass = isValueBlank(label) ? FileUpload.BUTTON_ICON_ONLY + " " + styleClass : styleClass;
            String buttonClass = HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS;
            if (component.isDisabled()) {
                buttonClass += " ui-state-disabled";
            }

            writer.startElement("span", component);
            writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("class", styleClass, "styleClass");
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }

            writer.startElement("span", null);
            writer.writeAttribute("class", buttonClass, null);

            //button icon
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + component.getChooseIcon(), null);
            writer.endElement("span");

            //text
            writer.startElement("span", null);
            writer.writeAttribute("id", clientId + "_label", null);
            writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
            if (isValueBlank(label)) {
                writer.write("&nbsp;");
            }
            else {
                writer.writeText(label, "value");
            }

            writer.endElement("span");

            encodeInputField(context, component, clientId, null);

            writer.endElement("span");

            writer.startElement("span", component);
            writer.writeAttribute("class", FileUpload.FILENAME_CLASS, null);
            writer.endElement("span");

            writer.endElement("span");
        }
        else {
            encodeSimpleInputField(context, component, clientId, style, styleClass);
        }
    }

    protected void encodeChooseButton(FacesContext context, FileUpload component, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String label = component.getLabel();
        String tabindex = (disabled) ? "-1" : "0";
        String cssClass = getStyleClassBuilder(context)
                .add(HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS)
                .add(FileUpload.CHOOSE_BUTTON_CLASS)
                .add(isValueBlank(label), FileUpload.BUTTON_ICON_ONLY)
                .add(component.getChooseButtonStyleClass())
                .add(disabled, "ui-state-disabled")
                .build();

        writer.startElement("span", null);
        writer.writeAttribute("class", cssClass, null);
        writer.writeAttribute("tabindex", tabindex, null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, clientId + "_label", null);

        String chooseButtonTitle = component.getChooseButtonTitle();
        if (chooseButtonTitle != null) {
            writer.writeAttribute("title", chooseButtonTitle, null);
        }

        //button icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + component.getChooseIcon(), null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_label", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        if (isValueBlank(label)) {
            writer.write("&nbsp;");
        }
        else {
            writer.writeText(label, "value");
        }

        writer.endElement("span");

        if (!disabled) {
            encodeInputField(context, component, clientId, "-1");
        }

        writer.endElement("span");
    }

    protected void encodeInputField(FacesContext context, FileUpload component, String clientId, String tabIndex) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("input", null);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);
        if (LangUtils.isNotBlank(tabIndex)) {
            writer.writeAttribute("tabindex", tabIndex, null);
        }

        if (component.isMultiple()) {
            writer.writeAttribute("multiple", "multiple", null);
        }
        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        if (component.getAccept() != null) {
            writer.writeAttribute("accept", component.getAccept(), null);
        }
        if (component.getTitle() != null) {
            writer.writeAttribute("title", component.getTitle(), null);
        }

        renderValidationMetadata(context, component);

        renderDynamicPassThruAttributes(context, component);

        writer.endElement("input");
    }

    protected void encodeSimpleInputField(FacesContext context, FileUpload component, String clientId, String style, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);

        if (component.isMultiple()) {
            writer.writeAttribute("multiple", "multiple", null);
        }
        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        if (component.getAccept() != null) {
            writer.writeAttribute("accept", component.getAccept(), null);
        }
        if (component.getTitle() != null) {
            writer.writeAttribute("title", component.getTitle(), null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        renderValidationMetadata(context, component);

        renderDynamicPassThruAttributes(context, component);

        writer.endElement("input");
    }

    protected void encodeButton(FacesContext context, String label, StyleClassBuilder styleClassBuilder, String icon, String title) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String cssClass = styleClassBuilder.add(HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS)
                .add("ui-state-disabled")
                .add(isValueBlank(label), FileUpload.BUTTON_ICON_ONLY)
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", cssClass, null);
        writer.writeAttribute("disabled", "disabled", null);

        if (title != null) {
            writer.writeAttribute("title", title, null);
        }

        //button icon
        String iconClass = HTML.BUTTON_LEFT_ICON_CLASS;
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass + " " + icon, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        if (isValueBlank(label)) {
            writer.write("&nbsp;");
        }
        else {
            writer.writeText(label, "value");
        }

        writer.endElement("span");

        writer.endElement("button");
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        FileUpload fileUpload = (FileUpload) component;

        if (fileUpload.getMode().equals("simple") && submittedValue != null && submittedValue.equals("")) {
            return null;
        }
        else {
            return submittedValue;
        }
    }

    @Override
    public void encodeChildren(FacesContext context, FileUpload component) throws IOException {
        // Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
