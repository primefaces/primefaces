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
package org.primefaces.component.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.faces.component.UIOutput;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.ItemSelectEvent;

public abstract class UIChart extends UIOutput implements ClientBehaviorHolder {

    private final static String DEFAULT_EVENT = "itemSelect";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

    protected enum PropertyKeys {
        widgetVar
        ,styleClass
        ,style
        ,title
        ,legendPosition
        ,legendCols
        ,legendRows
        ,shadow
        ,xaxisLabel
        ,yaxisLabel
        ,xaxisAngle
        ,yaxisAngle
        ,seriesColors
        ,extender;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

        @Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
        }
    }
	
	public String getWidgetVar() {
		return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
	}
	
    public String getTitle() {
		return (String) getStateHelper().eval(PropertyKeys.title, "");
	}
	public void setTitle(String _title) {
		getStateHelper().put(PropertyKeys.title, _title);
	}
        
    public String getXaxisLabel() {
		return (String) getStateHelper().eval(PropertyKeys.xaxisLabel, null);
	}
	public void setXaxisLabel(String _xLabel) {
		getStateHelper().put(PropertyKeys.xaxisLabel, _xLabel);
	}
        
    public String getYaxisLabel() {
		return (String) getStateHelper().eval(PropertyKeys.yaxisLabel, null);
	}
	public void setYaxisLabel(String _yLabel) {
		getStateHelper().put(PropertyKeys.yaxisLabel, _yLabel);
	}
        
    public Integer getYaxisAngle() {
		return (Integer) getStateHelper().eval(PropertyKeys.yaxisAngle, 0);
	}
	public void setYaxisAngle(Integer _yAngle) {
		getStateHelper().put(PropertyKeys.yaxisAngle, _yAngle);
	}
        
    public Integer getXaxisAngle() {
		return (Integer) getStateHelper().eval(PropertyKeys.xaxisAngle, 0);
	}
	public void setXaxisAngle(Integer _xAngle) {
		getStateHelper().put(PropertyKeys.xaxisAngle, _xAngle);
	}

    public String getLegendPosition() {
		return (String) getStateHelper().eval(PropertyKeys.legendPosition, null);
	}
	public void setLegendPosition(String _legendPosition) {
		getStateHelper().put(PropertyKeys.legendPosition, _legendPosition);
	}
    
	public boolean isShadow() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.shadow, true);
	}
	public void setShadow(boolean _shadow) {
		getStateHelper().put(PropertyKeys.shadow, _shadow);
	}
    
    public java.lang.String getSeriesColors() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.seriesColors, null);
	}
	public void setSeriesColors(java.lang.String _seriesColors) {
		getStateHelper().put(PropertyKeys.seriesColors, _seriesColors);
	}
    
    public Integer getLegendCols() {
		return (Integer) getStateHelper().eval(PropertyKeys.legendCols, 0);
	}
	public void setLegendCols(Integer _legendCols) {
		getStateHelper().put(PropertyKeys.legendCols, _legendCols);
	}
    
    public Integer getLegendRows() {
		return (Integer) getStateHelper().eval(PropertyKeys.legendRows, 0);
	}
	public void setLegendRows(Integer _legendRows) {
		getStateHelper().put(PropertyKeys.legendRows, _legendRows);
	}
    
    public java.lang.String getExtender() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.extender, null);
	}
	public void setExtender(java.lang.String _extender) {
		getStateHelper().put(PropertyKeys.extender, _extender);
	}
    
    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }
    
    @Override
    public void queueEvent(FacesEvent event) {
        if(event instanceof AjaxBehaviorEvent) {
            BehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String,String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            int itemIndex = Integer.parseInt(map.get("itemIndex"));
            int seriesIndex = Integer.parseInt(map.get("seriesIndex"));

            ItemSelectEvent itemSelectEvent = new ItemSelectEvent(this, behaviorEvent.getBehavior(), itemIndex, seriesIndex);

            super.queueEvent(itemSelectEvent);
        }
    }
}