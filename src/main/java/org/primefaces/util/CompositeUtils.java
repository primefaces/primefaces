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
