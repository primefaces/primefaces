/**
 * PrimeFaces Watermark Widget
 */
PrimeFaces.widget.Watermark = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);

        if(this.target.is(':not(:input)')) {
            this.target = this.target.find(':input');
        }

        this.target.attr('placeholder', this.cfg.value);

        this.removeScriptElement(this.id);
    }

});