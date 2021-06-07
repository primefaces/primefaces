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

import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;

@Named("basicTimelineView")
@ViewScoped
public class BasicTimelineView implements Serializable {

    private TimelineModel<String, ?> model;

    private boolean selectable = true;
    private boolean zoomable = true;
    private boolean moveable = true;
    private boolean stackEvents = true;
    private String eventStyle = "box";
    private boolean showCurrentTime = true;
    private boolean showNavigation = false;

    @PostConstruct
    protected void initialize() {
        model = new TimelineModel<>();

        model.add(TimelineEvent.<String>builder().data("PrimeUI 1.1").startDate(LocalDate.of(2014, 6, 12)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeFaces 5.1.3").startDate(LocalDate.of(2014, 10, 11)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeUI 2.2").startDate(LocalDate.of(2015, 12, 8)).build());
        model.add(TimelineEvent.<String>builder().data("Sentinel-Layout 1.1").startDate(LocalDate.of(2015, 3, 10)).build());
        model.add(TimelineEvent.<String>builder().data("Spark-Layout 1.0").startDate(LocalDate.of(2015, 4, 3)).build());
        model.add(TimelineEvent.<String>builder().data("Ronin-Layout 1.0").startDate(LocalDate.of(2015, 5, 15)).build());
        model.add(TimelineEvent.<String>builder().data("Modena-Layout 1.0").startDate(LocalDate.of(2015, 7, 10)).build());
        model.add(TimelineEvent.<String>builder().data("Rio-Layout 1.0").startDate(LocalDate.of(2015, 6, 15)).build());
        model.add(TimelineEvent.<String>builder().data("Adamantium-Layout 1.0").startDate(LocalDate.of(2015, 9, 4)).build());
        model.add(TimelineEvent.<String>builder().data("Titan-Layout 1.0").startDate(LocalDate.of(2015, 12, 14)).build());
        model.add(TimelineEvent.<String>builder().data("Volt-Layout 1.0").startDate(LocalDate.of(2015, 10, 12)).build());
        model.add(TimelineEvent.<String>builder().data("Atlas-Layout 1.0").startDate(LocalDate.of(2016, 1, 28)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeUI 4.1.0").startDate(LocalDate.of(2016, 2, 24)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeFaces 5.3.8").startDate(LocalDate.of(2016, 2, 29)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeNG 0.5").startDate(LocalDate.of(2016, 2, 29)).build());
    }

    public void onSelect(TimelineSelectEvent<String> e) {
        TimelineEvent<String> timelineEvent = e.getTimelineEvent();

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected event:", timelineEvent.getData());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public TimelineModel<String, ?> getModel() {
        return model;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isZoomable() {
        return zoomable;
    }

    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    public boolean isStackEvents() {
        return stackEvents;
    }

    public void setStackEvents(boolean stackEvents) {
        this.stackEvents = stackEvents;
    }

    public String getEventStyle() {
        return eventStyle;
    }

    public void setEventStyle(String eventStyle) {
        this.eventStyle = eventStyle;
    }

    public boolean isShowCurrentTime() {
        return showCurrentTime;
    }

    public void setShowCurrentTime(boolean showCurrentTime) {
        this.showCurrentTime = showCurrentTime;
    }

    public boolean isShowNavigation() {
        return showNavigation;
    }

    public void setShowNavigation(boolean showNavigation) {
        this.showNavigation = showNavigation;
    }
}
