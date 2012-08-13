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
package org.primefaces.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;

import javax.faces.context.FacesContext;
import org.primefaces.visit.ResetInputVisitCallback;

public class DefaultRequestContext extends RequestContext {

    private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private final static String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";
    private Map<String, Object> attributes;

    public DefaultRequestContext() {
        attributes = new HashMap<String, Object>();
    }

    @Override
    public boolean isAjaxRequest() {
        return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
    }

    @Override
    public void addCallbackParam(String name, Object value) {
        getCallbackParams().put(name, value);
    }

    @Override
    public void execute(String script) {
        getScriptsToExecute().add(script);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCallbackParams() {
        if(attributes.get(CALLBACK_PARAMS_KEY) == null) {
            attributes.put(CALLBACK_PARAMS_KEY, new HashMap<String, Object>());
        }
        return (Map<String, Object>) attributes.get(CALLBACK_PARAMS_KEY);
    }

    @SuppressWarnings("unchecked")
    public List<String> getScriptsToExecute() {
        if(attributes.get(EXECUTE_SCRIPT_KEY) == null) {
            attributes.put(EXECUTE_SCRIPT_KEY, new ArrayList());
        }
        return (List<String>) attributes.get(EXECUTE_SCRIPT_KEY);
    }
    
    @Override
    public void scrollTo(String clientId) {
        this.execute("PrimeFaces.scrollTo('" + clientId +  "');");
    }

    @Override
    public void update(String clientId) {
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(clientId);
    }

    @Override
    public void update(Collection<String> collection) {
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().addAll(collection);
    }

    @Override
    public void reset(Collection<String> ids) {        
        FacesContext context = FacesContext.getCurrentInstance();
        EnumSet<VisitHint> hints = EnumSet.of(VisitHint.SKIP_UNRENDERED);
        VisitContext visitContext = VisitContext.createVisitContext(context, null, hints);
        VisitCallback visitCallback = new ResetInputVisitCallback();
        UIViewRoot root = context.getViewRoot();
                
        for(String id : ids) {
            UIComponent targetComponent = root.findComponent(id);
            if(targetComponent == null) {
                throw new FacesException("Cannot find component with identifier \"" + id + "\" referenced from viewroot.");
            }
            
            targetComponent.visitTree(visitContext, visitCallback);
        }
    }
    
    @Override
    public void reset(String id) {
        Collection<String> list = new ArrayList<String>();
        list.add(id);
        
        reset(list);
    }
}