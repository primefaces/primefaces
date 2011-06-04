PrimeFaces.widget.Dashboard = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	this.cfg.connectWith = this.COLUMN_CLASS;
	this.cfg.placeholder = this.PLACEHOLDER_CLASS;
	this.cfg.forcePlaceholderSize = true;
	this.cfg.revert=true;
	this.jq = this.jqId + " " + this.COLUMN_CLASS;

    var _self = this;
	
	this.cfg.update= function(e, ui) {
		if(this === ui.item.parent()[0]) {
			var itemIndex = ui.item.parent().children().filter(':not(script):visible').index(ui.item),
			receiverColumnIndex =  ui.item.parent().parent().children().index(ui.item.parent());

            var options = {
                source: _self.id,
                process: _self.id
            };

            if(cfg.onReorderUpdate) {
				options.update = _self.cfg.onReorderUpdate;
			}


			var params = {};
			params[id + "_reordered"] = true;
			params[id + "_widgetId"] = ui.item.attr('id');
			params[id + "_itemIndex"] = itemIndex;
			params[id + "_receiverColumnIndex"] = receiverColumnIndex;
			
			if(ui.sender) {
				params[id + "_senderColumnIndex"] = ui.sender.parent().children().index(ui.sender);
            }

            options.params = params;
			
			PrimeFaces.ajax.AjaxRequest(options);
		}
	};
	
	jQuery(this.jq).sortable(this.cfg);
}

PrimeFaces.widget.Dashboard.prototype.COLUMN_CLASS = '.ui-dashboard-column';

PrimeFaces.widget.Dashboard.prototype.PLACEHOLDER_CLASS = 'ui-state-hover';
