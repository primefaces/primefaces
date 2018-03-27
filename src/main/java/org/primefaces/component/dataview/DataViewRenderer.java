/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.dataview;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.DataRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class DataViewRenderer extends DataRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DataView view = (DataView) component;    
        
        encodeScript(context, view);
    }

    protected void encodeScript(FacesContext context, DataView view) throws IOException {
        String clientId = view.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DataView", view.resolveWidgetVar(), clientId);

        encodeClientBehaviors(context, view);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, DataView view) throws IOException {
        if (view.isLazy()) {
            view.loadLazyData();
        }

        ResponseWriter writer = context.getResponseWriter();
        String clientId = view.getClientId(context);
        boolean empty = view.getRowCount() == 0;
        String layout = view.getLayout();
        String style = view.getLayout();

        String contentClass = empty
                ? DataView.EMPTY_CONTENT_CLASS
                : (layout.equals("list") ? DataView.TABLE_CONTENT_CLASS : DataView.GRID_CONTENT_CLASS);

        writer.startElement("div", view);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", DataView.HEADER_CLASS, "class");

        writer.startElement("button", view);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("type", "button", null);
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.endElement("span");
        writer.endElement("button");

        writer.endElement("div");
           

        writer.startElement("div", view);
        writer.writeAttribute("id", clientId + "_content", null);
        writer.writeAttribute("class", contentClass, null);

        writer.endElement("div");

        encodeFacet(context, view, "footer", DataView.FOOTER_CLASS);

        writer.endElement("div");
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
