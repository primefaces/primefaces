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

import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.showcase.domain.Order;
import org.primefaces.event.timeline.*;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import org.primefaces.PrimeFaces;
import org.primefaces.component.timeline.TimelineUpdater;

@Named("nestedGroupingTimelineView")
@ViewScoped
public class NestedGroupingTimelineView implements Serializable {

    private TimelineModel<Order, String> model;
    private TimelineEvent<Order> event; // current changed event
    private List<TimelineEvent<Order>> overlappedOrders; // all overlapped orders (events) to the changed order (event)
    private List<TimelineEvent<Order>> ordersToMerge; // selected orders (events) in the dialog which should be merged

    @PostConstruct
    protected void initialize() {

        model = new TimelineModel<>();

        // create nested groups
        TimelineGroup<String> group1 = new TimelineGroup<>("groupId1", "Truck Group Level 1", "groupId1", 1,
                Arrays.asList("groupId2", "id1", "id2", "id5", "id6"));
        TimelineGroup<String> group2 = new TimelineGroup<>("groupId2", "Truck Group Level 2", "groupId2", 2,
                Arrays.asList("id3", "id4"));
        TimelineGroup<String> group3 = new TimelineGroup<>("id1", "Truck 1", 2);
        TimelineGroup<String> group4 = new TimelineGroup<>("id2", "Truck 2", 2);
        TimelineGroup<String> group5 = new TimelineGroup<>("id3", "Truck 3", 3);
        TimelineGroup<String> group6 = new TimelineGroup<>("id4", "Truck 4", 3);
        TimelineGroup<String> group7 = new TimelineGroup<>("id5", "Truck 5", 2);
        TimelineGroup<String> group8 = new TimelineGroup<>("id6", "Truck 6", 2);
        TimelineGroup<String> group9 = new TimelineGroup<>("groupId3", "Truck Group Level 1", "groupId3", 1,
                Arrays.asList("id7", "id8", "id9"));
        TimelineGroup<String> group10 = new TimelineGroup<>("id7", "Truck 7", 2);
        TimelineGroup<String> group11 = new TimelineGroup<>("id8", "Truck 8", 2);
        TimelineGroup<String> group12 = new TimelineGroup<>("id9", "Truck 9", 2);

        // add nested groups to the model
        model.addGroup(group1);
        model.addGroup(group2);
        model.addGroup(group3);
        model.addGroup(group4);
        model.addGroup(group5);
        model.addGroup(group6);
        model.addGroup(group7);
        model.addGroup(group8);
        model.addGroup(group9);
        model.addGroup(group10);
        model.addGroup(group11);
        model.addGroup(group12);

        int orderNumber = 1;

        // iterate over groups
        for (int j = 1; j <= 12; j++) {
            LocalDateTime referenceDate = LocalDateTime.of(2015, Month.DECEMBER, 14, 8, 0);
            // iterate over events in the same group
            for (int i = 0; i < 6; i++) {
                LocalDateTime startDate = referenceDate.plusHours(3 * (Math.random() < 0.2 ? 1 : 0));

                LocalDateTime endDate = startDate.plusHours(2 + (int) Math.floor(Math.random() * 3));

                String imagePath = null;
                if (Math.random() < 0.25) {
                    imagePath = "images/timeline/box.png";
                }

                Order order = new Order(orderNumber, imagePath);
                model.add(TimelineEvent.<Order>builder()
                        .data(order)
                        .startDate(startDate)
                        .endDate(endDate)
                        .editable(true)
                        .group("id" + j)
                        .build());

                orderNumber++;
                referenceDate = endDate;
            }
        }
    }

    public TimelineModel<Order, String> getModel() {
        return model;
    }

    public void onChange(TimelineModificationEvent<Order> e) {
        // get changed event and update the model
        event = e.getTimelineEvent();
        model.update(event);

        // get overlapped events of the same group as for the changed event
        Set<TimelineEvent<Order>> overlappedEvents = model.getOverlappedEvents(event);

        if (overlappedEvents == null) {
            // nothing to merge
            return;
        }

        // list of orders which can be merged in the dialog
        overlappedOrders = new ArrayList<>(overlappedEvents);

        // no pre-selection
        ordersToMerge = null;

        // update the dialog's content and show the dialog
        PrimeFaces primefaces = PrimeFaces.current();
        primefaces.ajax().update("form:overlappedOrdersInner");
        primefaces.executeScript("PF('overlapEventsWdgt').show()");
    }

    public void onDelete(TimelineModificationEvent<Order> e) {
        // keep the model up-to-date
        model.delete(e.getTimelineEvent());
    }

    public void merge() {
        // merge orders and update UI if the user selected some orders to be merged
        if (ordersToMerge != null && !ordersToMerge.isEmpty()) {
            model.merge(event, ordersToMerge, TimelineUpdater.getCurrentInstance(":form:timeline"));
        }
        else {
            FacesMessage msg
                    = new FacesMessage(FacesMessage.SEVERITY_INFO, "Nothing to merge, please choose orders to be merged", null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        overlappedOrders = null;
        ordersToMerge = null;
    }

    public int getSelectedOrder() {
        if (event == null) {
            return 0;
        }

        return event.getData().getNumber();
    }

    public List<TimelineEvent<Order>> getOverlappedOrders() {
        return overlappedOrders;
    }

    public List<TimelineEvent<Order>> getOrdersToMerge() {
        return ordersToMerge;
    }

    public void setOrdersToMerge(List<TimelineEvent<Order>> ordersToMerge) {
        this.ordersToMerge = ordersToMerge;
    }

}
