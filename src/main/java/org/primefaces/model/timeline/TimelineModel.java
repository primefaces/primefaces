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
package org.primefaces.model.timeline;

import org.primefaces.component.timeline.TimelineUpdater;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class TimelineModel<E, G> implements Serializable {

    private static final long serialVersionUID = 20130316L;

    /**
     * list of events
     */
    private List<TimelineEvent<E>> events;

    /**
     * list of groups
     */
    private List<TimelineGroup<G>> groups;

    public TimelineModel() {
        events = new ArrayList<>();
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public TimelineModel(List<TimelineEvent<E>> events) {
        this.events = new ArrayList<>();

        if (events != null && !events.isEmpty()) {
            for (TimelineEvent<E> event : events) {
                add(event);
            }
        }
    }

    public TimelineModel(List<TimelineEvent<E>> events, List<TimelineGroup<G>> groups) {
        this(events);
        this.groups = groups;
    }

    /**
     * Adds a given event to the model without UI update.
     *
     * @param event event to be added
     */
    public void add(TimelineEvent<E> event) {
        events.add(event);
    }

    /**
     * Adds a given group to the model.
     *
     * @param group group to be added
     */
    public void addGroup(TimelineGroup<G> group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }

        groups.add(group);
    }

    /**
     * Adds a given event to the model with UI update.
     *
     * @param event           event to be added
     * @param timelineUpdater TimelineUpdater instance to add the event in UI
     */
    public void add(TimelineEvent<E> event, TimelineUpdater timelineUpdater) {
        events.add(event);

        if (timelineUpdater != null) {
            // update UI
            timelineUpdater.add(event);
        }
    }

    /**
     * Adds all given event to the model without UI update.
     *
     * @param events collection of events to be added
     */
    public void addAll(Collection<TimelineEvent<E>> events) {
        addAll(events, null);
    }

    /**
     * Adds all given groups to the model.
     *
     * @param groups collection of groups to be added
     */
    public void addAllGroups(Collection<TimelineGroup<G>> groups) {
        if (this.groups == null) {
            this.groups = new ArrayList<>();
        }

        this.groups.addAll(groups);
    }

    /**
     * Adds all given events to the model with UI update.
     *
     * @param events          collection of events to be added
     * @param timelineUpdater TimelineUpdater instance to add the events in UI
     */
    public void addAll(Collection<TimelineEvent<E>> events, TimelineUpdater timelineUpdater) {
        if (events != null && !events.isEmpty()) {
            for (TimelineEvent<E> event : events) {
                add(event, timelineUpdater);
            }
        }
    }

    /**
     * Updates a given event in the model without UI update.
     *
     * @param event event to be updated
     */
    public void update(TimelineEvent<E> event) {
        update(event, null);
    }

    /**
     * Updates a given event in the model with UI update.
     *
     * @param event           event to be added
     * @param timelineUpdater TimelineUpdater instance to update the event in UI
     */
    public void update(TimelineEvent<E> event, TimelineUpdater timelineUpdater) {
        int index = getIndex(event);
        if (index >= 0) {
            events.set(index, event);

            if (timelineUpdater != null) {
                // update UI
                timelineUpdater.update(event);
            }
        }
    }

    /**
     * Updates all given events in the model without UI update.
     *
     * @param events collection of events to be updated
     */
    public void updateAll(Collection<TimelineEvent<E>> events) {
        updateAll(events, null);
    }

    /**
     * Updates all given events in the model with UI update.
     *
     * @param events          collection of events to be updated
     * @param timelineUpdater TimelineUpdater instance to update the events in UI
     */
    public void updateAll(Collection<TimelineEvent<E>> events, TimelineUpdater timelineUpdater) {
        if (events != null && !events.isEmpty()) {
            for (TimelineEvent<E> event : events) {
                update(event, timelineUpdater);
            }
        }
    }

    /**
     * Deletes a given event in the model without UI update.
     *
     * @param event event to be deleted
     */
    public void delete(TimelineEvent<E> event) {
        delete(event, null);
    }

    /**
     * Deletes a given event in the model with UI update.
     *
     * @param event           event to be deleted
     * @param timelineUpdater TimelineUpdater instance to delete the event in UI
     */
    public void delete(TimelineEvent<E> event, TimelineUpdater timelineUpdater) {
        events.remove(event);

        if (timelineUpdater != null) {
            // update UI
            timelineUpdater.delete(event.getId());
        }
    }

    /**
     * Deletes all given events in the model without UI update.
     *
     * @param events collection of events to be deleted
     */
    public void deleteAll(Collection<TimelineEvent<E>> events) {
        deleteAll(events, null);
    }

    /**
     * Deletes all given events in the model with UI update.
     *
     * @param events          collection of events to be deleted
     * @param timelineUpdater TimelineUpdater instance to delete the events in UI
     */
    public void deleteAll(Collection<TimelineEvent<E>> events, TimelineUpdater timelineUpdater) {
        if (events != null && !events.isEmpty()) {
            for (TimelineEvent<E> event : events) {
                delete(event, timelineUpdater);
            }
        }
    }

    /**
     * Selects a given event in UI visually. To unselect all events, pass a null as event.
     *
     * @param event           event to be selected
     * @param timelineUpdater TimelineUpdater instance to select the event in UI
     */
    public void select(TimelineEvent<E> event, TimelineUpdater timelineUpdater) {
        if (timelineUpdater != null) {
            // update UI
            timelineUpdater.select(event != null ? event.getId() : null);
        }
    }

    /**
     * Clears the timeline model without UI update (no events are available after that)
     */
    public void clear() {
        events.clear();
    }

    /**
     * Clears the timeline model with UI update (no events are available after that)
     *
     * @param timelineUpdater TimelineUpdater instance to clear the timeline in UI
     */
    public void clear(TimelineUpdater timelineUpdater) {
        events.clear();

        if (timelineUpdater != null) {
            // update UI
            timelineUpdater.clear();
        }
    }

    /**
     * Gets all overlapped events to the given one. The given and overlapped events belong to the same group. Events are ordered
     * by their start dates - first events with more recent start dates and then events with older start dates. If start dates are
     * equal, events will be ordered by their end dates. In this case, if an event has a null end date, it is ordered before the
     * event with a not null end date.
     *
     * @param event given event
     * @return TreeSet&lt;TimelineEvent&lt;E&gt;&gt; ordered overlapped events or null if no overlapping exist
     */
    public Set<TimelineEvent<E>> getOverlappedEvents(TimelineEvent<E> event) {
        if (event == null) {
            return Collections.emptySet();
        }

        List<TimelineEvent<E>> overlappedEvents = null;
        for (TimelineEvent<E> e : events) {
            if (e.equals(event)) {
                // given event should not be included
                continue;
            }

            if (event.getGroup() == null && e.getGroup() != null
                    || (event.getGroup() != null && !event.getGroup().equals(e.getGroup()))) {
                // ignore different groups
                continue;
            }

            if (isOverlapping(event, e)) {
                if (overlappedEvents == null) {
                    overlappedEvents = new ArrayList<>();
                }

                overlappedEvents.add(e);
            }
        }

        if (overlappedEvents == null) {
            return Collections.emptySet();
        }

        // order overlapped events according to their start / end dates
        TreeSet<TimelineEvent<E>> orderedOverlappedEvents = new TreeSet<>(new TimelineEventComparator());
        orderedOverlappedEvents.addAll(overlappedEvents);

        return orderedOverlappedEvents;
    }

    /**
     * Merge the given one event with the given collection of events without UI update. Only events within one group can be
     * merged. Note: after merging, the merged event will get the same properties as the given one event except start and end
     * dates.
     *
     * @param event  given event to be merged with collection of events
     * @param events collection of events
     * @return TimelineEvent result event after merging
     * @throws IllegalStateException thrown if not all events are within the same group
     */
    public TimelineEvent<E> merge(TimelineEvent<E> event, Collection<TimelineEvent<E>> events) {
        return merge(event, events, null);
    }

    /**
     * Merge the given one event with the given collection of events with UI update. Only events within one group can be merged.
     * Note: after merging, the merged event will get the same properties as the given one event except start and end dates.
     *
     * @param event           given event to be merged with collection of events
     * @param events          collection of events
     * @param timelineUpdater TimelineUpdater instance to update the merged events in UI
     * @return TimelineEvent result event after merging
     * @throws IllegalStateException thrown if not all events are within the same group
     */
    public TimelineEvent<E> merge(TimelineEvent<E> event, Collection<TimelineEvent<E>> events, TimelineUpdater timelineUpdater) {
        if (event == null) {
            // nothing to merge
            return null;
        }

        if (events == null || events.isEmpty()) {
            // nothing to merge
            return event;
        }

        // check whether all events within the same group
        String group = event.getGroup();
        for (TimelineEvent<E> e : events) {
            if ((group == null && e.getGroup() != null) || (group != null && !group.equals(e.getGroup()))) {
                throw new IllegalStateException("Events to be merged may be only belong to one and the same group!");
            }
        }

        // order events according to their start / end dates
        TreeSet<TimelineEvent<E>> orderedEvents = new TreeSet<>(new TimelineEventComparator());
        orderedEvents.add(event);
        orderedEvents.addAll(events);

        // find the largest end date
        LocalDateTime endDate = orderedEvents.stream()
                .map(TimelineEvent::getEndDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        TimelineEvent<E> mergedEvent = new TimelineEvent<>(event.getData(), orderedEvents.first().getStartDate(), endDate, event.isEditable(),
                        event.getGroup(), event.getStyleClass());

        // merge...
        deleteAll(events, timelineUpdater);
        update(mergedEvent, timelineUpdater);

        return mergedEvent;
    }

    /**
     * Gets all events in this model
     *
     * @return List&lt;TimelineEvent&lt;E&gt;&gt; list of events
     */
    public List<TimelineEvent<E>> getEvents() {
        return events;
    }

    /**
     * Sets events into this model
     *
     * @param events List&lt;TimelineEvent&lt;E&gt;&gt; list of events
     */
    public void setEvents(List<TimelineEvent<E>> events) {
        this.events = events;
    }

    /**
     * Gets all groups in this model
     *
     * @return List&lt;TimelineGroup&lt;G&gt;&gt; list of groups
     */
    public List<TimelineGroup<G>> getGroups() {
        return groups;
    }

    /**
     * Sets groups into this model
     *
     * @param groups List&lt;TimelineGroup&lt;G&gt;&gt; list of groups
     */
    public void setGroups(List<TimelineGroup<G>> groups) {
        this.groups = groups;
    }

    /**
     * Gets event by its id property.
     *
     * @param id id property of the event
     * @return TimelineEvent found event or null
     */
    public TimelineEvent<E> getEvent(String id) {
        if (id != null) {
            return events.stream()
                    .filter(event -> id.equals(event.getId()))
                    .findAny()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Check if current model has the event passed as parameter.
     * @param event
     * @return
     */
    public boolean hasEvent(TimelineEvent<E> event) {
        return events.contains(event);
    }

    /**
     * Gets event by its positional index according to this instance.
     *
     * @param index index
     * @return TimelineEvent found event or null
     * @deprecated get event by id using {@link #getEvent(java.lang.String)} instead.
     */
    @Deprecated
    public TimelineEvent<E> getEvent(int index) {
        if (index < 0) {
            return null;
        }

        return events.get(index);
    }

    /**
     * Gets index of the given timeline event
     *
     * @param event given event
     * @return int positive index or -1 if the given event is not available in the timeline
     * @deprecated The index property is no longer necessary with the addition of {@link TimelineEvent#getId()} property.
     * This method could be changed to private in next releases.
     * See <a href='https://github.com/primefaces/primefaces/issues/5143'>issue 5143</a> for more information.
     */
    @Deprecated
    public int getIndex(TimelineEvent<E> event) {
        int index = -1;

        if (event != null) {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).equals(event)) {
                    index = i;

                    break;
                }
            }
        }

        return index;
    }

    private boolean isOverlapping(TimelineEvent<E> event1, TimelineEvent<E> event2) {
        if (event1.getEndDate() == null && event2.getEndDate() == null) {
            return event1.getStartDate().equals(event2.getStartDate());
        }
        else if (event1.getEndDate() == null && event2.getEndDate() != null) {
            return (event1.getStartDate().equals(event2.getStartDate()) || event1.getStartDate().equals(event2.getEndDate())
                    || (event1.getStartDate().isAfter(event2.getStartDate()) && event1.getStartDate().isBefore(event2.getEndDate())));
        }
        else if (event1.getEndDate() != null && event2.getEndDate() == null) {
            return (event2.getStartDate().equals(event1.getStartDate()) || event2.getStartDate().equals(event1.getEndDate())
                    || (event2.getStartDate().isAfter(event1.getStartDate()) && event2.getStartDate().isBefore(event1.getEndDate())));
        }
        else {
            // check with ODER if
            // 1. start date of the event 1 is within the event 2
            // 2. end date of the event 1 is within the event 2
            // 3. event 2 is completely strong within the event 1
            return (event1.getStartDate().equals(event2.getStartDate()) || event1.getStartDate().equals(event2.getEndDate())
                    || (event1.getStartDate().isAfter(event2.getStartDate()) && event1.getStartDate().isBefore(event2.getEndDate())))
                    || (event1.getEndDate().equals(event2.getStartDate()) || event1.getEndDate().equals(event2.getEndDate())
                    || (event1.getEndDate().isAfter(event2.getStartDate()) && event1.getEndDate().isBefore(event2.getEndDate())))
                    || (event1.getStartDate().isBefore(event2.getStartDate()) && event1.getEndDate().isAfter(event2.getEndDate()));
        }
    }
}
