/**
 * __PrimeFaces Spotlight Widget__
 * 
 * Spotlight highlights a certain component on page, drawing the user's attention to it.
 * 
 * @prop {JQuery} target The DOM element for the target component to highlight.
 * 
 * @interface {PrimeFaces.widget.SpotlightCfg} cfg The configuration for the {@link  Spotlight| Spotlight widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.active Whether the spotlight is initially turned on.
 * @prop {boolean} cfg.blockScroll `true` to block scrolling when the spotlight is turned on, or `false` otherwise.
 * @prop {string} cfg.target The search expression for the target component to highlight.
 */
PrimeFaces.widget.Spotlight = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);

        if(!$(document.body).children('.ui-spotlight').length) {
            this.createMasks();
        }

        if(this.cfg.active) {
            this.show();
        }
    },

    /**
     * Creates the mask overlay element for the spotlight effect and adds it to the DOM.
     * @private
     */
    createMasks: function() {
        var documentBody = $(document.body);
        documentBody.append('<div class="ui-widget-overlay ui-spotlight ui-spotlight-top ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-bottom ui-helper-hidden"></div>' +
                        '<div class="ui-widget-overlay ui-spotlight ui-spotlight-left ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-right ui-helper-hidden"></div>');
    },

    /**
     * Turns the spotlight on so that a certain part of the page is highlighted.
     */
    show: function() {
        this.calculatePositions();

        $(document.body).children('div.ui-spotlight').show();

        this.bindEvents();
    },

    /**
     * Computes and applies the rectangular position of the spotlight.
     * @private
     */
    calculatePositions: function() {
        var doc = $(document),
        documentBody = $(document.body),
        offset = PrimeFaces.utils.calculateRelativeOffset(this.target),
        zindex = ++PrimeFaces.zindex;

        documentBody.children('div.ui-spotlight-top').css({
            'left': '0px',
            'top': '0px',
            'width': documentBody.width() + 'px',
            'height': offset.top + 'px',
            'pointer-events' : 'none',
            'user-select': 'none',
            'z-index': zindex
        });

        var bottomTop = offset.top + this.target.outerHeight();
        documentBody.children('div.ui-spotlight-bottom').css({
            'left': '0px',
            'top': bottomTop + 'px',
            'width': documentBody.width() + 'px',
            'height': (doc.height() - bottomTop) + 'px',
            'pointer-events' : 'none',
            'user-select': 'none',
            'z-index': zindex
        });

        documentBody.children('div.ui-spotlight-left').css({
            'left': '0px',
            'top': offset.top + 'px',
            'width': offset.left + 'px',
            'height': this.target.outerHeight() + 'px',
            'pointer-events' : 'none',
            'user-select': 'none',
            'z-index': zindex
        });

        var rightLeft = offset.left + this.target.outerWidth();
        documentBody.children('div.ui-spotlight-right').css({
            'left': rightLeft + 'px',
            'top': offset.top + 'px',
            'width': (documentBody.width() - rightLeft) + 'px',
            'height': this.target.outerHeight() + 'px',
            'pointer-events' : 'none',
            'user-select': 'none',
            'z-index': zindex
        });
    },

    /**
     * Sets up all event listeners that are required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.target.data('zindex',this.target.zIndex()).css('z-index', ++PrimeFaces.zindex);

        if (this.cfg.blockScroll) {
            PrimeFaces.utils.preventScrolling();
        }
        PrimeFaces.utils.preventTabbing(this.id, $this.target.zIndex(), function() {
            return $this.target.find(':tabbable');
        });

        $(window).on('resize.spotlight scroll.spotlight', function() {
            $this.calculatePositions();
        });
    },

    /**
     * Removes the event listeners that were added when the spotlight was turned on.
     * @private
     */
    unbindEvents: function() {
        PrimeFaces.utils.enableTabbing(this.id);
        if (this.cfg.blockScroll) {
            PrimeFaces.utils.enableScrolling();
        }
        $(window).off('resize.spotlight scroll.spotlight');
    },

    /**
     * Turns of the spotlight so that the entire page is visible normally again.
     */
    hide: function() {
        $(document.body).children('.ui-spotlight').hide();
        this.unbindEvents();
        this.target.css('z-index', this.target.zIndex());
    }

});