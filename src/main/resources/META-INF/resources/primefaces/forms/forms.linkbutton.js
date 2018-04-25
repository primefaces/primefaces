/**
 * PrimeFaces LinkButton Widget
 */
PrimeFaces.widget.LinkButton = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    }

});