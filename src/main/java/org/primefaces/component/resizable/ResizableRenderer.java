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
package org.primefaces.component.resizable;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIGraphic;
import javax.faces.context.FacesContext;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ResizableRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Resizable resizable = (Resizable) component;
        String clientId = resizable.getClientId(context);

        UIComponent target = SearchExpressionFacade.resolveComponent(
                context, resizable, resizable.getFor(), SearchExpressionHint.PARENT_FALLBACK);
        String targetId = target.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);

        if (target instanceof UIGraphic) {
            wb.initWithComponentLoad("Resizable", resizable.resolveWidgetVar(context), clientId, targetId);
        }
        else {
            wb.init("Resizable", resizable.resolveWidgetVar(context), clientId);
        }

        wb.attr("target", targetId)
                .attr("minWidth", resizable.getMinWidth(), Integer.MIN_VALUE)
                .attr("maxWidth", resizable.getMaxWidth(), Integer.MAX_VALUE)
                .attr("minHeight", resizable.getMinHeight(), Integer.MIN_VALUE)
                .attr("maxHeight", resizable.getMaxHeight(), Integer.MAX_VALUE);

        if (resizable.isAnimate()) {
            wb.attr("animate", true)
                    .attr("animateEasing", resizable.getEffect())
                    .attr("animateDuration", resizable.getEffectDuration());
        }

        if (resizable.isProxy()) {
            wb.attr("helper", "ui-resizable-proxy");
        }

        wb.attr("handles", resizable.getHandles(), null)
                .attr("grid", resizable.getGrid(), 1)
                .attr("aspectRatio", resizable.isAspectRatio(), false)
                .attr("ghost", resizable.isGhost(), false);

        if (resizable.isContainment()) {
            wb.attr("isContainment", true);
            wb.attr("parentComponentId", resizable.getParent().getClientId(context));
        }

        wb.callback("onStart", "function(event,ui)", resizable.getOnStart())
                .callback("onResize", "function(event,ui)", resizable.getOnResize())
                .callback("onStop", "function(event,ui)", resizable.getOnStop());

        encodeClientBehaviors(context, resizable);

        wb.finish();
    }
}
