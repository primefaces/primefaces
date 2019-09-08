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
package org.primefaces.component.effect;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class EffectRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Effect effect = (Effect) component;
        String clientId = effect.getClientId(context);
        String source = component.getParent().getClientId(context);
        String event = effect.getEvent();
        int delay = effect.getDelay();

        UIComponent targetComponent = SearchExpressionFacade.resolveComponent(
                context, effect, effect.getFor(), SearchExpressionHint.PARENT_FALLBACK);
        String target = targetComponent.getClientId(context);

        String animation = getEffectBuilder(effect, target).build();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Effect", effect.resolveWidgetVar(context), clientId)
                .attr("source", source)
                .attr("event", event)
                .attr("delay", delay)
                .callback("fn", "function()", animation);

        wb.finish();
    }

    private EffectBuilder getEffectBuilder(Effect effect, String effectedComponentClientId) {
        EffectBuilder effectBuilder = new EffectBuilder(effect.getType(), effectedComponentClientId, effect.isQueue());

        for (UIComponent child : effect.getChildren()) {
            if (child instanceof UIParameter) {
                UIParameter param = (UIParameter) child;

                effectBuilder.withOption(param.getName(), (String) param.getValue());
            }
        }

        effectBuilder.atSpeed(effect.getSpeed());

        return effectBuilder;
    }
}
