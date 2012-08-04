/**
 * PrimeFaces Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('div.ui-panel-titlebar');
        this.title = this.header.children('span.ui-panel-title');
        
        this.onshowHandlers = [];
        
        if(this.cfg.toggleable) {
            this.toggler = $(this.jqId + '_toggler');
            this.toggleStateHolder = $(this.jqId + '_collapsed');
            this.content = $(this.jqId + '_content');

            this.bindToggler();
        }

        if(this.cfg.closable) {
            this.visibleStateHolder = $(this.jqId + "_visible");

            this.bindCloser();
        }

        if(this.cfg.hasMenu) {
            this.visibleStateHolder = $(this.jqId + "_visible");

            this.bindMenu();
        }
        
        this.jq.data('widget', this);
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
        if(this.cfg.toggleOrientation === 'vertical')
            this.slideDown();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideRight();    
    },
    
    collapse: function() {
        if(this.cfg.toggleOrientation === 'vertical')
            this.slideUp();
        else if(this.cfg.toggleOrientation === 'horizontal')
            this.slideLeft();
    },
    
    slideUp: function() {
        var _self = this;
        
        this.content.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');
        });
    },
    
    slideDown: function() {
        var _self = this;
        
        this.content.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');
        });
    },
    
    slideLeft: function() {
        var _self = this;
        
        this.originalWidth = this.jq.width();
                
        this.title.hide();
        
        this.content.css({
            'visibility':'hidden'
            ,'height': this.content.height()
        });
        
        this.jq.animate({
            width: '42px'
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');
        });
    },
    
    slideRight: function() {
        var _self = this;
        
        this.jq.animate({
            width: this.originalWidth
        }, this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');
            
            _self.title.show();
        
            _self.content.css({
                'visibility':'visible'
                ,'height': 'auto'
            });
        });
    },
    
    toggleState: function(collapsed, removeIcon, addIcon) {
        this.toggler.removeClass(removeIcon).addClass(addIcon);
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
        var _self = this,
        trigger = this.toggler.parent();

        this.bindTriggerVisuals(trigger);

        trigger.click(function() {_self.toggle();});
    },
    
    bindCloser: function() {
        var _self = this,
        trigger = $(this.jqId + '_closer').parent();

        this.bindTriggerVisuals(trigger);

        trigger.click(function() {_self.close();});
    },
    
    bindMenu: function() {
        var trigger = $(this.jqId + '_menu').parent();

        this.bindTriggerVisuals(trigger);
    },
    
    bindTriggerVisuals: function(trigger) {
        trigger.mouseover(function() {$(this).addClass('ui-state-hover');})
                .mouseout(function() {$(this).removeClass('ui-state-hover');});
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