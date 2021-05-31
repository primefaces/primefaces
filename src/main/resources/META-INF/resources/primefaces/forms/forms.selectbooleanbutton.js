/**
 * __PrimeFaces SelectBooleanButton Widget__
 * 
 * SelectBooleanButton is used to select a binary decision with a toggle button.
 * 
 * @prop {JQuery} icon The DOM element for the icon with the button.
 * @prop {JQuery} input The DOM element for the hidden input field storing the value of this widget.
 * @prop {boolean} disabled Whether this button is disabled.
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
PrimeFaces.widget.SelectBooleanButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.disabled = this.input.is(':disabled');
        this.icon = this.jq.children('.ui-button-icon-left');
        var $this = this;

        //bind events if not disabled
        if(!this.disabled) {
            this.jq.on('mouseover', function() {
                if(!$this.jq.hasClass('ui-state-active')) {
                    $this.jq.addClass('ui-state-hover');
                }
            }).on('mouseout', function() {
                $this.jq.removeClass('ui-state-hover');
            })
            .on('click', function() {
                $this.toggle();
                $this.input.trigger('focus');
            });
        }

        this.input.on('focus', function() {
            $this.jq.addClass('ui-state-focus');
        })
        .on('blur', function() {
            $this.jq.removeClass('ui-state-focus');
        })
        .on('keydown', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                e.preventDefault();
            }
        })
        .on('keyup', function(e) {
            var keyCode = $.ui.keyCode;
            if(e.which === keyCode.SPACE) {
                $this.toggle();

                e.preventDefault();
            }
        });

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Toggles the state of this button, i.e. turning it on if it is off and vice-versa. Corresponds to checking or
     * unchecking the underlying checkbox.
     */
    toggle: function() {
        if(!this.disabled) {
            if(this.input.prop('checked'))
                this.uncheck();
            else
                this.check();
        }
    },

    /**
     * Turns this button to its on state, which corresponds to checking the underlying checkbox.
     */
    check: function() {
        if(!this.disabled) {
            this.input.prop('checked', true);
            this.jq.addClass('ui-state-active').children('.ui-button-text').text(this.cfg.onLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.offIcon).addClass(this.cfg.onIcon);
            }

            this.input.trigger('change');
        }
    },

    /**
     * Turns this button to its off state, which corresponds to unchecking the underlying checkbox.
     */
    uncheck: function() {
        if(!this.disabled) {
            this.input.prop('checked', false);
            this.jq.removeClass('ui-state-active').children('.ui-button-text').text(this.cfg.offLabel);

            if(this.icon.length > 0) {
                this.icon.removeClass(this.cfg.onIcon).addClass(this.cfg.offIcon);
            }

            this.input.trigger('change');
        }
    },

    /**
     * Enables this input so that the user can enter a value.
     */
    enable: function() {
        PrimeFaces.utils.enableInputWidget(this.jq);
        this.disabled = false;
    },

    /**
     * Disables this input so that the user cannot enter a value anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableInputWidget(this.jq);
        this.disabled = true;
    }

});
