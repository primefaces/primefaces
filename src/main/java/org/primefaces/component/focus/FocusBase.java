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
package org.primefaces.component.focus;

import javax.faces.component.UIComponentBase;
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


public abstract class FocusBase extends UIComponentBase {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Focus";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.FocusRenderer";

	public enum PropertyKeys {

		forValue("for")
		,context
		,minSeverity;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public FocusBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getFor() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.forValue, null);
	}
	public void setFor(java.lang.String _for) {
		getStateHelper().put(PropertyKeys.forValue, _for);
	}

	public java.lang.String getContext() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.context, null);
	}
	public void setContext(java.lang.String _context) {
		getStateHelper().put(PropertyKeys.context, _context);
	}

	public java.lang.String getMinSeverity() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.minSeverity, "error");
	}
	public void setMinSeverity(java.lang.String _minSeverity) {
		getStateHelper().put(PropertyKeys.minSeverity, _minSeverity);
	}

}