/**
 * __PrimeFaces LinkButton Widget__
 * 
 * LinkButton a simple link, which is styled as a button and integrated with JSF navigation model.
 * 
 * @interface {PrimeFaces.widget.LinkButtonCfg} cfg The configuration for the {@link  LinkButton| LinkButton widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 */
PrimeFaces.widget.LinkButton = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg, this>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        PrimeFaces.skinButton(this.jq);
    }

});