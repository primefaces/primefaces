/* 
 * PrimeFaces ScrollPanel Widget 
 */
PrimeFaces.widget.ScrollPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this.cfg = cfg;
        this.id = this.cfg.id;
        if(this.id) {
            this.jqId = PrimeFaces.escapeClientId(this.id);
            this.jq = $(this.jqId);
        } else {
            this.jq = this.cfg.jq;
        }

        if(this.cfg.mode != 'native') {

            var _self = this;

            if(this.jq.is(':visible')) {
                this.render();
            } 
            else {
                var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
                hiddenParentWidget = hiddenParent.data('widget');

                if(hiddenParentWidget) {
                    hiddenParentWidget.addOnshowHandler(function() {
                        return _self.render();
                    });
                }
            }
        }
        
        $(this.jqId + '_s').remove();
    },
    
    generateDOM: function() {
        this.jq.wrapInner('<div class="ui-scrollpanel-container" />');
        this.container = this.jq.children('.ui-scrollpanel-container');

        this.container.wrapInner('<div class="ui-scrollpanel-wrapper" />');
        this.wrapper = this.container.children('.ui-scrollpanel-wrapper');

        this.content.removeAttr("style").addClass('ui-scrollpanel-content');

        var hbarDOM = '<div class="ui-scrollpanel-hbar ui-widget-header ui-corner-bottom">';
        hbarDOM += '<div class="ui-scrollpanel-handle ui-state-default ui-corner-all"><span class="ui-icon ui-icon-grip-solid-vertical"></span></div>';
        hbarDOM += '<div class="ui-scrollpanel-bl ui-state-default ui-corner-bl"><span class="ui-icon ui-icon-triangle-1-w"></span></div>';
        hbarDOM += '<div class="ui-scrollpanel-br ui-state-default ui-corner-br"><span class="ui-icon ui-icon-triangle-1-e"></span></div></div>';

        var vbarDOM = '<div class="ui-scrollpanel-vbar ui-widget-header ui-corner-bottom">';
        vbarDOM += '<div class="ui-scrollpanel-handle ui-state-default ui-corner-all"><span class="ui-icon ui-icon-grip-solid-horizontal"></span></div>';
        vbarDOM += '<div class="ui-scrollpanel-bt ui-state-default ui-corner-bl"><span class="ui-icon ui-icon-triangle-1-n"></span></div>';
        vbarDOM += '<div class="ui-scrollpanel-bb ui-state-default ui-corner-br"><span class="ui-icon ui-icon-triangle-1-s"></span></div></div>';

        this.container.append(hbarDOM);
        this.container.append(vbarDOM);
    },
    
    render: function(){
        if(this.jq.is(':hidden')) {
            return false;
        }

        //look into
        this.jq.wrapInner('<div style="display:inline-block;"/>');
        this.content = this.jq.children('div');

        var containerWidth = this.jq.width(),
        containerHeight = this.jq.height(),

        contentWidth = this.content.outerWidth(true),
        contentHeight = this.content.outerHeight(true),

        xScrolled = contentWidth > containerWidth,
        yScrolled = contentHeight > containerHeight;

        //no need to scroll and unwrap
        if(!(xScrolled||yScrolled)) {
            this.content.replaceWith(this.content.html());
            return;
        }

        this.generateDOM();

        this.container.css({width: containerWidth, height: containerHeight});

        var hbar = this.container.children('.ui-scrollpanel-hbar'),
        vbar = this.container.children('.ui-scrollpanel-vbar'),
        wrapperWidth = containerWidth - (yScrolled ? vbar.width() : 0),
        wrapperHeight = containerHeight - (xScrolled ? hbar.height() : 0);
        this.wrapper.css({width: wrapperWidth, height: wrapperHeight});

        if(xScrolled){
            this.h = {
                bar  : hbar,
                hand : hbar.children('.ui-scrollpanel-handle'),
                grip : hbar.find('.ui-scrollpanel-handle > span.ui-icon-grip-solid-vertical'),
                up   : hbar.children('.ui-scrollpanel-bl'),
                down : hbar.children('.ui-scrollpanel-br'),
                wlen : wrapperWidth,
                diff : contentWidth - wrapperWidth,
                dir  : 'x'
            };

            this.initScroll(this.h);
        }

        if(yScrolled){
            this.v = {
                bar  : vbar,
                hand : vbar.children('.ui-scrollpanel-handle'),
                grip : vbar.find('.ui-scrollpanel-handle > span.ui-icon-grip-solid-horizontal'),
                up   : vbar.children('.ui-scrollpanel-bt'),
                down : vbar.children('.ui-scrollpanel-bb'),
                wlen : wrapperHeight,
                diff : contentHeight - wrapperHeight,
                dir  : 'y'
            };

            this.initScroll(this.v);
        }

        return true;
    },
    
    initScroll: function(s) {
        s.bar.css({display : 'block'});

        if(s.dir === 'x'){
            var barWidth = s.wlen - s.up.outerWidth(true) - s.down.outerWidth(true),
            scrollable = barWidth - s.hand.outerWidth(true);
            s.bar.css({width : barWidth});
            s.upLen = parseFloat(s.up.outerWidth(true));

            if( scrollable > s.diff){
                s.scrollable = s.diff;
                s.controller = s.diff;
                s.ratio = 1;
                s.hand.outerWidth((barWidth - s.diff));
                s.grip.css('margin-left', (s.hand.innerWidth() - s.grip.outerWidth(true))/2);
            }
            else{
                s.scrollable = scrollable;
                s.controller = scrollable;
                s.ratio = s.diff / scrollable;
            }
        }
        else{
            var barHeight = s.wlen - s.up.outerHeight(true) - s.down.outerHeight(true),
            scrollable = barHeight - s.hand.outerHeight(true);
            s.bar.css({height : barHeight});
            s.upLen = parseFloat(s.up.outerHeight(true));

            if( scrollable > s.diff){
                s.scrollable = s.diff;
                s.controller = s.diff;
                s.ratio = 1;
                s.hand.outerHeight((barHeight - s.diff));
                s.grip.css('margin-top', (s.hand.innerHeight() - s.grip.outerHeight(true))/2);
            }
            else{
                s.scrollable = scrollable;
                s.controller = scrollable;
                s.ratio = s.diff / scrollable;
            }
        }

        this.bindEvents(s);
    },
    
    bindEvents: function(s){
        var scroll = s, _self = this;

        //visuals
        $.each([scroll.hand, scroll.up, scroll.down], function(i, e){
            e.mouseover(function() {
                $(this).addClass('ui-state-hover');
            }).mouseout(function() {
                $(this).removeClass('ui-state-hover');
            }).mouseup(function() {
                $(this).removeClass('ui-state-active');
            }).mousedown(function() {
                $(this).addClass('ui-state-active');
            });
        });

        //wheel
        this.wrapper.bind("mousewheel",  function(event, move){ 
            if(_self.scrollWithRatio('y', move, true))
                event.preventDefault();
        });
        scroll.bar.bind("mousewheel",  function(event, move){ 
            _self.scrollWithRatio( scroll.dir, move, true);
            event.preventDefault();
        });

        var dragOffset = undefined;

        //drag
        scroll.hand.draggable({
            axis: scroll.dir,

            drag: function (e, data) {
                var p = data.position;
                dragOffset = dragOffset || p;

                if(scroll.dir === 'x'){
                    _self.scrollWithRatio('x', dragOffset.left - p.left);
                }
                else{
                    _self.scrollWithRatio('y', dragOffset.top - p.top);
                }

                dragOffset = p;
            },
            containment: "parent",
            scroll: false,
            stop: function (e) {
                $(e.target).removeClass("ui-state-active");
            }
        });

        //buttons
        var mouseInterval, mouseDown = false, mouseCount = 0;
        scroll.up.mousedown(function(e){
            mouseDown = true;
            mouseCount = 0;
            mouseInterval = setInterval(function(){
                mouseCount++;
                _self.scrollWithRatio(scroll.dir, 2, true);
            }, 10);

            e.preventDefault();
        }).mouseenter(function(){
            if(mouseDown)
                $(this).mousedown();
        }).mouseup(function(){
            mouseDown = false;
            clearInterval(mouseInterval);
        }).mouseleave(function(){
            clearInterval(mouseInterval);
            $(this).removeClass('ui-state-active');
        }).click(function(){
            if(mouseCount < 5)
                _self.scrollWithRatio(scroll.dir, 20, true)
        });

        scroll.down.mousedown(function(e){
            mouseDown = true;
            mouseCount = 0;
            mouseInterval = setInterval(function(){
                mouseCount++;
                _self.scrollWithRatio(scroll.dir, -2, true);
            }, 10);

            e.preventDefault();
        }).mouseenter(function(){
            if(mouseDown)
                $(this).mousedown();
        }).mouseup(function(){
            mouseDown = false;
            clearInterval(mouseInterval);
        }).mouseleave(function(){
            clearInterval(mouseInterval);
            $(this).removeClass('ui-state-active');
        }).click(function(){
            if(mouseCount < 5)
                _self.scrollWithRatio(scroll.dir, -20, true)
        });

        $(document.body).bind('mouseup.scrollpanel', function(){
            clearInterval(mouseInterval);
            scroll.hand.removeClass('ui-state-active');
            mouseDown = false;
        });
    },
    
    scrollTo: function(x, y) {
        this.scrollX(x);
        this.scrollY(y);
    },
    
    scrollToRatio: function(x, y, moveBars) {
        this.scrollWithRatio('x', x, moveBars === false ? false : true);
        this.scrollWithRatio('y', y, moveBars === false ? false : true);
    },
    
    checkScrollable: function(o, d){
        if( o && d){
            if(o.controller + d < 0)
                return -o.controller;
            else if(o.controller + d > o.scrollable)
                return o.scrollable - o.controller;
            else
                return d;
        }
        return 0;
    },
    
    scrollWithRatio: function(dir, d, wheel){
        if(dir === 'x'){
            d = this.checkScrollable(this.h, d);

            //invalid move
            if(!d) return false;

            this.h.controller += d;
            var scrolled = this.h.scrollable - this.h.controller,
            newLeft = -scrolled * this.h.ratio;

            this.content.css({left : newLeft});

            if(wheel){
                this.h.hand.css({left : this.h.upLen + scrolled});
            }
        }
        else{
            d = this.checkScrollable(this.v, d);

            //invalid move
            if(!d) return false;

            this.v.controller += d;
            var scrolled = this.v.scrollable - this.v.controller,
            newTop = -scrolled * this.v.ratio;

            this.content.css({top : newTop});

            if(wheel){
                this.v.hand.css({top : this.v.upLen + scrolled});
            }
        }

        return true;
    },
    
    scrollX: function(x){
        this.content.css({left : typeof(x) == 'string' ? x : -x});
    },
    
    scrollY: function(y){
        this.content.css({top : typeof(y) == 'string' ? y : -y});
    }
 
});