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
    },
    
    createMasks: function() {
        $(document.body).append('<div class="ui-widget-overlay ui-spotlight ui-spotlight-top ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-bottom ui-helper-hidden"></div>' + 
                        '<div class="ui-widget-overlay ui-spotlight ui-spotlight-left ui-helper-hidden"></div><div class="ui-widget-overlay ui-spotlight ui-spotlight-right ui-helper-hidden"></div>');
    },
    
    show: function() {
        var doc = $(document),
        body = $(document.body),
        offset = this.target.offset(),
        topMask = body.children('div.ui-spotlight-top'),
        bottomMask = body.children('div.ui-spotlight-bottom'),
        leftMask = body.children('div.ui-spotlight-left'),
        rightMask = body.children('div.ui-spotlight-right');

        topMask.css({
            'left': 0,
            'top': 0,
            'width': doc.width(),
            'height': offset.top
        });
        
        var bottomTop = offset.top + this.target.outerHeight();
        bottomMask.css({
            'left': 0,
            'top': bottomTop,
            'width': doc.width(),
            'height': doc.height() - bottomTop
        });
        
        leftMask.css({
            'left': 0,
            'top': offset.top,
            'width': offset.left,
            'height': this.target.outerHeight()
        });
        
        var rightLeft = offset.left + this.target.outerWidth();
        rightMask.css({
            'left': rightLeft,
            'top': offset.top,
            'width': doc.width() - rightLeft,
            'height': this.target.outerHeight()
        });
    
        topMask.appendTo(document.body).show();
        bottomMask.appendTo(document.body).show();
        leftMask.appendTo(document.body).show();
        rightMask.appendTo(document.body).show();
    },
    
    hide: function() {
        $(document.body).children('.ui-spotlight').hide();
    }

});