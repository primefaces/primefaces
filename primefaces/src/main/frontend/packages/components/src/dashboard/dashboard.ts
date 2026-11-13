import type { KnownInteractionOptions } from "../dragdrop/dragdrop.js";

/**
 * The `ui` argument of {@link Dashboard.handleDrop}: the one of the sortable (legacy dashboard, with `item` and
 * `sender`) or of the droppable (responsive dashboard, with `draggable`).
 */
export type DropUi = Partial<Pick<PrimeFaces.interactions.SortableUi, "item" | "sender">>
    & Partial<Pick<PrimeFaces.interactions.DroppableUi, "draggable">>;

/**
 * The configuration for the {@link Dashboard} component.
 */
export interface DashboardCfg extends PrimeFaces.component.BaseComponentCfg, KnownInteractionOptions<PrimeFaces.interactions.SortableOptions> {
    /** Whether the responsive dashboard (drag and drop of the panels) is used instead of the legacy columns. */
    responsive: boolean;
    /** Selector of the draggable panels of the responsive dashboard, set on init. */
    draggable: string;
    /** Selector of the panels of the responsive dashboard, set on init. */
    panels: string;
}

/**
 * __PrimeFaces Dashboard Component__
 *
 * Dashboard provides a portal like layout with drag & drop based reorder capabilities.
 *
 * The columns are sortable with {@link PrimeFaces.interactions.sortable}, which can be used to interact with the dashboard
 * programmatically.
 *
 * ```javascript
 * const component = PF("myDashboard");
 *
 * // When dragged outside the dashboard: Have the items revert to their new positions using a smooth animation
 * PrimeFaces.interactions.sortable(component.jq.find(".p-dashboard-column"), { revert: true });
 * ```
 */
export class Dashboard<Cfg extends DashboardCfg = DashboardCfg> extends PrimeFaces.component.BaseComponent<Cfg> {

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
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
            drop: function(event: JQuery.TriggeredEvent, ui: PrimeFaces.interactions.DroppableUi) {
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
     * Sets up all event listeners required by this component.
     */
    private bindSortableEvents(): void {
        const $this = this;
        if (this.hasBehavior('reorder')) {
            this.cfg.update = function(event: JQuery.TriggeredEvent, ui: PrimeFaces.interactions.SortableUi) {
                if (this === ui.item.parent()[0]) {
                    $this.handleDrop($this, event, ui);
                }
            };
        }
    }

    /**
     * Handle dropping a panel either from legacy sortable or responsive draggable.
     * @param component the Dashboard component
     * @param e Event that occurred.
     * @param ui the UI element that was dragged
     */
    handleDrop(component: PrimeFaces.Component<"BaseComponent">, e: JQuery.TriggeredEvent, ui: DropUi): void {
        const item = (ui.item || ui.draggable)!;
        const ext: Partial<PrimeFaces.ajax.ConfigurationExtender> & Required<Pick<PrimeFaces.ajax.ConfigurationExtender, "params">> = {
            params: [
                { name: component.id + '_reordered', value: true },
                { name: component.id + '_panelId', value: item.attr('id') }
            ]
        };

        if (ui.draggable) {
            // responsive: the dragged panel swapped its position with the panel it was dropped on
            ext.params.push({ name: component.id + '_targetPanelId', value: $(e.target).attr('id') });
        }
        else {
            // classic: the panel was moved to the given index of the given column
            const column = item.parent();
            ext.params.push({ name: component.id + '_columnIndex', value: column.parent().children('.p-dashboard-column').index(column) });
            ext.params.push({ name: component.id + '_itemIndex', value: column.children().filter(':not(script):visible').index(item) });
        }

        component.callBehavior('reorder', ext);
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
