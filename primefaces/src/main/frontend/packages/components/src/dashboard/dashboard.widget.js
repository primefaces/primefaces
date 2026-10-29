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
 * 
 * @interface {PrimeFaces.widget.DashboardCfg} cfg The configuration for the {@link  Dashboard| Dashboard widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {PrimeFacesTypes.interactions.SortableOptions} cfg
 */
PrimeFaces.widget.Dashboard = class Dashboard extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
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
     * @private
     */
    renderResponsive() {
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
     * @private
     */
    bindDraggable() {
        // draggable panels by their title bar
        PrimeFaces.interactions.draggable($(this.cfg.panels), {
            scope: this.cfg.scope || 'dashboard',
            revert: true,
            handle: '.p-panel-titlebar',
            zIndex: 9999,
            opacity: 0.7
        });
    }

    /**
     * Sets up all droppable panels.
     * @private
     */
    bindDroppable() {
        var $this = this;
        // droppable on other panels in the dashboard
        PrimeFaces.interactions.droppable($(this.cfg.panels), {
            scope: this.cfg.scope || 'dashboard',
            tolerance: 'pointer',
            classes: {
                'p-droppable-active': 'p-dashboard-active',
                'p-droppable-hover': 'p-dashboard-hover'
            },
            drop: function(event, ui) {
                $this.swapPanels($(this).get(0), $(ui.draggable).get(0));
                if ($this.hasBehavior('reorder')) {
                    $this.handleDrop($this, event, ui);
                }
            }
        });
    }

    /**
     * Sets up the sortable for the legacy dashboard.
     * @param {HTMLElement} a the first panel
     * @param {HTMLElement} b the second panel
     * @private
     */
    swapPanels(a, b) {
        var aparent = a.parentNode;
        var asibling = a.nextSibling === b ? a : a.nextSibling;
        b.parentNode.insertBefore(a, b);
        aparent.insertBefore(b, asibling);
    }

    /**
      * Sets up the sortable for the legacy dashboard.
      * @private
      */
    renderSortable() {
        this.cfg.connectWith = this.jqId + ' .p-dashboard-column';
        this.cfg.placeholder = 'p-state-hover';
        this.cfg.forcePlaceholderSize = true;
        this.cfg.revert = false;
        this.cfg.handle = '.p-panel-titlebar';
        this.bindSortableEvents();
        PrimeFaces.interactions.sortable($(this.cfg.connectWith), this.cfg);
    }

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindSortableEvents() {
        var $this = this;
        if (this.hasBehavior('reorder')) {
            this.cfg.update = function(event, ui) {
                if (this === ui.item.parent()[0]) {
                    $this.handleDrop($this, event, ui);
                }
            };
        }
    }

    /**
     * Handle dropping a panel either from legacy sortable or responsive draggable.
     * @param {PrimeFaces.widget.BaseWidget} widget the Dashboard widget
     * @param {Event} e Event that occurred.
     * @param {JQuery} ui the UI element that was dragged
     */
    handleDrop(widget, e, ui) {
        var item = ui.item || ui.draggable;
        var isResponsive = ui.draggable;
        var parent = item.parent();
        var grid;
        var itemIndex, receiverColumnIndex;
        if (isResponsive) {
            grid = parent.parent();
            itemIndex = 0;
            receiverColumnIndex = grid.children().filter(':not(script):visible').index(parent);
        }
        else {
            itemIndex = parent.children().filter(':not(script):visible').index(item);
            receiverColumnIndex = parent.parent().children().index(parent);
        }

        var ext = {
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
            var target = $(e.target);
            ext.params.push({ name: widget.id + '_senderColumnIndex', value: grid.children().filter(':not(script):visible').index(target.parent()) });
        }

        widget.callBehavior('reorder', ext);
    }

    /**
     * Disables this dashboard so that it cannot be modified.
     */
    disable() {
        this.jq.addClass('p-state-disabled');
    }

    /**
     * Enables this dashboard so that it can be modified.
     */
    enable() {
        this.jq.removeClass('p-state-disabled');
    }

}