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
package org.primefaces.component.row;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.row.renderer.ColumnGroupHelperRenderer;
import org.primefaces.component.row.renderer.HelperRowRenderer;
import org.primefaces.component.row.renderer.PanelGridBodyRowRenderer;
import org.primefaces.component.row.renderer.PanelGridFacetRowRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

public class RowRenderer extends CoreRenderer {

    static final Map<String, HelperRowRenderer> RENDERERS;

    static {
        RENDERERS = new HashMap<>();
        RENDERERS.put("columnGroup", new ColumnGroupHelperRenderer());
        RENDERERS.put("panelGridBody", new PanelGridBodyRowRenderer());
        RENDERERS.put("panelGridFacet", new PanelGridFacetRowRenderer());
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Row row = (Row) component;
        String helperKey = (String) context.getAttributes().get(Constants.HELPER_RENDERER);

        if (helperKey != null) {
            HelperRowRenderer renderer = RENDERERS.get(helperKey);

            if (renderer != null) {
                renderer.encode(context, row);
            }
        }
        else {
            renderChildren(context, row);
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
