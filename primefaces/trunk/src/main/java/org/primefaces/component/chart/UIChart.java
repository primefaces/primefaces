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
package org.primefaces.component.chart;

import javax.el.MethodExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.ChartModel;

public abstract class UIChart extends UIComponentBase {

    protected enum PropertyKeys {
        widgetVar
		,value
		,var
        ,styleClass
        ,style
        ,live
        ,refreshInterval
        ,update
        ,itemSelectListener
        ,oncomplete
        ,wmode
        ,title
        ,legendPosition
        ,dataTipFunction
        ,model;

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

	public Object getValue() {
		return getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(Object _value) {
		getStateHelper().put(PropertyKeys.value, _value);
	}

	public String getVar() {
		return (String) getStateHelper().eval(PropertyKeys.var, null);
	}
	public void setVar(String _var) {
		getStateHelper().put(PropertyKeys.var, _var);
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
	
	public String getWmode() {
		return (String) getStateHelper().eval(PropertyKeys.wmode, null);
	}
	public void setWmode(String _wmode) {
		getStateHelper().put(PropertyKeys.wmode, _wmode);
	}

    public String getTitle() {
		return (String) getStateHelper().eval(PropertyKeys.title, "");
	}
	public void setTitle(String _title) {
		getStateHelper().put(PropertyKeys.title, _title);
	}

    public String getLegendPosition() {
		return (String) getStateHelper().eval(PropertyKeys.legendPosition, null);
	}
	public void setLegendPosition(String _legendPosition) {
		getStateHelper().put(PropertyKeys.legendPosition, _legendPosition);
	}
	
	public boolean isLive() {
		return (Boolean) getStateHelper().eval(PropertyKeys.live, false);
	}
	public void setLive(boolean _live) {
		getStateHelper().put(PropertyKeys.live, _live);
	}

	public int getRefreshInterval() {
		return (Integer) getStateHelper().eval(PropertyKeys.refreshInterval, 3000);
	}
	public void setRefreshInterval(int _refreshInterval) {
		getStateHelper().put(PropertyKeys.refreshInterval, _refreshInterval);
	}
	
	public String getUpdate() {
		return (String) getStateHelper().eval(PropertyKeys.update, null);
	}
	public void setUpdate(java.lang.String _update) {
		getStateHelper().put(PropertyKeys.update, _update);
	}

	public MethodExpression getItemSelectListener() {
		return (MethodExpression) getStateHelper().eval(PropertyKeys.itemSelectListener, null);
	}

	public void setItemSelectListener(MethodExpression _itemSelectListener) {
		getStateHelper().put(PropertyKeys.itemSelectListener, _itemSelectListener);
	}
	
	public String getOncomplete() {
		return (String) getStateHelper().eval(PropertyKeys.oncomplete, null);
	}
	public void setOncomplete(java.lang.String _oncomplete) {
		getStateHelper().put(PropertyKeys.oncomplete, _oncomplete);
	}
	
	public String getDataTipFunction() {
		return (String) getStateHelper().eval(PropertyKeys.dataTipFunction, null);
	}
	public void setDataTipFunction(String _dataTipFunction) {
		getStateHelper().put(PropertyKeys.dataTipFunction, _dataTipFunction);
	}

    public boolean isLiveDataRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_dataPoll");
    }

    public ChartModel getModel() {
		return (ChartModel) getStateHelper().eval(PropertyKeys.model, null);
	}
	public void setModel(ChartModel _value) {
		getStateHelper().put(PropertyKeys.model, _value);
	}

    @Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if(event instanceof ItemSelectEvent) {
			MethodExpression me = getItemSelectListener();
		
			if (me != null) 
				me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}

    public boolean hasModel() {
        return getValueExpression("model") != null;
    }
}