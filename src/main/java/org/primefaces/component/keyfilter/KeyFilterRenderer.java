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
package org.primefaces.component.keyfilter;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.InputHolder;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class KeyFilterRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        KeyFilter keyFilter = (KeyFilter) component;

        UIComponent target;
        if (isValueBlank(keyFilter.getFor())) {
            target = component.getParent();
        }
        else {
            target = SearchExpressionFacade.resolveComponent(context, keyFilter, keyFilter.getFor());
        }

        String targetClientId = target instanceof InputHolder ? ((InputHolder) target).getInputClientId() : target.getClientId();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init(KeyFilter.class.getSimpleName(), keyFilter.resolveWidgetVar(context), keyFilter.getClientId(context));
        wb.attr("target", targetClientId);

        if (keyFilter.getRegEx() != null) {
            wb.nativeAttr("regEx", keyFilter.getRegEx());
        }
        else if (keyFilter.getInputRegEx() != null) {
            wb.nativeAttr("inputRegEx", keyFilter.getInputRegEx());
        }
        else if (keyFilter.getMask() != null) {
            wb.attr("mask", keyFilter.getMask());
        }
        else if (keyFilter.getTestFunction() != null) {
            wb.callback("testFunction", "function(c)", keyFilter.getTestFunction() + ";");
        }

        if (keyFilter.isPreventPaste()) {
            wb.attr("preventPaste", keyFilter.isPreventPaste());
        }

        wb.finish();
    }

}
