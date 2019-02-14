/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.lifecycle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

public class LifecyclePhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public static PhaseInfo getPhaseInfo(PhaseId id, FacesContext facesContext) {

        Map<String, Object> session = facesContext.getExternalContext().getSessionMap();
        Map<String, LinkedHashMap<Integer, PhaseInfo>> storePerView =
                (Map<String, LinkedHashMap<Integer, PhaseInfo>>) session.get(LifecyclePhaseListener.class.getName());

        if (storePerView == null) {
            storePerView = new HashMap<>();
            session.put(LifecyclePhaseListener.class.getName(), storePerView);
        }

        String viewId = ComponentUtils.calculateViewId(facesContext);

        LinkedHashMap<Integer, PhaseInfo> store = storePerView.get(viewId);
        if (store == null) {
            store = new LinkedHashMap<>();
            storePerView.put(viewId, store);
        }

        PhaseInfo phaseInfo = store.get(id.getOrdinal());
        if (phaseInfo == null) {
            phaseInfo = new PhaseInfo();
            phaseInfo.setPhase(id.getOrdinal());
            store.put(id.getOrdinal(), phaseInfo);
        }

        return phaseInfo;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        if (isGetLifecycleInfoRequest(event.getFacesContext())) {
            return;
        }

        PhaseInfo phaseInfo = getPhaseInfo(event.getPhaseId(), event.getFacesContext());
        phaseInfo.setStart(System.currentTimeMillis());

        if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
            PhaseInfo anyPhaseInfo = getPhaseInfo(PhaseId.ANY_PHASE, event.getFacesContext());
            anyPhaseInfo.setStart(System.currentTimeMillis());
        }
    }

    @Override
    public void afterPhase(PhaseEvent event) {

        if (isGetLifecycleInfoRequest(event.getFacesContext())) {
            return;
        }

        PhaseInfo phaseInfo = getPhaseInfo(event.getPhaseId(), event.getFacesContext());
        phaseInfo.setEnd(System.currentTimeMillis());
        phaseInfo.setDuration(phaseInfo.getEnd() - phaseInfo.getStart());

        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            PhaseInfo anyPhaseInfo = getPhaseInfo(PhaseId.ANY_PHASE, event.getFacesContext());
            anyPhaseInfo.setEnd(System.currentTimeMillis());
            anyPhaseInfo.setDuration(anyPhaseInfo.getEnd() - anyPhaseInfo.getStart());
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    protected boolean isGetLifecycleInfoRequest(FacesContext facesContext) {
        if (!facesContext.getPartialViewContext().isAjaxRequest()) {
            return false;
        }

        String source = facesContext.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        if (LangUtils.isValueBlank(source)) {
            return false;
        }

        return facesContext.getExternalContext().getRequestParameterMap().containsKey(source + "_getlifecycleinfo");
    }
}
