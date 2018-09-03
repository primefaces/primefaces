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
package org.primefaces.component.poll;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js")
})
public class Poll extends PollBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Poll";

    @Override
    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
        super.broadcast(event); //backward compatibility

        FacesContext facesContext = getFacesContext();
        MethodExpression me = getListener();

        if (me != null) {
            me.invoke(facesContext.getELContext(), new Object[]{});
        }

        ValueExpression expr = getValueExpression(PropertyKeys.stop.toString());
        if (expr != null) {
            Boolean stop = (Boolean) expr.getValue(facesContext.getELContext());

            if (Boolean.TRUE.equals(stop)) {
                String widgetVar = resolveWidgetVar();
                PrimeFaces.current().executeScript("PF('" + widgetVar + "').stop();");
            }
        }
    }

    @Override
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (getValueExpression(PropertyKeys.partialSubmit.toString()) != null);
    }

    @Override
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }

    @Override
    public boolean isAjaxified() {
        return true;
    }
}