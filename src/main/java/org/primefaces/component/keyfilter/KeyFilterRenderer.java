/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.keyfilter;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.InputHolder;
import org.primefaces.context.RequestContext;
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
        ResponseWriter writer = context.getResponseWriter();
        KeyFilter keyFilter = (KeyFilter) component;

        UIComponent target;
        if (isValueBlank(keyFilter.getFor())) {
            target = component.getParent();
        }
        else {
            target = SearchExpressionFacade.resolveComponent(context, keyFilter, keyFilter.getFor());
        }

        String targetClientId = target instanceof InputHolder ? ((InputHolder) target).getInputClientId() : target.getClientId();

        WidgetBuilder wb = RequestContext.getCurrentInstance().getWidgetBuilder();
        wb.initWithDomReady(KeyFilter.class.getSimpleName(), keyFilter.resolveWidgetVar(), keyFilter.getClientId(context));
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
