/**
 * The configuration for the {@link Password} widget.
 */
export interface PasswordCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Enables strength indicator. */
    feedback: boolean;
    /** Label of the password prompt. */
    promptLabel: string;
    /** Text of the hint when the password is judged to be weak. */
    weakLabel: string;
    /** Text of the hint when the password is judged to be good. */
    goodLabel: string;
    /** Text of the hint when the password is judged to be strong. */
    strongLabel: string;
    /** Displays feedback inline rather than using a popup. */
    inline: boolean;
    /** Event displaying the feedback overlay. Default is 'focus'. */
    showEvent: string;
    /** Event hiding the feedback overlay. Default is 'blur'. */
    hideEvent: string;
    /** Whether or not this password can be unmasked/remasked. */
    unmaskable: boolean;
}

/**
 * __PrimeFaces Password Widget__
 *
 * Password component is an extended version of standard inputSecret component with theme integration and strength
 * indicator.
 */
export class Password<Cfg extends PasswordCfg = PasswordCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the informational text regarding how strong the password is. */
    infoText?: JQuery;
    /** The DOM element for mask/unmask icon */
    icon?: JQuery;
    /** The DOM element for the gauge giving visual feedback regarding how strong the password is. */
    meter?: JQuery;
    /** The DOM element for the overlay panel with the hint regarding how strong the password is. */
    panel?: JQuery;
    /** Handler for CSS transitions used by this widget. */
    transition?: PrimeFacesTypes.CssTransitionHandler | null;
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        if (!this.jq.is(':disabled')) {
            if (this.cfg.feedback) {
                this.setupFeedback();
            }

            if (this.cfg.unmaskable) {
                this.setupUnmasking();
            }

            PrimeFaces.skinInput(this.jq);
        }
    }

    /**
     * Sets up the overlay panel informing the user about how good the password their typed is.
     */
    private setupFeedback(): void {
        //remove previous panel if any
        const oldPanel = $(this.jqId + '_panel');
        if (oldPanel.length == 1) {
            oldPanel.remove();
        }

        //config
        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent + '.password' : 'focus.password';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent + '.password' : 'blur.password';
        this.cfg.promptLabel = this.cfg.promptLabel || this.getLabel('passwordPrompt');
        this.cfg.weakLabel = this.cfg.weakLabel || this.getLabel('weak');
        this.cfg.goodLabel = this.cfg.goodLabel || this.getLabel('medium');
        this.cfg.strongLabel = this.cfg.strongLabel || this.getLabel('strong');

        const panelStyle = this.cfg.inline ? 'p-password-panel-inline' : 'p-password-panel-overlay';

        //create panel element
        let panelMarkup = '<div id="' + this.id + '_panel" class="p-password-panel p-widget p-state-highlight p-hidden ' + panelStyle + '">';
        panelMarkup += '<div class="p-password-meter" style="background-position:0pt 0pt">&nbsp;</div>';
        panelMarkup += '<div class="p-password-info">' + PrimeFaces.escapeHTML(this.cfg.promptLabel as string) + '</div>';
        panelMarkup += '</div>';

        this.panel = $(panelMarkup).insertAfter(this.jq);
        this.meter = this.panel.children('div.p-password-meter');
        this.infoText = this.panel.children('div.p-password-info');

        //events
        this.jq.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent + ' keyup.password')
            .on(this.cfg.showEvent, () => {
                this.show();
            })
            .on(this.cfg.hideEvent, () => {
                this.hide();
            })
            .on("keyup.password", () => {
                const value = this.jq.val() as string;
                let label: string | null | undefined = null,
                    meterPos: string | null = null;

                if (value.length == 0) {
                    label = this.cfg.promptLabel;
                    meterPos = '0px 0px';
                }
                else {
                    const score = this.testStrength(this.jq.val() as string);

                    if (score < 30) {
                        label = this.cfg.weakLabel;
                        meterPos = '0px -10px';
                    }
                    else if (score >= 30 && score < 80) {
                        label = this.cfg.goodLabel;
                        meterPos = '0px -20px';
                    }
                    else if (score >= 80) {
                        label = this.cfg.strongLabel;
                        meterPos = '0px -30px';
                    }
                }

                //update meter and info text
                this.meter!.css('background-position', meterPos as string);
                this.infoText!.text(label as string);
            });

        //overlay setting
        if (!this.cfg.inline) {
            this.panel.appendTo('body');
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');
        }

        // the panel is created on init (if enabled), so remove it on refresh too
        const removePanel = function(this: Password) {
            this.panel!.remove();
        };
        this.addRefreshListener(removePanel);
        this.addDestroyListener(removePanel);
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        //Hide overlay on resize/scroll
        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });

        this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, () => {
            if (PrimeFaces.hideOverlaysOnViewportChange === true) {
                this.hide();
            }
        });
    }

    /**
     * Unbind all panel event listeners
     */
    private unbindPanelEvents(): void {
        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }

    /**
     * Sets up the ability to unmask and remask the password.
     */
    private setupUnmasking(): void {
        this.icon = $(PrimeFaces.escapeClientId(this.id + '_mask'));
        // add ARIA accessibility
        this.icon.attr('role', 'switch')
            .attr('aria-checked', 'true')
            .attr('aria-label', this.getAriaLabel('passwordShow'))
            .attr('tabindex', this.jq.attr('tabindex') || '0');

        // add keyboard support
        this.icon.off('keydown.password').on('keydown.password', (event) => {
            if (PrimeFaces.utils.isActionKey(event)) {
                this.toggleMask();
                event.preventDefault();
            }
        });

        this.icon.off('click.password').on('click.password', () => {
            this.toggleMask();
        });
    }

    /**
     * Toggle masking and unmasking the password.
     */
    toggleMask(): void {
        if (!this.cfg.unmaskable) {
            return;
        }

        if (this.jq.attr('type') === 'password') {
            this.jq.attr('type', 'text').parent().removeClass('p-password-masked').addClass('p-password-unmasked');
            this.icon!.attr('aria-checked', 'false').attr('aria-label', this.getAriaLabel('passwordHide'));
        }
        else {
            this.jq.attr('type', 'password').parent().removeClass('p-password-unmasked').addClass('p-password-masked');
            this.icon!.attr('aria-checked', 'true').attr('aria-label', this.getAriaLabel('passwordShow'));
        }
    }

    /**
     * Computes a numerical score that estimates how strong the given password is. The returned value can range from `0`
     * (very weak) to `128` (very strong). This test takes into account whether the password has got a certain minimal
     * length and whether it contains characters from certain character classes.
     * @param password A password to check for its strength.
     * @returns A value between `0` and `128` that indicates how good the password is, with `0` indicating a
     * very weak password and `128` indicating a very strong password.
     */
    testStrength(password: string): number {
        // return a number between 0 and 100.
        let score = 0;

        // must be at least 8 characters
        if (!password || password.length < 8)
            return score;

        // require 3 of the following 4 categories
        const variations = {
            digits: /\d/.test(password),
            lower: /[a-z]/.test(password),
            upper: /[A-Z]/.test(password),
            nonWords: /\W/.test(password)
        }

        // Object.values, not in the ES2016 lib
        const variationCount = [variations.digits, variations.lower, variations.upper, variations.nonWords].filter(Boolean).length;
        score += variationCount * 28;

        return parseInt(String(score));
    }

    /**
     * Returns a normalized value between `0` and `1.5` that indicates how much bigger the first input x is compared
     * to the other input y. `0` means that x is much smaller than `y`, a value of `1.5` mean that `x` is much larger
     * than `y`.
     * @param x First input, must be a non-negative number.
     * @param y  Second input, must be a positive number
     * @returns A value between `0` and `1.5` that indicates how big `x` is compared to `y`.
     */
    normalize(x: number, y: number): number {
        const diff = x - y;

        if (diff <= 0) {
            return x / y;
        }
        else {
            return 1 + 0.5 * (x / (x + y / 4));
        }
    }

    /**
     * Align the panel with the password strength indicator so that it is next to the password input field.
     */
    private align(): void {
        PrimeFaces.utils.position(this.panel!.css({
            left: '',
            top: '',
            'min-width': this.jq.outerWidth()!,
            'transform-origin': 'center top'
        }), {
            my: 'left top'
            , at: 'left bottom'
            , of: this.jq
            , collision: 'flipfit'
            , using: function(pos: { top: number; left: number }, directions: PrimeFacesTypes.utils.PositionFeedback) {
                $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
            }
        });
    }

    /**
     * Brings up the panel with the password strength indicator.
     */
    show(): void {
        if (!this.cfg.inline) {
            if (this.transition) {
                this.transition.show({
                    onEnter: () => {
                        PrimeFaces.nextZindex(this.panel);
                        this.align();
                    },
                    onEntered: () => {
                        this.bindPanelEvents();
                    }
                });
            }
        }
        else {
            this.panel!.css({ width: this.jq.outerWidth()! });
            this.panel!.slideDown();
        }
    }

    /**
     * Hides the panel with the password strength indicator.
     */
    hide(): void {
        if (this.cfg.inline) {
            this.panel!.slideUp();
        }
        else if (this.transition) {
            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                }
            });
        }
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq);
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, undefined);
    }

}
