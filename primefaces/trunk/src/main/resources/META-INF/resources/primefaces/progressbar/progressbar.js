PrimeFaces.widget.ProgressBar = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    if(this.cfg.ajax) {
        this.cfg.formId = this.jq.parents('form:first').attr('id');
    }
	
    this.jq.progressbar(this.cfg);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ProgressBar, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.ProgressBar.prototype.setValue = function(value) {
    this.jq.progressbar('value', value);
}

PrimeFaces.widget.ProgressBar.prototype.getValue  = function() {
    return this.jq.progressbar('value');
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

    if(this.cfg.behaviors) {
        var completeBehavior = this.cfg.behaviors['complete'];
        
        if(completeBehavior) {
            completeBehavior.call(this);
        }
    }
}

PrimeFaces.widget.ProgressBar.prototype.cancel = function() {
    clearInterval(this.progressPoll);
    this.setValue(0);
}