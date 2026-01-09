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
package org.primefaces.component.defaultcommand;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = DefaultCommand.DEFAULT_RENDERER, componentFamily = DefaultCommand.COMPONENT_FAMILY)
public class DefaultCommandRenderer extends CoreRenderer<DefaultCommand> {

    @Override
    public void encodeEnd(FacesContext context, DefaultCommand component) throws IOException {
        UIComponent target = SearchExpressionUtils.contextlessResolveComponent(context, component, component.getTarget());

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DefaultCommand", component)
                .attr("target", target.getClientId(context));

        String scope = component.getScope();
        if (scope != null) {
            UIComponent scopeComponent = SearchExpressionUtils.contextlessResolveComponent(context, component, scope);
            wb.attr("scope", scopeComponent.getClientId(context));
        }

        wb.finish();
    }
}
