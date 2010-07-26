PrimeFaces.widget.ColorPicker = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.overlay = new YAHOO.widget.Panel(this.id + "_overlay", {visible:false, draggable:false, close:true, width:'405px'});
	this.cfg.container = this.overlay;
	
	PrimeFaces.widget.ColorPicker.superclass.constructor.call(this, id + "_container", this.cfg);
	this.on('rgbChange', this.selectColor);
	
	this.overlay.render();
	
	this.button = new YAHOO.widget.Button(this.id + "_button");
	this.button.addListener('click', this.toggleColorPicker, {}, this);
	
}

YAHOO.lang.extend(PrimeFaces.widget.ColorPicker, YAHOO.widget.ColorPicker,
	{
		selectColor : function(event) {
			var color = event.newValue;
			var value = color[0] + "," + color[1] + "," + color[2];
		
			document.getElementById(this.id + "_input").value = value;
			YAHOO.util.Dom.setStyle(this.id + "_currentColorDisplay", "backgroundColor", "rgb(" + value + ")"); 
		},

		toggleColorPicker : function(event) {
			var visible = this.overlay.cfg.getProperty("visible"); 
		
			if(visible)
				this.overlay.hide();
			else
				this.overlay.show();
		}
});