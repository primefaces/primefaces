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

import org.primefaces.cdk.api.FacesRendererInfo;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentScriptBuilder;
import org.primefaces.util.FacetUtils;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Deferred.DEFAULT_RENDERER, componentFamily = Deferred.COMPONENT_FAMILY)
@FacesRendererInfo
public class DeferredRenderer extends CoreRenderer<Deferred> {

    private static final String BLOCK = "div";
    private static final String INLINE = "span";

    @Override
    public void decode(FacesContext context, Deferred component) {
        if (component.isLoadRequest(context)) {
            decodeBehaviors(context, component);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, Deferred component) throws IOException {
        boolean loaded = isContentRendered(context, component);

        encodeMarkup(context, component, loaded);
        encodeScript(context, component, loaded);
    }

    protected boolean isContentRendered(FacesContext context, Deferred component) {
        if (component.isLoadRequest(context) || component.isContentLoaded()) {
            return true;
        }

        // a stateless view can not keep the loaded state: placeholder on non-AJAX requests, content on AJAX requests (as p:outputPanel)
        return component.isStateless(context) && context.getPartialViewContext().isAjaxRequest();
    }

    protected void encodeMarkup(FacesContext context, Deferred component, boolean loaded) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String tag = "block".equals(component.getLayout()) ? BLOCK : INLINE;
        boolean manual = "manual".equals(component.getMode());
        String style = component.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(Deferred.CONTAINER_CLASS)
                .add(manual, Deferred.MANUAL_CLASS)
                .add(component.getStyleClass())
                .build();

        writer.startElement(tag, component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        if (loaded) {
            renderChildren(context, component);
            component.markContentLoaded();
        }
        else {
            encodePlaceholder(context, component, tag, manual);
        }

        writer.endElement(tag);
    }

    protected void encodePlaceholder(FacesContext context, Deferred component, String tag, boolean manual) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent loadingFacet = component.getLoadingFacet();

        if (!FacetUtils.shouldRenderFacet(loadingFacet)) {
            writer.startElement("i", null);
            writer.writeAttribute("class", Deferred.LOADING_CLASS, null);
            writer.writeAttribute("aria-hidden", "true", null);
            writer.endElement("i");
        }
        else if (manual) {
            // shown until the content is loaded manually and may contain the trigger, so it is not hidden from assistive technology
            loadingFacet.encodeAll(context);
        }
        else {
            writer.startElement(tag, null);
            writer.writeAttribute("class", Deferred.PLACEHOLDER_CLASS, null);
            writer.writeAttribute("aria-hidden", "true", null);
            loadingFacet.encodeAll(context);
            writer.endElement(tag);
        }
    }

    protected void encodeScript(FacesContext context, Deferred component, boolean loaded) throws IOException {
        ComponentScriptBuilder wb = getComponentScriptBuilder(context);
        wb.init("Deferred", component)
                .attr("mode", component.getMode())
                .attr("loaded", loaded, false);

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public void encodeChildren(FacesContext context, Deferred component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
