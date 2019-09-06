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
     * Binds timeline's events.
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
                
                // block the ghost event from being added
                this.instance.applyAdd = false;

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
     * in timeline and want to send them to server to update the backing model (with pe:remoteCommand and pe:convertTimelineEvents).
     *
     * @return {Object}
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
     * @return {Object}
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
     * @param animate flag to animate the render action (optional)
     */
    renderTimeline: function (animate) {
        this.instance.render({'animate': animate});
    },

    /**
     * Adds an event to the timeline. The provided parameter properties is an object, containing parameters
     * "start" (Date), "end" (Date), "content" (String), "group" (String). Parameters "end" and "group" are optional.
     *
     * @param properties event's properties
     * @param preventRender flag indicating whether to preventRendering or not (optional)
     */
    addEvent: function (properties, preventRender) {
        this.instance.addItem(properties, preventRender);
    },

    /**
     * Changes properties of an existing item in the timeline. The provided parameter properties is an object,
     * and can contain parameters "start" (Date), "end" (Date), "content" (String), "group" (String).
     *
     * @param index index of the event.
     * @param properties event's properties
     * @param preventRender flag indicating whether to preventRendering or not (optional)
     */
    changeEvent: function (index, properties, preventRender) {
        this.instance.changeItem(index, properties, preventRender);
    },

    /**
     * Deletes an existing event.
     *
     * @param index index of the event.
     * @param preventRender optional boolean parameter to prevent re-render timeline immediately after delete
     * @param preventRender flag indicating whether to preventRendering or not (optional)
     *        (for multiple deletions). Default is false.
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
     * Retrieves the properties of a single event. The returned object can contain parameters
     * "start" (Date), "end" (Date), "content" (String), "group" (String).
     *
     * @return {Object}
     */
    getEvent: function (index) {
        return this.instance.getItem(index);
    },

    /**
     * Is the event by given index editable?
     *
     * @param index index of the event
     * @return {boolean} true - editable, false - not
     */
    isEditable: function(index) {
        return this.instance.isEditable(this.getEvent(index));
    },

    /**
     * Returns an object with start and end properties, which each one of them is a Date object,
     * representing the currently visible time range.
     *
     * @return {Object}
     */
    getVisibleRange: function () {
        return this.instance.getVisibleChartRange();
    },

    /**
     * Sets the visible range (zoom) to the specified range. Accepts two parameters of type Date
     * that represent the first and last times of the wanted selected visible range.
     * Set start to null to include everything from the earliest date to end;
     * set end to null to include everything from start to the last date.
     *
     * @param start start Date
     * @param end end Date
     */
    setVisibleRange: function (start, end) {
        return this.instance.setVisibleChartRange(start, end);
    },

    /**
     * Moves the timeline the given movefactor to the left or right. Start and end date will be adjusted,
     * and the timeline will be redrawn. For example, try moveFactor = 0.1 or -0.1. moveFactor is a Number
     * that determines the moving amount. A positive value will move right, a negative value will move left.
     *
     * @param moveFactor Number
     */
    move: function (moveFactor) {
        return this.instance.move(moveFactor);
    },

    /**
     * Zooms the timeline the given zoomfactor in or out. Start and end date will be adjusted,
     * and the timeline will be redrawn. You can optionally give a date around which to zoom.
     * For example, try zoomfactor = 0.1 or -0.1. zoomFactor is a Number that determines the zooming amount.
     * Positive value will zoom in, negative value will zoom out.
     * zoomAroundDate is a Date around which to zoom and it is optional.
     *
     * @param zoomFactor Number
     * @param zoomAroundDate Date
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
     * @return {Integer}
     */
    getNumberOfEvents: function () {
        return this.instance.getData().length;
    },

    /**
     * Gets index of the currently selected event or -1.
     *
     * @return {Number}
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
     * Gets currently selected event with properties or null. Properties are
     * "start" (Date), "end" (Date), "content" (String), "group" (String), "editable" (boolean).
     *
     * @return {Object} JSON object
     */
    getSelectedEvent: function () {
        var index = this.getSelectedIndex();
        if (index != -1) {
            return this.instance.getItem(index);
        }

        return null;
    },

    /**
     * Selects an event by index. The visible range will be moved,
     * so that the selected event is placed in the middle.
     * To unselect all events, use a negative index, e.g. index = -1.
     *
     * @param index
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
     * Gets instance of the Timeline object.
     *
     * @return {Object}
     */
    getInstance: function () {
        return this.instance;
    }
});