PrimeFaces.widget.Rating = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);

    if(this.cfg.hasRateListener) {
        var _self = this;
        
        this.cfg.callback = function(value, link) {
            var options = {
                source: _self.id,
                process: _self.id,
                formId: _self.cfg.formId
            };

            if(_self.cfg.update) {
                options.update = _self.cfg.update;
            }

            var params = {};
            params[_self.id + '_ajaxRating'] = true;
	
            PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
        };
    }
	
    jQuery(this.jqId + ' .ui-rating-star').rating(this.cfg);
}