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
package org.primefaces.util;

import java.io.IOException;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

// helper to avoid java.lang.NoClassDefFoundError's in older environments
public class Jsf22Helper {

    private Jsf22Helper() {
    }

    public static void renderPassThroughAttributes(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String, Object> passthroughAttributes = component.getPassThroughAttributes(false);

        if (passthroughAttributes != null && !passthroughAttributes.isEmpty()) {
            for (Map.Entry<String, Object> attribute : passthroughAttributes.entrySet()) {

                Object attributeValue = attribute.getValue();
                if (attributeValue != null) {
                    String value = null;

                    if (attributeValue instanceof ValueExpression) {
                        Object expressionValue = ((ValueExpression) attributeValue).getValue(context.getELContext());
                        if (expressionValue != null) {
                            value = expressionValue.toString();
                        }
                    }
                    else {
                        value = attributeValue.toString();
                    }

                    if (value != null) {
                        writer.writeAttribute(attribute.getKey(), value, null);
                    }
                }
            }
        }
    }
}
