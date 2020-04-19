/**
 * __PrimeFaces ToggleSwitch Widget__
 * 
 * ToggleSwitch is used to select a boolean value.
 * 
 * > ToggleSwitch is designed to replace the old {@link InputSwitch|InputSwitch component}.
 * 
 * @prop {JQuery} input The DOM element for the hidden input field storing the value of this switch.
 * @prop {JQuery} slider The DOM element for the slider.
 * 
 * @interface {PrimeFaces.widget.ToggleSwitchCfg} cfg The configuration for the {@link  ToggleSwitch| ToggleSwitch widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.ToggleSwitch = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        
        this.slider = this.jq.children('.ui-toggleswitch-slider');
        this.input = $(this.jqId + '_input');

        if(!this.input.prop('disabled')) {
            this._bindEvents();
        }
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    _bindEvents: function() {
        var $this = this;

        this.jq.on('click.toggleSwitch', function(e) {
            $this.toggle();
            $this.input.trigger('focus');
        });

        this.input.on('focus.toggleSwitch', function(e) {
            $this.jq.addClass('ui-toggleswitch-focus');
        })
        .on('blur.toggleSwitch', function(e) {
            $this.jq.removeClass('ui-toggleswitch-focus');
        })
        .on('keydown.toggleSwitch', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                e.preventDefault();
            }
        })
        .on('keyup.toggleSwitch', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                $this.toggle();

                e.preventDefault();
            }
        });
    },

    /**
     * Turns this switch in case it is off, or turns of off in case it is on.
     */
    toggle: function() {
        if(this.input.prop('checked'))
            this.uncheck();
        else
            this.check();
    },

    /**
     * Turns this switch on if it is not already turned on.
     */
    check: function() {
        this.input.prop('checked', true).trigger('change');
        this.jq.attr('aria-checked', true).addClass('ui-toggleswitch-checked');
    },

    /**
     * Turns this switch off if it is not already turned of.
     */
    uncheck: function() {
        this.input.prop('checked', false).trigger('change');
        this.jq.attr('aria-checked', false).removeClass('ui-toggleswitch-checked');
    }
});
