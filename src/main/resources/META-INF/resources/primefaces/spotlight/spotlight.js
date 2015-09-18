/**
 * PrimeFaces Spotlight Widget
 */
PrimeFaces.widget.Spotlight = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.target = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.target);
        this.eventsToBlock = 'focus.' + this.id + ' mousedown.' + this.id + ' mouseup.' + this.id;
    
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
        offset = this.target.offset();
        
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
        
        $(document).on('keydown.' + this.id,
                function(event) {
                    var target = $(event.target);

                    if(event.keyCode === $.ui.keyCode.TAB) {
                        var tabbables = $this.target.find(':tabbable');
                        if(tabbables.length) {
                            var first = tabbables.filter(':first'),
                            last = tabbables.filter(':last');
                    
                            if(target.is(document.body)) {
                                first.focus(1);
                                event.preventDefault();
                            }
                            else if(event.target === last[0] && !event.shiftKey) {
                                first.focus(1);
                                event.preventDefault();
                            } 
                            else if (event.target === first[0] && event.shiftKey) {
                                last.focus(1);
                                event.preventDefault();
                            }
                        }
                    }
                    else if(!target.is(document.body) && (target.zIndex() < $this.target.zIndex())) {
                        event.preventDefault();
                    }
                })
                .on(this.eventsToBlock, function(event) {
                    if ($(event.target).zIndex() < $this.target.zIndex()) {
                        event.preventDefault();
                    }
                });
                
        $(window).on('resize.spotlight', function() {
            $this.calculatePositions();
        });
    },
    
    unbindEvents: function() {
        $(document).off(this.eventsToBlock).off('keydown.' + this.id);
        $(window).off('resize.spotlight');
    },
    
    hide: function() {
        $(document.body).children('.ui-spotlight').hide();
        this.unbindEvents();
        this.target.css('z-index', this.target.zIndex());
    }

});