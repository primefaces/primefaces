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
            width: this.target.width(),
            height: this.target.height()
        };
        
        var win = $(window),
        $this = this;
        
        
        $(window).on('scroll.' + this.cfg.id, function() {
            if(win.scrollTop() > $this.initialState.top) {
                $this.fix();
            }
            else {
                $this.restore();
            }
           
        });

        var offset = this.target.offset();
        var offsetLeft = offset.left;
        var offsetRight = win.width() - (offsetLeft + this.target.width());
        
        $(window).resize(function() {
            $this.target.css({'width': win.width()-(offsetLeft+offsetRight)});
        });
    },
            
    refresh: function(cfg) {
        $(window).off('scroll.' + this.cfg.id);
        
        this.init(cfg);
    },
            
    fix: function() {
        if(!this.fixed) {
            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin,
                'z-index': ++PrimeFaces.zindex,
                'width': this.initialState.width
            })
            .addClass('ui-shadow ui-sticky');
            
            $('<div class="ui-sticky-ghost"></div>').height(this.initialState.height).insertBefore(this.target);
            
            this.fixed = true;
        }
    },
        
            
    restore: function() {
        if(this.fixed) {
            this.target.css({
                'position': 'static',
                'top': 'auto',
                'width': this.initialState.width
            })
            .removeClass('ui-shadow ui-sticky');
            
            this.target.prev('.ui-sticky-ghost').remove();
        
            this.fixed = false;
        }
    }
    
});