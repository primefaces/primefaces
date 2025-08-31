/**
 * __PrimeFaces SelectOneRadio Widget__
 *
 * SelectOneRadio is an extended version of the standard SelectOneRadio with theme integration.
 *
 * @prop {boolean} facet Whether custom is used with a facet.
 * @prop {JQuery} originalInputs The DOM elements for the hidden radio input fields of type checkbox storing the value
 * of this widget.
 * @prop {JQuery} enabledInputs The (cloned) DOM elements for the non-disabled hidden input fields of type radio storing
 * the value of this widget.
 * @prop {JQuery} inputs The (cloned) DOM elements for the hidden input fields of type radio storing the value of this
 * widget.
 * @prop {JQuery} outputs The DOM elements for the radio icons shown on the UI.
 * @prop {JQuery} checkedRadio The DOM elements for the active radio icons shown on the UI .
 * @prop {JQuery} labels The DOM elements for the label texts of each radio button.
 *
 * @interface {PrimeFaces.widget.SelectOneRadioCfg} cfg The configuration for the {@link  SelectOneRadio| SelectOneRadio widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.custom Whether a custom layout is enabled.
 * @prop {boolean} cfg.unselectable Unselectable mode when true clicking a radio again will clear the selection.
 * @prop {boolean} cfg.readonly Whether the radio group is readonly.
 */
PrimeFaces.widget.SelectOneRadio = class SelectOneRadio extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        var $this = this;
        this.cfg.readonly = this.cfg.readonly === true;

        //custom layout
        if(this.cfg.custom) {
            this.facet = this.jq.attr('role') === 'radiogroup';
            this.originalInputs = this.jq.find((this.facet ? '.ui-helper-hidden ' : '') + ':radio');
            this.inputs = $('input:radio[name="' + this.id + '_clone"].ui-radio-clone');
            this.outputs = this.inputs.parent().next('.ui-radiobutton-box');
            this.labels = $();

            //labels
            var base = this.facet ? this.inputs : this.outputs;
            for(var j=0; j < base.length; j++) {
                this.labels = this.labels.add('label[for="' +
                    (this.facet ? base.eq(j).attr('id') : base.eq(j).prev().find('.ui-radio-clone').attr('id')) + '"]');
            }

            //update radio state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);

                input.val(original.val());

                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-radiobutton-icon')
                            .addClass('ui-icon-bullet').removeClass('ui-icon-blank');
                }
                this.setAriaChecked(input, original.is(':checked'));

                if(original.is(':disabled')) {
                    this.disable(i);
                }
            }

            //pfs metadata
            this.originalInputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }
        //regular layout
        else {
            this.outputs = this.jq.find('.ui-radiobutton-box');
            this.inputs = this.jq.find(':radio');
            this.labels = this.jq.find('label');

            this.inputs.each(function(){
                $this.setAriaChecked($(this), this.checked);
            });

            //pfs metadata
            this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');
        this.checkedRadio = this.outputs.filter('.ui-state-active');

        this.bindEvents();
    }

    /**
     * Sets aria-checked attribute.
     * @param {JQuery} input of which to set aria-checked attribute.
     * @param {boolean} checked state to set.
     * @private
     */
    setAriaChecked(input, checked) {
        input.closest('[role=radio]').attr('aria-checked', checked);
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        if(this.cfg.custom) {
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i);

                this.enable(i);
                input.prop('checked', false).parent().next().removeClass('ui-state-active').children('.ui-radiobutton-icon')
                            .removeClass('ui-icon-bullet').addClass('ui-icon-blank');
                this.setAriaChecked(input, false);
            }
        }

        this.init(cfg);
    }

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents() {
        var $this = this;

        if (!this.cfg.readonly) {
            this.outputs.filter(':not(.ui-state-disabled)').off('mouseenter.selectOneRadio mouseleave.selectOneRadio click.selectOneRadio')
            .on('mouseenter.selectOneRadio', function() {
                $(this).addClass('ui-state-hover');
            })
            .on('mouseleave.selectOneRadio', function() {
                $(this).removeClass('ui-state-hover');
            })
            .on('click.selectOneRadio', function(e) {
                var radio = $(this),
                input = radio.prev().children(':radio');

                var isSelectableRadio = $this.cfg.unselectable || !$this.checkedRadio || !$this.checkedRadio.is(radio);

                // configure ARIA
                $this.jq.find('[role=radio]').attr('aria-checked', false);
                $this.setAriaChecked(radio, true);

                // configure UI
                if(!radio.hasClass('ui-state-active') && isSelectableRadio) {
                    $this.unselect($this.checkedRadio);
                    $this.select(radio);
                }
                else if ($this.cfg.unselectable) {
                    $this.unselect($this.checkedRadio);
                }

                // fire events only if the radio is different
                if (isSelectableRadio) {
                    $this.fireClickEvent(input, e);
                    input.trigger('change');
                    input.trigger('focus.selectOneRadio');
                }

                // Github issue #4467
                e.stopPropagation();
                e.preventDefault();
            });

            this.labels.filter(':not(.ui-state-disabled)').off('click.selectOneRadio')
            .on('click.selectOneRadio', function(e) {
                var target = $(PrimeFaces.escapeClientId($(this).attr('for'))),
                radio = null;

                //checks if target is input or not(custom labels)
                if(target.is(':input'))
                    radio = target.parent().next();
                else
                    radio = target.children('.ui-radiobutton-box'); //custom layout

                // #13546 only trigger radio if its not already active
                if (!radio.hasClass('ui-state-active')) {
                    radio.trigger('click.selectOneRadio');
                }

                e.preventDefault();
            });
        }

        this.enabledInputs.off('focus.selectOneRadio blur.selectOneRadio keydown.selectOneRadio')
        .on('focus.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            radio.addClass('ui-state-focus');
        })
        .on('blur.selectOneRadio', function() {
            var input = $(this),
            radio = input.parent().next();

            radio.removeClass('ui-state-focus');
        })
        .on('keydown.selectOneRadio', function(e) {
            if ($this.cfg.readonly) {
                return;
            }

            var input = $(this),
            currentRadio = input.parent().next(),
            index = $this.enabledInputs.index(input),
            size = $this.enabledInputs.length;

            switch(e.key) {
                case 'ArrowUp':
                case 'ArrowLeft':
                    var prevRadioInput = (index === 0) ? $this.enabledInputs.eq((size - 1)) : $this.enabledInputs.eq(--index),
                    prevRadio = prevRadioInput.parent().next();

                    input.trigger("blur");
                    $this.unselect(currentRadio);
                    $this.select(prevRadio);
                    prevRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case 'ArrowDown':
                case 'ArrowRight':
                    var nextRadioInput = (index === (size - 1)) ? $this.enabledInputs.eq(0) : $this.enabledInputs.eq(++index),
                    nextRadio = nextRadioInput.parent().next();

                    input.trigger("blur");
                    $this.unselect(currentRadio);
                    $this.select(nextRadio);
                    nextRadioInput.trigger('focus').trigger('change');
                    e.preventDefault();
                break;

                case ' ':
                    if(!input.prop('checked')) {
                        $this.select(currentRadio);
                        input.trigger('focus').trigger('change');
                    }
                    else if ($this.cfg.unselectable) {
                        $this.unselect(currentRadio);
                        input.trigger('focus').trigger('change');
                    }

                    e.preventDefault();
                break;
            }
        });
    }

    /**
     * Unselects the given radio button option.
     * @param {JQuery} radio A radio button of this widget to unselect.
     */
    unselect(radio) {
        if (this.cfg.readonly) {
            return;
        }

        var radioInput = radio.prev().children(':radio');
        radioInput.prop('checked', false);
        radio.removeClass('ui-state-active').children('.ui-radiobutton-icon').removeClass('ui-icon-bullet').addClass('ui-icon-blank');

        if (this.cfg.custom) {
            var itemindex = radioInput.data('itemindex');
            this.originalInputs.eq(itemindex).prop('checked', false);
        }
    }

    /**
     * Selects the given radio button option. If another radio button option is selected already, it will be unselected.
     * @param {JQuery} radio A radio button of this widget to select.
     */
    select(radio) {
        if (this.cfg.readonly) {
            return;
        }

        var radioInput = radio.prev().children(':radio');
        this.checkedRadio = radio;
        radio.addClass('ui-state-active').children('.ui-radiobutton-icon').addClass('ui-icon-bullet').removeClass('ui-icon-blank');
        radioInput.prop('checked', true);

        if (this.cfg.custom) {
            var itemindex = radioInput.data('itemindex');
            this.originalInputs.eq(itemindex).prop('checked', true);
        }
    }

    /**
     * Removes some of the event listeners added by `bindEvents`. Called when this widget is disabled.
     * @private
     * @param {JQuery} input Radio input element for which to remove the listeners.
     */
    unbindEvents(input) {
        if(input) {
            input.off();
            input.parent().nextAll('.ui-radiobutton-box').off();
            this.labels.filter("label[for='" + input.attr('id') + "']").off();
        }
        else {
            this.inputs.off();
            this.labels.off();
            this.outputs.off();
        }
    }

    /**
     * Disables a given radio button option of this widget.
     * @param {number} [index] Index of the radio button option to disable. Disables all radio buttons if omitted.
     */
    disable(index) {
        if (this.cfg.readonly) {
            return;
        }

        if (index === null || index === undefined) {
            this.inputs.attr('disabled', 'disabled');
            this.labels.addClass('ui-state-disabled');
            this.outputs.addClass('ui-state-disabled');
            this.unbindEvents();
        }
        else {
            var input = this.inputs.eq(index),
                label = this.labels.filter("label[for='" + input.attr('id') + "']");
            input.attr('disabled', 'disabled').parent().nextAll('.ui-radiobutton-box').addClass('ui-state-disabled');
            label.addClass('ui-state-disabled');
            this.unbindEvents(input);
        }
    }

    /**
     * Enables a given radio button option of this widget.
     * @param {number} [index] Index of the radio button option to enable. Enables all radio buttons if omitted.
     */
    enable(index) {
        if (this.cfg.readonly) {
            return;
        }

        if (index === null || index === undefined) {
            this.inputs.removeAttr('disabled');
            this.labels.removeClass('ui-state-disabled');
            this.outputs.removeClass('ui-state-disabled');
        }
        else {
            var input = this.inputs.eq(index),
                label = this.labels.filter("label[for='" + input.attr('id') + "']");
            input.removeAttr('disabled').parent().nextAll('.ui-radiobutton-box').removeClass('ui-state-disabled');
            label.removeClass('ui-state-disabled');
        }
        this.bindEvents();
    }

    /**
     * Calls the behavior for when a radio button options was clicked.
     * @private
     * @param {JQuery} input Radio button input that was clicked.
     * @param {JQuery.TriggeredEvent} event (Click) event that was triggered.
     */
    fireClickEvent(input, event) {
        var userOnClick = input.prop('onclick');
        if (userOnClick) {
            userOnClick.call(this, event);
        }
    }

}