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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.AjaxRequestBuilder;
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

        AjaxRequestBuilder builder = PrimeRequestContext.getCurrentInstance(context).getAjaxRequestBuilder();

        String request = builder.init()
                .source(clientId)
                .form(poll, poll)
                .process(component, poll.getProcess())
                .update(component, poll.getUpdate())
                .async(poll.isAsync())
                .global(poll.isGlobal())
                .delay(poll.getDelay())
                .timeout(poll.getTimeout())
                .partialSubmit(poll.isPartialSubmit(), poll.isPartialSubmitSet(), poll.getPartialSubmitFilter())
                .resetValues(poll.isResetValues(), poll.isResetValuesSet())
                .ignoreAutoUpdate(poll.isIgnoreAutoUpdate())
                .onstart(poll.getOnstart())
                .onerror(poll.getOnerror())
                .onsuccess(poll.getOnsuccess())
                .oncomplete(poll.getOncomplete())
                .params(poll)
                .build();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Poll", poll.resolveWidgetVar(), clientId)
                .attr("frequency", poll.getInterval())
                .attr("autoStart", poll.isAutoStart())
                .callback("fn", "function()", request);

        wb.finish();
    }
}
