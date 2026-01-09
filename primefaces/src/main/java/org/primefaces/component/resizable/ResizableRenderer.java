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
package org.primefaces.component.resizable;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIGraphic;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Resizable.DEFAULT_RENDERER, componentFamily = Resizable.COMPONENT_FAMILY)
public class ResizableRenderer extends CoreRenderer<Resizable> {

    @Override
    public void decode(FacesContext context, Resizable component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Resizable component) throws IOException {
        String clientId = component.getClientId(context);

        UIComponent target = SearchExpressionUtils.contextlessOptionalResolveComponent(context, component, component.getFor());
        if (target == null) {
            target = component.getParent();
        }

        String targetId = target.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);

        if (target instanceof UIGraphic) {
            wb.initWithComponentLoad("Resizable", component.resolveWidgetVar(context), clientId, targetId);
        }
        else {
            wb.init("Resizable", component);
        }

        wb.attr("target", targetId)
                .attr("minWidth", component.getMinWidth(), Integer.MIN_VALUE)
                .attr("maxWidth", component.getMaxWidth(), Integer.MAX_VALUE)
                .attr("minHeight", component.getMinHeight(), Integer.MIN_VALUE)
                .attr("maxHeight", component.getMaxHeight(), Integer.MAX_VALUE);

        if (component.isAnimate()) {
            wb.attr("animate", true)
                    .attr("animateEasing", component.getEffect())
                    .attr("animateDuration", component.getEffectDuration());
        }

        if (component.isProxy()) {
            wb.attr("helper", "ui-resizable-proxy");
        }

        wb.attr("handles", component.getHandles(), null)
                .attr("grid", component.getGrid(), 1)
                .attr("aspectRatio", component.isAspectRatio(), false)
                .attr("ghost", component.isGhost(), false);

        if (component.isContainment()) {
            wb.attr("isContainment", true);
            wb.attr("parentComponentId", component.getParent().getClientId(context));
        }

        wb.callback("onStart", "function(event,ui)", component.getOnStart())
                .callback("onResize", "function(event,ui)", component.getOnResize())
                .callback("onStop", "function(event,ui)", component.getOnStop());

        encodeClientBehaviors(context, component);

        wb.finish();
    }
}
