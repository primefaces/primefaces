PrimeFaces.widget.Layout = function(el, cfg) {
	PrimeFaces.widget.Layout.superclass.constructor.call(this, el, cfg);
	
	if(YAHOO.lang.isString(el)) {
		this.id = el;
		this.cfg = cfg;
	} else {
		this.id = el.clientId;
		this.cfg = el;
	}
	
	if(this.cfg.closeTitle) YAHOO.widget.LayoutUnit.prototype.STR_CLOSE = this.cfg.closeTitle;
	if(this.cfg.collapseTitle) YAHOO.widget.LayoutUnit.prototype.STR_COLLAPSE = this.cfg.collapseTitle;
	if(this.cfg.expandTitle) YAHOO.widget.LayoutUnit.prototype.STR_EXPAND = this.cfg.expandTitle;
	
	this.render();
	this.setState();
	this.setupEventHandlers();
}

YAHOO.lang.extend(PrimeFaces.widget.Layout, YAHOO.widget.Layout,
{
	setupEventHandlers : function() {
		for(var unitName in this.getUnits()) {
			if(unitName != undefined) {
				var unit = this.getUnitByPosition(unitName);
				
				if(this.cfg.ajaxToggle) {
					unit.subscribe('collapse', this.handleToggle, {layoutUnit:unit, collapsed: true}, this);
					unit.subscribe('expand', this.handleToggle, {layoutUnit:unit, collapsed: false}, this);
				}
				
				if(this.cfg.ajaxClose) {
					unit.subscribe('close', this.handleClose, {layoutUnit:unit}, this);
				}
				
				if(this.cfg.ajaxResize) {
					unit.subscribe('endResize', this.handleResize, {layoutUnit:unit}, this);
				}
			}
			
		}
	},
	
	handleToggle : function(e, args) {
		var params = {};
		params[this.id + "_toggled"] = true;
		params[this.id + "_collapsed"] = args.collapsed;
		params[this.id + "_unit"] = args.layoutUnit.get('position');
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
		
		if(this.cfg.onToggleUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.onToggleUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(this.cfg.url, {}, params);
	},

	handleClose : function(e, args) {
		var params = {};
		params[this.id + "_closed"] = true;
		params[this.id + "_unit"] = args.layoutUnit.get('position');
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
		
		if(this.cfg.onCloseUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.onCloseUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(this.cfg.url, {}, params);
	},
	
	handleResize : function(e, args) {
		var params = {};
		params[this.id + "_resized"] = true;
		params[this.id + "_unit"] = args.layoutUnit.get('position');
		params[this.id + "_unitWidth"] = args.layoutUnit.getSizes().wrap.w;
		params[this.id + "_unitHeight"] = args.layoutUnit.getSizes().wrap.h;
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
		
		if(this.cfg.onResizeUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.onResizeUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(this.cfg.url, {}, params);
	},
	
	setState : function() {
		for(var unitName in this.getUnits()) {
			if(unitName != undefined) {
				var unit = this.getUnitByPosition(unitName),
				unitConfig = this.getUnitConfig(unitName);
				
				if(!unitConfig.visible)
					unit.close();
				if(unitConfig.collapsed)
					unit.collapse();
			}
		}
	},
	
	getUnits : function() {
		return this._units;
	},
	
	getUnitConfig : function(position) {
		for(var i=0; i < this.cfg.units.length; i++) {
			var unitConfig = this.cfg.units[i];
			
			if(unitConfig.position === position)
				return unitConfig;
		}
		
		return null;
	}
});