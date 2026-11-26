/**
 * When the content is loaded: `load` right after the component was rendered, `visible` once the component enters the
 * viewport, `manual` only via {@link Deferred.load}.
 */
export type DeferredMode = "load" | "visible" | "manual";

/**
 * The configuration for the {@link Deferred} component.
 */
export interface DeferredCfg extends PrimeFaces.component.BaseComponentCfg {
    /** When the content is loaded. */
    mode: DeferredMode;
    /** Whether the content was rendered by the server already. */
    loaded: boolean;
}

/**
 * __PrimeFaces Deferred Component__
 *
 * Deferred shows a placeholder and loads its content via AJAX. It is not related to the base class
 * {@link PrimeFaces.component.DeferredComponent}.
 */
export class Deferred<Cfg extends DeferredCfg = DeferredCfg> extends PrimeFaces.component.BaseComponent<Cfg> {

    /** Whether the load request was triggered and did not complete yet. */
    private loading = false;

    /** Observes the container in the `visible` mode until loading starts. */
    private observer: IntersectionObserver | null = null;

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.disconnect();
        this.addDestroyListener(() => this.disconnect());

        if (this.cfg.loaded) {
            return;
        }

        if (this.loading) {
            // updated by another request while the load request is pending, its response updates the new element
            this.jq.attr('aria-busy', 'true');
        }
        else if (this.cfg.mode === 'load') {
            this.load();
        }
        else if (this.cfg.mode === 'visible') {
            const container = this.jq.get(0);
            if (container) {
                this.observer = new IntersectionObserver((entries) => {
                    if (entries.some((entry) => entry.isIntersecting)) {
                        this.load();
                    }
                });
                this.observer.observe(container);
            }
        }
    }

    /**
     * Loads the content. Does nothing when the content is loaded already or the load request is running.
     */
    load(): void {
        if (this.cfg.loaded || this.loading) {
            return;
        }

        this.loading = true;
        this.disconnect();
        this.jq.attr('aria-busy', 'true');

        const options: PrimeFaces.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            async: false,
            ignoreAutoUpdate: true,
            global: false,
            params: [
                { name: this.getId() + '_load', value: true }
            ],
            oncomplete: () => {
                // on success the response replaced the container and the component was initialized with cfg.loaded,
                // in every other case the placeholder stays and the content can be loaded again
                this.loading = false;
                this.jq.removeAttr('aria-busy');
            }
        };

        if (this.hasBehavior('load')) {
            this.callBehavior('load', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Whether the content is loaded.
     * @returns `true` if the content was rendered by the server.
     */
    isLoaded(): boolean {
        return this.cfg.loaded === true;
    }

    private disconnect(): void {
        this.observer?.disconnect();
        this.observer = null;
    }
}
