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

import org.primefaces.component.api.IterationCleanupAware;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.Renderer;
import jakarta.faces.render.RendererWrapper;

/**
 * Wraps a {@link CoreRenderer} to give every PrimeFaces component a single place to clean up after itself.
 * <p>
 * A renderer which iterates sets a row index, a row key or an iteration variable, and historically each one had to
 * remember to reset it before it returned. The ones which forgot leaked the iteration variable into the rest of the
 * request and gave their children a row prefixed client id which the next request does not rebuild. Components which
 * implement {@link IterationCleanupAware} are reset here instead, also when the phase threw.
 * <p>
 * Both {@code decode} and {@code encodeEnd} are covered. They are the last thing which
 * {@code UIComponentBase#processDecodes} and {@code UIComponentBase#encodeEnd} do, so nothing which still needs the
 * row runs after them.
 *
 * @see PrimeRenderKit
 */
@SuppressWarnings("rawtypes")
public class PrimeRendererWrapper extends RendererWrapper {

    public PrimeRendererWrapper(Renderer wrapped) {
        super(wrapped);
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        try {
            super.decode(context, component);
        }
        finally {
            cleanupIterationState(context, component);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        try {
            super.encodeEnd(context, component);
        }
        finally {
            cleanupIterationState(context, component);
        }
    }

    private static void cleanupIterationState(FacesContext context, UIComponent component) {
        if (component instanceof IterationCleanupAware iterationCleanupAware) {
            iterationCleanupAware.cleanupIterationState(context);
        }
    }
}
