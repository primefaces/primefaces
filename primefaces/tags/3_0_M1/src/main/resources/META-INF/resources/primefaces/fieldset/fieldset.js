/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = jQuery(this.jqId);
    this.legend = this.jq.children('.ui-fieldset-legend');

    var _self = this;
    if(this.cfg.toggleable) {
        
        this.content = this.jq.children('.ui-fieldset-content');
        this.toggler = this.legend.children('.ui-fieldset-toggler');
        this.stateHolder = jQuery(this.jqId + '_collapsed');

        //Add clickable legend state behavior
        var _legend = this.legend;
        this.legend.click(function() {_self.toggle();})
                           .mouseover(function() {_legend.toggleClass('ui-state-hover');})
                           .mouseout(function() {_legend.toggleClass('ui-state-hover');})
                           .mousedown(function() {_legend.toggleClass('ui-state-active');})
                           .mouseup(function() {_legend.toggleClass('ui-state-active');})
    }
}

/**
 * Toggles the content
 */
PrimeFaces.widget.Fieldset.prototype.toggle = function() {

    this.updateToggleState(this.cfg.collapsed);

    var _self = this;
    this.content.slideToggle(this.cfg.toggleSpeed,
        function() {
            if(_self.cfg.ajaxToggle) {
                _self.fireToggleEvent();
            }
        });
}

/**
 * Fires an ajax toggle event and notify any server side toggleListeners
 */
PrimeFaces.widget.Fieldset.prototype.fireToggleEvent = function() {
    var options = {
        source: this.id,
        process: this.id
    };

    if(this.cfg.onToggleUpdate) {
       options.update = this.cfg.onToggleUpdate;
    }

    var params = {};
    params[this.id + "_ajaxToggle"] = true;
    params[this.id + "_collapsed"] = this.cfg.collapsed;

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
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