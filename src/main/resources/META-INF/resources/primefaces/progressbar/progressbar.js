PrimeFaces.widget.ProgressBar = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);

    if(this.cfg.ajax) {
        this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
    }
	
    jQuery(this.jqId).progressbar(this.cfg);
}

PrimeFaces.widget.ProgressBar.prototype.setValue = function(value) {
    jQuery(this.jqId).progressbar('value', value);
}

PrimeFaces.widget.ProgressBar.prototype.getValue  = function() {
    return jQuery(this.jqId).progressbar('value');
}

PrimeFaces.widget.ProgressBar.prototype.start = function() {
    var _self = this;
	
    if(this.cfg.ajax) {
		
        this.progressPoll = setInterval(function() {
            var options = {
                source: _self.id,
                process: _self.id,
                formId: _self.cfg._formId,
                async: true,
                oncomplete: function(xhr, status, args) {
                    var value = args[_self.id + '_value'];
                    _self.setValue(value);

                    //trigger close listener
                    if(value === 100) {
                        _self.fireCompleteEvent();
                    }
                }
            };

            PrimeFaces.ajax.AjaxRequest(options);
            
        }, this.cfg.interval);
    }
}

PrimeFaces.widget.ProgressBar.prototype.fireCompleteEvent = function() {
    clearInterval(this.progressPoll);

    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId,
        async: true
    };

    if(this.cfg.onCompleteUpdate)
        options.update = this.cfg.onCompleteUpdate;
    if(this.cfg.oncomplete)
        options.oncomplete = this.cfg.oncomplete;

    var params = {};
    params[this.id + '_complete'] = true;

    options.params = params;
	
    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.ProgressBar.prototype.cancel = function() {
    clearInterval(this.progressPoll);
    var _self = this;

    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId,
        async: true,
        oncomplete:function(xhr, status, args) {
            _self.setValue(0);
        }
    };

    if(this.cfg.onCancelUpdate) {
        options.update = this.cfg.onCancelUpdate;
    }

    var params = {};
    params[this.id + '_cancel'] = true;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}