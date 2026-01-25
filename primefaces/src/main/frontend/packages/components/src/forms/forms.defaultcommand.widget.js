/**
 * __PrimeFaces DefaultCommand Widget__
 * 
 * Which command to submit the form with when enter key is pressed a common problem in web apps not just specific to
 * Faces. Browsers tend to behave differently as there doesn’t seem to be a standard and even if a standard exists,
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
PrimeFaces.widget.DefaultCommand = class DefaultCommand extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.scope = this.cfg.scope ? $(PrimeFaces.escapeClientId(this.cfg.scope)) : null;
        var $this = this;

        // Support container elements such as splitButton
        if (this.jqTarget.is(':not(:button):not(:input):not(a)')) {
            this.jqTarget = this.jqTarget.find('button,a').filter(':visible').first();
        }

        // attach keypress listener to parent form
        var closestForm = this.jqTarget[0] && this.jqTarget[0].hasAttribute('data-pf-form')
            ? $(PrimeFaces.escapeClientId(this.jqTarget[0].getAttribute('data-pf-form')))
            : this.jqTarget.closest('form');
        var scopeKeydown = 'keydown.' + this.id;
        closestForm.off(scopeKeydown).on(scopeKeydown, { scopeEnter: false }, function(e, data) {
            if (e.key !== 'Enter') {
                return;
            }
            data = data || e.data;
            if (($this.scope && data.scopeEnter && data.scopeDefaultCommandId === $this.id) || (!$this.scope && !data.scopeEnter) || ($this.scope && $this.scope[0] === closestForm[0])) {
                // #7028/#13927 Do not proceed if target is a textarea, button, link, or TextEditor
                if (PrimeFaces.utils.isEnterKeyBlocked(e)) {
                    return true;
                }
                if (!$this.jqTarget.is(':disabled, .ui-state-disabled')) {
                    // focus default command button and click it
                    $this.jqTarget.trigger('focus').trigger(PrimeFaces.csp.clickEvent($this.jqTarget));
                }
                e.preventDefault();
                e.stopImmediatePropagation();
            }
        });

        // Add keydown listener to scope if not the closest form to prevent recursion
        if (this.scope && this.scope[0] !== closestForm[0]) {
            this.scope.off(scopeKeydown).on(scopeKeydown, function(e) {
                if (e.key === 'Enter') {
                    closestForm.trigger(e, {scopeEnter: true, scopeDefaultCommandId: $this.id});
                    e.stopPropagation();
                }
            });
        }
    }
}
