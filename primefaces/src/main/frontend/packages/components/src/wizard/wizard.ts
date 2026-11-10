/**
 * The configuration for the {@link Wizard} widget.
 */
export interface WizardCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** ID of the step shown initially. */
    initialStep: string;
    /** Called before going to the previous step, `false` stays on the current step. */
    onback: PrimeFacesTypes.widget.Wizard.OnBackCallback;
    /** Called before going to the next step, `false` stays on the current step. */
    onnext: PrimeFacesTypes.widget.Wizard.OnNextCallback;
    /** Whether the step titles are displayed. */
    showStepStatus: boolean;
    /** Whether the back and next buttons are displayed. */
    showNavBar: boolean;
    /** Whether the steps before the current one are highlighted as completed. */
    highlightCompletedSteps: boolean;
    /** IDs of the steps. */
    steps: string[];
    /** Effect of {@link PrimeFaces.effects} used when the step changes. */
    effect: string;
    /** Duration of the effect in milliseconds. */
    effectDuration: number;
}

/**
 * __PrimeFaces Wizard Widget__
 *
 * Wizard provides an AJAX enhanced UI to implement a workflow easily in a single page. Wizard consists of several child
 * tab components where each tab represents a step in the process.
 */
export class Wizard<Cfg extends WizardCfg = WizardCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The content of the current step. */
    content: JQuery = $();
    /** The button to the previous step. */
    backNav: JQuery = $();
    /** The button to the next step. */
    nextNav: JQuery = $();
    /** ID of the current step. */
    currentStep?: string;
    /** The step titles, only with `showStepStatus`. */
    stepControls?: JQuery;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.content = $(this.jqId + '_content');
        this.backNav = $(this.jqId + '_back');
        this.nextNav = $(this.jqId + '_next');
        this.cfg.formId = this.jq.parents('form').first().attr('id');
        this.currentStep = this.cfg.initialStep;

        // step controls
        if (this.cfg.showStepStatus) {
            this.stepControls = $(this.jqId + ' .p-wizard-step-titles li.p-wizard-step-title');
            this.updateStepStatus();
        }

        // navigation controls
        if (this.cfg.showNavBar) {
            const currentStepIndex = this.getStepIndex(this.currentStep);

            PrimeFaces.skinButton(this.backNav);
            PrimeFaces.skinButton(this.nextNav);

            this.backNav.on('click.wizard', () => { this.back(); });
            this.nextNav.on('click.wizard', () => { this.next(); });
            if (this.cfg.disableOnAjax !== false) {
                this.bindTriggers();
            }

            if (currentStepIndex === 0) {
                this.backNav.hide();
            }
            else if (currentStepIndex === (this.cfg.steps ?? []).length - 1) {
                this.nextNav.hide();
            }
        }
    }

    /**
     * Updates the current and completed state classes of the step titles.
     */
    updateStepStatus(): void {
        const currentStepIndex = this.getStepIndex(this.currentStep);
        if (currentStepIndex < 0 || !this.stepControls) {
            return;
        }

        this.stepControls.removeClass('p-wizard-step-current p-wizard-step-completed');
        this.stepControls.not('[data-pf-user-highlight]').removeClass('p-state-highlight');
        this.stepControls.eq(currentStepIndex).addClass('p-state-highlight p-wizard-step-current');
        if (this.cfg.highlightCompletedSteps) {
            this.stepControls.slice(0, currentStepIndex).addClass('p-state-highlight p-wizard-step-completed');
        }
    }

    /**
     * Goes to the previous step.
     */
    back(): void {
        if (this.cfg.onback) {
            const value = this.cfg.onback.call(this);
            if (value === false) {
                return;
            }
        }

        const targetStepIndex = this.getStepIndex(this.currentStep) - 1;
        const stepToGo = (this.cfg.steps ?? [])[targetStepIndex];
        if (targetStepIndex >= 0 && stepToGo !== undefined) {
            const effect = this.cfg.effect;
            if (effect) {
                PrimeFaces.effects.hide(this.content, effect, { duration: this.cfg.effectDuration, complete: () => {
                    this.loadStep(stepToGo, 'back');
                    PrimeFaces.effects.show(this.content, effect, { duration: this.cfg.effectDuration });
                } });
            }
            else {
                this.loadStep(stepToGo, 'back');
            }
        }
    }

    /**
     * Goes to the next step.
     */
    next(): void {
        if (this.cfg.onnext) {
            const value = this.cfg.onnext.call(this);
            if (value === false) {
                return;
            }
        }

        const targetStepIndex = this.getStepIndex(this.currentStep) + 1;
        const stepToGo = (this.cfg.steps ?? [])[targetStepIndex];
        if (targetStepIndex < (this.cfg.steps ?? []).length && stepToGo !== undefined) {
            const effect = this.cfg.effect;
            if (effect) {
                PrimeFaces.effects.hide(this.content, effect, { duration: this.cfg.effectDuration, complete: () => {
                    this.loadStep(stepToGo, 'next');
                    PrimeFaces.effects.show(this.content, effect, { duration: this.cfg.effectDuration });
                } });
            }
            else {
                this.loadStep(stepToGo, 'next');
            }
        }
    }

    /**
     * Loads the given step via AJAX.
     * @param stepToGo ID of the step to load.
     * @param event `back` or `next`, also the behavior that is called.
     */
    loadStep(stepToGo: string, event: 'back' | 'next'): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            formId: this.cfg.formId,
            params: [
                { name: this.getId() + '_direction', value: event },
                { name: this.getId() + '_stepToGo', value: stepToGo }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.content.html(content);
                    }
                });

                return true;
            },
            oncomplete: (_xhr, _status, args) => {
                if (args && !args.validationFailed) {
                    const currentStep = args['currentStep'] as string;
                    if (this.getStepIndex(currentStep) >= 0) {
                        this.currentStep = currentStep;
                    }
                    const currentStepIndex = this.getStepIndex(this.currentStep);

                    if (this.cfg.showNavBar) {
                        if (currentStepIndex === (this.cfg.steps ?? []).length - 1) {
                            this.hideNextNav();
                            this.showBackNav();
                        }
                        else if (currentStepIndex === 0) {
                            this.hideBackNav();
                            this.showNextNav();
                        }
                        else {
                            this.showBackNav();
                            this.showNextNav();
                        }
                    }

                    if (this.cfg.showStepStatus) {
                        this.updateStepStatus();
                    }
                }
            }
        };

        if (this.hasBehavior(event)) {
            this.callBehavior(event, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Finds the index of the given step.
     * @param step ID of the step.
     * @returns The 0-based index of the step, `-1` if there is none.
     */
    getStepIndex(step: string | undefined): number {
        const steps = this.cfg.steps ?? [];
        for (let i = 0; i < steps.length; i++) {
            if (steps[i] === step) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Goes back to the first step, if not already there.
     */
    reset(): void {
        const firstStep = (this.cfg.steps ?? [])[0];
        if (firstStep !== undefined && this.currentStep !== firstStep) {
            this.loadStep(firstStep, 'back');
        }
    }

    /**
     * Shows the button to the next step.
     */
    showNextNav(): void {
        this.nextNav.fadeIn();
    }

    /**
     * Hides the button to the next step.
     */
    hideNextNav(): void {
        this.nextNav.fadeOut();
    }

    /**
     * Enables the button to the next step.
     */
    enableNextNav(): void {
        PrimeFaces.utils.enableButton(this.nextNav);
    }

    /**
     * Disables the button to the next step.
     */
    disableNextNav(): void {
        PrimeFaces.utils.disableButton(this.nextNav);
    }

    /**
     * Shows the button to the previous step.
     */
    showBackNav(): void {
        this.backNav.fadeIn();
    }

    /**
     * Hides the button to the previous step.
     */
    hideBackNav(): void {
        this.backNav.fadeOut();
    }

    /**
     * Enables the button to the previous step.
     */
    enableBackNav(): void {
        PrimeFaces.utils.enableButton(this.backNav);
    }

    /**
     * Disables the button to the previous step.
     */
    disableBackNav(): void {
        PrimeFaces.utils.disableButton(this.backNav);
    }

    /**
     * Disables the navigation buttons while a request of this wizard runs.
     */
    private bindTriggers(): void {
        $(document).on('pfAjaxSend.' + this.id, (_e, _xhr, settings) => {
            if (PrimeFaces.ajax.Utils.isXhrSource(this, settings)) {
                this.disableBackNav();
                this.disableNextNav();
            }
        }).on('pfAjaxComplete.' + this.id, (_e, _xhr, settings) => {
            if (PrimeFaces.ajax.Utils.isXhrSource(this, settings)) {
                this.enableBackNav();
                this.enableNextNav();
            }
        });
        this.addRefreshListener(() => {
            $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);
        });
        this.addDestroyListener(() => {
            $(document).off('.' + this.id);
        });
    }
}
