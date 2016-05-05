PrimeFaces.widget.InputNumber = PrimeFaces.widget.BaseWidget.extend({

    /**
	 * Initializes the widget.
	 *
	 * @param {object} cfg The widget configuration.
	 */
    init : function(cfg) {
        this._super(cfg);
        this.inputInternal = $(this.jqId + '_hinput');
        this.inputExternal = $(this.jqId + '_input');
        this.plugOptArray =   cfg.pluginOptions;
        this.valueToRender =  cfg.valueToRender;
        this.disabled = cfg.disabled;

        var $this = this;

        //bind events if not disabled
        if (this.disabled) {
            this.inputExternal.attr("disabled", "disabled");
            this.inputExternal.addClass("ui-state-disabled");
            this.inputInternal.attr("disabled", "disabled");
        }


        //Visual effects
        PrimeFaces.skinInput(this.inputExternal);

        // copy the value from the external input to the hidden input
        var originalOnkeyup = this.inputExternal.prop('onkeyup');
        this.inputExternal.removeProp('onkeyup').off('keyup').on('keyup', function(e) {
            var proceed = true;
            if (originalOnkeyup) {
                proceed = originalOnkeyup.call(this, e);
            }
            if (proceed !== false) {
                var keyCode = e.which;
                if (keyCode === 8 || keyCode === 13 || keyCode === 32
                    || ( keyCode >= 46 && keyCode <= 90)
                    || ( keyCode >= 96 && keyCode <= 111)
                    || ( keyCode >= 186 && keyCode <= 222)) {
                    $this.copyValueToHiddenInput();
                }
            }
        });

        // also copy values on onchange - see #293
        // backup onchange, should be executed after our onchange
        var originalOnchange = this.inputExternal.prop('onchange');
        this.inputExternal.removeProp('onchange').off('change').on('change', function(e) {
            var proceed = true;
            if (originalOnchange) {
                proceed = originalOnchange.call(this, e);
            }
            if (proceed !== false) {
                $this.copyValueToHiddenInput();
            }
        });

        var originalOnkeydown = this.inputExternal.prop('onkeydown');
        this.inputExternal.removeProp('onkeydown').off('keydown').on('keydown', function(e) {
            var proceed = true;
            if (originalOnkeydown) {
                proceed = originalOnkeydown.call(this, e);
            }
            if (proceed !== false) {
                $this.copyValueToHiddenInput();
            }
        });

        this.inputExternal.autoNumeric('init', this.plugOptArray);

        if (this.valueToRender !== ""){
            //set the value to the external input the plugin will format it.
            this.inputExternal.autoNumeric('set',this.valueToRender);
            //then copie the value to the internal input
            var cleanVal = $this.inputExternal.autoNumeric('get');
            $this.inputInternal.attr('value', cleanVal);
        }else{
            $this.inputInternal.removeAttr('value');
        }
    },

    copyValueToHiddenInput : function() {
        var cleanVal = this.inputExternal.autoNumeric('get');
        if (cleanVal !== ""){
            this.inputInternal.attr('value', cleanVal);
        } else {
            this.inputInternal.removeAttr('value');
        }
    },

    enable : function() {
        this.inputExternal.removeAttr("disabled");
        this.inputExternal.removeClass("ui-state-disabled");
        this.inputInternal.removeAttr("disabled");
        this.disabled = false;
    },

    disable : function() {
        this.inputExternal.attr("disabled", "disabled");
        this.inputExternal.addClass("ui-state-disabled");
        this.inputInternal.attr("disabled", "disabled");
        this.disabled = true;
    },

    setValue : function(value){
        this.inputExternal.autoNumeric('set', value);
        var cleanVal = this.inputExternal.autoNumeric('get');
        this.inputInternal.attr('value', cleanVal);
    },

    getValue : function(){
         return this.inputExternal.autoNumeric('get');
    }
});
