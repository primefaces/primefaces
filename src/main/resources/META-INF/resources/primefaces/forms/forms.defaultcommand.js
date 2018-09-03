/**
 * PrimeFaces DefaultCommand Widget
 */
PrimeFaces.widget.DefaultCommand = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.scope = this.cfg.scope ? $(PrimeFaces.escapeClientId(this.cfg.scope)) : null;
        var $this = this;

        // container support - e.g. splitButton
        if (this.jqTarget.is(':not(:button):not(:input):not(a)')) {
        	this.jqTarget = this.jqTarget.find('button,a').filter(':visible').first();
        }

        //attach keypress listener to parent form
        this.jqTarget.closest('form').off('keypress.' + this.id).on('keypress.' + this.id, function(e) {
           var keyCode = $.ui.keyCode;
           if(e.which == keyCode.ENTER) {
                //do not proceed if event target is not in this scope or target is a textarea,button or link
                if (($this.scope && $this.scope[0] != e.target && $this.scope.find(e.target).length == 0)
                   || $(e.target).is('textarea,button,input[type="submit"],a')) {
                    return true;
                }

               $this.jqTarget.click();
               e.preventDefault();
           }
        });

        this.removeScriptElement(this.id);
    }
});
