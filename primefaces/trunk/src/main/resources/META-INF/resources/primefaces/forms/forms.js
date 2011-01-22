/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = function(cfg) {
    this.id = cfg.id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    PrimeFaces.skinInput(this.jq);
}