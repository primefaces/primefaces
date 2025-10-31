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
 * @prop {number} cfg.margin Margin to the top of the page during fixed scrolling.
 * @prop {string} cfg.stickyTopAt Selector for elements fixed at the top of the page whose height should be considered
 * when positioning the sticky element.
 * @prop {string} cfg.target The client ID of the component to be made sticky.
 */
PrimeFaces.widget.Sticky = class Sticky extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.cfg.margin = this.cfg.margin || 0;
        this.cfg.stickyTopAt = this.cfg.stickyTopAt || null;

        this.initialState = {
            top: this.target.offset().top,
            height: this.target.height()
        };

        this.calculateDynamicMargin();

        this.bindEvents();
    }

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh(cfg) {
        this.target = $(PrimeFaces.escapeClientId(this.cfg.target));

        if (this.fixed) {
            this.ghost.remove();
            this.fix(true);
        }
    }

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents() {
        var $this = this;

        PrimeFaces.utils.registerScrollHandler(this, 'scroll.' + this.id + '_align', function() {
            if ($(window).scrollTop() > $this.initialState.top - $this.cfg.margin)
                $this.fix();
            else
                $this.restore();
        });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', null, function () {
            var marginChanged = $this.calculateDynamicMargin();
            $this.initialState.top = ($this.ghost && $this.ghost.length && $this.ghost.offset()
                    && $this.ghost.offset().top > 0)
                    ? $this.ghost.offset().top
                    : ($this.target.offset() ? $this.target.offset().top : $this.initialState.top);
            if ($(window).scrollTop() > $this.initialState.top - $this.cfg.margin) {
                if (marginChanged && $this.fixed) {
                    $this.ghost.remove();
                    $this.fix(true);
                } else {
                    $this.fix();
                }
            } else {
                $this.restore();
            }
            if ($this.fixed) {
                $this.target.width($this.ghost.outerWidth() - ($this.target.outerWidth() - $this.target.width()));
            }
        });
    }

    /**
     * Pins this sticky to the page so that it is always visible.
     * @param {boolean} [force] If `true`, pin the sticky irrespective of whether it is pinned already.
     */
    fix(force) {
        if (!this.fixed || force) {
            var winScrollTop = $(window).scrollTop();

            this.target.css({
                'position': 'fixed',
                'top': this.cfg.margin + 'px',
                'z-index': PrimeFaces.utils.nextStickyZindex()
            })
            .addClass('ui-shadow ui-sticky');

            this.ghost = $('<div class="ui-sticky-ghost"></div>').height(this.target.outerHeight()).insertBefore(this.target);
            this.target.width(this.ghost.outerWidth() - (this.target.outerWidth() - this.target.width()));
            this.fixed = true;
            $(window).scrollTop(winScrollTop);
        }
    }

    /**
     * Unpins this sticky and returns it to its normal position.
     */
    restore() {
        if (this.fixed) {
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

    /**
     * @return {boolean} true if the margin has been changed, false otherwise
     */
    calculateDynamicMargin() {
        var fixedElementsOnTop = this.cfg.stickyTopAt ? $(this.cfg.stickyTopAt) : null;
        var previousMargin = this.cfg.margin;
        if (fixedElementsOnTop && fixedElementsOnTop.length) {
            this.cfg.margin = fixedElementsOnTop.toArray().reduce(function(height, elem) {
                return height + $(elem).outerHeight();
            }, 0);
        }
        return previousMargin !== this.cfg.margin;
    }

}
