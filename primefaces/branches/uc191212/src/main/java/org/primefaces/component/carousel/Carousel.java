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
package org.primefaces.component.carousel;

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
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class Carousel extends UIData implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Carousel";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.CarouselRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,circular
		,vertical
		,autoPlayInterval
		,pageLinks
		,effect
		,easing
		,effectDuration
		,dropdownTemplate
		,style
		,styleClass
		,itemStyle
		,itemStyleClass
		,headerText
		,footerText;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Carousel() {
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

	public boolean isCircular() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.circular, false);
	}
	public void setCircular(boolean _circular) {
		getStateHelper().put(PropertyKeys.circular, _circular);
		handleAttribute("circular", _circular);
	}

	public boolean isVertical() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.vertical, false);
	}
	public void setVertical(boolean _vertical) {
		getStateHelper().put(PropertyKeys.vertical, _vertical);
		handleAttribute("vertical", _vertical);
	}

	public int getAutoPlayInterval() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.autoPlayInterval, 0);
	}
	public void setAutoPlayInterval(int _autoPlayInterval) {
		getStateHelper().put(PropertyKeys.autoPlayInterval, _autoPlayInterval);
		handleAttribute("autoPlayInterval", _autoPlayInterval);
	}

	public int getPageLinks() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.pageLinks, 3);
	}
	public void setPageLinks(int _pageLinks) {
		getStateHelper().put(PropertyKeys.pageLinks, _pageLinks);
		handleAttribute("pageLinks", _pageLinks);
	}

	public java.lang.String getEffect() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, null);
	}
	public void setEffect(java.lang.String _effect) {
		getStateHelper().put(PropertyKeys.effect, _effect);
		handleAttribute("effect", _effect);
	}

	public java.lang.String getEasing() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.easing, null);
	}
	public void setEasing(java.lang.String _easing) {
		getStateHelper().put(PropertyKeys.easing, _easing);
		handleAttribute("easing", _easing);
	}

	public int getEffectDuration() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.effectDuration, java.lang.Integer.MIN_VALUE);
	}
	public void setEffectDuration(int _effectDuration) {
		getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
		handleAttribute("effectDuration", _effectDuration);
	}

	public java.lang.String getDropdownTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dropdownTemplate, "{page}");
	}
	public void setDropdownTemplate(java.lang.String _dropdownTemplate) {
		getStateHelper().put(PropertyKeys.dropdownTemplate, _dropdownTemplate);
		handleAttribute("dropdownTemplate", _dropdownTemplate);
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

	public java.lang.String getItemStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemStyle, null);
	}
	public void setItemStyle(java.lang.String _itemStyle) {
		getStateHelper().put(PropertyKeys.itemStyle, _itemStyle);
		handleAttribute("itemStyle", _itemStyle);
	}

	public java.lang.String getItemStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.itemStyleClass, null);
	}
	public void setItemStyleClass(java.lang.String _itemStyleClass) {
		getStateHelper().put(PropertyKeys.itemStyleClass, _itemStyleClass);
		handleAttribute("itemStyleClass", _itemStyleClass);
	}

	public java.lang.String getHeaderText() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.headerText, null);
	}
	public void setHeaderText(java.lang.String _headerText) {
		getStateHelper().put(PropertyKeys.headerText, _headerText);
		handleAttribute("headerText", _headerText);
	}

	public java.lang.String getFooterText() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.footerText, null);
	}
	public void setFooterText(java.lang.String _footerText) {
		getStateHelper().put(PropertyKeys.footerText, _footerText);
		handleAttribute("footerText", _footerText);
	}


    public final static String CONTAINER_CLASS = "ui-carousel ui-widget ui-widget-content ui-corner-all";
    public final static String ITEM_CLASS = "ui-carousel-item ui-widget-content ui-corner-all";
    public final static String HEADER_CLASS = "ui-carousel-header ui-widget-header ui-corner-all";
    public final static String HEADER_TITLE_CLASS = "ui-carousel-header-title";
    public final static String FOOTER_CLASS = "ui-carousel-footer ui-widget-header ui-corner-all";
    public final static String HORIZONTAL_NEXT_BUTTON = "ui-carousel-button ui-carousel-next-button ui-icon ui-icon-circle-triangle-e";
    public final static String HORIZONTAL_PREV_BUTTON = "ui-carousel-button ui-carousel-prev-button ui-icon ui-icon-circle-triangle-w";
    public final static String VERTICAL_NEXT_BUTTON = "ui-carousel-button ui-carousel-next-button ui-icon ui-icon-circle-triangle-s";
    public final static String VERTICAL_PREV_BUTTON = "ui-carousel-button ui-carousel-prev-button ui-icon ui-icon-circle-triangle-n";
    public final static String VIEWPORT_CLASS = "ui-carousel-viewport";
    public final static String VERTICAL_VIEWPORT_CLASS = "ui-carousel-viewport ui-carousel-vertical-viewport";
    public final static String PAGE_LINKS_CONTAINER_CLASS = "ui-carousel-page-links";
    public final static String PAGE_LINK_CLASS = "ui-icon ui-carousel-page-link ui-icon-radio-off";
    public final static String DROPDOWN_CLASS = "ui-carousel-dropdown ui-widget ui-state-default ui-corner-left";

    public int getRenderedChildCount() {
        int i = 0;
    
    for(UIComponent child : getChildren()) {
            if(child.isRendered()) {
                i++;
            }
        }

        return i;
    }

    public int getFirstVisible() {
		return (java.lang.Integer) getStateHelper().eval("firsVisible", 0);
	}
	public void setFirstVisible(int _firstVisible) {
		getStateHelper().put("firsVisible", _firstVisible);
	}

    @Override
    public void processDecodes(FacesContext facesContext) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        super.processDecodes(facesContext);
        this.setRows(rows);
    }
	
    @Override
	public void processValidators(FacesContext facesContext) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        super.processValidators(facesContext);
        this.setRows(rows);
    }
	
    @Override
	public void processUpdates(FacesContext facesContext) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        super.processUpdates(facesContext);
        this.setRows(rows);
	}

    @Override
    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows) {
        int rows = this.getRows();
		this.setRows(this.getRowCount());
        boolean retVal = super.visitRows(context, callback, visitRows);
        this.setRows(rows);

        return retVal;
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