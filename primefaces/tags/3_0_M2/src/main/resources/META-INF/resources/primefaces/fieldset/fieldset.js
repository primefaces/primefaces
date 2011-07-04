/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = $(this.jqId);
    this.legend = this.jq.children('.ui-fieldset-legend');

    var _self = this;
    
    if(this.cfg.toggleable) {
        
        this.content = this.jq.children('.ui-fieldset-content');
        this.toggler = this.legend.children('.ui-fieldset-toggler');
        this.stateHolder = $(this.jqId + '_collapsed');

        //Add clickable legend state behavior
        var _legend = this.legend;
        this.legend.click(function(e) {_self.toggle(e);})
                           .mouseover(function() {_legend.toggleClass('ui-state-hover');})
                           .mouseout(function() {_legend.toggleClass('ui-state-hover');})
                           .mousedown(function() {_legend.toggleClass('ui-state-active');})
                           .mouseup(function() {_legend.toggleClass('ui-state-active');})
    }
}

/**
 * Toggles the content
 */
PrimeFaces.widget.Fieldset.prototype.toggle = function(e) {
    this.updateToggleState(this.cfg.collapsed);
    
    var _self = this;

    this.content.slideToggle(this.cfg.toggleSpeed, function() {
        if(_self.cfg.behaviors) {
            var toggleBehavior = _self.cfg.behaviors['toggle'];

            if(toggleBehavior) {
                toggleBehavior.call(_self);
            }
        }
    });
}

/**
 * Updates the visual toggler state and saves state
 */
PrimeFaces.widget.Fieldset.prototype.updateToggleState = function(collapsed) {
    if(collapsed)
        this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
    else
        this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');

    this.cfg.collapsed = !collapsed;
    
    this.stateHolder.val(!collapsed);
}