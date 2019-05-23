/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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

    private static final long serialVersionUID = 1L;
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
