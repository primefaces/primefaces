/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.resources;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.renderkit.CoreRenderer;

@Deprecated
public class ResourcesRenderer extends CoreRenderer {

	private static Logger logger = Logger.getLogger(ResourcesRenderer.class.getName());
	
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		logger.info("p:resources component is deprecated and has no use in PrimeFaces 2.0 as JSF 2.0 resource apis are used instead to place resources on page.");
	}
}