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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UniqueIdVendor;
import javax.faces.context.FacesContext;

public class ComponentTraversalUtils {

    private ComponentTraversalUtils() {
    }

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
            UIComponent kid = kids.next();
            if (type.isAssignableFrom(kid.getClass())) {
                result = (T) kid;
                break;
            }

            result = first(type, kid);
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
            UIComponent kid = kids.next();
            if (type.isAssignableFrom(kid.getClass())) {
                result.add((T) kid);
            }
        }

        return result;
    }

    public static void withId(String id, UIComponent base, List<UIComponent> components) {

        if (id.equals(base.getId())) {
            components.add(base);
        }

        if (base.getFacetCount() > 0) {
            for (UIComponent facet : base.getFacets().values()) {
                withId(id, facet, components);
            }
        }

        if (base.getChildCount() > 0) {
            for (int i = 0, childCount = base.getChildCount(); i < childCount; i++) {
                UIComponent child = base.getChildren().get(i);
                withId(id, child, components);
            }
        }
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
            UIComponent kid = kids.next();
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
     * @param separatorChar The separatorChar (e.g. :).
     * @param context The FacesContext.
     * @param callback the callback for the found component
     */
    public static void firstById(String id, UIComponent base, char separatorChar, FacesContext context, ContextCallback callback) {

        // try #findComponent first
        UIComponent component = base.findComponent(id);

        // try #invokeOnComponent
        // it's required to support e.g. a full client id for a component which is placed inside UIData components
        if (component == null) {
            // #invokeOnComponent doesn't support the leading seperator char
            String tempExpression = id;
            if (tempExpression.charAt(0) == separatorChar) {
                tempExpression = tempExpression.substring(1);
            }

            context.getViewRoot().invokeOnComponent(context, tempExpression, callback);
        }
        else {
            callback.invokeContextCallback(context, component);
        }
    }

    public static UIForm closestForm(FacesContext context, UIComponent component) {
        return closest(UIForm.class, component);
    }

    public static UniqueIdVendor closestUniqueIdVendor(UIComponent component) {
        return closest(UniqueIdVendor.class, component);
    }

    public static UIComponent closestNamingContainer(UIComponent component) {
        return (UIComponent) closest(NamingContainer.class, component);
    }
}
