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
package org.primefaces.component.idlemonitor;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = IdleMonitor.DEFAULT_RENDERER, componentFamily = IdleMonitor.COMPONENT_FAMILY)
public class IdleMonitorRenderer extends CoreRenderer<IdleMonitor> {

    @Override
    public void decode(FacesContext context, IdleMonitor component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, IdleMonitor component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeScript(FacesContext context, IdleMonitor component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("IdleMonitor", component)
                .attr("timeout", component.getTimeout())
                .attr("multiWindowSupport", component.isMultiWindowSupport())
                .callback("onidle", "function()", component.getOnidle())
                .callback("onactive", "function()", component.getOnactive());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, IdleMonitor idleMonitor) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = idleMonitor.getClientId(context);

        writer.startElement("span", idleMonitor);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", "ui-idlemonitor", "styleClass");
        writer.endElement("span");
    }
}
