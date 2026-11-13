/**
 * The configuration for the {@link  BreadCrumb} component.
 * 
 * You can access this configuration via {@link BreadCrumb.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface BreadCrumbCfg extends PrimeFaces.component.BaseComponentCfg {
    /**
     * Used to produce an Advanced SEO structure on the page. Default is false.
     */
    seo: boolean;
}

/**
 * __PrimeFaces BreadCrumb Component__
 * 
 * BreadCrumb provides contextual information about the page hierarchy.
 * 
 * @typeParam Cfg Type of the configuration object.
 */
export class BreadCrumb<Cfg extends BreadCrumbCfg = BreadCrumbCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    seoScript: JQuery | undefined = undefined;

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        if (this.cfg.seo) {
            this.seoScript = $(this.jqId + '_seo');
        }
    }

    override refresh(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        if (this.seoScript) {
            this.seoScript.remove();
        }

        super.refresh(cfg);
    }
}
