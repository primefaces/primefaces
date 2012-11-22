/**
 * PrimeFaces Galleria Widget
 */
PrimeFaces.widget.Galleria = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);

        this.cfg.frameWidth = this.cfg.frameWidth||60;
        this.cfg.frameHeight = this.cfg.frameHeight||40;
        this.cfg.activeIndex = 0;
        this.cfg.showFilmstrip = (this.cfg.showFilmstrip === false) ? false : true;
        this.cfg.autoPlay = (this.cfg.autoPlay === false) ? false : true;
        this.cfg.transitionInterval = this.cfg.transitionInterval||4000;

        this.panelWrapper = this.jq.children('div.ui-galleria-panel-wrapper');
        this.panels = this.panelWrapper.children('div.ui-galleria-panel');
                    
        var activePanel = this.panels.eq(this.cfg.activeIndex);
        activePanel.removeClass('ui-helper-hidden'); 
        
        var activePanelImg = activePanel.children('img'),
        imageW = activePanelImg.width(),
        imageH = activePanelImg.height();
                                
        this.panelWrapper.width(imageW).height(imageH);
        this.jq.width(imageW);
                    
        if(this.cfg.showFilmstrip) {
            this.renderStrip();
            this.bindEvents();
        }
           
        this.jq.css('visibility', 'visible');
        
        if(this.cfg.autoPlay) {
            this.play();
        }
    },
                
    renderStrip: function() {
        var frameStyle = 'style="width:' + this.cfg.frameWidth + "px;height:" + this.cfg.frameHeight + 'px;"';
                    
        this.stripWrapper = $('<div class="ui-galleria-filmstrip-wrapper"></div>').width(this.panelWrapper.width() - 50).appendTo(this.jq);
        this.strip = $('<ul class="ui-galleria-filmstrip"></div>').appendTo(this.stripWrapper);
                    
        for(var i = 0; i < this.panels.length; i++) {
            var image = this.panels.eq(i).children('img'),
            frameClass = (i == this.cfg.activeIndex) ? 'ui-galleria-frame ui-galleria-frame-active' : 'ui-galleria-frame',
            frameMarkup = '<li class="'+ frameClass + '" ' + frameStyle + '>'
            + '<div class="ui-galleria-frame-content" ' + frameStyle + '>'
            + '<img src="' + image.attr('src') + '" class="ui-galleria-frame-image" ' + frameStyle + '/>'
            + '</div></li>';
                                      
            this.strip.append(frameMarkup);
        }
                    
        this.frames = this.strip.children('li.ui-galleria-frame');
                    
        //navigators
        this.jq.append('<div class="ui-galleria-nav-prev ui-icon ui-icon-circle-triangle-w"></div>' + 
            '<div class="ui-galleria-nav-next ui-icon ui-icon-circle-triangle-e"></div>');
    },
                
    bindEvents: function() {
        var $this = this;
                    
        this.jq.children('div.ui-galleria-nav-prev').on('click.galleria', function() {
            $this.prev();
        });
                    
        this.jq.children('div.ui-galleria-nav-next').on('click.galleria', function() {
            $this.next();
        });
                    
        this.strip.children('li.ui-galleria-frame').on('click.galleria', function() {
            $this.select($(this).index(), false);
        });
    },
                
    play: function() {
        var $this = this;
                    
        setInterval(function() {
            $this.next();
        }, this.cfg.transitionInterval);
    },
                
    select: function(index, reposition) {
        if(index !== this.cfg.activeIndex) {
            var oldPanel = this.panels.eq(this.cfg.activeIndex),
            oldFrame = this.frames.eq(this.cfg.activeIndex),
            newPanel = this.panels.eq(index),
            newFrame = this.frames.eq(index);

            //content
            oldPanel.fadeOut();
            newPanel.fadeIn();

            //frame
            oldFrame.removeClass('ui-galleria-frame-active');
            newFrame.addClass('ui-galleria-frame-active');
                        
            //position
            if(reposition) {
                var frameLeft = newFrame.position().left,
                stripLeft = this.strip.position().left,
                frameViewportLeft = frameLeft + stripLeft,
                viewportMid = (this.stripWrapper.width() / 2);

                if(frameViewportLeft > viewportMid) {
                    var lastFrame = this.frames.eq(this.frames.length - 1),
                    lastFrameViewportLeft = lastFrame.position().left + stripLeft;

                    if(lastFrameViewportLeft > this.stripWrapper.width()) {
                        this.strip.animate({
                            left: '-=65'
                        }, 500, 'easeInOutCirc');
                    }                             
                }
                else if((frameViewportLeft + newFrame.width()) < viewportMid) {
                    var firstFrame = this.frames.eq(0),
                    firstFrameViewportLeft = firstFrame.position().left + stripLeft;

                    if(firstFrameViewportLeft != 0) {
                        this.strip.animate({
                            left: '+=65'
                        }, 500, 'easeInOutCirc');
                    }
                }
            }
            
            this.cfg.activeIndex = index;
        }
    },
                
    prev: function() {
        var activeIndex = this.cfg.activeIndex;
        if(activeIndex != 0) {
            this.select(activeIndex - 1, true);
        }
    },
                
    next: function() {
        var activeIndex = this.cfg.activeIndex,
        $this = this;
                    
        if(activeIndex === (this.panels.length - 1)) {
            this.strip.animate({
                left: 0
            }, 250, 'easeInOutCirc', function() {
                $this.select(0, true);
            });
        } 
        else {
            this.select(activeIndex + 1, true);
        }
    }
    
});