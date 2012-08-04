/**
 * PrimeFaces Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.onshowHandlers = [];
        
        if(this.cfg.toggleable) {
            this.toggler = $(this.jqId + '_toggler');
            this.toggleStateHolder = $(this.jqId + '_collapsed');
            this.content = $(this.jqId + '_content');

            this.setupToggleTrigger();
        }

        if(this.cfg.closable) {
            this.visibleStateHolder = $(this.jqId + "_visible");

            this.setupCloseTrigger();
        }

        if(this.cfg.hasMenu) {
            this.visibleStateHolder = $(this.jqId + "_visible");

            this.setupMenuTrigger();
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
        var _self = this;
        
        this.content.slideDown(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggleState(false, 'ui-icon-plusthick', 'ui-icon-minusthick');
                
            if(_self.onshowHandlers.length > 0) {
                _self.invokeOnshowHandlers();
            }
        });
        
    },
    
    collapse: function() {
        var _self = this;
        
        this.content.slideUp(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            _self.toggleState(true, 'ui-icon-minusthick', 'ui-icon-plusthick');
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
    
    setupToggleTrigger: function() {
        var _self = this,
        trigger = this.toggler.parent();

        this.setupTriggerVisuals(trigger);

        trigger.click(function() {_self.toggle();});
    },
    
    setupCloseTrigger: function() {
        var _self = this,
        trigger = $(this.jqId + '_closer').parent();

        this.setupTriggerVisuals(trigger);

        trigger.click(function() {_self.close();});
    },
    
    setupMenuTrigger: function() {
        var trigger = $(this.jqId + '_menu').parent();

        this.setupTriggerVisuals(trigger);
    },
    
    setupTriggerVisuals: function(trigger) {
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