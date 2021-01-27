/**
 * __PrimeFaces Timeline Widget__
 * 
 * Timeline is an interactive graph to visualize events in time. Currently uses
 * [vis-timeline](https://github.com/visjs/vis-timeline).
 * 
 * @typedef PrimeFaces.widget.Timeline.AddCallbackCallback Callback that is invoked when an event was added to this
 * timeline. See also {@link Timeline.addCallback}.
 * @param {import("vis-timeline").TimelineItem} PrimeFaces.widget.Timeline.AddCallbackCallback.item The timeline item
 * that was added.
 * 
 * @typedef PrimeFaces.widget.Timeline.ChangedCallbackCallback Callback for when an item of the timeline has changed or
 * was moved. See also {@link Timeline.changedCallback}.
 * @param {import("vis-timeline").TimelineItem} PrimeFaces.widget.Timeline.ChangedCallbackCallback.item  The timeline
 * item that was changed.
 * 
 * @typedef PrimeFaces.widget.Timeline.DeleteCallbackCallback Callback for when an item of the timeline was deleted. See
 * also {@link Timeline.deleteCallback}.
 * @param {import("vis-timeline").TimelineItem} PrimeFaces.widget.Timeline.DeleteCallbackCallback.item The timeline item
 * that was deleted.
 * 
 * @typedef PrimeFaces.widget.Timeline.TimelineExtender An extender function that may be used modify the settings of the
 * configuration object. The {@link TimelineCfg.opts} are passed directly to vis-timeline. See also
 * {@link TimelineCfg.extender}.
 * @this {PrimeFaces.widget.Timeline} PrimeFaces.widget.Timeline.TimelineExtender 
 * 
 * @interface {PrimeFaces.widget.Timeline.TimeRange} TimeRange Represents a time range between two points in time.
 * @prop {number | null} TimeRange.start Lower bound of the time range.
 * @prop {number | null} TimeRange.end Upper bound of the time range.
 * 
 * @interface {PrimeFaces.widget.Timeline.TimelineBiRange} TimelineBiRange Represents one or two time ranges.
 * @prop {number} TimelineBiRange.startFirst Start of the first time range.
 * @prop {number} TimelineBiRange.endFirst End of the first time range.
 * @prop {number | null} TimelineBiRange.startSecond Start of the second time range.
 * @prop {number | null} TimelineBiRange.endSecond End of the second time range.
 * 
 * @prop {PrimeFaces.widget.Timeline.AddCallbackCallback} addCallback Callback that is invoked when an event was added
 * to this timeline.
 * @prop {PrimeFaces.widget.Timeline.ChangedCallbackCallback} changedCallback Callback for when an item of the timeline
 * has changed or was moved.
 * @prop {PrimeFaces.widget.Timeline.DeleteCallbackCallback} deleteCallback Callback for when an item of the timeline
 * was deleted.
 * @prop {import("vis-timeline").Timeline} instance The current vis-timeline instance.
 * @prop {boolean} lazy Whether the lazy loading feature is enabled, which loads events dynamically via AJAX.
 * @prop {number | null} max If restricting the timeline to a certain range, the upper bound.
 * @prop {number | null} min If restricting the timeline to a certain range, the lower bound.
 * @prop {number} pFactor The current preload factor, see {@link TimelineCfg.preloadFactor}.
 * @prop {PrimeFaces.widget.Timeline.TimeRange} rangeLoadedEvents Time range of the events that were loaded.
 * 
 * @interface {PrimeFaces.widget.TimelineCfg} cfg The configuration for the {@link  Timeline| Timeline widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * 
 * @prop {string} cfg.accept `accept` option for the jQueryUI droppable overlay when using drag&drop.
 * @prop {string} cfg.activeClass Active style class for the droppable overlay when using drag&drop.
 * @prop {Date} cfg.currentTime The currently shown time. 
 * @prop {import("vis-data/declarations/data-set").DataSetInitialOptions<"id">} cfg.data The data for the vis-timeline
 * data set.
 * @prop {PrimeFaces.widget.Timeline.TimelineExtender} cfg.extender An extender function that may be used modify the
 * settings of this configuration object. The {@link opts} are passed directly to vis-timeline.
 * @prop {import("vis-timeline").DataGroupCollectionType} cfg.groups Optional groups for the events.
 * @prop {string} cfg.hoverClass Hover style class for the droppable overlay when using drag&drop.
 * @prop {boolean} cfg.isMenuPresent Whether a menu should be present for the timeline.
 * @prop {import("vis-timeline").TimelineOptions} cfg.opts The options for the vis timeline.
 * @prop {number} cfg.preloadFactor Preload factor is a positive float value or 0 which can be used for lazy loading of
 * events. When the lazy loading feature is active, the calculated time range for preloading will be multiplied by the
 * preload factor. The result of this multiplication specifies the additional time range which will be considered for
 * the preloading during moving / zooming too. For example, if the calculated time range for preloading is 5 days and
 * the preload factor is `0.2`, the result is `5 * 0.2 = 1` day. That means, 1 day backwards and 1 day onwards will be
 * added to the original calculated time range. The event's area to be preloaded is wider then. This helps to avoid
 * frequently, time-consuming fetching of events. Default value is `0`.
 * @prop {string} cfg.scope `scope` option for the jQuery UI droppable overlay when using drag&drop.
 */
PrimeFaces.widget.Timeline = PrimeFaces.widget.DeferredWidget.extend({
    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.cfg = cfg;
        this.id = cfg.id;

        this.lazy = this.getBehavior("lazyload") != null;
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
            this.cfg.opts.onInitialDrawComplete = $.proxy(function() {
                var el = document.getElementById(this.id);
                $(el).find(".timeline-menu").show();
            }, this);
        }

        if (this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.renderDeferred();
    },

    /**
     * Creates timeline widget with all initialization steps.
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function () {
        // instantiate a timeline object
        var el = document.getElementById(this.id);
        var items = new vis.DataSet(this.cfg.data);

        // bind items events
        this._bindItemsEvents();
        if (this.cfg.groups) {
            this.instance = new vis.Timeline(el, items, new vis.DataSet(this.cfg.groups), this.cfg.opts);
        } else {
            this.instance = new vis.Timeline(el, items, this.cfg.opts);
        }

        if (this.cfg.currentTime) {
            this.instance.setCurrentTime(this.cfg.currentTime);
        }

        // bind timeline events
        this._bindTimelineEvents(el);
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) { 
        // clean up memory
        if (this.instance) {
            this.instance.destroy();
        }

        this._super(cfg);
    },

    /**
     * @override
     * @inheritdoc
     */
    destroy: function() {
        this._super();

        // clean up memory
        if (this.instance) {
            this.instance.destroy();
        }
    },
    
    /**
     * Sets up all event listeners for the timeline items.
     * @private
     */
    _bindItemsEvents: function () {
        // "add" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable.add && this.getBehavior("add")) {
            this.cfg.opts.onAdd =
               $.proxy(function(item, callback) {
                    var params = [];
                    if (!item.id) {
                        //item.undefined until https://github.com/visjs/vis-timeline/issues/97 is fixed.
                        item.id = item.undefined;
                    }

                    params.push({
                        name: this.id + '_id',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: PrimeFaces.toISOString(item.start)
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: PrimeFaces.toISOString(item.end)
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }

                    this.addCallback = callback;

                    this.getBehavior("add").call(this, {params: params, item: item, callback: callback});

                    if (this.addCallback) {
                        this.addCallback(item);
                        this.addCallback = null;
                    }
               }, this);
        }

        // "change" event
        if (this.cfg.opts.selectable && (this.cfg.opts.editable.updateTime || this.cfg.opts.editable.updateGroup) && this.getBehavior("change")) {
            this.cfg.opts.onMoving =
                $.proxy(function(item, callback) {
                    var params = [];
                    params.push({
                        name: this.id + '_eventId',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: PrimeFaces.toISOString(item.start)
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: PrimeFaces.toISOString(item.end)
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }

                    this.getBehavior("change").call(this, {params: params, item: item, callback: callback});
                    callback(item);
                }, this);
        }

        // "changed" event
        if (this.cfg.opts.selectable && (this.cfg.opts.editable.updateTime || this.cfg.groups && this.cfg.opts.editable.updateGroup) && this.getBehavior("changed")) {
            this.cfg.opts.onMove =
                $.proxy(function(item, callback) {
                    var params = [];
                    params.push({
                        name: this.id + '_eventId',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: PrimeFaces.toISOString(item.start)
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: PrimeFaces.toISOString(item.end)
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }

                    this.changedCallback = callback;
                    this.getBehavior("changed").call(this, {params: params, item: item, callback: callback});
                    if (this.changedCallback) {
                        this.changedCallback(item);
                        this.changedCallback = null;
                    }
                }, this);
        }

        // "edit" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable.updateTime && this.getBehavior("edit")) {
            this.cfg.opts.onUpdate =
                $.proxy(function(item, callback) {
                    var options = {
                        params: [
                            {name: this.id + '_eventId', value: item.id}
                        ],
                        item: item,
                        callback: callback
                    };

                    this.changedCallback = callback;
                    this.getBehavior("edit").call(this, options);
                    if (this.changedCallback) {
                        this.changedCallback(item);
                        this.changedCallback = null;
                    }
                }, this);
        }

        // "delete" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable.remove && this.getBehavior("delete")) {
            this.cfg.opts.onRemove =
                $.proxy(function(item, callback) {
                    var options = {
                        params: [
                            {name: this.id + '_eventId', value: item.id}
                        ],
                        item: item,
                        callback: callback
                    };

                    this.deleteCallback = callback;
                    this.getBehavior("delete").call(this, options);
                    if (this.deleteCallback) {
                        this.deleteCallback(item);
                        this.deleteCallback = null;
                    }
                }, this);
        }
    },

    /**
     * Sets up all event listeners for the timeline's events.
     * @private
     * @param {HTMLElement} el Main element of this widget.
     */
    _bindTimelineEvents: function (el) {
        // "select" event
        if (this.cfg.opts.selectable && this.getBehavior("select")) {
            this.instance.on('select', $.proxy(function () {
                var selectedId = this.getSelectedId();
                if (!selectedId) {
                    return;
                }

                var options = {
                    params: [
                        {name: this.id + '_eventId', value: selectedId}
                    ]
                };

                this.getBehavior("select").call(this, options);
            }, this));
        }

        // "rangechange" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.getBehavior("rangechange")) {
            this.instance.on('rangechange', $.proxy(function (properties) {
                var options = {
                    params: [
                        {name: this.id + '_startDate', value: PrimeFaces.toISOString(properties.start)},
                        {name: this.id + '_endDate', value: PrimeFaces.toISOString(properties.end)}
                    ],
                    properties: properties
                };

                this.getBehavior("rangechange").call(this, options);
            }, this));
        }

        // "rangechanged" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.getBehavior("rangechanged")) {
            this.instance.on('rangechanged', $.proxy(function (properties) {
                var options = {
                    params: [
                        {name: this.id + '_startDate', value: PrimeFaces.toISOString(properties.start)},
                        {name: this.id + '_endDate', value: PrimeFaces.toISOString(properties.end)}
                    ],
                    properties: properties
                };

                // #5455/#6902 only fire if initiated by user
                if (properties.byUser || this.initiatedByUser) {
                    this.initiatedByUser = false;
                    this.getBehavior("rangechanged").call(this, options); 
                }
            }, this));
        }

        // "lazyload" event
        if (this.lazy) {
            // initial page load
            this.fireLazyLoading();

            // moving / zooming
            this.instance.on('rangechanged', $.proxy(function () {
                this.fireLazyLoading();
            }, this));
        }

        // register this timeline as droppable if needed
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("drop")) {
            var droppableOpts = {tolerance: "pointer"};
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

            droppableOpts.drop = $.proxy(function (evt, ui) {
                var inst = this.getInstance();

                var x = evt.pageX - vis.util.getAbsoluteLeft(inst.dom.center);
                var y = evt.pageY - vis.util.getAbsoluteTop(inst.dom.center);

                var xstart = inst._toTime(x);
                var xend = inst._toTime(x + inst.dom.container.clientWidth / 10); // add 10% of timeline width

                var snap = inst.itemSet.options.snap || null;
                var scale = inst.body.util.getScale();
                var step = inst.body.util.getStep();
                var snappedStart = snap ? snap(xstart, scale, step).toDate() : xstart;
                var snappedEnd = snap ? snap(xend, scale, step).toDate() : xend;

                var params = [];
                params.push({
                    name: this.id + '_startDate',
                    value: PrimeFaces.toISOString(snappedStart)
                });

                params.push({
                    name: this.id + '_endDate',
                    value: PrimeFaces.toISOString(snappedEnd)
                });

                var group = inst.itemSet.groupFromTarget(evt); // (group may be undefined)

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
                var uiData = ui.draggable.closest(".ui-datatable, .ui-datagrid, .ui-datalist, .ui-carousel, .ui-treetable");
                if (uiData.length > 0) {
                    params.push({
                        name: this.id + '_uiDataId',
                        value: uiData.attr('id')
                    });
                }

                // call the drop listener
                // parameters event and ui can be accessible in "onstart" (p:ajax) via cfg.ext.event and cfg.ext.ui
                // or in "execute" (pe:javascript) via ext.event and ext.ui
                this.getBehavior("drop").call(this, {params: params, event: evt, ui: ui});
            }, this);

            // make the timeline droppable
            $(el).droppable(droppableOpts);
        }
    },

    /**
     * Retrieves the array of current data (events) as an JSON string. This method is useful when you done some changes
     * in timeline and want to send them to server to update the backing model (with `pe:remoteCommand` and
     * `pe:convertTimelineEvents`).
     *
     * @return {string} A JSON string with the current data.
     */
    getData: function () {
        var newData = this.instance.itemsData.map($.proxy(function(item) {
            var newItem = {};
            if (item.hasOwnProperty('content')) {
                newItem.data = item.content;
            }

            if (item.hasOwnProperty('start')) {
                newItem.startDate = item.start.getTime();
            }

            if (item.hasOwnProperty('end') && (item.end != null)) {
                newItem.endDate = item.end.getTime();
            } else {
                newItem.endDate = null;
            }

            if (item.hasOwnProperty('editable')) {
                newItem.editable = item.editable;
            } else {
                newItem.editable = null;
            }

            if (item.hasOwnProperty('group')) {
                newItem.group = item.group;
            } else {
                newItem.group = null;
            }

            if (item.hasOwnProperty('className')) {
                newItem.styleClass = item.className;
            } else {
                newItem.styleClass = null;
            }

            return newItem;
        }, this));

        return JSON.stringify(newData);
    },

    /**
     * Fires event for lazy loading.
     * @private
     */
    fireLazyLoading: function() {
        var range = this.getLazyLoadRange();
        if (range == null) {
            // don't send event
            return;
        }

        var options = {
            params: []
        };

        if (range.startFirst != null && range.endFirst != null) {
            options.params[0] = {name: this.id + '_startDateFirst', value: PrimeFaces.toISOString(new Date(range.startFirst))};
            options.params[1] = {name: this.id + '_endDateFirst', value: PrimeFaces.toISOString(new Date(range.endFirst))};
        }

        if (range.startSecond != null && range.endSecond != null) {
            options.params[2] = {name: this.id + '_startDateSecond', value: PrimeFaces.toISOString(new Date(range.startSecond))};
            options.params[3] = {name: this.id + '_endDateSecond', value: PrimeFaces.toISOString(new Date(range.endSecond))};
        }

        this.getBehavior("lazyload").call(this, options);
    },

    /**
     * Gets time range(s) for events to be lazy loaded.
     * 
     * The internal time range for already loaded events will be updated.
     *
     * @private
     * @return {PrimeFaces.widget.Timeline.TimelineBiRange | null} The time range(s) for events to be lazy loaded.
     */
    getLazyLoadRange: function() {
        var visibleRange = this.instance.getWindow();

        if (this.rangeLoadedEvents.start == null || this.rangeLoadedEvents.end == null) {
            // initial load
            var pArea = (visibleRange.end.getTime() - visibleRange.start.getTime()) * this.pFactor;
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
            var startFirstR = this.rangeLoadedEvents.end + 1;
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
            var endFirstL = this.rangeLoadedEvents.start - 1;
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
            var pAreaZ = (visibleRange.end.getTime() - visibleRange.start.getTime()) * this.pFactor;
            var endFirstZ = this.rangeLoadedEvents.start - 1;
            var startSecondZ = this.rangeLoadedEvents.end + 1;
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
    },

    /**
     * Force render the timeline component.
     */
    renderTimeline: function () {
        this.instance.redraw();
    },

    /**
     * Adds an event to the timeline.
     *
     * @param {import("vis-timeline").DataItem} properties Properties for the event.
     */
    addEvent: function (properties) {
        this.instance.itemsData.add(properties);
    },

    /**
     * Changes properties of an existing item in the timeline. The provided parameter properties is an object,
     * and can contain parameters "start" (Date), "end" (Date), "content" (String), "group" (String).
     * 
     * @param {import("vis-data/declarations/data-interface").DeepPartial<import("vis-timeline").DataItem>} properties
     * Properties for the event.
     */
    changeEvent: function (properties) {
        this.instance.itemsData.update(properties);
    },

    /**
     * Deletes an existing event.
     *
     * @param {import("vis-timeline").IdType} id Index of the event.
     */
    deleteEvent: function (id) {
        this.instance.itemsData.remove(id);
    },

    /**
     * Deletes all events from the timeline.
     */
    deleteAllEvents: function () {
        this.instance.itemsData.clear();
    },

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
     * @param {import("vis-data/declarations/data-interface").DeepPartial<import("vis-timeline").DataGroup>} properties
     * The event's properties.
     */
    updateGroup: function (properties) {
        var dataset = this.instance.groupsData.getDataSet();
        dataset.update(properties);
    },

    /**
     * Cancels event adding.
     */
    cancelAdd: function () {
        if (this.addCallback) {
            this.addCallback(null);
            this.addCallback = null;
        }
    },

    /**
     * Cancels event changing.
     */
    cancelChange: function () {
        if (this.changedCallback) {
            this.changedCallback(null);
            this.changedCallback = null;
        }
    },

    /**
     * Cancels event deleting.
     */
    cancelDelete: function () {
        if (this.deleteCallback) {
            this.deleteCallback(null);
            this.deleteCallback = null;
        }
    },

    /**
     * Retrieves the properties of a single event. The returned object can contain parameters `start` (Date), `end`
     * (Date), `content` (String), `group` (String).
     *
     * @param {import("vis-timeline").IdType} id 0-based index of the item to retrieve.
     * @return {import("vis-data/declarations/data-interface").FullItem<import("vis-timeline").DataItem, "id"> | null}
     * The event at the given index, or `null` when no such events exists at the index.
     */
    getEvent: function (id) {
        return this.instance.itemsData.get(id);
    },

    /**
     * Is the event by given id editable?
     *
     * @param {number} id Index of the event to check.
     * @return {import("vis-timeline").TimelineItemEditableType} An object with properties `updateTime`,
     * `updateGroup` and `remove`.
     */
    getEditable: function(id) {
        return this.instance.itemSet.getItemById(id).editable;
    },

    /**
     * The currently visible time range of the timeline.
     *
     * @return {import("vis-timeline").TimelineWindow} The time range that is currently visible.
     */
    getVisibleRange: function () {
        return this.instance.getWindow();
    },

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
     * @param {import("vis-timeline").DateType} start Start of the time range.
     * @param {import("vis-timeline").DateType} end End of the time range.
     * @param {import("vis-timeline").TimelineAnimationOptions} [options] Optional settings.
     * @param {() => void} [callback] Function A callback function can be passed as an optional parameter. This function
     * will be called at the end of the `setVisibleRange` function.
     * @return {undefined} Always returns `undefined`.
     */
    setVisibleRange: function (start, end, options, callback) {
        return this.instance.setWindow(start, end, options, callback);
    },

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
     * @param {number} moveFactor The amount to move by. A positive value will move right, a negative value will move
     * left.
     * @param {import("vis-timeline").TimelineAnimationOptions} [options] Optional settings.
     * @param {() => void} [callback] A callback function can be passed as an optional parameter. This function will be
     * called at the end of move operation.
     */
    move: function (moveFactor, options, callback) {
        this.initiatedByUser = true;
        var range = this.instance.getWindow();
        var interval = range.end - range.start;
        var start = range.start.valueOf() + interval * moveFactor;
        var end = range.end.valueOf() + interval * moveFactor;

        this.instance.setWindow(start, end, options, callback);
    },

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
     * @param {number} zoomFactor An number between -1 and +1. If positive zoom in, and if negative zoom out.
     * @param {import("vis-timeline").TimelineAnimationOptions} [options] Optional settings.
     * @param {() => void} [callback] A callback function can be passed as an optional parameter. This function will be
     * called at the end of the zooming operation.
     * @return {undefined} Always returns `undefined`.
     */
    zoom: function (zoomFactor, options, callback) {
        this.initiatedByUser = true;
        if (zoomFactor >= 0) {
            return this.instance.zoomIn(zoomFactor, options, callback);
        } else {
            return this.instance.zoomOut(-zoomFactor, options, callback);
        }
    },

    /**
     * Gets number of events (items in the timeline).
     *
     * @return {number} The number of event in the timeline.
     */
    getNumberOfEvents: function () {
        return this.instance.itemsData.length;
    },

    /**
     * Gets id of the currently selected event.
     *
     * @return {import("vis-timeline").IdType | null} The index of the currently selected event, or `null` if no event
     * is currently selected.
     */
    getSelectedId: function () {
        var selection = this.instance.getSelection();
        if (selection.length) {
            if (selection[0] != undefined) {
                return selection[0];
            }
        }

        return null;
    },

    /**
     * Finds the currently selected event.
     *
     * @return {import("vis-data/declarations/data-interface").FullItem<import("vis-timeline").DataItem, "id"> | null}
     * The currently selected event, or `null` when no event is selected.
     */
    getSelectedEvent: function () {
        var id = this.getSelectedId();
        if (id) {
            return this.instance.itemsData.get(id);
        }

        return null;
    },

    /**
     * Selects an event by its ID. The visible range will be moved, so that the selected event is placed in the middle.
     * 
     * To unselect all events, pass null as the parameter.
     *
     * @param {import("vis-timeline").IdType | null} id Index of the event to select.
     * When negative, unselects all events.
     */
    setSelection: function(id) {
        if (id) {
            this.instance.setSelection(id);
        } else {
            // unselect all events
            this.instance.setSelection([]);
        }
    },

    /**
     * Gets instance of the timeline object.
     *
     * @return {import("vis-timeline").Timeline} The current timeline instance.
     */
    getInstance: function () {
        return this.instance;
    }
});