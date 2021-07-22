/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.selenium.internal;

import javax.faces.context.FacesContext;
import javax.faces.event.*;

import org.primefaces.selenium.internal.component.PrimeFacesSeleniumSystemEventListener;

public class PrimefacesSeleniumPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void beforePhase(PhaseEvent phaseEvent) {
        if (!FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
            /*
             * PrimefacesSeleniumPhaseListener adds PrimeFacesSeleniumSystemEventListener as PreRenderViewEvent. PrimeFacesSeleniumSystemEventListener adds
             * PrimeFacesSeleniumDummyComponent to the component-tree. And finally PrimeFacesSeleniumDummyComponent adds pfselenium.core.csp.js after core.js
             * was added by PrimeFaces itself. This works independent of JSF-stage, MOVE_TO_BOTTOM , ...
             */
            FacesContext.getCurrentInstance().getViewRoot().subscribeToViewEvent(PreRenderViewEvent.class, new PrimeFacesSeleniumSystemEventListener());
        }
    }

    @Override
    public void afterPhase(PhaseEvent phaseEvent) {

    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
