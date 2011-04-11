package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

import org.primefaces.model.ScheduleEvent;

public class ScheduleEntrySelectEvent extends FacesEvent {

	private ScheduleEvent scheduleEvent;
	
	public ScheduleEntrySelectEvent(UIComponent uiComponent, ScheduleEvent scheduleEvent) {
		super(uiComponent);
		this.scheduleEvent = scheduleEvent;
	}

	@Override
	public boolean isAppropriateListener(FacesListener faceslistener) {
		return false;
	}

	@Override
	public void processListener(FacesListener faceslistener) {
		throw new UnsupportedOperationException();
	}
	
	public ScheduleEvent getScheduleEvent() {
		return scheduleEvent;
	}
}