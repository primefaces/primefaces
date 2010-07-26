PrimeFaces.widget.Keyboard = function(id, options) {
	this.id = PrimeFaces.escapeClientId(id);
	this.cfg = options;

	if(options.layoutTemplate != undefined)
		this.cfg.layout = PrimeFaces.widget.KeyboardUtils.createLayoutFromTemplate(options.layoutTemplate);
	else
		this.cfg.layout = PrimeFaces.widget.KeyboardUtils.getPresetLayout(options.layoutName);
	
	jQuery(this.id).keypad(this.cfg);
}


PrimeFaces.widget.KeyboardUtils = {

	layouts : {
		qwertyBasic : 
		 	[jQuery.keypad.qwertyAlphabetic[0] + jQuery.keypad.CLOSE,  
			jQuery.keypad.HALF_SPACE + jQuery.keypad.qwertyAlphabetic[1] + 
			jQuery.keypad.HALF_SPACE + jQuery.keypad.CLEAR, 
			jQuery.keypad.SPACE + jQuery.keypad.qwertyAlphabetic[2] + 
			jQuery.keypad.SHIFT + jQuery.keypad.BACK],
		
		qwerty : jQuery.keypad.qwertyLayout,
		
		alphabetic :
			['abcdefghij' + jQuery.keypad.CLOSE, 
	        'klmnopqrst' + jQuery.keypad.CLEAR, 
	        'uvwxyz' + jQuery.keypad.SPACE + jQuery.keypad.SPACE + 
	        jQuery.keypad.SHIFT + jQuery.keypad.BACK]
	},

	controls : {
		close : jQuery.keypad.CLOSE,
		clear : jQuery.keypad.CLEAR,
		back : jQuery.keypad.BACK,
		shift : jQuery.keypad.SHIFT,
		spacebar : jQuery.keypad.SPACE_BAR,
		space : jQuery.keypad.SPACE,
		halfspace : jQuery.keypad.HALF_SPACE,
		
	},

	getPresetLayout : function(name) {
		return this.layouts[name];
	},
	
	getPresetControl : function(name) {
		return this.controls[name];
	},
	
	isDefinedControl : function(key) {
		return this.controls[key] != undefined;
	},

	createLayoutFromTemplate : function(template) {
		var lines = template.split(','),
		template = new Array(lines.length);
		
		for(var i = 0; i < lines.length;i++) {
			template[i] = "";
			var lineControls = lines[i].split('-');

			for(var j = 0; j < lineControls.length;j++) {
				if(this.isDefinedControl(lineControls[j]))
					template[i] = template[i] + this.getPresetControl(lineControls[j])
				else
					template[i] = template[i] + lineControls[j];
			}
		}
		
		return template;
	}
		
}; 