PrimeFaces.widget.Panel = function(id, cfg) {
    this.id = id;
    this.jqId = PrimeFaces.escapeClientId(id);
    this.cfg = cfg;
	
    if(!this.cfg.visible) {
        jQuery(this.jqId).css('display','none');
    }
}

PrimeFaces.widget.Panel.prototype.toggle = function() {
    var togglerIcon = this.jqId + '_toggler',
    toggleStateHolder = this.jqId + '_collapsed',
    content = this.jqId + "_content";
    
    if(this.cfg.collapsed) {
        jQuery(togglerIcon).removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        this.cfg.collapsed = false;
        jQuery(toggleStateHolder).val(false);
    }
    else {
        jQuery(togglerIcon).removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');
        this.cfg.collapsed = true;
        jQuery(toggleStateHolder).val(true);
    }
	
    var _self = this;

    jQuery(content).slideToggle(this.cfg.toggleSpeed,
        function() {
            if(_self.cfg.ajaxToggle) {
                var options = {
                    source: _self.id,
                    process: _self.id
                };

                if(_self.cfg.onToggleUpdate) {
                   options.update = _self.cfg.onToggleUpdate;
                }
                
                var params = {};
                params[_self.id + "_ajaxToggle"] = true;
                params[_self.id + "_collapsed"] = _self.cfg.collapsed;
				
                PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
                
            }
        });
}

PrimeFaces.widget.Panel.prototype.close = function() {
    jQuery(this.jqId + "_visible").val(false);

    if(this.cfg.onCloseStart) {
        this.cfg.onCloseStart.call();
    }

    var _self = this;

    if(this.cfg.ajaxClose) {
        jQuery(this.jqId).fadeOut(this.cfg.closeSpeed,
            function() {
                var options = {
                    source: _self.id,
                    process: _self.id
                };

                if(_self.cfg.onCloseUpdate) {
                   options.update = _self.cfg.onCloseUpdate;
                }

                if(_self.cfg.onCloseComplete) {
                    options.oncomplete = function() {
                        _self.cfg.onCloseComplete.call();
                    };
                }

                var params = {};
                params[_self.id + "_ajaxClose"] = true;

                PrimeFaces.ajax.AjaxRequest(_self.cfg.url, options, params);
                
            });
    } else {

        jQuery(this.jqId).fadeOut(this.cfg.closeSpeed, function() {
            if(scope.cfg.onCloseComplete) {
                scope.cfg.onCloseComplete.call();
            }
        });
    }
}

PrimeFaces.widget.Panel.prototype.show = function() {
    jQuery(this.jqId).fadeIn(this.cfg.closeSpeed);
	
    jQuery(this.visibleStateHolder).val(true);
}