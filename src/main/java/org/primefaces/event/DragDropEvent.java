package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class DragDropEvent extends FacesEvent {

	private String dragId;

	private String dropId;
	
	public DragDropEvent(UIComponent component, String dragId, String dropId) {
		super(component);
		this.dragId = dragId;
		this.dropId = dropId;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return (listener instanceof DropListener);
	}

	@Override
	public void processListener(FacesListener listener) {
		((DropListener) listener).processDragDrop(this);
	}
	
	public String getDragId() {
		return dragId;
	}

	public String getDropId() {
		return dropId;
	}
}