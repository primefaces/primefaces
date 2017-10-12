/**
 * Copyright 2009-2017 PrimeTek.
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

import java.util.ArrayList;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.component.api.AutoUpdatable;
import org.primefaces.context.RequestContext;

/**
 * Registers components to auto update before rendering
 */
public class AutoUpdateComponentListener implements SystemEventListener {

    private static final String COMPONENTS = AutoUpdateComponentListener.class.getName() + ".COMPONENTS";
    
    @Override
    public void processEvent(SystemEvent cse) throws AbortProcessingException {
        if (!(cse.getSource() instanceof AutoUpdatable)) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        AutoUpdatable component = (AutoUpdatable) cse.getSource();
        String clientId = ((UIComponent) component).getClientId(context);

        // PostAddToViewEvent should work for stateless views
        //                  but fails for MyFaces ViewPooling
        //                  and sometimes on postbacks as PostAddToViewEvent should actually ony be called once
        if (cse instanceof PostAddToViewEvent) {
            if (component.isAutoUpdate() && context.isPostback()) {
                if (!RequestContext.getCurrentInstance(context).isIgnoreAutoUpdate() &&
                        !context.getPartialViewContext().getRenderIds().contains(clientId)) {
                    context.getPartialViewContext().getRenderIds().add(clientId);
                }
            }
        }
        // PreRenderComponentEvent should work for normal cases and MyFaces ViewPooling
        //                      but likely fails for stateless view as we save the clientIds in the viewRoot
        else if (cse instanceof PreRenderComponentEvent) {
            ArrayList<String> clientIds = getAutoUpdateComponentClientIds(context);
            if (clientIds == null) {
                clientIds = new ArrayList<String>();
                context.getViewRoot().getAttributes().put(COMPONENTS, clientIds);
            }

            if (component.isAutoUpdate()) {
                if (!clientIds.contains(clientId)) {
                    clientIds.add(clientId);
                }
            }
            else {
                clientIds.remove(clientId);
            }
        }
    }

    @Override
    public boolean isListenerForSource(Object o) {
        return o instanceof AutoUpdatable;
    }
    
    public static ArrayList<String> getAutoUpdateComponentClientIds(FacesContext context) {
        return (ArrayList<String>) context.getViewRoot().getAttributes().get(COMPONENTS);
    }
}
