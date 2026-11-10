/**
 * The configuration for the {@link TriStateCheckbox} widget.
 */
export interface TriStateCheckboxCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

/**
 * __PrimeFaces TriStateCheckbox Widget__
 *
 * TriStateCheckbox adds a new state to a checkbox value.
 */
export class TriStateCheckbox<Cfg extends TriStateCheckboxCfg = TriStateCheckboxCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the hidden input field storing the value of this widget. */
    input: JQuery = $();
    /** The DOM element for the container with the label and the icon. */
    box: JQuery = $();
    /** The DOM element for the icon showing the current state of this checkbox widget. */
    icon: JQuery = $();
    /** The DOM element for the label of the checkbox. */
    itemLabel: JQuery = $();
    /** The DOM element for the aria-label of the checkbox. */
    ariaHelper: JQuery = $();
    /** Whether this widget is disabled, also `true` when it is read-only. */
    disabled: boolean = false;
    /** Whether the user can change the state of the checkbox. */
    readonly: boolean = false;
    /** A modulo operation with the result being in the range `[0,mod)`. */
    fixedMod: PrimeFacesTypes.widget.TriStateCheckbox.FixedMod = (number, mod) => ((number % mod) + mod) % mod;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.p-chkbox-box');
        this.icon = this.box.children('.p-chkbox-icon');
        this.itemLabel = this.jq.find('.p-chkbox-label');
        this.ariaHelper = $('<span class="p-hidden-accessible" aria-live="polite"></span>').prependTo(this.jq);
        this.updateAriaLabel();
        this.updateStatus();
        this.fixedMod = function(number, mod) {
            return ((number % mod) + mod) % mod;
        };

        //bind events if not disabled/readonly
        if (!this.disabled) {
            this.box.on('mouseenter.triStateCheckbox', () => {
                this.box.addClass('p-state-hover');
            })
            .on('mouseleave.triStateCheckbox', () => {
                this.box.removeClass('p-state-hover');
            })
            .on('click.triStateCheckbox', () => {
                this.toggle(1);
                this.input.trigger('focus');
            });

            this.input.on('focus.triStateCheckbox', () => {
                this.box.addClass('p-state-focus');
            })
            .on('blur.triStateCheckbox', () => {
                this.box.removeClass('p-state-focus');
            })
            .on('keydown.triStateCheckbox', (e) => {
                switch (e.key) {
                    case ' ':
                    case 'Enter':
                    case 'ArrowUp':
                    case 'ArrowDown':
                    case 'ArrowLeft':
                    case 'ArrowRight':
                        e.preventDefault();
                        break;
                }
            })
            .on('keyup.triStateCheckbox', (e) => {
                switch (e.key) {
                    case ' ':
                    case 'Enter':
                    case 'ArrowUp':
                    case 'ArrowRight':
                        this.toggle(1);
                        break;
                    case 'ArrowDown':
                    case 'ArrowLeft':
                        this.toggle(-1);
                        break;
                }
            });

            //toggle state on label click
            this.itemLabel.on('click.triStateCheckbox', () => {
                this.toggle(1);
                this.input.trigger('focus');
            });

            // client behaviors
            if (this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.input, this.cfg.behaviors as unknown as Parameters<typeof PrimeFaces.attachBehaviors>[1]);
            }
        }

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    /**
     * Toggles this tri state checkbox in the given direction. Moves between unchecked, half-checked, and fully-checked.
     * @param direction `-1` to move backwards through the states, `+1` to move forward through the states
     */
    toggle(direction?: -1 | 1): void {
        if (!this.disabled) {
            // default to switch to next state
            if (isNaN(direction as number)) {
                direction = 1;
            }

            const oldValue = parseInt(this.input.val() as string);
            const newValue = this.fixedMod((oldValue + (direction as number)), 3);
            this.input.val(newValue);

            // remove / add def. icon and active classes
            if (newValue === 0) {
                this.box.removeClass('p-state-active');
            } else {
                this.box.addClass('p-state-active');
            }

            // remove old icon and add the new one: none (null), checked (true), crossed (false)
            const iconsClasses = ['', 'p-checkbox-checked-icon', 'p-checkbox-crossed-icon'];
            this.icon.removeClass(iconsClasses[oldValue]).addClass(iconsClasses[newValue]!);

            // change title to the new one
            const iconTitles = this.box.data('titlestates') as { titles?: string[] | null } | null | undefined;
            if (iconTitles != null && iconTitles.titles != null && iconTitles.titles.length > 0) {
                this.box.attr('title', iconTitles.titles[newValue]!);
            }

            this.updateAriaLabel();

            // fire change event
            this.input.trigger('change');
        }
    }

    /**
     * Updates the aria-label of the component based on its current state.
     * If a title is set on the box element, that will be used as the aria-label.
     * Otherwise, a default label will be used based on the current value:
     * - 0: nullLabel
     * - 1: trueLabel
     * - 2: falseLabel
     */
    private updateAriaLabel(): void {
        let ariaLabel = this.box.attr('title');
        if (!ariaLabel) {
            const value = parseInt(this.input.val() as string);
            if (value === 0) {
                ariaLabel = this.getAriaLabel('nullLabel');
            } else if (value === 1) {
                ariaLabel = this.getAriaLabel('trueLabel');
            } else if (value === 2) {
                ariaLabel = this.getAriaLabel('falseLabel');
            }
        }
        this.ariaHelper.text(ariaLabel as string);
    }

    /**
     * Updates the disabled/readonly status of the component.
     */
    private updateStatus(): void {
        this.readonly = this.box.hasClass('p-chkbox-readonly');
        this.disabled = this.input.is(':disabled') || this.readonly;
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.updateStatus();
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    override enable(): void {
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.updateStatus();
    }
}
