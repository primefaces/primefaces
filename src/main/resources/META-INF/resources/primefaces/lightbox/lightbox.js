/**
 * PrimeFaces LightBox Widget
 */
PrimeFaces.widget.LightBox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.links = this.jq.children(':not(.ui-lightbox-inline)');
        this.onshowHandlers = [];

        this.createPanel();

        if(this.cfg.mode == 'image') {
            this.setupImaging();
        } else if(this.cfg.mode == 'inline') {
            this.setupInline();
        } else if(this.cfg.mode == 'iframe') {
            this.setupIframe();
        }

        this.bindCommonEvents();

        if(this.cfg.visible) {
            this.links.eq(0).click();
        }

        this.panel.data('widget', this);
    },
    
    createPanel: function() {
        var dom = '<div id="' + this.id + '_panel" class="ui-lightbox ui-widget ui-helper-hidden ui-hidden-container">';
        dom += '<div class="ui-lightbox-content-wrapper">';
        dom += '<a class="ui-state-default ui-lightbox-nav-left ui-corner-right ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-w">go</span></a>';
        dom += '<div class="ui-lightbox-content ui-corner-all"></div>';
        dom += '<a class="ui-state-default ui-lightbox-nav-right ui-corner-left ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-e">go</span></a>';
        dom += '</div>';
        dom += '<div class="ui-lightbox-caption ui-widget-header ui-helper-hidden"></div>';
        dom += '</div>';

        $(document.body).append(dom);
        this.panel = $(this.jqId + '_panel');
        this.contentWrapper = this.panel.children('.ui-lightbox-content-wrapper');
        this.content = this.contentWrapper.children('.ui-lightbox-content');
        this.caption = this.panel.children('.ui-lightbox-caption');
    },
    
    setupImaging: function() {
        var _self = this;

        this.content.append('<img class="ui-helper-hidden"></img>');
        this.imageDisplay = this.content.children('img');
        this.navigators = this.contentWrapper.children('a');

        this.imageDisplay.load(function() {
            var leftOffset = (_self.panel.width() - _self.imageDisplay.width()) / 2,
            topOffset = (_self.panel.height() - _self.imageDisplay.height()) / 2;

            //prepare content for new image
            _self.content.removeClass('ui-lightbox-loading').animate({
                width:_self.imageDisplay.width()
                ,height: _self.imageDisplay.height()
            },
            500,
            function() {            
                //show image
                _self.imageDisplay.fadeIn();
                _self.showNavigators();
                _self.caption.slideDown();
            });

            _self.panel.animate({
                left: '+=' + leftOffset
                ,top: '+=' + topOffset
            }, 500);
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
                _self.content.addClass('ui-lightbox-loading').width(32).height(32);
                _self.show();
            }
            else {
                _self.imageDisplay.fadeOut(function() {
                    _self.content.addClass('ui-lightbox-loading');
                });

                _self.caption.slideUp();
            }

            setTimeout(function() {
                _self.imageDisplay.attr('src', link.attr('href'));
                _self.current = link.index();

                var title = link.attr('title');
                if(title) {
                    _self.caption.html(title);
                }
            }, 1000);


            e.preventDefault();
            e.stopPropagation();
        });
    },
    
    setupInline: function() {
        this.inline = this.jq.children('.ui-lightbox-inline');
        this.inline.appendTo(this.content).show();
        var _self = this;

        this.links.click(function(e) {
            _self.show();

            var title = $(this).attr('title');
            if(title) {
                _self.caption.html(title);
                _self.caption.slideDown();
            }

            e.preventDefault();
            e.stopPropagation();
        });
    },
    
    setupIframe: function() {
        var _self = this;
        this.cfg.width = this.cfg.width||'640px';
        this.cfg.height = this.cfg.height||'480px';

        _self.content.append('<iframe frameborder="0" style="width:' + this.cfg.width + ';height:' + this.cfg.height + ';border:0 none; display: block;" src="' 
            + this.links.eq(0).attr('href') + '"></iframe>');

        this.links.click(function(e) {
            _self.show();

            var title = $(this).attr('title');
            if(title) {
                _self.caption.html(title);
                _self.caption.slideDown();
            }

            e.preventDefault();
            e.stopPropagation();
        });
    },
    
    bindCommonEvents: function() {
        var _self = this;

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
    },
    
    show: function() {
        this.center();
        this.panel.css('z-index', ++PrimeFaces.zindex).show();
        this.enableModality();

        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }

        //execute onshowHandlers and remove successful ones
        this.onshowHandlers = $.grep(this.onshowHandlers, function(fn) {
            return !fn.call();
        });
    },
    
    hide: function() {
        this.panel.fadeOut();
        this.disableModality();
        this.imageDisplay.hide();
        this.hideNavigators();
        this.caption.hide();

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },
    
    center: function() {    
        var win = $(window),
        left = (win.width() / 2 ) - (this.panel.width() / 2),
        top = (win.height() / 2 ) - (this.panel.height() / 2);

        this.panel.css({
        'left': left,
        'top': top
        });
    },
    
    enableModality: function() {
        $(document.body).append('<div id="' + this.id + '_modal" class="ui-widget-overlay"></div>').
        children(this.jqId + '_modal').css({
            'width': $(document).width()
            ,'height': $(document).height()
            ,'z-index': this.panel.css('z-index') - 1
        });
    },
    
    disableModality: function() {
        $(document.body).children(this.jqId + '_modal').remove();
    },
    
    showNavigators: function() {
        this.navigators.zIndex(this.imageDisplay.zIndex() + 1).show();
    },
    
    hideNavigators: function() {
        this.navigators.hide();
    },
    
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    }
    
});