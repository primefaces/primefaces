/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.timeline.TimelineAddEvent;
import org.primefaces.event.timeline.TimelineDragDropEvent;
import org.primefaces.event.timeline.TimelineLazyLoadEvent;
import org.primefaces.event.timeline.TimelineModificationEvent;
import org.primefaces.event.timeline.TimelineRangeEvent;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineModel;

import java.time.LocalDateTime;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "add", event = TimelineAddEvent.class, description = "Fired when a new item is added to the timeline."),
    @FacesBehaviorEvent(name = "change", event = TimelineModificationEvent.class, description = "Fired when an item is being modified."),
    @FacesBehaviorEvent(name = "changed", event = TimelineModificationEvent.class, description = "Fired when an item has been modified."),
    @FacesBehaviorEvent(name = "edit", event = TimelineModificationEvent.class, description = "Fired when an item is being edited."),
    @FacesBehaviorEvent(name = "delete", event = TimelineModificationEvent.class, description = "Fired when an item is deleted."),
    @FacesBehaviorEvent(name = "select", event = TimelineSelectEvent.class, description = "Fired when an item is selected on the timeline.",
            defaultEvent = true),
    @FacesBehaviorEvent(name = "rangechange", event = TimelineRangeEvent.class, description = "Fired when the visible range is changing."),
    @FacesBehaviorEvent(name = "rangechanged", event = TimelineRangeEvent.class, description = "Fired when the visible range has been changed."),
    @FacesBehaviorEvent(name = "lazyload", event = TimelineLazyLoadEvent.class, description = "Fired when lazy loading is triggered to fetch events."),
    @FacesBehaviorEvent(name = "drop", event = TimelineDragDropEvent.class, description = "Fired when a draggable item is dropped onto the timeline.")
})
public abstract class TimelineBase extends UIComponentBase implements Widget, RTLAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TimelineRenderer";

    public TimelineBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Group content of the timeline.")
    public abstract UIComponent getGroupFacet();

    @Facet(description = "Event title content of the timeline.")
    public abstract UIComponent getEventTitleFacet();

    @Facet(description = "Loading content of the timeline.")
    public abstract UIComponent getLoadingFacet();

    @Facet(description = "Menu content of the timeline.")
    public abstract UIComponent getMenuFacet();

    @Property(description = "Name of the request-scoped variable for underlaying object in the TimelineEvent for each iteration.")
    public abstract String getVar();

    @Property(description = "An instance of TimelineModel representing the backing model.", required = true)
    public abstract TimelineModel<Object, Object> getValue();

    @Property(description = "Name of the request-scoped variable for underlaying object in the TimelineGroup for each iteration.")
    public abstract String getVarGroup();

    @Property(description = "User locale for i18n localization messages. The attribute can be either a String or java.util.Locale object.",
            implicitDefaultValue = "View Locale.")
    public abstract Object getLocale();

    @Property(implicitDefaultValue = "Server's time zone.",
            description = "Target time zone to convert start / end dates of TimelineEvent's in server side."
                    + " The attribute can be either a String, TimeZone object or null.")
    public abstract Object getTimeZone();

    @Property(description = "Time zone the user would like to see dates in UI. The attribute can be either a String or TimeZone object or null.",
            implicitDefaultValue = "Browser's time zone.")
    public abstract Object getClientTimeZone();

    @Property(description = "The height of the timeline in pixels or as a percentage. When height is undefined or null, the height of the timeline is"
            + " automatically adjusted to fit the contents. It is possible to set a maximum height using option maxHeight to prevent the timeline"
            + " from getting too high in case of automatically calculated height.")
    public abstract String getHeight();

    @Property(description = "Specifies a minimum height for the Timeline in pixels.")
    public abstract Integer getMinHeight();

    @Property(description = "Specifies the maximum height for the Timeline in pixels.")
    public abstract Integer getMaxHeight();

    @Property(description = "Specifies the horizontal scrollable.", defaultValue = "false")
    public abstract boolean isHorizontalScroll();

    @Property(description = "Specifies the vertical scrollable.", defaultValue = "false")
    public abstract boolean isVerticalScroll();

    @Property(description = "The width of the timeline in pixels or as a percentage.", defaultValue = "100%")
    public abstract String getWidth();

    @Property(description = "Check if the timeline container is resized, and if so, resize the timeline. Useful when the webpage (browser window) or"
            + " a layout pane / unit containing the timeline component is resized.",
            defaultValue = "true")
    public abstract boolean isResponsive();

    @Property(description = "Orientation of the timeline axis: 'top', 'bottom', 'both', or 'none'. If orientation is 'bottom', the time axis is drawn"
            + " at the bottom. When 'top', the axis is drawn on top. When 'both', two axes are drawn, both on top and at the bottom."
            + " In case of 'none', no axis is drawn at all.", defaultValue = "bottom")
    public abstract String getOrientationAxis();

    @Property(description = "Orientation of the timeline items: 'top' or 'bottom'."
            + " Determines whether items are aligned to the top or bottom of the Timeline.",
            defaultValue = "bottom")
    public abstract String getOrientationItem();

    @Property(description = "If true, the items in the timeline can be manipulated. Only applicable when option selectable is true.",
            defaultValue = "false")
    public abstract boolean isEditable();

    @Property(description = "If true, new items can be created by double tapping an empty space in the Timeline. Takes precedence over editable.",
            defaultValue = "isEditable()")
    public abstract boolean isEditableAdd();

    @Property(description = "If true, items can be deleted by first selecting them, and then clicking the delete button on the top right of the item."
            + " Takes precedence over editable.",
            defaultValue = "isEditable()")
    public abstract boolean isEditableRemove();

    @Property(description = "If true, items can be dragged from one group to another. Only applicable when the Timeline has groups."
            + " Takes precedence over editable.",
            defaultValue = "isEditable()")
    public abstract boolean isEditableGroup();

    @Property(description = "If true, items can be dragged to another moment in time. Takes precedence over editable.",
            defaultValue = "isEditable()")
    public abstract boolean isEditableTime();

    @Property(description = "If true, TimelineEvent specific editables properties are overridden by timeline settings.",
            defaultValue = "false")
    public abstract boolean isEditableOverrideItems();

    @Property(description = "If true, events on the timeline are selectable. Selectable events can fire AJAX \"select\" events.",
            defaultValue = "true")
    public abstract boolean isSelectable();

    @Property(description = "If true, the timeline is zoomable. When the timeline is zoomed, AJAX \"rangechange\" events are fired.",
            defaultValue = "true")
    public abstract boolean isZoomable();

    @Property(description = "If true, the timeline is movable. When the timeline is moved, AJAX \"rangechange\" events are fired.",
            defaultValue = "true")
    public abstract boolean isMoveable();

    @Property(description = "The initial start date for the axis of the timeline."
            + " If not provided, the earliest date present in the events is taken as start date.")
    public abstract LocalDateTime getStart();

    @Property(description = "The initial end date for the axis of the timeline."
            + " If not provided, the latest date present in the events is taken as end date.")
    public abstract LocalDateTime getEnd();

    @Property(description = "Set a minimum Date for the visible range. It will not be possible to move beyond this minimum.")
    public abstract LocalDateTime getMin();

    @Property(description = "Set a maximum Date for the visible range. It will not be possible to move beyond this maximum.")
    public abstract LocalDateTime getMax();

    @Property(description = "Specifies whether the Timeline is only zoomed when an additional key is down."
            + " Available values are '' (does not apply), 'altKey', 'ctrlKey', 'shiftKey' or 'metaKey'. Only applicable when option moveable is set true.")
    public abstract String getZoomKey();

    @Property(description = "Set a minimum zoom interval for the visible range in milliseconds. It will not be possible to zoom in further than this minimum.",
            defaultValue = "10L")
    public abstract Long getZoomMin();

    @Property(description = "Set a maximum zoom interval for the visible range in milliseconds. It will not be possible to zoom out further than this maximum."
            + " Default value equals 315360000000000 ms (about 10000 years).", defaultValue = "315360000000000L")
    public abstract Long getZoomMax();

    @Property(description = "Preload factor is a positive float value or 0 which can be used for lazy loading of events."
            + " When the lazy loading feature is active, the calculated time range for preloading will be multiplicated by the preload factor."
            + " The result of this multiplication specifies the additional time range which will be considered for the preloading during moving / zooming too."
            + " For example, if the calculated time range for preloading is 5 days and the preload factor is 0.2, the result is 5 * 0.2 = 1 day."
            + " That means, 1 day backwards and / or 1 day onwards will be added to the original calculated time range."
            + " The event's area to be preloaded is wider then. This helps to avoid frequently, time-consuming fetching of events.",
            defaultValue = "0.0f")
    public abstract Float getPreloadFactor();

    @Property(description = "The minimal margin in pixels between events.",
            defaultValue = "10")
    public abstract int getEventMargin();

    @Property(description = "The minimal horizontal margin in pixels between items. Takes precedence over eventMargin property.",
            defaultValue = "getEventMargin()")
    public abstract int getEventHorizontalMargin();

    @Property(description = "The minimal vertical margin in pixels between items. Takes precedence over eventMargin property.",
            defaultValue = "getEventMargin()")
    public abstract int getEventVerticalMargin();

    @Property(description = "The minimal margin in pixels between events and the horizontal axis.",
            defaultValue = "10")
    public abstract int getEventMarginAxis();

    @Property(description = "Specifies the default type for the timeline items. Choose from 'box', 'point' and 'range'."
            + " If undefined, the Timeline will auto detect the type from the items data: if a start and end date is available,"
            + " a 'range' will be created, and else, a 'box' is created.")
    public abstract String getEventStyle();

    @Property(description = "Allows to customize the way groups are ordered."
            + " When true (default), groups will be ordered by content alphabetically (when the list of groups is missing)"
            + " or by native ordering of TimelineGroup object in the list of groups (when the list of groups is available)."
            + " When false, groups will not be ordered at all.", defaultValue = "true")
    public abstract boolean isGroupsOrder();

    @Property(description = "A css text string to apply custom styling for an individual group label, for example \"color: red; background-color: pink;\".")
    public abstract String getGroupStyle();

    @Property(description = "When moving items on the Timeline, they will be snapped to nice dates like full hours or days, depending on the current scale."
            + " The snap function can be replaced with a custom javascript function, or can be set to null to disable snapping."
            + " The signature of the snap function is: function snap(date: Date, scale: string, step: number) : Date or number."
            + " The parameter scale can be can be 'millisecond', 'second', 'minute', 'hour', 'weekday, 'week', 'day, 'month, or 'year'."
            + " The parameter step is a number like 1, 2, 4, 5.")
    public abstract String getSnap();

    @Property(description = "If true, the events are stacked above each other to prevent overlapping events.",
            defaultValue = "true")
    public abstract boolean isStackEvents();

    @Property(description = "If true, the timeline shows a red, vertical line displaying the current time.",
            defaultValue = "true")
    public abstract boolean isShowCurrentTime();

    @Property(description = "By default, the timeline shows both minor and major date labels on the horizontal axis."
            + " For example the minor labels show minutes and the major labels show hours. When \"showMajorLabels\" is false, no major labels are shown.",
            defaultValue = "true")
    public abstract boolean isShowMajorLabels();

    @Property(description = "By default, the timeline shows both minor and major date labels on the horizontal axis."
            + " For example the minor labels show minutes and the major labels show hours."
            + " When \"showMinorLabels\" is false, no minor labels are shown."
            + " When both \"showMajorLabels\" and \"showMinorLabels\" are false, no horizontal axis will be visible.",
            defaultValue = "true")
    public abstract boolean isShowMinorLabels();

    @Property(description = "By default, the timeline shows nested groups without collapsed. When \"showNested\" is false,"
            + " all nested groups shown as collapsed. If \"showNested\" is set different in TimelineGroup model, it will override this.",
            defaultValue = "true")
    public abstract boolean isShowNested();

    @Property(description = "When a Timeline is configured to be clickToUse, it will react to mouse and touch events only when active."
            + " When active, a blue shadow border is displayed around the Timeline. The Timeline is set active by clicking on it,"
            + " and is changed to inactive again by clicking outside the Timeline or by pressing the ESC key.",
            defaultValue = "false")
    public abstract boolean isClickToUse();

    @Property(description = "If true, items with titles will display a tooltip. If false, item tooltips are prevented from showing.",
            defaultValue = "true")
    public abstract boolean isShowTooltips();

    @Property(description = "If true, tooltips will follow the mouse as they move around in the item.",
            defaultValue = "false")
    public abstract boolean isTooltipFollowMouse();

    @Property(description = "Set how the tooltip should act if it is about to overflow out of the timeline. Choose from 'cap', 'flip' and 'none'."
            + " If it is set to 'cap', the tooltip will just cap its position to inside to timeline."
            + " If set to 'flip', the position of the tooltip will flip around the cursor so that a corner is at the cursor, and the rest of it is visible."
            + " If set to 'none', the tooltip will be positioned independently of the timeline, so parts of the tooltip could possibly be hidden"
            + " or stick ouf of the timeline, depending how CSS overflow is defined for the timeline (by default it's hidden).",
            defaultValue = "flip")
    public abstract String getTooltipOverflowMethod();

    @Property(description = "Set a value (in ms) that the tooltip is delayed before showing.",
            defaultValue = "500")
    public abstract int getTooltipDelay();

    @Property(description = "Style class to apply when an acceptable draggable is dragged over.")
    public abstract String getDropHoverStyleClass();

    @Property(description = "Style class to apply when an acceptable draggable is being dragged over.")
    public abstract String getDropActiveStyleClass();

    @Property(description = "Selector to define the accepted draggables.")
    public abstract String getDropAccept();

    @Property(description = "Scope key to match draggables and droppables.")
    public abstract String getDropScope();

    @Property(description = "Name of javascript function to extend the options of the underlying timeline javascript component.")
    public abstract String getExtender();

}
