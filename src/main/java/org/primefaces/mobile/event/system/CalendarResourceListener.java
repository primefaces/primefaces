/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mobile.event.system;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.util.Constants;
import org.primefaces.util.ResourceUtils;

public class CalendarResourceListener implements SystemEventListener {
    
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot view = context.getViewRoot();
        String renderKitId = view.getRenderKitId();
        
        if(Constants.MOBILE_RENDER_KIT_ID.equals(renderKitId)) {
            ResourceUtils.addComponentResource(context, "mobile/widgets/datepicker.css", Constants.LIBRARY, "head");
            ResourceUtils.addComponentResource(context, "mobile/widgets/datepicker.js", Constants.LIBRARY, "head");
        }
    }

    public boolean isListenerForSource(Object source) {
        return true;
    }
}
