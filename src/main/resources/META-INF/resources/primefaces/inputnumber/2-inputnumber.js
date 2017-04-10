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

        var $this = this;

        //bind events if not disabled
        if (this.disabled) {
            this.input.attr("disabled", "disabled");
            this.input.addClass("ui-state-disabled");
            this.hiddenInput.attr("disabled", "disabled");
        }

        //Visual effects
        PrimeFaces.skinInput(this.input);

        // copy the value from the input to the hidden input
        var originalOnkeyup = this.input.prop('onkeyup');
        this.input.removeProp('onkeyup').off('keyup').on('keyup', function (e) {

            var keyCode = e.which;
            if (keyCode === 8 || keyCode === 13 || keyCode === 32
                    || (keyCode >= 46 && keyCode <= 90)
                    || (keyCode >= 96 && keyCode <= 111)
                    || (keyCode >= 186 && keyCode <= 222)) {

                var oldValue = $this.copyValueToHiddenInput();
                if (originalOnkeyup && originalOnkeyup.call(this, e) === false) {
                    $this.setValueToHiddenInput(oldValue);
                }
            }
            else {
                if (originalOnkeyup) {
                    originalOnkeyup.call(this, e);
                }
            }
        });

        // also copy values on onchange - see #293
        // backup onchange, should be executed after our onchange
        var originalOnchange = this.input.prop('onchange');
        this.input.removeProp('onchange').off('change').on('change', function (e) {

            var oldValue = $this.copyValueToHiddenInput();
            if (originalOnchange && originalOnchange.call(this, e) === false) {
                $this.setValueToHiddenInput(oldValue);
            }
        });

        var originalOnkeydown = this.input.prop('onkeydown');
        this.input.removeProp('onkeydown').off('keydown').on('keydown', function (e) {
            setTimeout(function () {

                var oldValue = $this.copyValueToHiddenInput();
                if (originalOnkeydown && originalOnkeydown.call(this, e) === false) {
                    $this.setValueToHiddenInput(oldValue);
                }
            }, 1);
        });

        this.input.autoNumeric('init', this.plugOptArray);

        if (this.valueToRender !== "") {
            //set the value to the input the plugin will format it.
            this.input.autoNumeric('set', this.valueToRender);
        }

        this.copyValueToHiddenInput();

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    copyValueToHiddenInput: function () {
        var oldVal = this.hiddenInput.val();

        var newVal = this.input.autoNumeric('get');
        this.setValueToHiddenInput(newVal);

        return oldVal;
    },

    setValueToHiddenInput: function (value) {
        if (value !== "") {
            this.hiddenInput.attr('value', value);
        } else {
            this.hiddenInput.removeAttr('value');
        }
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
