/*
 * Copyright 2009 Prime Technology.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;

import org.primefaces.util.Constants;

public class RequestContextImpl extends RequestContext {

	private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
	private final static String PARTIAL_UPDATE_TARGETS_KEY = "PARTIAL_UPDATE_TARGETS_KEY";
	
	private ExternalContext externalContext = null;
	private Map<Object, Object> attributes;
	private String ajaxRedirectUrl = null;
	
	public RequestContextImpl(ExternalContext externalContext) {
		this.externalContext = externalContext;
		this.attributes = new HashMap<Object, Object>();

		setCurrentInstance(this);
	}
	
	@Override
	public boolean isAjaxRequest() {
		return externalContext.getRequestParameterMap().containsKey(Constants.PARTIAL_REQUEST_PARAM);
	}
	
	@Override
	public void release() {
		externalContext = null;
		attributes = null;
		
		setCurrentInstance(null);
	}

	@Override
	public void addCallbackParam(String name, Object value) {
		getCallbackParams().put(name, value);
	}
	
	@Override
	public void addPartialUpdateTarget(String target) {
		getPartialUpdateTargets().add(target);
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
	public List<String> getPartialUpdateTargets() {
		if(attributes.get(PARTIAL_UPDATE_TARGETS_KEY) == null) {
			attributes.put(PARTIAL_UPDATE_TARGETS_KEY, new ArrayList());
		}
		return (List<String>) attributes.get(PARTIAL_UPDATE_TARGETS_KEY);
	}
	
	@Override
	public String getAjaxRedirectUrl() {
		return ajaxRedirectUrl;
	}
	
	@Override
	public void setAjaxRedirectUrl(String ajaxRedirectUrl) {
		this.ajaxRedirectUrl = ajaxRedirectUrl;
	}
}