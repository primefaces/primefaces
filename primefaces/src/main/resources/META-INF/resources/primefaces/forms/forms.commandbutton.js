/**
 * __PrimeFaces CommandButton Widget__
 * 
 * CommandButton is an extended version of standard commandButton with AJAX and theming.
 * 
 * @interface {PrimeFaces.widget.CommandButtonCfg} cfg The configuration for the {@link  CommandButton| CommandButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
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

        if (this.cfg.disableOnAjax === true) {
            this.bindTriggers();
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
        var $this = this;

        $(document).on('pfAjaxSend.' + this.id, function(e, xhr, settings) {
            if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                $this.disable();
            }
        }).on('pfAjaxComplete.' + this.id, function(e, xhr, settings) {
            if (PrimeFaces.ajax.Utils.isXhrSource($this, settings)) {
                $this.enable();
            }
        });
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