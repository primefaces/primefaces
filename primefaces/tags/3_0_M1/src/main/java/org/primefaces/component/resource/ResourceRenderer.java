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
package org.primefaces.component.resource;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.renderkit.CoreRenderer;

@Deprecated
public class ResourceRenderer extends CoreRenderer {

	private static Logger logger = Logger.getLogger(ResourceRenderer.class.getName());

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		logger.info("Resource component is deprecated, use standard h:outputStylesheet or h:outputScript instead.");
	}
}