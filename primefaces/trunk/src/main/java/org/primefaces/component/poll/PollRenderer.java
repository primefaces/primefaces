/*
 * Copyright 2010 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.poll;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class PollRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext facesContext, UIComponent component) {
        Poll poll = (Poll) component;

        if (facesContext.getExternalContext().getRequestParameterMap().containsKey(poll.getClientId(facesContext))) {
            poll.queueEvent(new ActionEvent(poll));
        }
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        Poll poll = (Poll) component;
        String clientId = poll.getClientId(facesContext);
        UIComponent form = ComponentUtils.findParentForm(facesContext, poll);
        if (form == null) {
            throw new FacesException("Poll:" + clientId + " needs to be enclosed in a form component");
        }

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function() {");

        writer.write(poll.resolveWidgetVar() + "= new PrimeFaces.widget.Poll('" + clientId + "', {");
        writer.write("frequency:" + poll.getInterval());
        writer.write(",autoStart:" + poll.isAutoStart());
        writer.write(",fn: function() {");
        writer.write(buildAjaxRequest(facesContext, poll));
        writer.write("}");

        writer.write("});});");

        writer.endElement("script");
    }
}