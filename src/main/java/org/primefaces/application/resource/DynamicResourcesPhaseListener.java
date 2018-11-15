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
package org.primefaces.application.resource;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.ResourceUtils;

public class DynamicResourcesPhaseListener implements PhaseListener {

    private static final String INITIAL_RESOURCES = DynamicResourcesPhaseListener.class.getName() + ".INITIAL_RESOURCES";

    @Override
    public void beforePhase(PhaseEvent event) {

    }

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();

        // we only need to collect resources on ajax requests
        // for non ajax, the head will always be rendered again
        if (context.getViewRoot() == null || !context.getPartialViewContext().isAjaxRequest()) {
            return;
        }

        // we can also skip non-postback ajax requests, which occurs e.g. without a form
        if (!context.isPostback()) {
            return;
        }

        // skip update=@all as the head, with all resources, will already be rendered
        if (context.getPartialViewContext().isRenderAll()) {
            return;
        }

        // JSF 2.3 contains a own dynamic resource handling
        if (PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isAtLeastJsf23()) {
            return;
        }

        // collect all current resources before new components can be added to the view in later phases
        List<ResourceUtils.ResourceInfo> initialResources = ResourceUtils.getComponentResources(context);

        putInitialResources(context, initialResources);
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public static void putInitialResources(FacesContext context, List<ResourceUtils.ResourceInfo> resources) {
        context.getAttributes().put(INITIAL_RESOURCES, resources);
    }

    public static List<ResourceUtils.ResourceInfo> getInitialResources(FacesContext context) {
        return (List<ResourceUtils.ResourceInfo>) context.getAttributes().get(INITIAL_RESOURCES);
    }

}
