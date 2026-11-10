/**
 * The configuration for the {@link Lifecycle} widget.
 */
export interface LifecycleCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
}

/**
 * __PrimeFaces Lifecycle Widget__
 *
 * Lifecycle is a utility component which displays the execution time of each Faces phase. It also synchronizes
 * automatically after each AJAX request.
 */
export class Lifecycle<Cfg extends LifecycleCfg = LifecycleCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether this widget was initialized. */
    initialized: boolean = false;
    /** Whether the Faces AJAX event listener was registered already. */
    facesListenerAdded?: boolean;
    /** Whether the lifecycle display is currently being updated. */
    updating: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        if (!this.initialized) {
            this.initialized = true;
            this.updating = false;

            this.update();
        }

        $(document).off('pfAjaxSuccess.'+this.id).on('pfAjaxSuccess.'+this.id, () => {
            if (!this.updating) {
                PrimeFaces.queueTask(() => {
                    if (!this.updating) {
                        this.update();
                    }
                }, 25);
            }
        });
        this.addDestroyListener(() => {
            $(document).off('pfAjaxSuccess.'+this.id);
        });

        // faces listeners can't be removed, so register it only once for this widget instance which survives refreshes
        if (window.faces && window.faces.ajax && !this.facesListenerAdded) {
            this.facesListenerAdded = true;
            faces.ajax.addOnEvent((data) => {
                if (data.status === 'success') {
                    if (!this.updating) {
                        PrimeFaces.queueTask(() => {
                            if (!this.updating) {
                                this.update();
                            }
                        }, 25);
                    }
                }
            });
        }
    }

    /**
     * Updates this lifecycle widget after an AJAX request.
     */
    private update(): void {
        this.updating = true;

        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            ignoreAutoUpdate: true,
            formId: this.cfg.formId,
            params: [{ name: this.getId() + '_getlifecycleinfo', value: true }],
            oncomplete: () => {
                this.updating = false;
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }
}
