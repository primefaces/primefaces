/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.renderkit;

import javax.faces.application.FacesMessage;
import org.primefaces.component.api.UINotification;

public class UINotificationRenderer extends CoreRenderer {
    
    protected boolean shouldRender(UINotification component, FacesMessage message, String severityName) {
        String severityLevel = component.getSeverity();
        
        if((message.isRendered() && !component.isRedisplay()) || (severityLevel != null && severityLevel.indexOf(severityName) == -1)) {
            return false;
        }
        else {
            return true;
        }
    }
    
    protected String getSeverityName(FacesMessage message) {
        int ordinal = message.getSeverity().getOrdinal();
        String severity = null;

        if(ordinal == FacesMessage.SEVERITY_INFO.getOrdinal())
            severity = "info";
        else if(ordinal == FacesMessage.SEVERITY_ERROR.getOrdinal())
            severity = "error";
        else if(ordinal == FacesMessage.SEVERITY_WARN.getOrdinal())
            severity = "warn";
        else if(ordinal == FacesMessage.SEVERITY_FATAL.getOrdinal())
            severity = "fatal";
        
        return severity;
    }
}
