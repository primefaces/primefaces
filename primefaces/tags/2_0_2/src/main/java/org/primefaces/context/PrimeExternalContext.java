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

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.primefaces.application.PrimeFacesPhaseListener;
import org.primefaces.context.RequestContext;

public class PrimeExternalContext extends ExternalContextWrapper {

	private ExternalContext delegate;
		
	public PrimeExternalContext(ExternalContext delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void redirect(String url) throws IOException {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		
		if(requestContext.isAjaxRequest()) {			
			LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			PrimeFacesPhaseListener listener = new PrimeFacesPhaseListener();
			PhaseEvent phaseEvent = new PhaseEvent(FacesContext.getCurrentInstance(), PhaseId.RENDER_RESPONSE, lifecycle);

			requestContext.setAjaxRedirectUrl(url);
			
			listener.beforePhase(phaseEvent);	//send ajax redirect request
			
			RequestContext.getCurrentInstance().release();
		} else {
			RequestContext.getCurrentInstance().release();
			super.redirect(url);
		}
	}

	@Override
	public ExternalContext getWrapped() {
		return delegate;
	}
}