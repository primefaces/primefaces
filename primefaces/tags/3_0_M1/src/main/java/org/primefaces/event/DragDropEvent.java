package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class DragDropEvent extends FacesEvent {

	private String dragId;

	private String dropId;

    private Object data;
	
	public DragDropEvent(UIComponent component, String dragId, String dropId) {
		super(component);
		this.dragId = dragId;
		this.dropId = dropId;
	}

    public DragDropEvent(UIComponent component, String dragId, String dropId, Object data) {
		super(component);
		this.dragId = dragId;
		this.dropId = dropId;
        this.data = data;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return false;
	}

	@Override
	public void processListener(FacesListener listener) {
		throw new UnsupportedOperationException();
	}
	
	public String getDragId() {
		return dragId;
	}

	public String getDropId() {
		return dropId;
	}

    public Object getData() {
        return data;
    }

}