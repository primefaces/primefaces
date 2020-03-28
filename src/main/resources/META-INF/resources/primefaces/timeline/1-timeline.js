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
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function (cfg) {
        this._super(cfg);
        this.cfg = cfg;
        this.id = cfg.id;

        this.lazy = this.getBehavior("lazyload") != null;
        if (this.lazy) {
            this.min = (typeof this.cfg.opts.min !== 'undefined' ? this.cfg.opts.min.getTime() : null);
            this.max = (typeof this.cfg.opts.max !== 'undefined' ? this.cfg.opts.max.getTime() : null);
            this.pFactor = this.cfg.opts.preloadFactor;

            this.rangeLoadedEvents = {
                start: null,
                end: null
            };
        }

        this.renderDeferred();
    },

    /**
     * Creates timeline widget with all initialization steps.
     * @override
     * @protected
     */
    _render: function () {
        // configure localized text
        var settings = PrimeFaces.getLocaleSettings();
        if(settings) {
            for(var setting in settings) {
                this.cfg.opts[setting] = settings[setting];
            }
        }

        // instantiate a timeline object
        var el = document.getElementById(this.id);
        this.instance = new links.Timeline(el, this.cfg.opts);

        // draw the timeline with created data and options
        this.instance.draw(this.cfg.data, null);

        // set current time (workaround)
        if (this.cfg.opts.currentTime) {
            this.instance.setCurrentTime(this.cfg.opts.currentTime);
        }

        // bind events
        this.bindEvents(el);
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     * @param {HTMLElement} el The main element of this timeline.
     */
    bindEvents: function (el) {
        if (this.cfg.opts.responsive) {
            var layoutPane = $(el).closest(".ui-layout-pane");
            if (layoutPane.length > 0) {
                // timeline is within layout pane / unit ==> resize it when resizing layout pane / unit
                layoutPane.on('layoutpaneonresize', $.proxy(function () {
                    this.instance.checkResize();
                }, this));
            } else {
                // resize timeline on window resizing
                var nsevent = "resize.timeline" + PrimeFaces.escapeClientId(this.id);
                $(window).off(nsevent).on(nsevent, $.proxy(function () {
                    this.instance.checkResize();
                }, this));
            }
        }

        // "select" event
        if (this.cfg.opts.selectable && this.getBehavior("select")) {
            links.events.addListener(this.instance, 'select', $.proxy(function () {
                var index = this.getSelectedIndex();
                if (index < 0) {
                    return;
                }

                var options = {
                    params: [
                        {name: this.id + '_eventIdx', value: index}
                    ]
                };

                this.getBehavior("select").call(this, options);
            }, this));
        }

        // "add" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("add")) {
            links.events.addListener(this.instance, 'add', $.proxy(function () {
                var event = this.getSelectedEvent();
                if (event == null) {
                    return;
                }

                var params = [];
                params.push({
                    name: this.id + '_startDate',
                    value: event.start.getTime()
                });

                if (event.end) {
                    params.push({
                        name: this.id + '_endDate',
                        value: event.end.getTime()
                    });
                }

                if (event.group) {
                    params.push({
                        name: this.id + '_group',
                        value: event.group
                    });
                }

                this.getBehavior("add").call(this, {params: params});
            }, this));
        }

        // "change" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.cfg.opts.timeChangeable && this.getBehavior("change")) {
            links.events.addListener(this.instance, 'change', $.proxy(function () {
                var index = this.getSelectedIndex();
                if (index < 0) {
                    return;
                }

                var event = this.getEvent(index);
                if (event == null) {
                    return;
                }

                if (!this.instance.isEditable(event)) {
                    // only editable events can be changed
                    return;
                }

                var params = [];
                params.push({
                    name: this.id + '_eventIdx',
                    value: index
                });

                params.push({
                    name: this.id + '_startDate',
                    value: event.start.getTime()
                });

                if (event.end) {
                    params.push({
                        name: this.id + '_endDate',
                        value: event.end.getTime()
                    });
                }

                if (event.group) {
                    params.push({
                        name: this.id + '_group',
                        value: event.group
                    });
                }

                this.getBehavior("change").call(this, {params: params});
            }, this));
        }

        // "changed" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.cfg.opts.timeChangeable && this.getBehavior("changed")) {
            links.events.addListener(this.instance, 'changed', $.proxy(function () {
                var index = this.getSelectedIndex();
                if (index < 0) {
                    return;
                }

                var event = this.getEvent(index);
                if (event == null) {
                    return;
                }

                if (!this.instance.isEditable(event)) {
                    // only editable events can be changed
                    return;
                }

                var params = [];
                params.push({
                    name: this.id + '_eventIdx',
                    value: index
                });

                params.push({
                    name: this.id + '_startDate',
                    value: event.start.getTime()
                });

                if (event.end) {
                    params.push({
                        name: this.id + '_endDate',
                        value: event.end.getTime()
                    });
                }

                if (event.group) {
                    params.push({
                        name: this.id + '_group',
                        value: event.group
                    });
                }

                this.getBehavior("changed").call(this, {params: params});
            }, this));
        }

        // "edit" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("edit")) {
            links.events.addListener(this.instance, 'edit', $.proxy(function () {
                var index = this.getSelectedIndex();
                if (index < 0) {
                    return;
                }

                if (!this.isEditable(index)) {
                    // only editable events can be edited
                    return;
                }

                var options = {
                    params: [
                        {name: this.id + '_eventIdx', value: index}
                    ]
                };

                this.getBehavior("edit").call(this, options);
            }, this));
        }

        // "delete" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("delete")) {
            links.events.addListener(this.instance, 'delete', $.proxy(function () {
                var index = this.getSelectedIndex();
                if (index < 0) {
                    return;
                }

                if (!this.isEditable(index)) {
                    // only editable events can be deleted
                    return;
                }

                var options = {
                    params: [
                        {name: this.id + '_eventIdx', value: index}
                    ]
                };

                this.getBehavior("delete").call(this, options);
            }, this));
        }

        // "rangechange" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.getBehavior("rangechange")) {
            links.events.addListener(this.instance, 'rangechange', $.proxy(function () {
                var range = this.instance.getVisibleChartRange();

                var options = {
                    params: [
                        {name: this.id + '_startDate', value: range.start.getTime()},
                        {name: this.id + '_endDate', value: range.end.getTime()}
                    ]
                };

                this.getBehavior("rangechange").call(this, options);
            }, this));
        }

        // "rangechanged" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.getBehavior("rangechanged")) {
            links.events.addListener(this.instance, 'rangechanged', $.proxy(function () {
                var range = this.instance.getVisibleChartRange();

                var options = {
                    params: [
                        {name: this.id + '_startDate', value: range.start.getTime()},
                        {name: this.id + '_endDate', value: range.end.getTime()}
                    ]
                };

                this.getBehavior("rangechanged").call(this, options);
            }, this));
        }

        // "lazyload" event
        if (this.lazy) {
            // initial page load
            this.fireLazyLoading();

            // moving / zooming
            links.events.addListener(this.instance, 'rangechanged', $.proxy(function () {
                this.fireLazyLoading();
            }, this));
        }

        // register this timeline as droppable if needed
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("drop")) {
            var droppableOpts = {tolerance: "pointer"};
            if (this.cfg.opts.hoverClass) {
                droppableOpts.hoverClass = this.cfg.opts.hoverClass;
            }

            if (this.cfg.opts.activeClass) {
                droppableOpts.activeClass = this.cfg.opts.activeClass;
            }

            if (this.cfg.opts.accept) {
                droppableOpts.accept = this.cfg.opts.accept;
            }

            if (this.cfg.opts.scope) {
                droppableOpts.scope = this.cfg.opts.scope;
            }

            droppableOpts.drop = $.proxy(function (evt, ui) {
                var inst = this.getInstance();

                var x = evt.pageX - links.Timeline.getAbsoluteLeft(inst.dom.content);
                var y = evt.pageY - links.Timeline.getAbsoluteTop(inst.dom.content);

                var xstart = inst.screenToTime(x);
                var xend = inst.screenToTime(x + inst.size.frameWidth / 10); // add 10% of timeline width

                if (this.cfg.opts.snapEvents) {
                    inst.step.snap(xstart);
                    inst.step.snap(xend);
                }

                var params = [];
                params.push({
                    name: this.id + '_startDate',
                    value: xstart.getTime()
                });

                params.push({
                    name: this.id + '_endDate',
                    value: xend.getTime()
                });

                var group = inst.getGroupFromHeight(y); // (group may be undefined)

                if (group) {
                    params.push({
                        name: this.id + '_group',
                        value: inst.getGroupName(group)
                    });
                }

                params.push({
                    name: this.id + '_dragId',
                    value: ui.draggable.attr('id')
                });

                // check if draggable is within a data iteration component
                var uiData = ui.draggable.closest(".ui-datatable, .ui-datagrid, .ui-datalist, .ui-carousel");
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
     * in timeline and want to send them to server to update the backing model (with
     * `pe:remoteCommand` and `pe:convertTimelineEvents`).
     *
     * @return {string} A JSON string with the current data.
     */
    getData: function () {
        var newData = $.map(this.instance.getData(), $.proxy(function(item) {
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
            options.params[0] = {name: this.id + '_startDateFirst', value: range.startFirst};
            options.params[1] = {name: this.id + '_endDateFirst', value: range.endFirst};
        }

        if (range.startSecond != null && range.endSecond != null) {
            options.params[2] = {name: this.id + '_startDateSecond', value: range.startSecond};
            options.params[3] = {name: this.id + '_endDateSecond', value: range.endSecond};
        }

        this.getBehavior("lazyload").call(this, options);
    },

    /**
     * Gets time range(s) for events to be lazy loaded.
     * The internal time range for already loaded events will be updated.
     *
     * @return {PrimeFaces.widget.Timeline.TimelineBiRange | null} The time range(s) for events to be lazy loaded.
     */
    getLazyLoadRange: function() {
        var visibleRange = this.instance.getVisibleChartRange();

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
     *
     * @param {boolean} [animate] Flag indicating whether to animate the render action.
     */
    renderTimeline: function (animate) {
        this.instance.render({'animate': animate});
    },

    /**
     * Adds an event to the timeline.
     *
     * @param {import("vis-timeline").DataItem} properties Properties for the event.
     * @param {boolean} [preventRender] Flag indicating whether to preventRendering or not.
     */
    addEvent: function (properties, preventRender) {
        this.instance.addItem(properties, preventRender);
    },

    /**
     * Changes properties of an existing event in the timeline.
     * 
     * @param {number} index Index of the event.
     * @param {import("vis-data/declarations/data-interface").DeepPartial<import("vis-timeline").DataItem>} properties
     * Properties for the event.
     * @param {boolean} [preventRender] Flag indicating whether to prevent rendering or not.
     */
    changeEvent: function (index, properties, preventRender) {
        this.instance.changeItem(index, properties, preventRender);
    },

    /**
     * Deletes an existing event.
     *
     * @param {number} index Index of the event.
     * @param {boolean} [preventRender] flag indicating whether to prevent re-rendering the timeline immediately after
     * delete (for multiple deletions). Default is false.
     */
    deleteEvent: function (index, preventRender) {
        this.instance.deleteItem(index, preventRender);
    },

    /**
     * Deletes all events from the timeline.
     */
    deleteAllEvents: function () {
        this.instance.deleteAllItems();
    },

    /**
     * Cancels event adding.
     */
    cancelAdd: function () {
        this.instance.cancelAdd();
    },

    /**
     * Cancels event changing.
     */
    cancelChange: function () {
        this.instance.cancelChange();
    },

    /**
     * Cancels event deleting.
     */
    cancelDelete: function () {
        this.instance.cancelDelete();
    },

    /**
     * Retrieves the properties of a single event.
     * @param {number} index 0-based index of the item to retrieve.
     * @return {import("vis-data/declarations/data-interface").FullItem<import("vis-timeline").DataItem, "id"> | null}
     * The event at the given index, or `null` when no such events exists at the index.
     */
    getEvent: function (index) {
        return this.instance.getItem(index);
    },

    /**
     * Checks whether the event at the given index is editable.
     *
     * @param {number} index Index of the event
     * @return {boolean} `true` if the event is editable, or `false` otherwise.
     */
    isEditable: function(index) {
        return this.instance.isEditable(this.getEvent(index));
    },

    /**
     * The currently visible time range of the timeline.
     *
     * @return {import("vis-timeline").TimelineWindow} The time range that is currently visible.
     */
    getVisibleRange: function () {
        return this.instance.getVisibleChartRange();
    },

    /**
     * Sets the visible range to the specified time range by zooming.
     *
     * @param {Date | null} start Start of the time range. Pass `null` to include everything from the earliest date to
     * the given end date.
     * @param {Date | null } end End of the time range. Pass `null` to include everything from the given start date to
     * the last date.
     * @return {undefined} Always returns `undefined`.
     */
    setVisibleRange: function (start, end) {
        return this.instance.setVisibleChartRange(start, end);
    },

    /**
     * Moves the timeline the given move factor to the left or right. Start and end date will be adjusted, and the
     * timeline will be redrawn. For example, try a `moveFactor` of `0.1` or `-0.1`.
     * 
     * @param {number} moveFactor The amount to move by. A positive value will move right, a negative value will move
     * left.
     * @return {undefined} Always returns `undefined`.
     */
    move: function (moveFactor) {
        return this.instance.move(moveFactor);
    },

    /**
     * Zooms the timeline by the given zoom factor. Start and end date will be adjusted, and the timeline will be
     * redrawn. You can optionally give a date around which to zoom. For example, try a `zoomfactor` of `0.1` or `-0.1`.
     *
     * @param {number} zoomFactor Amount by which to zoom. Positive value will zoom in, negative value will zoom out.
     * @param {Date} [zoomAroundDate] Date Date around which to zoom
     * @return {undefined} Always returns `undefined`.
     */
    zoom: function (zoomFactor, zoomAroundDate) {
        return this.instance.zoom(zoomFactor, zoomAroundDate);
    },

    /**
     * Check if the timeline container is resized, and if so, resize the timeline. Useful when the webpage is resized.
     */
    checkResize: function () {
        this.instance.checkResize();
    },

    /**
     * Gets number of events (items in the timeline).
     *
     * @return {number} The number of event in the timeline.
     */
    getNumberOfEvents: function () {
        return this.instance.getData().length;
    },

    /**
     * Gets the index of the currently selected event.
     *
     * @return {number} The index of the currently select event, or `-1` if no event is currently selected.
     */
    getSelectedIndex: function () {
        var selection = this.instance.getSelection();
        if (selection.length) {
            if (selection[0].row != undefined) {
                return selection[0].row;
            }
        }

        return -1;
    },

    /**
     * Finds the currently selected event.
     *
     * @return {import("vis-data/declarations/data-interface").FullItem<import("vis-timeline").DataItem, "id"> | null}
     * The currently selected event, or `null` when no event is selected.
     */
    getSelectedEvent: function () {
        var index = this.getSelectedIndex();
        if (index != -1) {
            return this.instance.getItem(index);
        }

        return null;
    },

    /**
     * Selects an event by given index. The visible range will be moved, so that the selected event is placed in the
     * middle. To unselect all events, use a negative index, e.g. `-1`.
     *
     * @param {number} index Index of the event to select. When negative, unselects all events.
     */
    setSelection: function(index) {
        if (index >= 0) {
            this.instance.setSelection([{
                'row': index
            }]);
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