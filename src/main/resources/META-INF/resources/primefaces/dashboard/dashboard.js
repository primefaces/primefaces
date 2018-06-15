/**
 * PrimeFaces Dashboard Widget
 */
PrimeFaces.widget.Dashboard = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.cfg.connectWith =  this.jqId + ' .ui-dashboard-column';
        this.cfg.placeholder = 'ui-state-hover';
        this.cfg.forcePlaceholderSize = true;
        this.cfg.revert=false;
        this.cfg.handle='.ui-panel-titlebar';

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

        $(this.jqId + ' .ui-dashboard-column').sortable(this.cfg);
    }

});