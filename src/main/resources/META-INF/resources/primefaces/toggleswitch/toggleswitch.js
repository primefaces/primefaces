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

        if(!this.input.is(':disabled')) {
            this._bindEvents();
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    _bindEvents: function() {
        var $this = this;

        this.slider.on('click.toggleSwitch', function(e) {
            $this.input.trigger('click').trigger('focus.toggleSwitch');
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
        })
        .on('change.toggleSwitch', function(e) {
            if($this.isChecked()) {
                $this.input.prop('checked', true).attr('aria-checked', true);
                $this.jq.addClass('ui-toggleswitch-checked');
            }
            else {
                $this.input.prop('checked', false).attr('aria-checked', false);
                $this.jq.removeClass('ui-toggleswitch-checked');
            }
        });
    },

    /**
     * Checks whether this checkbox is currently checked.
     * @return {boolean} `true` if this checkbox is checked, or `false` otherwise.
     */
    isChecked: function() {
        return this.input.prop('checked');
    },

    /**
     * Turns this switch in case it is off, or turns of off in case it is on.
     */
    toggle: function() {
        if(this.isChecked())
            this.uncheck();
        else
            this.check();
    },

    /**
     * Turns this switch on if it is not already turned on.
     */
    check: function() {
        this.input.prop('checked', true).attr('aria-checked', true).trigger('change');
        this.jq.addClass('ui-toggleswitch-checked');
    },

    /**
     * Turns this switch off if it is not already turned of.
     */
    uncheck: function() {
        this.input.prop('checked', false).attr('aria-checked', false).trigger('change');
        this.jq.removeClass('ui-toggleswitch-checked');
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
    }
});
