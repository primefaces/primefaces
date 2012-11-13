/**
 * PrimeFaces LightBox Widget
 */
PrimeFaces.widget.LightBox = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.links = this.jq.children(':not(.ui-lightbox-inline)');

        this.createPanel();

        if(this.cfg.mode === 'image') {
            this.setupImaging();
        } else if(this.cfg.mode === 'inline') {
            this.setupInline();
        } else if(this.cfg.mode === 'iframe') {
            this.setupIframe();
        }

        this.bindCommonEvents();

        if(this.cfg.visible) {
            this.links.eq(0).click();
        }

        this.panel.data('widget', this);
        this.links.data('primefaces-lightbox-trigger', true).find('*').data('primefaces-lightbox-trigger', true);
    },
    
    refresh: function(cfg) {
        $(PrimeFaces.escapeClientId(cfg.id) + '_panel').remove();
        
        this.init(cfg);
    },
    
    createPanel: function() {
        var dom = '<div id="' + this.id + '_panel" class="ui-lightbox ui-widget ui-helper-hidden ui-corner-all ui-shadow">';
        dom += '<div class="ui-lightbox-content-wrapper">';
        dom += '<a class="ui-state-default ui-lightbox-nav-left ui-corner-right ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-w">go</span></a>';
        dom += '<div class="ui-lightbox-content ui-corner-all"></div>';
        dom += '<a class="ui-state-default ui-lightbox-nav-right ui-corner-left ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-e">go</span></a>';
        dom += '</div>';
        dom += '<div class="ui-lightbox-caption ui-widget-header"><span class="ui-lightbox-caption-text"></span>';
        dom += '<a class="ui-lightbox-close ui-corner-all" href="#"><span class="ui-icon ui-icon-closethick"></span></a><div style="clear:both" /></div>';
        dom += '</div>';
        
        $(document.body).append(dom);
        
        this.panel = $(this.jqId + '_panel');
        this.contentWrapper = this.panel.children('.ui-lightbox-content-wrapper');
        this.content = this.contentWrapper.children('.ui-lightbox-content');
        this.caption = this.panel.children('.ui-lightbox-caption');
        this.captionText = this.caption.children('.ui-lightbox-caption-text');        
        this.closeIcon = this.caption.children('.ui-lightbox-close');
    },
    
    setupImaging: function() {
        var _self = this;

        this.content.append('<img class="ui-helper-hidden"></img>');
        this.imageDisplay = this.content.children('img');
        this.navigators = this.contentWrapper.children('a');
        
        this.imageDisplay.load(function() { 
            var image = $(this);
            
            _self.scaleImage(image);

            //coordinates to center overlay
            var leftOffset = (_self.panel.width() - image.width()) / 2,
            topOffset = (_self.panel.height() - image.height()) / 2;
            
            //resize content for new image
            _self.content.removeClass('ui-lightbox-loading').animate({
                width: image.width()
                ,height: image.height()
            },
            500,
            function() {            
                //show image
                image.fadeIn();
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
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover'); 
        })
        .click(function(e) {
            var nav = $(this);

            _self.hideNavigators();

            if(nav.hasClass('ui-lightbox-nav-left')) {
                var index = _self.current == 0 ? _self.links.length - 1 : _self.current - 1;

                _self.links.eq(index).trigger('click');
            } 
            else {
                var index = _self.current == _self.links.length - 1 ? 0 : _self.current + 1;

                _self.links.eq(index).trigger('click');
            }

            e.preventDefault(); 
        });

        this.links.click(function(e) {
            var link = $(this);
            
            if(_self.isHidden()) {
                _self.content.addClass('ui-lightbox-loading').width(32).height(32);
                _self.show();
            }
            else {
                _self.imageDisplay.fadeOut(function() {
                    //clear for onload scaling
                    $(this).css({
                        'width': 'auto'
                        ,'height': 'auto'
                    });

                    _self.content.addClass('ui-lightbox-loading');
                });

                _self.caption.slideUp();
            }

            setTimeout(function() {
                _self.imageDisplay.attr('src', link.attr('href'));
                _self.current = link.index();
                
                var title = link.attr('title');
                if(title) {
                    _self.captionText.html(title);
                }
            }, 1000);


            e.preventDefault();
        });
    },
    
    scaleImage: function(image) {
        var win = $(window),
        winWidth = win.width(),
        winHeight = win.height(),
        imageWidth = image.width(),
        imageHeight = image.height(),
        ratio = imageHeight / imageWidth;
        
        if(imageWidth >= winWidth && ratio <= 1){
            imageWidth = winWidth * 0.75;
            imageHeight = imageWidth * ratio;
        } 
        else if(imageHeight >= winHeight){
            imageHeight = winHeight * 0.75;
            imageWidth = imageHeight / ratio;
        }

        image.css({
            'width':imageWidth + 'px'
            ,'height':imageHeight + 'px'
        })
    },
    
    setupInline: function() {
        this.inline = this.jq.children('.ui-lightbox-inline');
        this.inline.appendTo(this.content).show();
        var _self = this;

        this.links.click(function(e) {
            _self.show();

            var title = $(this).attr('title');
            if(title) {
                _self.captionText.html(title);
                _self.caption.slideDown();
            }

            e.preventDefault();
        });
    },
    
    setupIframe: function() {
        var _self = this;
        this.cfg.width = this.cfg.width||'640px';
        this.cfg.height = this.cfg.height||'480px';
        
        this.iframe = $('<iframe frameborder="0" style="width:' + this.cfg.width + ';height:' + this.cfg.height + ';border:0 none; display: block;" src="' 
            + this.links.eq(0).attr('href') + '"></iframe>').appendTo(this.content);

        this.links.click(function(e) {
            _self.show();

            var title = $(this).attr('title');
            if(title) {
                _self.caption.html(title);
                _self.caption.slideDown();
            }

            e.preventDefault();
        });
    },
    
    bindCommonEvents: function() {
        var _self = this;
        
        this.closeIcon.mouseover(function() {
            $(this).addClass('ui-state-hover');
        })
        .mouseout(function() {
            $(this).removeClass('ui-state-hover');
        })

        this.closeIcon.click(function(e) {
            _self.hide();
            e.preventDefault();
        });

        //hide when outside is clicked
        $(document.body).bind('click.ui-lightbox', function (e) {            
            if(_self.isHidden()) {
                return;
            }
            
            //do nothing if target is the link
            var target = $(e.target);
            if(target.data('primefaces-lightbox-trigger')) {
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
        
        //sync window resize
        $(window).resize(function() {
            if(!_self.isHidden()) {
                $(document.body).children('.ui-widget-overlay').css({
                    'width': $(document).width()
                    ,'height': $(document).height()
                });
            }
        });
    },
    
    show: function() {
        this.center();
        
        this.panel.css('z-index', ++PrimeFaces.zindex).show();
        
        if(!this.isModalActive()) {
            this.enableModality();
        }

        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },
    
    hide: function() {
        this.panel.fadeOut();
        this.disableModality();
        this.caption.hide();

        if(this.cfg.mode == 'image') {
            this.imageDisplay.hide().attr('src', '').removeAttr('style');
            this.hideNavigators();
        }

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
    
    isModalActive: function() {
        return $(document.body).children(this.jqId + '_modal').length === 1;
    },
    
    showNavigators: function() {
        this.navigators.zIndex(this.imageDisplay.zIndex() + 1).show();
    },
    
    hideNavigators: function() {
        this.navigators.hide();
    },
    
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    },
    
    isHidden: function() {
        return this.panel.is(':hidden');
    },
    
    showURL: function(opt) {
        if(opt.width)
            this.iframe.attr('width', opt.width);
        if(opt.height)
            this.iframe.attr('height', opt.height);
        
        this.iframe.attr('src', opt.src); 
        
        this.show();
    }
    
});