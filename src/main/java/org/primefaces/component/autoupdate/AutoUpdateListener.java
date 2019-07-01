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
package org.primefaces.component.autoupdate;

import java.util.ArrayList;
import java.util.List;
import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRenderComponentEvent;

/**
 * Registers components to auto update
 */
public class AutoUpdateListener implements ComponentSystemEventListener {

    private static final String COMPONENT_CLIENT_IDS = AutoUpdateListener.class.getName() + ".COMPONENT_CLIENT_IDS";

    private static final AutoUpdateListener INSTANCE = new AutoUpdateListener();

    private ValueExpression disabled;

    public AutoUpdateListener() {
    }

    public AutoUpdateListener(ValueExpression disabled) {
        this();
        this.disabled = disabled;
    }

    @Override
    public void processEvent(ComponentSystemEvent cse) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = ((UIComponent) cse.getSource()).getClientId(context);

        List<String> clientIds = getOrCreateAutoUpdateComponentClientIds(context);
        if (disabled == null || ((boolean) disabled.getValue(context.getELContext())) != true) {
            if (!clientIds.contains(clientId)) {
                clientIds.add(clientId);
            }
        }
        else {
            clientIds.remove(clientId);
        }
    }

    public static List<String> getOrCreateAutoUpdateComponentClientIds(FacesContext context) {
        List<String> clientIds = getAutoUpdateComponentClientIds(context);
        if (clientIds == null) {
            clientIds = new ArrayList<>();
            context.getViewRoot().getAttributes().put(COMPONENT_CLIENT_IDS, clientIds);
        }
        return clientIds;
    }

    public static List<String> getAutoUpdateComponentClientIds(FacesContext context) {
        return (List<String>) context.getViewRoot().getAttributes().get(COMPONENT_CLIENT_IDS);
    }

    public static void subscribe(UIComponent component) {
        subscribe(component, INSTANCE);
    }

    public static void subscribe(UIComponent component, ValueExpression disabled) {
        AutoUpdateListener listener = new AutoUpdateListener(disabled);
        subscribe(component, listener);
    }

    protected static void subscribe(UIComponent component, ComponentSystemEventListener listener) {

        // PostAddToViewEvent should work for stateless views
        //                  but fails for MyFaces ViewPooling
        //                  and sometimes on postbacks as PostAddToViewEvent should actually ony be called once
        component.subscribeToEvent(PostAddToViewEvent.class, listener);

        // PreRenderComponentEvent should work for normal cases and MyFaces ViewPooling
        //                      but likely fails for stateless view as we save the clientIds in the viewRoot
        component.subscribeToEvent(PreRenderComponentEvent.class, listener);
    }
}
