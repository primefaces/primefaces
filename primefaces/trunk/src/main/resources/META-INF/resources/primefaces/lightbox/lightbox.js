/**
 * PrimeFaces LightBox Widget
 */
PrimeFaces.widget.LightBox = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);
    this.links = this.jq.children();
    
    this.createPanel();
    
    this.bindEvents();
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.LightBox, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.LightBox.prototype.createPanel = function() {
    var dom = '<div id="' + this.id + '_panel" class="ui-lightbox ui-widget ui-helper-hidden">';
    dom += '<div class="ui-lightbox-content-wrapper">';
    dom += '<a class="ui-state-default ui-lightbox-nav-left ui-corner-right ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-w">go</span></a>';
    dom += '<div class="ui-lightbox-content ui-corner-all"><img class="ui-helper-hidden"></img></div>';
    dom += '<a class="ui-state-default ui-lightbox-nav-right ui-corner-left ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-e">go</span></a>';
    dom += '</div></div>';
    
    $(document.body).append(dom);
    this.panel = $(this.jqId + '_panel');
    this.contentWrapper = this.panel.children('.ui-lightbox-content-wrapper');
    this.content = this.contentWrapper.children('.ui-lightbox-content');
    this.imageDisplay = this.content.children('img');
    this.navigators = this.contentWrapper.children('a');
}

PrimeFaces.widget.LightBox.prototype.bindEvents = function() {
    var _self = this;
    
    this.imageDisplay.load(function() {
        //prepare content for new image
        _self.content.removeClass('ui-lightbox-loading').width(_self.imageDisplay.width()).height(_self.imageDisplay.height());
        _self.center();
        
        //show image
        _self.imageDisplay.fadeIn();
        _self.showNavigators();
    });
    
    this.navigators.mouseover(function() {
       $(this).addClass('ui-state-hover'); 
    }).mouseout(function() {
       $(this).removeClass('ui-state-hover'); 
    }).click(function(e) {
       var nav = $(this);
       
       _self.hideNavigators();
       
       if(nav.hasClass('ui-lightbox-nav-left')) {
           var index = _self.current == 0 ? _self.links.length - 1 : _self.current - 1;
           
           _self.links.eq(index).click();
       } 
       else {
           var index = _self.current == _self.links.length - 1 ? 0 : _self.current + 1;
           
           _self.links.eq(index).click();
       }
        
       e.preventDefault(); 
    });
    
    this.links.click(function(e) {
        var link = $(this);
        
        if(_self.panel.is(':hidden')) {
            _self.show();
        }
        else {
            _self.imageDisplay.fadeOut(function() {
                _self.content.addClass('ui-lightbox-loading');
            });
        }
        
        setTimeout(function() {
            _self.imageDisplay.attr('src', link.attr('href'));
            _self.current = link.index();
        }, 1000);
        

        e.preventDefault();
        e.stopPropagation();
    });
    
    //hide when outside is clicked
    $(document.body).bind('click.ui-lightbox', function (e) {            
        if(_self.panel.is(":hidden")) {
            return;
        }

        //hide if mouse is outside of lightbox
        var offset = _self.panel.offset();
        if(e.pageX < offset.left ||
            e.pageX > offset.left + _self.panel.width() ||
            e.pageY < offset.top ||
            e.pageY > offset.top + _self.panel.height()) {
            
            _self.hide();
        }
    });
}

PrimeFaces.widget.LightBox.prototype.show = function() {
    this.content.addClass('ui-lightbox-loading').width(20).height(20);
    this.center();
    this.panel.css('z-index', ++PrimeFaces.zindex).show();
    this.enableModality();
}

PrimeFaces.widget.LightBox.prototype.hide = function() {
    this.panel.fadeOut();
    this.disableModality();
    this.imageDisplay.hide();
    this.hideNavigators();
}

PrimeFaces.widget.LightBox.prototype.center = function() {    
    var win = $(window),
    left = (win.width() / 2 ) - (this.panel.width() / 2),
    top = (win.height() / 2 ) - (this.panel.height() / 2);
    
    this.panel.css({
       'left': left,
       'top': top
    });
}

PrimeFaces.widget.LightBox.prototype.enableModality = function() {
    $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay"></div>').
    children(this.jqId + '_modal').css({
        'width': $(document).width()
        ,'height': $(document).height()
        ,'z-index': this.panel.css('z-index') - 1
    });
}

PrimeFaces.widget.LightBox.prototype.disableModality = function() {
    $(document.body).children(this.jqId + '_modal').remove();
}

PrimeFaces.widget.LightBox.prototype.showNavigators = function() {
    this.navigators.zIndex(this.imageDisplay.zIndex() + 1).show();
}

PrimeFaces.widget.LightBox.prototype.hideNavigators = function() {
    this.navigators.hide();
}