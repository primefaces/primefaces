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
package org.primefaces.component.dnd;

import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Draggable.DEFAULT_RENDERER, componentFamily = Draggable.COMPONENT_FAMILY)
public class DraggableRenderer extends CoreRenderer<Draggable> {

    @Override
    public void encodeEnd(FacesContext context, Draggable component) throws IOException {
        String clientId = component.getClientId(context);

        renderDummyMarkup(context, component, clientId);

        UIComponent target = SearchExpressionUtils.contextlessOptionalResolveComponent(context, component, component.getFor());
        if (target == null) {
            target = component.getParent();
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Draggable", component)
                .attr("target", target.getClientId(context))
                .attr("cursor", component.getCursor())
                .attr("disabled", component.isDisabled(), false)
                .attr("axis", component.getAxis(), null)
                .attr("containment", component.getContainment(), null)
                .attr("appendTo", SearchExpressionUtils.resolveOptionalClientIdForClientSide(context, component, component.getAppendTo()))
                .attr("helper", component.getHelper(), null)
                .attr("zIndex", component.getZindex(), -1)
                .attr("handle", component.getHandle(), null)
                .attr("opacity", component.getOpacity(), 1.0)
                .attr("stack", component.getStack(), null)
                .attr("scope", component.getScope(), null)
                .attr("cancel", component.getCancel(), null);

        wb.callback("onStart", "function(event,ui)", component.getOnStart())
                .callback("onStop", "function(event,ui)", component.getOnStop())
                .callback("onDrag", "function(event,ui)", component.getOnDrag());

        if (component.isRevert()) {
            wb.attr("revert", "invalid");
        }

        if (component.getGrid() != null) {
            wb.append(",grid:[").append(component.getGrid()).append("]");
        }

        if (component.isSnap()) {
            wb.attr("snap", true)
                    .attr("snapTolerance", component.getSnapTolerance())
                    .attr("snapMode", component.getSnapMode(), null);
        }

        //Dashboard support
        String dashboard = component.getDashboard();
        if (dashboard != null) {
            Dashboard db = (Dashboard) SearchExpressionUtils.contextlessResolveComponent(context, component, dashboard);
            wb.selectorAttr("connectToSortable", "#" + db.getClientId(context) + " .ui-dashboard-column");
        }

        wb.finish();
    }

}
