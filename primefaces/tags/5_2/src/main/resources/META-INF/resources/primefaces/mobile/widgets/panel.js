/**
 * PrimeFaces Mobile Panel Widget
 */
PrimeFaces.widget.Panel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.header = this.jq.children('.ui-panel-m-titlebar');
        this.content = this.jq.children('.ui-panel-m-content');
        this.onshowHandlers = this.onshowHandlers||{};
        
        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        if(this.cfg.toggleable) {
            this.toggler = this.header.children('.ui-panel-m-titlebar-icon');
            this.toggleStateHolder = $(this.jqId + '_collapsed');
            
            this.toggler.on('click', function(e) {
                $this.toggle();
                
                e.preventDefault();
            });
        }
    },
    
    toggle: function() {
        if(this.content.is(':visible'))
            this.collapse();
        else
            this.expand();
    },
    
    collapse: function() {
        this.toggleState(true, 'ui-icon-minus', 'ui-icon-plus');
        this.content.hide();
    },
    
    expand: function() {
        this.toggleState(false, 'ui-icon-plus', 'ui-icon-minus');
        this.content.show();
        this.invokeOnshowHandlers();
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
    
    addOnshowHandler: function(id, fn) {
        this.onshowHandlers[id] = fn;
    },
    
    invokeOnshowHandlers: function() {
        for(var id in this.onshowHandlers) {
            if(this.onshowHandlers.hasOwnProperty(id)) {
                var fn = this.onshowHandlers[id];
                
                if(fn.call()) {
                    delete this.onshowHandlers[id];
                }
            }
        }
    }
});