/*
 * PrimeFaces InputMask Widget
 */
PrimeFaces.widget.InputMask = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    if(this.cfg.mask) {
        this.jq.mask(this.cfg.mask, this.cfg);
    }

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    PrimeFaces.skinInput(this.jq);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.InputMask, PrimeFaces.widget.BaseWidget);