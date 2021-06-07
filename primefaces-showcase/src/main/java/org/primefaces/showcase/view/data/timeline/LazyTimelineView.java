/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.showcase.view.data.timeline;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.PrimitiveIterator;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.timeline.TimelineUpdater;
import org.primefaces.event.timeline.TimelineLazyLoadEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

@Named
@ViewScoped
public class LazyTimelineView implements Serializable {

    private TimelineModel<String, ?> model;

    private float preloadFactor = 0;
    private long zoomMax;

    @PostConstruct
    protected void initialize() {
        // create empty model
        model = new TimelineModel<>();

        // about five months in milliseconds for zoomMax
        // this can help to avoid a long loading of events when zooming out to wide time ranges
        zoomMax = 1000L * 60 * 60 * 24 * 31 * 5;
    }

    public TimelineModel<String, ?> getModel() {
        return model;
    }

    public void onLazyLoad(TimelineLazyLoadEvent e) {
        try {
            // simulate time-consuming loading before adding new events
            Thread.sleep((long) (1000 * Math.random() + 100));
        }
        catch (InterruptedException ex) {
            // ignore
        }

        TimelineUpdater timelineUpdater = TimelineUpdater.getCurrentInstance(":form:timeline");

        LocalDateTime startDate = e.getStartDateFirst(); // alias getStartDate() can be used too
        LocalDateTime endDate = e.getEndDateFirst(); // alias getEndDate() can be used too

        // fetch events for the first time range
        generateRandomEvents(startDate, endDate, timelineUpdater);

        if (e.hasTwoRanges()) {
            // zooming out ==> fetch events for the second time range
            generateRandomEvents(e.getStartDateSecond(), e.getEndDateSecond(), timelineUpdater);
        }
    }

    private void generateRandomEvents(LocalDateTime startDate, LocalDateTime endDate, TimelineUpdater timelineUpdater) {
        LocalDateTime curDate = startDate;
        Random rnd = new Random();
        PrimitiveIterator.OfInt randomInts = rnd.ints(1, 99999).iterator();

        while (curDate.isBefore(endDate)) {
            // create events in the given time range
            if (rnd.nextBoolean()) {
                // event with only one date
                model.add(TimelineEvent.<String>builder()
                        .data("Event " + randomInts.nextInt())
                        .startDate(curDate)
                        .build(), timelineUpdater);
            }
            else {
                // event with start and end dates
                model.add(TimelineEvent.<String>builder()
                        .data("Event " + randomInts.nextInt())
                        .startDate(curDate)
                        .endDate(curDate.plusHours(18))
                        .build(),
                        timelineUpdater);
            }

            curDate = curDate.plusHours(24);
        }
    }

    public void clearTimeline() {
        // clear Timeline, so that it can be loaded again with a new preload factor
        model.clear();
    }

    public void setPreloadFactor(float preloadFactor) {
        this.preloadFactor = preloadFactor;
    }

    public float getPreloadFactor() {
        return preloadFactor;
    }

    public long getZoomMax() {
        return zoomMax;
    }
}
