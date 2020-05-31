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
package org.primefaces.component.ai.speechrecognition;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class SpeechRecognitionRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SpeechRecognition cam = (SpeechRecognition) component;
        encodeMarkup(context, cam);
        encodeScript(context, cam);
    }

    protected void encodeMarkup(FacesContext context, SpeechRecognition cam) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = cam.getClientId(context);
        String style = cam.getStyle();
        String styleClass = cam.getStyleClass();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, SpeechRecognition component) throws IOException {
        String clientId = component.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("SpeechRecognition", component.resolveWidgetVar(context), clientId)
                .attr("width", component.getWidth(), 320)
                .attr("height", component.getHeight(), 240)
                .attr("language", component.getLanguage(), "en-US")
                .callback("speechHandler", "function(speech)", component.getSpeechHandler())
                .callback("speechErrorHandler", "function(error)", component.getSpeechErrorHandler());

        if (component.getUpdate() != null) {
            wb.attr("update", SearchExpressionFacade.resolveClientIds(context, component, component.getUpdate(),
                    SearchExpressionUtils.SET_RESOLVE_CLIENT_SIDE));
        }
        if (component.getProcess() != null) {
            wb.attr("process", SearchExpressionFacade.resolveClientIds(context, component, component.getProcess(),
                    SearchExpressionUtils.SET_RESOLVE_CLIENT_SIDE));
        }

        wb.finish();
    }

}
