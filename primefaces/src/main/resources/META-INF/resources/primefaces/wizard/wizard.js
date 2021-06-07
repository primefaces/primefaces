/**
 * __PrimeFaces Wizard Widget__
 * 
 * Wizard provides an AJAX enhanced UI to implement a workflow easily in a single page. Wizard consists of several child
 * tab components where each tab represents a step in the process.
 * 
 * @typedef PrimeFaces.widget.Wizard.OnBackCallback Callback that is invoked before switching to the previous wizard
 * step, see also {@link WizardCfg.onback}
 * @this {PrimeFaces.widget.Wizard} PrimeFaces.widget.Wizard.OnBackCallback
 * @return {boolean} PrimeFaces.widget.Wizard.OnBackCallback `true` to switch to the next wizard step, `false` to stay
 * at the current step.
 * 
 * @typedef PrimeFaces.widget.Wizard.OnNextCallback Callback that is invoked before switching to the  next wizard step.
 * If this return `false`, stays on the current tab. See also {@link WizardCfg.onnext}.
 * @this {PrimeFaces.widget.Wizard} PrimeFaces.widget.Wizard.OnNextCallback
 * 
 * @prop {JQuery} backNav The DOM element for the button that switches back to the previous wizard step.
 * @prop {JQuery} content The DOM element for the content of the wizard step.
 * @prop {string} currentStep ID of the currently active wizard step tab.
 * @prop {JQuery} nextNav The DOM element for the button that switches back to the next wizard step.
 * @prop {JQuery} stepControls The DOM element for the container with the wizard step controls.
 * 
 * @interface {PrimeFaces.widget.WizardCfg} cfg The configuration for the {@link  Wizard| Wizard widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.initialStep ID of the wizard step tab that is shown initially.
 * @prop {PrimeFaces.widget.Wizard.OnBackCallback} cfg.onback Callback that is invoked before switching to the previous
 * wizard step. If this returns `false`, stays on the current tab.
 * @prop {PrimeFaces.widget.Wizard.OnNextCallback} cfg.onnext Callback that is invoked before switching to the next
 * wizard step. If this return `false`, stays on the current tab.
 * @prop {boolean} cfg.showStepStatus Whether to display a progress indicator.
 * @prop {boolean} cfg.showNavBar Whether to display a navigation bar.
 * @prop {string[]} cfg.steps List of IDs of the individual wizard step tabs.
 * @prop {string} cfg.effect Animation effect to use when showing and hiding wizard.
 * @prop {number} cfg.effectDuration Duration of the animation effect in milliseconds.
 */
PrimeFaces.widget.Wizard = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.content = $(this.jqId + '_content');
        this.backNav = $(this.jqId + '_back');
        this.nextNav = $(this.jqId + '_next');
        this.cfg.formId = this.jq.parents('form:first').attr('id');
        this.currentStep = this.cfg.initialStep;

        var _self = this;

        //Step controls
        if(this.cfg.showStepStatus) {
            this.stepControls = $(this.jqId + ' .ui-wizard-step-titles li.ui-wizard-step-title');
        }

        //Navigation controls
        if(this.cfg.showNavBar) {
            var currentStepIndex = this.getStepIndex(this.currentStep);

            //visuals
            PrimeFaces.skinButton(this.backNav);
            PrimeFaces.skinButton(this.nextNav);

            //events
            this.backNav.on("click", function() {_self.back();});
            this.nextNav.on("click", function() {_self.next();});

            if(currentStepIndex == 0)
                this.backNav.hide();
            else if(currentStepIndex == this.cfg.steps.length - 1)
                this.nextNav.hide();
        }
    },

    /**
     * Returns to the previous wizard step.
     */
    back: function() {
        var $this = this;
        if(this.cfg.onback) {
            var value = this.cfg.onback.call(this);
            if(value === false) {
                return;
            }
        }

        var targetStepIndex = this.getStepIndex(this.currentStep) - 1;
        if(targetStepIndex >= 0) {
            var stepToGo = this.cfg.steps[targetStepIndex];
            if (this.cfg.effect) {
                this.content.hide($this.cfg.effect, {}, $this.cfg.effectDuration, function() {
                    $this.loadStep(stepToGo, "back");
                    $this.content.show($this.cfg.effect, {}, $this.cfg.effectDuration);
                });
            } else {
                this.loadStep(stepToGo, "back");
            }
        }
    },

    /**
     * Advances to the next wizard step.
     */
    next: function() {
        var $this = this;
        if(this.cfg.onnext) {
            var value = this.cfg.onnext.call(this);
            if(value === false) {
                return;
            }
        }

        var targetStepIndex = this.getStepIndex(this.currentStep) + 1;
        if(targetStepIndex < this.cfg.steps.length) {
            var stepToGo = this.cfg.steps[targetStepIndex];
            if (this.cfg.effect) {
                this.content.hide($this.cfg.effect, {}, $this.cfg.effectDuration, function() {
                    $this.loadStep(stepToGo, "next");
                    $this.content.show($this.cfg.effect, {}, $this.cfg.effectDuration);
                });
            } else {
                this.loadStep(stepToGo, "next");
            }
        }
    },

    /**
     * Loads the given wizard step via AJAX, if not already loaded.
     * @private
     * @param {string} stepToGo ID of the wizard step tab to load. 
     * @param {string} event Type of event that triggered the loading, `back` or `next`. 
     */
    loadStep: function(stepToGo, event) {
        var $this = this,
        options = {
            source: this.id,
            process: this.id,
            update: this.id,
            formId: this.cfg.formId,
            params: [
                {name: this.id + '_direction', value: event},
                {name: this.id + '_stepToGo', value: stepToGo}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            this.content.html(content);
                        }
                    });

                return true;
            },
            oncomplete: function(xhr, status, args, data) {
                $this.currentStep = args.currentStep;

                if(!args.validationFailed) {
                    var currentStepIndex = $this.getStepIndex($this.currentStep);

                    if($this.cfg.showNavBar) {
                        if(currentStepIndex === $this.cfg.steps.length - 1) {
                            $this.hideNextNav();
                            $this.showBackNav();
                        } else if(currentStepIndex === 0) {
                            $this.hideBackNav();
                            $this.showNextNav();
                        } else {
                            $this.showBackNav();
                            $this.showNextNav();
                        }
                    }

                    //update step status
                    if($this.cfg.showStepStatus) {
                        $this.stepControls.removeClass('ui-state-highlight');
                        $($this.stepControls.get(currentStepIndex)).addClass('ui-state-highlight');
                    }
                }
            }
        };

        if(this.hasBehavior(event)) {
            this.callBehavior(event, options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    },

    /**
     * Finds the index of the given wizard step.
     * @param {string} step ID of the wizard step tab to check.
     * @return {number} The 0-based index of the given wizard step tab.
     */
    getStepIndex: function(step) {
        for(var i=0; i < this.cfg.steps.length; i++) {
            if(this.cfg.steps[i] == step)
                return i;
        }

        return -1;
    },

    /**
     * Shows the button for navigating to the next wizard step.
     */
    showNextNav: function() {
        this.nextNav.fadeIn();
    },

    /**
     * Hides the button for navigating to the next wizard step.
     */
    hideNextNav: function() {
        this.nextNav.fadeOut();
    },

    /**
     * Shows the button for navigating to the previous wizard step.
     */
    showBackNav: function() {
        this.backNav.fadeIn();
    },

    /**
     * Hides the button for navigating to the previous wizard step.
     */
    hideBackNav: function() {
        this.backNav.fadeOut();
    }

});
