/**
 * PrimeFaces Fieldset Widget
 */
PrimeFaces.widget.Fieldset = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.legend = this.jqId + ' .ui-fieldset-legend';

    var _self = this;
    if(this.cfg.toggleable) {
        this.content = this.jqId + ' .ui-fieldset-content';
        this.toggler = this.jqId + ' .ui-fieldset-toggler';
        this.stateHolder = this.jqId + '_collapsed';

        //Add clickable legend state behavior
        jQuery(this.legend).click(function() {_self.toggle();})
                           .mouseover(function() {jQuery(this).toggleClass('ui-state-hover');})
                           .mouseout(function() {jQuery(this).toggleClass('ui-state-hover');})
                           .mousedown(function() {jQuery(this).toggleClass('ui-state-active');})
                           .mouseup(function() {jQuery(this).toggleClass('ui-state-active');})
    }
}

/**
 * Toggles the content
 */
PrimeFaces.widget.Fieldset.prototype.toggle = function() {

    this.updateToggleState(this.cfg.collapsed);

    var _self = this;
    jQuery(this.content).slideToggle(this.cfg.toggleSpeed,
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

    PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
}

/**
 * Updates the visual toggler state and saves state
 */
PrimeFaces.widget.Fieldset.prototype.updateToggleState = function(collapsed) {
    if(collapsed)
        jQuery(this.toggler).removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
    else
        jQuery(this.toggler).removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');

    this.cfg.collapsed = !collapsed;
    jQuery(this.stateHolder).val(!collapsed);
}