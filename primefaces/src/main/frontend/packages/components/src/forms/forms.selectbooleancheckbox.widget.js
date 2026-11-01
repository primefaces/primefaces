/**
 * __PrimeFaces SelectBooleanCheckbox Widget__
 * 
 * SelectBooleanCheckbox is an extended version of the standard checkbox with theme integration.
 * 
 * @prop {JQuery} input The DOM element for the hidden input field storing the current value of this widget.
 * @prop {JQuery} box The DOM element for the box with the checkbox.
 * @prop {JQuery} icon The DOM element for the checked or unchecked checkbox icon.
 * @prop {JQuery} itemLabel The DOM element for the label of the checkbox.
 * @prop {boolean} disabled Whether this checkbox is disabled.
 * 
 * @interface {PrimeFaces.widget.SelectBooleanCheckboxCfg} cfg The configuration for the {@link  SelectBooleanCheckbox| SelectBooleanCheckbox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.SelectBooleanCheckbox = class SelectBooleanCheckbox extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.p-chkbox-box');
        this.icon = this.box.children('.p-chkbox-icon');
        this.itemLabel = this.jq.find('.p-chkbox-label');
        this.disabled = this.input.is(':disabled');

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);

        this.bindEvents();
    }


    /**
     * Binds the event handlers for the checkbox.
     * This method sets up event listeners for mouse interactions, focus/blur events,
     * change events, and label clicks if the checkbox is not disabled.
     * @private
     */
    bindEvents() {
        if (this.disabled) {
            return;
        }
        var $this = this;
        this.box.removeClass('p-state-disabled')
            .off('.selectBooleanCheckbox')
            .on('mouseenter.selectBooleanCheckbox', function () {
                $this.box.addClass('p-state-hover');
            })
            .on('mouseleave.selectBooleanCheckbox', function () {
                $this.box.removeClass('p-state-hover');
            })
            .on('click.selectBooleanCheckbox', function () {
                $this.input.trigger('click').trigger('focus.selectBooleanCheckbox');
            });

        this.input.off('.selectBooleanCheckbox')
            .on('focus.selectBooleanCheckbox', function () {
                $this.box.addClass('p-state-focus');
            })
            .on('blur.selectBooleanCheckbox', function () {
                $this.box.removeClass('p-state-focus');
            })
            .on('change.selectBooleanCheckbox', function () {
                var isChecked = $this.isChecked();
                $this.input.prop('checked', isChecked).attr('aria-checked', isChecked);
                $this.box.toggleClass('p-state-active', isChecked)
                    .children('.p-chkbox-icon')
                    .toggleClass('p-checkbox-checked-icon', isChecked);
            });

        this.itemLabel.removeClass('p-state-disabled')
            .off('.selectBooleanCheckbox')
            .on("click.selectBooleanCheckbox", function () {
                $this.toggle();
                $this.input.trigger('focus');
            });
    }

    /**
     * Toggles the state of the checkbox.
     * If the checkbox is currently checked, it will be unchecked.
     * If the checkbox is currently unchecked, it will be checked.
     */
    toggle() {
        this.isChecked() ? this.uncheck() : this.check();
    }

    /**
     * Checks whether this checkbox is currently checked.
     * @return {boolean} `true` if this checkbox is checked, or `false` otherwise.
     */
    isChecked() {
        return this.input.prop('checked');
    }

    /**
     * Checks this checkbox, if it is not checked already.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    check(silent) {
        if (!this.isChecked()) {
            this.input.prop('checked', true).attr('aria-checked', true);
            this.box.addClass('p-state-active')
                .children('.p-chkbox-icon')
                .addClass('p-checkbox-checked-icon');

            if (!silent) {
                this.input.trigger('change');
            }
        }
    }

    /**
     * Unchecks this checkbox, if it is not unchecked already .
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    uncheck(silent) {
        if (this.isChecked()) {
            this.input.prop('checked', false).attr('aria-checked', false);
            this.box.removeClass('p-state-active')
                .children('.p-chkbox-icon')
                .removeClass('p-checkbox-checked-icon');

            if (!silent) {
                this.input.trigger('change');
            }
        }
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    enable() {
        this.disabled = false;
        PrimeFaces.utils.enableInputWidget(this.jq, this.input);
        this.bindEvents();
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.input);
        this.disabled = true;
    }

    /**
     * Resets the input.
     * @param {boolean} [silent] `true` to suppress triggering event listeners, or `false` otherwise.
     */
    resetValue(silent) {
        this.uncheck(silent);
    }
}
