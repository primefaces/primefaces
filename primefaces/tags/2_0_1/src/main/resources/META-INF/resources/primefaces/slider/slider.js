PrimeFaces.widget.SliderUtils = {
		
	handleSlide : function(offset, cfg) {
		var conversionFactor = cfg.minMaxDiff / cfg.bgWidth;
		
		document.getElementById(cfg.elId).value = Math.round(offset * conversionFactor) + cfg.minValue;
	}
};