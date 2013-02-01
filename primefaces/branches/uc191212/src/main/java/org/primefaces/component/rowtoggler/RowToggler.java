/*
 * Generated, Do Not Modify
 */
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

@ResourceDependencies({

})
public class RowToggler extends UIComponentBase {


	public static final String COMPONENT_TYPE = "org.primefaces.component.RowToggler";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.RowTogglerRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {
;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public RowToggler() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

/*import javax.faces.component.UIComponent;

    public void processDecodes(FacesContext context) {
        UIComponent expansion = getTable(context).getFacet("expansion");

        if(expansion != null) {
            expansion.processDecodes(context);
        }
    }

    public void processValidators(FacesContext context) {
        UIComponent expansion = getTable(context).getFacet("expansion");

        if(expansion != null) {
            expansion.processValidators(context);
        }
    }

    public void processUpdates(FacesContext context) {
        UIComponent expansion = getTable(context).getFacet("expansion");

        if(expansion != null) {
            expansion.processUpdates(context);
        }
    }

    private DataTable table;

    private DataTable getTable(FacesContext context) {
        if(table == null) {
            table = findParentTable(context);
        }

        return table;
    }

    private DataTable findParentTable(FacesContext context) {
		UIComponent parent = this.getParent();

		while(parent != null) {
			if(parent instanceof DataTable) {
				return (DataTable) parent;
            }

			parent = parent.getParent();
		}

		return null;
	}*/

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	public void handleAttribute(String name, Object value) {
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}