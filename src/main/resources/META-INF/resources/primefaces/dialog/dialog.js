PrimeFaces.widget.Dialog = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $PF(this.jqId);
	
    this.jq.dialog(this.cfg);
	
    if(this.cfg.ajaxClose) {
        this.jq.bind('dialogclose', { dialog: this }, this.handleClose);
    }
	
    if(this.cfg.closable == false) {
        this.jq.parent().find('.ui-dialog-titlebar-close').hide();
    }
}

PrimeFaces.widget.Dialog.prototype.show = function() {
    this.jq.dialog('open');
}

PrimeFaces.widget.Dialog.prototype.hide = function() {
    this.jq.dialog('close');
}

PrimeFaces.widget.Dialog.prototype.handleClose = function(event, ui) {
    var _self = event.data.dialog;

    var options = {
        source: _self.id,
        process: _self.id
    }
    
    if(_self.cfg.onCloseUpdate) {
        options.update = _self.cfg.onCloseUpdate;
    }

    var params = {};
    params[_self.id + "_ajaxClose"] = true;
	
    PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
}