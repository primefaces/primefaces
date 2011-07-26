/**
 * PrimeFaces Dialog Widget
 */
PrimeFaces.widget.Dialog = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.content = this.jq.children('.ui-dialog-content');
    this.titlebar = this.jq.children('.ui-dialog-titlebar');
    this.footer = this.jq.find('.ui-dialog-footer');
    this.icons = this.titlebar.children('.ui-dialog-titlebar-icon');
    this.closeIcon = this.titlebar.children('.ui-dialog-titlebar-close');
    this.minimizeIcon = this.titlebar.children('.ui-dialog-titlebar-minimize');
    this.maximizeIcon = this.titlebar.children('.ui-dialog-titlebar-maximize');
    this.visible = false;
    this.blockEvents = 'focus mousedown mouseup keydown keypress click';
    
    //configuration
    this.cfg.width = this.cfg.width || 'auto';
    this.cfg.height = this.cfg.height || 'auto';
    this.cfg.draggable = this.cfg.draggable == false ? false : true;
    this.cfg.resizable = this.cfg.resizable == false ? false : true;
    this.cfg.minWidth = this.cfg.minWidth || 150;
    this.cfg.minHeight = this.cfg.minHeight || this.titlebar.outerHeight();
    this.cfg.zindex = this.cfg.zindex || 1000;
    this.cfg.closeOnEscape = this.cfg.closeOnEscape == false ? false : true;
    this.cfg.position = this.cfg.position || 'center';
    
    //size and zindex
    this.jq.css({
        'width': this.cfg.width,
        'height': this.cfg.height,
        'z-index': this.cfg.zindex 
    });
    
    //position
    this.initPosition();
 
    //events
    this.bindEvents();
    
    if(this.cfg.draggable) {
        this.setupDraggable();
    }
    
    if(this.cfg.resizable){
        this.setupResizable();
    }
    
    if(this.cfg.appendToBody){
        this.jq.appendTo('body');
    }
        
    if(this.cfg.autoOpen){
        this.jq.show();
    }
}

PrimeFaces.widget.Dialog.prototype.enableModality = function(){
    var _self = this;

    $(document.body).append('<div class="ui-widget-overlay"></div>').
        children('.ui-widget-overlay').css({
            'width': $(document).width()
            ,'height': $(document).height()
            ,'z-index': this.jq.css('z-index') - 1
        });

    //disable tabbing out of modal dialog
    $(document).bind('keydown', function(event) {
        if(event.keyCode == $.ui.keyCode.TAB) {
            var tabbables = _self.content.find(':tabbable'),
            first = tabbables.filter(':first'),
            last  = tabbables.filter(':last');

            if(event.target === last[0] && !event.shiftKey) {
                first.focus(1);
                return false;
            } else if (event.target === first[0] && event.shiftKey) {
                last.focus(1);
                return false;
            }
        }        
    });

    //stop events from targets outside of dialog
    $(document).bind(this.blockEvents, function(event) {
        if ($(event.target).zIndex() < _self.jq.zIndex()) {
            return false;
        }
    });
}


PrimeFaces.widget.Dialog.prototype.disableModality = function(){
    $(document.body).children('.ui-widget-overlay').remove();
    $(document).unbind(this.blockEvents);
}

PrimeFaces.widget.Dialog.prototype.show = function() {
    if(this.visible) {
       return;
    }
    
    if(this.cfg.showEffect) {
        var _self = this;
            
        this.jq.show(this.cfg.showEffect, function() {
            if(_self.onShow)
                _self.onShow.call(_self);
        });
    }    
    else {
        this.jq.show();
        
        if(this.onShow)    
            this.onShow.call(this);
    }
    
    this.focusFirstInput();
    this.visible = true;
    
    if(this.cfg.modal)
        this.enableModality();
}

PrimeFaces.widget.Dialog.prototype.hide = function() {   
    if(!this.visible) {
       return;
    }
            
    if(this.cfg.hideEffect) {
        var _self = this;
    
        this.jq.hide(this.cfg.hideEffect, function() {
            if(_self.onHide)
                _self.onHide.call(_self);
        });
    }
    else {
        this.jq.hide();
        
        if(this.onHide)
            this.onHide.call(this);
    }
    
    this.visible = false;
    
    if(this.cfg.modal)
        this.disableModality();
}

PrimeFaces.widget.Dialog.prototype.focusFirstInput = function() {
    this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
}

PrimeFaces.widget.Dialog.prototype.bindEvents = function() {   
    var _self = this;
    
    this.jq.mousedown(function(){
        //_self.overlay();
    });

    this.icons.mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    })
        
    this.closeIcon.click(function(e, ui) {
        _self.hide(e, ui);
        e.preventDefault();
    });
}

PrimeFaces.widget.Dialog.prototype.setupDraggable = function() {    
    this.jq.draggable({
        handle: '.ui-dialog-titlebar',
        containment : 'body'
    });
}
PrimeFaces.widget.Dialog.prototype.setupResizable = function() {    
    this.jq.resizable({
        handles : 'n,s,e,w,ne,nw,se,sw',
        minWidth : this.cfg.minWidth,
        minHeight : this.cfg.minHeight,
        alsoResize : this.footer.length > 0 ? this.content : false,
        containment: 'body'
    });
}

PrimeFaces.widget.Dialog.prototype.onShow = function(event, ui) {
    if(this.cfg.onShow) {
        this.cfg.onShow.call(this, event, ui);
    }
} 

PrimeFaces.widget.Dialog.prototype.initPosition = function() {
    
    if(/(center|left|top|right|bottom)/.test(this.cfg.position)) {
        this.cfg.position = this.cfg.position.replace(',', ' ');
        
        this.jq.position({
            my: 'center'
            ,at: this.cfg.position
            ,collision: 'fit'
            ,of: window
            ,using: function(pos) {
                var topOffset = $(this).css(pos).offset().top;
                if(topOffset < 0) {
                    $(this).css('top', pos.top - topOffset);
                }
            }
        });
    }
    else {
        var coords = this.cfg.position.split(','),
        x = $.trim(coords[0]),
        y = $.trim(coords[1]);
        
        this.jq.offset({
            top: y
            ,left: x
        });
    }
    
    //Hide allocated space after dimensions are properly calculated
    this.jq.css('display', 'none').css('visibility', 'visible');
}

PrimeFaces.widget.Dialog.prototype.onHide = function(event, ui) {

    if(this.cfg.onHide) {
        this.cfg.onHide.call(this, event, ui);
    }

    if(this.cfg.behaviors) {
        var closeBehavior = this.cfg.behaviors['close'];

        if(closeBehavior) {
            closeBehavior.call(this);
        }
    }
}
