/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.EditableValueHolderAttachedObjectTarget;

public class FacetUtils {

    private static final Logger LOGGER = Logger.getLogger(FacetUtils.class.getName());

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
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);
        facet.visitTree(visitContext, new EditableValueHolderVisitCallback(callback));
    }

    private static class EditableValueHolderVisitCallback implements VisitCallback {

        private final ContextCallback callback;

        public EditableValueHolderVisitCallback(ContextCallback callback) {
            this.callback = callback;
        }

        @Override
        public VisitResult visit(VisitContext context, UIComponent target) {
            return invokeOnDeepestEditableValueHolder(context, target, callback);
        }

        public VisitResult invokeOnDeepestEditableValueHolder(VisitContext visitContext, UIComponent component, ContextCallback callback) {
            FacesContext context = visitContext.getFacesContext();
            if (component instanceof EditableValueHolder) {
                if (!ComponentUtils.isVisitable(visitContext, component)) {
                    return VisitResult.ACCEPT;
                }
                callback.invokeContextCallback(context, component);
                return VisitResult.COMPLETE;
            }

            BeanInfo info = (BeanInfo) component.getAttributes().get(UIComponent.BEANINFO_KEY);
            List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                    .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

            if (targets != null) {
                AtomicReference<VisitResult> result = new AtomicReference<>(VisitResult.ACCEPT);
                for (int i = 0; i < targets.size(); i++) {
                    AttachedObjectTarget target = targets.get(i);
                    if (target instanceof EditableValueHolderAttachedObjectTarget) {
                        List<UIComponent> children = target.getTargets(component);
                        if (children == null || targets.isEmpty()) {
                            throw new FacesException("Cannot resolve <cc:editableValueHolder /> target in component with id: \"" + component.getClientId() + "\"");
                        }
                        for (int j = 0; j < children.size(); j++) {
                            final UIComponent child = children.get(j);
                            component.invokeOnComponent(context, component.getClientId(context),
                                    (ctxt, comp) -> result.set(invokeOnDeepestEditableValueHolder(visitContext, child, callback)));
                            if (result.get() != VisitResult.ACCEPT) {
                                return result.get();
                            }
                        }
                    }
                }
            }

            return VisitResult.COMPLETE;
        }
    }

}
