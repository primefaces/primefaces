/**
 * __PrimeFaces Galleria Widget__
 *
 * Galleria is used to display a set of images, optionally with a slideshow.
 *
 * @prop {JQuery} caption The DOM element for the caption below the image.
 * @prop {JQuery} frames The DOM elements for the frames in the image strip with the image preview.
 * @prop {number} interval ID of the set-interval ID for the slideshow.
 * @prop {JQuery} panels The DOM element for the panels with the images.
 * @prop {JQuery} panelWrapper The DOM element for the wrapper with the panel with the images.
 * @prop {boolean} slideshowActive Whether the slideshow is currently running.
 * @prop {JQuery} strip The DOM element for the strip at the bottom with the preview images.
 * @prop {JQuery} stripWrapper The DOM element for the wrapper with strip at the bottom with the preview images.
 *
 * @interface {PrimeFaces.widget.GalleriaCfg} cfg The configuration for the {@link  Galleria| Galleria widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 *
 * @prop {boolean} cfg.autoPlay Images are displayed in a slideshow in autoPlay.
 * @prop {number} cfg.activeIndex Index of the image that is currently displayed.
 * @prop {string} cfg.effect Name of animation to use.
 * @prop {number} cfg.effectSpeed Duration of the animation in milliseconds.
 * @prop {JQuery.EffectsOptions<HTMLElement>} cfg.effectOptions Options for the transition between two images.
 * @prop {number} cfg.frameHeight Height of the frames.
 * @prop {number} cfg.frameWidth Width of the frames.
 * @prop {number} cfg.panelHeight Height of the viewport.
 * @prop {number} cfg.panelWidth Width of the viewport.
 * @prop {boolean} cfg.showCaption Whether the caption below the image is shown.
 * @prop {boolean} cfg.showFilmstrip Whether the strip with all available images is shown.
 * @prop {number} cfg.transitionInterval Defines interval of slideshow, in milliseconds.
 */
PrimeFaces.widget.Galleria = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.cfg.panelWidth = this.cfg.panelWidth||600;
        this.cfg.panelHeight = this.cfg.panelHeight||400;
        this.cfg.frameWidth = this.cfg.frameWidth||60;
        this.cfg.frameHeight = this.cfg.frameHeight||40;
        this.cfg.activeIndex = 0;
        this.cfg.showFilmstrip = (this.cfg.showFilmstrip === false) ? false : true;
        this.cfg.autoPlay = (this.cfg.autoPlay === false) ? false : true;
        this.cfg.transitionInterval = this.cfg.transitionInterval||4000;
        this.cfg.effect = this.cfg.effect||'fade';
        this.cfg.effectSpeed = this.cfg.effectSpeed||250;
        this.cfg.effectOptions = {};

        this.panelWrapper = this.jq.children('ul.ui-galleria-panel-wrapper');
        this.panels = this.panelWrapper.children('li.ui-galleria-panel');

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.panelWrapper.width(this.cfg.panelWidth).height(this.cfg.panelHeight);
        this.panels.width(this.cfg.panelWidth).height(this.cfg.panelHeight);
        this.jq.width(this.cfg.panelWidth);

        if(this.cfg.showFilmstrip) {
            this.renderStrip();

            if(this.panels.length) {
                this.bindEvents();
            }
        }

        if(this.cfg.custom) {
            this.panels.children('img').remove();
        }

        var activePanel = this.panels.eq(this.cfg.activeIndex);
        activePanel.removeClass('ui-helper-hidden');

        if(this.cfg.showCaption) {
            this.caption = $('<div class="ui-galleria-caption"></div>').css({
                    'bottom': (this.cfg.showFilmstrip ? this.stripWrapper.outerHeight(true) : 0)  + 'px',
                    'width': this.panelWrapper.width() + 'px'
                    }).appendTo(this.jq);

            this.showCaption(activePanel);
        }

        this.jq.css('visibility', 'visible');

        if(this.cfg.autoPlay && this.panels.length) {
            this.startSlideshow();
        }
    },

    /**
     * Creates the HTML elements for the strip with the available images.
     * @private
     */
    renderStrip: function() {
        //strip
        var frameStyle = 'style="width:' + this.cfg.frameWidth + "px;height:" + this.cfg.frameHeight + 'px;"';

        this.stripWrapper = $('<div class="ui-galleria-filmstrip-wrapper"></div>')
                .width(this.panelWrapper.width() - 50)
                .height(this.cfg.frameHeight)
                .appendTo(this.jq);

        this.strip = $('<ul class="ui-galleria-filmstrip"></ul>').appendTo(this.stripWrapper);

        for(var i = 0; i < this.panels.length; i++) {
            var image = this.panels.eq(i).children('img'),
            frameClass = (i == this.cfg.activeIndex) ? 'ui-galleria-frame ui-galleria-frame-active' : 'ui-galleria-frame',
            frameMarkup = '<li class="'+ frameClass + '" ' + frameStyle + '>'
            + '<div class="ui-galleria-frame-content" ' + frameStyle + '>'
            + '<img src="' + image.attr('src') + '" class="ui-galleria-frame-image" ' + frameStyle + '></img>'
            + '</div></li>';

            this.strip.append(frameMarkup);
        }

        this.frames = this.strip.children('li.ui-galleria-frame');

        //navigators
        this.jq.append('<div class="ui-galleria-nav-prev ui-icon ui-icon-circle-triangle-w" style="bottom:' + (this.cfg.frameHeight / 2) + 'px"></div>' +
            '<div class="ui-galleria-nav-next ui-icon ui-icon-circle-triangle-e" style="bottom:' + (this.cfg.frameHeight / 2) + 'px"></div>');
    },

    /**
     * Sets up all event listenters required by this widget.
     * @private
     */
    bindEvents: function() {
        var $this = this;

        this.jq.children('div.ui-galleria-nav-prev').on('click.galleria', function() {
            $this.stopSlideshow();
            $this.prev();
        });

        this.jq.children('div.ui-galleria-nav-next').on('click.galleria', function() {
            $this.stopSlideshow();
            $this.next();
        });

        this.strip.children('li.ui-galleria-frame').on('click.galleria', function() {
            $this.stopSlideshow();
            $this.select($(this).index(), false);
        });

        // Touch Swipe Events
        if (PrimeFaces.env.isTouchable(this.cfg)) {
            this.jq.swipe({
                swipeLeft:function(event) {
                    $this.stopSlideshow();
                    $this.prev();
                },
                swipeRight: function(event) {
                    $this.stopSlideshow();
                    $this.next();
                },
                excludedElements: PrimeFaces.utils.excludedSwipeElements()
            });
        }

        // Keyboard accessibility
        this.jq.on('keydown.galleria', function(e) {
            var keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.LEFT:
                    $this.stopSlideshow();
                    $this.prev();
                    break;
                case keyCode.RIGHT:
                    $this.stopSlideshow();
                    $this.next();
                    break;
            }
        });
    },

    /**
     * Starts the slideshow, if it is not started already.
     */
    startSlideshow: function() {
        var $this = this;

        this.interval = setInterval(function() {
            $this.next();
        }, this.cfg.transitionInterval);

        this.slideshowActive = true;
    },

    /**
     * Starts the slideshow, if it is not stopped already.
     */
    stopSlideshow: function() {
        if(this.slideshowActive) {
           clearInterval(this.interval);

           this.slideshowActive = false;
        }
    },

    /**
     * Checks whether the slideshow is currently active.
     * @return {boolean} `true` if the slideshow is currently active, or `false` otherwise.
     */
    isSlideshowActive: function() {
        return this.slideshowActive;
    },

    /**
     * Moves the slideshow to the image at the given index.
     * @param {number} index 0-based index of the image to display.
     * @param {boolean} [reposition] `true` (or not given) to reposition the image strip with an animation.
     */
    select: function(index, reposition) {
        if(index !== this.cfg.activeIndex) {
            if(this.cfg.showCaption) {
                this.hideCaption();
            }

            var oldPanel = this.panels.eq(this.cfg.activeIndex),
            newPanel = this.panels.eq(index);

            //content
            oldPanel.hide(this.cfg.effect, this.cfg.effectOptions, this.cfg.effectSpeed);
            newPanel.show(this.cfg.effect, this.cfg.effectOptions, this.cfg.effectSpeed);

            //frame
            if(this.cfg.showFilmstrip) {
                var oldFrame = this.frames.eq(this.cfg.activeIndex),
                newFrame = this.frames.eq(index);

                oldFrame.removeClass('ui-galleria-frame-active').css('opacity', '');
                newFrame.animate({opacity:1.0}, this.cfg.effectSpeed, null, function() {
                   $(this).addClass('ui-galleria-frame-active');
                });

                //viewport
                if(reposition === undefined || reposition === true) {
                    var frameLeft = newFrame.position().left,
                    stepFactor = this.cfg.frameWidth + parseInt(newFrame.css('margin-right')),
                    stripLeft = this.strip.position().left,
                    frameViewportLeft = frameLeft + stripLeft,
                    frameViewportRight = frameViewportLeft + this.cfg.frameWidth;

                    if(frameViewportRight > this.stripWrapper.width()) {
                        this.strip.animate({left: '-=' + stepFactor}, this.cfg.effectSpeed, 'easeInOutCirc');
                    } else if(frameViewportLeft < 0) {
                        this.strip.animate({left: '+=' + stepFactor}, this.cfg.effectSpeed, 'easeInOutCirc');
                    }
                }
            }

            //caption
            if(this.cfg.showCaption) {
                this.showCaption(newPanel);
            }

            this.cfg.activeIndex = index;
        }
    },

    /**
     * Hides the caption text below the image.
     */
    hideCaption: function() {
        this.caption.stop().slideUp(this.cfg.effectSpeed);
    },

    /**
     * Shows the caption text below the image. Pass the `activePanel` property of this panel as the parameter.
     * @param {JQuery} panel The panel that contains the caption text.
     */
    showCaption: function(panel) {
        var image = panel.children('img');
        this.caption.queue(function () {
            $(this).html('<h4>' + PrimeFaces.escapeHTML(image.attr('title')) + '</h4><p>' + PrimeFaces.escapeHTML(image.attr('alt')) + '</p>').dequeue();
        }).slideDown(this.cfg.effectSpeed);
    },

    /**
     * Moves to the previous image that comes before the currently shown image.
     */
    prev: function() {
        if (this.isAnimating()) {
            return;
        }

        if(this.cfg.activeIndex != 0) {
            this.select(this.cfg.activeIndex - 1);
        }
    },

    /**
     * Moves to the next image that comes after the currently shown image.
     */
    next: function() {
        if (this.isAnimating()) {
            return;
        }

        if(this.cfg.activeIndex !== (this.panels.length - 1)) {
            this.select(this.cfg.activeIndex + 1);
        }
        else {
            this.select(0, false);
            this.strip.animate({left: 0}, this.cfg.effectSpeed, 'easeInOutCirc');
        }
    },

    /**
     * Checks whether an animation is currently in progress.
     * @return {boolean} `true` if an animation is currently active, or `false` otherwise.
     */
    isAnimating: function() {
        return this.frames.is(':animated');
    }

});