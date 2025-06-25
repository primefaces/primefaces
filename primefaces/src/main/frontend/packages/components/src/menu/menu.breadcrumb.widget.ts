/**
 * The configuration for the {@link  BreadCrumb} widget.
 * 
 * You can access this configuration via {@link BreadCrumb.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface BreadCrumbCfg extends PrimeType.widget.BaseWidgetCfg {
    /**
     * Used to produce an Advanced SEO structure on the page. Default is false.
     */
    seo: boolean;
}

/**
 * __PrimeFaces BreadCrumb Widget__
 * 
 * BreadCrumb provides contextual information about the page hierarchy.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class BreadCrumb<Cfg extends BreadCrumbCfg = BreadCrumbCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    seoScript: JQuery | undefined = undefined;

    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        if (this.cfg.seo) {
            this.seoScript = $(this.jqId + '_seo');
        }
    }

    override refresh(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        if (this.seoScript) {
            this.seoScript.remove();
        }

        super.refresh(cfg);
    }
}
