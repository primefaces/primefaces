/* 
 * CommandButton
 */
PrimeFaces.widget.CommandButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = jQuery(this.jqId);
	
	this.jq.button(this.cfg);
	
	//firefox focus fix
	this.jq.mouseout(function() {
		jQuery(this).removeClass('ui-state-focus');
	});
}

PrimeFaces.widget.CommandButton.prototype.disable = function() {
    this.jq.button('disable');
}

PrimeFaces.widget.CommandButton.prototype.enable = function() {
    this.jq.button('enable');
}

/* 
 * LinkButton
 * @deprecated
 */
PrimeFaces.widget.LinkButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	
	jQuery(this.jqId).button(this.cfg);
}

/*
 * Button
 */
PrimeFaces.widget.Button = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);

	jQuery(this.jqId).button(this.cfg);
}


/* 
 * MenuButton
 */
PrimeFaces.widget.MenuButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.menuId = this.id + '_menu'
	
	this.cfg.icons = {primary: 'ui-icon-triangle-1-s'};

	jQuery(this.jqId + "_button").button(this.cfg);

    var manager = YAHOO.widget.MenuManager;
    if(manager.getMenu(this.menuId)) {
        manager.removeMenuWithId(this.menuId);
    }
	
	this.menu = new YAHOO.widget.Menu(id + '_menu', {
		itemData : this.cfg.items,
		context: [id + '_button', 'tl', 'bl', ['beforeShow', 'windowResize']],
		effect: {
			effect: YAHOO.widget.ContainerEffect.FADE,
			duration: 0.25
		}
	});

    if(this.cfg.appendToBody)
        this.menu.render(document.body);
    else
        this.menu.render(this.id + '_menuContainer');
}

PrimeFaces.widget.MenuButton.prototype.showMenu = function() {
	this.menu.show();
}

PrimeFaces.widget.MenuButton.prototype.hide = function() {
	this.menu.hide();
}

PrimeFaces.widget.MenuButton.prototype.getMenu = function() {
	return this.menu;
}

/*
 * CheckButton
 */
PrimeFaces.widget.CheckButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jq = this.jqId + '_input';

	jQuery(this.jq).button(this.cfg);
}