/**
 * PrimeFaces Sticky Widget
 */
PrimeFaces.widget.Sticky = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
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
        win = $(window);

        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', function() {
            if(win.scrollTop() > $this.initialState.top - $this.cfg.margin)
                $this.fix();
            else
                $this.restore();
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, function() {
            if ($this.fixed) {
                $this.target.width($this.ghost.outerWidth() - ($this.target.outerWidth() - $this.target.width()));
            }
        });
    },

    fix: function(force) {
        if(!this.fixed || force) {
            var win = $(window),
            winScrollTop = win.scrollTop();

            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin,
                'z-index': ++PrimeFaces.zindex
            })
            .addClass('ui-shadow ui-sticky');

            this.ghost = $('<div class="ui-sticky-ghost"></div>').height(this.target.outerHeight()).insertBefore(this.target);
            this.target.width(this.ghost.outerWidth() - (this.target.outerWidth() - this.target.width()));
            this.fixed = true;
            win.scrollTop(winScrollTop);
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
