PrimeFaces.widget.MenuButton = function(clientId, cfg) {
	this.cfg = cfg;
	this.cfg.type = 'menu';
	this.cfg.menu = clientId + '_select';
	
	PrimeFaces.widget.MenuButton.superclass.constructor.call(this, clientId + '_input', this.cfg);
	
	jQuery(PrimeFaces.escapeClientId(clientId)).show();
	
	this.getMenu().subscribe('click', this.handleClick, this, true);
}

YAHOO.lang.extend(PrimeFaces.widget.MenuButton, YAHOO.widget.Button,
{
	handleClick : function (type, args) {
		var event = args[0],
		menuitem = args[1];

		if(menuitem) {
			this.cfg.commands[menuitem.index].call(this);
		}
	}
});