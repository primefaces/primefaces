/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menubar = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId + '_menu');

    if(!this.cfg.autoSubmenuDisplay) {
        this.cfg.trigger = this.jqId + ' li';
        this.cfg.triggerEvent = 'click';
    }

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jq.wijmenu('deactivate');
    };

    this.jq.wijmenu(this.cfg);
}

/**
 * PrimeFaces Menubar Widget
 */
PrimeFaces.widget.Menu = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    this.cfg.orientation = 'vertical';

    if(this.cfg.position == 'dynamic') {
        this.cfg.position = {
            my: this.cfg.my
            ,at: this.cfg.at
        }

        this.cfg.trigger = PrimeFaces.escapeClientId(this.cfg.trigger);
    }

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jq.wijmenu('deactivate');
    };

    this.jq.wijmenu(this.cfg);

    this.element = this.jq.parent().parent();   //main container element
    this.element.css('z-index', this.cfg.zindex);

    if(this.cfg.style)
        this.element.attr('style', this.cfg.style);

    if(this.cfg.styleClass)
        this.element.addClass(this.cfg.styleClass);
}

/*
 * PrimeFaces MenuButton Widget
 */
PrimeFaces.widget.MenuButton = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(id);
    this.jqbutton = jQuery(this.jqId + '_button');
    this.jqMenu = jQuery(this.jqId + '_menu');

    //menu options
    this.cfg.trigger = this.jqId + '_button';
    this.cfg.orientation = 'vertical';
    this.cfg.position = {
        my: 'left top'
        ,at: 'left bottom'
    };

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jqMenu.wijmenu('deactivate');
    };

    //crete button and menu
    this.jqbutton.button({icons:{primary:'ui-icon-triangle-1-s'}});
    this.jqMenu.wijmenu(this.cfg);

    if(this.cfg.disabled) {
        this.jqbutton.button('disable');
    }

    this.jqMenu.parent().parent().css('z-index', this.cfg.zindex);
}

/*
 * PrimeFaces ContextMenu Widget
 */
PrimeFaces.widget.ContextMenu = function(id, cfg) {
	this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    //mouse tracking
    if(!PrimeFaces.widget.ContextMenu.mouseTracking) {
        PrimeFaces.widget.ContextMenu.mouseTracking = true;

        jQuery(document).mousemove(function(e){
            PrimeFaces.widget.ContextMenu.pageX = e.pageX;
            PrimeFaces.widget.ContextMenu.pageY = e.pageY;
        });
    }

    //configuration
    this.cfg.orientation = 'vertical';
    this.cfg.triggerEvent = 'rtclick';
    this.cfg.trigger = typeof this.cfg.target == 'string' ? PrimeFaces.escapeClientId(this.cfg.target) : this.cfg.target;

    this.cfg.position = {
            my: 'left top'
            ,using: function(to) {
                jQuery(this).css({
                    top: PrimeFaces.widget.ContextMenu.pageY,
                    left: PrimeFaces.widget.ContextMenu.pageX
                });
            }
        }

    var _self = this;
    this.cfg.select = function(event, ui) {
        _self.jq.wijmenu('deactivate');
    };

    this.jq.wijmenu(this.cfg);

    this.element = this.jq.parent().parent();   //main container element
    this.element.css('z-index', this.cfg.zindex);

    if(this.cfg.style)
        this.element.attr('style', this.cfg.style);

    if(this.cfg.styleClass)
        this.element.addClass(this.cfg.styleClass);
}