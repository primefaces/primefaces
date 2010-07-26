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
package org.primefaces.application;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.RenderKit;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.primefaces.component.api.AjaxComponent;
import org.primefaces.component.PartialViewRoot;
import org.primefaces.context.RequestContext;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.util.ApplicationUtils;
import org.primefaces.util.ArrayUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.RendererUtils;

public class PrimeFacesPhaseListener implements PhaseListener {
	
	private static final Logger logger = Logger.getLogger(PrimeFacesPhaseListener.class.getName());

	public void afterPhase(PhaseEvent phaseEvent) {
		RequestContext.getCurrentInstance().release();
	}
	
	public void beforePhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		boolean isAjaxRequest = params.containsKey(Constants.PARTIAL_REQUEST_PARAM);
		boolean isPartialViewProcess = params.containsKey(Constants.PARTIAL_PROCESS_PARAM) && !params.get(Constants.PARTIAL_PROCESS_PARAM).equals("@all");
		
		if(isPartialViewProcess) {
			restorePartialView(facesContext);
		}

		if(isAjaxRequest) {
			handleAjaxRequest(facesContext);
		}
	}
	
	private void handleAjaxRequest(FacesContext facesContext) {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String viewNamespace = ApplicationUtils.getViewNamespace(facesContext);
		
		if(logger.isLoggable(Level.FINE)) {
			logger.fine("Processing PrimeFaces ajax request");
		}
		
		initPartialRenderView(facesContext);
		
		boolean isPPRRequest = !params.containsKey(Constants.PARTIAL_SOURCE_PARAM);
		
		if(isPPRRequest) {
			String[] idsToUpdate = getIdsToUpdate(facesContext, requestContext);
			
			ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
			response.setContentType("text/xml");
			
			ResponseWriter writer = facesContext.getResponseWriter();
			try {
				writer.write("<partialResponse>");
				
				if(idsToUpdate != null) {
					writeComponents(facesContext, idsToUpdate, viewNamespace);
				}
				
				writeState(facesContext);
				
				if(!requestContext.getCallbackParams().isEmpty()) {
					writeCallbackParams(facesContext, requestContext);
				}
				
				writer.write("</partialResponse>");
			}catch(IOException exception) {
				exception.printStackTrace();
			}
		} else {
			String ajaxSource = params.get(Constants.PARTIAL_SOURCE_PARAM);
			
			boolean found = facesContext.getViewRoot().invokeOnComponent(facesContext, ajaxSource, RENDER_PARTIAL_RESPONSE);
			
			if(found == false) {
	        	logger.log(Level.WARNING, "Component \"{0}\" not found to be updated partially", ajaxSource);
			}
		}
		
		facesContext.responseComplete();
	}
	
	private String[] getIdsToUpdate(FacesContext facesContext, RequestContext requestContext) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String[] idsToUpdate = null;
		String declaredUpdateIds = params.get(Constants.PARTIAL_UPDATE_PARAM);
		List<String> dynamicUpdateIds = requestContext.getPartialUpdateTargets();
	
		if(declaredUpdateIds != null && !declaredUpdateIds.equals("@none")) {
			idsToUpdate = declaredUpdateIds.split("[,\\s]+");
		}
		
		if(!dynamicUpdateIds.isEmpty()) {
			if(idsToUpdate != null)
				idsToUpdate = ArrayUtils.concat(idsToUpdate, dynamicUpdateIds.toArray(new String[0]));
			else
				idsToUpdate = dynamicUpdateIds.toArray(new String[0]);
		}
		
		return idsToUpdate;
	}

	/**
	 * Restore to original for rendering
	 */
	private void restorePartialView(FacesContext facesContext) {
		PartialViewRoot partialView = (PartialViewRoot) facesContext.getViewRoot();
		
		for (int i = 0; i < partialView.getChildCount(); i++) {
			UIComponent kid = partialView.getChildren().get(i);
			kid.setParent(partialView.getParents().get(i));
		}
		
		facesContext.setViewRoot(partialView.getBase());	
		partialView = null;
	}

	/**
	 * Write state to sync with client
	 */
	private void writeState(FacesContext facesContext) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("<state>");
		RendererUtils.startCDATA(facesContext);
		
		StateManager stateManager = facesContext.getApplication().getStateManager();
		stateManager.writeState(facesContext, stateManager.saveView(facesContext));
		
		RendererUtils.endCDATA(facesContext);
		writer.write("</state>");
	}

	/**
	 * Write partial output of each component
	 */
	private void writeComponents(FacesContext facesContext, String[] ids, String namespace) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("<components>");
		for(String id : ids) {
			String clientId = id.trim();
			
			//Portlet namespace support
			if(!namespace.equals("")) {
				String viewrootClientId = facesContext.getViewRoot().getClientId(facesContext);
				
				if(viewrootClientId.indexOf(namespace) == -1)	//portlet 2
					clientId = namespace + ":" + viewrootClientId + ":" + clientId;
				else											//portlet 1
					clientId = viewrootClientId + ":" + clientId;
			}
			
			writer.write("<component>");
			writer.write("<id>" + clientId + "</id>");
			writer.write("<output>");
			RendererUtils.startCDATA(facesContext);
			
			boolean found = facesContext.getViewRoot().invokeOnComponent(facesContext, clientId, RENDER_RESPONSE);
			if(found == false) {
		    	logger.log(Level.WARNING, "Component \"{0}\" not found to be updated partially", clientId);
			}
				
			RendererUtils.endCDATA(facesContext);
			writer.write("</output>");
			writer.write("</component>");
		}
		writer.write("</components>");
	}
	
	/**
	 * Serialize each callback parameter to json and write
	 */
	private void writeCallbackParams(FacesContext facesContext, RequestContext requestContext) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Map<String,Object> params = requestContext.getCallbackParams();
		
		writer.write("<callbackParams>");
			for(String paramName : params.keySet()) {
				
				writer.write("<callbackParam>");
				try {
					Object paramValue = params.get(paramName);
					String json = isBean(paramValue) ? "{\"" + paramName + "\":" + new JSONObject(paramValue).toString() + "}": new JSONObject().put(paramName, paramValue).toString();
					writer.write(json);
				} catch (JSONException e) {
					logger.log(Level.SEVERE, "Error in serializing callback parameter \"{0}\"", paramName);
					throw new FacesException(e.getMessage());
				}
				writer.write("</callbackParam>");	
				
			}
		writer.write("</callbackParams>");	
	}
	
	private boolean isBean(Object value) {
		if(value == null)
			return false;
		
		if(value instanceof Boolean || value instanceof String || value instanceof Number) {
			return false;
	    }
		
		return true;
	}

	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
	
	public static final ContextCallback RENDER_RESPONSE = new ContextCallback() {
		public void invokeContextCallback(FacesContext facesContext, UIComponent component) {
			try {
				component.encodeAll(facesContext);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};
	
	public static final ContextCallback RENDER_PARTIAL_RESPONSE = new ContextCallback() {
		public void invokeContextCallback(FacesContext facesContext, UIComponent component) {
			try {
				((AjaxComponent) component).encodePartially(facesContext);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};
	
	//Prepare artifacts for partial page rendering
	private void initPartialRenderView(FacesContext facesContext) {
		initPartialResponseWriter(facesContext);
	}
	
	private void initPartialResponseWriter(FacesContext facesContext) {
		if(facesContext.getResponseWriter() != null)
			return;
		
		try {
			ServletResponse response = (ServletResponse) facesContext.getExternalContext().getResponse();
			ServletRequest request = (ServletRequest) facesContext.getExternalContext().getRequest();
			
			RenderKit renderKit = facesContext.getRenderKit();
			ResponseWriter responseWriter = renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());
			facesContext.setResponseWriter(responseWriter);
		}catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}