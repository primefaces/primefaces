/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinInput(this.jq);
    },

    disable: function() {
        this.jq.prop('disabled', true).addClass('ui-state-disabled');
    },

    enable: function() {
        this.jq.prop('disabled', false).removeClass('ui-state-disabled');
    }
});
