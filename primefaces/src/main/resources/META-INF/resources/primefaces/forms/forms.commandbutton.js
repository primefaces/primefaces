/**
 * __PrimeFaces CommandButton Widget__
 * 
 * CommandButton is an extended version of standard commandButton with AJAX and theming.
 *
 * @forcedProp {number} [ajaxCount] Number of concurrent active Ajax requests.
 *
 * @interface {PrimeFaces.widget.CommandButtonCfg} cfg The configuration for the {@link  CommandButton| CommandButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 @prop {boolean} cfg.validateClientDynamic When set to `true` this button is only enabled after successful client side validation, otherwise classic behaviour. Used together with p:clientValidator.
 */
PrimeFaces.widget.CommandButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);

        this.bindTriggers();

        if (cfg.validateClientDynamic) {
            let that = this;
            setTimeout( function() {
                PrimeFaces.validation.validateButtonCsvRequirements(that.jq[0]); // TODO: Which code runs when we click the button? Are there some possible synergies? Refactoring possible?
            }, 0 );

            PrimeFaces.validation.bindAjaxComplete(); // TODO: do this only once for the whole page not per CommandButton!
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        $(document).off('pfAjaxSend.' + this.id + ' pfAjaxComplete.' + this.id);

        this._super(cfg);
    },

    /**
     * Sets up the global event listeners on the button.
     * @private
     */
    bindTriggers: function() {
        PrimeFaces.bindButtonInlineAjaxStatus(this, this.jq);
    },

    /**
     * Disables this button so that the user cannot press the button anymore.
     */
    disable: function() {
        PrimeFaces.utils.disableButton(this.jq);
    },

    /**
     * Enables this button so that the user can press the button.
     */
    enable: function() {
        PrimeFaces.utils.enableButton(this.jq);
    }

});
