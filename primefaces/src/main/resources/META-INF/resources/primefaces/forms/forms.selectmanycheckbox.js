/**
 * __PrimeFaces SelectManyCheckbox Widget__
 * 
 * SelectManyCheckbox is an extended version of the standard SelectManyCheckbox.
 * 
 * @prop {boolean} [disabled] `true` if this many select element is disabled, `false` if enabled, `undefined`
 * if the state is not known.
 * @prop {JQuery} enabledInputs The (cloned) DOM elements for the non-disabled hidden input fields of type checkbox
 * storing the value of this widget. 
 * @prop {JQuery} inputs The (cloned) DOM elements for the hidden input fields of type checkbox storing the value of
 * this widget.
 * @prop {JQuery} originalInputs The DOM elements for the hidden input fields of type checkbox storing the value of
 * this widget.
 * @prop {JQuery} outputs The DOM elements for the checkbox icons shown on the UI.
 * 
 * @interface {PrimeFaces.widget.SelectManyCheckboxCfg} cfg The configuration for the {@link  SelectManyCheckbox| SelectManyCheckbox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.custom Whether a custom HTML snippet needs to be used for the individual select items.
 */
PrimeFaces.widget.SelectManyCheckbox = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if(this.cfg.custom) {
            this.originalInputs = this.jq.find(':checkbox');
            this.inputs = $('input:checkbox[name="' + this.id + '"].ui-chkbox-clone');
            this.outputs = this.inputs.parent().next('.ui-chkbox-box');

            //update checkbox state
            for(var i = 0; i < this.inputs.length; i++) {
                var input = this.inputs.eq(i),
                itemindex = input.data('itemindex'),
                original = this.originalInputs.eq(itemindex);

                input.val(original.val());

                if (original.is(':checked')) {
                    var checkbox = input.prop('checked', true).parent().next();
                    this.check(input, checkbox);
                }
            }
        }
        else {
            this.outputs = this.jq.find('.ui-chkbox-box:not(.ui-state-disabled)');
            this.inputs = this.jq.find(':checkbox:not(:disabled)');
        }

        this.enabledInputs = this.inputs.filter(':not(:disabled)');

        this.bindEvents();

        //pfs metadata
        this.inputs.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;
    
        this.outputs.filter(':not(.ui-state-disabled)').on('mouseenter', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseleave', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');

            input.trigger('click').trigger('focus');

            if($.browser.msie && parseInt($.browser.version) < 9) {
                input.trigger('change');
            }
        });

        //delegate focus-blur-change states
        this.enabledInputs.on('focus', function() {
            var input = $(this),
            checkbox = input.parent().next();

            checkbox.addClass('ui-state-focus');
        })
        .on('blur', function() {
            var input = $(this),
            checkbox = input.parent().next();

            checkbox.removeClass('ui-state-focus');
        })
        .on('change', function(e) {
            var input = $(this),
            checkbox = input.parent().next(),
            disabled = input.is(':disabled');

            if(disabled) {
                return;
            }

            if(input.is(':checked')) {
                $this.check(input, checkbox);
            }
            else {
                $this.uncheck(input, checkbox);
            }
        });
    },

    /**
     * Checks the given checkbox and associated input.
     * @private
     * @param {JQuery} input the input.
     * @param {JQuery} checkbox the checbkox.
     */
    check: function(input, checkbox) {
        checkbox.addClass('ui-state-active');
        checkbox.children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        input.attr('aria-checked', true);
        input.prop('checked', true);
    },

    /**
     * Unchecks the given checkbox and associated input.
     * @private
     * @param {JQuery} input the input.
     * @param {JQuery} checkbox the checbkox.
     */
    uncheck: function(input, checkbox) {
        checkbox.removeClass('ui-state-active');
        checkbox.children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        input.attr('aria-checked', false);
        input.prop('checked', false);
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq, this.inputs);
        this.disabled = false;
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq, this.inputs);
        this.disabled = true;
    },

    /**
     * Check all available options.
     * @param {boolean} silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    checkAll: function(silent) {
        var $this = this;
        this.outputs.filter(':not(.ui-state-disabled)').each(function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');

            if (!input.is(':checked')) {
                $this.check(input, checkbox);
                if(!silent) {
                    this.input.trigger('change');
                }
            }
        });
    },

    /**
     * Uncheck all available options.
     * @param {boolean} silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    uncheckAll: function(silent) {
        var $this = this;
        this.outputs.filter(':not(.ui-state-disabled)').each(function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');

            if (input.is(':checked')) {
                $this.uncheck(input, checkbox);
                if(!silent) {
                    this.input.trigger('change');
                }
            }
        });
    },

    /**
     * Resets the input.
     * @param {boolean} silent `true` to suppress triggering event listeners, or `false` otherwise.
     */
    resetValue: function(silent) {
        this.uncheckAll(silent);
    }
});
