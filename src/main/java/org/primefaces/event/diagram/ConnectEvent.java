/**
 * Copyright 2009-2018 PrimeTek.
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
import org.primefaces.event.AbstractAjaxBehaviorEvent;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.EndPoint;

public class ConnectEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private final Element sourceElement;
    private final Element targetElement;
    private final EndPoint sourceEndPoint;
    private final EndPoint targetEndPoint;

    public ConnectEvent(UIComponent component, Behavior behavior, Element sourceElement, Element targetElement,
            EndPoint sourceEndPoint, EndPoint targetEndPoint) {
        super(component, behavior);
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
        this.sourceEndPoint = sourceEndPoint;
        this.targetEndPoint = targetEndPoint;
    }

    public Element getSourceElement() {
        return sourceElement;
    }

    public Element getTargetElement() {
        return targetElement;
    }

    public EndPoint getSourceEndPoint() {
        return sourceEndPoint;
    }

    public EndPoint getTargetEndPoint() {
        return targetEndPoint;
    }
}
