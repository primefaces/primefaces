/*
 * Copyright 2009-2013 PrimeTek.
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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;

import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.StringEncrypter;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.visit.ResetInputVisitCallback;

public class DefaultRequestContext extends RequestContext {

    private final static String ATTRIBUTES_KEY = "ATTRIBUTES";
    private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private final static String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";
    private final static String APPLICATION_CONTEXT_KEY = DefaultApplicationContext.class.getName();

    private Map<String, Object> attributes;
    private WidgetBuilder widgetBuilder;
    private AjaxRequestBuilder ajaxRequestBuilder;
    private FacesContext context;
    private StringEncrypter encrypter;
    private ApplicationContext applicationContext;

    public DefaultRequestContext(FacesContext context) {
    	this.context = context;
    	this.attributes = new HashMap<String, Object>();
    	this.widgetBuilder = new WidgetBuilder();
    	this.ajaxRequestBuilder = new AjaxRequestBuilder(context);

    	// get applicationContext from application map
    	this.applicationContext = (ApplicationContext) context.getExternalContext().getApplicationMap().get(APPLICATION_CONTEXT_KEY);
    	if (this.applicationContext == null) {
    		this.applicationContext = new DefaultApplicationContext(context);
			context.getExternalContext().getApplicationMap().put(APPLICATION_CONTEXT_KEY, this.applicationContext);
    	}
    }

    @Override
    public boolean isAjaxRequest() {
        return context.getPartialViewContext().isAjaxRequest();
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

    @Override
	@SuppressWarnings("unchecked")
    public List<String> getScriptsToExecute() {
        if(attributes.get(EXECUTE_SCRIPT_KEY) == null) {
            attributes.put(EXECUTE_SCRIPT_KEY, new ArrayList());
        }
        return (List<String>) attributes.get(EXECUTE_SCRIPT_KEY);
    }

    @Override
	public WidgetBuilder getWidgetBuilder() {
        return widgetBuilder;
    }

	@Override
	public AjaxRequestBuilder getAjaxRequestBuilder() {
		return ajaxRequestBuilder;
	}
    
    @Override
    public void scrollTo(String clientId) {
        this.execute("PrimeFaces.scrollTo('" + clientId +  "');");
    }

    @Override
    public void update(String clientId) {
    	context.getPartialViewContext().getRenderIds().add(clientId);
    }

    @Override
    public void update(Collection<String> collection) {
    	context.getPartialViewContext().getRenderIds().addAll(collection);
    }

    @Override
    public void reset(Collection<String> ids) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        for(String id : ids) {
        	reset(visitContext, id);
        }
    }

    @Override
    public void reset(String id) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        reset(visitContext, id);
    }
    
    private void reset(VisitContext visitContext, String id) {
        UIViewRoot root = context.getViewRoot();

        UIComponent targetComponent = root.findComponent(id);
        if(targetComponent == null) {
            throw new FacesException("Cannot find component with identifier \"" + id + "\" referenced from viewroot.");
        }

        targetComponent.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
    }

    @Override
    public void returnFromDialog(Object data) {
        Map<String,Object> session = context.getExternalContext().getSessionMap();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String dcid = params.get("dcid");
        session.put(dcid, data);

        this.execute("PrimeFaces.hideDialog({dcid:'" + dcid + "'});");
    }
    
    @Override
    public void release() {
        attributes = null;
        widgetBuilder = null;
        ajaxRequestBuilder = null;
        context = null;
        applicationContext = null;
        encrypter = null;

    	setCurrentInstance(null);
    }

    @Override
    public Map<Object, Object> getAttributes() {
        if(attributes.get(ATTRIBUTES_KEY) == null) {
            attributes.put(ATTRIBUTES_KEY, new HashMap<Object, Object>());
        }
        return (Map<Object, Object>) attributes.get(ATTRIBUTES_KEY);
    }

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public StringEncrypter getEncrypter() {
		// lazy init, it's not required for all pages
    	if (encrypter == null) {
	    	// we can't store it in the ApplicationMap, as Cipher isn't thread safe
	    	encrypter = new StringEncrypter(applicationContext.getConfig().getSecretKey());
    	}

		return encrypter;
	}
}
