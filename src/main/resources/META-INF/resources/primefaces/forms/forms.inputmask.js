/**
 * PrimeFaces InputMask Widget
 */
PrimeFaces.widget.InputMask = PrimeFaces.widget.BaseWidget.extend({

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

    setValue: function(value) {
        this.jq.val(value);
        this.jq.unmask().mask(this.cfg.mask, this.cfg);
    },

    getValue: function() {
        return this.jq.val();
    }

});
