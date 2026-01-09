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
package org.primefaces.component.autoupdate;

import org.primefaces.util.LangUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.event.ComponentSystemEventListener;
import jakarta.faces.event.PostAddToViewEvent;
import jakarta.faces.event.PreRenderComponentEvent;

/**
 * Registers components to auto update
 */
public class AutoUpdateListener implements ComponentSystemEventListener, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String COMPONENT_CLIENT_IDS = AutoUpdateListener.class.getName() + ".COMPONENT_CLIENT_IDS";

    private static final AutoUpdateListener INSTANCE = new AutoUpdateListener();

    private ValueExpression disabled;
    private String on;

    public AutoUpdateListener() {
    }

    public AutoUpdateListener(ValueExpression disabled) {
        this();
        this.disabled = disabled;
    }

    public AutoUpdateListener(String on) {
        this();
        this.on = on;
    }

    public AutoUpdateListener(ValueExpression disabled, String on) {
        this();
        this.disabled = disabled;
        this.on = on;
    }

    @Override
    public void processEvent(ComponentSystemEvent cse) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = ((UIComponent) cse.getSource()).getClientId(context);

        Map<String, List<String>> infos = getOrCreateAutoUpdateComponentInfos(context);
        if (disabled == null || !((boolean) disabled.getValue(context.getELContext()))) {
            if (!infos.containsKey(clientId)) {
                if (on == null) {
                    infos.put(clientId, null);
                }
                else {
                    String[] onList = context.getApplication().getSearchExpressionHandler().splitExpressions(context, on);
                    infos.put(clientId, Arrays.asList(onList));
                }
            }
        }
        else {
            infos.remove(clientId);
        }
    }

    public static Map<String, List<String>> getOrCreateAutoUpdateComponentInfos(FacesContext context) {
        Map<String, List<String>> infos = getAutoUpdateComponentInfos(context);
        if (infos == null) {
            infos = new HashMap<>();
            context.getViewRoot().getAttributes().put(COMPONENT_CLIENT_IDS, infos);
        }
        return infos;
    }

    public static Map<String, List<String>> getAutoUpdateComponentInfos(FacesContext context) {
        return (Map<String, List<String>>) context.getViewRoot().getAttributes().get(COMPONENT_CLIENT_IDS);
    }

    public static void subscribe(UIComponent component) {
        subscribe(component, INSTANCE);
    }

    public static void subscribe(UIComponent component, String on) {
        if (LangUtils.isBlank(on)) {
            subscribe(component, INSTANCE);
        }
        else {
            subscribe(component, new AutoUpdateListener(on));
        }
    }

    public static void subscribe(UIComponent component, ValueExpression disabled) {
        AutoUpdateListener listener = new AutoUpdateListener(disabled);
        subscribe(component, listener);
    }

    public static void subscribe(UIComponent component, ValueExpression disabled, String on) {
        AutoUpdateListener listener = new AutoUpdateListener(disabled, on);
        subscribe(component, listener);
    }

    protected static void subscribe(UIComponent component, ComponentSystemEventListener listener) {

        // PreRenderComponentEvent works for normal views (stateful) and even MyFaces ViewPooling
        //                      but fails for stateless view as we can't save the clientIds in the viewRoot
        component.subscribeToEvent(PreRenderComponentEvent.class, listener);

        // In case of stateless views, we cant access previous rendered auto-updatable components
        // so we need to listen to PostAddToViewEvent - independent if the component is rendered or not
        // see #11408
        if (FacesContext.getCurrentInstance().getViewRoot().isTransient()) {
            component.subscribeToEvent(PostAddToViewEvent.class, listener);
        }
    }
}
