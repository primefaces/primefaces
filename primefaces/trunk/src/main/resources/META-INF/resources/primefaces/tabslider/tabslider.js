PrimeFaces.widget.TabSlider = function(elId, cfg) {
	this.id = PrimeFaces.escapeClientId(elId);
	this.cfg = cfg;
	this.cfg.autoPlay = false;
	this.cfg.navigationFormatter = this.formatNavigationText;

	jQuery(this.id).anythingSlider(this.cfg);
	
	if(this.cfg.activeIndex != 1)
		this.showTab(this.cfg.activeIndex);
}

PrimeFaces.widget.TabSlider.prototype.showTab = function(index) {
	jQuery(this.id).anythingSlider(index);
}

PrimeFaces.widget.TabSlider.prototype.formatNavigationText = function(index, panel) {
	return this.navHeaders[index-1];
}