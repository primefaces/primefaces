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
PrimeFaces.widget.SelectBooleanCheckbox = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.box = this.jq.find('.ui-chkbox-box');
        this.icon = this.box.children('.ui-chkbox-icon');
        this.itemLabel = this.jq.find('.ui-chkbox-label');
        this.disabled = this.input.is(':disabled');

        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.box.on('mouseenter.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-hover');
            })
            .on('mouseleave.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-hover');
            })
            .on('click.selectBooleanCheckbox', function() {
                $this.input.trigger('click').trigger('focus.selectBooleanCheckbox');
            });

            this.input.on('focus.selectBooleanCheckbox', function() {
                $this.box.addClass('ui-state-focus');
            })
            .on('blur.selectBooleanCheckbox', function() {
                $this.box.removeClass('ui-state-focus');
            })
            .on('change.selectBooleanCheckbox', function(e) {
                if($this.isChecked()) {
                    $this.input.prop('checked', true).attr('aria-checked', true);
                    $this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
                }
                else {
                    $this.input.prop('checked', false).attr('aria-checked', false);
                    $this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
                }
            });

            //toggle state on label click
            this.itemLabel.on("click", function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Checks this checkbox if it is currently unchecked, or unchecks it otherwise.
     */
    toggle: function() {
        if(this.isChecked())
            this.uncheck();
        else
            this.check();
    },

    /**
     * Checks whether this checkbox is currently checked.
     * @return {boolean} `true` if this checkbox is checked, or `false` otherwise.
     */
    isChecked: function() {
        return this.input.prop('checked');
    },

    /**
     * Checks this checkbox, if it is not checked already .
     */
    check: function() {
        if(!this.isChecked()) {
            this.input.prop('checked', true).trigger('change');
            this.input.attr('aria-checked', true);
            this.box.addClass('ui-state-active').children('.ui-chkbox-icon').removeClass('ui-icon-blank').addClass('ui-icon-check');
        }
    },

    /**
     * Unchecks this checkbox, if it is not unchecked already .
     */
    uncheck: function() {
        if(this.isChecked()) {
            this.input.prop('checked', false).trigger('change');
            this.input.attr('aria-checked', false);
            this.box.removeClass('ui-state-active').children('.ui-chkbox-icon').addClass('ui-icon-blank').removeClass('ui-icon-check');
        }
    }

});
