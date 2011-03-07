PrimeFaces.widget.Resizable = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.target = PrimeFaces.escapeClientId(this.cfg.target);

    if(this.cfg.ajaxResize) {
        this.cfg.formId = $(this.target).parents('form:first').attr('id');
    }

    var _self = this;

    this.cfg.stop = function(event, ui) {
        if(_self.cfg.onStop) {
            _self.cfg.onStop.call(_self, event, ui);
        }

        if(_self.cfg.ajaxResize) {
            _self.fireAjaxResizeEvent(event, ui);
        }
    }

    this.cfg.start = function(event, ui) {
        if(_self.cfg.onStart) {
            _self.cfg.onStart.call(_self, event, ui);
        }
    }
    
    this.cfg.resize = function(event, ui) {
        if(_self.cfg.onResize) {
            _self.cfg.onResize.call(_self, event, ui);
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

    options.params = params;
    
    PrimeFaces.ajax.AjaxRequest(options);
}