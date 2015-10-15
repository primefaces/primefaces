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

import java.util.ArrayList;
import java.util.Iterator;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UniqueIdVendor;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

public class ComponentTraversalUtils {

    public static <T> T closest(Class<T> type, UIComponent base) {
        UIComponent parent = base.getParent();

        while (parent != null) {
            if (type.isAssignableFrom(parent.getClass())) {
                return (T) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public static <T> T first(Class<T> type, UIComponent base) {
        T result = null;

        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
            UIComponent kid = (UIComponent) kids.next();
            if (type.isAssignableFrom(kid.getClass())) {
                result = (T) kid;
                break;
            }

            result = first(type, base);
            if (result != null) {
                break;
            }
        }

        return result;
    }
    
    public static <T> ArrayList<T> children(Class<T> type, UIComponent base) {
        
        ArrayList<T> result = new ArrayList<T>();
        
        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if (type.isAssignableFrom(kid.getClass())) {
                result.add((T) kid);
            }
        }

        return result;
    }
    
    /**
     * Finds the first component with the given id (NOT clientId!).
     * 
     * @param id The id.
     * @param base The base component to start the traversal.
     * @return The component or null.
     */
    public static UIComponent firstWithId(String id, UIComponent base) {
        if (id.equals(base.getId())) {
            return base;
        }

        UIComponent result = null;

        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
            UIComponent kid = (UIComponent) kids.next();
            if (id.equals(kid.getId())) {
                result = kid;
                break;
            }
            result = firstWithId(id, kid);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    /**
     * Finds the first component by the given id expression or client id.
     * 
     * @param id The id.
     * @param base The base component to start the traversal.
     * @param separatorString The seperatorString (e.g. :).
     * @param context The FacesContext.
     * @return The component or null.
     */
    public static UIComponent firstById(String id, UIComponent base, String separatorString, FacesContext context) {
        return firstById(id, base, separatorString, context, false);
    }
    
    /**
     * Finds the first component by the given id expression or client id.
     * 
     * @param id The id.
     * @param base The base component to start the traversal.
     * @param separatorString The seperatorString (e.g. :).
     * @param context The FacesContext.
     * @param skipUnrendered Defined if unrendered components should be skipped.
     * @return The component or null.
     */
    public static UIComponent firstById(String id, UIComponent base, String separatorString, FacesContext context, boolean skipUnrendered) {

        // try #findComponent first
        UIComponent component = base.findComponent(id);

        // try #invokeOnComponent
        // it's required to support e.g. a full client id for a component which is placed inside UIData components
        if (component == null) {
            // #invokeOnComponent doesn't support the leading seperator char
            String tempExpression = id;
            if (tempExpression.startsWith(separatorString)) {
                tempExpression = tempExpression.substring(1);
            }

            if (skipUnrendered)
            {
                VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
                IdVisitCallback callback = new IdVisitCallback(tempExpression);
                context.getViewRoot().visitTree(visitContext, callback);
                
                return callback.getComponent();
            }
            else
            {                
                IdContextCallback callback = new IdContextCallback();
                context.getViewRoot().invokeOnComponent(context, tempExpression, callback);

                component = callback.getComponent();
            }
        }
        
        return component;
    }
    
    
    
    public static UIForm closestForm(FacesContext context, UIComponent component) {
        return closest(UIForm.class, component);
    }

    public static UniqueIdVendor closestUniqueIdVendor(UIComponent component) {
        return (UniqueIdVendor) closest(UniqueIdVendor.class, component);
    }

    public static UIComponent closestNamingContainer(UIComponent component) {
        return (UIComponent) closest(NamingContainer.class, component);
    }
    
    
    public static class IdVisitCallback implements VisitCallback {

        private final String targetClientId;
        private UIComponent component;
        
        public IdVisitCallback(String targetClientId) {
            this.targetClientId = targetClientId;
        }
        
        public VisitResult visit(VisitContext context, UIComponent target) {
            
            if (target.getClientId().equals(targetClientId)) {
                this.component = target;
                return VisitResult.COMPLETE;
            }
            
            return VisitResult.ACCEPT;
        }
        
        public UIComponent getComponent() {
            return component;
        }
    }
    
    public static class IdContextCallback implements ContextCallback {

        private UIComponent component;

        public void invokeContextCallback(FacesContext context, UIComponent target) {
            component = target;
        }

        public UIComponent getComponent() {
            return component;
        }
    }
}
