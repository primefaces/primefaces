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
        wb.init("Draggable", draggable.resolveWidgetVar(), clientId)
                .attr("target", target.getClientId(context))
                .attr("cursor", draggable.getCursor())
                .attr("disabled", draggable.isDisabled(), false)
                .attr("axis", draggable.getAxis(), null)
                .attr("containment", draggable.getContainment(), null)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, draggable, draggable.getAppendTo()), null)
                .attr("helper", draggable.getHelper(), null)
                .attr("zIndex", draggable.getZindex(), -1)
                .attr("handle", draggable.getHandle(), null)
                .attr("opacity", draggable.getOpacity(), 1.0)
                .attr("stack", draggable.getStack(), null)
                .attr("scope", draggable.getScope(), null)
                .attr("cancel", draggable.getCancel(), null);

        wb.callback("onStart", "function(event,ui)", draggable.getOnStart())
                .callback("onStop", "function(event,ui)", draggable.getOnStop());

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
