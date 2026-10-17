/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.timeline;

import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.lifecycle.Lifecycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class DefaultTimelineUpdaterTest {

    /**
     * The updater patches the widget over JS without re-rendering the timeline, so no encodeEnd follows to clean up
     * after it. It calls encodeGroup and encodeEvent all the same, which put the group and the event in the request
     * map, so it has to clean up after itself.
     */
    @Test
    void processCrudOperationsLeavesNoVarBehind() {
        FacesContext context = newContext();
        Timeline timeline = newTimeline(context);

        DefaultTimelineUpdater updater = new DefaultTimelineUpdater(timeline.getClientId(context), "widget");
        updater.add(TimelineEvent.builder().data("added").group("group").startDate(LocalDateTime.now()).build());
        updater.beforePhase(new PhaseEvent(context, PhaseId.RENDER_RESPONSE, mock(Lifecycle.class)));

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        assertFalse(requestMap.containsKey("event"));
        assertFalse(requestMap.containsKey("group"));
    }

    @Test
    void processCrudOperationsRestoresTheVarOfAnOuterIteration() {
        FacesContext context = newContext();
        Timeline timeline = newTimeline(context);

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        requestMap.put("event", "the row of the outer table");
        requestMap.put("group", "the group of the outer table");

        DefaultTimelineUpdater updater = new DefaultTimelineUpdater(timeline.getClientId(context), "widget");
        updater.add(TimelineEvent.builder().data("added").group("group").startDate(LocalDateTime.now()).build());
        updater.beforePhase(new PhaseEvent(context, PhaseId.RENDER_RESPONSE, mock(Lifecycle.class)));

        assertEquals("the row of the outer table", requestMap.get("event"));
        assertEquals("the group of the outer table", requestMap.get("group"));
    }

    private static FacesContext newContext() {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());
        UIViewRoot viewRoot = new UIViewRoot();
        viewRoot.setLocale(Locale.ENGLISH);
        context.setViewRoot(viewRoot);
        return context;
    }

    private static Timeline newTimeline(FacesContext context) {
        TimelineModel<Object, Object> model = new TimelineModel<>();
        model.addGroup(new TimelineGroup<>("group", "the group"));
        model.add(TimelineEvent.builder().data("one").group("group").startDate(LocalDateTime.now()).build());

        Timeline timeline = new Timeline();
        timeline.setId("timeline");
        timeline.setVar("event");
        timeline.setVarGroup("group");
        timeline.setValue(model);

        context.getViewRoot().getChildren().add(timeline);
        // the updater looks the real renderer up, so the mock render kit has to hand it out
        context.getRenderKit().addRenderer(Timeline.COMPONENT_FAMILY, Timeline.DEFAULT_RENDERER, new TimelineRenderer());

        return timeline;
    }
}
