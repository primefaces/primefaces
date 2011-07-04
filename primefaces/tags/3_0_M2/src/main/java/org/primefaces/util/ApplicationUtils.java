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
package org.primefaces.util;

import java.util.Map;

import javax.faces.context.FacesContext;

public class ApplicationUtils {

	private ApplicationUtils() {}

	public static boolean isMojarra(FacesContext facesContext) {
		return facesContext.getExternalContext().getApplicationMap().containsKey("com.sun.faces.ApplicationAssociate");
	}

	public static String getViewNamespace(FacesContext facesContext) {
		Map<String, Object> appMap = facesContext.getExternalContext().getApplicationMap();
		
		if(appMap.get("primefacesViewNamespace") == null) {
			String namespace = facesContext.getExternalContext().encodeNamespace("");
			appMap.put("primefacesViewNamespace", namespace);
		}
		
		return (String) appMap.get("primefacesViewNamespace");
	}
}
