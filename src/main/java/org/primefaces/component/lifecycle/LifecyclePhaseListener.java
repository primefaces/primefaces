/*
 * Copyright 2009-2016 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

public class LifecyclePhaseListener implements PhaseListener {

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

    public static PhaseInfo getPhaseInfo(PhaseId id, FacesContext facesContext) {
        
        Map<String, Object> session = facesContext.getExternalContext().getSessionMap(); 
        Map<String, LinkedHashMap<Integer, PhaseInfo>> storePerView = (Map<String, LinkedHashMap<Integer, PhaseInfo>>) session.get(LifecyclePhaseListener.class.getName());
        
        if (storePerView == null) {
            storePerView = new HashMap<String, LinkedHashMap<Integer, PhaseInfo>>();
            session.put(LifecyclePhaseListener.class.getName(), storePerView);
        }

        String viewId = ComponentUtils.calculateViewId(facesContext);
        
        LinkedHashMap<Integer, PhaseInfo> store = storePerView.get(viewId);
        if (store == null) {
            store = new LinkedHashMap<Integer, PhaseInfo>();
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
    
    protected boolean isGetLifecycleInfoRequest(FacesContext facesContext) {
        if (!facesContext.getPartialViewContext().isAjaxRequest()) {
            return false;
        }
        
        String source = facesContext.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM);
        if (ComponentUtils.isValueBlank(source)) {
            return false;
        }
        
        return facesContext.getExternalContext().getRequestParameterMap().containsKey(source + "_getlifecycleinfo");
    }
}
