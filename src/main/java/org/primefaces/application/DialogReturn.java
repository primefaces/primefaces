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

public interface DialogReturn extends ClientBehaviorHolder, PrimeClientBehaviorHolder {

    Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("dialogReturn", SelectEvent.class)
            .put("click", null)
            .ibuild();

    Collection<String> EVENT_NAMES = Collections.unmodifiableSet(BEHAVIOR_EVENT_MAPPING.keySet());

    String DEFAULT_EVENT = "click";

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

    void queueEvent(FacesEvent event);

    default void handleEvent(FacesEvent event, UIComponent source, Consumer<FacesEvent> queueEvent) {
        FacesContext context = event.getFacesContext();

        if (event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if (eventName.equals("dialogReturn")) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                Map<String, Object> session = context.getExternalContext().getSessionMap();
                String dcid = params.get(source.getClientId(context) + "_pfdlgcid");
                Object selectedValue = session.get(dcid);
                session.remove(dcid);

                event = new SelectEvent(source, behaviorEvent.getBehavior(), selectedValue);
                queueEvent.accept(event);
            }
            else if (eventName.equals("click")) {
                queueEvent.accept(event);
            }
        }
        else {
            queueEvent.accept(event);
        }
    }
}
