/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.event.diagram;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.event.FacesListener;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.EndPoint;

public class ConnectionChangeEvent extends AjaxBehaviorEvent {
    
    private Element originalSourceElement;
    private Element newSourceElement;
    private Element originalTargetElement;
    private Element newTargetElement;
    private EndPoint originalSourceEndPoint;
    private EndPoint newSourceEndPoint;
    private EndPoint originalTargetEndPoint;
    private EndPoint newTargetEndPoint;

    public ConnectionChangeEvent(UIComponent component, Behavior behavior, Element originalSourceElement, Element newSourceElement, Element originalTargetElement, Element newTargetElement, EndPoint originalSourceEndPoint, EndPoint newSourceEndPoint, EndPoint originalTargetEndPoint, EndPoint newTargetEndPoint) {
        super(component, behavior);
        this.originalSourceElement = originalSourceElement;
        this.newSourceElement = newSourceElement;
        this.originalTargetElement = originalTargetElement;
        this.newTargetElement = newTargetElement;
        this.originalSourceEndPoint = originalSourceEndPoint;
        this.newSourceEndPoint = newSourceEndPoint;
        this.originalTargetEndPoint = originalTargetEndPoint;
        this.newTargetEndPoint = newTargetEndPoint;
    }
    
    @Override
	public boolean isAppropriateListener(FacesListener faceslistener) {
		return (faceslistener instanceof AjaxBehaviorListener);
	}

	@Override
	public void processListener(FacesListener faceslistener) {
		((AjaxBehaviorListener) faceslistener).processAjaxBehavior(this);
	}

    public Element getOriginalSourceElement() {
        return originalSourceElement;
    }

    public Element getNewSourceElement() {
        return newSourceElement;
    }

    public Element getOriginalTargetElement() {
        return originalTargetElement;
    }

    public Element getNewTargetElement() {
        return newTargetElement;
    }

    public EndPoint getOriginalSourceEndPoint() {
        return originalSourceEndPoint;
    }

    public EndPoint getNewSourceEndPoint() {
        return newSourceEndPoint;
    }

    public EndPoint getOriginalTargetEndPoint() {
        return originalTargetEndPoint;
    }

    public EndPoint getNewTargetEndPoint() {
        return newTargetEndPoint;
    }
}
