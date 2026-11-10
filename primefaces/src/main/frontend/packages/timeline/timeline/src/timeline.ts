import { Timeline as VisTimeline, type DataGroup, type DataItem, type DateType, type IdType, type TimelineAnimationOptions,
    type TimelineItem, type TimelineItemEditableType, type TimelineWindow } from "vis-timeline/esnext";
import { DataSet } from "vis-data/esnext";
import * as VisUtil from "vis-util/esnext";

/** The options of a droppable, see {@link PrimeFaces.interactions.droppable}. */
type DroppableOptions = NonNullable<Parameters<typeof PrimeFaces.interactions.droppable>[1]>;

/**
 * The configuration for the {@link Timeline} widget.
 */
export interface TimelineCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** `accept` option for the droppable overlay when using drag&drop. */
    accept: string;
    /** Active style class for the droppable overlay when using drag&drop. */
    activeClass: string;
    /** The currently shown time. */
    currentTime: Date;
    /** The events (items) for the vis-timeline data set. */
    data: DataItem[];
    /**
     * An extender function that may be used modify the settings of this configuration object. The {@link opts} are
     * passed directly to vis-timeline.
     */
    extender: PrimeFacesTypes.widget.Timeline.TimelineExtender;
    /** Optional groups for the events. */
    groups: DataGroup[];
    /** Hover style class for the droppable overlay when using drag&drop. */
    hoverClass: string;
    /** Whether a menu should be present for the timeline. */
    isMenuPresent: boolean;
    /** The options for the vis timeline. */
    opts: PrimeFacesTypes.widget.Timeline.TimelineOptions;
    /**
     * Preload factor is a positive float value or 0 which can be used for lazy loading of events. When the lazy loading
     * feature is active, the calculated time range for preloading will be multiplied by the preload factor. The result
     * of this multiplication specifies the additional time range which will be considered for the preloading during
     * moving / zooming too. For example, if the calculated time range for preloading is 5 days and the preload factor
     * is `0.2`, the result is `5 * 0.2 = 1` day. That means, 1 day backwards and 1 day onwards will be added to the
     * original calculated time range. The event's area to be preloaded is wider then. This helps to avoid frequently,
     * time-consuming fetching of events. Default value is `0`.
     */
    preloadFactor: number;
    /** `scope` option for the droppable overlay when using drag&drop. */
    scope: string;
}

/**
 * __PrimeFaces Timeline Widget__
 *
 * Timeline is an interactive graph to visualize events in time. Currently uses
 * [vis-timeline](https://github.com/visjs/vis-timeline).
 */
export class Timeline<Cfg extends TimelineCfg = TimelineCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The configuration, the server always renders `data`, `opts` and `preloadFactor`. */
    declare cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg> & Pick<TimelineCfg, "data" | "opts" | "preloadFactor">;

    /** Callback that is invoked when an event was added to this timeline. */
    addCallback: PrimeFacesTypes.widget.Timeline.AddCallbackCallback | null = null;
    /** Callback for when an item of the timeline has changed or was moved. */
    changedCallback: PrimeFacesTypes.widget.Timeline.ChangedCallbackCallback | null = null;
    /** Callback for when an item of the timeline was deleted. */
    deleteCallback: PrimeFacesTypes.widget.Timeline.DeleteCallbackCallback | null = null;
    /** When the timeline is move to the right or left, whether that move was initiated by the user. */
    initiatedByUser?: boolean;
    /** The current vis-timeline instance. */
    instance: PrimeFacesTypes.widget.Timeline.VisTimelineInstance | null = null;
    /** Whether the lazy loading feature is enabled, which loads events dynamically via AJAX. */
    lazy: boolean = false;
    /** If restricting the timeline to a certain range, the upper bound. */
    max: number | null = null;
    /** If restricting the timeline to a certain range, the lower bound. */
    min: number | null = null;
    /** The current preload factor, see {@link TimelineCfg.preloadFactor}. */
    pFactor: number = 0;
    /** Time range of the events that were loaded, set only when lazy loading is enabled. */
    rangeLoadedEvents!: PrimeFacesTypes.widget.Timeline.TimeRange;
    /** The context menu widget to use for the timeline, until the timeline was rendered. */
    contextMenuWidget: PrimeFacesTypes.widget.ContextMenuLikeWidget | null = null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg = cfg as typeof this.cfg;
        this.id = cfg.id;

        this.lazy = this.hasBehavior("lazyload");
        if (this.lazy) {
            this.min = (typeof this.cfg.opts.min !== 'undefined' ? this.cfg.opts.min.getTime() : null);
            this.max = (typeof this.cfg.opts.max !== 'undefined' ? this.cfg.opts.max.getTime() : null);
            this.pFactor = this.cfg.preloadFactor;

            this.rangeLoadedEvents = {
                start: null,
                end: null
            };
        }

        if (this.cfg.isMenuPresent) {
            this.cfg.opts.onInitialDrawComplete = () => {
                const el = document.getElementById(this.getId());
                $(el!).find(".timeline-menu").show();
            };
        }

        if (this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.renderDeferred();
    }

    /**
     * Creates timeline widget with all initialization steps.
     */
    protected override _render(): void {
        // instantiate a timeline object
        const el = document.getElementById(this.getId())!;
        const items = new DataSet(this.cfg.data);

        // #7217 must allow HTML in elements
        this.cfg.opts.xss = {
            disabled: true
        };

        // bind items events
        this._bindItemsEvents();
        // the internals are not part of the type declarations of vis-timeline
        if (this.cfg.groups) {
            this.instance = new VisTimeline(el, items, new DataSet(this.cfg.groups), this.cfg.opts) as PrimeFacesTypes.widget.Timeline.VisTimelineInstance;
        } else {
            this.instance = new VisTimeline(el, items, this.cfg.opts) as PrimeFacesTypes.widget.Timeline.VisTimelineInstance;
        }

        if (this.cfg.currentTime) {
            this.instance.setCurrentTime(this.cfg.currentTime);
        }

        // bind timeline events
        this._bindTimelineEvents(el);

        // bind context menu
        if (this.contextMenuWidget) {
            this.bindContextMenu(this.contextMenuWidget, this, this.getId(), this.cfg);
            this.contextMenuWidget = null;
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        // clean up memory
        if (this.instance) {
            this.instance.destroy();
            this.instance = null;
        }

        super.refresh(cfg);
    }

    override destroy(): void {
        super.destroy();

        // clean up memory
        if (this.instance) {
            this.instance.destroy();
            this.instance = null;
        }
    }

    /**
     * Sets up all event listeners for the timeline items.
     */
    private _bindItemsEvents(): void {
        // "add" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable.add && this.hasBehavior("add")) {
            this.cfg.opts.onAdd =
                (item, callback) => {
                    const params: PrimeFacesTypes.ajax.RequestParameter[] = [];
                    if (!item.id) {
                        //item.undefined until https://github.com/visjs/vis-timeline/issues/97 is fixed.
                        item.id = (item as TimelineItem & { undefined: IdType }).undefined;
                    }

                    params.push({
                        name: this.id + '_id',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: (item.start as Date).toISOString()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: (item.end as Date).toISOString()
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }

                    this.addCallback = callback;

                    this.callBehavior("add", { params: params, item: item, callback: callback } as PrimeFacesTypes.widget.Timeline.BehaviorExtender);

                    if (this.addCallback) {
                        this.addCallback(item);
                        this.addCallback = null;
                    }
                };
        }

        // "change" event
        if (this.cfg.opts.selectable && (this.cfg.opts.editable.updateTime || this.cfg.opts.editable.updateGroup) && this.hasBehavior("change")) {
            this.cfg.opts.onMoving =
                (item, callback) => {
                    const params: PrimeFacesTypes.ajax.RequestParameter[] = [];
                    params.push({
                        name: this.id + '_eventId',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: (item.start as Date).toISOString()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: (item.end as Date).toISOString()
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }

                    this.callBehavior("change", { params: params, item: item, callback: callback } as PrimeFacesTypes.widget.Timeline.BehaviorExtender);
                    callback(item);
                };
        }

        // "changed" event
        if (this.cfg.opts.selectable && (this.cfg.opts.editable.updateTime || this.cfg.groups && this.cfg.opts.editable.updateGroup) && this.hasBehavior("changed")) {
            this.cfg.opts.onMove =
                (item, callback) => {
                    const params: PrimeFacesTypes.ajax.RequestParameter[] = [];
                    params.push({
                        name: this.id + '_eventId',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: (item.start as Date).toISOString()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: (item.end as Date).toISOString()
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }

                    this.changedCallback = callback;
                    this.callBehavior("changed", { params: params, item: item, callback: callback } as PrimeFacesTypes.widget.Timeline.BehaviorExtender);
                    if (this.changedCallback) {
                        this.changedCallback(item);
                        this.changedCallback = null;
                    }
                };
        }

        // "edit" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable.updateTime && this.hasBehavior("edit")) {
            this.cfg.opts.onUpdate =
                (item, callback) => {
                    const options = {
                        params: [
                            { name: this.id + '_eventId', value: item.id }
                        ],
                        item: item,
                        callback: callback
                    };

                    this.changedCallback = callback;
                    this.callBehavior("edit", options);
                    if (this.changedCallback) {
                        this.changedCallback(item);
                        this.changedCallback = null;
                    }
                };
        }

        // "delete" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable.remove && this.hasBehavior("delete")) {
            this.cfg.opts.onRemove =
                (item, callback) => {
                    const options = {
                        params: [
                            { name: this.id + '_eventId', value: item.id }
                        ],
                        item: item,
                        callback: callback
                    };

                    this.deleteCallback = callback;
                    this.callBehavior("delete", options);
                    if (this.deleteCallback) {
                        this.deleteCallback(item);
                        this.deleteCallback = null;
                    }
                };
        }
    }

    /**
     * Sets up all event listeners for the timeline's events.
     * @param el Main element of this widget.
     */
    private _bindTimelineEvents(el: HTMLElement): void {
        // "select" event
        if (this.cfg.opts.selectable && this.hasBehavior("select")) {
            this.instance!.on('select', () => {
                const selectedId = this.getSelectedId();
                if (!selectedId) {
                    return;
                }

                const options = {
                    params: [
                        { name: this.id + '_eventId', value: selectedId }
                    ]
                };

                this.callBehavior("select", options);
            });
        }

        // "rangechange" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.hasBehavior("rangechange")) {
            this.instance!.on('rangechange', (properties: PrimeFacesTypes.widget.Timeline.RangeChangeProperties) => {
                const options = {
                    params: [
                        { name: this.id + '_startDate', value: properties.start.toISOString() },
                        { name: this.id + '_endDate', value: properties.end.toISOString() }
                    ],
                    properties: properties
                };

                this.callBehavior("rangechange", options);
            });
        }

        // "rangechanged" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.hasBehavior("rangechanged")) {
            this.instance!.on('rangechanged', (properties: PrimeFacesTypes.widget.Timeline.RangeChangeProperties) => {
                const options = {
                    params: [
                        { name: this.id + '_startDate', value: properties.start.toISOString() },
                        { name: this.id + '_endDate', value: properties.end.toISOString() }
                    ],
                    properties: properties
                };

                // #5455/#6902 only fire if initiated by user
                if (properties.byUser || this.initiatedByUser) {
                    this.initiatedByUser = false;
                    this.callBehavior("rangechanged", options);
                }
            });
        }

        // "lazyload" event
        if (this.lazy) {
            // initial page load
            this.fireLazyLoading();

            // moving / zooming
            this.instance!.on('rangechanged', () => {
                this.fireLazyLoading();
            });
        }

        // register this timeline as droppable if needed
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.hasBehavior("drop")) {
            const droppableOpts: DroppableOptions = { tolerance: "pointer" };
            if (this.cfg.hoverClass) {
                droppableOpts.hoverClass = this.cfg.hoverClass;
            }

            if (this.cfg.activeClass) {
                droppableOpts.activeClass = this.cfg.activeClass;
            }

            if (this.cfg.accept) {
                droppableOpts.accept = this.cfg.accept;
            }

            if (this.cfg.scope) {
                droppableOpts.scope = this.cfg.scope;
            }

            droppableOpts.drop = (evt, ui: { draggable: JQuery }) => {
                const inst = this.getInstance()!;

                const x = evt.pageX! - VisUtil.getAbsoluteLeft(inst.dom.center);

                const xstart = inst._toTime(x);
                const xend = inst._toTime(x + inst.dom.container.clientWidth / 10); // add 10% of timeline width

                const snap = inst.itemSet.options.snap || null;
                const scale = inst.body.util.getScale();
                const step = inst.body.util.getStep();
                const snappedStart = snap ? new Date(snap(xstart, scale, step)) : xstart;
                const snappedEnd = snap ? new Date(snap(xend, scale, step)) : xend;

                const params: PrimeFacesTypes.ajax.RequestParameter[] = [];
                params.push({
                    name: this.id + '_startDate',
                    value: snappedStart.toISOString()
                });

                params.push({
                    name: this.id + '_endDate',
                    value: snappedEnd.toISOString()
                });

                const group = inst.itemSet.groupFromTarget(evt); // (group may be undefined)

                if (group) {
                    params.push({
                        name: this.id + '_group',
                        value: group.groupId
                    });
                }

                params.push({
                    name: this.id + '_dragId',
                    value: ui.draggable.attr('id')
                });

                // check if draggable is within a data iteration component
                const uiData = ui.draggable.closest(".p-datatable, .p-datagrid, .p-datalist, .p-carousel, .p-treetable");
                if (uiData.length > 0) {
                    params.push({
                        name: this.id + '_uiDataId',
                        value: uiData.attr('id')
                    });
                }

                // call the drop listener
                // parameters event and ui can be accessible in "onstart" (p:ajax) via cfg.ext.event and cfg.ext.ui
                // or in "execute" (pe:javascript) via ext.event and ext.ui
                this.callBehavior("drop", { params: params, event: evt, ui: ui } as PrimeFacesTypes.widget.Timeline.BehaviorExtender);
            };

            // make the timeline droppable
            PrimeFaces.interactions.droppable($(el), droppableOpts);
        }
    }

    override bindContextMenu(
        menuWidget: PrimeFacesTypes.widget.ContextMenuLikeWidget,
        targetWidget: this,
        targetId: string,
        cfg: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.ContextMenuLikeWidgetCfg>,
    ): void {
        // if the timeline is not yet initialized, store the menu widget and bind later
        if (!targetWidget.instance) {
            this.contextMenuWidget = menuWidget;
            return;
        }

        targetWidget.instance.on('contextmenu', function (properties: { event: Event }) {
            // the native event of vis-timeline
            menuWidget.show(properties.event as unknown as JQuery.TriggeredEvent);
            properties.event.preventDefault();
        });
    }

    /**
     * Retrieves the array of current data (events) as an JSON string. This method is useful when you done some changes
     * in timeline and want to send them to server to update the backing model (with `pe:remoteCommand` and
     * `pe:convertTimelineEvents`).
     *
     * @returns A JSON string with the current data.
     */
    getData(): string {
        const newData = this.instance!.itemsData.get().map((item) => {
            const newItem: Record<string, unknown> = {};
            if (item.hasOwnProperty('content')) {
                newItem['data'] = item.content;
            }

            if (item.hasOwnProperty('start')) {
                newItem['startDate'] = (item.start as Date).getTime();
            }

            if (item.hasOwnProperty('end') && (item.end != null)) {
                newItem['endDate'] = (item.end as Date).getTime();
            } else {
                newItem['endDate'] = null;
            }

            if (item.hasOwnProperty('editable')) {
                newItem['editable'] = item.editable;
            } else {
                newItem['editable'] = null;
            }

            if (item.hasOwnProperty('group')) {
                newItem['group'] = item.group;
            } else {
                newItem['group'] = null;
            }

            if (item.hasOwnProperty('className')) {
                newItem['styleClass'] = item.className;
            } else {
                newItem['styleClass'] = null;
            }

            return newItem;
        });

        return JSON.stringify(newData);
    }

    /**
     * Fires event for lazy loading.
     */
    private fireLazyLoading(): void {
        const range = this.getLazyLoadRange();
        if (range == null) {
            // don't send event
            return;
        }

        const options: { params: PrimeFacesTypes.ajax.RequestParameter[] } = {
            params: []
        };

        if (range.startFirst != null && range.endFirst != null) {
            options.params[0] = { name: this.id + '_startDateFirst', value: new Date(range.startFirst).toISOString() };
            options.params[1] = { name: this.id + '_endDateFirst', value: new Date(range.endFirst).toISOString() };
        }

        if (range.startSecond != null && range.endSecond != null) {
            options.params[2] = { name: this.id + '_startDateSecond', value: new Date(range.startSecond).toISOString() };
            options.params[3] = { name: this.id + '_endDateSecond', value: new Date(range.endSecond).toISOString() };
        }

        this.callBehavior("lazyload", options);
    }

    /**
     * Gets time range(s) for events to be lazy loaded.
     *
     * The internal time range for already loaded events will be updated.
     *
     * @returns The time range(s) for events to be lazy loaded.
     */
    private getLazyLoadRange(): PrimeFacesTypes.widget.Timeline.TimelineBiRange | null {
        const visibleRange = this.instance!.getWindow();

        if (this.rangeLoadedEvents.start == null || this.rangeLoadedEvents.end == null) {
            // initial load
            const pArea = (visibleRange.end.getTime() - visibleRange.start.getTime()) * this.pFactor;
            this.rangeLoadedEvents.start = Math.round(visibleRange.start.getTime() - pArea);
            this.rangeLoadedEvents.end = Math.round(visibleRange.end.getTime() + pArea);

            if (this.min != null && this.rangeLoadedEvents.start < this.min) {
                this.rangeLoadedEvents.start = this.min;
            }

            if (this.max != null && this.rangeLoadedEvents.end > this.max) {
                this.rangeLoadedEvents.end = this.max;
            }

            return {
                startFirst: this.rangeLoadedEvents.start,
                endFirst: this.rangeLoadedEvents.end,
                startSecond: null,
                endSecond: null
            };
        }

        if ((visibleRange.end.getTime() > this.rangeLoadedEvents.end) &&
            (visibleRange.start.getTime() >= this.rangeLoadedEvents.start)) {
            // moving right
            const startFirstR = this.rangeLoadedEvents.end + 1;
            this.rangeLoadedEvents.end = Math.round(visibleRange.end.getTime() +
                (visibleRange.end.getTime() - visibleRange.start.getTime()) * this.pFactor);

            if (this.max != null && this.rangeLoadedEvents.end > this.max) {
                this.rangeLoadedEvents.end = this.max;
            }

            return {
                startFirst: startFirstR,
                endFirst: this.rangeLoadedEvents.end,
                startSecond: null,
                endSecond: null
            };
        }

        if ((visibleRange.start.getTime() < this.rangeLoadedEvents.start) &&
            (visibleRange.end.getTime() <= this.rangeLoadedEvents.end)) {
            // moving left
            const endFirstL = this.rangeLoadedEvents.start - 1;
            this.rangeLoadedEvents.start = Math.round(visibleRange.start.getTime() -
                (visibleRange.end.getTime() - visibleRange.start.getTime()) * this.pFactor);

            if (this.min != null && this.rangeLoadedEvents.start < this.min) {
                this.rangeLoadedEvents.start = this.min;
            }

            return {
                startFirst: this.rangeLoadedEvents.start,
                endFirst: endFirstL,
                startSecond: null,
                endSecond: null
            };
        }

        if ((visibleRange.start.getTime() < this.rangeLoadedEvents.start) &&
            (visibleRange.end.getTime() > this.rangeLoadedEvents.end)) {
            // zooming out
            const pAreaZ = (visibleRange.end.getTime() - visibleRange.start.getTime()) * this.pFactor;
            const endFirstZ = this.rangeLoadedEvents.start - 1;
            const startSecondZ = this.rangeLoadedEvents.end + 1;
            this.rangeLoadedEvents.start = Math.round(visibleRange.start.getTime() - pAreaZ);
            this.rangeLoadedEvents.end = Math.round(visibleRange.end.getTime() + pAreaZ);

            if (this.min != null && this.rangeLoadedEvents.start < this.min) {
                this.rangeLoadedEvents.start = this.min;
            }

            if (this.max != null && this.rangeLoadedEvents.end > this.max) {
                this.rangeLoadedEvents.end = this.max;
            }

            return {
                startFirst: this.rangeLoadedEvents.start,
                endFirst: endFirstZ,
                startSecond: startSecondZ,
                endSecond: this.rangeLoadedEvents.end
            };
        }

        return null;
    }

    /**
     * Force render the timeline component.
     */
    renderTimeline(): void {
        this.instance!.redraw();
    }

    /**
     * Adds an event to the timeline.
     *
     * @param properties Properties for the event.
     */
    addEvent(properties: DataItem): void {
        this.instance!.itemsData.add(properties);
    }

    /**
     * Changes properties of an existing item in the timeline. The provided parameter properties is an object,
     * and can contain parameters "start" (Date), "end" (Date), "content" (String), "group" (String).
     *
     * @param properties Properties for the event.
     */
    changeEvent(properties: Parameters<DataSet<DataItem, "id">["update"]>[0]): void {
        this.instance!.itemsData.update(properties);
    }

    /**
     * Deletes an existing event.
     *
     * @param id Index of the event.
     */
    deleteEvent(id: IdType): void {
        this.instance!.itemsData.remove(id);
    }

    /**
     * Deletes all events from the timeline.
     */
    deleteAllEvents(): void {
        this.instance!.itemsData.clear();
    }

    /**
     * Updates a group of the timeline, adding it if it does not exists.
     *
     * The provided parameter properties is an object, containing the properties
     *
     * - `id` (string)
     * - `content` (string)
     * - `style` (string)
     * - `className` (string)
     * - `order` (number)
     *
     * Parameters `style`, `className` and `order` are optional.
     *
     * @param properties The event's properties.
     */
    updateGroup(properties: Parameters<DataSet<DataGroup, "id">["update"]>[0]): void {
        const dataset = this.instance!.groupsData!.getDataSet();
        dataset.update(properties);
    }

    /**
     * Cancels event adding.
     */
    cancelAdd(): void {
        if (this.addCallback) {
            this.addCallback(null);
            this.addCallback = null;
        }
    }

    /**
     * Cancels event changing.
     */
    cancelChange(): void {
        if (this.changedCallback) {
            this.changedCallback(null);
            this.changedCallback = null;
        }
    }

    /**
     * Cancels event deleting.
     */
    cancelDelete(): void {
        if (this.deleteCallback) {
            this.deleteCallback(null);
            this.deleteCallback = null;
        }
    }

    /**
     * Retrieves the properties of a single event. The returned object can contain parameters `start` (Date), `end`
     * (Date), `content` (String), `group` (String).
     *
     * @param id 0-based index of the item to retrieve.
     * @returns The event at the given index, or `null` when no such events exists at the index.
     */
    getEvent(id: IdType): (DataItem & { id: IdType }) | null {
        return this.instance!.itemsData.get(id);
    }

    /**
     * Is the event by given id editable?
     *
     * @param id Index of the event to check.
     * @returns An object with properties `updateTime`, `updateGroup` and `remove`.
     */
    getEditable(id: IdType): TimelineItemEditableType | undefined {
        return this.instance!.itemSet.getItemById(id)!.editable;
    }

    /**
     * The currently visible time range of the timeline.
     *
     * @returns The time range that is currently visible.
     */
    getVisibleRange(): TimelineWindow {
        return this.instance!.getWindow();
    }

    /**
     * Set the current visible window. The parameters `start` and `end` can be a date, number, or string.
     *
     * If the parameter value of `start` or `end` is `null`, the parameter will be left unchanged.
     *
     * The parameter `options` must include an `animation` property, which can be a boolean or an object of the form
     * `{duration: number, easingFunction: string}`.
     *
     * If `true` (default) or an object, the range is animated smoothly to the new window. An object can be provided to
     * specify duration and easing function.
     *
     * Default duration is 500 ms, and default easing function is `easeInOutQuad`.
     *
     * Available easing functions:
     *
     * - `linear`
     * - `easeInQuad`
     * - `easeOutQuad`
     * - `easeInOutQuad`
     * - `easeInCubic`,
     * - `easeOutCubic`
     * - `easeInOutCubic`
     * - `easeInQuart`
     * - `easeOutQuart`
     * - `easeInOutQuart`
     * - `easeInQuint`
     * - `easeOutQuint`
     * - `easeInOutQuint`.
     *
     * @param start Start of the time range.
     * @param end End of the time range.
     * @param options Optional settings.
     * @param callback Function A callback function can be passed as an optional parameter. This function
     * will be called at the end of the `setVisibleRange` function.
     * @returns Always returns `undefined`.
     */
    setVisibleRange(start: DateType, end: DateType, options?: TimelineAnimationOptions, callback?: () => void): void {
        return this.instance!.setWindow(start, end, options, callback);
    }

    /**
     * Moves the timeline the given move factor to the left or right. Start and end date will be adjusted, and the
     * timeline will be redrawn.
     *
     * For example, try a move factor of `0.1` or `-0.1`. The move factor is a number that determines the moving amount.
     * A positive value will move right, a negative value will move left.
     *
     * The parameter `options` must include an `animation` property, which can be a boolean or an object of the form
     * `{duration: number, easingFunction: string}`.
     *
     * If `true` (default) or an object, the range is animated smoothly to the new window. An object can be provided to
     * specify duration and easing function.
     *
     * Default duration is 500 ms, and default easing function is `easeInOutQuad`.
     *
     * Available easing functions:
     *
     * - `linear`
     * - `easeInQuad`
     * - `easeOutQuad`
     * - `easeInOutQuad`
     * - `easeInCubic`,
     * - `easeOutCubic`
     * - `easeInOutCubic`
     * - `easeInQuart`
     * - `easeOutQuart`
     * - `easeInOutQuart`
     * - `easeInQuint`
     * - `easeOutQuint`
     * - `easeInOutQuint`.
     *
     * @param moveFactor The amount to move by. A positive value will move right, a negative value will move
     * left.
     * @param options Optional settings.
     * @param callback A callback function can be passed as an optional parameter. This function will be
     * called at the end of move operation.
     */
    move(moveFactor: number, options?: TimelineAnimationOptions, callback?: () => void): void {
        this.initiatedByUser = true;
        const range = this.instance!.getWindow();
        const interval = range.end.valueOf() - range.start.valueOf();
        const start = range.start.valueOf() + interval * moveFactor;
        const end = range.end.valueOf() + interval * moveFactor;

        this.instance!.setWindow(start, end, options, callback);
    }

    /**
     * Zooms the timeline the given zoom factor in or out.
     *
     * The parameter `options` must include an `animation` property, which can be a boolean or an object of the form
     * `{duration: number, easingFunction: string}`.
     *
     * If `true` (default) or an object, the range is animated smoothly to the new window. An object can be provided to
     * specify duration and easing function.
     *
     * Default duration is 500 ms, and default easing function is `easeInOutQuad`.
     *
     * Available easing functions:
     *
     * - `linear`
     * - `easeInQuad`
     * - `easeOutQuad`
     * - `easeInOutQuad`
     * - `easeInCubic`,
     * - `easeOutCubic`
     * - `easeInOutCubic`
     * - `easeInQuart`
     * - `easeOutQuart`
     * - `easeInOutQuart`
     * - `easeInQuint`
     * - `easeOutQuint`
     * - `easeInOutQuint`.
     *
     * @param zoomFactor An number between -1 and +1. If positive zoom in, and if negative zoom out.
     * @param options Optional settings.
     * @param callback A callback function can be passed as an optional parameter. This function will be
     * called at the end of the zooming operation.
     * @returns Always returns `undefined`.
     */
    zoom(zoomFactor: number, options?: TimelineAnimationOptions, callback?: () => void): void {
        this.initiatedByUser = true;
        if (zoomFactor >= 0) {
            return this.instance!.zoomIn(zoomFactor, options, callback);
        } else {
            return this.instance!.zoomOut(-zoomFactor, options, callback);
        }
    }

    /**
     * Gets number of events (items in the timeline).
     *
     * @returns The number of event in the timeline.
     */
    getNumberOfEvents(): number {
        return this.instance!.itemsData.length;
    }

    /**
     * Gets id of the currently selected event.
     *
     * @returns The index of the currently selected event, or `null` if no event is currently selected.
     */
    getSelectedId(): IdType | null {
        const selection = this.instance!.getSelection();
        if (selection.length) {
            if (selection[0] != undefined) {
                return selection[0];
            }
        }

        return null;
    }

    /**
     * Finds the currently selected event.
     *
     * @returns The currently selected event, or `null` when no event is selected.
     */
    getSelectedEvent(): (DataItem & { id: IdType }) | null {
        const id = this.getSelectedId();
        if (id) {
            return this.instance!.itemsData.get(id);
        }

        return null;
    }

    /**
     * Selects an event by its ID. The visible range will be moved, so that the selected event is placed in the middle.
     *
     * To unselect all events, pass null as the parameter.
     *
     * @param id Index of the event to select. When negative, unselects all events.
     */
    setSelection(id: IdType | null): void {
        if (id) {
            this.instance!.setSelection(id);
        } else {
            // unselect all events
            this.instance!.setSelection([]);
        }
    }

    /**
     * Gets instance of the timeline object.
     *
     * @returns The current timeline instance.
     */
    getInstance(): PrimeFacesTypes.widget.Timeline.VisTimelineInstance | null {
        return this.instance;
    }
}
