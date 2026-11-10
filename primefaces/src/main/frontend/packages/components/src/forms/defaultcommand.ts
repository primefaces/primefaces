/**
 * The configuration for the {@link DefaultCommand} widget.
 */
export interface DefaultCommandCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Client ID of the ancestor component that limits the default command, to have several in one form. */
    scope: string;
    /** Client ID of the button or link to click on Enter. */
    target: string;
}

/**
 * __PrimeFaces DefaultCommand Widget__
 *
 * DefaultCommand defines the command (e.g. a button or link) that submits the form when the Enter key is pressed,
 * normalizing the different browser behaviors.
 */
export class DefaultCommand<Cfg extends DefaultCommandCfg = DefaultCommandCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The button or link to click. */
    jqTarget: JQuery = $();
    /** The ancestor component that limits the default command, only with `scope`. */
    scope: JQuery | null = null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.jqTarget = this.findTarget();
        this.scope = this.cfg.scope ? $(PrimeFaces.escapeClientId(this.cfg.scope)) : null;

        // attach keydown listener to parent form
        const target = this.jqTarget[0];
        const formId = target?.getAttribute('data-pf-form');
        const closestForm = target && formId !== null && formId !== undefined
            ? $(PrimeFaces.escapeClientId(formId))
            : this.jqTarget.closest('form');

        // destroyed with the form that has the listener, not with the target: an update of only the target (e.g. to
        // enable it) must keep the default command, the target is found again on each Enter
        this.bindLifecycleTo(closestForm);

        const scopeKeydown = 'keydown.' + this.getId();
        closestForm.off(scopeKeydown).on(scopeKeydown, { scopeEnter: false },
                (e: JQuery.TriggeredEvent, data?: { scopeEnter: boolean; scopeDefaultCommandId?: string }) => {
            if (e.key !== 'Enter') {
                return;
            }
            data = data || e.data;
            if ((this.scope && data?.scopeEnter && data.scopeDefaultCommandId === this.id) || (!this.scope && !data?.scopeEnter)
                    || (this.scope && this.scope[0] === closestForm[0])) {
                // #7028/#13927 Do not proceed if target is a textarea, button, link, or TextEditor
                if (PrimeFaces.utils.isEnterKeyBlocked(e)) {
                    return;
                }

                this.jqTarget = this.findTarget();
                if (!this.jqTarget.length) {
                    // the target is not rendered anymore, the browser handles the key
                    return;
                }

                if (!this.jqTarget.is(':disabled, .p-state-disabled')) {
                    // focus default command button and click it
                    this.jqTarget.trigger('focus').trigger(PrimeFaces.csp.clickEvent(this.jqTarget));
                }
                e.preventDefault();
                e.stopImmediatePropagation();
            }
        });

        // Add keydown listener to scope if not the closest form to prevent recursion
        if (this.scope && this.scope[0] !== closestForm[0]) {
            this.scope.off(scopeKeydown).on(scopeKeydown, (e) => {
                if (e.key === 'Enter') {
                    closestForm.trigger(e, { scopeEnter: true, scopeDefaultCommandId: this.id });
                    e.stopPropagation();
                }
            });
        }

        // the scope may stay when the form is re-rendered without the default command
        this.addDestroyListener(() => {
            closestForm.off(scopeKeydown);
            if (this.scope) {
                this.scope.off(scopeKeydown);
            }
        });
    }

    /**
     * Finds the target button or link; of a container such as a splitButton its first visible button or link.
     * @returns The target, empty if it is not rendered.
     */
    findTarget(): JQuery {
        let target = $(PrimeFaces.escapeClientId(this.cfg.target ?? ''));
        // Support container elements such as splitButton
        if (target.is(':not(:button):not(:input):not(a)')) {
            target = target.find('button,a').filter(':visible').first();
        }
        return target;
    }
}
