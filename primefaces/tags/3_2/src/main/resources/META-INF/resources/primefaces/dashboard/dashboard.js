/**
 * PrimeFaces Dashboard Widget
 */
PrimeFaces.widget.Dashboard = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.connectWith = '.ui-dashboard-column';
        this.cfg.placeholder = 'ui-state-hover';
        this.cfg.forcePlaceholderSize = true;
        this.cfg.revert=true;
        this.cfg.handle='.ui-panel-titlebar';

        var _self = this;

        if(this.cfg.behaviors) {
            var reorderBehavior = this.cfg.behaviors['reorder'];

            if(reorderBehavior) {
                this.cfg.update = function(e, ui) {

                    if(this === ui.item.parent()[0]) {
                        var itemIndex = ui.item.parent().children().filter(':not(script):visible').index(ui.item),
                        receiverColumnIndex =  ui.item.parent().parent().children().index(ui.item.parent());

                        var ext = {
                            params: {}
                        }  
                        ext.params[_self.id + "_reordered"] = true;
                        ext.params[_self.id + "_widgetId"] = ui.item.attr('id');
                        ext.params[_self.id + "_itemIndex"] = itemIndex;
                        ext.params[_self.id + "_receiverColumnIndex"] = receiverColumnIndex;

                        if(ui.sender) {
                            ext.params[_self.id + "_senderColumnIndex"] = ui.sender.parent().children().index(ui.sender);
                        }

                        reorderBehavior.call(_self, e, ext);
                    }

                };
            }
        } 

        $(this.jqId + ' .ui-dashboard-column').sortable(this.cfg);
    }
    
});