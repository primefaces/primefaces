/**
 * Copyright 2009-2018 PrimeTek.
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.primefaces.component.api;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

public class WrapperEvent extends FacesEvent {

    private FacesEvent event = null;
    private String rowKey = null;

    public WrapperEvent(UIComponent component, FacesEvent event, String rowKey) {
        super(component);
        this.event = event;
        this.rowKey = rowKey;
    }

    public FacesEvent getFacesEvent() {
        return (event);
    }

    public String getRowKey() {
        return rowKey;
    }

    @Override
    public PhaseId getPhaseId() {
        return (event.getPhaseId());
    }

    @Override
    public void setPhaseId(PhaseId phaseId) {
        event.setPhaseId(phaseId);
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return (false);
    }

    @Override
    public void processListener(FacesListener listener) {
        throw new IllegalStateException();
    }
}
