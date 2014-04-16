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
package org.primefaces.mobile.application;

import java.util.Map;
import java.util.Set;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.mobile.util.MobileUtils;

public class MobileNavigationHandler extends ConfigurableNavigationHandler {
    
    private ConfigurableNavigationHandler base;

    
    public MobileNavigationHandler(ConfigurableNavigationHandler base) {
        this.base = base;
    }

    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        if(outcome != null && outcome.startsWith("pm:")) {
            String command = MobileUtils.buildNavigation(outcome);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            if(requestContext != null) {
                requestContext.execute(command.toString());
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