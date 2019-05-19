/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.event.diagram;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.EndPoint;

public class ConnectionChangeEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private Element originalSourceElement;
    private Element newSourceElement;
    private Element originalTargetElement;
    private Element newTargetElement;
    private EndPoint originalSourceEndPoint;
    private EndPoint newSourceEndPoint;
    private EndPoint originalTargetEndPoint;
    private EndPoint newTargetEndPoint;

    public ConnectionChangeEvent(UIComponent component, Behavior behavior, Element originalSourceElement, Element newSourceElement,
            Element originalTargetElement, Element newTargetElement, EndPoint originalSourceEndPoint, EndPoint newSourceEndPoint,
            EndPoint originalTargetEndPoint, EndPoint newTargetEndPoint) {
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
