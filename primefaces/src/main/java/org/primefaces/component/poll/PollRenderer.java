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
package org.primefaces.component.poll;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.time.Duration;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Poll.DEFAULT_RENDERER, componentFamily = Poll.COMPONENT_FAMILY)
public class PollRenderer extends CoreRenderer<Poll> {

    @Override
    public void decode(FacesContext context, Poll component) {
        if (context.getExternalContext().getRequestParameterMap().containsKey(component.getClientId(context))) {
            ActionEvent event = new ActionEvent(component);
            if (component.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            component.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, Poll component) throws IOException {
        String clientId = component.getClientId(context);

        renderDummyMarkup(context, component, clientId);

        String request = preConfiguredAjaxRequestBuilder(context, component)
                .params(component)
                .build();

        Object interval = component.getInterval();

        long convertedInterval;
        if (interval instanceof Number) {
            convertedInterval = ((Number) interval).longValue();
        }
        else if (interval instanceof Duration) {
            convertedInterval = ((Duration) interval).getSeconds();
        }
        else if (interval instanceof String) {
            try {
                convertedInterval = Long.parseLong((String) interval);
            }
            catch (NumberFormatException e) {
                throw new FacesException(interval + " is not a valid long for \"interval\" for p:poll", e);
            }
        }
        else {
            throw new FacesException(interval.getClass() + " is not supported as \"interval\" for p:poll");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Poll", component)
                .attr("frequency", convertedInterval)
                .attr("autoStart", component.isAutoStart())
                .attr("intervalType", component.getIntervalType(), "second")
                .callback("onActivated", "function()", component.getOnactivated())
                .callback("onDeactivated", "function()", component.getOndeactivated())
                .callback("fn", "function()", request);

        wb.finish();
    }
}
