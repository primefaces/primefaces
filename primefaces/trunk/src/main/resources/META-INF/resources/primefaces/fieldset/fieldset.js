/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.onshowHandlers = [];
        
        this.legend = this.jq.children('.ui-fieldset-legend');

        var _self = this;

        if(this.cfg.toggleable) {

            this.content = this.jq.children('.ui-fieldset-content');
            this.toggler = this.legend.children('.ui-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            //Add clickable legend state behavior
            this.legend.click(function(e) {_self.toggle(e);})
                            .mouseover(function() {_self.legend.toggleClass('ui-state-hover');})
                            .mouseout(function() {_self.legend.toggleClass('ui-state-hover');})
                            .mousedown(function() {_self.legend.toggleClass('ui-state-active');})
                            .mouseup(function() {_self.legend.toggleClass('ui-state-active');})
        }
        
        this.jq.data('widget', this);
    },
    
    /**
     * Toggles the content
     */
    toggle: function(e) {
        this.updateToggleState(this.cfg.collapsed);

        var _self = this;

        this.content.slideToggle(this.cfg.toggleSpeed, 'easeInOutCirc', function() {
            if(_self.cfg.behaviors) {
                var toggleBehavior = _self.cfg.behaviors['toggle'];

                if(toggleBehavior) {
                    toggleBehavior.call(_self);
                }
            }
            
            if(_self.onshowHandlers.length > 0) {
                _self.invokeOnshowHandlers();
            }
        });
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
    
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    },
    
    invokeOnshowHandlers: function() {
        this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
            return !fn.call();
        });
    }
    
});