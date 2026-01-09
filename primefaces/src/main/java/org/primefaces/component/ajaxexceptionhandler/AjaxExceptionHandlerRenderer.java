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
package org.primefaces.component.ajaxexceptionhandler;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = AjaxExceptionHandler.DEFAULT_RENDERER, componentFamily = AjaxExceptionHandler.COMPONENT_FAMILY)
public class AjaxExceptionHandlerRenderer extends CoreRenderer<AjaxExceptionHandler> {

    @Override
    public void encodeEnd(FacesContext context, AjaxExceptionHandler component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, AjaxExceptionHandler component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AjaxExceptionHandler", component);
        wb.attr("exceptionType", component.getType())
                .attr("update", SearchExpressionUtils.resolveClientIdsForClientSide(context, component, component.getUpdate()))
                .callback("onexception", "function(errorName, errorMessage)", component.getOnexception());

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, AjaxExceptionHandler component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);

        writer.writeAttribute("style", "display:none", null);

        writer.endElement("div");
    }

}
