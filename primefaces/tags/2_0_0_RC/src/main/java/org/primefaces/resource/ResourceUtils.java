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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

public class ResourceUtils {

	public final static String VERSION_INFO = "/2.0.0.RC";
	public final static String RESOURCE_FOLDER = "/META-INF/resources";
	public final static String RESOURCE_PATTERN = "/primefaces_resource";
	public final static String RESOURCE_VERSION_PATTERN = RESOURCE_PATTERN + VERSION_INFO;
	public final static String CSS_RESOURCE_PATTERN = "primefaces_resource:url:";
	
	public static String getResourceURL(FacesContext facesContext, String resource) {
		String contextPath = facesContext.getExternalContext().getRequestContextPath();
		
		return contextPath + RESOURCE_PATTERN + VERSION_INFO + resource;
	}
	
	public static ResourceHolder getResourceHolder(FacesContext facesContext) {
		ValueExpression ve = facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), "#{primeFacesResourceHolder}", ResourceHolder.class);
		
		return (ResourceHolder) ve.getValue(facesContext.getELContext());
	}
}
