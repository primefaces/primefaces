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
    
    //configuration
    this.cfg.width = this.cfg.width || 'auto';
    this.cfg.height = this.cfg.height || 'auto';
    this.cfg.draggable = this.cfg.draggable == false ? false : true;
    this.cfg.resizable = this.cfg.resizable == false ? false : true;
    this.cfg.minWidth = this.cfg.minWidth || 150;
    this.cfg.minHeight = this.cfg.minHeight || 150;
    this.cfg.zindex = this.cfg.zindex || 1000;
    this.cfg.closeOnEscape = this.cfg.closeOnEscape == false ? false : true;
    this.cfg.position = this.cfg.position || 'center';
    
    this.jq.css({
        width : this.cfg.width,
        height: this.cfg.height
    });
        
    //options
    this.jq.css(this.cfg.position = calculatePosition(this.jq, this.cfg.position));
 
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
        
    if(this.cfg.closeOnEscape) {
        $(document).bind('keydown',function(e, ui){
            if(e.which == $.ui.keyCode.ESCAPE && _self.jq.hasClass('ui-dialog-selected'))
                _self.hide(e,ui);
        });
    }
}

PrimeFaces.widget.Dialog.prototype.show = function(event, ui) {
        
    if(this.jq.is(':visible'))
        return;
        
    this.load();
    
    var _self = this;
    if(this.cfg.showEffect)
        this.jq.show(this.cfg.showEffect, function(e, ui){_self.onShow(e,ui);} );
    else{
        this.jq.show();
        this.onShow(event, ui);
    }
    
    this.overlay();
    this.focusFirstInput();
}

PrimeFaces.widget.Dialog.prototype.hide = function(event, ui) {   
    if(this.jq.is(':hidden'))
        return;
        
    this.save();
    
    var _self = this;
    if(this.cfg.hideEffect)
        this.jq.hide(this.cfg.hideEffect, function(e, ui){_self.onHide(e,ui);});
    else{
        this.jq.hide();
        this.onHide(event, ui);
    }
        
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
        
    this.closeIcon.click(function(e, ui) {
        _self.hide(e, ui);
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
        alsoResize : this.footer.length > 0 ? this.content : false,
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


PrimeFaces.widget.Dialog.prototype.onShow = function(event, ui) {
    if(this.cfg.onShow) {
        this.cfg.onShow.call(this, event, ui);
    }
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

