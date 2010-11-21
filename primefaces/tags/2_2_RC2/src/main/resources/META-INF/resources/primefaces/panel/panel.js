/**
 * PrimeFaces Panel Widget
 */
PrimeFaces.widget.Panel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(id);

    if(this.cfg.toggleable) {
        this.toggler = jQuery(this.jqId + '_toggler');
        this.toggleStateHolder = jQuery(this.jqId + '_collapsed');
        this.content = jQuery(this.jqId + '_content');
    }

    if(this.cfg.closable) {
        this.visibleStateHolder = jQuery(this.jqId + "_visible");
    }
	
    if(!this.cfg.visible) {
        jQuery(this.jqId).css('display','none');
    }
}

PrimeFaces.widget.Panel.prototype.toggle = function() {
    if(this.cfg.collapsed) {
        this.toggler.removeClass('ui-icon-plusthick').addClass('ui-icon-minusthick');
        this.cfg.collapsed = false;
        this.toggleStateHolder.val(false);
    }
    else {
        this.toggler.removeClass('ui-icon-minusthick').addClass('ui-icon-plusthick');
        this.cfg.collapsed = true;
        this.toggleStateHolder.val(true);
    }
	
    var _self = this;

    this.content.slideToggle(this.cfg.toggleSpeed,
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
    this.visibleStateHolder.val(false);

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
            if(_self.cfg.onCloseComplete) {
                _self.cfg.onCloseComplete.call();
            }
        });
    }
}

PrimeFaces.widget.Panel.prototype.show = function() {
    jQuery(this.jqId).fadeIn(this.cfg.closeSpeed);
	
    this.visibleStateHolder.val(true);
}