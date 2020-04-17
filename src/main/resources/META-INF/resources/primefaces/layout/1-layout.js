/**
 * __PrimeFaces Layout Widget__
 * 
 * Layout component features a highly customizable borderLayout model making it very easy to create complex layouts even
 * if you’re not familiar with web design.
 * 
 * > __Layout and LayoutUnit are deprecated__, use FlexGrid or GridCSS instead. They'll be removed on 9.0.
 * 
 * @deprecated
 * 
 * @prop {JQueryLayout.LayoutInstance} layout The layout instance that was created.
 * 
 * @typedef PrimeFaces.widget.Layout.OnCloseCallback Callback that is invoked when a border pane is closed. See also
 * {@link LayoutCfg.onClose}.
 * @this {PrimeFaces.widget.Layout} PrimeFaces.widget.Layout.OnCloseCallback 
 * @param {JQueryLayout.BorderPaneState} PrimeFaces.widget.Layout.OnCloseCallback.state The state of the border pane
 * that was closed.
 * 
 * @typedef PrimeFaces.widget.Layout.OnResizeCallback Callback that is invoked when a border pane is opened. See also
 * {@link LayoutCfg.onResize}.
 * @this {PrimeFaces.widget.Layout} PrimeFaces.widget.Layout.OnResizeCallback 
 * @param {JQueryLayout.BorderPaneState} PrimeFaces.widget.Layout.OnResizeCallback.state The state of the border pane
 * that was opened.
 * 
 * @typedef PrimeFaces.widget.Layout.OnToggleCallback Callback that is invoked when a border pane is opened or closed.
 * See also {@link LayoutCfg.onToggle}.
 * @this {PrimeFaces.widget.Layout} PrimeFaces.widget.Layout.OnToggleCallback 
 * @param {JQueryLayout.BorderPaneState} PrimeFaces.widget.Layout.OnToggleCallback.state The state of the border pane
 * that was opened or closed.
 * 
 * @interface {PrimeFaces.widget.LayoutCfg} cfg The configuration for the {@link  Layout| Layout widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {JQueryLayout.SubKeyLayoutSettings} cfg
 * 
 * @prop {boolean} cfg.full Specifies whether layout should span all page or not.
 * @prop {PrimeFaces.widget.Layout.OnCloseCallback} cfg.onClose Callback that is invoked when a border pane is closed.
 * @prop {PrimeFaces.widget.Layout.OnResizeCallback} cfg.onResize Callback that is invoked when a border pane is
 * opened.
 * @prop {PrimeFaces.widget.Layout.OnToggleCallback} cfg.onToggle Callback that is invoked when a border pane is opened
 * or closed.
 * @prop {string} cfg.parent ID of the element on which this layout widget should be initialized.
 */
PrimeFaces.widget.Layout = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
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

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
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

    /**
     * Toggles the given border pane, either hiding it or showing it.
     * @param {JQueryLayout.BorderPane} location The border pane to toggle.
     */
    toggle: function(location) {
        this.layout.toggle(location);
    },

    /**
     * Shows given border pane, if not already visible.
     * @param {JQueryLayout.BorderPane} location The border pane to show. 
     */
    show: function(location) {
        this.layout.show(location);
    },

    /**
     * Hides given border pane, if not already hidden.
     * @param {JQueryLayout.BorderPane} location The border pane to hide.
     */
    hide: function(location) {
        this.layout.hide(location);
    },

    /**
     * Callback that is invoked when a border pane is hidden. Invokes the appropriate behaviors.
     * @param {JQueryLayout.BorderPane} location The type of border pane that was hidden. 
     * @param {JQuery} pane DOM element of the pane that was hidden. 
     * @param {JQueryLayout.BorderPaneState} state UI state of the pane that was hidden.
     * @private
     */
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

    /**
     * Callback that is invoked when a border pane is shown.
     * @param {JQueryLayout.BorderPane} location The type of border pane that was shown.
     * @param {JQuery} pane DOM element of the pane that was shown.
     * @param {JQueryLayout.BorderPaneState} state UI state of the pane that was shown.
     * @private
     */
    onshow: function(location, pane, state) {

    },

    /**
     * Callback that is invoked when a border pane is opened.
     * @param {JQueryLayout.BorderPane} location The type of border pane that was opened.
     * @param {JQuery} pane DOM element of the pane that was opened.
     * @param {JQueryLayout.BorderPaneState} state UI state of the pane that was opened.
     * @private
     */
    onopen: function(location, pane, state) {
        pane.siblings('.ui-layout-resizer-' + location).removeClass('ui-widget-content ui-corner-all');

        if(this.cfg.onToggle) {
            this.cfg.onToggle.call(this, state);
        }

        this.fireToggleEvent(location, false);
    },

    /**
     * Callback that is invoked when a border pane is closed.
     * @param {JQueryLayout.BorderPane} location The type of border pane that was closed.
     * @param {JQuery} pane DOM element of the pane that was closed.
     * @param {JQueryLayout.BorderPaneState} state UI state of the pane that was closed.
     * @private
     */
    onclose: function(location, pane, state) {
        pane.siblings('.ui-layout-resizer-' + location).addClass('ui-widget-content ui-corner-all');

        if(!state.isHidden) {
            if(this.cfg.onToggle) {
                this.cfg.onToggle.call(this, state);
            }

            this.fireToggleEvent(location, true);
        }
    },

    /**
     * Callback that is invoked when a border pane is resized.
     * @param {JQueryLayout.BorderPane} location The type of border pane that was resized.
     * @param {JQuery} pane DOM element of the pane that was resized.
     * @param {JQueryLayout.BorderPaneState} state UI state of the pane that was resized.
     * @private
     */
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

    /**
     * Fires the appropriate behaviors for when a border pane is shown or hidden.
     * @param {JQueryLayout.BorderPane} location The type of border pane that was resized.
     * @param {boolean} collapsed `true` if the pane was hidden (collapsed), `false` if it was shown (expanded). 
     * @private 
     */
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