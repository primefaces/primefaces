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
package org.primefaces.util;

import java.beans.BeanInfo;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.EditableValueHolderAttachedObjectTarget;

public class CompositeUtils {

    private CompositeUtils() {
    }

    public static boolean isComposite(UIComponent component) {
        return UIComponent.isCompositeComponent(component);
    }

    /**
     * Attention: This only supports cc:editableValueHolder which target a single component!
     *
     * @param context
     * @param composite
     * @param callback
     */
    public static void invokeOnDeepestEditableValueHolder(FacesContext context, UIComponent composite,
            final ContextCallback callback) {

        if (composite instanceof EditableValueHolder) {
            callback.invokeContextCallback(context, composite);
            return;
        }

        BeanInfo info = (BeanInfo) composite.getAttributes().get(UIComponent.BEANINFO_KEY);
        List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                AttachedObjectTarget target = targets.get(i);
                if (target instanceof EditableValueHolderAttachedObjectTarget) {

                    List<UIComponent> childs = target.getTargets(composite);
                    if (childs == null || childs.isEmpty()) {
                        throw new FacesException(
                                "Cannot not resolve editableValueHolder target in composite component with id: \""
                                + composite.getClientId() + "\"");
                    }

                    if (childs.size() > 1) {
                        throw new FacesException(
                                "Only a single editableValueHolder target is supported in composite component with id: \""
                                + composite.getClientId() + "\"");
                    }

                    final UIComponent child = childs.get(0);

                    composite.invokeOnComponent(context, composite.getClientId(context), new ContextCallback() {

                        @Override
                        public void invokeContextCallback(FacesContext context, UIComponent target) {
                            if (isComposite(child)) {
                                invokeOnDeepestEditableValueHolder(context, child, callback);
                            }
                            else {
                                callback.invokeContextCallback(context, child);
                            }
                        }
                    });
                }
            }
        }
    }
}
