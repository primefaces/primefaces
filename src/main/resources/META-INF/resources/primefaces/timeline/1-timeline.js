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
        
        if (this.cfg.groups) {
            this.instance = new vis.Timeline(el, items, new vis.DataSet(this.cfg.groups), this.cfg.opts);
        } else {
            this.instance = new vis.Timeline(el, items, this.cfg.opts);
        }
        
        if (this.cfg.currentTime) {
            this.instance.setCurrentTime(this.cfg.currentTime);
        }

        // bind events
        this.bindEvents(el);
    },

    /**
     * Binds timeline's events.
     */
    bindEvents: function (el) {
        // "select" event
        if (this.cfg.opts.selectable && this.getBehavior("select")) {
            this.instance.on('select', $.proxy(function () {
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
            this.instance.setOptions({
               onAdd: $.proxy(function(item, callback) {
                    var params = [];
                    params.push({
                        name: this.id + '_startDate',
                        value: item.start.getTime()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: item.end.getTime()
                        });
                    }

                    if (item.group) {
                        params.push({
                            name: this.id + '_group',
                            value: item.group
                        });
                    }
                    
                    var maxId = this.instance.itemsData.max("id");
                    
                    item.id = 1 + (maxId ? maxId : 0);
                    this.addCallback = callback;
                    
                    this.getBehavior("add").call(this, {params: params, item: item});
                    
                    this.addCallback = null;
               }, this)
            });            
        }

        // "change" event        
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.cfg.opts.timeChangeable && this.getBehavior("change")) {
            this.instance.setOptions({
                onMoving: $.proxy(function(item, callback) {
                    var params = [];
                    params.push({
                        name: this.id + '_eventIdx',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: item.start.getTime()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: item.end.getTime()
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
                }, this)
            });
        }

        // "changed" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.cfg.opts.timeChangeable && this.getBehavior("changed")) {
            this.instance.setOptions({
                onMove: $.proxy(function(item, callback) {
                    var params = [];
                    params.push({
                        name: this.id + '_eventIdx',
                        value: item.id
                    });

                    params.push({
                        name: this.id + '_startDate',
                        value: item.start.getTime()
                    });

                    if (item.end) {
                        params.push({
                            name: this.id + '_endDate',
                            value: item.end.getTime()
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
                    this.changedCallback = null;
                }, this)
            });
        }

        // "edit" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("edit")) {
            this.instance.setOptions({
                onUpdate: $.proxy(function(item, callback) {
                    var options = {
                        params: [
                            {name: this.id + '_eventIdx', value: item.id}
                        ],
                        item: item,
                        callback: callback
                    };
                    
                    this.changedCallback = callback;
                    this.getBehavior("edit").call(this, options);
                    this.changedCallback = null;
                }, this)
            });
        }

        // "delete" event
        if (this.cfg.opts.selectable && this.cfg.opts.editable && this.getBehavior("delete")) {
            this.instance.setOptions({
                onRemove: $.proxy(function(item, callback) {
                    var options = {
                        params: [
                            {name: this.id + '_eventIdx', value: item.id}
                        ],
                        item: item,
                        callback: callback
                    };

                    this.deleteCallback = callback;
                    this.getBehavior("delete").call(this, options);
                    this.deleteCallback = null;
                }, this)
            });
        }

        // "rangechange" event
        if ((this.cfg.opts.zoomable || this.cfg.opts.moveable) && this.getBehavior("rangechange")) {
            this.instance.on('rangechange', $.proxy(function (properties) {
                var options = {
                    params: [
                        {name: this.id + '_startDate', value: properties.start.getTime()},
                        {name: this.id + '_endDate', value: properties.end.getTime()}
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
                        {name: this.id + '_startDate', value: properties.start.getTime()},
                        {name: this.id + '_endDate', value: properties.end.getTime()}
                    ],
                    properties: properties
                };

                this.getBehavior("rangechanged").call(this, options);
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
                    value: snappedStart.getTime()
                });

                params.push({
                    name: this.id + '_endDate',
                    value: snappedEnd.getTime()
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
     * @param index index of the event.
     * @param properties event's properties
     */
    changeEvent: function (index, properties) {
        properties.id = index;
        this.instance.itemsData.update(properties);
    },

    /**
     * Deletes an existing event.
     *
     * @param index Number index of the event.
     */
    deleteEvent: function (index) {
        this.instance.itemsData.remove(index);
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
        }
    },

    /**
     * Cancels event changing.
     */
    cancelChange: function () {
        if (this.changedCallback) {
            this.changedCallback(null);
        }
    },

    /**
     * Cancels event deleting.
     */
    cancelDelete: function () {
        if (this.deleteCallback) {
            this.deleteCallback(null);
        }
    },

    /**
     * Retrieves the properties of a single event. The returned object can contain parameters
     * "start" (Date), "end" (Date), "content" (String), "group" (String).
     *
     * @return {Object}
     */
    getEvent: function (index) {
        return this.instance.itemsData.get(index);
    },

    /**
     * Is the event by given index editable?
     *
     * @param index index of the event
     * @return {object} Object with properties updateTime, updateGroup and remove.
     */
    getEditable: function(index) {
        return this.instance.itemSet.getItemById(index).editable;
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
     * Move the window such that given time is centered on screen. Parameter time can be a Date, Number, or String. 
     * Available options:
     * 
     * animation: boolean or {duration: number, easingFunction: string} 
     * If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided 
     * to specify duration and easing function. Default duration is 500 ms, and default easing function 
     * is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", 
     * "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", 
     * "easeInQuint", "easeOutQuint", "easeInOutQuint".
     *
     * @param time Date, Number or String
     * @param options Object 
     * @param callback Function A callback function can be passed as an optional parameter. 
     * This function will be called at the end of moveTo function.
     */
    move: function (time, options, callback) {
        return this.instance.moveTo(time, options, callback);
    },

    /**
     * Zoom in the current visible window. The parameter percentage can be a Number and must be between -1 and +1. 
     * If the parameter value of percentage is null, the window will be left unchanged. Available options:
     * 
     * animation: boolean or {duration: number, easingFunction: string} 
     * If true (default) or an Object, the range is animated smoothly to the new window. An object can be provided 
     * to specify duration and easing function. Default duration is 500 ms, and default easing function 
     * is 'easeInOutQuad'. Available easing functions: "linear", "easeInQuad", "easeOutQuad", "easeInOutQuad", 
     * "easeInCubic", "easeOutCubic", "easeInOutCubic", "easeInQuart", "easeOutQuart", "easeInOutQuart", 
     * "easeInQuint", "easeOutQuint", "easeInOutQuint".
     *
     * @param percentage Number An number between -1 and +1. If positive zoom in, and if negative zoom out.
     * @param options Object
     * @param callback Function A callback function can be passed as an optional parameter. This function 
     * will be called at the end of zoomIn function.
     */
    zoom: function (percentage, options, callback) {
        if (percentage) { 
            if (percentage >= 0) {
                return this.instance.zoomIn(percentage, options, callback);
            } else {
                return this.instance.zoomOut(-percentage, options, callback);
            }
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
     * Gets index of the currently selected event or -1.
     *
     * @return {Number}
     */
    getSelectedIndex: function () {
        var selection = this.instance.getSelection();
        if (selection.length) {
            if (selection[0] != undefined) {
                return selection[0];
            }
        }

        return -1;
    },

    /**
     * Gets currently selected event with properties or null. Properties are
     * "start" (Date), "end" (Date), "content" (String), "group" (String), "editable" (boolean).
     *
     * @return {Object} JSON object
     */
    getSelectedEvent: function () {
        var index = this.getSelectedIndex();
        if (index != -1) {
            return this.instance.itemsData.get(index);
        }

        return null;
    },

    /**
     * Selects an event by index. The visible range will be moved,
     * so that the selected event is placed in the middle.
     * To unselect all events, use a negative id, e.g. index = -1.
     *
     * @param index
     */
    setSelection: function(index) {
        if (index >= 0) {
            this.instance.setSelection(index);
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