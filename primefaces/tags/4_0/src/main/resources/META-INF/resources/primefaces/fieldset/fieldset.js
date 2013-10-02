/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.onshowHandlers = this.onshowHandlers||{};
        
        this.legend = this.jq.children('.ui-fieldset-legend');

        var $this = this;

        if(this.cfg.toggleable) {
            this.content = this.jq.children('.ui-fieldset-content');
            this.toggler = this.legend.children('.ui-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            //Add clickable legend state behavior
            this.legend.click(function(e) {$this.toggle(e);})
                            .mouseover(function() {$this.legend.toggleClass('ui-state-hover');})
                            .mouseout(function() {$this.legend.toggleClass('ui-state-hover');})
                            .mousedown(function() {$this.legend.toggleClass('ui-state-active');})
                            .mouseup(function() {$this.legend.toggleClass('ui-state-active');})
        }
    },
    
    /**
     * Toggles the content
     */
    toggle: function(e) {
        this.updateToggleState(this.cfg.collapsed);

        var $this = this;

        this.content.slideToggle(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            if($this.cfg.behaviors) {
                var toggleBehavior = $this.cfg.behaviors['toggle'];

                if(toggleBehavior) {
                    toggleBehavior.call($this);
                }
            }
        });
        
        $this.invokeOnshowHandlers();
    },
    
    /**
     * Updates the visual toggler state and saves state
     */
    updateToggleState: function(collapsed) {
        if(collapsed)
            this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        else
            this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');

        this.cfg.collapsed = !collapsed;

        this.stateHolder.val(!collapsed);
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