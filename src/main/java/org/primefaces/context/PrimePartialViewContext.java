/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.context;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;
import javax.faces.event.PhaseId;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

public class PrimePartialViewContext extends PartialViewContextWrapper {

    private PartialViewContext wrapped;
    private PartialResponseWriter writer = null;

    public PrimePartialViewContext(PartialViewContext wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public PartialViewContext getWrapped() {
        return this.wrapped;
    }

    @Override
    public void processPartial(PhaseId phaseId) {
        if (phaseId == PhaseId.RENDER_RESPONSE) {
            FacesContext context = FacesContext.getCurrentInstance();
            Object resetValuesObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.RESET_VALUES_PARAM);
            boolean resetValues = (null != resetValuesObject && "true".equals(resetValuesObject)) ? true : false;
            
            if (resetValues) {
                ComponentUtils.resetValuesFromComponentsToRender(context);
            }
        }

        getWrapped().processPartial(phaseId);
    }

    @Override
    public void setPartialRequest(boolean value) {
        getWrapped().setPartialRequest(value);
    }

    @Override
    public PartialResponseWriter getPartialResponseWriter() {
        if (writer == null) {
            PartialResponseWriter parentWriter = getWrapped().getPartialResponseWriter();
            writer = new PrimePartialResponseWriter(parentWriter);
        }

        return writer;
    }

    @Override
    public boolean isAjaxRequest() {
        return getWrapped().isAjaxRequest()
                || FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("javax.faces.partial.ajax");
    }

    @Override
    public boolean isPartialRequest() {
        return getWrapped().isPartialRequest()
                || FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("javax.faces.partial.execute");
    }
}