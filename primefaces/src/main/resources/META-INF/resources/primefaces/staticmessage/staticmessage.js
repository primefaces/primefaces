/**
 * __PrimeFaces StaticMessage Widget__
 * 
 * StaticMessage widget to handle events.
 *
 * @interface {PrimeFaces.widget.StaticMessageCfg} cfg The configuration for the {@link StaticMessage| StaticMessage widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.StaticMessage = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.bindEvents();
    },

    /**
     * Bind behavior events.
     */
    bindEvents: function() {
        var $this = this;
        var closer = $('.ui-messages-close', this.jq);

        closer.on('click.staticmessage', function(e) {
            if ($this.hasBehavior('close')) {
                $this.callBehavior('close');
            }
        });
        PrimeFaces.skinCloseAction(closer);
    }

});