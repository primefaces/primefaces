import type { KnownInteractionOptions } from "../dragdrop/dragdrop.js";

/**
 * The configuration for the {@link Dashboard} widget.
 */
export interface DashboardCfg extends PrimeFacesTypes.widget.BaseWidgetCfg, KnownInteractionOptions<PrimeFacesTypes.interactions.SortableOptions> {
    /** Whether the responsive dashboard (drag and drop of the panels) is used instead of the legacy columns. */
    responsive: boolean;
    /** Selector of the draggable panels of the responsive dashboard, set on init. */
    draggable: string;
    /** Selector of the panels of the responsive dashboard, set on init. */
    panels: string;
}

/**
 * __PrimeFaces Dashboard Widget__
 *
 * Dashboard provides a portal like layout with drag & drop based reorder capabilities.
 *
 * The columns are sortable with {@link PrimeFaces.interactions.sortable}, which can be used to interact with the dashboard
 * programmatically.
 *
 * ```javascript
 * const widget = PF("MyDashboardWidget");
 *
 * // When dragged outside the dashboard: Have the items revert to their new positions using a smooth animation
 * PrimeFaces.interactions.sortable(widget.jq.find(".p-dashboard-column"), { revert: true });
 * ```
 */
export class Dashboard<Cfg extends DashboardCfg = DashboardCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        if (this.cfg.responsive) {
            this.renderResponsive();
        }
        else {
            this.renderSortable();
        }
    }

    /**
     * Sets up the responsive drag drop for the dashboard.
     */
    private renderResponsive(): void {
        if (this.cfg.disabled) {
            return;
        }
        this.cfg.draggable = this.jqId + ' .p-dashboard-panel';
        this.cfg.panels = this.jqId + ' .p-panel';
        this.bindDraggable();
        this.bindDroppable();
    }

    /**
     * Sets up all draggable panels.
     */
    private bindDraggable(): void {
        // draggable panels by their title bar
        PrimeFaces.interactions.draggable($(this.cfg.panels as string), {
            scope: this.cfg.scope || 'dashboard',
            revert: true,
            handle: '.p-panel-titlebar',
            zIndex: 9999,
            opacity: 0.7
        });
    }

    /**
     * Sets up all droppable panels.
     */
    private bindDroppable(): void {
        const $this = this;
        // droppable on other panels in the dashboard
        PrimeFaces.interactions.droppable($(this.cfg.panels as string), {
            scope: this.cfg.scope || 'dashboard',
            tolerance: 'pointer',
            classes: {
                'p-droppable-active': 'p-dashboard-active',
                'p-droppable-hover': 'p-dashboard-hover'
            },
            drop: function(event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DroppableUi) {
                $this.swapPanels($(this).get(0)!, $(ui.draggable).get(0)!);
                if ($this.hasBehavior('reorder')) {
                    $this.handleDrop($this, event, ui);
                }
            }
        });
    }

    /**
     * Sets up the sortable for the legacy dashboard.
     * @param a the first panel
     * @param b the second panel
     */
    private swapPanels(a: HTMLElement, b: HTMLElement): void {
        const aparent = a.parentNode!;
        const asibling = a.nextSibling === b ? a : a.nextSibling;
        b.parentNode!.insertBefore(a, b);
        aparent.insertBefore(b, asibling);
    }

    /**
     * Sets up the sortable for the legacy dashboard.
     */
    private renderSortable(): void {
        this.cfg.connectWith = this.jqId + ' .p-dashboard-column';
        this.cfg.placeholder = 'p-state-hover';
        this.cfg.forcePlaceholderSize = true;
        this.cfg.revert = false;
        this.cfg.handle = '.p-panel-titlebar';
        this.bindSortableEvents();
        PrimeFaces.interactions.sortable($(this.cfg.connectWith as string), this.cfg);
    }

    /**
     * Sets up all event listeners required by this widget.
     */
    private bindSortableEvents(): void {
        const $this = this;
        if (this.hasBehavior('reorder')) {
            this.cfg.update = function(event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.SortableUi) {
                if (this === ui.item.parent()[0]) {
                    $this.handleDrop($this, event, ui);
                }
            };
        }
    }

    /**
     * Handle dropping a panel either from legacy sortable or responsive draggable.
     * @param widget the Dashboard widget
     * @param e Event that occurred.
     * @param ui the UI element that was dragged
     */
    handleDrop(widget: PrimeFacesTypes.Widget<"BaseWidget">, e: JQuery.TriggeredEvent, ui: PrimeFacesTypes.widget.Dashboard.DropUi): void {
        const item = (ui.item || ui.draggable)!;
        const isResponsive = ui.draggable;
        const parent = item.parent();
        let grid: JQuery | undefined;
        let itemIndex: number, receiverColumnIndex: number;
        if (isResponsive) {
            grid = parent.parent();
            itemIndex = 0;
            receiverColumnIndex = grid.children().filter(':not(script):visible').index(parent);
        }
        else {
            itemIndex = parent.children().filter(':not(script):visible').index(item);
            receiverColumnIndex = parent.parent().children().index(parent);
        }

        const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> & Required<Pick<PrimeFacesTypes.ajax.ConfigurationExtender, "params">> = {
            params: [
                { name: widget.id + '_reordered', value: true },
                { name: widget.id + '_widgetId', value: item.attr('id') },
                { name: widget.id + '_itemIndex', value: itemIndex },
                { name: widget.id + '_receiverColumnIndex', value: receiverColumnIndex }
            ]
        };

        if (ui.sender) {
            ext.params.push({ name: widget.id + '_senderColumnIndex', value: ui.sender.parent().children().index(ui.sender) });
        }
        if (isResponsive) {
            const target = $(e.target);
            ext.params.push({ name: widget.id + '_senderColumnIndex', value: grid!.children().filter(':not(script):visible').index(target.parent()) });
        }

        widget.callBehavior('reorder', ext);
    }

    /**
     * Disables this dashboard so that it cannot be modified.
     */
    override disable(): void {
        this.jq.addClass('p-state-disabled');
    }

    /**
     * Enables this dashboard so that it can be modified.
     */
    override enable(): void {
        this.jq.removeClass('p-state-disabled');
    }
}
