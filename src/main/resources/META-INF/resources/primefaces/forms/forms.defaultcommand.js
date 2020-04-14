/**
 * PrimeFaces DefaultCommand Widget
 */
PrimeFaces.widget.DefaultCommand = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.scope = this.cfg.scope ? $(PrimeFaces.escapeClientId(this.cfg.scope)) : null;
        var $this = this;

        // container support - e.g. splitButton
        if (this.jqTarget.is(':not(:button):not(:input):not(a)')) {
            this.jqTarget = this.jqTarget.find('button,a').filter(':visible').first();
        }

        //attach keypress listener to parent form
        var closestForm = this.jqTarget.closest('form');
        closestForm.off('keydown.' + this.id).on('keydown.' + this.id, {scopeEnter: false}, function (e, data) {
            var keyCode = $.ui.keyCode;

            data = data || e.data;
            if (($this.scope && data.scopeEnter && data.scopeDefaultCommandId === $this.id)
                    || (!$this.scope && !data.scopeEnter && (e.which == keyCode.ENTER))) {
                //do not proceed if target is a textarea,button or link
                if ($(e.target).is('textarea,button,input[type="submit"],a')) {
                    return true;
                }

                if (!$this.jqTarget.is(':disabled, .ui-state-disabled')) {
                    $this.jqTarget.click();
                }
                e.preventDefault();
                e.stopImmediatePropagation();
            }
        });

        if (this.scope) {
            this.scope.off('keydown.' + this.id).on('keydown.' + this.id, function (e) {
                var keyCode = $.ui.keyCode;
                if (e.which == keyCode.ENTER) {
                    closestForm.trigger(e, {scopeEnter: true, scopeDefaultCommandId: $this.id});
                    //e.preventDefault();
                    e.stopPropagation();
                }
            });
        }
    }
});
