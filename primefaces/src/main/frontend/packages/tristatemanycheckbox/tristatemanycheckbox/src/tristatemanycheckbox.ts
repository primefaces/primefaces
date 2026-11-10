/**
 * The state of an item: `0` the first (unchecked), `1` the second (checked) and `2` the third (crossed) state.
 */
export type TriStateManyCheckboxState = 0 | 1 | 2;

/**
 * The configuration for the {@link TriStateManyCheckbox} widget.
 */
export interface TriStateManyCheckboxCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** The style classes of the icon of each state. */
    icons: [string, string, string];
    /** Whether the component is read only. */
    readonly: boolean;
    /** The title of each state, only set if a state has one. */
    titles?: [string, string, string];
}

/**
 * __PrimeFaces TriStateManyCheckbox Widget__
 *
 * TriStateManyCheckbox is a group of checkboxes with three states each. A click on the box or the label, Space, Enter,
 * ArrowUp and ArrowRight move an item to the next state, ArrowDown and ArrowLeft to the previous one. The state of each
 * item is kept in an input.
 */
export class TriStateManyCheckbox<Cfg extends TriStateManyCheckboxCfg = TriStateManyCheckboxCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The inputs with the state of each item. */
    inputs: JQuery<HTMLInputElement> = $();
    /** The boxes showing the state of each item. */
    boxes: JQuery = $();
    /** The labels of the items. */
    labels: JQuery = $();

    /** The live region announcing a changed state to screen readers. */
    private ariaHelper: JQuery = $();
    /** The indices of the items that are not disabled on the server, only they are disabled and enabled by the widget. */
    private enabledItems: number[] = [];

    /**
     * Initializes the widget and binds the events.
     * @param cfg The configuration of the widget.
     */
    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.inputs = this.jq.find('.p-hidden-accessible > input') as JQuery<HTMLInputElement>;
        this.boxes = this.jq.find('.p-chkbox-box');
        this.labels = this.jq.find('.p-chkbox-label');
        this.ariaHelper = $('<span class="p-hidden-accessible" aria-live="polite"></span>').prependTo(this.jq);
        this.enabledItems = this.inputs.toArray().flatMap((input, index) => input.disabled ? [] : [index]);

        // the inputs belong to the component (search expressions, validation messages)
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        this.bindEvents();
    }

    /**
     * Binds the events of the boxes, labels and inputs.
     */
    private bindEvents(): void {
        this.boxes.on('click.tristatemanycheckbox', (e) => this.onClick(this.boxes.index(e.currentTarget)));
        this.labels.on('click.tristatemanycheckbox', (e) => {
            e.preventDefault();
            this.onClick(this.labels.index(e.currentTarget));
        });

        this.inputs
            .on('focus.tristatemanycheckbox', (e) => this.boxes.eq(this.inputs.index(e.currentTarget)).addClass('p-state-focus'))
            .on('blur.tristatemanycheckbox', (e) => this.boxes.eq(this.inputs.index(e.currentTarget)).removeClass('p-state-focus'))
            .on('keydown.tristatemanycheckbox', (e) => {
                const index = this.inputs.index(e.currentTarget);
                // a held key changes the state once
                const repeat = (e.originalEvent as KeyboardEvent | undefined)?.repeat;
                switch (e.key) {
                    case ' ':
                    case 'Enter':
                    case 'ArrowUp':
                    case 'ArrowRight':
                        e.preventDefault();
                        if (!repeat) {
                            this.toggle(index, 1);
                        }
                        break;
                    case 'ArrowDown':
                    case 'ArrowLeft':
                        e.preventDefault();
                        if (!repeat) {
                            this.toggle(index, -1);
                        }
                        break;
                }
            });
    }

    /**
     * Moves an item to the next state after a click on its box or label and focuses it.
     * @param index The index of the item.
     */
    private onClick(index: number): void {
        const input = this.inputs[index];
        if (input && !input.disabled) {
            this.toggle(index, 1);
            input.focus();
        }
    }

    /**
     * Moves an item to the next or previous state and calls the `onchange` callback and the `change` behavior.
     * Nothing happens if the item is disabled or the component is read only.
     * @param index The index of the item.
     * @param direction `1` for the next state, `-1` for the previous state.
     */
    toggle(index: number, direction: 1 | -1 = 1): void {
        const input = this.inputs[index];
        if (!input || input.disabled || this.cfg.readonly) {
            return;
        }

        const state = ((this.getState(index) + direction + 3) % 3) as TriStateManyCheckboxState;
        this.setState(index, state);
        this.ariaHelper.text(this.cfg.titles?.[state] || this.getAriaLabel(state === 0 ? 'nullLabel' : state === 1 ? 'trueLabel' : 'falseLabel'));

        $(input).trigger('change');
        if (this.hasBehavior('change')) {
            this.callBehavior('change');
        }
    }

    /**
     * Retrieves the state of an item.
     * @param index The index of the item.
     * @returns The state of the item.
     */
    getState(index: number): TriStateManyCheckboxState {
        const state = Number(this.inputs[index]?.value);
        return state === 1 || state === 2 ? state : 0;
    }

    /**
     * Retrieves the states of all items.
     * @returns The state of each item.
     */
    getStates(): TriStateManyCheckboxState[] {
        return this.inputs.toArray().map((_input, index) => this.getState(index));
    }

    /**
     * Sets the state of an item, without calling the `onchange` callback or a behavior.
     * @param index The index of the item.
     * @param state The new state.
     */
    setState(index: number, state: TriStateManyCheckboxState): void {
        const input = this.inputs[index];
        const icon = this.cfg.icons?.[state];
        if (!input || icon === undefined) {
            return;
        }

        input.value = String(state);
        const box = this.boxes.eq(index).toggleClass('p-state-active', state !== 0);
        box.children('.p-chkbox-icon').attr('class', icon);
        if (this.cfg.titles) {
            box.attr('title', this.cfg.titles[state]);
        }
    }

    /**
     * Disables the items, their states are not submitted.
     */
    override disable(): void {
        this.setEnabled(false);
    }

    /**
     * Enables the items, except the ones that are disabled on the server.
     */
    override enable(): void {
        this.setEnabled(true);
    }

    /**
     * Enables or disables the items that are not disabled on the server.
     * @param enabled `true` to enable the items, `false` to disable them.
     */
    private setEnabled(enabled: boolean): void {
        const isEnabledItem = (index: number) => this.enabledItems.includes(index);
        this.inputs.filter(isEnabledItem).prop('disabled', !enabled);
        this.boxes.filter(isEnabledItem).toggleClass('p-state-disabled', !enabled);
        this.labels.filter(isEnabledItem).toggleClass('p-state-disabled', !enabled);
    }
}
