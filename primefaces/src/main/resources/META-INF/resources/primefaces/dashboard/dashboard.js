/**
 * __PrimeFaces Dashboard Widget__
 * 
 * Dashboard provides a portal like layout with drag & drop based reorder capabilities.
 * 
 * Currently this uses the JQueryUI sortable widget. You can use `$.fn.sortable` to interact with the dashboard
 * programmatically.
 * 
 * ```javascript
 * const widget = PF("MyDashboardWidget");
 * 
 * // When dragged outside the dashboard: Have the items revert to their new positions using a smooth animation
 * widget.jq.find(".ui-dashboard-column").sortable("option", "revert", true);
 * ```
 * 
 * @interface {PrimeFaces.widget.DashboardCfg} cfg The configuration for the {@link  Dashboard| Dashboard widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryUI.SortableOptions} cfg
 */
PrimeFaces.widget.Dashboard = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.connectWith =  this.jqId + ' .ui-dashboard-column';
        this.cfg.placeholder = 'ui-state-hover';
        this.cfg.forcePlaceholderSize = true;
        this.cfg.revert=false;
        this.cfg.handle='.ui-panel-titlebar';

        this.bindEvents();

        $(this.jqId + ' .ui-dashboard-column').sortable(this.cfg);
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        if(this.hasBehavior('reorder')) {
            this.cfg.update = function(e, ui) {
                if(this === ui.item.parent()[0]) {
                    var itemIndex = ui.item.parent().children().filter(':not(script):visible').index(ui.item),
                    receiverColumnIndex =  ui.item.parent().parent().children().index(ui.item.parent());

                    var ext = {
                        params: [
                            {name: $this.id + '_reordered', value: true},
                            {name: $this.id + '_widgetId', value: ui.item.attr('id')},
                            {name: $this.id + '_itemIndex', value: itemIndex},
                            {name: $this.id + '_receiverColumnIndex', value: receiverColumnIndex}
                        ]
                    };

                    if(ui.sender) {
                        ext.params.push({name: $this.id + '_senderColumnIndex', value: ui.sender.parent().children().index(ui.sender)});
                    }

                    $this.callBehavior('reorder', ext);
                }
            };
        }
    },

    /**
     * Disables this dashboard so that it cannot be modified.
     */
    disable: function () {
        this.jq.addClass('ui-state-disabled');
    },

    /**
     * Enables this dashboard so that it can be modified.
     */
    enable: function () {
        this.jq.removeClass('ui-state-disabled');
    }

});