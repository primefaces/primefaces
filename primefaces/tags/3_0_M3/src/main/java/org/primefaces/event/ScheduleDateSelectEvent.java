package org.primefaces.event;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

@Deprecated
public class ScheduleDateSelectEvent extends FacesEvent {

	public ScheduleDateSelectEvent(UIComponent component, Date date) {
		super(component);
		this.date = date;
	}

	private Date date;
	
	@Override
	public boolean isAppropriateListener(FacesListener faceslistener) {
		return false;
	}

	@Override
	public void processListener(FacesListener faceslistener) {
		throw new UnsupportedOperationException();
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public PhaseId getPhaseId() {
		return PhaseId.INVOKE_APPLICATION;
	}
}