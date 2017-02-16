/*
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.util;

import java.beans.BeanInfo;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.EditableValueHolderAttachedObjectTarget;

public class CompositeUtils {
    
    static class EditableValueHolderCallbackWrapper implements ContextCallback {
        private final ContextCallback callback;
        
        public EditableValueHolderCallbackWrapper(ContextCallback callback) {
            this.callback = callback;
        }

        @Override
        public void invokeContextCallback(FacesContext context, UIComponent target) {
            if (isComposite(target)) {
                invokeOnEditableValueHolder(context, target, this);
            }
            else {
                callback.invokeContextCallback(context, target);
            }
        }
    }
    
    public static boolean isComposite(UIComponent component) {
        return UIComponent.isCompositeComponent(component);
    }
    
    public static void invokeOnEditableValueHolder(FacesContext context, UIComponent composite,
            ContextCallback callback) {
        BeanInfo info = (BeanInfo) composite.getAttributes().get(UIComponent.BEANINFO_KEY);
        List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

        for (AttachedObjectTarget target : targets) {
            if (target instanceof EditableValueHolderAttachedObjectTarget) {
                UIComponent children = composite.findComponent(target.getName());
                if (children == null) {
                    throw new FacesException(
                            "Cannot find editableValueHolder with name: \"" + target.getName()
                                    + "\" in composite component with id: \"" + composite.getClientId() + "\"");
                }

                ComponentTraversalUtils.closestNamingContainer(composite)
                        .invokeOnComponent(context, children.getClientId(context), callback);
            }
        }
    }

    public static void invokeOnDeepestEditableValueHolder(FacesContext context, UIComponent composite,
            ContextCallback callback) {
        EditableValueHolderCallbackWrapper callbackWrapper = new EditableValueHolderCallbackWrapper(callback);
        invokeOnEditableValueHolder(context, composite, callbackWrapper);
    }
}
