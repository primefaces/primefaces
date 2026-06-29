/**
 * PrimeFaces SelectManyCheckbox Widget
 */
PrimeFaces.widget.SelectManyCheckbox = PrimeFaces.widget.BaseWidget.extend({

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

                if(original.is(':checked')) {
                    input.prop('checked', true).parent().next().addClass('ui-state-active').children('.ui-chkbox-icon')
                            .addClass('ui-icon-check').removeClass('ui-icon-blank');
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

    bindEvents: function() {
        this.outputs.filter(':not(.ui-state-disabled)').on('mouseover', function() {
            $(this).addClass('ui-state-hover');
        })
        .on('mouseout', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('click', function() {
            var checkbox = $(this),
            input = checkbox.prev().children(':checkbox');

            input.trigger('click');

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
                checkbox.children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');

                checkbox.addClass('ui-state-active');
            }
            else {
                checkbox.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
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

        if (this.cfg.custom) {
            var itemindex = input.data('itemindex');
            this.originalInputs.eq(itemindex).prop('checked', true);
        }
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

        if (this.cfg.custom) {
            var itemindex = input.data('itemindex');
            this.originalInputs.eq(itemindex).prop('checked', false);
        }
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
