/**
 * __PrimeFaces SelectBooleanButton Widget__
 * 
 * SelectBooleanButton is used to select a binary decision with a toggle button.
 * 
 * @prop {JQuery} icon The DOM element for the icon with the button.
 * @prop {JQuery} input The DOM element for the hidden input field storing the value of this widget.
 * @prop {boolean} disabled Whether this button is disabled.
 * @prop {string} onLabel Calculated On label value either set by user or locale.
 * @prop {string} offLabel Calculated Off label value either set by user or locale.
 * 
 * @interface {PrimeFaces.widget.SelectBooleanButtonCfg} cfg The configuration for the {@link  SelectBooleanButton| SelectBooleanButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.onLabel Label to display when button is selected.
 * @prop {string} cfg.onIcon Icon to display when button is selected.
 * @prop {string} cfg.offLabel Label to display when button is unselected.
 * @prop {string} cfg.offIcon Icon to display when button is unselected.
 */
PrimeFaces.widget.SelectBooleanButton = class SelectBooleanButton extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.input = $(this.jqId + '_input');
        this.disabled = this.input.is(':disabled');
        this.icon = this.jq.children('.p-button-icon-left');
        this.onLabel = this.cfg.onLabel || this.getAriaLabel('switch.ON');
        this.offLabel = this.cfg.offLabel || this.getAriaLabel('switch.OFF');
        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.jq.on('mouseover', function() {
                if(!$this.jq.hasClass('p-state-active')) {
                    $this.jq.addClass('p-state-hover');
                }
            }).on('mouseout', function() {
                $this.jq.removeClass('p-state-hover');
            })
            .on('click', function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }
        
        if (this.input.prop('checked')) {
            this.input.attr('aria-label', this.onLabel);
            this.jq.children('.p-button-text').text(this.onLabel);
        }
        else {
            this.input.attr('aria-label', this.offLabel);
            this.jq.children('.p-button-text').text(this.offLabel);
        }

        this.input.on('focus', function() {
            $this.jq.addClass('p-state-focus');
        })
        .on('blur', function() {
            $this.jq.removeClass('p-state-focus');
        })
        .on('keydown', function(e) {
            if(e.key === ' ') {
                e.preventDefault();
            }
        })
        .on('keyup', function(e) {
            if(e.key === ' ') {
                $this.toggle();

                e.preventDefault();
            }
        });

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    }

    /**
     * Toggles the state of this button, i.e. turning it on if it is off and vice-versa. Corresponds to checking or
     * unchecking the underlying checkbox.
     */
    toggle() {
        if(!this.disabled) {
            if(this.input.prop('checked'))
                this.uncheck();
            else
                this.check();
        }
    }

    /**
     * Turns this button to its on state, which corresponds to checking the underlying checkbox.
     */
    check() {
        if (!this.disabled) {
            this.input.prop('checked', true);
            this.input.attr('aria-label', this.onLabel);
            this.jq.addClass('p-state-active').children('.p-button-text').text(this.onLabel);

            if (this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
            }

            this.input.trigger('change');
        }
    }

    /**
     * Turns this button to its off state, which corresponds to unchecking the underlying checkbox.
     */
    uncheck() {
        if (!this.disabled) {
            this.input.prop('checked', false);
            this.input.attr('aria-label', this.offLabel);
            this.jq.removeClass('p-state-active').children('.p-button-text').text(this.offLabel);

            if (this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
            }

            this.input.trigger('change');
        }
    }

    /**
     * Enables this input so that the user can enter a value.
     */
    enable() {
        PrimeFaces.utils.enableInputWidget(this.jq);
        this.disabled = false;
    }

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable() {
        PrimeFaces.utils.disableInputWidget(this.jq);
        this.disabled = true;
    }

}
