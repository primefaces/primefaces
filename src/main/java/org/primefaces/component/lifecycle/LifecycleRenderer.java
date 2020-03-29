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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;

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

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Lifecycle", lifecycle.resolveWidgetVar(context), clientId);
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
        writer.writeAttribute("class", Lifecycle.STYLE_CLASS_RESULT + " " + Lifecycle.STYLE_CLASS_SCORE
                + "-" + getScore(phaseId, phaseInfo.getDuration()), null);
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
