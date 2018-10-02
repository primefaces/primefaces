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
package org.primefaces.component.autoupdate;

import java.util.ArrayList;

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
    private static final AutoUpdateListener INSTANCE_ENABLED = new AutoUpdateListener(false);
    private static final AutoUpdateListener INSTANCE_DISABLED = new AutoUpdateListener(true);

    private final boolean disabled;

    public AutoUpdateListener() {
        disabled = false;
    }

    public AutoUpdateListener(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public void processEvent(ComponentSystemEvent cse) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = ((UIComponent) cse.getSource()).getClientId(context);

        ArrayList<String> clientIds = getOrCreateAutoUpdateComponentClientIds(context);
        if (disabled) {
            clientIds.remove(clientId);
        }
        else {
            if (!clientIds.contains(clientId)) {
                clientIds.add(clientId);
            }
        }
    }

    public static ArrayList<String> getOrCreateAutoUpdateComponentClientIds(FacesContext context) {
        ArrayList<String> clientIds = getAutoUpdateComponentClientIds(context);
        if (clientIds == null) {
            clientIds = new ArrayList<>();
            context.getViewRoot().getAttributes().put(COMPONENT_CLIENT_IDS, clientIds);
        }
        return clientIds;
    }

    public static ArrayList<String> getAutoUpdateComponentClientIds(FacesContext context) {
        return (ArrayList<String>) context.getViewRoot().getAttributes().get(COMPONENT_CLIENT_IDS);
    }

    public static void subscribe(UIComponent component, boolean disabled) {
        // PostAddToViewEvent should work for stateless views
        //                  but fails for MyFaces ViewPooling
        //                  and sometimes on postbacks as PostAddToViewEvent should actually ony be called once
        component.subscribeToEvent(PostAddToViewEvent.class, disabled ? INSTANCE_DISABLED : INSTANCE_ENABLED);

        // PreRenderComponentEvent should work for normal cases and MyFaces ViewPooling
        //                      but likely fails for stateless view as we save the clientIds in the viewRoot
        component.subscribeToEvent(PreRenderComponentEvent.class, disabled ? INSTANCE_DISABLED : INSTANCE_ENABLED);
    }
}
