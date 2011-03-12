/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.mobile.component.panelgrid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class PanelGridRenderer extends CoreRenderer {

    private Map<Integer, String> columnKeys;

    private Map<Integer, String> blockKeys;

    public PanelGridRenderer() {
        columnKeys = new HashMap<Integer, String>();
        columnKeys.put(2, "a");
        columnKeys.put(3, "b");
        columnKeys.put(4, "c");
        columnKeys.put(5, "d");

        blockKeys = new HashMap<Integer, String>();
        blockKeys.put(0, "a");
        blockKeys.put(1, "b");
        blockKeys.put(2, "c");
        blockKeys.put(3, "d");
        blockKeys.put(4, "e");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        PanelGrid grid = (PanelGrid) component;
        int columns = grid.getColumns();
        String gridClass = "ui-grid-" + columnKeys.get(columns);
        String styleClass = grid.getStyleClass();
        styleClass = styleClass == null ? gridClass : gridClass + " " + styleClass;

        writer.startElement("div", grid);
        writer.writeAttribute("id", grid.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if(grid.getStyle() != null) {
            writer.writeAttribute("style", grid.getStyle(), null);
        }

        for(int i=0; i < grid.getChildCount(); i++) {
            String blockClass = "ui-block-" + blockKeys.get(i%columns);

            writer.startElement("div", grid);
            writer.writeAttribute("class", blockClass, null);
            grid.getChildren().get(i).encodeAll(context);
            writer.endElement("div");
        }

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