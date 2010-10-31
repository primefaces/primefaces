PrimeFaces.widget.ColorPicker = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.jqDialog = this.jqId + '_dialog';
	this.jqButton = this.jqId + '_button';
	
	//Create picker
	PrimeFaces.widget.ColorPicker.superclass.constructor.call(this, id + "_cpContainer", this.cfg);
	this.on('rgbChange', this.selectColor);
	
	if(this.cfg.initialValue) {
		this.setValue(this.cfg.initialValue, false);
	}
	
	this.setupUIControls();
}

YAHOO.lang.extend(PrimeFaces.widget.ColorPicker, YAHOO.widget.ColorPicker, {
	selectColor : function(event) {
		var color = event.newValue,
		value = color[0] + "," + color[1] + "," + color[2];
	
		document.getElementById(this.id + "_input").value = value;
		jQuery(this.jqId + '_livePreview').css('backgroundColor', 'rgb(' + value + ')'); 
	},
	
	setupUIControls : function() {
		//Dialog
		jQuery(this.jqDialog).dialog({autoOpen:false, resizable:false, draggable:false,  height:230, width:400});
		
		//Button
		var cp = this;
		jQuery(this.jqButton).button().click(function() {
			if(jQuery(cp.jqDialog).dialog('isOpen'))
				jQuery(cp.jqDialog).dialog('close');
			else
				jQuery(cp.jqDialog).dialog('open');
			
			jQuery(cp.jqDialog).parent().position({
				of: jQuery(this),
				my: 'left top',
				at: 'left bottom'
			});
		});
		//firefox focus fix
		jQuery(this.jqButton).mouseout(function() {
			jQuery(this).removeClass('ui-state-focus');
		});
	}
});