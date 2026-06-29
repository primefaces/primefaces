/**
 * __PrimeFaces DefaultCommand Widget__
 * 
 * Which command to submit the form with when enter key is pressed a common problem in web apps not just specific to
 * JSF. Browsers tend to behave differently as there doesn’t seem to be a standard and even if a standard exists,
 * IE probably will not care about it. There are some ugly workarounds like placing a hidden button and writing
 * JavaScript for every form in your app. `DefaultCommand` solves this problem by normalizing the command (e.g. button
 * or link) to submit the form with on enter key press.
 * 
 * @prop {JQuery} jqTarget The DOM element for the target button or link.
 * @prop {JQuery | null} scope The ancestor component to enable multiple default commands in a form. 
 * 
 * @interface {PrimeFaces.widget.DefaultCommandCfg} cfg The configuration for the {@link  DefaultCommand| DefaultCommand widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.scope Identifier of the ancestor component to enable multiple default commands in a form.
 * @prop {string} cfg.target Identifier of the default command component.
 */
PrimeFaces.widget.DefaultCommand = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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
                    $this.jqTarget.trigger(PrimeFaces.csp.clickEvent());
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
