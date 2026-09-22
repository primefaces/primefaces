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
package org.primefaces.component.api;

import jakarta.faces.context.FacesContext;

/**
 * Implemented by components whose renderer leaves iteration state behind: a row index, a row key, or an iteration
 * variable in the request map. {@code org.primefaces.renderkit.PrimeRendererWrapper} calls
 * {@link #cleanupIterationState(FacesContext)} right after the renderer's {@code decode} and {@code encodeEnd}, also
 * when either threw, so that the state never outlives the phase of the component which set it.
 * <p>
 * State left behind has two effects for the rest of the request. The iteration variable resolves to the last row for
 * everything which renders after the component. And where the component folds the row into the client id it hands its
 * children, their state is saved under a client id which the next request does not rebuild.
 */
public interface IterationCleanupAware {

    /**
     * Resets whatever iteration state this component has set. Called after {@code decode} and after {@code encodeEnd},
     * so it must be safe to call when nothing was set, when it was already reset, and when the phase threw halfway.
     *
     * @param context the {@link FacesContext}.
     */
    void cleanupIterationState(FacesContext context);
}
