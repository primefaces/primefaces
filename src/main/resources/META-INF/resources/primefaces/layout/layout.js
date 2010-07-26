if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Layout = function(targetEl, cfg) {
	this.cfg = cfg;
	this.target = targetEl == 'body' ? 'body' : PrimeFaces.escapeClientId(targetEl);

	this.cfg.defaults = {
		applyDefaultStyles: false 
		,paneClass: 'pf-layout-pane'
		,resizerClass: 'pf-layout-resizer'
		,togglerClass: 'pf-layout-toggler'
		,buttonClass: 'pf-layout-button'
	};
	
	jQuery(this.target).layout(this.cfg);
}