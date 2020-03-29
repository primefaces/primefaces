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
package org.primefaces.component.progressbar;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.PrimeFaces;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ProgressBarRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ProgressBar progressBar = (ProgressBar) component;
        String clientId = progressBar.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (params.containsKey(clientId)) {
            PrimeFaces.current().ajax().addCallbackParam(progressBar.getClientId(context) + "_value", progressBar.getValue());
        }

        decodeBehaviors(context, progressBar);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ProgressBar progressBar = (ProgressBar) component;

        encodeMarkup(context, progressBar);

        if (!progressBar.isDisplayOnly()) {
            encodeScript(context, progressBar);
        }
    }

    protected void encodeMarkup(FacesContext context, ProgressBar progressBar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String mode = progressBar.getMode();
        int value = progressBar.getValue();
        String labelTemplate = progressBar.getLabelTemplate();
        String style = progressBar.getStyle();
        String styleClass = progressBar.getStyleClass();
        styleClass = styleClass == null ? ProgressBar.CONTAINER_CLASS : ProgressBar.CONTAINER_CLASS + " " + styleClass;
        styleClass = styleClass + " " + (mode.equals("determinate") ? ProgressBar.DETERMINATE_CLASS : ProgressBar.INDETERMINATE_CLASS);

        if (progressBar.isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }

        writer.startElement("div", progressBar);
        writer.writeAttribute("id", progressBar.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        //value
        writer.startElement("div", progressBar);
        writer.writeAttribute("class", ProgressBar.VALUE_CLASS, null);
        if (value != 0) {
            writer.writeAttribute("style", "display:block;width:" + value + "%", style);
        }
        writer.endElement("div");

        //label
        writer.startElement("div", progressBar);
        writer.writeAttribute("class", ProgressBar.LABEL_CLASS, null);
        if (labelTemplate != null) {
            writer.writeAttribute("style", "display:block", style);
            writer.writeText(labelTemplate.replaceAll("\\{value\\}", String.valueOf(value)), null);
        }
        writer.endElement("div");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ProgressBar progressBar) throws IOException {
        String clientId = progressBar.getClientId(context);
        boolean isAjax = progressBar.isAjax();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ProgressBar", progressBar.resolveWidgetVar(context), clientId)
                .attr("initialValue", progressBar.getValue())
                .attr("ajax", isAjax)
                .attr("labelTemplate", progressBar.getLabelTemplate(), null)
                .attr("animationDuration", progressBar.getAnimationDuration())
                .attr("global", progressBar.isGlobal(), true);

        if (isAjax) {
            wb.attr("interval", progressBar.getInterval());

            encodeClientBehaviors(context, progressBar);
        }

        wb.finish();
    }
}
