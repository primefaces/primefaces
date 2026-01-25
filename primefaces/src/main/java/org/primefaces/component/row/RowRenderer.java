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
package org.primefaces.component.row;

import org.primefaces.component.row.renderer.ColumnGroupHelperRenderer;
import org.primefaces.component.row.renderer.HelperRowRenderer;
import org.primefaces.component.row.renderer.PanelGridBodyRowRenderer;
import org.primefaces.component.row.renderer.PanelGridFacetRowRenderer;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Row.DEFAULT_RENDERER, componentFamily = Row.COMPONENT_FAMILY)
public class RowRenderer extends CoreRenderer<Row> {

    static final Map<String, HelperRowRenderer> RENDERERS;

    static {
        RENDERERS = new HashMap<>();
        RENDERERS.put("columnGroup", new ColumnGroupHelperRenderer());
        RENDERERS.put("panelGridBody", new PanelGridBodyRowRenderer());
        RENDERERS.put("panelGridFacet", new PanelGridFacetRowRenderer());
    }

    @Override
    public void encodeEnd(FacesContext context, Row component) throws IOException {
        String helperKey = (String) context.getAttributes().get(Constants.HELPER_RENDERER);

        if (helperKey != null) {
            HelperRowRenderer renderer = RENDERERS.get(helperKey);

            if (renderer != null) {
                renderer.encode(context, component);
            }
        }
        else {
            renderChildren(context, component);
        }
    }

    @Override
    public void encodeChildren(FacesContext context, Row component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
