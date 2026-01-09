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
package org.primefaces.component.hotkey;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.AgentUtils;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Hotkey.DEFAULT_RENDERER, componentFamily = Hotkey.COMPONENT_FAMILY)
public class HotkeyRenderer extends CoreRenderer<Hotkey> {

    @Override
    public void decode(FacesContext facesContext, Hotkey component) {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();

        if (params.containsKey(component.getClientId(facesContext))) {
            component.queueEvent(new ActionEvent(component));
        }
    }

    @Override
    public void encodeEnd(FacesContext context, Hotkey component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("script", null);
        RendererUtils.encodeScriptTypeIfNecessary(context);

        String event = "keydown." + clientId;
        writer.write("$(function(){");

        if (!component.isDisabled()) {
            String bind = component.getBindMac() != null && AgentUtils.isMac(context)
                    ? component.getBindMac()
                    : component.getBind();

            writer.write("$(document).off('" + event + "').on('" + event + "',null,'" + bind + "',function(){");

            if (component.isAjaxified()) {
                String request = preConfiguredAjaxRequestBuilder(context, component)
                        .params(component)
                        .build();

                writer.write(request);
            }
            else {
                writer.write(component.getHandler());
            }
            writer.write(";return false;});});");
        }
        else {
            writer.write("$(document).off('" + event + "')});");
        }

        writer.endElement("script");
    }
}
