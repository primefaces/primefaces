/**
 * __PrimeFaces Sticky Widget__
 * 
 * Sticky component positions other components as fixed so that these components stay in window viewport during
 * scrolling.
 * 
 * @interface {PrimeFaces.widget.Sticky.InitialState} InitialState Describes some of the initial geometry of the target
 * component before it was made sticky, see {@link Sticky.initialState}.
 * @prop {number} InitialState.height The initial height of the target element. 
 * @prop {number} InitialState.top The initial position of the top edge of the target element.
 * 
 * @prop {boolean} fixed Whether this sticky is currently fixed to the top of the page.
 * @prop {JQuery} ghost The DOM element for the ghost helper element.
 * @prop {PrimeFaces.widget.Sticky.InitialState} initialState The initial position and height of the target component
 * before it was pinned to the page.
 * @prop {JQuery} target The DOM element for the component to be made sticky.
 * 
 * @interface {PrimeFaces.widget.StickyCfg} cfg The configuration for the {@link  Sticky| Sticky widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number} margin Margin to the top of the page during fixed scrolling.
 * @prop {string} target The client ID of the component to be made sticky.
 */
PrimeFaces.widget.Sticky = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));

        if(this.fixed) {
            this.ghost.remove();
            this.fix(true);
        }
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
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

    /**
     * Pins this sticky to the page so that it is always visible.
     * @param {boolean} [force] If `true`, pin the sticky irrespective of whether it is pinned already.
     */
    fix: function(force) {
        if(!this.fixed || force) {
            var win = $(window),
            winScrollTop = win.scrollTop();

            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin + 'px',
                'z-index': PrimeFaces.nextZindex()
            })
            .addClass('ui-shadow ui-sticky');

            this.ghost = $('<div class="ui-sticky-ghost"></div>').height(this.target.outerHeight()).insertBefore(this.target);
            this.target.width(this.ghost.outerWidth() - (this.target.outerWidth() - this.target.width()));
            this.fixed = true;
            win.scrollTop(winScrollTop);
        }
    },

    /**
     * Unpins this sticky and returns it to its normal position.
     */
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
