/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.column;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.renderer.ColumnGroupHelperRenderer;
import org.primefaces.component.column.renderer.HelperColumnRenderer;
import org.primefaces.component.column.renderer.PanelGridBodyColumnRenderer;
import org.primefaces.component.column.renderer.PanelGridFacetColumnRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

public class ColumnRenderer extends CoreRenderer {

    static final Map<String,HelperColumnRenderer> RENDERERS;
        
    static {
        RENDERERS = new HashMap<String,HelperColumnRenderer>();
        RENDERERS.put("columnGroup", new ColumnGroupHelperRenderer());
        RENDERERS.put("panelGridBody", new PanelGridBodyColumnRenderer());
        RENDERERS.put("panelGridFacet", new PanelGridFacetColumnRenderer());
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Column column = (Column) component;
        String helperKey = (String) context.getAttributes().get(Constants.HELPER_RENDERER);
        
        if(helperKey != null) {
            HelperColumnRenderer renderer = RENDERERS.get(helperKey);
            
            if(renderer != null) {
                renderer.encode(context, column);
            }
        }
        else {
            renderChildren(context, column);
        }
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
