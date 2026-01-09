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
package org.primefaces.component.autoupdate;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import java.util.List;
import java.util.Map;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

public class AutoUpdatePhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void beforePhase(PhaseEvent phaseEvent) {
        FacesContext context = phaseEvent.getFacesContext();
        if (!context.isPostback() || PrimeRequestContext.getCurrentInstance(context).isIgnoreAutoUpdate()) {
            return;
        }

        Map<String, List<String>> infos = AutoUpdateListener.getAutoUpdateComponentInfos(context);
        if (infos != null && !infos.isEmpty()) {
            for (Map.Entry<String, List<String>> entries : infos.entrySet()) {
                String clientId = entries.getKey();
                List<String> events = entries.getValue();

                // events are optional, otherwise always try to update
                if (events != null && !events.isEmpty()) {
                    if (!requestContainsEventToUpdate(context, events)) {
                        continue;
                    }
                }

                if (!context.getPartialViewContext().getRenderIds().contains(clientId)) {
                    context.getPartialViewContext().getRenderIds().add(clientId);
                }
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    /**
     * Checks if the current request contains one of the events to update.
     *
     * @param context the {@link FacesContext}
     * @param events the events to be checked
     * @return if it contains one of the events.
     */
    protected boolean requestContainsEventToUpdate(FacesContext context, List<String> events) {
        String update = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_UPDATE_PARAM);

        // empty update param == no event/observer will be updated this request -> skip
        if (LangUtils.isBlank(update)) {
            return false;
        }

        // if observers are defined on p:autoUpdate, at least one of the events must be updated in the current request
        for (String event : events) {
            if (update.contains("@obs(" + event + ")")) {
                return true;
            }
        }

        return false;
    }
}
