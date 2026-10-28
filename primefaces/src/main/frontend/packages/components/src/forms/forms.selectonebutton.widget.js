/**
 * __PrimeFaces SelectOneButton Widget__
 * 
 * SelectOneButton is an input component to do a single select.
 * 
 * @typedef PrimeFaces.widget.SelectOneButton.ChangeCallback Callback that is invoked when the value of this widget has
 * changed. See also {@link SelectOneButtonCfg.change}.
 * @this {PrimeFaces.widget.SelectOneButton} PrimeFaces.widget.SelectOneButton.ChangeCallback 
 * 
 * @prop {boolean} facet Whether custom is used with a facet.
 * @prop {JQuery} buttons The DOM element for the button options the user can select.
 * @prop {JQuery} inputs The DOM element for the hidden input fields storing the value of this widget. 
 * 
 * @interface {PrimeFaces.widget.SelectOneButtonCfg} cfg The configuration for the {@link  SelectOneButton| SelectOneButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @prop {function(): void} cfg.confirmation Callback that requests confirmation before changing the value.
 * 
 * @prop {boolean} cfg.custom Whether a custom layout is enabled.
 * @prop {boolean} cfg.unselectable Whether selection can be cleared.
 * @prop {PrimeFaces.widget.SelectOneButton.ChangeCallback} cfg.change Callback that is invoked when the value of this
 * widget has changed.
 */
PrimeFaces.widget.SelectOneButton = class SelectOneButton extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.cfg.unselectable = this.cfg.unselectable !== false;
        // the default layout root is also a radiogroup, so the role alone doesn't identify the facet span
        this.facet = this.cfg.custom && this.jq.attr('role') === 'radiogroup';
        this.inputs = this.jq.find((this.facet ? '.p-hidden ' : '') + ':radio:not(:disabled)');

        //custom layout
        if(this.cfg.custom) {
            // For facet layout, buttons are within jq. For referenced layout, search in parent container
            if (this.facet) {
                this.buttons = this.jq.find('[role="radio"]:not(.p-state-disabled)');
            }
            else {
                this.buttons = this.jq.parent().find('[role="radio"]:not(.p-state-disabled)');
            }

            //update button state
            for(let i = 0; i < this.inputs.length; i++) {
                const original = this.inputs.eq(i);

                if(original.is(':checked')) {
                    this.buttons.eq(i).addClass('p-state-active').attr('aria-checked', 'true');
                }

                if(original.is(':disabled')) {
                    this.disable(i);
                }
            }
        }
        //regular layout
        else {
            this.buttons = this.jq.children('div:not(.p-state-disabled)');
        }

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents() {
        this.buttons.filter(':not(.p-state-disabled)').off('mouseover.selectOneButton mouseout.selectOneButton click.selectOneButton')
        .on('mouseover.selectOneButton', function() {
            $(this).addClass('p-state-hover');
        })
        .on('mouseout.selectOneButton', function() {
            $(this).removeClass('p-state-hover');
        })
        .on('click.selectOneButton', (e) => {
            // clicks on the native radio itself (e.g. fired by keyboard activation) are handled by the change listener
            if ($(e.target).is(':radio')) {
                return;
            }
            this.handleButtonInteraction(e.currentTarget, e);
            e.stopPropagation();
            e.preventDefault();
        });

        /* keyboard interaction and focus are handled by the native radio inputs */
        this.inputs.off('focus.selectOneButton blur.selectOneButton change.selectOneButton')
        .on('focus.selectOneButton', (e) => {
            $(e.currentTarget).closest('.p-button').addClass('p-state-focus');
        })
        .on('blur.selectOneButton', (e) => {
            $(e.currentTarget).closest('.p-button').removeClass('p-state-focus');
        })
        .on('change.selectOneButton', (e) => {
            if (e.currentTarget.checked) {
                const index = this.inputs.index(e.currentTarget);
                const button = this.buttons.eq(index);
                if (this.cfg.confirmation) {
                    this.inputs.each((i, input) => {
                        input.checked = this.buttons.eq(i).hasClass('p-state-active');
                    });
                }
                this.confirmChange(() => this.select(button));
            }
        });
    }

    /**
     * Handles button interaction (click or keyboard action) for selecting/unselecting.
     * @private
     * @param {HTMLElement} target The button element that was interacted with.
     * @param {Event} e The event object.
     */
    handleButtonInteraction(target, e) {
        const button = $(target);
        const isActive = button.hasClass('p-state-active');

        button.find(':radio').trigger('focus');

        if (isActive && !this.cfg.unselectable) {
            return;
        }

        this.confirmChange(() => {
            if (isActive) {
                this.unselect(button);
            }
            else {
                this.select(button);
            }
        });
    }

    /**
     * Runs a user selection after confirmation, when configured.
     * @private
     * @param {function(): void} action The selection change to perform.
     */
    confirmChange(action) {
        if (this.cfg.confirmation) {
            this.jq.data('pfconfirmcommand', action);
            this.cfg.confirmation.call(this.jq[0]);
        }
        else {
            action();
        }
    }

    /**
     * Selects the given button option. If another button option is selected already, it will be unselected.
     * @param {JQuery} button A button of this widget to select.
     */
    select(button) {
        const active = this.buttons.filter('.p-state-active');
        active.removeClass('p-state-active p-state-hover');

        if (this.cfg.custom) {
            active.attr('aria-checked', 'false');
            const index = this.buttons.index(button);
            if (index >= 0) {
                this.inputs.prop('checked', false);
                this.inputs.eq(index).prop('checked', true);
            }
            button.attr('aria-checked', 'true');
        }
        else {
            active.find(':radio').prop('checked', false);
            button.find(':radio').prop('checked', true);
        }

        button.addClass('p-state-active');

        this.triggerChange();
    }

    /**
     * Unselects the given button option.
     * @param {JQuery} button A button of this widget to unselect.
     */
    unselect(button) {
        if(this.cfg.unselectable) {
            button.removeClass('p-state-active p-state-hover');

            if (this.cfg.custom) {
                button.attr('aria-checked', 'false');
                const index = this.buttons.index(button);
                if (index >= 0) {
                    this.inputs.eq(index).prop('checked', false);
                }
            }
            else {
                button.find(':radio').prop('checked', false);
            }

            this.triggerChange();
        }
    }

    /**
     * Trigger the change behavior when the value of this widget has changed.
     * @private
     */
    triggerChange() {
        if(this.cfg.change) {
            this.cfg.change.call(this);
        }

        this.callBehavior('change');
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        if(this.cfg.custom) {
            for(let i = 0; i < this.buttons.length; i++) {
                const button = this.buttons.eq(i);
                this.enable(i);
                button.removeClass('p-state-active');
                if (i < this.inputs.length) {
                    this.inputs.eq(i).prop('checked', false);
                }
            }
        }

        this.init(cfg);
    }

    /**
     * Disables this input components so that the user cannot select an option anymore.
     * @param {number} [index] Index of the button option to disable. Disables all buttons if omitted.
     */
    disable(index) {
        if (index === null || index === undefined) {
            this.buttons.removeClass('p-state-hover p-state-focus p-state-active')
                    .addClass('p-state-disabled').attr('disabled', 'disabled');
            this.inputs.attr('disabled', 'disabled');
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('p-state-hover p-state-focus p-state-active')
                    .addClass('p-state-disabled').attr('disabled', 'disabled');
            this.inputs.eq(index).attr('disabled', 'disabled');
        }
    }

    /**
     * Enables this input components so that the user can select an option.
     * @param {number} [index] Index of the button option to enable. Enables all buttons if omitted.
     */
    enable(index) {
        if (index === null || index === undefined) {
            this.buttons.removeClass('p-state-disabled').removeAttr('disabled');
            this.inputs.removeAttr('disabled');
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('p-state-disabled').removeAttr('disabled');
            this.inputs.eq(index).removeAttr('disabled');
        }
    }

}
