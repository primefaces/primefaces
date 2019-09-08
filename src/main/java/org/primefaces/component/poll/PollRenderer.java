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
package org.primefaces.component.poll;

import java.io.IOException;
import java.time.Duration;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class PollRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Poll poll = (Poll) component;

        if (context.getExternalContext().getRequestParameterMap().containsKey(poll.getClientId(context))) {
            ActionEvent event = new ActionEvent(poll);
            if (poll.isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }

            poll.queueEvent(event);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Poll poll = (Poll) component;
        String clientId = poll.getClientId(context);

        String request = preConfiguredAjaxRequestBuilder(context, poll)
                .params(poll)
                .build();

        Object interval = poll.getInterval();

        int convertedInterval;
        if (interval instanceof Integer) {
            convertedInterval = (Integer) interval;
        }
        else if (interval instanceof Duration) {
            convertedInterval = (int) ((Duration) interval).getSeconds();
        }
        else {
            throw new FacesException(interval.getClass() + " is not supported as \"interval\" for p:poll");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Poll", poll.resolveWidgetVar(context), clientId)
                .attr("frequency", convertedInterval)
                .attr("autoStart", poll.isAutoStart())
                .attr("intervalType", poll.getIntervalType(), "second")
                .callback("fn", "function()", request);

        wb.finish();
    }
}
