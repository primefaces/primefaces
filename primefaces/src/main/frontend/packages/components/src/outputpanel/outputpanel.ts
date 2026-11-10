/**
 * The configuration for the {@link OutputPanel} widget.
 */
export interface OutputPanelCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the content is loaded via AJAX after the page was loaded. */
    deferred: boolean;
    /** When the deferred content is loaded. */
    deferredMode: PrimeFacesTypes.widget.OutputPanel.DeferredMode;
    /** Normalized to a boolean on init, not used: the load request is never global. */
    global: boolean;
}

/**
 * __PrimeFaces OutputPanel Widget__
 *
 * OutputPanel is a panel component with the ability for deferred loading.
 */
export class OutputPanel<Cfg extends OutputPanelCfg = OutputPanelCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.global = this.cfg.global || false;

        if (this.cfg.deferred) {
            if (this.cfg.deferredMode === 'load') {
                this.loadContent();
            }
            else if (this.cfg.deferredMode === 'visible') {
                if (this.visible()) {
                    this.loadContent();
                }
                else {
                    this.bindScrollMonitor();
                }
            }
        }
    }

    private loadContent(): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            async: false,
            ignoreAutoUpdate: true,
            global: false,
            params: [
                { name: this.getId() + '_load', value: true }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.jq.html(content);
                    }
                });

                return true;
            },
            onerror: () => {
                this.jq.html('');
            }
        };

        if (this.hasBehavior('load')) {
            this.callBehavior('load', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    private bindScrollMonitor(): void {
        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.getId() + '_align', () => {
            if (this.visible()) {
                PrimeFaces.utils.unbindScrollHandler(this, 'scroll.' + this.getId() + '_align');
                this.loadContent();
            }
        });
    }

    /**
     * Checks whether this panel is within the visible area of the window.
     * @returns `true` if the top or the bottom edge is in the visible area.
     */
    visible(): boolean {
        const win = $(window);
        const scrollTop = win.scrollTop() ?? 0;
        const height = win.height() ?? 0;
        const top = this.jq.offset()?.top ?? 0;
        const bottom = top + (this.jq.innerHeight() ?? 0);

        return (top >= scrollTop && top <= (scrollTop + height)) || (bottom >= scrollTop && bottom <= (scrollTop + height));
    }
}
