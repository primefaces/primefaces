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
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
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

    public static void invokeOnEditableValueHolder(FacesContext context, UIComponent facet, ContextCallback callback) {

        VisitContext visitContext = VisitContext.createVisitContext(context, null,
                ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        // loop through all facet components
        // sometimes its not enough to check the first component as it may be wrapped in some layout-components
        facet.visitTree(visitContext, (ctx, component) -> {

            if (CompositeUtils.isComposite(component)) {
                // a composite can implement EditableValueHolder
                if (component instanceof EditableValueHolder) {
                    callback.invokeContextCallback(context, component);
                }
                // otherwise try to check for cc:editableValueHolder
                else {
                    CompositeUtils.invokeOnDeepestEditableValueHolder(context, component, callback);
                }

                // skip composite subtree
                // a user must implement EditableValueHolder or use cc:editableValueHolder
                return VisitResult.REJECT;
            }
            else if (component instanceof EditableValueHolder) {
                callback.invokeContextCallback(context, component);
            }

            return VisitResult.ACCEPT;
        });
    }

}
