/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.application;

import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

/**
 * Keeps objects within Flash during opening a Dialog Framework - dialog and so allows passing objects via Flash to a Dialog Framework - dialog.
 */
public class DialogKeepFlashPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void beforePhase(PhaseEvent event) {
        if (isInDialogPreparation()) {
            Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
            for (String flashName : flash.keySet()) {
                flash.keep(flashName);
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    protected boolean isInDialogPreparation() {
        String dialogOutcome = (String) FacesContext.getCurrentInstance().getAttributes().get(Constants.DialogFramework.OUTCOME);
        return LangUtils.isNotEmpty(dialogOutcome);
    }

}
