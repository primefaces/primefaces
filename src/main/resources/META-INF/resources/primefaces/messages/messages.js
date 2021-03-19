/**
 * __PrimeFaces Message Widget__
 * 
 * Message is a pre-skinned extended version of the standard JSF message component.
 * 
 * @interface {PrimeFaces.widget.MessageCfg} cfg The configuration for the {@link  Message| Message widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.target Client ID of the target for which to show this message.
 */
PrimeFaces.widget.Message = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        var text = this.jq.find('.ui-message-error-detail').text();

        if(text) {
           var target = $(PrimeFaces.escapeClientId(this.cfg.target));

           if (this.cfg.tooltip) {
              target.data('tooltip', text);
           }

           target.attr('aria-describedby', this.id + '_error-detail');
        }
   }
});