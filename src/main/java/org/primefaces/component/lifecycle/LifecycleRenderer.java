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

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class LifecycleRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Lifecycle lifecycle = (Lifecycle) component;
        String clientId = lifecycle.getClientId(context);
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("table", lifecycle);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", Lifecycle.STYLE_CLASS, null);
        
        writer.startElement("tr", null);
        for (PhaseId phaseId : PhaseId.VALUES) {
            if (phaseId != PhaseId.ANY_PHASE) {
                encodePhase(phaseId, phaseId.toString(), context, writer);
            }
        }
        encodePhase(PhaseId.ANY_PHASE, "ALL", context, writer);
        writer.endElement("tr");
        
        writer.endElement("table");


        WidgetBuilder wb = RequestContext.getCurrentInstance().getWidgetBuilder();
        wb.initWithDomReady("Lifecycle", lifecycle.resolveWidgetVar(), clientId);
        wb.finish();
    }
    
    protected void encodePhase(PhaseId phaseId, String name, FacesContext context, ResponseWriter writer) throws IOException {
        PhaseInfo phaseInfo = LifecyclePhaseListener.getPhaseInfo(phaseId, context);

        writer.startElement("td", null);

        writer.startElement("div", null);
        writer.writeAttribute("class", Lifecycle.STYLE_CLASS_NAME, null);
        writer.write(name);
        writer.endElement("div");

        writer.startElement("div", null);
        writer.writeAttribute("class", Lifecycle.STYLE_CLASS_RESULT + " " + Lifecycle.STYLE_CLASS_SCORE + "-" + getScore(phaseId, phaseInfo.getDuration()), null);
        writer.write(phaseInfo.getDuration() + "ms");
        writer.endElement("div");

        writer.endElement("td");
    }
    
    protected int getScore(PhaseId phaseId, double duration) {
  
        if (phaseId == PhaseId.ANY_PHASE) {
            
            if (duration <= 400) {
                return 100;
            }
            if (duration <= 800) {
                return 66;
            }
            if (duration <= 1200) {
                return 33;
            }
        }
        else if (phaseId == PhaseId.RESTORE_VIEW || phaseId == PhaseId.RENDER_RESPONSE) {
            
            if (duration <= 100) {
                return 100;
            }
            if (duration <= 200) {
                return 66;
            }
            if (duration <= 400) {
                return 33;
            }
        }
        else {
            
            if (duration <= 50) {
                return 100;
            }
            if (duration <= 100) {
                return 66;
            }
            if (duration <= 200) {
                return 33;
            }
        }

        return 0;
    }
}
