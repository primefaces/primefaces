/**
 * PrimeFaces Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('div.ui-panel-titlebar');
        this.title = this.header.children('span.ui-panel-title');
        this.content = $(this.jqId + '_content');
        
        this.onshowHandlers = [];
        
        if(this.cfg.toggleable) {
            this.bindToggler();
        }

        if(this.cfg.closable) {
            this.bindCloser();
        }
        
        //visuals for action items
        this.header.find('.ui-panel-titlebar-icon').on('hover.ui-panel', function() {$(this).toggleClass('ui-state-hover');});
    },
    
    toggle: function() {
        if(this.cfg.collapsed) {
            this.expand();
        }
        else {
            this.collapse();
        }
    },
    
    expand: function() {
        this.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');
        
        if(this.cfg.toggleOrientation === 'vertical')
            this.slideDown();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideRight();    
    },
    
    collapse: function() {
        this.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');
        
        if(this.cfg.toggleOrientation === 'vertical')
            this.slideUp();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideLeft();
    },
    
    slideUp: function() {        
        this.content.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc');
    },
    
    slideDown: function() {        
        this.content.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc');
    },
    
    slideLeft: function() {
        var _self = this;
        
        this.originalWidth = this.jq.width();
                
        this.title.hide();
        this.toggler.hide();
        this.content.hide();

        this.jq.animate({
            width: '42px'
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggler.show();
            _self.jq.addClass('ui-panel-collapsed-h');
        });
    },
    
    slideRight: function() {
        var _self = this,
        expandWidth = this.originalWidth||'100%';
        
        this.toggler.hide();
        
        this.jq.animate({
            width: expandWidth
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.jq.removeClass('ui-panel-collapsed-h');
            _self.title.show();
            _self.toggler.show();
        
            _self.content.css({
                'visibility': 'visible'
                ,'display': 'block'
                ,'height': 'auto'
            });
        });
    },
    
    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.children('span.ui-icon').removeClass(removeIcon).addClass(addIcon);
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(collapsed);
        
        this.fireToggleEvent();
    },
    
    fireToggleEvent: function() {
        if(this.cfg.behaviors) {
            var toggleBehavior = this.cfg.behaviors['toggle'];
            
            if(toggleBehavior) {
                toggleBehavior.call(this);
            }
        }
    },
    
    close: function() {
        this.visibleStateHolder.val(false);

        var _self = this;

        $(this.jqId).fadeOut(this.cfg.closeSpeed,
            function(e) {
                if(_self.cfg.behaviors) {
                    var closeBehavior = _self.cfg.behaviors['close'];
                    if(closeBehavior) {
                        closeBehavior.call(_self, e);
                    }
                }
            }
        );
    },
    
    show: function() {
        var _self = this;
        $(this.jqId).fadeIn(this.cfg.closeSpeed, function() {
            _self.invokeOnshowHandlers();
        });

        this.visibleStateHolder.val(true);
    },
    
    bindToggler: function() {
        var _self = this;
        
        this.toggler = $(this.jqId + '_toggler');
        this.toggleStateHolder = $(this.jqId + '_collapsed');

        this.toggler.click(function() {_self.toggle();});
    },
    
    bindCloser: function() {
        var _self = this;
        
        this.closer = $(this.jqId + '_closer');
        this.visibleStateHolder = $(this.jqId + "_visible");

        this.closer.click(function() {_self.close();});
    },
        
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    },
    
    invokeOnshowHandlers: function() {
        this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
            return !fn.call();
        });
    }

});