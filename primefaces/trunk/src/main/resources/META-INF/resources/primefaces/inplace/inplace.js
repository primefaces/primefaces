/**
 * PrimeFaces Inplace Widget
 */
PrimeFaces.widget.Inplace = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.display = $(this.jqId + '_display');
    this.content = $(this.jqId + '_content');
    this.cfg.formId = this.jq.parents('form:first').attr('id');

    var _self = this;
	
	if(!this.cfg.disabled) {
        
        if(this.cfg.toggleable) {
            this.display.bind(this.cfg.event, function(){
                _self.show();
            });

            this.display.mouseover(function(){
                $(this).toggleClass("ui-state-highlight");
            }).mouseout(function(){
                $(this).toggleClass("ui-state-highlight");
            });
        }
        else {
            this.display.css('cursor', 'default');
        }

        if(this.cfg.editor) {
            this.cfg.formId = $(this.jqId).parents('form:first').attr('id');

            this.editor = $(this.jqId + '_editor');
            this.editor.children('.ui-inplace-save').button({icons: {primary: "ui-icon-check"},text:false}).click(function(e) {_self.save(e);});
            this.editor.children('.ui-inplace-cancel').button({icons: {primary: "ui-icon-close"},text:false}).click(function(e) {_self.cancel(e);});
        }
	}
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Inplace, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Inplace.prototype.show = function() {    
    this.toggle(this.content, this.display, function() {
        this.content.find(':input:text:visible:enabled:first').focus().select();
    });
}

PrimeFaces.widget.Inplace.prototype.hide = function() {
    this.toggle(this.display, this.content);
}

PrimeFaces.widget.Inplace.prototype.toggle = function(elToShow, elToHide, callback) {
    var _self = this;

    if(this.cfg.effect == 'fade') {
        elToHide.fadeOut(this.cfg.effectSpeed,
            function(){
                elToShow.fadeIn(_self.cfg.effectSpeed);
                
                if(callback)
                    callback.call(_self);
            });
    }
    else if(this.cfg.effect == 'slide') {
            elToHide.slideUp(this.cfg.effectSpeed,
                function(){
                    elToShow.slideDown(_self.cfg.effectSpeed);
            });
    }
    else if(this.cfg.effect == 'none') {
            elToHide.hide();
            elToShow.show();
    }
}

PrimeFaces.widget.Inplace.prototype.getDisplay = function() {
    return this.display;
}

PrimeFaces.widget.Inplace.prototype.getContent = function() {
    return this.content;
}

PrimeFaces.widget.Inplace.prototype.save = function(e) {
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.hasBehavior('save')) {
        var saveBehavior = this.cfg.behaviors['save'];
        
        saveBehavior.call(this, e, options);
    } else {
        PrimeFaces.ajax.AjaxRequest(options); 
    }
}

PrimeFaces.widget.Inplace.prototype.cancel = function(e) {
    var options = {
        source: this.id,
        update: this.id,
        process: this.id,
        formId: this.cfg.formId
    };
    
    var params = {};
    params[this.id + '_cancel'] = true;
    
    options.params = params;
    
    if(this.hasBehavior('cancel')) {
        var saveBehavior = this.cfg.behaviors['cancel'];
        
        saveBehavior.call(this, e, options);
    } else {
        PrimeFaces.ajax.AjaxRequest(options); 
    }
}

PrimeFaces.widget.Inplace.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}