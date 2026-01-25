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

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Droppable.DEFAULT_RENDERER, componentFamily = Droppable.COMPONENT_FAMILY)
public class DroppableRenderer extends CoreRenderer<Droppable> {

    @Override
    public void decode(FacesContext context, Droppable component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Droppable component) throws IOException {
        String clientId = component.getClientId(context);

        renderDummyMarkup(context, component, clientId);

        UIComponent target = SearchExpressionUtils.contextlessOptionalResolveComponent(context, component, component.getFor());
        if (target == null) {
            target = component.getParent();
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Droppable", component)
                .attr("target", target.getClientId(context))
                .attr("disabled", component.isDisabled(), false)
                .attr("hoverClass", component.getHoverStyleClass(), null)
                .attr("activeClass", component.getActiveStyleClass(), null)
                .attr("accept", component.getAccept(), null)
                .attr("scope", component.getScope(), null)
                .attr("tolerance", component.getTolerance(), null)
                .attr("greedy", component.isGreedy(), false);

        if (component.getOnDrop() != null) {
            wb.append(",onDrop:").append(component.getOnDrop());
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

}
