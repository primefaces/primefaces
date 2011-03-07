/**
 * PrimeFaces Inplace Widget
 */
PrimeFaces.widget.Inplace = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.display = $(this.jqId + '_display');
    this.content = $(this.jqId + '_content');

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
            this.editor.children('.ui-inplace-save').button({icons: {primary: "ui-icon-check"},text:false}).click(function() {_self.save();});
            this.editor.children('.ui-inplace-cancel').button({icons: {primary: "ui-icon-close"},text:false}).click(function() {_self.cancel();});
        }
	}

    
}

PrimeFaces.widget.Inplace.prototype.show = function() {
    this.toggle(this.content, this.display);
}

PrimeFaces.widget.Inplace.prototype.hide = function() {
    this.toggle(this.display, this.content);
}

PrimeFaces.widget.Inplace.prototype.toggle = function(elToShow, elToHide) {
    var _self = this;

    if(this.cfg.effect == 'fade') {
        elToHide.fadeOut(this.cfg.effectSpeed,
            function(){
                elToShow.fadeIn(_self.cfg.effectSpeed);
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

PrimeFaces.widget.Inplace.prototype.save = function() {
    this.doAjaxInplaceRequest(this.id, this.cfg.onEditUpdate);
}

PrimeFaces.widget.Inplace.prototype.cancel = function() {
    this.doAjaxInplaceRequest();
}

PrimeFaces.widget.Inplace.prototype.doAjaxInplaceRequest = function(process, update) {
    var options = {
        source: this.id,
        update: this.id,
        formId: this.cfg.formId
    };

    var params = {};

    if(process) {
        options.process = process;
        params[this.id + '_save'] = true;
    }

    if(update) {
        options.update = options.update + ' ' + update;
    }

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}