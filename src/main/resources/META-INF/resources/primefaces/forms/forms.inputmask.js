/**
 * __PrimeFaces InputMask Widget__
 * 
 * InputMask forces an input to fit in a defined mask template.
 * 
 * @interface {PrimeFaces.widget.InputMaskCfg} cfg The configuration for the {@link  InputMask| InputMask widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryMaskedInput.MaskSettings} cfg
 * 
 * @prop {string} cfg.mask The mask template to use.
 */
PrimeFaces.widget.InputMask = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.mask) {
            var originalOnchange = this.jq.prop('onchange');
            if(originalOnchange) {
                this.cfg.onChange = originalOnchange;
                this.jq.prop('onchange', null);
            }
            
            this.jq.mask(this.cfg.mask, this.cfg);
        }

        //Visuals
        PrimeFaces.skinInput(this.jq);
    },

    /**
     * Sets the value of this input field to the given value. If the value does not fit the mask, it is adjusted
     * appropriately.
     * @param {string} value New value to set on this input field
     */
    setValue: function(value) {
        this.jq.val(value);
        this.jq.unmask().mask(this.cfg.mask, this.cfg);
    },

    /**
     * Returns the current value of this input field.
     * @return {string} The current value of this input field.
     */
    getValue: function() {
        return this.jq.val();
    }

});
