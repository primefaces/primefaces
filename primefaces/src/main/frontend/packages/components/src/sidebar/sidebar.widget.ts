/**
 * The configuration for the {@link Sidebar} widget.
 */
export interface SidebarCfg extends PrimeFacesTypes.widget.DynamicOverlayWidgetCfg {
    /** Whether the sidebar is modal, normalized to a boolean on init (default `true`). */
    modal: boolean;
    /** Whether the close icon is displayed, normalized to a boolean on init (default `true`). */
    showCloseIcon: boolean;
    /** Base z-index of the sidebar. */
    baseZIndex: number;
    /** Called when the sidebar was hidden. */
    onHide: PrimeFacesTypes.widget.Sidebar.OnHideCallback;
    /** Called when the sidebar was shown. */
    onShow: PrimeFacesTypes.widget.Sidebar.OnShowCallback;
    /** Whether the sidebar is shown initially. */
    visible: boolean;
    /** `true` to load the content via AJAX when the sidebar is shown the first time. */
    dynamic: boolean;
}

/**
 * __PrimeFaces Sidebar Widget__
 *
 * Sidebar is a panel component displayed as an overlay at the edges of the screen.
 */
export class Sidebar<Cfg extends SidebarCfg = SidebarCfg> extends PrimeFaces.widget.DynamicOverlayWidget<Cfg> {
    /** The container of the content. */
    content: JQuery = $();
    /** The close icon, only with `showCloseIcon`. */
    closeIcon?: JQuery;
    /** With dynamic loading, whether the content was already loaded. */
    loaded: boolean = false;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.cfg.modal = (this.cfg.modal === true || this.cfg.modal === undefined);
        this.cfg.showCloseIcon = (this.cfg.showCloseIcon === true || this.cfg.showCloseIcon === undefined);
        this.cfg.baseZIndex = this.cfg.baseZIndex || 0;

        this.content = this.jq.children('.p-sidebar-content');

        if (this.cfg.showCloseIcon) {
            this.closeIcon = this.jq.children('.p-sidebar-close');
        }

        this.applyARIA();

        if (this.cfg.visible) {
            this.show();
        }

        this.bindEvents();
    }

    override refresh(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.refresh(cfg);

        this.loaded = false;
    }

    private bindEvents(): void {
        if (this.cfg.showCloseIcon && this.closeIcon) {
            this.closeIcon.off('mouseover mouseout focus blur click').on('mouseover', (e) => {
                $(e.currentTarget).addClass('p-state-hover');
            }).on('mouseout', (e) => {
                $(e.currentTarget).removeClass('p-state-hover');
            }).on('focus', (e) => {
                $(e.currentTarget).addClass('p-state-focus');
            }).on('blur', (e) => {
                $(e.currentTarget).removeClass('p-state-focus');
            }).on('click', (e) => {
                this.hide();
                e.preventDefault();
            });
        }
    }

    /**
     * Shows this sidebar, if hidden.
     * @param reload With dynamic loading, `true` to load the content again.
     */
    show(reload: boolean = false): void {
        if (this.isVisible()) {
            return;
        }

        if ((!this.loaded || reload === true) && this.cfg.dynamic) {
            this.loadContents();
        }
        else {
            this._show();
        }
    }

    private _show(): void {
        this.jq.addClass('p-sidebar-active');
        this.jq.attr({
            'aria-hidden': 'false',
            'aria-modal': String(this.cfg.modal)
        });
        this.jq.css('z-index', String((this.cfg.baseZIndex ?? 0) + (++PrimeFaces.zindex)));

        this.postShow();

        if (this.cfg.modal) {
            this.enableModality();
        }
    }

    private postShow(): void {
        this.callBehavior('open');

        PrimeFaces.invokeDeferredRenders(this.id);

        // execute user defined callback
        if (this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    }

    /**
     * Hides this sidebar, if shown.
     */
    hide(): void {
        if (!this.isVisible()) {
            return;
        }

        this.jq.removeClass('p-sidebar-active');
        this.jq.attr({
            'aria-hidden': 'true',
            'aria-modal': 'false'
        });
        this.onHide();

        if (this.cfg.modal) {
            this.disableModality();
        }
    }

    /**
     * @returns `true` if this sidebar is shown.
     */
    isVisible(): boolean {
        return this.jq.hasClass('p-sidebar-active');
    }

    /**
     * Calls the `close` behavior and the `onHide` callback.
     * @param event Unused, passed to the callback.
     * @param ui Unused, passed to the callback.
     */
    private onHide(event?: JQuery.TriggeredEvent, ui?: unknown): void {
        this.callBehavior('close');

        if (this.cfg.onHide) {
            this.cfg.onHide.call(this, event, ui);
        }
    }

    /**
     * Hides this sidebar if it is shown, or shows it if it is hidden.
     */
    toggle(): void {
        if (this.isVisible()) {
            this.hide();
        }
        else {
            this.show();
        }
    }

    override enableModality(overlay?: JQuery | null): void {
        super.enableModality(overlay);

        this.modalOverlay?.one('click.sidebar', () => {
            this.hide();
        });
    }

    protected override getModalTabbables(): JQuery {
        return this.jq.find(':tabbable');
    }

    private applyARIA(): void {
        this.jq.attr({
            'role': 'dialog',
            'aria-hidden': String(!this.cfg.visible),
            'aria-modal': String(!!(this.cfg.modal && this.cfg.visible))
        });

        if (this.cfg.showCloseIcon && this.closeIcon) {
            PrimeFaces.skinCloseAction(this.closeIcon);
        }
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
                        this.content.html(content);
                    }
                });

                return true;
            },
            oncomplete: () => {
                this.loaded = true;
                this._show();
            }
        };

        if (this.hasBehavior('loadContent')) {
            this.callBehavior('loadContent', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }
}
