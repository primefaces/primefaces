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
package org.primefaces.component.api;

import java.util.Map;
import java.util.function.Consumer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.Constants;

public interface DialogReturnAware {

    public static final String EVENT_DIALOG_RETURN = "dialogReturn";
    public static final String ATTRIBUTE_DIALOG_RETURN_SCRIPT = "data-dialogreturn";

    default boolean isDialogReturnEvent(FacesEvent event, FacesContext context) {
        if (event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            return eventName.equals(EVENT_DIALOG_RETURN);
        }
        return false;
    }

    default void queueDialogReturnEvent(FacesEvent event, FacesContext context, UIComponent source, Consumer<FacesEvent> queueEvent) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
        Map<String, Object> session = context.getExternalContext().getSessionMap();
        String dcid = params.get(source.getClientId(context) + "_pfdlgcid");
        Object selectedValue = session.get(dcid);
        session.remove(dcid);

        queueEvent.accept(new SelectEvent(source, behaviorEvent.getBehavior(), selectedValue));
    }
}
