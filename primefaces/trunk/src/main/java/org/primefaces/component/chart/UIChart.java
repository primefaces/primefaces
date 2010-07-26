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
package org.primefaces.component.chart;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import org.primefaces.event.ItemSelectEvent;

public abstract class UIChart extends UIComponentBase {

	private java.lang.String _widgetVar;
	private java.lang.Object _value;
	private java.lang.String _var;
	private java.lang.String _styleClass;
	private java.lang.String _style;
	private java.lang.Boolean _live;
	private java.lang.Integer _refreshInterval;
	private java.lang.String _update;
	private javax.el.MethodExpression _itemSelectListener;
	private java.lang.String _oncomplete;
	private java.lang.String _wmode;
	private java.lang.String _width;
	private java.lang.String _height;
	private String _dataTipFunction;
	
	public java.lang.String getWidgetVar() {
		if(_widgetVar != null )
			return _widgetVar;

		ValueExpression ve = getValueExpression("widgetVar");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		this._widgetVar = _widgetVar;
	}

	public java.lang.Object getValue() {
		if(_value != null )
			return _value;

		ValueExpression ve = getValueExpression("value");
		return ve != null ? (java.lang.Object) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setValue(java.lang.Object _value) {
		this._value = _value;
	}

	public java.lang.String getVar() {
		if(_var != null )
			return _var;

		ValueExpression ve = getValueExpression("var");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setVar(java.lang.String _var) {
		this._var = _var;
	}

	public java.lang.String getStyleClass() {
		if(_styleClass != null )
			return _styleClass;

		ValueExpression ve = getValueExpression("styleClass");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setStyleClass(java.lang.String _styleClass) {
		this._styleClass = _styleClass;
	}

	public java.lang.String getStyle() {
		if(_style != null )
			return _style;

		ValueExpression ve = getValueExpression("style");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setStyle(java.lang.String _style) {
		this._style = _style;
	}
	
	public java.lang.String getWmode() {
		if(_wmode != null )
			return _wmode;

		ValueExpression ve = getValueExpression("wmode");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setWmode(java.lang.String _wmode) {
		this._wmode = _wmode;
	}
	
	public java.lang.String getWidth() {
		if(_width != null )
			return _width;

		ValueExpression ve = getValueExpression("width");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : "500px";
	}
	public void setWidth(java.lang.String _width) {
		this._width = _width;
	}
	
	public java.lang.String getHeight() {
		if(_height != null )
			return _height;

		ValueExpression ve = getValueExpression("height");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : "350px";
	}
	public void setHeight(java.lang.String _height) {
		this._height = _height;
	}
	
	public boolean isLive() {
		if(_live != null )
			return _live;

		ValueExpression ve = getValueExpression("live");
		return ve != null ? (java.lang.Boolean) ve.getValue(getFacesContext().getELContext())  : false;
	}
	public void setLive(boolean _live) {
		this._live = _live;
	}

	public int getRefreshInterval() {
		if(_refreshInterval != null )
			return _refreshInterval;

		ValueExpression ve = getValueExpression("refreshInterval");
		return ve != null ? (java.lang.Integer) ve.getValue(getFacesContext().getELContext())  : 3000;
	}
	public void setRefreshInterval(int _refreshInterval) {
		this._refreshInterval = _refreshInterval;
	}
	
	public java.lang.String getUpdate() {
		if(_update != null )
			return _update;

		ValueExpression ve = getValueExpression("update");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setUpdate(java.lang.String _update) {
		this._update = _update;
	}

	public javax.el.MethodExpression getItemSelectListener() {
		return this._itemSelectListener;
	}

	public void setItemSelectListener(javax.el.MethodExpression _itemSelectListener) {
		this._itemSelectListener = _itemSelectListener;
	}
	
	public java.lang.String getOncomplete() {
		if(_oncomplete != null )
			return _oncomplete;

		ValueExpression ve = getValueExpression("oncomplete");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setOncomplete(java.lang.String _oncomplete) {
		this._oncomplete = _oncomplete;
	}
	
	public String getDataTipFunction() {
		if(_dataTipFunction != null )
			return _dataTipFunction;

		ValueExpression ve = getValueExpression("dataTipFunction");
		return ve != null ? (java.lang.String) ve.getValue(getFacesContext().getELContext())  : null;
	}
	public void setDataTipFunction(String _dataTipFunction) {
		this._dataTipFunction = _dataTipFunction;
	}
	
	public void broadcast(FacesEvent event) throws AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();

		if(event instanceof ItemSelectEvent) {
			MethodExpression me = getItemSelectListener();
		
			if (me != null) 
				me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}

	public Object saveState(FacesContext context) {
		Object values[] = new Object[15];
		values[0] = super.saveState(context);
		values[1] = _widgetVar;
		values[2] = _value;
		values[3] = _var;
		values[4] = _styleClass;
		values[5] = _style;
		values[6] = _live;
		values[7] = _refreshInterval;
		values[8] = _update;
		values[9] = _itemSelectListener;
		values[10] = _oncomplete;
		values[11] = _wmode;
		values[12] = _width;
		values[13] = _height;
		values[14] = _dataTipFunction;
		
		return ((Object) values);
	}
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_widgetVar = (String) values[1];
		_value = (Object) values[2];
		_var = (String) values[3];
		_styleClass = (String) values[4];
		_style = (String) values[5];
		_live = (Boolean) values[6];
		_refreshInterval = (Integer) values[7];
		_update = (String) values[8];
		_itemSelectListener = (MethodExpression) values[9];
		_oncomplete = (String) values[10];
		_wmode = (String) values[11];
		_width = (String) values[12];
		_height = (String) values[13];
		_dataTipFunction = (String) values[14];
	}
}