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
package org.primefaces.util;

import java.beans.BeanInfo;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.ContextCallback;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.AttachedObjectTarget;
import jakarta.faces.view.EditableValueHolderAttachedObjectTarget;

public class CompositeUtils {

    private CompositeUtils() {
    }

    public static boolean isComposite(UIComponent component) {
        return UIComponent.isCompositeComponent(component);
    }

    /**
     * Invoke callback on the first rendered {@link EditableValueHolder} component
     */
    public static void invokeOnDeepestEditableValueHolder(FacesContext context, UIComponent composite, ContextCallback callback) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
        composite.visitTree(visitContext, new EditableValueHolderVisitCallback(callback));
    }

    private static class EditableValueHolderVisitCallback implements VisitCallback {

        private final ContextCallback callback;

        public EditableValueHolderVisitCallback(ContextCallback callback) {
            this.callback = callback;
        }

        @Override
        public VisitResult visit(VisitContext context, UIComponent target) {
            if (target instanceof EditableValueHolder) {
                callback.invokeContextCallback(context.getFacesContext(), target);
                return VisitResult.COMPLETE;
            }
            else if (UIComponent.isCompositeComponent(target)) {
                return visitEditableValueHolderTargets(context, target);
            }
            return VisitResult.ACCEPT;
        }

        private VisitResult visitEditableValueHolderTargets(VisitContext visitContext, UIComponent component) {
            BeanInfo info = (BeanInfo) component.getAttributes().get(UIComponent.BEANINFO_KEY);
            List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                    .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

            if (targets != null) {
                for (int i = 0; i < targets.size(); i++) {
                    AttachedObjectTarget target = targets.get(i);
                    if (target instanceof EditableValueHolderAttachedObjectTarget) {
                        List<UIComponent> children = target.getTargets(component);
                        if (children == null || children.isEmpty()) {
                            throw new FacesException("Cannot resolve <cc:editableValueHolder /> target " +
                                    "in component with id: \"" + component.getClientId() + "\"");
                        }
                        for (int j = 0; j < children.size(); j++) {
                            final UIComponent child = children.get(j);
                            if (child.visitTree(visitContext, this)) {
                                return VisitResult.COMPLETE;
                            }
                        }
                    }
                }
            }

            return VisitResult.ACCEPT;
        }
    }
}
