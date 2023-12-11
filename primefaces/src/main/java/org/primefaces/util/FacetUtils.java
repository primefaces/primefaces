/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;

public class FacetUtils {

    private FacetUtils() {
    }

    /**
     * Checks if the facet and one of the first level children is rendered.
     *
     * @param facet The Facet component to check
     * @param alwaysRender flag to ignore children and only check the facet itself
     * @return true if the facet should be rendered, false if not
     */
    public static boolean shouldRenderFacet(UIComponent facet, boolean alwaysRender) {
        // no facet declared at all or facet without any content
        if (facet == null) {
            return false;
        }

        // user wants to always render this facet so it can be updated
        if (alwaysRender) {
            return true;
        }

        // the facet contains multiple childs, so its wrapped inside a UIPanel
        // NOTE: we need a equals check as instanceof would also catch e.g. p:dialog
        if (facet.getClass().equals(UIPanel.class)) {
            // For any future version of JSF where the f:facet gets a rendered attribute
            if (!facet.isRendered()) {
                return false;
            }

            // check all childs - if all of them are rendered=false, we skip rendering the whole facet
            return ComponentUtils.shouldRenderChildren(facet);
        }

        // the facet contains only one child now, which means that the facet is the child component
        // we dont want to check children, just if the component is rendered=true
        return facet.isRendered();
    }

    /**
     * Checks if the facet and one of the first level children is rendered.
     * @param facet The Facet component to check
     * @return true when facet and one of the first level children is rendered.
     */
    public static boolean shouldRenderFacet(UIComponent facet) {
        return shouldRenderFacet(facet, false);
    }

    public static void invokeOnValueHolder(FacesContext context, UIComponent facet, ContextCallback callback) {
        // the facet contains multiple childs, so its wrapped inside a UIPanel
        if (facet instanceof UIPanel) {
            for (int i = 0; i < facet.getChildCount(); i++) {
                UIComponent child = facet.getChildren().get(i);
                if (child instanceof ValueHolder) {
                    callback.invokeContextCallback(context, child);
                }
            }
        }
        // the facet contains only one child now, which means that the facet is the child component
        else {
            // the child is a ValueHolder, independent if it's a composite or not
            if (facet instanceof ValueHolder) {
                callback.invokeContextCallback(context, facet);
            }
            // try to check for cc:editableValueHolder
            else if (CompositeUtils.isComposite(facet)) {
                CompositeUtils.invokeOnDeepestEditableValueHolder(context, facet, callback);
            }
        }
    }

}
