/**
 * PrimeFaces Tooltip Widget
 */
PrimeFaces.widget.Tooltip = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.cfg.showEvent = this.cfg.showEvent ? this.cfg.showEvent + '.tooltip' : 'mouseover.tooltip';
        this.cfg.hideEvent = this.cfg.hideEvent ? this.cfg.hideEvent + '.tooltip' : 'mouseout.tooltip';
        this.cfg.showEffect = this.cfg.showEffect ? this.cfg.showEffect : 'fade';
        this.cfg.hideEffect = this.cfg.hideEffect ? this.cfg.hideEffect : 'fade';
        
        if(this.cfg.target)
            this.bindTarget();
        else
            this.bindGlobal();
        
        $(this.jqId + '_s').remove();
    },
    
    refresh: function(cfg) {
        if(cfg.target) {
            $(document.body).children(PrimeFaces.escapeClientId(cfg.id)).remove();
        } else {
            $(document.body).children('.ui-tooltip-global').remove();
        }
        
        this._super(cfg);
    },
    
    bindGlobal: function() {
        this.jq = $('<div class="ui-tooltip ui-tooltip-global ui-widget ui-widget-content ui-corner-all ui-shadow" />').appendTo('body');
        this.globalSelector = 'a,:input,:button';
        var $this = this;
        
        $(document).off(this.cfg.showEvent + ' ' + this.cfg.hideEvent, this.globalSelector)
                    .on(this.cfg.showEvent, this.globalSelector, function() {
                        var element = $(this),
                        title = element.attr('title');

                        if(title) {
                            $this.jq.text(title);
                            $this.globalTitle = title;
                            $this.target = element;
                            element.removeAttr('title');
                            $this.show();
                        }
                    })
                    .on(this.cfg.hideEvent + '.tooltip', this.globalSelector, function() {
                        var element = $(this);

                        if($this.globalTitle) {
                            $this.jq.hide();
                            element.attr('title', $this.globalTitle);
                            $this.globalTitle = null;
                            $this.target = null;
                        }
                    });
                    
        var resizeNS = 'resize.tooltip';
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });
    },
    
    bindTarget: function() {
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jq = $(this.jqId);
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
        
        var $this = this;
        this.target.off(this.cfg.showEvent + ' ' + this.cfg.hideEvent)
                    .on(this.cfg.showEvent, function() {
                        $this.show();
                    })
                    .on(this.cfg.hideEvent + '.tooltip', function() {
                        $this.hide();
                    });

        this.jq.appendTo(document.body);

        if($.trim(this.jq.html()) == '') {
            this.jq.html(this.target.attr('title'));
        }

        this.target.removeAttr('title');
        
        var resizeNS = 'resize.' + this.id;
        $(window).unbind(resizeNS).bind(resizeNS, function() {
            if($this.jq.is(':visible')) {
                $this.align();
            }
        });
    },

    align: function() {
        this.jq.css({
            left:'', 
            top:'',
            'z-index': ++PrimeFaces.zindex
        })
        .position({
            my: 'left top',
            at: 'right bottom',
            of: this.target
        });
    },
    
    show: function() {
        var _self = this;

        this.timeout = setTimeout(function() {
            _self.align();
            _self.jq.show(_self.cfg.showEffect, {}, 400);
        }, 150);
    },
    
    hide: function() {
        clearTimeout(this.timeout);

        this.jq.hide(this.cfg.hideEffect, {}, 400, function() {
            $(this).css('z-index', '');
        });
    }
    
});