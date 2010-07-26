package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class IdleEvent extends FacesEvent {

	public IdleEvent(UIComponent component) {
		super(component);
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return (listener instanceof IdleEventListener);
	}

	@Override
	public void processListener(FacesListener listener) {
		((IdleEventListener) listener).processIdleEvent(this);
	}

}
