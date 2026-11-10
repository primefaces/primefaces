/**
 * The configuration for the {@link Fieldset} widget.
 */
export interface FieldsetCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the content can be toggled between expanded and collapsed. */
    toggleable: boolean;
    /** Whether the fieldset is currently collapsed, updated on each toggle. */
    collapsed: boolean;
    /** Toggle duration in milliseconds. */
    toggleSpeed: number;
    /** `true` to load the content via AJAX when the fieldset is expanded the first time. */
    dynamic: boolean;
}

/**
 * __PrimeFaces Fieldset Widget__
 *
 * Fieldset is a grouping component as an extension to html fieldset.
 */
export class Fieldset<Cfg extends FieldsetCfg = FieldsetCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The legend of this fieldset. */
    legend: JQuery = $();
    /** The toggler icon, only when toggleable. */
    toggler?: JQuery;
    /** The content of this fieldset, only when toggleable. */
    content?: JQuery;
    /** The hidden input with the collapsed state, only when toggleable. */
    stateHolder?: JQuery;
    /** With dynamic loading, whether the content was already loaded. */
    loaded: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.legend = this.jq.children('.p-fieldset-legend');

        if (this.cfg.toggleable) {
            this.content = this.jq.children('.p-fieldset-content');
            this.toggler = this.legend.children('.p-fieldset-toggler');
            this.stateHolder = $(this.jqId + '_collapsed');

            this.legend.on('click.fieldset', (e) => {
                this.toggle(e);
            })
            .on('mouseover.fieldset mouseout.fieldset', () => {
                this.legend.toggleClass('p-state-hover');
            })
            .on('mousedown.fieldset mouseup.fieldset', () => {
                this.legend.toggleClass('p-state-active');
            })
            .on('focus.fieldset blur.fieldset', () => {
                this.legend.toggleClass('p-state-focus');
            })
            .on('keydown.fieldset', (e) => {
                if (e.key === 'Enter') {
                    this.toggle(e);
                    e.preventDefault();
                    e.stopPropagation();
                }
            });
        }
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.refresh(cfg);

        this.loaded = false;
    }

    /**
     * Toggles the content of this fieldset (collapsed or expanded).
     * @param _e Optional event that triggered the toggling, unused.
     */
    toggle(_e?: JQuery.TriggeredEvent): void {
        const content = this.content;
        if (!content) {
            // not toggleable
            return;
        }

        const collapsed = !!this.cfg.collapsed;

        if (!this.loaded && this.cfg.dynamic && collapsed) {
            this.loadContents();
        }
        else {
            this.updateToggleState(collapsed);
        }

        content.slideToggle(this.cfg.toggleSpeed as number, 'easeInOutCirc', () => {
            this.callBehavior('toggle');
        });

        PrimeFaces.invokeDeferredRenders(this.id);
    }

    /**
     * Updates the toggler state and saves it.
     * @param collapsed Whether this fieldset was collapsed before the toggle.
     */
    private updateToggleState(collapsed: boolean): void {
        this.legend.attr('aria-expanded', String(collapsed));

        this.cfg.collapsed = !collapsed;

        this.stateHolder?.val(String(!collapsed));
    }

    private loadContents(): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: this.getId(),
            update: this.getId(),
            ignoreAutoUpdate: true,
            params: [
                { name: this.getId() + '_contentLoad', value: true }
            ],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.content?.html(content);
                        this.loaded = true;
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.updateToggleState(!!this.cfg.collapsed);
            }
        };

        PrimeFaces.ajax.Request.handle(options);
    }
}
