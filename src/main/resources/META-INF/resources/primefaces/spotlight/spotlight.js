/**
 * PrimeFaces Spotlight Widget
 */
PrimeFaces.widget.Spotlight = PrimeFaces.widget.BaseWidget.extend({

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

    createMasks: function() {
        var documentBody = $(document.body);
        documentBody.append('<div class="ui-widget-overlay ui-spotlight ui-spotlight-top ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-bottom ui-helper-hidden"></div>' +
                        '<div class="ui-widget-overlay ui-spotlight ui-spotlight-left ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-right ui-helper-hidden"></div>');
    },

    show: function() {
        this.calculatePositions();

        $(document.body).children('div.ui-spotlight').show();

        this.bindEvents();
    },

    calculatePositions: function() {
        var doc = $(document),
        documentBody = $(document.body),
        offset = PrimeFaces.utils.calculateRelativeOffset(this.target);

        documentBody.children('div.ui-spotlight-top').css({
            'left': 0,
            'top': 0,
            'width': documentBody.width(),
            'height': offset.top
        });

        var bottomTop = offset.top + this.target.outerHeight();
        documentBody.children('div.ui-spotlight-bottom').css({
            'left': 0,
            'top': bottomTop,
            'width': documentBody.width(),
            'height': doc.height() - bottomTop
        });

        documentBody.children('div.ui-spotlight-left').css({
            'left': 0,
            'top': offset.top,
            'width': offset.left,
            'height': this.target.outerHeight()
        });

        var rightLeft = offset.left + this.target.outerWidth();
        documentBody.children('div.ui-spotlight-right').css({
            'left': rightLeft,
            'top': offset.top,
            'width': documentBody.width() - rightLeft,
            'height': this.target.outerHeight()
        });
    },

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

    unbindEvents: function() {
        PrimeFaces.utils.enableTabbing(this.id);
        if (this.cfg.blockScroll) {
            PrimeFaces.utils.enableScrolling();
        }
        $(window).off('resize.spotlight scroll.spotlight');
    },

    hide: function() {
        $(document.body).children('.ui-spotlight').hide();
        this.unbindEvents();
        this.target.css('z-index', this.target.zIndex());
    }

});