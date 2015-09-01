/**
 * PrimeFaces Sticky Widget
 */
PrimeFaces.widget.Sticky = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.cfg.margin = this.cfg.margin||0;

        this.initialState = {
            top: this.target.offset().top,
            height: this.target.height()
        };

        this.bindEvents();
    },
    
    refresh: function(cfg) {        
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
        
        if(this.fixed) {
            this.ghost.remove();
            this.fix(true);
        }
    },
    
    bindEvents: function() {
        var $this = this,
        win = $(window),
        scrollNS = 'scroll.' + this.cfg.id,
        resizeNS = 'resize.' + this.cfg.id;

        win.off(scrollNS).on(scrollNS, function() {
            if(win.scrollTop() > $this.initialState.top)
                $this.fix();
            else
                $this.restore();
        })
        .off(resizeNS).on(resizeNS, function() {
            if ($this.fixed) {
                $this.target.width($this.ghost.outerWidth() - ($this.target.outerWidth() - $this.target.width()));
            }
        });
    },
    
    fix: function(force) {
        if(!this.fixed || force) {
            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin,
                'z-index': ++PrimeFaces.zindex
            })
            .addClass('ui-shadow ui-sticky');

            this.ghost = $('<div class="ui-sticky-ghost"></div>').height(this.initialState.height).insertBefore(this.target);
            this.target.width(this.ghost.outerWidth() - (this.target.outerWidth() - this.target.width()));
            this.fixed = true;
        }
    },
    
    restore: function() {
        if(this.fixed) {
            this.target.css({
                position: 'static',
                top: 'auto',
                width: 'auto'
            })
            .removeClass('ui-shadow ui-sticky');

            this.ghost.remove();
            this.fixed = false;
        }
    }

});   