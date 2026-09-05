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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.faces.render.RenderKit;
import jakarta.faces.render.RenderKitWrapper;
import jakarta.faces.render.Renderer;

/**
 * {@link RenderKit} which hands out every PrimeFaces {@link CoreRenderer} inside a {@link PrimeRendererWrapper}, so
 * that a component can clean up its iteration state in one place instead of every renderer having to remember it.
 * <p>
 * Only {@link CoreRenderer} instances are wrapped. Renderers of the Faces implementation and of other component
 * libraries are handed out untouched, so that code which casts them to their own type keeps working.
 *
 * @see org.primefaces.component.api.IterationCleanupAware
 */
@SuppressWarnings("rawtypes")
public class PrimeRenderKit extends RenderKitWrapper {

    private final Map<String, Map<String, Renderer>> wrappers = new ConcurrentHashMap<>();

    public PrimeRenderKit(RenderKit wrapped) {
        super(wrapped);
    }

    @Override
    public Renderer getRenderer(String family, String rendererType) {
        Renderer renderer = super.getRenderer(family, rendererType);

        if (!(renderer instanceof CoreRenderer)) {
            return renderer;
        }

        // the wrapper is stateless, but a stable instance per family and type keeps renderers comparable by identity
        Map<String, Renderer> wrappersByType = wrappers.computeIfAbsent(family, k -> new ConcurrentHashMap<>());
        Renderer wrapper = wrappersByType.get(rendererType);

        if (!(wrapper instanceof PrimeRendererWrapper primeRendererWrapper) || primeRendererWrapper.getWrapped() != renderer) {
            wrapper = new PrimeRendererWrapper(renderer);
            wrappersByType.put(rendererType, wrapper);
        }

        return wrapper;
    }

    @Override
    public void addRenderer(String family, String rendererType, Renderer renderer) {
        Map<String, Renderer> wrappersByType = wrappers.get(family);
        if (wrappersByType != null) {
            wrappersByType.remove(rendererType);
        }

        super.addRenderer(family, rendererType, renderer);
    }
}
