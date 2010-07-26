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
package org.primefaces.resource;

import static org.junit.Assert.*;

import javax.servlet.ServletException;

import org.junit.Test;

public class ResourceServletTest {

	@Test
	public void shouldResolveResourcePath() {
		ResourceServlet rs = new ResourceServlet();
		String version = ResourceUtils.VERSION_INFO;
		String requestURI = "/myapp/primefaces_resource" + version + "/yui/button/button.css";
		
		assertEquals("/yui/button/button.css", rs.getResourcePath(requestURI));
 	}
	
	@Test
	public void shouldResolveResourceFileExtension() {
		ResourceServlet rs = new ResourceServlet();
		String version = ResourceUtils.VERSION_INFO;
		
		assertEquals("css", rs.getResourceFileExtension("/myapp/primefaces_resource" + version + "/yui/button/button.css"));
		assertEquals("js", rs.getResourceFileExtension("/myapp/primefaces_resource" + version + "/yui/button/button.js"));
		assertEquals("png", rs.getResourceFileExtension("/myapp/primefaces_resource" + version + "/yui/calendar/calendar.png"));
 	}
	
	@Test
	public void shouldResolveResponseContentType() throws ServletException{
		ResourceServlet rs = new ResourceServlet();
		rs.init(null);
		String version = ResourceUtils.VERSION_INFO;
		
		assertEquals("text/css", rs.getResourceContentType("/myapp/primefaces_resource" + version + "/yui/button/button.css"));
		assertEquals("text/javascript", rs.getResourceContentType("/myapp/primefaces_resource" + version + "/yui/button/button.js"));
		assertEquals("image/png", rs.getResourceContentType("/myapp/primefaces_resource" + version + "/yui/calendar/calendar.png"));
	}
}