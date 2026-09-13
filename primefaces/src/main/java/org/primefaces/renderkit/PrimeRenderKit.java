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

    private static final char KEY_SEPARATOR = '\0';

    /**
     * Keyed by family and renderer type, so that the key space stays bounded by the number of registered renderer
     * types. Keying by the renderer itself would be one lookup less, but a {@link RenderKitWrapper} further down the
     * chain is free to build a new renderer per lookup, the way this class does itself, and the map would then grow
     * by about the component count on every request, forever. The cached wrapper is handed out only while it still
     * wraps what the delegate returns, which also covers a renderer replaced through
     * {@link #addRenderer(String, String, Renderer)}.
     */
    private final Map<String, PrimeRendererWrapper> wrappers = new ConcurrentHashMap<>();

    public PrimeRenderKit(RenderKit wrapped) {
        super(wrapped);
    }

    @Override
    public Renderer getRenderer(String family, String rendererType) {
        Renderer renderer = super.getRenderer(family, rendererType);

        if (!(renderer instanceof CoreRenderer)) {
            return renderer;
        }

        // the wrapper is stateless, but a stable instance per renderer keeps renderers comparable by identity
        String key = family + KEY_SEPARATOR + rendererType;
        PrimeRendererWrapper wrapper = wrappers.get(key);

        if (wrapper == null || wrapper.getWrapped() != renderer) {
            wrapper = new PrimeRendererWrapper(renderer);
            wrappers.put(key, wrapper);
        }

        return wrapper;
    }
}
