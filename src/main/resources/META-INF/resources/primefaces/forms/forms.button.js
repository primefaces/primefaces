/**
 * __PrimeFaces Button Widget__
 * 
 * Button is an extension to the standard h:button component with skinning capabilities.
 * 
 * @interface {PrimeFaces.widget.ButtonCfg} cfg The configuration for the {@link  Button| Button widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.Button = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    },

    /**
     * Enables this button so that the user cannot press it.
     */
    disable: function() {
        this.jq.removeClass('ui-state-hover ui-state-focus ui-state-active')
                .addClass('ui-state-disabled').attr('disabled', 'disabled');
    },

    /**
     * Enables this button so that the user can press it.
     */
    enable: function() {
        this.jq.removeClass('ui-state-disabled').removeAttr('disabled');
    }

});
