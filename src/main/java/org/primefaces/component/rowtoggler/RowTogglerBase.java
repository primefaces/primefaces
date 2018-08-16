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
package org.primefaces.component.rowtoggler;

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
import org.primefaces.component.datatable.DataTable;


public abstract class RowTogglerBase extends UIComponentBase {


	public static final String COMPONENT_TYPE = "org.primefaces.component.RowToggler";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.RowTogglerRenderer";

	public enum PropertyKeys {

		expandLabel
		,collapseLabel
		,tabindex;
	}

	public RowTogglerBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getExpandLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.expandLabel, null);
	}
	public void setExpandLabel(java.lang.String _expandLabel) {
		getStateHelper().put(PropertyKeys.expandLabel, _expandLabel);
	}

	public java.lang.String getCollapseLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.collapseLabel, null);
	}
	public void setCollapseLabel(java.lang.String _collapseLabel) {
		getStateHelper().put(PropertyKeys.collapseLabel, _collapseLabel);
	}

	public java.lang.String getTabindex() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.tabindex, "0");
	}
	public void setTabindex(java.lang.String _tabindex) {
		getStateHelper().put(PropertyKeys.tabindex, _tabindex);
	}

}