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
package org.primefaces.component.schedule;

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
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TimeZone;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.ScheduleEvent;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="schedule/schedule.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js"),
	@ResourceDependency(library="primefaces", name="schedule/schedule.js")
})
public class Schedule extends UIComponentBase implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.Schedule";
	public static final String COMPONENT_FAMILY = "org.primefaces";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.ScheduleRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,value
		,locale
		,aspectRatio
		,view
		,initialDate
		,showWeekends
		,style
		,styleClass
		,draggable
		,resizable
		,showHeader
		,leftHeaderTemplate
		,centerHeaderTemplate
		,rightHeaderTemplate
		,allDaySlot
		,slotMinutes
		,firstHour
		,minTime
		,maxTime
		,axisFormat
		,timeFormat
		,timeZone;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Schedule() {
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

	public org.primefaces.model.ScheduleModel getValue() {
		return (org.primefaces.model.ScheduleModel) getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(org.primefaces.model.ScheduleModel _value) {
		getStateHelper().put(PropertyKeys.value, _value);
		handleAttribute("value", _value);
	}

	public java.lang.Object getLocale() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.locale, null);
	}
	public void setLocale(java.lang.Object _locale) {
		getStateHelper().put(PropertyKeys.locale, _locale);
		handleAttribute("locale", _locale);
	}

	public double getAspectRatio() {
		return (java.lang.Double) getStateHelper().eval(PropertyKeys.aspectRatio, java.lang.Double.MIN_VALUE);
	}
	public void setAspectRatio(double _aspectRatio) {
		getStateHelper().put(PropertyKeys.aspectRatio, _aspectRatio);
		handleAttribute("aspectRatio", _aspectRatio);
	}

	public java.lang.String getView() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.view, "month");
	}
	public void setView(java.lang.String _view) {
		getStateHelper().put(PropertyKeys.view, _view);
		handleAttribute("view", _view);
	}

	public java.lang.Object getInitialDate() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.initialDate, null);
	}
	public void setInitialDate(java.lang.Object _initialDate) {
		getStateHelper().put(PropertyKeys.initialDate, _initialDate);
		handleAttribute("initialDate", _initialDate);
	}

	public boolean isShowWeekends() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showWeekends, true);
	}
	public void setShowWeekends(boolean _showWeekends) {
		getStateHelper().put(PropertyKeys.showWeekends, _showWeekends);
		handleAttribute("showWeekends", _showWeekends);
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

	public boolean isDraggable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.draggable, true);
	}
	public void setDraggable(boolean _draggable) {
		getStateHelper().put(PropertyKeys.draggable, _draggable);
		handleAttribute("draggable", _draggable);
	}

	public boolean isResizable() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.resizable, true);
	}
	public void setResizable(boolean _resizable) {
		getStateHelper().put(PropertyKeys.resizable, _resizable);
		handleAttribute("resizable", _resizable);
	}

	public boolean isShowHeader() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showHeader, true);
	}
	public void setShowHeader(boolean _showHeader) {
		getStateHelper().put(PropertyKeys.showHeader, _showHeader);
		handleAttribute("showHeader", _showHeader);
	}

	public java.lang.String getLeftHeaderTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.leftHeaderTemplate, "prev,next today");
	}
	public void setLeftHeaderTemplate(java.lang.String _leftHeaderTemplate) {
		getStateHelper().put(PropertyKeys.leftHeaderTemplate, _leftHeaderTemplate);
		handleAttribute("leftHeaderTemplate", _leftHeaderTemplate);
	}

	public java.lang.String getCenterHeaderTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.centerHeaderTemplate, "title");
	}
	public void setCenterHeaderTemplate(java.lang.String _centerHeaderTemplate) {
		getStateHelper().put(PropertyKeys.centerHeaderTemplate, _centerHeaderTemplate);
		handleAttribute("centerHeaderTemplate", _centerHeaderTemplate);
	}

	public java.lang.String getRightHeaderTemplate() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.rightHeaderTemplate, "month,agendaWeek,agendaDay");
	}
	public void setRightHeaderTemplate(java.lang.String _rightHeaderTemplate) {
		getStateHelper().put(PropertyKeys.rightHeaderTemplate, _rightHeaderTemplate);
		handleAttribute("rightHeaderTemplate", _rightHeaderTemplate);
	}

	public boolean isAllDaySlot() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.allDaySlot, true);
	}
	public void setAllDaySlot(boolean _allDaySlot) {
		getStateHelper().put(PropertyKeys.allDaySlot, _allDaySlot);
		handleAttribute("allDaySlot", _allDaySlot);
	}

	public int getSlotMinutes() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.slotMinutes, 30);
	}
	public void setSlotMinutes(int _slotMinutes) {
		getStateHelper().put(PropertyKeys.slotMinutes, _slotMinutes);
		handleAttribute("slotMinutes", _slotMinutes);
	}

	public int getFirstHour() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.firstHour, 6);
	}
	public void setFirstHour(int _firstHour) {
		getStateHelper().put(PropertyKeys.firstHour, _firstHour);
		handleAttribute("firstHour", _firstHour);
	}

	public java.lang.String getMinTime() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.minTime, null);
	}
	public void setMinTime(java.lang.String _minTime) {
		getStateHelper().put(PropertyKeys.minTime, _minTime);
		handleAttribute("minTime", _minTime);
	}

	public java.lang.String getMaxTime() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.maxTime, null);
	}
	public void setMaxTime(java.lang.String _maxTime) {
		getStateHelper().put(PropertyKeys.maxTime, _maxTime);
		handleAttribute("maxTime", _maxTime);
	}

	public java.lang.String getAxisFormat() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.axisFormat, null);
	}
	public void setAxisFormat(java.lang.String _axisFormat) {
		getStateHelper().put(PropertyKeys.axisFormat, _axisFormat);
		handleAttribute("axisFormat", _axisFormat);
	}

	public java.lang.String getTimeFormat() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.timeFormat, null);
	}
	public void setTimeFormat(java.lang.String _timeFormat) {
		getStateHelper().put(PropertyKeys.timeFormat, _timeFormat);
		handleAttribute("timeFormat", _timeFormat);
	}

	public java.lang.Object getTimeZone() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.timeZone, null);
	}
	public void setTimeZone(java.lang.Object _timeZone) {
		getStateHelper().put(PropertyKeys.timeZone, _timeZone);
		handleAttribute("timeZone", _timeZone);
	}


    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("dateSelect","eventSelect", "eventMove", "eventResize"));

	private java.util.Locale appropriateLocale;
	
	java.util.Locale calculateLocale(FacesContext facesContext) {
		if(appropriateLocale == null) {
			Object userLocale = getLocale();
			if(userLocale != null) {
				if(userLocale instanceof String)
					appropriateLocale = new java.util.Locale((String) userLocale, "");
				else if(userLocale instanceof java.util.Locale)
					appropriateLocale = (java.util.Locale) userLocale;
				else
					throw new IllegalArgumentException("Type:" + userLocale.getClass() + " is not a valid locale type for calendar:" + this.getClientId(facesContext));
			} else {
				appropriateLocale = facesContext.getViewRoot().getLocale();
			}
		}
		
		return appropriateLocale;
	}

    private TimeZone appropriateTimeZone;

    public java.util.TimeZone calculateTimeZone() {
		if(appropriateTimeZone == null) {
			Object usertimeZone = getTimeZone();
			if(usertimeZone != null) {
				if(usertimeZone instanceof String)
					appropriateTimeZone =  TimeZone.getTimeZone((String) usertimeZone);
				else if(usertimeZone instanceof java.util.TimeZone)
					appropriateTimeZone = (TimeZone) usertimeZone;
				else
					throw new IllegalArgumentException("TimeZone could be either String or java.util.TimeZone");
			} else {
				appropriateTimeZone = TimeZone.getDefault();
			}
		}
		
		return appropriateTimeZone;
	}

	@Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = this.getClientId(context);
        TimeZone tz = calculateTimeZone();

        if(isSelfRequest(context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if(eventName.equals("dateSelect")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(params.get(clientId + "_selectedDate")));
                calendar.setTimeZone(tz);
                Date selectedDate = calendar.getTime();
                DateSelectEvent dateSelectEvent = new DateSelectEvent(this, behaviorEvent.getBehavior(), selectedDate);
                dateSelectEvent.setPhaseId(behaviorEvent.getPhaseId());

                wrapperEvent = dateSelectEvent;
            }
            else if(eventName.equals("eventSelect")) {
                String selectedEventId = params.get(clientId + "_selectedEventId");
				ScheduleEvent selectedEvent = this.getValue().getEvent(selectedEventId);

                wrapperEvent = new ScheduleEntrySelectEvent(this, behaviorEvent.getBehavior(), selectedEvent);
            }
            else if(eventName.equals("eventMove")) {
                String movedEventId = params.get(clientId + "_movedEventId");
				ScheduleEvent movedEvent = this.getValue().getEvent(movedEventId);
                int dayDelta = Integer.valueOf(params.get(clientId + "_dayDelta"));
				int minuteDelta = Integer.valueOf(params.get(clientId + "_minuteDelta"));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(movedEvent.getStartDate());
                calendar.setTimeZone(tz);
                calendar.add(Calendar.DATE, dayDelta);
                calendar.add(Calendar.MINUTE, minuteDelta);
                movedEvent.getStartDate().setTime(calendar.getTimeInMillis());

                calendar = Calendar.getInstance();
                calendar.setTime(movedEvent.getEndDate());
                calendar.setTimeZone(tz);
                calendar.add(Calendar.DATE, dayDelta);
                calendar.add(Calendar.MINUTE, minuteDelta);
				movedEvent.getEndDate().setTime(calendar.getTimeInMillis());

                wrapperEvent = new ScheduleEntryMoveEvent(this, behaviorEvent.getBehavior(), movedEvent, dayDelta, minuteDelta);
            }
            else if(eventName.equals("eventResize")) {
                String resizedEventId = params.get(clientId + "_resizedEventId");
				ScheduleEvent resizedEvent = this.getValue().getEvent(resizedEventId);
                int dayDelta = Integer.valueOf(params.get(clientId + "_dayDelta"));
				int minuteDelta = Integer.valueOf(params.get(clientId + "_minuteDelta"));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(resizedEvent.getEndDate());
                calendar.setTimeZone(tz);
				calendar.add(Calendar.DATE, dayDelta);
                calendar.add(Calendar.MINUTE, minuteDelta);
				resizedEvent.getEndDate().setTime(calendar.getTimeInMillis());

                wrapperEvent = new ScheduleEntryResizeEvent(this, behaviorEvent.getBehavior(), resizedEvent, dayDelta, minuteDelta);
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
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