/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.deferred;

import org.primefaces.cdk.api.FacesComponentInfo;

import java.util.function.BiConsumer;

import jakarta.faces.application.Application;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitHint;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PostValidateEvent;
import jakarta.faces.event.PreValidateEvent;

@FacesComponent(value = Deferred.COMPONENT_TYPE, namespace = Deferred.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Deferred loads its content via AJAX after the page was rendered and shows a placeholder until then.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Deferred extends DeferredBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Deferred";

    public static final String CONTAINER_CLASS = "p-deferred p-component";
    public static final String MANUAL_CLASS = "p-deferred-manual";
    public static final String PLACEHOLDER_CLASS = "p-deferred-placeholder";
    public static final String LOADING_CLASS = "p-deferred-loading p-icon p-loading-icon p-deferred-loading-icon";

    private static final String CONTENT_LOADED = "contentLoaded";

    public boolean isLoadRequest(FacesContext context) {
        String clientId = getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_load");
    }

    /**
     * The content counts as loaded once it was rendered in this view, or when it is marked with the {@code loaded} attribute.
     *
     * @return {@code true} if the content is loaded
     */
    public boolean isContentLoaded() {
        return Boolean.TRUE.equals(getLoaded()) || Boolean.TRUE.equals(getStateHelper().get(CONTENT_LOADED));
    }

    public void markContentLoaded() {
        getStateHelper().put(CONTENT_LOADED, Boolean.TRUE);
    }

    /**
     * A stateless view can not keep the loaded state, the component follows the per-request rule of p:outputPanel there.
     *
     * @param context the {@link FacesContext}
     * @return {@code true} if the view is stateless
     */
    public boolean isStateless(FacesContext context) {
        return context.getViewRoot() != null && context.getViewRoot().isTransient();
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (isStateless(context) && !isLoadRequest(context)) {
            super.processDecodes(context);
            return;
        }
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        try {
            process(context, UIComponent::processDecodes);
            try {
                decode(context);
            }
            catch (RuntimeException e) {
                context.renderResponse();
                throw e;
            }
        }
        finally {
            popComponentFromEL(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (isStateless(context) && !isLoadRequest(context)) {
            super.processValidators(context);
            return;
        }
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        try {
            Application application = context.getApplication();
            application.publishEvent(context, PreValidateEvent.class, this);
            process(context, UIComponent::processValidators);
            application.publishEvent(context, PostValidateEvent.class, this);
        }
        finally {
            popComponentFromEL(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (isStateless(context) && !isLoadRequest(context)) {
            super.processUpdates(context);
            return;
        }
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        try {
            process(context, UIComponent::processUpdates);
        }
        finally {
            popComponentFromEL(context);
        }
    }

    /**
     * Like rendered="false": the children are only processed once the content is loaded, the loading facet only until then.
     * The load request processes neither of them.
     */
    private void process(FacesContext context, BiConsumer<UIComponent, FacesContext> phase) {
        if (isLoadRequest(context)) {
            return;
        }

        if (isContentLoaded()) {
            for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
                phase.accept(getChildren().get(i), context);
            }
        }
        else {
            UIComponent loadingFacet = getLoadingFacet();
            if (loadingFacet != null) {
                phase.accept(loadingFacet, context);
            }
        }
    }

    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        FacesContext facesContext = context.getFacesContext();
        if (!context.getHints().contains(VisitHint.SKIP_UNRENDERED) || isStateless(facesContext)) {
            return super.visitTree(context, callback);
        }
        if (!isVisitable(context)) {
            return false;
        }

        // interpret not loaded content (and the loading facet of loaded content) as SKIP_UNRENDERED
        pushComponentToEL(facesContext, this);
        try {
            VisitResult result = context.invokeVisitCallback(this, callback);
            if (result == VisitResult.COMPLETE) {
                return true;
            }

            if (result == VisitResult.ACCEPT) {
                if (isContentLoaded()) {
                    for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
                        if (getChildren().get(i).visitTree(context, callback)) {
                            return true;
                        }
                    }
                }
                else {
                    UIComponent loadingFacet = getLoadingFacet();
                    if (loadingFacet != null && loadingFacet.visitTree(context, callback)) {
                        return true;
                    }
                }
            }
        }
        finally {
            popComponentFromEL(facesContext);
        }

        return false;
    }
}
