/**
 * The configuration for the {@link SelectBooleanCheckbox} widget.
 */
export interface SelectBooleanCheckboxCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

/**
 * __PrimeFaces SelectBooleanCheckbox Widget__
 *
 * SelectBooleanCheckbox is an extended version of the standard checkbox with theme integration.
 */
export class SelectBooleanCheckbox<Cfg extends SelectBooleanCheckboxCfg = SelectBooleanCheckboxCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The hidden checkbox input with the value. */
    input: JQuery<HTMLInputElement> = $();
    /** The visible box. */
    box: JQuery = $();
    /** The icon inside the box. */
    icon: JQuery = $();
    /** The label of the checkbox. */
    itemLabel: JQuery = $();
    /** Whether this checkbox is disabled. */
    disabled: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.input = $(this.jqId + '_input') as JQuery<HTMLInputElement>;
        this.box = this.jq.find('.p-chkbox-box');
        this.icon = this.box.children('.p-chkbox-icon');
        this.itemLabel = this.jq.find('.p-chkbox-label');
        this.disabled = this.input.is(':disabled');

        // pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        this.bindEvents();
    }

    private bindEvents(): void {
        if (this.disabled) {
            return;
        }
        this.box.removeClass('p-state-disabled')
            .off('.selectBooleanCheckbox')
            .on('mouseenter.selectBooleanCheckbox', () => {
                this.box.addClass('p-state-hover');
            })
            .on('mouseleave.selectBooleanCheckbox', () => {
                this.box.removeClass('p-state-hover');
            })
            .on('click.selectBooleanCheckbox', () => {
                this.input.trigger('click').trigger('focus.selectBooleanCheckbox');
            });

        this.input.off('.selectBooleanCheckbox')
            .on('focus.selectBooleanCheckbox', () => {
                this.box.addClass('p-state-focus');
            })
            .on('blur.selectBooleanCheckbox', () => {
                this.box.removeClass('p-state-focus');
            })
            .on('change.selectBooleanCheckbox', () => {
                const isChecked = this.isChecked();
                this.input.prop('checked', isChecked).attr('aria-checked', String(isChecked));
                this.box.toggleClass('p-state-active', isChecked)
                    .children('.p-chkbox-icon')
                    .toggleClass('p-checkbox-checked-icon', isChecked);
            });

        this.itemLabel.removeClass('p-state-disabled')
            .off('.selectBooleanCheckbox')
            .on('click.selectBooleanCheckbox', () => {
                this.toggle();
                this.input.trigger('focus');
            });
    }

    /**
     * Checks this checkbox if it is unchecked, unchecks it otherwise.
     */
    toggle(): void {
        if (this.isChecked()) {
            this.uncheck();
        }
        else {
            this.check();
        }
    }

    /**
     * @returns `true` if this checkbox is checked.
     */
    isChecked(): boolean {
        return this.input.prop('checked');
    }

    /**
     * Checks this checkbox, if it is not checked already.
     * @param silent `true` to suppress the `change` event.
     */
    check(silent?: boolean): void {
        if (!this.isChecked()) {
            this.input.prop('checked', true).attr('aria-checked', 'true');
            this.box.addClass('p-state-active')
                .children('.p-chkbox-icon')
                .addClass('p-checkbox-checked-icon');

            if (!silent) {
                this.input.trigger('change');
            }
        }
    }

    /**
     * Unchecks this checkbox, if it is not unchecked already.
     * @param silent `true` to suppress the `change` event.
     */
    uncheck(silent?: boolean): void {
        if (this.isChecked()) {
            this.input.prop('checked', false).attr('aria-checked', 'false');
            this.box.removeClass('p-state-active')
                .children('.p-chkbox-icon')
                .removeClass('p-checkbox-checked-icon');

            if (!silent) {
                this.input.trigger('change');
            }
        }
    }

    /**
     * Enables this checkbox so that the user can change it.
     */
    override enable(): void {
        this.disabled = false;
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.bindEvents();
    }

    /**
     * Disables this checkbox so that the user cannot change it anymore.
     */
    override disable(): void {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }

    /**
     * Resets the checkbox to unchecked.
     * @param silent `true` to suppress the `change` event.
     */
    resetValue(silent?: boolean): void {
        this.uncheck(silent);
    }
}
