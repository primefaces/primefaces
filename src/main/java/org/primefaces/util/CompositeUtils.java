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
    
    public static boolean isComposite(UIComponent component) {
        return UIComponent.isCompositeComponent(component);
    }
    
    public static void invokeOnEditableValueHolder(FacesContext context, UIComponent composite,
            final ContextCallback callback) {
        BeanInfo info = (BeanInfo) composite.getAttributes().get(UIComponent.BEANINFO_KEY);
        List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

        for (AttachedObjectTarget target : targets) {
            if (target instanceof EditableValueHolderAttachedObjectTarget) {
                final UIComponent children = composite.findComponent(target.getName());
                if (children == null) {
                    throw new FacesException(
                            "Cannot find editableValueHolder with name: \"" + target.getName()
                                    + "\" in composite component with id: \"" + composite.getClientId() + "\"");
                }

                composite.invokeOnComponent(context, composite.getClientId(context), new ContextCallback() {
                    public void invokeContextCallback(FacesContext context, UIComponent target) {
                        if (isComposite(children)) {
                            invokeOnEditableValueHolder(context, children, callback);
                        }
                        else {
                            callback.invokeContextCallback(context, children);
                        }
                    }
                });
            }
        }
    }

    public static void invokeOnDeepestEditableValueHolder(FacesContext context, UIComponent composite,
            ContextCallback callback) {
        invokeOnEditableValueHolder(context, composite, callback);
    }
}
