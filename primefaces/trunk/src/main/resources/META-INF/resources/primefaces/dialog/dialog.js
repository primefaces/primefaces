PrimeFaces.widget.Dialog = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
	
    jQuery(this.jqId).dialog(this.cfg);
	
    if(this.cfg.ajaxClose) {
        jQuery(this.jqId).bind('dialogclose', { dialog: this }, this.handleClose);
    }
	
    if(this.cfg.closable == false) {
        jQuery(this.jqId).parent().find('.ui-dialog-titlebar-close').hide();
    }
}

PrimeFaces.widget.Dialog.prototype.show = function() {
    jQuery(this.jqId).dialog('open');
}

PrimeFaces.widget.Dialog.prototype.hide = function() {
    jQuery(this.jqId).dialog('close');
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