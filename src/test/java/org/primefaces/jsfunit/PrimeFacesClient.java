package org.primefaces.jsfunit;

import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.w3c.dom.Element;

public class PrimeFacesClient < T extends PrimeFacesClient > {

    protected String componentId;
    
    protected JSFClientSession client;
    
    public PrimeFacesClient(JSFClientSession client) {
        if(client == null) {
            throw new NullPointerException("Client session cannot be null.");
        }
        
        this.client = client;
    }
    
    public PrimeFacesClient(JSFClientSession client, String componentId) {
        this(client);
        setComponentID(componentId);
    }
    
    
    public T setComponentID(String id) {
        this.componentId = id;
        return (T) this;
    }
    
    protected String getComponentID() {
        return this.componentId;
    }
    
    public Element getElement() {
        return getSession().getElement(getComponentID());
    }
    
    public Element getElement(String id) {
        if(id == null) {
            throw new NullPointerException("Component id is null.");
        }
        return getSession().getElement(id);
    }
    
    protected JSFClientSession getSession() {
        return this.client;
    }
    
    public T executeJS(String js) {

        //todo
        
        return (T) this;
    }
}
