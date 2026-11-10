/**
 * The configuration for the {@link SpeedDial} widget.
 */
export interface SpeedDialCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** Direction in which the items open. */
    direction: PrimeFacesTypes.widget.SpeedDial.OpeningDirection;
    /** Whether the items close on a click outside. */
    hideOnClickOutside: boolean;
    /** Whether a mask is shown behind the speed dial while open. */
    mask: boolean;
    /** Called when the button was clicked. */
    onClick: PrimeFacesTypes.widget.SpeedDial.OnClickCallback;
    /** Called when the items were closed. */
    onHide: PrimeFacesTypes.widget.SpeedDial.OnHideCallback;
    /** Called when the items were opened. */
    onShow: PrimeFacesTypes.widget.SpeedDial.OnShowCallback;
    /** Called when the items were opened or closed. */
    onVisibleChange: PrimeFacesTypes.widget.SpeedDial.OnVisibleChangeCallback;
    /** Radius of the circle types, defaults to 20 pixels per item. */
    radius: number;
    /** Whether the items stay open when an item was clicked. */
    keepOpen: boolean;
    /** Transition delay in milliseconds between the items. */
    transitionDelay: number;
    /** How the items are arranged. */
    type: PrimeFacesTypes.widget.SpeedDial.OpeningType;
    /** Whether the items are open initially. */
    visible: boolean;
}

/**
 * __PrimeFaces SpeedDial Widget__
 *
 * When pressed, a floating action button can display multiple primary actions that can be performed on a page.
 */
export class SpeedDial<Cfg extends SpeedDialCfg = SpeedDialCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** The mask behind the speed dial. */
    mask: JQuery = $();
    /** The container of the button and the items. */
    container: JQuery = $();
    /** The badge around the button, if any. */
    badge: JQuery = $();
    /** The floating action button. */
    button: JQuery = $();
    /** The icon of the button. */
    buttonIcon: JQuery = $();
    /** The container of the items. */
    itemContainer: JQuery = $();
    /** The items. */
    items: JQuery = $();
    /** The number of items. */
    itemsCount: number = 0;
    /** Whether the items are open. */
    visible: boolean = false;
    /** Whether the button or an item was clicked, so the document click does not close the items. */
    isItemClicked?: boolean;
    /** The click listener on the document, only with `hideOnClickOutside`. */
    documentClickListener: ((event: JQuery.TriggeredEvent) => void) | null = null;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.mask = this.jq.children('.p-speeddial-mask');
        this.container = this.jq.children('.p-speeddial');
        this.badge = this.container.children('.p-overlay-badge');
        const buttonContainer = this.badge.length === 0 ? this.container : this.badge;
        this.button = buttonContainer.children('.p-speeddial-button');
        this.buttonIcon = this.button.children('.p-button-icon-left, .p-button-icon-right');
        this.itemContainer = this.container.children('.p-speeddial-list');
        this.items = this.itemContainer.children('.p-speeddial-item');
        this.itemsCount = this.items.length;

        this.cfg.visible = this.cfg.visible || false;
        this.cfg.direction = this.cfg.direction || 'up';
        this.cfg.transitionDelay = this.cfg.transitionDelay || 30;
        this.cfg.type = this.cfg.type || 'linear';
        this.cfg.radius = this.cfg.radius || 0;
        this.cfg.mask = this.cfg.mask || false;
        this.cfg.hideOnClickOutside = this.cfg.hideOnClickOutside === undefined ? true : this.cfg.hideOnClickOutside;
        this.cfg.keepOpen = this.cfg.keepOpen || false;

        this.visible = this.cfg.visible;

        this.renderDeferred();
    }

    protected override _render(): void {
        this.createItemContainerStyle();
        this.updateItemStyles();

        if (this.cfg.hideOnClickOutside) {
            this.bindDocumentClickListener();
        }

        this.bindEvents();

        if (this.cfg.visible) {
            this.show();
        }
    }

    /**
     * Sets the size differences between button and items for the circle types.
     */
    private createItemContainerStyle(): void {
        if (this.cfg.type !== 'linear') {
            const button = this.button.get(0);
            const firstItem = this.items.get(0);
            const itemContainer = this.itemContainer.get(0);

            if (button && firstItem && itemContainer) {
                const wDiff = Math.abs(button.offsetWidth - firstItem.offsetWidth);
                const hDiff = Math.abs(button.offsetHeight - firstItem.offsetHeight);
                itemContainer.style.setProperty('--item-diff-x', wDiff / 2 + 'px');
                itemContainer.style.setProperty('--item-diff-y', hDiff / 2 + 'px');
            }
        }
    }

    private updateItemStyles(): void {
        for (let i = 0; i < this.itemsCount; i++) {
            const itemStyle = this.getItemStyle(i);
            this.items.eq(i).css(itemStyle);
        }
    }

    private bindEvents(): void {
        this.button.on('click.speeddial', (e) => {
            this.onClick(e);
        });

        this.items.on('click.speeddial', () => {
            this.onItemClick();
        });
    }

    /**
     * Opens the items.
     */
    show(): void {
        if (this.mask) {
            this.mask.addClass('p-speeddial-mask-visible');
        }

        this.container.addClass('p-speeddial-opened');
        this.container.find('a').attr('tabindex', '0');
        this.visible = true;
        this.updateItemStyles();

        if (this.cfg.onVisibleChange) {
            this.cfg.onVisibleChange.call(this, true);
        }

        if (this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    }

    /**
     * Closes the items.
     */
    hide(): void {
        if (this.mask) {
            this.mask.removeClass('p-speeddial-mask-visible');
        }

        this.container.removeClass('p-speeddial-opened');
        this.container.find('a').attr('tabindex', '-1');
        this.visible = false;
        this.updateItemStyles();

        if (this.cfg.onVisibleChange) {
            this.cfg.onVisibleChange.call(this, false);
        }

        if (this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    }

    /**
     * Opens or closes the items after a click on the button.
     * @param event The click event.
     */
    private onClick(event: JQuery.TriggeredEvent): void {
        if (this.visible) {
            this.hide();
        }
        else {
            this.show();
        }

        if (this.cfg.onClick) {
            this.cfg.onClick.call(this, event);
        }

        this.isItemClicked = true;
    }

    private onItemClick(): void {
        if (!this.cfg.keepOpen) {
            this.hide();
        }

        this.isItemClicked = true;
    }

    private bindDocumentClickListener(): void {
        if (!this.documentClickListener) {
            this.documentClickListener = (event) => {
                if (this.visible && this.isOutsideClicked(event)) {
                    this.hide();
                }

                this.isItemClicked = false;
            };
            $(document).on('click.' + this.id, this.documentClickListener);
            const unbindDocumentClickListener = () => {
                $(document).off('click.' + this.id);
                this.documentClickListener = null;
            };
            this.addRefreshListener(unbindDocumentClickListener);
            this.addDestroyListener(unbindDocumentClickListener);
        }
    }

    /**
     * @param event The click event.
     * @returns Whether the click was outside of the speed dial and not on the button or an item.
     */
    private isOutsideClicked(event: JQuery.TriggeredEvent): boolean | undefined {
        const containerEl = this.container.get(0);
        const target = event.target as Node;
        return containerEl && !(containerEl.isSameNode(target) || containerEl.contains(target) || this.isItemClicked);
    }

    /**
     * @param index Index of the item.
     * @returns The transition delay of the item in milliseconds.
     */
    private calculateTransitionDelay(index: number): number {
        const length = this.itemsCount;
        const visible = this.visible;

        return (visible ? index : length - index - 1) * (this.cfg.transitionDelay ?? 0);
    }

    /**
     * @param index Index of the item.
     * @returns The position of the item for the circle types.
     */
    private calculatePointStyle(index: number): Record<string, string> {
        const type = this.cfg.type;
        let step = 0;
        const direction = this.cfg.direction;
        let x = '';
        let y = '';

        if (type !== 'linear') {
            const length = this.itemsCount;
            const radius = this.cfg.radius || (length * 20);

            if (type === 'circle') {
                step = 2 * Math.PI / length;

                return {
                    left: 'calc(' + (radius * Math.cos(step * index)) + 'px + var(--item-diff-x, 0px))',
                    top: 'calc(' + (radius * Math.sin(step * index)) + 'px + var(--item-diff-y, 0px))',
                };
            }
            else if (type === 'semi-circle') {
                step = Math.PI / (length - 1);
                x = 'calc(' + (radius * Math.cos(step * index)) + 'px + var(--item-diff-x, 0px))';
                y = 'calc(' + (radius * Math.sin(step * index)) + 'px + var(--item-diff-y, 0px))';
                if (direction === 'up') {
                    return { left: x, bottom: y };
                }
                else if (direction === 'down') {
                    return { left: x, top: y };
                }
                else if (direction === 'left') {
                    return { right: y, top: x };
                }
                else if (direction === 'right') {
                    return { left: y, top: x };
                }
            }
            else if (type === 'quarter-circle') {
                step = Math.PI / (2 * (length - 1));
                x = 'calc(' + (radius * Math.cos(step * index)) + 'px + var(--item-diff-x, 0px))';
                y = 'calc(' + (radius * Math.sin(step * index)) + 'px + var(--item-diff-y, 0px))';
                if (direction === 'up-left') {
                    return { right: x, bottom: y };
                }
                else if (direction === 'up-right') {
                    return { left: x, bottom: y };
                }
                else if (direction === 'down-left') {
                    return { right: y, top: x };
                }
                else if (direction === 'down-right') {
                    return { left: y, top: x };
                }
            }
        }

        return {};
    }

    /**
     * @param index Index of the item.
     * @returns The position and transition delay of the item.
     */
    private getItemStyle(index: number): Record<string, string> {
        const transitionDelay = this.calculateTransitionDelay(index);
        const pointStyle = this.calculatePointStyle(index);
        pointStyle['transitionDelay'] = transitionDelay + 'ms';

        return pointStyle;
    }
}
