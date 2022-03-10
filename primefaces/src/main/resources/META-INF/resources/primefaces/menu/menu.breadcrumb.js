/**
 * __PrimeFaces BreadCrumb Widget__
 * 
 * BreadCrumb provides contextual information about the page hierarchy.
 *
 * @prop {JQuery} seoScript The script element for the seo.
 * 
 * @interface {PrimeFaces.widget.BreadCrumbCfg} cfg The configuration for the {@link  BreadCrumb| BreadCrumb widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.seo Used to produce an Advanced SEO structure on the page. Default is false.
 */
PrimeFaces.widget.BreadCrumb = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if (this.cfg.seo) {
            this.seoScript = $(this.jqId + '_seo');
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        if (this.seoScript) {
            this.seoScript.remove();
        }
        
        this._super(cfg);
    }
});
