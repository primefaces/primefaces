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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CometContext {
	
	public final static String CHANNEL_PATH = "/primefaces_comet/";
	public final static String PUBLISH_DATA = "primefacesPushEventMessage";
	public final static String CHANNEL_NAME = "primefacesCometChannel";
	
	private final static Logger logger = Logger.getLogger(CometContext.class.getName());
	
	private CometContext() {}

	public static void publish(String channel, Object object) {	
		if(object == null) {
			throw new IllegalArgumentException("Publish data cannot be null");
		}
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		
		request.setAttribute(CHANNEL_NAME, channel);
		request.setAttribute(PUBLISH_DATA, object);
		
		try {
			request.getRequestDispatcher(CHANNEL_PATH + channel).forward(request, response);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
 }