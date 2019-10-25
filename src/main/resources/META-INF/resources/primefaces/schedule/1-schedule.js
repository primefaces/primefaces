/**
 * PrimeFaces Schedule Widget
 */
PrimeFaces.widget.Schedule = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.cfg.formId = this.jq.closest('form').attr('id');
        this.cfg.theme = true;
        this.viewNameState = $(this.jqId + '_view');
        this.cfg.urlTarget = this.cfg.urlTarget || "_blank";
        this.cfg.plugins = [ 'interaction', 'dayGrid', 'timeGrid'];

        if(this.cfg.defaultDate) {
            this.cfg.defaultDate = moment(this.cfg.defaultDate);
        }

        this.setupEventSource();

        this.configureLocale();

        if(this.cfg.tooltip) {
            this.tip = $('<div class="ui-tooltip ui-widget ui-widget-content ui-shadow ui-corner-all"></div>').appendTo(this.jq);
        }

        this.setupEventHandlers();

        if(this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.setViewOptions();

        this.renderDeferred();
    },

    _render: function() {
        var _self = this;
        var calendarEl = document.getElementById(this.cfg.id);
        _self.calendar = new FullCalendar.Calendar(calendarEl, this.cfg);
        _self.calendar.render();

        this.bindViewChangeListener();
    },

    configureLocale: function() {
        var lang = PrimeFaces.locales[this.cfg.locale];

        if(lang) {
            this.cfg.firstDay = lang.firstDay;
            /*
            this.cfg.buttonText = {today: lang.currentText
                                  ,month: lang.month
                                  ,week: lang.week
                                  ,day: lang.day};
            this.cfg.allDayText = lang.allDayText;
            this.cfg.weekNumberTitle = lang.weekNumberTitle;
            if(lang.eventLimitText) {
                this.cfg.eventLimitText = lang.eventLimitText;
            }
             */
        }
    },

    setupEventHandlers: function() {
        var $this = this;

        this.cfg.dateClick = function(dateClickInfo) {
            if($this.hasBehavior('dateSelect')) {
                var ext = {
                    params: [
                        {name: $this.id + '_selectedDate', value: dateClickInfo.date.toISOString()}
                    ]
                };

                $this.callBehavior('dateSelect', ext);
            }
        };

        this.cfg.eventClick = function(eventClickInfo) {
            if (eventClickInfo.event.url) {
                var targetWindow = window.open('', $this.cfg.urlTarget);
                if ($this.cfg.noOpener) {
                    targetWindow.opener = null;    
                }
                targetWindow.location = targetWindow.event.url;
                return false;
            }

            if($this.hasBehavior('eventSelect')) {
                var ext = {
                    params: [
                        {name: $this.id + '_selectedEventId', value: eventClickInfo.event.id}
                    ]
                };

                $this.callBehavior('eventSelect', ext);
            }
        };

        this.cfg.eventDrop = function(eventDropInfo) {
            if($this.hasBehavior('eventMove')) {
                var ext = {
                    params: [
                        {name: $this.id + '_movedEventId', value: eventDropInfo.event.id},
                        //TODO: years and months?
                        {name: $this.id + '_dayDelta', value: eventDropInfo.delta.days},
                        {name: $this.id + '_minuteDelta', value: (eventDropInfo.delta.milliseconds/60000)}
                    ]
                };

                $this.callBehavior('eventMove', ext);
            }
        };

        this.cfg.eventResize = function(eventResizeInfo) {
            if($this.hasBehavior('eventResize')) {
                var ext = {
                    params: [
                        {name: $this.id + '_resizedEventId', value: eventResizeInfo.event.id},
                        //TODO: years and months?
                        {name: $this.id + '_dayDelta', value: eventResizeInfo.endDelta.days},
                        {name: $this.id + '_minuteDelta', value: (eventResizeInfo.endDelta.milliseconds/60000)}
                        //TODO: startDelta???
                    ]
                };

                $this.callBehavior('eventResize', ext);
            }
        };

        if(this.cfg.tooltip) {
            this.cfg.eventMouseEnter = function(mouseEnterInfo) {
                if(mouseEnterInfo.event.description) {
                    $this.tipTimeout = setTimeout(function() {
                        $this.tip.css({
                            'left': mouseEnterInfo.jsEvent.pageX,
                            'top': mouseEnterInfo.jsEvent.pageY + 15,
                            'z-index': ++PrimeFaces.zindex
                        });
                        $this.tip[0].innerHTML = mouseEnterInfo.event.description;
                        $this.tip.show();
                    }, 150);
                }
            };

            this.cfg.eventMouseLeave = function(mouseLeaveInfo) {
                if($this.tipTimeout) {
                    clearTimeout($this.tipTimeout);
                }

                if($this.tip.is(':visible')) {
                    $this.tip.hide();
                    $this.tip.text('');
                }
            };
        } else {
            // PF #2795 default to regular tooltip
            this.cfg.eventRender = function(info) {
                if(info.event.description) {
                    element.attr('title', info.event.description);
                }
            };
        }
    },

    setupEventSource: function() {
        var $this = this;

        this.cfg.events = function(fetchInfo, successCallback) {
            //var offset = start.utcOffset()*60000; // <-- #2977: assume start,end in same zone
            var options = {
                source: $this.id,
                process: $this.id,
                update: $this.id,
                formId: $this.cfg.formId,
                params: [
                    {name: $this.id + '_start', value: fetchInfo.start.valueOf() /*+ offset*/},
                    {name: $this.id + '_end', value: fetchInfo.end.valueOf() /*+ offset*/}
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            successCallback(JSON.parse(content).events);
                        }
                    });

                    return true;
                }
            };

            PrimeFaces.ajax.Request.handle(options);
        };
    },

    update: function() {
        var _self = this;
        _self.calendar.refetchEvents();
    },

    bindViewChangeListener: function() {
        var excludedClasses = '.fc-prev-button,.fc-next-button,.fc-prevYear-button,.fc-nextYear-button,.fc-today-button';
        var viewButtons = this.jq.find('> .fc-toolbar button:not(' + excludedClasses + ')'),
            $this = this;

        viewButtons.each(function(i) {
            var viewButton = viewButtons.eq(i),
                buttonClasses = viewButton.attr('class').split(' ');
            for(var i = 0; i < buttonClasses.length; i++) {
                var buttonClassParts = buttonClasses[i].split('-');
                if(buttonClassParts.length === 3) {
                    viewButton.data('view', buttonClassParts[1]);
                    break;
                }
            }
        });

        viewButtons.on('click.schedule', function() {
            var viewName = $(this).data('view');

            $this.viewNameState.val(viewName);

            $this.callBehavior('viewChange');
        });
    },

    setViewOptions: function() {
        var views = {
            month: {},       // month view
            week: {},        // basicWeek & agendaWeek views
            day: {},         // basicDay & agendaDay views
            agenda: {},      // agendaDay & agendaWeek views
            agendaDay: {},   // agendaDay view
            agendaWeek: {}   // agendaWeek view
        };

        var columnFormat = this.cfg.columnFormatOptions;
        if(columnFormat) {
            for (var view in views) {
                if(view == "agendaWeek") {  // Github #2421
                    views[view] = {columnHeaderFormat: columnFormat['week']};
                }
                else {
                    views[view] = {columnHeaderFormat: columnFormat[view]};
                }
            }
        }

        this.cfg.views = this.cfg.views||{};
        $.extend(true, this.cfg.views, views);
    }

});
