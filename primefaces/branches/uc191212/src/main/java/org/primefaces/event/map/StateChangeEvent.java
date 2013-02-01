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
package org.primefaces.event.map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.event.FacesListener;

import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;

public class StateChangeEvent extends AjaxBehaviorEvent {

	private LatLngBounds bounds;
	
	private int zoomLevel;

	private LatLng center;

	public StateChangeEvent(UIComponent component, Behavior behavior, LatLngBounds bounds, int zoomLevel, LatLng center) {
		super(component, behavior);
		this.bounds = bounds;
		this.zoomLevel = zoomLevel;
        this.center = center;
	}

	@Override
	public boolean isAppropriateListener(FacesListener faceslistener) {
		return (faceslistener instanceof AjaxBehaviorListener);
	}

	@Override
	public void processListener(FacesListener faceslistener) {
		((AjaxBehaviorListener) faceslistener).processAjaxBehavior(this);
	}

	public LatLngBounds getBounds() {
		return bounds;
	}
	
	public int getZoomLevel() {
		return zoomLevel;
	}
        
        public LatLng getCenter() {
            return center;
        }

        public void setCenter(LatLng center) {
            this.center = center;
        }
}
