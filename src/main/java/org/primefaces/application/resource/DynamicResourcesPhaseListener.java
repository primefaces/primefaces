package org.primefaces.application.resource;

import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import org.primefaces.context.RequestContext;
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
        
        // skip update=@all as the head will all resources will already be rendered
        if (context.getPartialViewContext().isRenderAll()) {
            return;
        }

        // JSF 2.3 contains a own dynamic resource handling
        if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF23()) {
            return;
        }

        // collect all current resources before new components can be added to the view in later phases
        ArrayList<ResourceUtils.ResourceInfo> initialResources = ResourceUtils.getComponentResources(context);

        putInitialResources(context, initialResources);
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
    
    public static void putInitialResources(FacesContext context, ArrayList<ResourceUtils.ResourceInfo> resources) {
        context.getAttributes().put(INITIAL_RESOURCES, resources);
    }
    
    public static ArrayList<ResourceUtils.ResourceInfo> getInitialResources(FacesContext context) {
        return (ArrayList<ResourceUtils.ResourceInfo>) context.getAttributes().get(INITIAL_RESOURCES);
    }
    
}
