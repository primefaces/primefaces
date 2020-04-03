PrimeFaces.widget.Timeline = PrimeFaces.widget.DeferredWidget.extend({
    /**
     * Initializes the widget.
     *
     * @param {Object} cfg widget configuration's object.
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

     //@Override
    refresh: function(cfg) { 
        // clean up memory
        if (this.instance) {
            this.instance.destroy();
        }

        this._super(cfg);
    },

    //@Override
    destroy: function() {
        this._super();

        // clean up memory
        if (this.instance) {
            this.instance.destroy();
        }
    },
    
    /**
     * Bind items events.
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
                        value: item.start.toISOString()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: item.end.toISOString()
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
                        value: item.start.toISOString()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: item.end.toISOString()
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
                        value: item.start.toISOString()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: item.end.toISOString()
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
     * Binds timeline's events.
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
                        {name: this.id + '_startDate', value: properties.start.toISOString()},
                        {name: this.id + '_endDate', value: properties.end.toISOString()}
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
                        {name: this.id + '_startDate', value: properties.start.toISOString()},
                        {name: this.id + '_endDate', value: properties.end.toISOString()}
                    ],
                    properties: properties
                };

                // #5455 only fire if initiated by user
                if (properties.byUser) {
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
                    value: snappedStart.toISOString()
                });

                params.push({
                    name: this.id + '_endDate',
                    value: snappedEnd.toISOString()
                });

                var group = inst.itemSet.groupFromTarget(evt); // (group may be undefined)

                if (group) {
                    params.push({
                        name: this.id + '_group',
                        value: group.content
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
     * in timeline and want to send them to server to update the backing model (with pe:remoteCommand and pe:convertTimelineEvents).
     *
     * @return {Object}
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
            options.params[0] = {name: this.id + '_startDateFirst', value: new Date(range.startFirst).toISOString()};
            options.params[1] = {name: this.id + '_endDateFirst', value: new Date(range.endFirst).toISOString()};
        }

        if (range.startSecond != null && range.endSecond != null) {
            options.params[2] = {name: this.id + '_startDateSecond', value: new Date(range.startSecond).toISOString()};
            options.params[3] = {name: this.id + '_endDateSecond', value: new Date(range.endSecond).toISOString()};
        }

        this.getBehavior("lazyload").call(this, options);
    },

    /**
     * Gets time range(s) for events to be lazy loaded.
     * The internal time range for already loaded events will be updated.
     *
     * @return {Object}
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
     * Adds an event to the timeline. The provided parameter properties is an object, containing parameters
     * "start" (Date), "end" (Date), "content" (String), "group" (String). Parameters "end" and "group" are optional.
     *
     * @param properties event's properties
     */
    addEvent: function (properties) {
        this.instance.itemsData.add(properties);
    },

    /**
     * Changes properties of an existing item in the timeline. The provided parameter properties is an object,
     * and can contain parameters "start" (Date), "end" (Date), "content" (String), "group" (String).
     *
     * @param properties event's properties
     */
    changeEvent: function (properties) {
        this.instance.itemsData.update(properties);
    },

    /**
     * Deletes an existing event.
     *
     * @param id String id of the event.
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
     * Update a group to the timeline adding if not exists. The provided parameter properties is an object, containing parameters
     * "id" (String), "content" (String), "style" (String), "className" (String), "order" (Number). Parameters "style", "className" and "order" are optional.
     *
     * @param properties event's properties
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
     * Retrieves the properties of a single event. The returned object can contain parameters
     * "start" (Date), "end" (Date), "content" (String), "group" (String).
     *
     * @param {String} id the id of the event.
     * @return {Object}
     */
    getEvent: function (id) {
        return this.instance.itemsData.get(id);
    },

    /**
     * Is the event by given id editable?
     *
     * @param id the id of the event
     * @return {object} Object with properties updateTime, updateGroup and remove.
     */
    getEditable: function(id) {
        return this.instance.itemSet.getItemById(id).editable;
    },

    /**
     * Returns an object with start and end properties, which each one of them is a Date object,
     * representing the currently visible time range.
     *
     * @return {Object}
     */
    getVisibleRange: function () {
        return this.instance.getWindow();
    },

    /**
     * Set the current visible window. The parameters start and end can be a Date, Number, or String.
     * If the parameter value of start or end is null, the parameter will be left unchanged. Available options:
     *
     * animation: boolean or {duration: number, easingFunction: string} If true (default) or an Object,
     * the range is animated smoothly to the new window. An object can be provided to specify duration
     * and easing function. Default duration is 500 ms, and default easing function is 'easeInOutQuad'.
     * Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", "easeInCubic",
     * "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", "easeInQuint",
     * "easeOutQuint", "easeInOutQuint".
     *
     * @param start start Date
     * @param end end Date
     * @param options Object
     * @param callback Function A callback function can be passed as an optional parameter.
     * This function will be called at the end of setVisibleRange function.
     */
    setVisibleRange: function (start, end, options, callback) {
        return this.instance.setWindow(start, end, options, callback);
    },

    /**
     * Moves the timeline the given movefactor to the left or right. Start and end date will be adjusted,
     * and the timeline will be redrawn. For example, try moveFactor = 0.1 or -0.1. moveFactor is a Number
     * that determines the moving amount. A positive value will move right, a negative value will move left.
     * Available options:
     *
     * animation: boolean or {duration: number, easingFunction: string}
     * If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided
     * to specify duration and easing function. Default duration is 500 ms, and default easing function
     * is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad",
     * "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart",
     * "easeInQuint", "easeOutQuint", "easeInOutQuint".
     *
     * @param moveFactor Number
     * @param options Object
     * @param callback Function A callback function can be passed as an optional parameter.
     * This function will be called at the end of move function.
     */
    move: function (moveFactor, options, callback) {
        var range = this.instance.getWindow();
        var interval = range.end - range.start;
        var start = range.start.valueOf() + interval * moveFactor;
        var end = range.end.valueOf() + interval * moveFactor;

        this.instance.setWindow(start, end, options, callback);
    },

    /**
     * Zooms the timeline the given zoomfactor in or out. Available options:
     *
     * animation: boolean or {duration: number, easingFunction: string}
     * If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided
     * to specify duration and easing function. Default duration is 500 ms, and default easing function
     * is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad",
     * "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart",
     * "easeInQuint", "easeOutQuint", "easeInOutQuint".
     *
     * @param zoomFactor Number An number between -1 and +1. If positive zoom in, and if negative zoom out.
     * @param options Object Optional.
     * @param callback Function A callback function can be passed as an optional parameter. This function
     * will be called at the end of zoomIn function.
     */
    zoom: function (zoomFactor, options, callback) {
        if (zoomFactor >= 0) {
            return this.instance.zoomIn(zoomFactor, options, callback);
        } else {
            return this.instance.zoomOut(-zoomFactor, options, callback);
        }
    },

    /**
     * Gets number of events (items in the timeline).
     *
     * @return {Integer}
     */
    getNumberOfEvents: function () {
        return this.instance.itemsData.length;
    },

    /**
     * Gets id of the currently selected event or null.
     *
     * @return {Number}
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
     * Gets currently selected event with properties or null. Properties are
     * "start" (Date), "end" (Date), "content" (String), "group" (String), "editable" (boolean).
     *
     * @return {Object} JSON object
     */
    getSelectedEvent: function () {
        var id = this.getSelectedId();
        if (id) {
            return this.instance.itemsData.get(id);
        }

        return null;
    },

    /**
     * Selects an event by id. The visible range will be moved,
     * so that the selected event is placed in the middle.
     * To unselect all events, pass null as parameter.
     *
     * @param id
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
     * Gets instance of the Timeline object.
     *
     * @return {Object}
     */
    getInstance: function () {
        return this.instance;
    }
});