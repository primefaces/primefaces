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
 * @prop {JQuery} originalInputs The DOM elements for the hidden radio input fields storing the value of this widget.
 * @prop {JQuery} buttons The DOM element for the button options the user can select.
 * @prop {JQuery} inputs The DOM element for the hidden input fields storing the value of this widget. 
 * 
 * @interface {PrimeFaces.widget.SelectOneButtonCfg} cfg The configuration for the {@link  SelectOneButton| SelectOneButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
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
        var $this = this;
        this.cfg.unselectable = this.cfg.unselectable !== false;

        //custom layout
        if(this.cfg.custom) {
            this.facet = this.jq.attr('role') === 'radiogroup';
            this.originalInputs = this.jq.find((this.facet ? '.ui-helper-hidden ' : '') + ':radio');
            // For facet layout, buttons are within jq. For referenced layout, search in parent container
            if (this.facet) {
                this.buttons = this.jq.find('[role="radio"]');
            }
            else {
                this.buttons = this.jq.parent().find('[role="radio"]');
            }
            this.inputs = this.originalInputs;

            //update button state
            for(var i = 0; i < this.originalInputs.length; i++) {
                var original = this.originalInputs.eq(i);

                if(original.is(':checked')) {
                    this.buttons.eq(i).addClass('ui-state-active');
                }

                if(original.is(':disabled')) {
                    this.disable(i);
                }
            }

            //pfs metadata
            this.originalInputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }
        //regular layout
        else {
            this.buttons = this.jq.children('div:not(.ui-state-disabled)');
            this.inputs = this.jq.find(':radio:not(:disabled)');
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
        var $this = this;

        this.buttons.filter(':not(.ui-state-disabled)').off('mouseover.selectOneButton mouseout.selectOneButton click.selectOneButton')
        .on('mouseover.selectOneButton', function() {
            var button = $(this);
            button.addClass('ui-state-hover');
        })
        .on('mouseout.selectOneButton', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click.selectOneButton', function(e) {
            var button = $(this);
            var index = $this.buttons.index(button);
            var isActive = button.hasClass('ui-state-active');

            if(isActive) {
                $this.unselect(button);
            }
            else {
                $this.select(button);
            }

            e.stopPropagation();
            e.preventDefault();
        });

        /* For keyboard accessibility */
        this.buttons.off('focus.selectOneButton blur.selectOneButton keydown.selectOneButton')
        .on('focus.selectOneButton', function(){
            var button = $(this);
            button.addClass('ui-state-focus');
        })
        .on('blur.selectOneButton', function(){
            var button = $(this);
            button.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneButton', function(e) {
            if (PrimeFaces.utils.isActionKey(e)) {
                var button = $(this);
                var isActive = button.hasClass('ui-state-active');

                if(isActive) {
                    $this.unselect(button);
                }
                else {
                    $this.select(button);
                }
                e.preventDefault();
            }
        });
    }

    /**
     * Selects the given button option. If another button option is selected already, it will be unselected.
     * @param {JQuery} button A button of this widget to select.
     */
    select(button) {
        this.buttons.filter('.ui-state-active').removeClass('ui-state-active ui-state-hover');
        
        if (this.cfg.custom) {
            var index = this.buttons.index(button);
            if (index >= 0) {
                this.originalInputs.prop('checked', false);
                this.originalInputs.eq(index).prop('checked', true);
            }
        }
        else {
            this.buttons.filter('.ui-state-active').find(':radio').prop('checked', false);
            var radio = button.find(':radio');
            radio.prop('checked', true);
        }

        button.addClass('ui-state-active');

        this.triggerChange();
    }

    /**
     * Unselects the given button option.
     * @param {JQuery} button A button of this widget to unselect.
     */
    unselect(button) {
        if(this.cfg.unselectable) {
            button.removeClass('ui-state-active ui-state-hover');

            if (this.cfg.custom) {
                var index = this.buttons.index(button);
                if (index >= 0) {
                    this.originalInputs.eq(index).prop('checked', false);
                }
            }
            else {
                var radio = button.find(':radio');
                radio.prop('checked', false).trigger('change');
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
            for(var i = 0; i < this.buttons.length; i++) {
                var button = this.buttons.eq(i);
                this.enable(i);
                button.removeClass('ui-state-active');
                if (i < this.originalInputs.length) {
                    this.originalInputs.eq(i).prop('checked', false);
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
            this.buttons.removeClass('ui-state-hover ui-state-focus ui-state-active')
                    .addClass('ui-state-disabled').attr('disabled', 'disabled');
            this.inputs.attr('disabled', 'disabled');
            if (this.cfg.custom) {
                this.originalInputs.attr('disabled', 'disabled');
            }
        }
        else {
            var button = this.buttons.eq(index);
            button.removeClass('ui-state-hover ui-state-focus ui-state-active')
                    .addClass('ui-state-disabled').attr('disabled', 'disabled');
            var input = this.inputs.eq(index);
            input.attr('disabled', 'disabled');
            if (this.cfg.custom) {
                this.originalInputs.eq(index).attr('disabled', 'disabled');
            }
        }
    }

    /**
     * Enables this input components so that the user can select an option.
     * @param {number} [index] Index of the button option to enable. Enables all buttons if omitted.
     */
    enable(index) {
        if (index === null || index === undefined) {
            this.buttons.removeClass('ui-state-disabled').removeAttr('disabled');
            this.inputs.removeAttr('disabled');
            if (this.cfg.custom) {
                this.originalInputs.removeAttr('disabled');
            }
        }
        else {
            var button = this.buttons.eq(index);
            button.removeClass('ui-state-disabled').removeAttr('disabled');
            var input = this.inputs.eq(index);
            input.removeAttr('disabled');
            if (this.cfg.custom) {
                this.originalInputs.eq(index).removeAttr('disabled');
            }
        }
    }

}
