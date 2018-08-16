/*
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.graphicimage;

import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;


public abstract class GraphicImageBase extends HtmlGraphicImage {


	public static final String COMPONENT_TYPE = "org.primefaces.component.GraphicImage";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.GraphicImageRenderer";

	public enum PropertyKeys {

		cache
		,name
		,library
		,stream;
	}

	public GraphicImageBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public boolean isCache() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
	}
	public void setCache(boolean _cache) {
		getStateHelper().put(PropertyKeys.cache, _cache);
	}

	public java.lang.String getName() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.name, null);
	}
	public void setName(java.lang.String _name) {
		getStateHelper().put(PropertyKeys.name, _name);
	}

	public java.lang.String getLibrary() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.library, null);
	}
	public void setLibrary(java.lang.String _library) {
		getStateHelper().put(PropertyKeys.library, _library);
	}

	public boolean isStream() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.stream, true);
	}
	public void setStream(boolean _stream) {
		getStateHelper().put(PropertyKeys.stream, _stream);
	}

}