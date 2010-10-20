/**
 * PrimeFaces Dialog Widget
 */
PrimeFaces.widget.Dialog = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $PF(this.jqId);
	
    this.jq.dialog(this.cfg);

    var _self = this;

    //Close handler to invoke remote closeListener
    if(this.cfg.ajaxClose) {
        this.jq.bind('dialogclose', function(event, ui) {
            _self.onClose(event, ui);
        });
    }

    //Hide close icon if dialog is not closable
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

/**
 * Fires an ajax request to invoke a closeListener passing a CloseEvent
 */
PrimeFaces.widget.Dialog.prototype.onClose = function(event, ui) {
    var options = {
        source: this.id,
        process: this.id
    }
    
    if(this.cfg.onCloseUpdate) {
        options.update = this.cfg.onCloseUpdate;
    }

    var params = {};
    params[this.id + "_ajaxClose"] = true;
	
    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}