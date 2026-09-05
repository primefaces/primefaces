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

    /**
     * Keyed by the renderer itself, which is an application scoped singleton without an equals of its own. That is one
     * lookup and no key to build, on a path which {@code UIComponentBase} walks several times per component per
     * request, and a renderer which is replaced simply becomes a different key.
     */
    private final Map<Renderer, Renderer> wrappers = new ConcurrentHashMap<>();

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
        return wrappers.computeIfAbsent(renderer, PrimeRendererWrapper::new);
    }
}
