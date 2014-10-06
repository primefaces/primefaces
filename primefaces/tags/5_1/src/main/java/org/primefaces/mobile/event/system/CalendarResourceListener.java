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

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.util.Constants;

public class CalendarResourceListener implements SystemEventListener {
    
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        Calendar calendar = (Calendar) event.getSource();
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot view = context.getViewRoot();
        String renderKitId = view.getRenderKitId();
        
        if(renderKitId.equals(Constants.MOBILE_RENDER_KIT_ID)) {
            view.addComponentResource(context, createResource(context, "mobile/widgets/datepicker.css", Constants.LIBRARY, "javax.faces.resource.Stylesheet"));
            view.addComponentResource(context, createResource(context, "mobile/widgets/datepicker.js", Constants.LIBRARY, "javax.faces.resource.Script"));
        }
    }

    public boolean isListenerForSource(Object source) {
        return true;
    }
    
    private UIComponent createResource(FacesContext context, String name, String library, String renderer) {
        UIComponent resource = context.getApplication().createComponent("javax.faces.Output");
        resource.setRendererType(renderer);
        
        Map<String, Object> attrs = resource.getAttributes();
        attrs.put("name", name);
        attrs.put("library", library);
        attrs.put("target", "head");
       
        return resource;
    } 
}
