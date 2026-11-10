/**
 * The configuration for the {@link CascadeSelect} widget.
 */
export interface CascadeSelectCfg extends PrimeFacesTypes.widget.DynamicOverlayWidgetCfg {
    /** If true, disables the component. */
    disabled: boolean;
}

/**
 * __PrimeFaces CascadeSelect Widget__
 *
 * CascadeSelect CascadeSelect displays a nested structure of options.
 */
export class CascadeSelect<Cfg extends CascadeSelectCfg = CascadeSelectCfg> extends PrimeFaces.widget.DynamicOverlayWidget<Cfg> {
    /** The DOM element for the content in the available selectable options. */
    contents: JQuery = $();
    /** Unbind callback for the hide overlay handler. */
    hideOverlayHandler?: PrimeFacesTypes.Unbindable;
    /** The DOM element for the hidden input with the current value. */
    input: JQuery = $();
    /** The DOM elements for the the available selectable options. */
    items: JQuery = $();
    /** The DOM element for the wrapper with the container with the available selectable options. */
    itemsWrapper: JQuery = $();
    /** The DOM element for the label indicating the currently selected option. */
    label: JQuery = $();
    /** The DOM element for the overlay panel with the available selectable options. */
    panel: JQuery = $();
    /** Handler for CSS transitions used by this widget. */
    transition?: PrimeFacesTypes.CssTransitionHandler | null;
    /** Unbind callback for the resize handler. */
    resizeHandler?: PrimeFacesTypes.Unbindable;
    /** Unbind callback for the scroll handler. */
    scrollHandler?: PrimeFacesTypes.Unbindable;
    /** The DOM elements for the buttons that can trigger (hide or show) the overlay panel with the available selectable options. */
    triggers: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        this.panel = $(PrimeFaces.escapeClientId(cfg.id as string) + '_panel');
        super.init(cfg, this.panel, cfg.id + '_panel');

        this.input = $(this.jqId + '_input');
        this.label = this.jq.children('.p-cascadeselect-label');
        this.triggers = this.jq.children('.p-cascadeselect-trigger').add(this.label);
        this.itemsWrapper = this.panel.children('.p-cascadeselect-items-wrapper');
        this.items = this.itemsWrapper.find('li.p-cascadeselect-item');
        this.contents = this.items.children('.p-cascadeselect-item-content');
        this.cfg.disabled = this.jq.hasClass('p-state-disabled');

        if (!this.cfg.disabled) {
            this.bindEvents();
            this.transition = PrimeFaces.utils.registerCSSTransition(this.panel, 'p-connected-overlay');
        }
    }

    /**
     * Sets up all event listeners that are required by this widget.
     */
    private bindEvents(): void {
        this.triggers.off('click.cascadeselect').on('click.cascadeselect', (e) => {
            if (this.panel.is(':hidden')) {
                this.show();
            }
            else {
                this.hide();
            }

            this.input.trigger('focus.cascadeselect');
            e.preventDefault();
        });

        this.input.off('focus.cascadeselect blur.cascadeselect keydown.cascadeselect')
            .on('focus.cascadeselect', () => {
                this.jq.addClass('p-state-focus');
            })
            .on('blur.cascadeselect', () => {
                this.jq.removeClass('p-state-focus');
            })
            .on('keydown.cascadeselect', (e) => {
                switch(e.key) {
                    case 'ArrowDown':
                        if (this.panel.is(':visible')) {
                            this.panel.find('.p-cascadeselect-item:first > .p-cascadeselect-item-content').focus();
                        }
                        else if (e.altKey) {
                            this.show();
                        }
                        e.preventDefault();
                        break;

                    case 'Escape':
                        if (this.panel.is(':visible')) {
                            this.hide();
                            e.preventDefault();
                        }
                        break;

                    case 'Tab':
                        this.hide();
                        break;

                    default:
                        break;
                }
            });

        this.contents.off('click.cascadeselect keydown.cascadeselect')
            .on('click.cascadeselect', (e) => {
                const item = $(e.currentTarget).parent();
                const subpanel = item.children('.p-cascadeselect-panel');

                this.deactivateItems(item);
                item.addClass('p-cascadeselect-item-active p-state-highlight');

                if (subpanel.length > 0) {
                    const parentPanel = item.closest('.p-cascadeselect-panel');
                    this.alignSubPanel(subpanel, parentPanel);
                    subpanel.show();
                }
                else {
                    this.input.val(item.attr('data-value') as string);
                    this.label.text(item.attr('data-label') as string).removeClass('p-cascadeselect-label-placeholder');
                    this.panel.find('.p-cascadeselect-item.p-state-active').removeClass('p-state-active');
                    item.addClass('p-state-active');
                    this.callBehavior('itemSelect');
                    this.hide();
                    e.stopPropagation();
                }
            })
            .on('keydown.cascadeselect', (e) => {
                const item = $(e.currentTarget).parent();

                switch(e.key) {
                    case 'ArrowDown': {
                        const nextItem = item.next();
                        if (nextItem) {
                            nextItem.children('.p-cascadeselect-item-content').focus();
                        }
                        break;
                    }

                    case 'ArrowUp': {
                        const prevItem = item.prev();
                        if (prevItem) {
                            prevItem.children('.p-cascadeselect-item-content').focus();
                        }
                        break;
                    }

                    case 'ArrowRight':
                        if (item.hasClass('p-cascadeselect-item-group')) {
                            if (item.hasClass('p-cascadeselect-item-active')) {
                                item.find('> .p-cascadeselect-panel > .p-cascadeselect-item:first > .p-cascadeselect-item-content').focus();
                            }
                            else {
                                item.children('.p-cascadeselect-item-content').trigger('click.cascadeselect');
                            }
                        }
                        break;

                    case 'ArrowLeft': {
                        this.hideGroup(item);
                        this.hideGroup(item.siblings('.p-cascadeselect-item-active'));

                        const parentItem = item.parent().closest('.p-cascadeselect-item');
                        if (parentItem) {
                            parentItem.children('.p-cascadeselect-item-content').focus();
                        }
                        break;
                    }

                    case 'Enter':
                    case 'NumpadEnter':
                        item.children('.p-cascadeselect-item-content').trigger('click.cascadeselect');
                        if (!item.hasClass('p-cascadeselect-item-group')) {
                            this.input.trigger('focus.cascadeselect');
                        }
                        break;

                    default:
                        break;
                }

                e.preventDefault();
            });
    }

    /**
     * Removes some event listeners when this widget was disabled.
     */
    private unbindEvents(): void {
        this.contents.off();
        this.triggers.off();
        this.input.off();
    }

    /**
     * Disables this widget so that the user cannot select any option.
     */
    override disable(): void {
        if (!this.cfg.disabled) {
            this.cfg.disabled = true;
            this.jq.addClass('p-state-disabled');
            this.input.attr('disabled', 'disabled');
            this.unbindEvents();
        }
    }

    /**
     * Enables this widget so that the user can select an option.
     */
    override enable(): void {
        if (this.cfg.disabled) {
            this.cfg.disabled = false;
            this.jq.removeClass('p-state-disabled');
            this.input.removeAttr('disabled');
            this.bindEvents();
        }
    }

    /**
     * Deactivate siblings and active children of an item.
     * @param item Cascade select panel element.
     */
    private deactivateItems(item: JQuery): void {
        const parentItem = item.parent().parent();
        const siblings = item.siblings('.p-cascadeselect-item-active');

        this.hideGroup(siblings);
        this.hideGroup(siblings.find('.p-cascadeselect-item-active'));

        if (!parentItem.is(this.itemsWrapper)) {
            this.deactivateItems(parentItem);
        }
    }

    /**
     * Sets up all panel event listeners
     */
    private bindPanelEvents(): void {
        this.hideOverlayHandler = PrimeFaces.utils.registerHideOverlayHandler(this, 'mousedown.' + this.id + '_hide', this.panel,
            () => this.triggers,
            (e, eventTarget) => {
                if(!(this.panel.is(eventTarget) || this.panel.has(eventTarget[0] ?? "").length > 0)) {
                    this.hide();
                }
            });

        this.resizeHandler = PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_hide', this.panel, () => {
            this.handleViewportChange();
        });

        // #12008 - Only register scroll handler if there is a small number of items
        if (this.items.length < 10) {
            this.scrollHandler = PrimeFaces.utils.registerConnectedOverlayScrollHandler(this, 'scroll.' + this.id + '_hide', this.jq, () => {
                this.handleViewportChange();
            });
        }
    }

    /**
     * Fired when the browser viewport is resized or scrolled.  In Mobile environment we don't want to hider the overlay
     * we want to re-align it.  This is because on some mobile browser the popup may force the browser to trigger a
     * resize immediately and close the overlay. See GitHub #7075.
     */
    private handleViewportChange(): void {
        if (PrimeFaces.env.mobile || PrimeFaces.hideOverlaysOnViewportChange === false) {
            this.alignPanel();
        } else {
            this.hide();
        }
    }

    /**
     * Unbind all panel event listeners
     */
    private unbindPanelEvents(): void {
        if (this.hideOverlayHandler) {
            this.hideOverlayHandler.unbind();
        }

        if (this.resizeHandler) {
            this.resizeHandler.unbind();
        }

        if (this.scrollHandler) {
            this.scrollHandler.unbind();
        }
    }

    /**
     * Brings up the overlay panel with the available options.
     */
    show(): void {
        if (this.transition) {
            this.transition.show({
                onEnter: () => {
                    PrimeFaces.nextZindex(this.panel);
                    this.alignPanel();
                },
                onEntered: () => {
                    this.input.attr('aria-expanded', 'true');
                    this.bindPanelEvents();
                }
            });
        }
    }

    /**
     * Hides the panel of a group item.
     * @param item Dom element of the cascadeselect.
     */
    hideGroup(item: JQuery): void {
        item.removeClass('p-cascadeselect-item-active p-state-highlight').children('.p-cascadeselect-panel').hide();
    }

    /**
     * Hides the overlay panel with the available options.
     */
    hide(): void {
        if (this.panel.is(':visible') && this.transition) {
            this.transition.hide({
                onExit: () => {
                    this.unbindPanelEvents();
                },
                onExited: () => {
                    this.panel.css('z-index', '');
                    this.input.attr('aria-expanded', 'false');
                }
            });
        }
    }

    /**
     * Adjust the width of the overlay panel.
     */
    private alignPanelWidth(): void {
        //align panel and container
        if (this.cfg.appendTo) {
            this.panel.css('min-width', this.jq.outerWidth()!);
        }
    }

    /**
     * Align the overlay panel with the available options.
     */
    private alignPanel(): void {
        this.alignPanelWidth();

        if (this.panel.parent().is(this.jq)) {
            this.panel.css({
                left: '0px',
                top: this.jq.innerHeight() + 'px',
                'transform-origin': 'center top'
            });
        }
        else {
            PrimeFaces.utils.position(this.panel.css({left:'0px', top:'0px', 'transform-origin': 'center top'}), {
                my: 'left top'
                ,at: 'left bottom'
                ,of: this.jq
                ,collision: 'flipfit'
                ,using: function(pos: { top: number; left: number }, directions: PrimeFacesTypes.utils.PositionFeedback) {
                    $(this).css('transform-origin', 'center ' + directions.vertical).css(pos);
                }
            });
        }
    }

    /**
     * Align the sub overlay panel with the available options.
     * @param subpanel Sub panel element of the cascade select panel.
     * @param parentPanel Parent panel element of the sub panel element.
     */
    private alignSubPanel(subpanel: JQuery, parentPanel: JQuery): void {
        const subitemWrapper = subpanel.children('.p-cascadeselect-items-wrapper');
        subpanel.css({'display':'block', 'opacity':'0', 'pointer-events': 'none'});
        subitemWrapper.css({'overflow': 'scroll'});

        PrimeFaces.utils.position(subpanel.css({left:'0px', top:'0px'}), {
                my: 'left top'
                ,at: 'right top'
                ,of: parentPanel.children('.p-cascadeselect-item-active:first')
                ,collision: 'flipfit'
            });

        subpanel.css({'display':'none', 'opacity':'', 'pointer-events': '', 'z-index': PrimeFaces.nextZindex()});
        subitemWrapper.css({'overflow': ''});
    }
}
