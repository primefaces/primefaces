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
package org.primefaces.renderkit;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.faces.context.FacesContext;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.RenderKitFactory;

/**
 * {@link RenderKitFactory} to wrap the {@link RenderKit} with our {@link PrimeRenderKit}.
 */
public class PrimeRenderKitFactory extends RenderKitFactory {

    private final Map<String, PrimeRenderKit> renderKits = new ConcurrentHashMap<>();

    public PrimeRenderKitFactory(RenderKitFactory wrapped) {
        super(wrapped);
    }

    @Override
    public void addRenderKit(String renderKitId, RenderKit renderKit) {
        renderKits.remove(renderKitId);

        getWrapped().addRenderKit(renderKitId, renderKit);
    }

    @Override
    public RenderKit getRenderKit(FacesContext context, String renderKitId) {
        RenderKit renderKit = getWrapped().getRenderKit(context, renderKitId);

        if (renderKit == null || renderKit instanceof PrimeRenderKit) {
            return renderKit;
        }

        // a RenderKit is application scoped, so the wrapper is cached to keep its renderer wrappers alive
        PrimeRenderKit wrapper = renderKits.get(renderKitId);

        if (wrapper == null || wrapper.getWrapped() != renderKit) {
            wrapper = new PrimeRenderKit(renderKit);
            renderKits.put(renderKitId, wrapper);
        }

        return wrapper;
    }

    @Override
    public Iterator<String> getRenderKitIds() {
        return getWrapped().getRenderKitIds();
    }
}
