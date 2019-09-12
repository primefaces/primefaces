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
package org.primefaces.component.dnd;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DraggableRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Draggable draggable = (Draggable) component;
        String clientId = draggable.getClientId(context);

        renderDummyMarkup(context, component, clientId);

        UIComponent target = SearchExpressionFacade.resolveComponent(
                context, draggable, draggable.getFor(), SearchExpressionHint.PARENT_FALLBACK);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Draggable", draggable.resolveWidgetVar(context), clientId)
                .attr("target", target.getClientId(context))
                .attr("cursor", draggable.getCursor())
                .attr("disabled", draggable.isDisabled(), false)
                .attr("axis", draggable.getAxis(), null)
                .attr("containment", draggable.getContainment(), null)
                .attr("appendTo",
                        SearchExpressionFacade.resolveClientId(context, draggable, draggable.getAppendTo(), SearchExpressionHint.RESOLVE_CLIENT_SIDE), null)
                .attr("helper", draggable.getHelper(), null)
                .attr("zIndex", draggable.getZindex(), -1)
                .attr("handle", draggable.getHandle(), null)
                .attr("opacity", draggable.getOpacity(), 1.0)
                .attr("stack", draggable.getStack(), null)
                .attr("scope", draggable.getScope(), null)
                .attr("cancel", draggable.getCancel(), null);

        wb.callback("onStart", "function(event,ui)", draggable.getOnStart())
                .callback("onStop", "function(event,ui)", draggable.getOnStop())
                .callback("onDrag", "function(event,ui)", draggable.getOnDrag());

        if (draggable.isRevert()) {
            wb.attr("revert", "invalid");
        }

        if (draggable.getGrid() != null) {
            wb.append(",grid:[").append(draggable.getGrid()).append("]");
        }

        if (draggable.isSnap()) {
            wb.attr("snap", true)
                    .attr("snapTolerance", draggable.getSnapTolerance())
                    .attr("snapMode", draggable.getSnapMode(), null);
        }

        //Dashboard support
        String dashboard = draggable.getDashboard();
        if (dashboard != null) {
            Dashboard db = (Dashboard) SearchExpressionFacade.resolveComponent(context, draggable, dashboard);
            wb.selectorAttr("connectToSortable", "#" + db.getClientId(context) + " .ui-dashboard-column");
        }

        wb.finish();
    }

}
