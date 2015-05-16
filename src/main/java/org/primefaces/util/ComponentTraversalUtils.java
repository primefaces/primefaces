/*
 * Copyright 2015 tandraschko.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.util.Iterator;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UniqueIdVendor;
import javax.faces.context.FacesContext;

public class ComponentTraversalUtils {

    public static UIComponent closest(Class<?> type, UIComponent source) {
        UIComponent parent = source.getParent();

        while (parent != null) {
            if (type.isAssignableFrom(parent.getClass())) {
                return parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    /**
     * Finds the first component with the given id (NOT clientId!).
     * 
     * @param id The id.
     * @param base The base component to start the traversal.
     * @return The component or null.
     */
    public static UIComponent firstById(String id, UIComponent base) {
        if (id.equals(base.getId())) {
            return base;
        }

        UIComponent kid = null;
        UIComponent result = null;

        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
            kid = (UIComponent) kids.next();
            if (id.equals(kid.getId())) {
                result = kid;
                break;
            }
            result = firstById(id, kid);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    
    
    public static UIComponent closestForm(FacesContext context, UIComponent component) {
        return closest(UIForm.class, component);
    }

    public static UniqueIdVendor closestUniqueIdVendor(UIComponent component) {
        return (UniqueIdVendor) closest(UniqueIdVendor.class, component);
    }

    public static UIComponent closestNamingContainer(UIComponent component) {
        return closest(NamingContainer.class, component);
    }
}
