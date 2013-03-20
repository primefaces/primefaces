package org.primefaces.application;

import java.util.Map;
import java.util.Set;
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
                String sourceComponentId = (String) requestContext.getCallbackParams().get("sourceComponentId");
                String sourceWidget = (String) requestContext.getCallbackParams().get("sourceWidget");
                String script = "PrimeFaces.openDialog({url:'" + url + "', sourceComponentId:'" + sourceComponentId + "'";
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
