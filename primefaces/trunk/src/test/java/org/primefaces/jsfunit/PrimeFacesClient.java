/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.jsfunit;

import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.w3c.dom.Element;

public class PrimeFacesClient {

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
        this.componentId = componentId;
    }
    
    public void setComponentID(String id) {
        this.componentId = id;
    }
    
    protected String getComponentID() {
        return this.componentId;
    }
    
    public Element getRootElement() {
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
}
