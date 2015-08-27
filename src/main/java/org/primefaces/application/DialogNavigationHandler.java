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
package org.primefaces.application;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.util.Constants;

public class DialogNavigationHandler extends ConfigurableNavigationHandler {
    
    private ConfigurableNavigationHandler base;

    
    public DialogNavigationHandler(ConfigurableNavigationHandler base) {
        this.base = base;
    }

    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        Map<Object,Object> attrs = requestContext.getAttributes();
        String dialogOutcome = (String) attrs.get(Constants.DIALOG_FRAMEWORK.OUTCOME);
        
        if(dialogOutcome != null) {
            NavigationCase navCase = getNavigationCase(context, fromAction, dialogOutcome);
            String toViewId = navCase.getToViewId(context);
            Map<String,Object> options = (Map<String,Object>) attrs.get(Constants.DIALOG_FRAMEWORK.OPTIONS);
            Map<String,List<String>> params = (Map<String,List<String>>) attrs.get(Constants.DIALOG_FRAMEWORK.PARAMS);

            if (params == null) {
                params = Collections.emptyMap();
            }
            
            boolean includeViewParams = false;
            if (options != null && options.containsKey(Constants.DIALOG_FRAMEWORK.INCLUDE_VIEW_PARAMS)) {
                includeViewParams = (Boolean) options.get(Constants.DIALOG_FRAMEWORK.INCLUDE_VIEW_PARAMS);
            }

            String url = context.getApplication().getViewHandler().getBookmarkableURL(context, toViewId, params, includeViewParams);
            
            StringBuilder sb = new StringBuilder();
            String sourceComponentId = (String) attrs.get(Constants.DIALOG_FRAMEWORK.SOURCE_COMPONENT);
            String sourceWidget = (String) attrs.get(Constants.DIALOG_FRAMEWORK.SOURCE_WIDGET);
            String pfdlgcid = UUID.randomUUID().toString();
                        
            sb.append("PrimeFaces.openDialog({url:'").append(url).append("',pfdlgcid:'").append(pfdlgcid)
                                    .append("',sourceComponentId:'").append(sourceComponentId).append("'");

            if(sourceWidget != null) {
                sb.append(",sourceWidget:PF('").append(sourceWidget).append("')");
            }
            
            sb.append(",options:{");
            if(options != null && options.size() > 0) {
                for(Iterator<String> it = options.keySet().iterator(); it.hasNext();) {
                    String optionName = it.next();
                    Object optionValue = options.get(optionName);
                    
                    sb.append(optionName).append(":");
                    if(optionValue instanceof String)
                        sb.append("'").append(optionValue).append("'");
                    else 
                        sb.append(optionValue);
                    
                    if(it.hasNext())
                        sb.append(",");
                }
            }
            sb.append("}});");
            
            requestContext.execute(sb.toString());
            sb.setLength(0);
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
