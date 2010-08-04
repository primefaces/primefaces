/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.resource;

import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@Deprecated
public class ResourceServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(ResourceServlet.class.getName());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		logger.info("ResourceServlet is deprecated and has no use in PrimeFaces 2.2+ as native JSF 2.0 resource APIs are used instead to load resources on page.");
	}
}