/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.cdk.api.component;

import org.primefaces.cdk.api.PrimeBehaviorEventKeys;
import org.primefaces.cdk.api.PrimeFacetKeys;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.facet.PrimeFacet;

import java.util.List;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

public interface PrimeComponent {

    PrimePropertyKeys[] getPropertyKeys();

    PrimeFacetKeys[] getFacetKeys();

    default UIComponent getFacet(PrimeFacetKeys facetKey) {
        UIComponent facet = getFacet(facetKey.getName());
        if (facet != null) {
            return facet;
        }

        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent child = getChildren().get(i);
                if (child instanceof PrimeFacet && ((PrimeFacet) child).getName().equals(facetKey.getName())) {
                    return child;
                }
            }
        }

        return null;
    }

    UIComponent getFacet(String name);

    default ValueExpression getValueExpression(PrimePropertyKeys property) {
        return getValueExpression(property.getName());
    }

    ValueExpression getValueExpression(String name);

    List<UIComponent> getChildren();

    int getChildCount();

    default boolean isClientBehaviorRequestSource(FacesContext context) {
        UIComponent component = (UIComponent) this;
        String partialSource = context.getExternalContext().getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        return component.getClientId(context).equals(partialSource);
    }

    default boolean isClientBehaviorRequestEvent(FacesContext context, PrimeBehaviorEventKeys... targetEvents) {
        UIComponent component = (UIComponent) this;
        ExternalContext externalContext = context.getExternalContext();
        String partialSource = externalContext.getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        String partialEvent = externalContext.getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (component.getClientId(context).equals(partialSource)) {
            for (PrimeBehaviorEventKeys targetEvent : targetEvents) {
                if (partialEvent.equals(targetEvent.getEventName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /* not needed yet - maybe a alternative name to widgetVar
    @Property
    String getRef();
    void setRef(String ref);
    */
}
