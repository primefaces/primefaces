/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.component.carousel;

import org.primefaces.event.PageChangeEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.MapBuilder;

import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Collection;
import java.util.Map;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class Carousel extends CarouselBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Carousel";

    public static final String STYLE_CLASS = "ui-carousel ui-widget";
    public static final String CONTENT_CLASS = "ui-carousel-content";
    public static final String CONTAINER_CLASS = "ui-carousel-container";
    public static final String INDICATORS_CONTENT_CLASS = "ui-carousel-indicators ui-reset";
    public static final String HORIZONTAL_CLASS = "ui-carousel-horizontal";
    public static final String VERTICAL_CLASS = "ui-carousel-vertical";
    public static final String HEADER_CLASS = "ui-carousel-header";
    public static final String FOOTER_CLASS = "ui-carousel-footer";
    public static final String PREV_BUTTON_CLASS = "ui-carousel-prev ui-link";
    public static final String PREV_BUTTON_ICON_CLASS = "ui-carousel-prev-icon pi";
    public static final String NEXT_BUTTON_CLASS = "ui-carousel-next ui-link";
    public static final String NEXT_BUTTON_ICON_CLASS = "ui-carousel-next-icon pi";
    public static final String ITEMS_CONTENT_CLASS = "ui-carousel-items-content";
    public static final String ITEMS_CONTAINER_CLASS = "ui-carousel-items-container ui-items-hidden";
    public static final String ITEM_CLASS = "ui-carousel-item";

    private static final String DEFAULT_EVENT = "pageChange";
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put(DEFAULT_EVENT, PageChangeEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (ComponentUtils.isRequestSource(this, context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if (DEFAULT_EVENT.equals(eventName)) {
                String clientId = getClientId(context);
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                int pageValue = Integer.parseInt(params.get(clientId + "_pageValue"));
                PageChangeEvent pageChangeEvent = new PageChangeEvent(this, behaviorEvent.getBehavior(), pageValue);
                pageChangeEvent.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(pageChangeEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }
}