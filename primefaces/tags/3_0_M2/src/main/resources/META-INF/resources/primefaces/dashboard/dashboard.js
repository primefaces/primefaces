/**
 * PrimeFaces Dashboard Widget
 */
PrimeFaces.widget.Dashboard = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	this.cfg.connectWith = this.COLUMN_CLASS;
	this.cfg.placeholder = this.PLACEHOLDER_CLASS;
	this.cfg.forcePlaceholderSize = true;
	this.cfg.revert=true;
	this.jq = $(this.jqId + " " + this.COLUMN_CLASS);

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
                    ext.params[id + "_reordered"] = true;
                    ext.params[id + "_widgetId"] = ui.item.attr('id');
                    ext.params[id + "_itemIndex"] = itemIndex;
                    ext.params[id + "_receiverColumnIndex"] = receiverColumnIndex;

                    if(ui.sender) {
                        ext.params[id + "_senderColumnIndex"] = ui.sender.parent().children().index(ui.sender);
                    }

                    reorderBehavior.call(_self, e, ext);
                }
                
            };
        }
    } 
	
	
	this.jq.sortable(this.cfg);
}

PrimeFaces.widget.Dashboard.prototype.COLUMN_CLASS = '.ui-dashboard-column';

PrimeFaces.widget.Dashboard.prototype.PLACEHOLDER_CLASS = 'ui-state-hover';
