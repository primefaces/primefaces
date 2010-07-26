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
package org.primefaces.comet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.primefaces.application.PrimeFacesPhaseListener;

@SuppressWarnings("unchecked")
public class PrimeFacesCometHandler implements AtmosphereHandler {
	
	private final static Logger logger = Logger.getLogger(PrimeFacesCometHandler.class.getName());

	public void onRequest(AtmosphereResource event) throws IOException {
		if(logger.isLoggable(Level.FINE)) {
			logger.fine("Handling comet event");
		}
		
		HttpServletRequest request = (HttpServletRequest) event.getRequest();
		HttpServletResponse response = (HttpServletResponse) event.getResponse();
		
		if(request.getMethod().equals("GET")) {
			response.setContentType("text/html;charset=ISO-8859-1");
			response.addHeader("Cache-Control", "private");
			response.addHeader("Pragma", "no-cache");
			
			event.suspend();
			
			if(logger.isLoggable(Level.FINE))
				logger.log(Level.FINE, "Client:\"{0}\" has subscribed", request.getRemoteAddr());
			
		} else if(request.getMethod().equals("POST")) {
			if(logger.isLoggable(Level.FINE)) {
				logger.fine("Handling publish event request");
			}
			
			Set<AtmosphereResource> channelSubscribers = new HashSet<AtmosphereResource>();
			
			for(Iterator<AtmosphereResource> iterator = event.getBroadcaster().getAtmosphereResources(); iterator.hasNext();) {
				AtmosphereResource resource = iterator.next();
				HttpServletRequest suspendedRequest = (HttpServletRequest) resource.getRequest();
				String subscribedChannel = suspendedRequest.getPathInfo().substring(1);
				String channelToBroadcast = (String) request.getAttribute(CometContext.CHANNEL_NAME);
				
				if(subscribedChannel.equalsIgnoreCase(channelToBroadcast)) {
					channelSubscribers.add(resource);
				}
			}
			
			event.getBroadcaster().broadcast(request.getAttribute(CometContext.PUBLISH_DATA), channelSubscribers);
			
			//Complete forwarded PrimeFaces ajax request
			LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			Lifecycle lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			new PrimeFacesPhaseListener().beforePhase(new PhaseEvent(FacesContext.getCurrentInstance(), PhaseId.RENDER_RESPONSE, lifecycle));
		}
	}

	public void onStateChange(AtmosphereResourceEvent event) throws IOException {
		if(event.getMessage() == null)
			return;
		
		if(event.isCancelled()) {
			if(logger.isLoggable(Level.FINE))
				logger.fine("Ignoring publishing cancelled event");
			
			return;
		}
		
		if(logger.isLoggable(Level.FINE))
			logger.fine("Publishing to subsciber.");

		HttpServletRequest request = (HttpServletRequest) event.getResource().getRequest();
		HttpServletResponse response = ((HttpServletResponse) event.getResource().getResponse());

		String msg = (String) event.getMessage();
		String widget = request.getParameter("widget");
		String script = "<script type=\"text/javascript\">window.parent." + widget + ".handlePublish('" + msg + "');</script>";
			
		response.getWriter().write(script);
		response.getWriter().flush();

		if(logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Publishing to \"{0}\" has subscribed", request.getRemoteAddr());
	}
}