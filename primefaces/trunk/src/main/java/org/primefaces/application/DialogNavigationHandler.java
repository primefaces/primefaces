/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.application;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

public class DialogNavigationHandler extends ConfigurableNavigationHandler {
    
    private ConfigurableNavigationHandler base;

    
    public DialogNavigationHandler(ConfigurableNavigationHandler base) {
        this.base = base;
    }

    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        if(outcome != null && outcome.startsWith("dialog:")) {
            NavigationCase navCase = getNavigationCase(context, fromAction, outcome.split(":")[1]);
            String toViewId = navCase.getToViewId(context);
            String url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, null, false);
            
            RequestContext requestContext = RequestContext.getCurrentInstance();
            if(requestContext != null) {
                Map<Object,Object> attrs = requestContext.getAttributes();
                String sourceComponentId = (String) attrs.get("sourceComponentId");
                String sourceWidget = (String) attrs.get("sourceWidget");
                String dcid = UUID.randomUUID().toString();
                String script = "PrimeFaces.openDialog({url:'" + url + "',dcid:'" + dcid + "',sourceComponentId:'" + sourceComponentId + "'";
                
                if(sourceWidget != null) {
                    script += ",sourceWidget:" + sourceWidget;
                }
                
                script += "});";
                
                requestContext.execute(script);
            }
        }
        else {
            base.handleNavigation(context, fromAction, outcome);
        }
    }

    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        return base.getNavigationCase(context, fromAction, outcome);
    }

    @Override
    public Map<String, Set<NavigationCase>> getNavigationCases() {
        return base.getNavigationCases();
    }
}
