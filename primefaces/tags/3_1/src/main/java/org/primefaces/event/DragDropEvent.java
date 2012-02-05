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
package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.event.FacesListener;

public class DragDropEvent extends AjaxBehaviorEvent {

	private String dragId;

	private String dropId;

    private Object data;
	
	public DragDropEvent(UIComponent component, Behavior behavior, String dragId, String dropId) {
		super(component, behavior);
		this.dragId = dragId;
		this.dropId = dropId;
	}

    public DragDropEvent(UIComponent component, Behavior behavior, String dragId, String dropId, Object data) {
		super(component, behavior);
		this.dragId = dragId;
		this.dropId = dropId;
        this.data = data;
	}

	@Override
	public boolean isAppropriateListener(FacesListener faceslistener) {
        return (faceslistener instanceof AjaxBehaviorListener);
	}

	@Override
	public void processListener(FacesListener faceslistener) {
		((AjaxBehaviorListener) faceslistener).processAjaxBehavior(this);
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