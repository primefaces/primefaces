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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

public class DefaultRequestContext extends RequestContext {

    private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private final static String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";
    private final static String PUSH_DATA_KEY = "PUSH_DATA";
    private Map<String, Object> attributes;

    public DefaultRequestContext() {
        this.attributes = new HashMap<String, Object>();

        setCurrentInstance(this);
    }

    @Override
    public boolean isAjaxRequest() {
        return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
    }

    @Override
    public void release() {
        attributes = null;

        setCurrentInstance(null);
    }

    @Override
    public void addCallbackParam(String name, Object value) {
        getCallbackParams().put(name, value);
    }

    @Override
    public void addPartialUpdateTarget(String target) {
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(target);
    }

    @Override
    public void addPartialUpdateTargets(Collection<String> collection) {
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().addAll(collection);
    }

    @Override
    public void execute(String script) {
        getScriptsToExecute().add(script);
    }

    @Override
    public void push(String channel, Object data) {
        Map<String,List<Object>> pushData = getPushData(); 
        if(!pushData.containsKey(channel)) {
            pushData.put(channel, new ArrayList<Object>());
        }
        
        pushData.get(channel).add(data);
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
    
    @SuppressWarnings("unchecked")
    public Map<String, List<Object>> getPushData() {
        if(attributes.get(PUSH_DATA_KEY) == null) {
            attributes.put(PUSH_DATA_KEY, new HashMap<String, List<Object>>());
        }
        return (Map<String, List<Object>>) attributes.get(PUSH_DATA_KEY);
    }

    @Override
    public void scrollTo(String clientId) {
        this.execute("PrimeFaces.scrollTo('" + clientId +  "');");
    }
}