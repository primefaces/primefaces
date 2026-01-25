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
package org.primefaces.component.poll;

import org.primefaces.PrimeFaces;
import org.primefaces.cdk.api.FacesComponentDescription;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = Poll.COMPONENT_TYPE, namespace = Poll.COMPONENT_FAMILY)
@FacesComponentDescription("Poll is an AJAX component that has the ability to send periodical AJAX requests and execute listeners on Faces backing beans.")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
public class Poll extends PollBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Poll";

    @Override
    public void broadcast(jakarta.faces.event.FacesEvent event) throws jakarta.faces.event.AbortProcessingException {
        super.broadcast(event); //backward compatibility

        FacesContext facesContext = getFacesContext();
        MethodExpression me = getListener();

        if (me != null) {
            me.invoke(facesContext.getELContext(), new Object[]{});
        }

        ValueExpression expr = getValueExpression(PropertyKeys.stop);
        if (expr != null) {
            Boolean stop = expr.getValue(facesContext.getELContext());

            if (Boolean.TRUE.equals(stop)) {
                String widgetVar = resolveWidgetVar();
                PrimeFaces.current().executeScript("PF('" + widgetVar + "').stop();");
            }
        }
    }

    @Override
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (getValueExpression(PropertyKeys.partialSubmit) != null);
    }

    @Override
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (getValueExpression(PropertyKeys.resetValues) != null);
    }

    @Override
    public boolean isAjaxified() {
        return true;
    }
}