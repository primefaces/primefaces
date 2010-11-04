PrimeFaces.widget.Resizable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.target = PrimeFaces.escapeClientId(this.cfg.target);

    //resize handler
    var _self = this;
    this.cfg.stop = function(event, ui) {
        if(_self.cfg.onResize) {
            _self.cfg.onResize.call(_self, event, ui);
        }

        if(_self.cfg.ajaxResize) {
            _self.fireAjaxResizeEvent(event, ui);
        }
    }

    jQuery(this.target).resizable(this.cfg);
}

PrimeFaces.widget.Resizable.prototype.fireAjaxResizeEvent = function(event, ui) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onResizeUpdate) {
        options.update = this.cfg.onResizeUpdate;
    }

    var params = {};
    params[this.id + '_ajaxResize'] = true;
    params[this.id + '_width'] = ui.helper.width();
    params[this.id + '_height'] = ui.helper.height();
    
    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}