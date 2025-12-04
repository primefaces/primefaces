/**
 * __PrimeFaces SelectManyButton Widget__
 * 
 * SelectManyButton is a multi select component using button UI.
 * 
 * @typedef PrimeFaces.widget.SelectManyButton.ChangeCallback Callback that is invoked when the value of this widget has
 * changed. See also {@link SelectManyButtonCfg.change}.
 * @this {PrimeFaces.widget.SelectManyButton} PrimeFaces.widget.SelectManyButton.ChangeCallback 
 * 
 * @prop {boolean} facet Whether custom is used with a facet.
 * @prop {JQuery} buttons The DOM elements for the selectable buttons.
 * @prop {boolean} [disabled] `true` if this many select element is disabled, `false` if enabled, `undefined`
 * if the state is not known.
 * @prop {JQuery} inputs The DOM elements for the hidden input fields of type checkbox storing which buttons are
 * selected.
 * 
 * @interface {PrimeFaces.widget.SelectManyButtonCfg} cfg The configuration for the {@link  SelectManyButton| SelectManyButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.custom Whether a custom layout is enabled.
 * @prop {PrimeFaces.widget.SelectManyButton.ChangeCallback} cfg.change Callback that is invoked when the value of this
 * widget has changed.
 */
PrimeFaces.widget.SelectManyButton = class SelectManyButton extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.inputs = this.jq.find(':checkbox:not(:disabled)');

        //custom layout
        if(this.cfg.custom) {
            // For facet layout, buttons are within jq. For referenced layout, search in parent container
            const buttonsInJq = this.jq.find('[role="checkbox"]');
            if (buttonsInJq.length > 0) {
                this.facet = true;
                this.buttons = buttonsInJq;
            }
            else {
                this.facet = false;
                this.buttons = this.jq.parent().find('[role="checkbox"]');
            }

            //update button state
            for(let i = 0; i < this.inputs.length; i++) {
                const input = this.inputs.eq(i);

                if(input.is(':checked')) {
                    this.buttons.eq(i).addClass('ui-state-active');
                }

                if(input.is(':disabled')) {
                    this.disable(i);
                }
            }
        }
        //regular layout
        else {
            this.buttons = this.jq.children('div:not(.ui-state-disabled)');
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
        if (this.cfg.custom) {
            this.bindCustomLayoutEvents();
        }
        else {
            this.bindRegularLayoutEvents();
        }

        this.bindInputEvents();
    }

    /**
     * Binds event handlers for custom layout buttons.
     * @private
     */
    bindCustomLayoutEvents() {
        // Custom layout event handling
        this.buttons.filter(':not(.ui-state-disabled)').off('mouseover.selectManyButton mouseout.selectManyButton click.selectManyButton')
        .on('mouseover.selectManyButton', function() {
            const button = $(this);
            if(!button.hasClass('ui-state-active')) {
                button.addClass('ui-state-hover');
            }
        })
        .on('mouseout.selectManyButton', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectManyButton', (e) => {
            this.handleButtonInteraction(e.currentTarget, e);
            e.stopPropagation();
            e.preventDefault();
        });

        /* For keyboard accessibility on buttons */
        this.buttons.off('focus.selectManyButton blur.selectManyButton keydown.selectManyButton')
        .on('focus.selectManyButton', function(){
            $(this).addClass('ui-state-focus');
        })
        .on('blur.selectManyButton', function(){
            $(this).removeClass('ui-state-focus');
        })
        .on('keydown.selectManyButton', (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                this.handleButtonInteraction(e.currentTarget, e);
                e.preventDefault();
            }
        });
    }

    /**
     * Binds event handlers for regular layout buttons.
     * @private
     */
    bindRegularLayoutEvents() {
        // Regular layout event handling
        this.buttons.on('mouseover', function() {
            const button = $(this);
            if(!button.hasClass('ui-state-active')) {
                button.addClass('ui-state-hover');
            }
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function(e) {
            const button = $(this);
            const input = button.children(':checkbox');

            if(button.hasClass('ui-state-active'))
                button.addClass('ui-state-hover');
            else
                button.removeClass('ui-state-hover');
            
            input.trigger('focus').trigger('click');
        });
    }

    /**
     * Binds event handlers for input elements (shared for both layouts).
     * @private
     */
    bindInputEvents() {
        /* Keyboard support on inputs - shared for both layouts */
        this.inputs.on('focus', (e) => {
            const input = $(e.currentTarget);
            const button = this.getButtonForInput(input);
            if (button) {
                button.addClass('ui-state-focus');
            }
        })
        .on('blur', (e) => {
            const input = $(e.currentTarget);
            const button = this.getButtonForInput(input);
            if (button) {
                button.removeClass('ui-state-focus');
            }
        })
        .on('change', (e) => {
            const input = $(e.currentTarget);
            const button = this.getButtonForInput(input);
            if (button) {
                if(input.prop('checked'))
                    button.addClass('ui-state-active');
                else
                    button.removeClass('ui-state-active');
            }

            this.triggerChange();
        })
        .on('keydown', (e) => {
            if (PrimeFaces.utils.isActionKey(e)) {
                const input = $(e.currentTarget);
                
                // Toggle the checkbox state
                const isChecked = input.prop('checked');
                input.prop('checked', !isChecked).trigger('change');
                e.preventDefault();
            }
        })
        .on('click', (e) => {
            e.stopPropagation();
        });
    }

    /**
     * Gets the button element associated with the given input element.
     * @private
     * @param {JQuery} input The input element.
     * @return {JQuery|null} The associated button element, or null if not found.
     */
    getButtonForInput(input) {
        if (this.cfg.custom) {
            // In custom layout, find button by index
            const index = this.inputs.index(input);
            if (index >= 0 && index < this.buttons.length) {
                return this.buttons.eq(index);
            }
            return null;
        }
        else {
            // In regular layout, button is parent
            return input.parent();
        }
    }

    /**
     * Handles button interaction (click or keyboard action) for selecting/unselecting.
     * @private
     * @param {HTMLElement} target The button element that was interacted with.
     * @param {Event} e The event object.
     */
    handleButtonInteraction(target, e) {
        const button = $(target);
        const isActive = button.hasClass('ui-state-active');

        if(isActive) {
            this.unselect(button);
        }
        else {
            this.select(button);
        }
    }

    /**
     * Selects the given button option.
     * @param {JQuery} button A button of this widget to select.
     */
    select(button) {
        if (this.cfg.custom) {
            const index = this.buttons.index(button);
            if (index >= 0) {
                this.inputs.eq(index).prop('checked', true);
                button.addClass('ui-state-active');
            }
        }
        else {
            button.children(':checkbox').prop('checked', true);
        }

        this.triggerChange();
    }

    /**
     * Unselects the given button option.
     * @param {JQuery} button A button of this widget to unselect.
     */
    unselect(button) {
        if (this.cfg.custom) {
            const index = this.buttons.index(button);
            if (index >= 0) {
                this.inputs.eq(index).prop('checked', false);
                button.removeClass('ui-state-active ui-state-hover');
            }
        }
        else {
            button.children(':checkbox').prop('checked', false);
        }

        this.triggerChange();
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
                button.removeClass('ui-state-active');
                if (i < this.inputs.length) {
                    this.inputs.eq(i).prop('checked', false);
                }
            }
        }

        this.init(cfg);
    }

    /**
     * Enables this input so that the user can enter a value.
     * @param {number} [index] Index of the button option to enable. Enables all buttons if omitted.
     */
    enable(index) {
        if (index === null || index === undefined) {
            if (this.cfg.custom) {
                this.buttons.removeClass('ui-state-disabled').removeAttr('disabled');
                this.inputs.removeAttr('disabled');
            }
            else {
                PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
            }
            this.disabled = false;
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('ui-state-disabled').removeAttr('disabled');
            const input = this.inputs.eq(index);
            input.removeAttr('disabled');
        }
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     * @param {number} [index] Index of the button option to disable. Disables all buttons if omitted.
     */
    disable(index) {
        if (index === null || index === undefined) {
            if (this.cfg.custom) {
                this.buttons.removeClass('ui-state-hover ui-state-focus ui-state-active')
                        .addClass('ui-state-disabled').attr('disabled', 'disabled');
                this.inputs.attr('disabled', 'disabled');
            }
            else {
                PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
            }
            this.disabled = true;
        }
        else {
            const button = this.buttons.eq(index);
            button.removeClass('ui-state-hover ui-state-focus ui-state-active')
                    .addClass('ui-state-disabled').attr('disabled', 'disabled');
            const input = this.inputs.eq(index);
            input.attr('disabled', 'disabled');
        }
    }

}
