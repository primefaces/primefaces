PrimeFaces.widget.InputNumber = PrimeFaces.widget.BaseWidget.extend({

    /**
     * Initializes the widget.
     *
     * @param {object} cfg The widget configuration.
     */
    init: function (cfg) {
        this._super(cfg);
        this.input = $(this.jqId + '_input');
        this.hiddenInput = $(this.jqId + '_hinput');
        this.plugOptArray = cfg.pluginOptions;
        this.valueToRender = cfg.valueToRender;
        this.disabled = cfg.disabled;

        //bind events if not disabled
        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.input.addClass("ui-state-disabled");
            this.hiddenInput.attr("disabled", "disabled");
        }

        //Visual effects
        PrimeFaces.skinInput(this.input);

        this.wrapEvents();

        this.input.autoNumeric('init', this.plugOptArray);

        if (this.valueToRender !== "") {
            //set the value to the input the plugin will format it.
            this.input.autoNumeric('set', this.valueToRender);
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        this.hiddenInput.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Wraps the events on the external (visible) input to copy the value to the hidden input,
     * before calling the callback.
     */
    wrapEvents: function() {
        var $this = this;

        // copy the value from the input to the hidden input
        var originalOnkeyup = this.input.prop('onkeyup');
        this.input.prop('onkeyup', null).off('keyup').on('keyup', function (e) {

            var oldValue;

            var keyCode = e.which;
            if (keyCode === 8 || keyCode === 13 || keyCode === 32
                    || (keyCode >= 46 && keyCode <= 90)
                    || (keyCode >= 96 && keyCode <= 111)
                    || (keyCode >= 186 && keyCode <= 222)) {

                oldValue = $this.copyValueToHiddenInput();
            }

            if (originalOnkeyup && originalOnkeyup.call(this, e) === false) {
                if (oldValue) {
                    $this.setValueToHiddenInput(oldValue);
                }
                return false;
            }
        });

        var originalOnchange = this.input.prop('onchange');
        this.input.prop('onchange', null).off('change').on('change', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnchange && originalOnchange.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
                return false;
            }
        });

        var originalOnkeydown = this.input.prop('onkeydown');
        this.input.prop('onkeydown', null).off('keydown').on('keydown', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnkeydown && originalOnkeydown.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
                return false;
            }
        });
    },

    copyValueToHiddenInput: function() {
        var oldVal = this.hiddenInput.val();

        var newVal = this.input.autoNumeric('get');

        if (oldVal !== newVal) {
            this.setValueToHiddenInput(newVal);
        }

        return oldVal;
    },

    setValueToHiddenInput: function(value) {
        this.hiddenInput.val(value);
    },

    enable: function () {
        this.input.removeAttr("disabled");
        this.input.removeClass("ui-state-disabled");
        this.hiddenInput.removeAttr("disabled");
        this.disabled = false;
    },

    disable: function () {
        this.input.attr("disabled", "disabled");
        this.input.addClass("ui-state-disabled");
        this.hiddenInput.attr("disabled", "disabled");
        this.disabled = true;
    },

    setValue: function (value) {
        this.input.autoNumeric('set', value);
        var cleanVal = this.input.autoNumeric('get');
        this.hiddenInput.attr('value', cleanVal);
    },

    getValue: function () {
        return this.input.autoNumeric('get');
    }
});
