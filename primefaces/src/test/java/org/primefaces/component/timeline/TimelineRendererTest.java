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
import org.primefaces.renderkit.PrimeRendererWrapper;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TimelineRendererTest {

    /**
     * The timeline puts the event it renders in the request map under its var, and the group under its varGroup. So a
     * render which iterated the events leaves neither behind, and whatever renders after the timeline resolves the var
     * of its own scope.
     */
    @Test
    void encodeEndLeavesNoVarBehind() throws Exception {
        FacesContext context = new FacesContextMock(new CollectingResponseWriter());
        UIViewRoot viewRoot = new UIViewRoot();
        viewRoot.setLocale(Locale.ENGLISH);
        context.setViewRoot(viewRoot);

        TimelineModel<Object, Object> model = new TimelineModel<>();
        model.addGroup(new TimelineGroup<>("group", "the group"));
        model.add(TimelineEvent.builder().data("one").group("group").startDate(LocalDateTime.now()).build());
        model.add(TimelineEvent.builder().data("two").group("group").startDate(LocalDateTime.now()).build());

        Timeline timeline = new Timeline();
        timeline.setId("timeline");
        timeline.setVar("event");
        timeline.setVarGroup("group");
        timeline.setValue(model);

        new PrimeRendererWrapper(new TimelineRenderer()).encodeEnd(context, timeline);

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        assertFalse(requestMap.containsKey("event"));
        assertFalse(requestMap.containsKey("group"));
    }
}
