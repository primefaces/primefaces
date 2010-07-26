PrimeFaces.widget.Panel = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.bodySelector = this.jqId + "_content";
	this.togglerSelector = this.jqId + "_toggler";
	this.toggleStateHolder = this.jqId + "_collapsed";
	this.visibleStateHolder = this.jqId + "_visible";
	this.cfg = cfg;
	
	if(!this.cfg.visible) {
		jQuery(this.jqId).css('display','none');
	}
}

PrimeFaces.widget.Panel.prototype.toggle = function() {
	
	if(this.cfg.collapsed) {
		jQuery(this.togglerSelector).removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
		this.cfg.collapsed = false;
		jQuery(this.toggleStateHolder).val(false);
	}
	else {
		jQuery(this.togglerSelector).removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');
		this.cfg.collapsed = true;
		jQuery(this.toggleStateHolder).val(true);
	}
	
	var scope = this;
	jQuery(this.bodySelector).slideToggle(this.cfg.toggleSpeed, 
			function() {
				if(scope.cfg.ajaxToggle) {
					var params = {};
					params[scope.id + "_toggled"] = true;
					params[scope.id + "_collapsed"] = scope.cfg.collapsed;
					params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.id;
					
					if(scope.cfg.onToggleUpdate) {
						params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.onToggleUpdate;
					}
					
					PrimeFaces.ajax.AjaxRequest(
								scope.cfg.url,{
							}, 
							params);
				}
			});
}

PrimeFaces.widget.Panel.prototype.close = function() {
	var scope = this;
	
	if(this.cfg.onCloseStart) {
		this.cfg.onCloseStart.call();
	}
	
	if(this.cfg.ajaxClose) {
		jQuery(this.jqId).fadeOut(this.cfg.closeSpeed, 
				function() {
					var params = {};
					params[scope.id + "_closed"] = true;
					params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.id;
					
					if(scope.cfg.onCloseUpdate) {
						params[PrimeFaces.PARTIAL_UPDATE_PARAM] = scope.cfg.onCloseUpdate;
					}
					
					PrimeFaces.ajax.AjaxRequest(
								scope.cfg.url, {
								oncomplete: function() {
									if(scope.cfg.onCloseComplete) {
										scope.cfg.onCloseComplete.call();
									}
								}
							}, 
							params);
		});
	} else {
		jQuery(this.jqId).fadeOut(this.cfg.closeSpeed, function() {
			if(scope.cfg.onCloseComplete) {
				scope.cfg.onCloseComplete.call();
			}
		});
	}
	
	jQuery(this.visibleStateHolder).val(false);
}

PrimeFaces.widget.Panel.prototype.show = function() {
	jQuery(this.jqId).fadeIn(this.cfg.closeSpeed);
	
	jQuery(this.visibleStateHolder).val(true);
}