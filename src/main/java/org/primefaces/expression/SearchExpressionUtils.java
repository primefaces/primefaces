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
package org.primefaces.expression;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;

public class SearchExpressionUtils {

    public static VisitContext createVisitContext(FacesContext context, int hints) {
        if (isHintSet(hints, SearchExpressionHint.SKIP_UNRENDERED)) {
            return VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
        }

        return VisitContext.createVisitContext(context);
    }

    public static boolean isHintSet(int hints, int hint) {
        return (hints & hint) != 0;
    }

     // used by p:resolveClientId
    public static String resolveClientId(String expression, UIComponent source) {
        return SearchExpressionFacade.resolveClientId(
                FacesContext.getCurrentInstance(),
                source,
                expression);
    }

    // used by p:resolveClientIds
    public static String resolveClientIds(String expressions, UIComponent source) {
        return SearchExpressionFacade.resolveClientIds(
                FacesContext.getCurrentInstance(),
                source,
                expressions);
    }

    // used by p:resolveWidgetVar
    public static String resolveWidgetVar(String expression, UIComponent component) {
        UIComponent resolvedComponent = SearchExpressionFacade.resolveComponent(
                FacesContext.getCurrentInstance(),
                component,
                expression);

        if (resolvedComponent instanceof Widget) {
            return "PF('" + ((Widget) resolvedComponent).resolveWidgetVar() + "')";
        }
        else {
            throw new FacesException("Component with clientId " + resolvedComponent.getClientId() + " is not a Widget");
        }
    }
}
