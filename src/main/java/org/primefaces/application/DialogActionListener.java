package org.primefaces.application;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.primefaces.component.api.Widget;
import org.primefaces.context.RequestContext;

public class DialogActionListener implements ActionListener {

    private ActionListener base;

    public DialogActionListener(ActionListener base) {
        this.base = base;
    }
        
    public void processAction(ActionEvent event) throws AbortProcessingException {
        UIComponent source = event.getComponent();
        RequestContext context = RequestContext.getCurrentInstance();
        if(source instanceof Widget) {
            context.addCallbackParam("sourceWidget", ((Widget) source).resolveWidgetVar());
        }
        
        context.addCallbackParam("sourceComponentId", source.getClientId());
        
        base.processAction(event);
    }
    
}
