/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.primefaces.model.Visibility;

public class ToggleEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Visibility status
     */
    private Visibility visibility;

    /**
     * Related data if available
     */
    private Object data;

    public ToggleEvent(UIComponent component, Behavior behavior, Visibility visibility) {
        super(component, behavior);
        this.visibility = visibility;
    }

    public ToggleEvent(UIComponent component, Behavior behavior, Visibility visibility, Object data) {
        super(component, behavior);
        this.visibility = visibility;
        this.data = data;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Object getData() {
        return data;
    }
}
