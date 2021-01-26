/**
 * __PrimeFaces Resizable Widget__
 * 
 * Resizable component is used to make another JSF component resizable.
 * 
 * @typedef PrimeFaces.widget.Resizable.OnResizeCallback Client-side callback to execute during resizing. See also
 * {@link ResizableCfg.onResize}.
 * @this {PrimeFaces.widget.Resizable} PrimeFaces.widget.Resizable.OnResizeCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Resizable.OnResizeCallback.event The event that triggered the resize.
 * @param {JQueryUI.ResizableUIParams} PrimeFaces.widget.Resizable.OnResizeCallback.ui The details about the resize.
 * 
 * @typedef PrimeFaces.widget.Resizable.OnStartCallback Client-side callback to execute when resizing begins. See also
 * {@link ResizableCfg.onStart}.
 * @this {PrimeFaces.widget.Resizable} PrimeFaces.widget.Resizable.OnStartCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Resizable.OnStartCallback.event The event that triggered the resizing to
 * start.
 * @param {JQueryUI.ResizableUIParams} PrimeFaces.widget.Resizable.OnStartCallback.ui Details about the resize.
 * 
 * @typedef PrimeFaces.widget.Resizable.OnStopCallback Client-side callback to execute after resizing end. See also
 * {@link ResizableCfg.onStop}.
 * @this {PrimeFaces.widget.Resizable} PrimeFaces.widget.Resizable.OnStopCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Resizable.OnStopCallback.event The event that triggered the resize to end.
 * @param {JQueryUI.ResizableUIParams} PrimeFaces.widget.Resizable.OnStopCallback.ui Details about the resize.
 * 
 * @prop {JQuery} jqTarget The DOM element for the target widget t o be resized.
 * 
 * @interface {PrimeFaces.widget.ResizableCfg} cfg The configuration for the {@link  Resizable| Resizable widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.ajaxResize Whether AJAX requests are sent when the element is resized.
 * @prop {string} cfg.containment ID of the element to which the target widget is constrained.
 * @prop {string} cfg.formId ID of the form to use for AJAX requests.
 * @prop {PrimeFaces.widget.Resizable.OnResizeCallback} cfg.onResize Client-side callback to execute during resizing.
 * @prop {PrimeFaces.widget.Resizable.OnStartCallback} cfg.onStart Client-side callback to execute when resizing
 * begins.
 * @prop {PrimeFaces.widget.Resizable.OnStopCallback} cfg.onStop Client-side callback to execute after resizing end.
 * @prop {string} cfg.parentComponentId ID of the parent of the resizable element. 
 * @prop {JQueryUI.ResizableEvent} cfg.resize Callback passed to JQuery UI for when a resizing event occurs.
 * @prop {JQueryUI.ResizableEvent} cfg.start Callback passed to JQuery UI for when a resizing event starts.
 * @prop {JQueryUI.ResizableEvent} cfg.stop Callback passed to JQuery UI for when a resizing event ends.
 * @prop {string} cfg.target ID of the target widget or element to be resized. 
 */
PrimeFaces.widget.Resizable = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));

        this.renderDeferred();
    },

    /**
     * Renders this widget, if the target widget is already visible, or adds a deferred renderer otherwise.
     * @private
     */
    renderDeferred: function() {
        if(this.jqTarget.is(':visible')) {
            this._render();
        }
        else {
            var container = this.jqTarget.parent()[0].closest('.ui-hidden-container');
            if (container) {
                var $container = $(container);
                if ($container.length) {
                    var $this = this;
                    PrimeFaces.addDeferredRender(this.id, $container.attr('id'), function() {
                        return $this.render();
                    });
                }
            }
        }
    },

    /**
     * Renders the client-side parts of this widget, if this target widget to be resized is already visible.
     * @private
     * @return {boolean} `true` if the target widget is visible, or `false` otherwise.
     */
    render: function() {
        if(this.jqTarget.is(':visible')) {
            this._render();
            return true;
        }

        return false;
    },

    /**
     * Renders the client-side parts of this widget.
     * @private
     */
    _render: function() {
        if(this.cfg.ajaxResize) {
            this.cfg.formId = $(this.target).parents('form:first').attr('id');
        }

        if (this.cfg.isContainment) {
        	this.cfg.containment = PrimeFaces.escapeClientId(this.cfg.parentComponentId);
        }

        var _self = this;

        this.cfg.stop = function(event, ui) {
            if(_self.cfg.onStop) {
                _self.cfg.onStop.call(_self, event, ui);
            }

            _self.fireAjaxResizeEvent(event, ui);
        };

        this.cfg.start = function(event, ui) {
            if(_self.cfg.onStart) {
                _self.cfg.onStart.call(_self, event, ui);
            }
        };

        this.cfg.resize = function(event, ui) {
            if(_self.cfg.onResize) {
                _self.cfg.onResize.call(_self, event, ui);
            }
        };

        this.jqTarget.resizable(this.cfg);

        this.removeScriptElement(this.id);
    },

    /**
     * Triggers the behavior for when the component was resized.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that triggered the resize.
     * @param {JQueryUI.ResizableUIParams} ui Data of the resize event.
     */
    fireAjaxResizeEvent: function(event, ui) {
        if(this.hasBehavior('resize')) {
            var ext = {
                params: [
                    {name: this.id + '_width', value: parseInt(ui.helper.width())},
                    {name: this.id + '_height', value: parseInt(ui.helper.height())}
                ]
            };

            this.callBehavior('resize', ext);
        }
    }

});