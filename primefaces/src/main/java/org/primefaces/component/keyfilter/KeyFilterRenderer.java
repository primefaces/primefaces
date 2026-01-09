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
package org.primefaces.component.keyfilter;

import org.primefaces.component.api.InputHolder;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = KeyFilter.DEFAULT_RENDERER, componentFamily = KeyFilter.COMPONENT_FAMILY)
public class KeyFilterRenderer extends CoreRenderer<KeyFilter> {

    @Override
    public void decode(FacesContext context, KeyFilter component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, KeyFilter component) throws IOException {
        UIComponent target;
        if (isValueBlank(component.getFor())) {
            target = component.getParent();
        }
        else {
            target = SearchExpressionUtils.contextlessResolveComponent(context, component, component.getFor());
        }

        String targetClientId = target instanceof InputHolder ? ((InputHolder) target).getInputClientId() : target.getClientId();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(KeyFilter.class.getSimpleName(), component);
        wb.attr("target", targetClientId);

        if (component.getRegEx() != null) {
            wb.nativeAttr("regEx", component.getRegEx());
        }
        else if (component.getInputRegEx() != null) {
            wb.nativeAttr("inputRegEx", component.getInputRegEx());
        }
        else if (component.getMask() != null) {
            wb.attr("mask", component.getMask());
        }
        else if (component.getTestFunction() != null) {
            wb.callback("testFunction", "function(c)", component.getTestFunction() + ";");
        }

        if (component.isPreventPaste()) {
            wb.attr("preventPaste", component.isPreventPaste());
        }

        wb.finish();
    }

}
