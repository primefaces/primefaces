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
package org.primefaces.component.fileupload;

import org.apache.poi.util.StringUtil;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.StyleClassBuilder;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.validate.ClientValidator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class FileUploadRenderer extends CoreRenderer {

    private static final Logger LOGGER = Logger.getLogger(FileUploadRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        FileUpload fileUpload = (FileUpload) component;
        if (!fileUpload.isDisabled()) {
            if (!context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
                LOGGER.severe(() -> "Decoding FileUpload requires contentType \"multipart/form-data\"." +
                        " Skipping clientId: " + component.getClientId(context));
                return;
            }

            PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);

            FileUploadDecoder decoder = applicationContext.getFileUploadDecoder();
            decoder.decode(context, fileUpload);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        FileUpload fileUpload = (FileUpload) component;

        if (fileUpload.getDropZone() != null) {
            fileUpload.setMode("advanced");
            fileUpload.setAuto(true);
            fileUpload.setDragDropSupport(true);
        }

        encodeMarkup(context, fileUpload);
        encodeScript(context, fileUpload);
    }

    protected void encodeScript(FacesContext context, FileUpload fileUpload) throws IOException {
        String update = fileUpload.getUpdate();
        String process = fileUpload.getProcess();
        WidgetBuilder wb = getWidgetBuilder(context);

        if (fileUpload.getMode().equals("advanced")) {
            PrimeApplicationContext pfContext = PrimeApplicationContext.getCurrentInstance(context);

            wb.init("FileUpload", fileUpload)
                    .attr("dnd", fileUpload.isDragDropSupport(), true)
                    .attr("previewWidth", fileUpload.getPreviewWidth(), 80)
                    .attr("sequentialUploads", fileUpload.isSequential(), false)
                    .attr("maxChunkSize", fileUpload.getMaxChunkSize(), 0)
                    .attr("maxRetries", fileUpload.getMaxRetries(), 30)
                    .attr("retryTimeout", fileUpload.getRetryTimeout(), 1000)
                    .attr("resumeContextPath", pfContext.getFileUploadResumeUrl(), null)
                    .attr("dropZone", SearchExpressionUtils.resolveClientIdsForClientSide(context, fileUpload, fileUpload.getDropZone()))
                    .callback("onAdd", "function(file, callback)", fileUpload.getOnAdd())
                    .callback("oncancel", "function()", fileUpload.getOncancel())
                    .callback("onupload", "function()", fileUpload.getOnupload());

        }
        else {
            wb.init("SimpleFileUpload", fileUpload)
                    .attr("skinSimple", fileUpload.isSkinSimple(), false)
                    .attr("displayFilename", fileUpload.isDisplayFilename());
        }

        wb.attr("mode", fileUpload.getMode())
                .attr("auto", fileUpload.isAuto(), false)
                .attr("update", SearchExpressionUtils.resolveClientIdsForClientSide(context, fileUpload, update))
                .attr("process", SearchExpressionUtils.resolveClientIdsForClientSide(context, fileUpload, process))
                .attr("global", fileUpload.isGlobal(), true)
                .attr("disabled", fileUpload.isDisabled(), false)
                .attr("invalidSizeMessage", fileUpload.getInvalidSizeMessage(), null)
                .attr("invalidFileMessage", fileUpload.getInvalidFileMessage(), null)
                .attr("fileLimitMessage", fileUpload.getFileLimitMessage(), null)
                .attr("messageTemplate", fileUpload.getMessageTemplate(), null)
                .attr("maxFileSize", fileUpload.getSizeLimit(), Long.MAX_VALUE)
                .attr("fileLimit", fileUpload.getFileLimit(), Integer.MAX_VALUE)
                .callback("onstart", "function()", fileUpload.getOnstart())
                .callback("onerror", "function()", fileUpload.getOnerror())
                .callback("oncomplete", "function(args)", fileUpload.getOncomplete())
                .callback("onvalidationfailure", "function(msg)", fileUpload.getOnvalidationfailure());

        String allowTypes = fileUpload.getAllowTypes();
        if (allowTypes != null) {
            wb.append(",allowTypes:").append(allowTypes);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        if (fileUpload.getMode().equals("simple")) {
            encodeSimpleMarkup(context, fileUpload);
        }
        else {
            encodeAdvancedMarkup(context, fileUpload);
        }
    }

    protected void encodeAdvancedMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = fileUpload.getClientId(context);
        String style = fileUpload.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(FileUpload.CONTAINER_CLASS)
                .add(fileUpload.getStyleClass())
                .add(fileUpload.getDropZone() != null, FileUpload.WITHDROPZONE_CLASS)
                .build();
        boolean disabled = fileUpload.isDisabled();

        writer.startElement("div", fileUpload);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        //buttonbar
        writer.startElement("div", fileUpload);
        writer.writeAttribute("class", FileUpload.BUTTON_BAR_CLASS, null);

        //choose button
        encodeChooseButton(context, fileUpload, disabled);

        if (!fileUpload.isAuto()) {
            StyleClassBuilder uploadCssClassBuilder = getStyleClassBuilder(context)
                    .add(FileUpload.UPLOAD_BUTTON_CLASS)
                    .add(fileUpload.getUploadButtonStyleClass());
            encodeButton(context, fileUpload.getUploadLabel(), uploadCssClassBuilder, fileUpload.getUploadIcon(), fileUpload.getUploadButtonTitle());

            StyleClassBuilder cancelCssClassBuilder = getStyleClassBuilder(context)
                    .add(FileUpload.CANCEL_BUTTON_CLASS)
                    .add(fileUpload.getCancelButtonStyleClass());
            encodeButton(context, fileUpload.getCancelLabel(), cancelCssClassBuilder, fileUpload.getCancelIcon(), fileUpload.getCancelButtonTitle());
        }

        writer.endElement("div");

        renderChildren(context, fileUpload);

        //content
        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.CONTENT_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", FileUpload.FILES_CLASS, null);
        writer.startElement("div", null);
        writer.endElement("div");
        writer.endElement("div");

        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeSimpleMarkup(FacesContext context, FileUpload fileUpload) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = fileUpload.getClientId(context);
        String style = fileUpload.getStyle();
        String styleClass = fileUpload.getStyleClass();
        String label = fileUpload.getLabel();

        if (fileUpload.isSkinSimple()) {
            styleClass = (styleClass == null) ? FileUpload.CONTAINER_CLASS_SIMPLE : FileUpload.CONTAINER_CLASS_SIMPLE + " " + styleClass;
            styleClass = isValueBlank(label) ? FileUpload.BUTTON_ICON_ONLY + " " + styleClass : styleClass;
            String buttonClass = HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS;
            if (fileUpload.isDisabled()) {
                buttonClass += " ui-state-disabled";
            }

            writer.startElement("span", fileUpload);
            writer.writeAttribute("id", clientId, "id");
            writer.writeAttribute("class", styleClass, "styleClass");
            if (style != null) {
                writer.writeAttribute("style", style, "style");
            }

            writer.startElement("span", null);
            writer.writeAttribute("class", buttonClass, null);

            //button icon
            writer.startElement("span", null);
            writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + fileUpload.getChooseIcon(), null);
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

            encodeInputField(context, fileUpload, clientId, null);

            writer.endElement("span");

            writer.startElement("span", fileUpload);
            writer.writeAttribute("class", FileUpload.FILENAME_CLASS, null);
            writer.endElement("span");

            writer.endElement("span");
        }
        else {
            encodeSimpleInputField(context, fileUpload, clientId, style, styleClass);
        }
    }

    protected void encodeChooseButton(FacesContext context, FileUpload fileUpload, boolean disabled) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = fileUpload.getClientId(context);
        String label = fileUpload.getLabel();
        String tabindex = (disabled) ? "-1" : "0";
        String cssClass = getStyleClassBuilder(context)
                .add(HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS)
                .add(FileUpload.CHOOSE_BUTTON_CLASS)
                .add(isValueBlank(label), FileUpload.BUTTON_ICON_ONLY)
                .add(fileUpload.getChooseButtonStyleClass())
                .add(disabled, "ui-state-disabled")
                .build();

        writer.startElement("span", null);
        writer.writeAttribute("class", cssClass, null);
        writer.writeAttribute("tabindex", tabindex, null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, clientId + "_label", null);

        String chooseButtonTitle = fileUpload.getChooseButtonTitle();
        if (chooseButtonTitle != null) {
            writer.writeAttribute("title", chooseButtonTitle, null);
        }

        //button icon
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_LEFT_ICON_CLASS + " " + fileUpload.getChooseIcon(), null);
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
            encodeInputField(context, fileUpload, clientId, "-1");
        }

        writer.endElement("span");
    }

    protected void encodeInputField(FacesContext context, FileUpload fileUpload, String clientId, String tabIndex) throws IOException {
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

        if (fileUpload.isMultiple()) {
            writer.writeAttribute("multiple", "multiple", null);
        }
        if (fileUpload.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        if (fileUpload.getAccept() != null) {
            writer.writeAttribute("accept", fileUpload.getAccept(), null);
        }
        if (fileUpload.getTitle() != null) {
            writer.writeAttribute("title", fileUpload.getTitle(), null);
        }

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, fileUpload, new ClientValidator() {
                @Override
                public Map<String, Object> getMetadata() {
                    HashMap metadata = new HashMap();

                    int fileLimit = fileUpload.getFileLimit();
                    if (fileLimit != Integer.MAX_VALUE) {
                        metadata.put("data-p-filelimit", fileLimit);
                    }

                    long sizeLimit = fileUpload.getSizeLimit();
                    if (sizeLimit != Long.MAX_VALUE) {
                        metadata.put("data-p-sizelimit", sizeLimit);
                    }

                    String allowTypes = fileUpload.getAllowTypes();
                    if (!StringUtil.isBlank(allowTypes)) {
                        metadata.put("data-p-allowtypes", allowTypes);
                    }

                    return metadata;
                }

                @Override
                public String getValidatorId() {
                    return "primefaces.File";
                }
            });
        }

        renderDynamicPassThruAttributes(context, fileUpload);

        writer.endElement("input");
    }

    protected void encodeSimpleInputField(FacesContext context, FileUpload fileUpload, String clientId, String style, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, "true", null);

        if (fileUpload.isMultiple()) {
            writer.writeAttribute("multiple", "multiple", null);
        }
        if (fileUpload.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", "disabled");
        }
        if (fileUpload.getAccept() != null) {
            writer.writeAttribute("accept", fileUpload.getAccept(), null);
        }
        if (fileUpload.getTitle() != null) {
            writer.writeAttribute("title", fileUpload.getTitle(), null);
        }
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        renderDynamicPassThruAttributes(context, fileUpload);

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
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
