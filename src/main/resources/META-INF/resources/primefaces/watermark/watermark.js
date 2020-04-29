/**
 * __PrimeFaces Watermark Widget__
 * 
 * Watermark displays a hint on an input field.
 * 
 * @prop {JQuery} target The DOM element for the target component of this watermark.
 * 
 * @interface {PrimeFaces.widget.WatermarkCfg} cfg The configuration for the {@link  Watermark| Watermark widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.target Search expression for the target component of the watermark.
 * @prop {string} cfg.value The text of the watermark.
 */
PrimeFaces.widget.Watermark = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);

        if(this.target.is(':not(:input)')) {
            this.target = this.target.find(':input');
        }

        this.target.attr('placeholder', this.cfg.value);
    }

});