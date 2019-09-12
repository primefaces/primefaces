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
package org.primefaces.component.fileupload;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import org.primefaces.expression.SearchExpressionHint;

public class FileUploadRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (!context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            return;
        }

        FileUpload fileUpload = (FileUpload) component;

        if (!fileUpload.isDisabled()) {
            PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);
            String uploader = applicationContext.getConfig().getUploader();
            boolean isAtLeastJSF22 = applicationContext.getEnvironment().isAtLeastJsf22();
            String inputToDecodeId = getSimpleInputDecodeId(fileUpload, context);

            if (uploader.equals("auto")) {
                if (isAtLeastJSF22) {
                    NativeFileUploadDecoder.decode(context, fileUpload, inputToDecodeId);
                }
                else {
                    CommonsFileUploadDecoder.decode(context, fileUpload, inputToDecodeId);
                }
            }
            else if (uploader.equals("native")) {
                if (!isAtLeastJSF22) {
                    throw new FacesException("native uploader requires at least a JSF 2.2 runtime");
                }

                NativeFileUploadDecoder.decode(context, fileUpload, inputToDecodeId);
            }
            else if (uploader.equals("commons")) {
                CommonsFileUploadDecoder.decode(context, fileUpload, inputToDecodeId);
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        FileUpload fileUpload = (FileUpload) component;

        encodeMarkup(context, fileUpload);
        encodeScript(context, fileUpload);
    }

    protected void encodeScript(FacesContext context, FileUpload fileUpload) throws IOException {
        String clientId = fileUpload.getClientId(context);
        String update = fileUpload.getUpdate();
        String process = fileUpload.getProcess();
        WidgetBuilder wb = getWidgetBuilder(context);

        if (fileUpload.getMode().equals("advanced")) {
            wb.init("FileUpload", fileUpload.resolveWidgetVar(context), clientId);

            wb.attr("auto", fileUpload.isAuto(), false)
                    .attr("dnd", fileUpload.isDragDropSupport(), true)
                    .attr("update", SearchExpressionFacade.resolveClientIds(context, fileUpload, update, SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                    .attr("process", SearchExpressionFacade.resolveClientIds(context, fileUpload, process, SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                    .attr("maxFileSize", fileUpload.getSizeLimit(), Long.MAX_VALUE)
                    .attr("fileLimit", fileUpload.getFileLimit(), Integer.MAX_VALUE)
                    .attr("invalidFileMessage", fileUpload.getInvalidFileMessage(), null)
                    .attr("invalidSizeMessage", fileUpload.getInvalidSizeMessage(), null)
                    .attr("fileLimitMessage", fileUpload.getFileLimitMessage(), null)
                    .attr("messageTemplate", fileUpload.getMessageTemplate(), null)
                    .attr("previewWidth", fileUpload.getPreviewWidth(), 80)
                    .attr("disabled", fileUpload.isDisabled(), false)
                    .attr("sequentialUploads", fileUpload.isSequential(), false)
                    .callback("onAdd", "function(file, callback)", fileUpload.getOnAdd())
                    .callback("onstart", "function()", fileUpload.getOnstart())
                    .callback("onerror", "function()", fileUpload.getOnerror())
                    .callback("oncancel", "function()", fileUpload.getOncancel())
                    .callback("oncomplete", "function(args)", fileUpload.getOncomplete());

            String allowTypes = fileUpload.getAllowTypes();

            if (allowTypes != null) {
                wb.append(",allowTypes:").append(allowTypes);
            }
        }
        else {
            wb.init("SimpleFileUpload", fileUpload.resolveWidgetVar(context), clientId)
                    .attr("skinSimple", fileUpload.isSkinSimple(), false)
                    .attr("maxFileSize", fileUpload.getSizeLimit(), Long.MAX_VALUE)
                    .attr("invalidSizeMessage", EscapeUtils.forJavaScript(fileUpload.getInvalidSizeMessage()), null);
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
        String styleClass = fileUpload.getStyleClass();
        styleClass = styleClass == null ? FileUpload.CONTAINER_CLASS : FileUpload.CONTAINER_CLASS + " " + styleClass;
        boolean disabled = fileUpload.isDisabled();

        writer.startElement("div", fileUpload);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, styleClass);
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        //buttonbar
        writer.startElement("div", fileUpload);
        writer.writeAttribute("class", FileUpload.BUTTON_BAR_CLASS, null);

        //choose button
        encodeChooseButton(context, fileUpload, disabled);

        if (!fileUpload.isAuto()) {
            encodeButton(context, fileUpload.getUploadLabel(), FileUpload.UPLOAD_BUTTON_CLASS, " " + fileUpload.getUploadIcon());
            encodeButton(context, fileUpload.getCancelLabel(), FileUpload.CANCEL_BUTTON_CLASS, " " + fileUpload.getCancelIcon());
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

            encodeInputField(context, fileUpload, clientId);

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
        String cssClass = HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " " + FileUpload.CHOOSE_BUTTON_CLASS;
        cssClass = isValueBlank(label) ? FileUpload.BUTTON_ICON_ONLY + " " + cssClass : cssClass;
        String tabindex = (disabled) ? "-1" : "0";
        if (disabled) {
            cssClass += " ui-state-disabled";
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", cssClass, null);
        writer.writeAttribute("tabindex", tabindex, null);
        writer.writeAttribute("role", "button", null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, clientId + "_label", null);

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
            encodeInputField(context, fileUpload, clientId);
        }

        writer.endElement("span");
    }

    protected void encodeInputField(FacesContext context, FileUpload fileUpload, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String inputId = clientId + "_input";

        writer.startElement("input", null);
        writer.writeAttribute("type", "file", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("tabindex", "-1", null);
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
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        renderDynamicPassThruAttributes(context, fileUpload);

        writer.endElement("input");
    }

    protected void encodeButton(FacesContext context, String label, String styleClass, String icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String cssClass = HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " ui-state-disabled " + styleClass;
        cssClass = isValueBlank(label) ? FileUpload.BUTTON_ICON_ONLY + " " + cssClass : cssClass;

        writer.startElement("button", null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", cssClass, null);
        writer.writeAttribute("disabled", "disabled", null);

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

    public String getSimpleInputDecodeId(FileUpload fileUpload, FacesContext context) {
        String clientId = fileUpload.getClientId(context);

        if (fileUpload.getMode().equals("simple") && !fileUpload.isSkinSimple()) {
            return clientId;
        }
        else {
            return clientId + "_input";
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
