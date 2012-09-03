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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.util.Constants;

/**
 * A RequestContext is a helper class consisting of several utilities.
 * RequestContext is thread-safe and scope is same as FacesContext.
 * Current instance can be retrieved as;
 * <blockquote>
 *  RequestContext.getCurrentInstance();
 * </blockquote>
 */
public abstract class RequestContext {

    /**
     * @return Current RequestRequest instance in thread-local.
     */
    public static RequestContext getCurrentInstance() {
        return (RequestContext) FacesContext.getCurrentInstance().getAttributes().get(Constants.REQUEST_CONTEXT_ATTR);
    }

    /**
     * @return true if request is an ajax request, otherwise return false.
     */
    public abstract boolean isAjaxRequest();

    /**
     * Add a parameter for ajax oncomplete client side callbacks. Value would be serialized to json.
     * @param name name of the parameter.
     * @param object value of the parameter.
     */
    public abstract void addCallbackParam(String name, Object value);

    /**
     * @return all callback parameters added in the current request.
     */
    public abstract Map<String, Object> getCallbackParams();

    /**
     * @return all scripts added in the current request.
     */
    public abstract List<String> getScriptsToExecute();

    /**
     * Execute a javascript after current ajax request is completed.
     * @param script Javascript statement to execute.
     */
    public abstract void execute(String script);
    
    /**
     * Scroll to a component after ajax request is completed.
     * @param clientId Client side identifier of the component.
     */
    public abstract void scrollTo(String clientId);
    
    /**
     * Update a component with ajax.
     * @param name Client side identifier of the component.
     */
    public abstract void update(String name);

    /**
     * Update components with ajax.
     * @param collection Client side identifiers of the components.
     */
    public abstract void update(Collection<String> collection);
    
    /**
     * Reset an editableValueHolder.
     * @param id Client side identifier of the component.
     */
    public abstract void reset(String id);
    
    /**
     * Reset a collection of editableValueHolders.
     * @param ids Client side identifiers of the components.
     */
    public abstract void reset(Collection<String> ids);
}
