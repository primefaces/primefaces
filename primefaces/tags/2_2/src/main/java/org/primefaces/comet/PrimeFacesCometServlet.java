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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.DefaultBroadcaster;
import org.primefaces.comet.PrimeFacesCometHandler;

public class PrimeFacesCometServlet extends AtmosphereServlet {

	public PrimeFacesCometServlet() {
		super();
	}
		
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		atmosphereHandlers.put(CometContext.CHANNEL_PATH + "*", new AtmosphereHandlerWrapper(new PrimeFacesCometHandler(), new DefaultBroadcaster()));
	}

	@Override
	protected boolean detectJerseyRuntime(ServletConfig servletConfig) {
		return false;
	}
}
