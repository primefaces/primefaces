/**
 * PrimeFaces Dialog Widget
 */
PrimeFaces.widget.Dialog = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.closeIcon = $(this.jqId + ' .ui-dialog-titlebar .ui-dialog-titlebar-close');
    this.minimizeIcon = $(this.jqId + ' .ui-dialog-titlebar .ui-dialog-titlebar-minimize');
    this.maximizeIcon = $(this.jqId + ' .ui-dialog-titlebar .ui-dialog-titlebar-maximize');
    this.content = this.jq.find('.ui-dialog-content');
    this.footer = this.jq.find('.ui-dialog-footer');
    this.icons = this.jq.find('.ui-dialog-titlebar .ui-dialog-titlebar-close, .ui-dialog-titlebar .ui-dialog-titlebar-minimize, .ui-dialog-titlebar .ui-dialog-titlebar-maximize');
    
    
    this.jq.css({
        width : this.cfg.width || 300
    });
    
    if(this.cfg.appendToBody){
        this.jq.appendTo('body');
    }
    
    //options
    this.jq.css(this.cfg.position = calculatePosition(this.jq, this.cfg.position || 'center'));
        
    this.cfg.draggable = this.cfg.draggable || true;
    
    //events
    this.bindEvents();
    
    //draggable
    if(this.cfg.draggable) {
        this.setupDraggable();
    }
    
    if(this.cfg.resizable){
        this.setupResizable();
    }
        
    if(this.cfg.autoOpen){
        this.jq.show();
        this.overlay();
    }
}


//renews dialog layout
PrimeFaces.widget.Dialog.prototype.overlay = function(){
    var _self = this;
    var s = $('.ui-dialog-selected');
    if(!this.jq.hasClass('ui-dialog-selected') && s.length){
        this.jq.css('z-index', parseInt(s.css('z-index')) + 1);
        s.removeClass('ui-dialog-selected');
    }
    else{
        var z = this.jq.css('z-index');
        this.jq.css('z-index', (parseInt(z) || 1000));
    }
        
    this.jq.addClass('ui-dialog-selected');
        
    if(this.cfg.modal){
        if(!this.modalPanel){
            this.modalPanel = $('<div class="ui-dialog-modal ui-widget-overlay"></div>');
            $('body').append(this.modalPanel);
        }

        this.modalPanel.css('z-index', this.jq.css('z-index') - 1).show();
        $(document).bind('keydown', function(e){
            if (e.keyCode == $.ui.keyCode.TAB) {
                e.preventDefault();
                return false;
            }
        });
    }
        
    if(this.cfg.closeOnEscape){
        $(document).bind('keydown',function(e){
            if(e.which == $.ui.keyCode.ESCAPE && _self.jq.hasClass('ui-dialog-selected'))
                _self.hide();
        });
    }
}

PrimeFaces.widget.Dialog.prototype.show = function() {
        
    if(this.jq.is(':visible'))
        return;
        
    this.load();
    
    var os = this.cfg.onShow || false;
    
    if(this.cfg.showEffect)
        this.jq.show(this.cfg.showEffect, os );
    else
        this.jq.show('size', 1, os);
    
    this.overlay();
    this.focusFirstInput();
}

PrimeFaces.widget.Dialog.prototype.hide = function() {   
    if(this.jq.is(':hidden'))
        return;
        
    this.save();
    
    var oh = this.cfg.onHide || false;
        
    if(this.cfg.hideEffect)
        this.jq.hide(this.cfg.hideEffect, oh);
    else
        this.jq.hide('size', 1, oh);
        
    if(this.cfg.modal){
        this.modalPanel.hide();
        $(document).unbind('keydown');
    }
}

PrimeFaces.widget.Dialog.prototype.focusFirstInput = function() {
    this.jq.find(':not(:submit):not(:button):input:visible:enabled:first').focus();
}

PrimeFaces.widget.Dialog.prototype.bindEvents = function() {   
    var _self = this;
    
    this.jq.mousedown(function(){
        _self.overlay();
    });

    this.icons.mouseover(function() {
        $(this).addClass('ui-state-hover');
    }).mouseout(function() {
        $(this).removeClass('ui-state-hover');
    })
        
    this.closeIcon.click(function(e) {
        _self.hide();
        
        e.preventDefault();
    });
        
//      this.minimizeIcon.click(function(e) { e.preventDefault(); });
        
//      this.maximizeIcon.click(function(e) { e.preventDefault(); });
}
    
    
//saves positions&dimensions
PrimeFaces.widget.Dialog.prototype.save = function() {
    this.cfg.top = this.jq.offset().top - $(window).scrollTop();
    this.cfg.left = this.jq.offset().left - $(window).scrollLeft();
    this.cfg.width = this.jq.width();
    this.cfg.height = this.jq.height();
}
    
    
//loads position&dimensions
PrimeFaces.widget.Dialog.prototype.load = function() {
    if(this.cfg.top)
        this.jq.css({
            top :  $(window).scrollTop()  + this.cfg.top, 
            left :  $(window).scrollLeft()  + this.cfg.left
        });
    else
        this.jq.css({
            top : $(window).scrollTop() + ($(window).height() - this.jq.height())/2, 
            left : $(window).scrollLeft() + ($(window).width() - this.jq.width())/2
        });
        
    //next for width/height on minimize/maximize
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
        alsoResize : this.footer.length ? this.content : false,
        containment: 'body'
    });
}
    
function calculatePosition(o, p){
    if(!(o || q))
        return false;
    var w = $(window), 
    st = w.scrollTop(),
    sl = w.scrollLeft(),
    wh = w.height(),
    ww = w.width(),
    ow = o.width(),
    oh = o.height();
        
    var q = p.split(","),
    result = {
        top : st + ( parseInt(q[1] || '') || ((wh/1.3 - oh)/2)), 
        left : sl + ( parseInt(q[0] || '') || (ww - ow)/2)
        };
        
    if( p.indexOf('left') > -1)
        result.left = sl + 10;
    else if( p.indexOf('right') > -1)
        result.left = sl + (ww - ow - 10);

    if( p.indexOf('top') > -1)
        result.top = st + 10;
    else if( p.indexOf('bottom') > -1)
        result.top = st + (wh - oh - 10);
      
    return result;
}