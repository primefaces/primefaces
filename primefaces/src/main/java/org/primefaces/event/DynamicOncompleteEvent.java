/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.PrimeFaces;
import org.primefaces.component.api.AjaxSource;

import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.PhaseId;

/**
 * Queue this event during decode in case your {@code oncomplete} attribute is a {@link ValueExpression}.
 * This way the value will be re-evaluated and executed via {@link PrimeFaces#executeScript(String)}.
 */
public class DynamicOncompleteEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    public DynamicOncompleteEvent(UIComponent component, AjaxSource ajaxSource, PhaseId phaseId) {
        super(component, (event) -> {
            String oncomplete = ajaxSource.getOncomplete();

            if (oncomplete != null) {
                PrimeFaces.current().executeScript(oncomplete);
            }
        });

        setPhaseId(phaseId);
    }
}
