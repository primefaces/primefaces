if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.AccordionPanel = function(clientId, cfg) {
	PrimeFaces.widget.AccordionPanel.superclass.constructor.call(this, clientId, cfg);
	this.clientId = clientId;
	this.cfg = cfg;
	
	this.on('afterPanelClose', this.handlePanelClose, null, this);
	this.on('afterPanelOpen', this.handlePanelOpen, null, this);
}

YAHOO.lang.extend(PrimeFaces.widget.AccordionPanel, YAHOO.widget.AccordionView, {
	
	handlePanelClose : function(acco) {
		for(var i=0;i < this.cfg.expandItem.length;i++) {
			if(this.cfg.expandItem[i] == acco.index) {
				this.cfg.expandItem.splice(i, 1);
			}
		}
		
		this.saveState();
	}
	
	,handlePanelOpen : function(acco) {
		this.cfg.expandItem.push(acco.index);
		
		this.saveState();
	}
	
	,saveState : function() {
		document.getElementById(this.clientId + '_state').value = this.cfg.expandItem.join(',');
	}
});