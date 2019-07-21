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
package org.primefaces.application;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public interface DialogReturnHolder extends ClientBehaviorHolder, PrimeClientBehaviorHolder {

    String DEFAULT_EVENT = "click";

    Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("dialogReturn", SelectEvent.class)
            .put(DEFAULT_EVENT, null)
            .ibuild();

    Collection<String> EVENT_NAMES = Collections.unmodifiableSet(BEHAVIOR_EVENT_MAPPING.keySet());

    @Override
    default Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    default Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    default String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    default void handleQueueEvent(FacesEvent event, FacesContext context, UIComponent source, Consumer<FacesEvent> queueEvent) {
        if (event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if ("dialogReturn".equals(eventName)) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                Map<String, Object> session = context.getExternalContext().getSessionMap();
                String dcid = params.get(source.getClientId(context) + "_pfdlgcid");
                Object selectedValue = session.get(dcid);
                session.remove(dcid);

                event = new SelectEvent(source, behaviorEvent.getBehavior(), selectedValue);
                queueEvent.accept(event);
            }
            else if (DEFAULT_EVENT.equals(eventName)) {
                queueEvent.accept(event);
            }
        }
        else {
            queueEvent.accept(event);
        }
    }
}
