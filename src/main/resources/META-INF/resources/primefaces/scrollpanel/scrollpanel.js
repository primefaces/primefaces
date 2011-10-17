/* 
 * PrimeFaces ScrollPanel Widget 
 */
PrimeFaces.widget.ScrollPanel = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

    this.container = this.jq.children('.ui-scrollpanel-container');
    this.wrapper = this.container.children('.ui-scrollpanel-wrapper');
    this.content = this.wrapper.children('.ui-scrollpanel-content');
    
    var _self = this;
    
    if(this.jq.is(':visible')) {
        this.init();
    } 
    else {
        var hiddenParent = this.jq.parents('.ui-helper-hidden:first'),
        hiddenParentWidget = hiddenParent.data('widget');
        
        if(hiddenParentWidget) {
            hiddenParentWidget.addOnshowHandler(function() {
                return _self.init();
            });
        }
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.ScrollPanel, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.ScrollPanel.prototype.init = function(){
    if(this.jq.is(':hidden'))
        return false;
    
    var contentWidth = this.content.outerWidth(true),
    contentHeight = this.content.outerHeight(true),
    
    containerWidth = this.jq.width(),
    containerHeight = this.jq.height();
    this.container.css({width: containerWidth, height: containerHeight});
    
    if(this.cfg.scrollMode === 'native'){
        this.container.css({overflow: 'auto'});
    }
    else{
        var xScrolled = contentWidth > containerWidth,
        yScrolled = contentHeight > containerHeight,
        hbar = this.container.children('.ui-scrollpanel-hbar'),
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
    }
    
    return true;
}

PrimeFaces.widget.ScrollPanel.prototype.initScroll = function(s){
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
}

PrimeFaces.widget.ScrollPanel.prototype.bindEvents = function(s){
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
    
    //drag
    scroll.hand.draggable({
        axis: scroll.dir,
        drag: function (e, data) {
            if(scroll.dir === 'x'){
                var move = (e.target.offsetLeft - data.position.left);
                _self.scrollWithRatio('x', move);
            }
            else{
                var move = (e.target.offsetTop - data.position.top);
                _self.scrollWithRatio('y', move);
            }
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
    
    $(window).bind('mouseup.scrollpanel', function(){
        clearInterval(mouseInterval);
        mouseDown = false;
    });
}

PrimeFaces.widget.ScrollPanel.prototype.scrollTo = function(x, y) {
    this.scrollX(x);
    this.scrollY(y);
}

PrimeFaces.widget.ScrollPanel.prototype.scrollToRatio = function(x, y, moveBars) {
    this.scrollWithRatio('x', x, moveBars === false ? false : true);
    this.scrollWithRatio('y', y, moveBars === false ? false : true);
}

PrimeFaces.widget.ScrollPanel.prototype.checkScrollable = function(o, d){
    if( o && d){
        if(o.controller + d < 0)
            return -o.controller;
        else if(o.controller + d > o.scrollable)
            return o.scrollable - o.controller;
        else
            return d;
    }
    return 0;
}

PrimeFaces.widget.ScrollPanel.prototype.scrollWithRatio = function(dir, d, wheel){
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
}

PrimeFaces.widget.ScrollPanel.prototype.scrollX = function(x){
    this.content.css({left : typeof(x) == 'string' ? x : -x});
}

PrimeFaces.widget.ScrollPanel.prototype.scrollY = function(y){
    this.content.css({top : typeof(y) == 'string' ? y : -y});
}