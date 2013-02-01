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
package org.primefaces.component.sheet;

import org.primefaces.component.api.UIData;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.component.column.Column;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="sheet/sheet.css"),
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="sheet/sheet.js")
})
public class Sheet extends UIData implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.Sheet";
	public static final String COMPONENT_FAMILY = "org.primefaces";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.SheetRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,scrollHeight
		,scrollWidth
		,style
		,styleClass;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Sheet() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
		handleAttribute("widgetVar", _widgetVar);
	}

	public int getScrollHeight() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollHeight, java.lang.Integer.MIN_VALUE);
	}
	public void setScrollHeight(int _scrollHeight) {
		getStateHelper().put(PropertyKeys.scrollHeight, _scrollHeight);
		handleAttribute("scrollHeight", _scrollHeight);
	}

	public int getScrollWidth() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.scrollWidth, java.lang.Integer.MIN_VALUE);
	}
	public void setScrollWidth(int _scrollWidth) {
		getStateHelper().put(PropertyKeys.scrollWidth, _scrollWidth);
		handleAttribute("scrollWidth", _scrollWidth);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
		handleAttribute("style", _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
		handleAttribute("styleClass", _styleClass);
	}



    public final static String CONTAINER_CLASS = "ui-sheet ui-widget";
    public static final String CAPTION_CLASS = "ui-widget-header ui-sheet-caption ui-corner-tl ui-corner-tr";
    public static final String HEADER_CLASS = "ui-widget-header ui-sheet-header";
    public static final String HEADER_BOX_CLASS = "ui-sheet-header-box";
    public static final String BODY_CLASS = "ui-sheet-body";
    public static final String CELL_CLASS = "ui-sh-c";
    public static final String ROW_CLASS = "ui-widget-content";
    public static final String COLUMN_HEADER_CLASS = "ui-state-default";
    public static final String CELL_DISPLAY_CLASS = "ui-sh-c-d";
    public static final String CELL_EDIT_CLASS = "ui-sh-c-e";
    public static final String INDEX_CELL_CLASS = "ui-sheet-index-cell ui-sh-c";
    public static final String EDITOR_BAR_CLASS = "ui-sheet-editor-bar ui-widget-header";
    public static final String CELL_INFO_CLASS = "ui-sheet-cell-info";
    public static final String EDITOR_CLASS = "ui-sheet-editor";
    public static final String SORTABLE_COLUMN = "ui-sortable-column"; 
    public static final String SORTABLE_COLUMN_ICON = "ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";    

    public static final String[] LETTERS = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public boolean isSortingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_sorting");
    }

    /*@Override
    public void processDecodes(FacesContext context) {
        if(isSortingRequest(context)) {
            this.decode(context);
            context.renderResponse();
        }
        else {
            super.processDecodes(context);
        }
	}*/

    public List<Column> columns;
    public Column findColumn(String clientId) {
        for(Column column : getColumns()) {
            if(column.getClientId().equals(clientId)) {
                return column;
            }
        }
        
        return null;
    }

    public List<Column> getColumns() {        
        if(columns == null) {
            columns = new ArrayList<Column>();

            for(UIComponent child : this.getChildren()) {
                if(child.isRendered() && child instanceof Column) {
                    columns.add((Column) child);
                }
            }
        }

        return columns;
    }

    public boolean isColResizeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_colResize");
    }

    public void syncColumnWidths() {
        Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String clientId = this.getClientId();
        
        String columnId = params.get(clientId + "_columnId");
        String width = params.get(clientId + "_width");
        Column column = findColumn(columnId);
        
        column.setWidth(Integer.parseInt(width));
    }

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null)
			return userWidgetVar;
		 else
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
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