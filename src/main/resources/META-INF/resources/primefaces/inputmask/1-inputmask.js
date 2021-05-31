/**
 * __PrimeFaces InputMask Widget__
 * 
 * InputMask forces an input to fit in a defined mask template.
 * 
 * @interface {PrimeFaces.widget.InputMaskCfg} cfg The configuration for the {@link  InputMask| InputMask widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {Inputmask.Options} cfg
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

        this.applyMask();

        //Visuals
        PrimeFaces.skinInput(this.jq);
    },

    /**
     * Applys the mask to the input.
     * @private
     */
    applyMask: function() {
        if(this.cfg.mask && !this.jq.is('[readonly]') && !this.jq.is(':disabled')) {
            this.jq.inputmask('remove').inputmask(this.cfg);
        }
    },

    /**
     * Sets the value of this input field to the given value. If the value does not fit the mask, it is adjusted
     * appropriately.
     * @param {string} value New value to set on this input field
     */
    setValue: function(value) {
        this.jq.inputmask("setvalue", value);
    },

    /**
     * Returns the current value of this input field including the mask like "12/31/1999".
     * @return {string} The current value of this input field with mask.
     */
    getValue: function() {
        return this.jq.val();
    },

    /**
     * Returns the current value of this input field without the mask like "12311999".
     * @return {string} The current value of this input field without mask.
     */
    getValueUnmasked: function() {
        return this.jq.inputmask('unmaskedvalue');
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        this.jq.inputmask('remove');
        PrimeFaces.utils.disableInputWidget(this.jq);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq);
        this.applyMask();
    }

});
