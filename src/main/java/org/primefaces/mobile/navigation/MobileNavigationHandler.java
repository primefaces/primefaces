/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.mobile.navigation;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

public class MobileNavigationHandler extends NavigationHandler {
    
    private NavigationHandler base;

    
    public MobileNavigationHandler(NavigationHandler base) {
        this.base = base;
    }

    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        if(outcome.startsWith("pm:")) {
            String view = outcome.split("pm:")[1];
            
            RequestContext requestContext = RequestContext.getCurrentInstance();
            if(requestContext != null) {
                requestContext.execute("PrimeFaces.navigate('" + view + "')");
            }
        }
        else {
            base.handleNavigation(context, fromAction, outcome);
        }
    }
}