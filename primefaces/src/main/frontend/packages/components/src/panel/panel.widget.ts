/**
 * The configuration for the {@link Panel} widget.
 */
export interface PanelCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the panel is closable. */
    closable: boolean;
    /** Duration of the close effect in milliseconds. */
    closeSpeed: number;
    /** Whether the panel is collapsed, updated on each toggle. */
    collapsed: boolean;
    /** Whether the panel has a menu in the header. */
    hasMenu: boolean;
    /** Whether the panel can be expanded and collapsed. */
    toggleable: boolean;
    /** Whether a click on the whole header toggles the panel. */
    toggleableHeader: boolean;
    /** Whether the collapsed state is kept across views. */
    multiViewState: boolean;
    /** Orientation of the toggle effect. */
    toggleOrientation: PrimeFacesTypes.widget.Panel.ToggleOrientation;
    /** Duration of the toggle effect in milliseconds. */
    toggleSpeed: number;
}

/**
 * __PrimeFaces Panel Widget__
 *
 * Panel is a grouping component with content toggle, close and menu integration.
 */
export class Panel<Cfg extends PanelCfg = PanelCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The header of this panel. */
    header: JQuery = $();
    /** The title text in the header. */
    title: JQuery = $();
    /** The content of this panel. */
    content: JQuery = $();
    /** The toggler icon, only when toggleable. */
    toggler?: JQuery;
    /** The hidden input with the collapsed state, only when toggleable. */
    toggleStateHolder?: JQuery;
    /** The close icon, only when closable. */
    closer?: JQuery;
    /** The hidden input with the visible state, only when closable. */
    visibleStateHolder?: JQuery;
    /** Whether an icon in the header was clicked, so the header click does not toggle. */
    isTitlebarClicked: boolean = false;
    /** The width of this panel before it was collapsed horizontally. */
    originalWidth?: number;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.header = this.jq.children('div.p-panel-titlebar');
        this.title = this.header.children('span.p-panel-title');
        this.content = $(this.jqId + '_content');

        this.bindEvents();
    }

    private bindEvents(): void {
        if (this.cfg.toggleable) {
            this.bindToggler();

            if (this.cfg.toggleableHeader) {
                this.header.on('click.panel', () => {
                    if (!this.isTitlebarClicked) {
                        this.toggle();
                    }

                    this.isTitlebarClicked = false;
                });
            }
        }

        if (this.cfg.closable) {
            this.bindCloser();
        }

        if (this.cfg.hasMenu) {
            const menu = $(this.jqId + '_menu');
            const label = this.getLabel('choose');
            if (label !== undefined) {
                menu.attr('aria-label', label);
            }
            menu.on('click.panel', (e) => {
                e.preventDefault();
            });
        }

        // visuals for action items
        this.header.find('.p-panel-titlebar-icon').on('mouseover.panel', (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        }).on('mouseout.panel', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        }).on('click.panel', (e) => {
            const href = $(e.currentTarget).attr('href');
            if (!href || href === '#') {
                e.preventDefault();
            }

            this.isTitlebarClicked = true;
        });
    }

    /**
     * Expands this panel if it is collapsed, or collapses it if it is expanded.
     */
    toggle(): void {
        if (this.cfg.collapsed) {
            this.expand();
            PrimeFaces.invokeDeferredRenders(this.id);
        }
        else {
            this.collapse();
        }
    }

    /**
     * Expands this panel.
     */
    expand(): void {
        this.toggleState(false);

        if (this.cfg.toggleOrientation === 'vertical') {
            this.slideDown();
        }
        else if (this.cfg.toggleOrientation === 'horizontal') {
            this.slideRight();
        }
    }

    /**
     * Collapses this panel.
     */
    collapse(): void {
        this.toggleState(true);

        if (this.cfg.toggleOrientation === 'vertical') {
            this.slideUp();
        }
        else if (this.cfg.toggleOrientation === 'horizontal') {
            this.slideLeft();
        }
    }

    private slideUp(): void {
        this.content.slideUp(this.cfg.toggleSpeed as number, 'easeInOutCirc', () => {
            this.jq.addClass('p-panel-collapsed');
        });
    }

    private slideDown(): void {
        this.jq.removeClass('p-panel-collapsed');
        this.content.slideDown(this.cfg.toggleSpeed as number, 'easeInOutCirc');
    }

    private slideLeft(): void {
        const toggler = this.toggler;
        if (!toggler) {
            return;
        }

        this.originalWidth = this.jq.width();

        this.title.hide();
        toggler.hide();
        this.content.hide();

        this.jq.animate({
            width: '42px'
        }, this.cfg.toggleSpeed as number, 'easeInOutCirc', () => {
            toggler.show();
            this.jq.addClass('p-panel-collapsed-h');
        });
    }

    private slideRight(): void {
        const toggler = this.toggler;
        if (!toggler) {
            return;
        }

        const expandWidth = this.originalWidth || '100%';

        toggler.hide();

        this.jq.animate({
            width: expandWidth
        }, this.cfg.toggleSpeed as number, 'easeInOutCirc', () => {
            this.jq.removeClass('p-panel-collapsed-h');
            this.title.show();
            toggler.show();

            this.content.css({
                'visibility': 'visible',
                'display': 'block',
                'height': 'auto'
            });
        });
    }

    /**
     * Updates the toggler and the state holder, and sends the new state to the server.
     * @param collapsed Whether the panel is now collapsed.
     */
    private toggleState(collapsed: boolean): void {
        if (!this.toggler || !this.toggleStateHolder) {
            // not toggleable
            return;
        }

        this.toggler.attr('aria-expanded', String(!collapsed));
        this.cfg.collapsed = collapsed;
        this.toggleStateHolder.val(String(collapsed));
        this.toggler.attr('aria-label', collapsed ? this.getAriaLabel('collapseRow') : this.getAriaLabel('expandRow'));

        if (this.hasBehavior('toggle')) {
            this.callBehavior('toggle');
        }
        else if (this.cfg.multiViewState) {
            const options: PrimeFacesTypes.ajax.Configuration = {
                source: this.getId(),
                partialSubmit: true,
                partialSubmitFilter: PrimeFaces.escapeClientId(this.getId() + '_collapsed'),
                process: this.getId(),
                ignoreAutoUpdate: true,
                global: false,
                params: [
                    { name: this.getId() + '_skipChildren', value: true }
                ]
            };

            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * Closes this panel with a fade out effect.
     */
    close(): void {
        if (this.visibleStateHolder) {
            this.visibleStateHolder.val('false');
        }

        this.jq.fadeOut(this.cfg.closeSpeed as number, () => {
            if (this.hasBehavior('close')) {
                this.callBehavior('close');
            }
        });
    }

    /**
     * Shows this panel with a fade in effect.
     */
    show(): void {
        this.jq.fadeIn(this.cfg.closeSpeed as number, () => {
            PrimeFaces.invokeDeferredRenders(this.id);
        });

        if (this.visibleStateHolder) {
            this.visibleStateHolder.val('true');
        }
    }

    private bindToggler(): void {
        const toggler = $(this.jqId + '_toggler');
        this.toggler = toggler;
        toggler.attr('role', 'button');
        this.toggleStateHolder = $(this.jqId + '_collapsed');
        toggler.attr('aria-label', this.cfg.collapsed ? this.getAriaLabel('collapseRow') : this.getAriaLabel('expandRow'));

        toggler.on('click.panel', () => {
            this.toggle();

            return false;
        });
    }

    private bindCloser(): void {
        const closer = $(this.jqId + '_closer');
        this.closer = closer;
        PrimeFaces.skinCloseAction(closer);
        this.visibleStateHolder = $(this.jqId + '_visible');

        closer.on('click.panel', (e) => {
            this.close();
            e.preventDefault();

            return false;
        });
    }
}
