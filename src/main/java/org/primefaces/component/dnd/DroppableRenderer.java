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

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DroppableRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Droppable droppable = (Droppable) component;
        String clientId = droppable.getClientId(context);

        renderDummyMarkup(context, component, clientId);

        UIComponent target = SearchExpressionFacade.resolveComponent(
                context, droppable, droppable.getFor(), SearchExpressionHint.PARENT_FALLBACK);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Droppable", droppable.resolveWidgetVar(context), clientId)
                .attr("target", target.getClientId(context))
                .attr("disabled", droppable.isDisabled(), false)
                .attr("hoverClass", droppable.getHoverStyleClass(), null)
                .attr("activeClass", droppable.getActiveStyleClass(), null)
                .attr("accept", droppable.getAccept(), null)
                .attr("scope", droppable.getScope(), null)
                .attr("tolerance", droppable.getTolerance(), null)
                .attr("greedy", droppable.isGreedy(), false);

        if (droppable.getOnDrop() != null) {
            wb.append(",onDrop:").append(droppable.getOnDrop());
        }

        encodeClientBehaviors(context, droppable);

        wb.finish();
    }

}
