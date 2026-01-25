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
package org.primefaces.cdk.api.component;

import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;

import java.util.Collection;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.BehaviorEvent;
import jakarta.faces.event.FacesEvent;

public interface PrimeClientBehaviorHolder extends ClientBehaviorHolder {

    PrimeClientBehaviorEventKeys[] getClientBehaviorEventKeys();

    Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping();

    Collection<String> getImplicitBehaviorEventNames();

    default boolean isAjaxRequestSource(FacesContext context) {
        UIComponent component = (UIComponent) this;
        String partialSource = context.getExternalContext().getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        return component.getClientId(context).equals(partialSource);
    }

    default boolean isAjaxBehaviorEventSource(FacesEvent event) {
        if (!(event instanceof AjaxBehaviorEvent)) {
            return false;
        }

        UIComponent component = (UIComponent) this;
        FacesContext context = event.getFacesContext();
        String partialSource = context.getExternalContext().getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        return component.getClientId(context).equals(partialSource);
    }

    default boolean isAjaxBehaviorEvent(FacesEvent event, PrimeClientBehaviorEventKeys... targetEvents) {
        if (!(event instanceof AjaxBehaviorEvent)) {
            return false;
        }

        UIComponent component = (UIComponent) this;
        FacesContext context = event.getFacesContext();
        ExternalContext externalContext = context.getExternalContext();
        String partialSource = externalContext.getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_SOURCE_PARAM_NAME);
        String partialEvent = externalContext.getRequestParameterMap().get(ClientBehaviorContext.BEHAVIOR_EVENT_PARAM_NAME);
        if (component.getClientId(context).equals(partialSource)) {
            for (PrimeClientBehaviorEventKeys targetEvent : targetEvents) {
                if (partialEvent.equals(targetEvent.getName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
