/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.visit;

import jakarta.faces.component.ContextCallback;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;

/**
 * Backwards compatible support for JSF 2.3 and below.
 * <p>
 * This class is a callback implementation for resetting input values in JSF components.
 * It implements the {@link ContextCallback} interface and is used to reset the value
 * of {@link EditableValueHolder} components or visit their children if they are not
 * instances of {@link EditableValueHolder}.
 * @see <a href="https://github.com/jakartaee/faces/issues/1936">Faces Issue #1936</a>
 */
public class ResetInputContextCallback implements ContextCallback {

    private VisitContext visitContext;

    /**
     * Constructs a new ResetInputContextCallback with the given {@link VisitContext}.
     *
     * @param visitContext the visit context to be used for visiting component trees
     */
    public ResetInputContextCallback(VisitContext visitContext) {
        this.visitContext = visitContext;
    }

    /**
     * Invokes the context callback on the given component. If the component is an instance
     * of {@link EditableValueHolder}, its value is reset. Otherwise, the component's tree
     * is visited using the {@link ResetInputVisitCallback} instance.
     *
     * @param fc the current {@link FacesContext}
     * @param component the component on which to invoke the context callback
     */
    @Override
    public void invokeContextCallback(FacesContext fc, UIComponent component) {
        if (component instanceof EditableValueHolder) {
            ((EditableValueHolder) component).resetValue();
        }
        else {
            component.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
        }
    }
}