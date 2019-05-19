/**
 * PrimeFaces Layout Widget
 */
PrimeFaces.widget.Layout = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);

        if(this.cfg.full) {                                                 //full
            var dialog = $(this.cfg.center.paneSelector).parents('.ui-dialog-content');
            if(dialog.length == 1) {                                         // full + dialog
                this.jq = dialog;
            } else {
                this.jq = $('body');
            }
        } else if(this.cfg.parent) {                                        //nested
            this.jq = $(PrimeFaces.escapeClientId(this.cfg.parent));
        } else {                                                            //element
            this.jq = $(this.jqId);
        }

        this.renderDeferred();
    },

    _render: function() {
        var $this = this;

        //defaults
        this.cfg.defaults = {
            onshow: function(location,pane,state,options) { $this.onshow(location,pane,state); },
            onhide: function(location,pane,state,options) { $this.onhide(location,pane,state); },
            onopen: function(location,pane,state,options) { $this.onopen(location,pane,state); },
            onclose: function(location,pane,state,options) { $this.onclose(location,pane,state); },
            onresize_end: function(location,pane,state,options) { $this.onresize(location,pane,state); },
            contentSelector: '.ui-layout-unit-content',
            slidable: false,
            togglerLength_open: 0,
            togglerLength_closed: 23,
            togglerAlign_closed: 'top',
            togglerContent_closed: '<a href="javascript:void(0)" class="ui-layout-unit-expand-icon ui-state-default ui-corner-all"><span class="ui-icon ui-icon-arrow-4-diag"></span></a>'
        };

        this.layout = this.jq.layout(this.cfg);

        if(!this.cfg.full) {
            this.jq.css('overflow','visible');
        }

        this.bindEvents();
    },

    bindEvents: function() {
        var _self = this,
        units = this.jq.children('.ui-layout-unit'),
        resizers = this.jq.children('.ui-layout-resizer');

        units.children('.ui-layout-unit-header').children('a.ui-layout-unit-header-icon').mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        }).click(function() {
            var element = $(this),
            unit = element.parents('.ui-layout-unit:first'),
            pane = unit.data('layoutEdge');

            if(element.children('span').hasClass('ui-icon-close')) {
                _self.hide(pane);
            } else {
                _self.toggle(pane);
            }
        });

        resizers.find('a.ui-layout-unit-expand-icon').mouseover(function() {
            $(this).addClass('ui-state-hover');
        }).mouseout(function() {
            $(this).removeClass('ui-state-hover');
        });
    },

    toggle: function(location) {
        this.layout.toggle(location);
    },

    show: function(location) {
        this.layout.show(location);
    },

    hide: function(location) {
        this.layout.hide(location);
    },

    onhide: function(location, pane, state) {
        if(this.cfg.onClose) {
            this.cfg.onClose.call(this, state);
        }

        if(this.hasBehavior('close')) {
            var ext = {
                params : [
                    {name: this.id + '_unit', value: location}
                ]
            };

            this.callBehavior('close', ext);
        }
    },

    onshow: function(location, pane, state) {

    },

    onopen: function(location, pane, state) {
        pane.siblings('.ui-layout-resizer-' + location).removeClass('ui-widget-content ui-corner-all');

        if(this.cfg.onToggle) {
            this.cfg.onToggle.call(this, state);
        }

        this.fireToggleEvent(location, false);
    },

    onclose: function(location, pane, state) {
        pane.siblings('.ui-layout-resizer-' + location).addClass('ui-widget-content ui-corner-all');

        if(!state.isHidden) {
            if(this.cfg.onToggle) {
                this.cfg.onToggle.call(this, state);
            }

            this.fireToggleEvent(location, true);
        }
    },

    onresize: function(location, pane, state) {
        if(this.cfg.onResize) {
            this.cfg.onResize.call(this, state);
        }

        if(!state.isClosed && !state.isHidden) {
            if(this.hasBehavior('resize')) {
                var ext = {
                    params : [
                        {name: this.id + '_unit', value: location},
                        {name: this.id + '_width', value: state.innerWidth},
                        {name: this.id + '_height', value: state.innerHeight}
                    ]
                };

                this.callBehavior('resize', ext);
            }
        }
    },

    fireToggleEvent: function(location, collapsed) {
        if(this.hasBehavior('toggle')) {
            var ext = {
                params : [
                    {name: this.id + '_unit', value: location},
                    {name: this.id + '_collapsed', value: collapsed}
                ]
            };

            this.callBehavior('toggle', ext);
        }
    }

});