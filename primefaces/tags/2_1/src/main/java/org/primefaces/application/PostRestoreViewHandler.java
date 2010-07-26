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

import java.util.Map;

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.primefaces.component.PartialViewRoot;
import org.primefaces.context.RequestContextImpl;
import org.primefaces.util.Constants;

public class PostRestoreViewHandler implements PhaseListener {

	public void afterPhase(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		//Setup Request Context
		new RequestContextImpl(facesContext.getExternalContext());
		
		//Handle partial view process
		boolean isPartialViewProcess = params.containsKey(Constants.PARTIAL_PROCESS_PARAM) && !params.get(Constants.PARTIAL_PROCESS_PARAM).equals("@all");
		
		if(isPartialViewProcess) {	
			String processIds = params.get(Constants.PARTIAL_PROCESS_PARAM);
			
			buildPartialView(facesContext, processIds);
		}
	}

	public void beforePhase(PhaseEvent event) {
		//Do Nothing
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
	
	private void buildPartialView(FacesContext facesContext, String processIds) {
		UIViewRoot originalView = facesContext.getViewRoot();
		PartialViewRoot partialView = new PartialViewRoot(originalView);
		facesContext.setViewRoot(partialView);
		
		if(processIds.equals("@none")) {
			return;
		}

		String[] ids = processIds.split("[,\\s]+");

		for(String id : ids) {
			originalView.invokeOnComponent(facesContext, id.trim(), ADD_TO_PARTIAL_VIEW);
		}
	}
	
	public static final ContextCallback ADD_TO_PARTIAL_VIEW = new ContextCallback() {
		public void invokeContextCallback(FacesContext facesContext, UIComponent component) {
			PartialViewRoot partialViewRoot = (PartialViewRoot) facesContext.getViewRoot();
			
			partialViewRoot.getParents().add(component.getParent());
			component.setParent(partialViewRoot);
			partialViewRoot.getChildren().add(component);
		}
	};
}