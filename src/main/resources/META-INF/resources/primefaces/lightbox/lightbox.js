/**
 * __PrimeFaces LightBox Widget__
 * 
 * @typedef {"iframe" | "image" | "inlne"} PrimeFaces.widget.LightBox.ContentMode Type of the content that is shown in
 * the lightbox.
 * 
 * @typedef PrimeFaces.widget.LightBox.OnHideCallback Client-side callback invoked when the lightbox is hidden. See also
 * {@link LightBoxCfg.onHide}.
 * @this {PrimeFaces.widget.LightBox} PrimeFaces.widget.LightBox.OnHideCallback 
 * 
 * @typedef PrimeFaces.widget.LightBox.OnShowCallback Client-side callback invoked when the lightbox is shown. See also
 * {@link LightBoxCfg.onShow}.
 * @this {PrimeFaces.widget.LightBox} PrimeFaces.widget.LightBox.OnShowCallback 
 * 
 * @typedef {() => void} PrimeFaces.widget.LightBox.OnShowHandlersCallback List of registered callback handlers for when
 * the lightbox is shown. See also {@link LightBox.onshowHandlers}.
 * 
 * @interface {PrimeFaces.widget.LightBox.UrlSettings} UrlSettings Settings for showing an URL in an iframe inside
 * the lightbox.
 * @prop {number} [UrlSettings.height] Height of the iframe in pixels.
 * @prop {string} UrlSettings.src URL to show in an iframe.
 * @prop {string} [UrlSettings.title] Title text to show below the iframe.
 * @prop {number} [UrlSettings.width] Width of the iframe in pixels.
 * 
 * @prop {JQuery} caption The DOM element for the caption container below the lightbox.
 * @prop {JQuery} captionText The DOM element for the caption text below the lightbox.
 * @prop {JQuery} closeIcon The DOM element for the close icon to hide the lightbox.
 * @prop {JQuery} content The DOM element for the content of the lightbox
 * @prop {JQuery} contentWrapper The DOM element for the content container of the lightbox-
 * @prop {number} current Index of the slide currently being shown.
 * @prop {JQuery} iframe The DOM element for the iframe, if `mode` is set to `iframe`.
 * @prop {boolean} iframeLoaded Whether the iframe was already loaded.
 * @prop {JQuery} imageDisplay The DOM element for the image, if `mode` is set to `image`.
 * @prop {JQuery} inline The DOM element for the inline content element, if `mode` is set to `inline`.
 * @prop {JQuery} links The DOM element for the links in the inline content, if `mode` is set to `inline`.
 * @prop {JQuery} navigators The DOM element for the arrow buttons for switching to the previous or next slide. 
 * @prop {PrimeFaces.widget.LightBox.OnShowHandlersCallback[]} onshowHandlers List of registered callback handlers for
 * when the lightbox is shown.
 * @prop {JQuery} panel The DOM element for the entire lightbox overlay panel.
 * 
 * @interface {PrimeFaces.widget.LightBoxCfg} cfg The configuration for the {@link  LightBox| LightBox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {string} cfg.appendTo Selector for the element to which the overlay lightbox panel is appended.
 * @prop {number} cfg.height Height of the overlay in iframe mode.
 * @prop {string} cfg.iframeTitle Title of the iframe element.
 * @prop {PrimeFaces.widget.LightBox} cfg.mode The type of content that is shown in the lightbox.
 * @prop {PrimeFaces.widget.LightBox.OnHideCallback} cfg.onHide Client-side callback invoked when the lightbox is
 * hidden.
 * @prop {PrimeFaces.widget.LightBox.OnShowCallback} cfg.onShow Client-side callback invoked when the lightbox is
 * shown.
 * @prop {boolean} cfg.visible Whether the lightbox is initially visible.
 * @prop {number} cfg.width Width of the overlay in iframe mode.
 */
PrimeFaces.widget.LightBox = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        // the dynamic overlay must be appended to the body
        cfg.appendTo = '@(body)';

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
            this.links.eq(0).trigger("click");
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    refresh: function(cfg) {
        PrimeFaces.utils.removeDynamicOverlay(this, this.panel, this.id + '_panel', $(document.body));

        this._super(cfg);
    },

    /**
     * Creates the DOM elements for the lightbox panel.
     * @private
     */
    createPanel: function() {
        this.panel = $('<div id="' + this.id + '_panel" class="ui-lightbox ui-widget ui-helper-hidden ui-corner-all ui-shadow">'
            + '<div class="ui-lightbox-content-wrapper">'
            + '<a class="ui-state-default ui-lightbox-nav-left ui-corner-right ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-w">go</span></a>'
            + '<div class="ui-lightbox-content ui-corner-all"></div>'
            + '<a class="ui-state-default ui-lightbox-nav-right ui-corner-left ui-helper-hidden"><span class="ui-icon ui-icon-carat-1-e">go</span></a>'
            + '</div>'
            + '<div class="ui-lightbox-caption ui-widget-header"><span class="ui-lightbox-caption-text"></span>'
            + '<a class="ui-lightbox-close ui-corner-all" href="#"><span class="ui-icon ui-icon-closethick"></span></a><div style="clear:both"></div></div>'
            + '</div>');

        PrimeFaces.utils.registerDynamicOverlay(this, this.panel, this.id + '_panel');

        this.contentWrapper = this.panel.children('.ui-lightbox-content-wrapper');
        this.content = this.contentWrapper.children('.ui-lightbox-content');
        this.caption = this.panel.children('.ui-lightbox-caption');
        this.captionText = this.caption.children('.ui-lightbox-caption-text');
        this.closeIcon = this.caption.children('.ui-lightbox-close');
    },

    /**
     * Sets up the DOM elements and events handlers for showing images in the lightbox
     * @private
     */
    setupImaging: function() {
        var $this = this;

        this.content.append('<img class="ui-helper-hidden"></img>');
        this.imageDisplay = this.content.children('img');
        this.navigators = this.contentWrapper.children('a');

        this.imageDisplay.on('load', function() {
            var image = $(this);

            $this.scaleImage(image);

            //coordinates to center overlay
            var leftOffset = ($this.panel.width() - image.width()) / 2,
            topOffset = ($this.panel.height() - image.height()) / 2;

            //resize content for new image
            $this.content.removeClass('ui-lightbox-loading');
            $this.content.stop().animate({ width: image.width(), height: image.height() },
                500,
                function() {
                    //show image
                    image.fadeIn();
                    $this.showNavigators();
                    $this.caption.slideDown();
                });

            $this.panel.stop().animate({ left: '+=' + leftOffset, top: '+=' + topOffset}, 500);
        });

        this.navigators.on("mouseover", function() {
            $(this).addClass('ui-state-hover');
        })
        .on("mouseout", function() {
            $(this).removeClass('ui-state-hover');
        })
        .on("click", function(e) {
            var nav = $(this);

            $this.hideNavigators();

            if(nav.hasClass('ui-lightbox-nav-left')) {
                var index = $this.current == 0 ? $this.links.length - 1 : $this.current - 1;

                $this.links.eq(index).trigger('click');
            }
            else {
                var index = $this.current == $this.links.length - 1 ? 0 : $this.current + 1;

                $this.links.eq(index).trigger('click');
            }

            e.preventDefault();
        });

        this.links.on("click", function(e) {
            var link = $(this);

            if($this.isHidden()) {
                $this.content.addClass('ui-lightbox-loading').width(32).height(32);
                $this.show();
            }
            else {
                $this.imageDisplay.stop().fadeOut(function() {
                    //clear for onload scaling
                    $(this).css({
                        'width': 'auto'
                        ,'height': 'auto'
                    });

                    $this.content.addClass('ui-lightbox-loading');
                });

                $this.caption.stop().slideUp();
            }

            clearTimeout(this.timeout);
            this.timeout = setTimeout(function() {
                $this.imageDisplay.attr('src', link.attr('href'));
                $this.current = link.index();

                var title = link.attr('title');
                if(title) {
                    $this.captionText.text(title);
                }
            }, 1000);

            e.preventDefault();
        });
    },

    /**
     * Scales the given image so that it fits the lightbox viewport.
     * @param {JQuery} image An image to be scaled.
     * @private
     */
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
        });
    },

    /**
     * Sets up the DOM elements and events handlers for inline content such as videos in the lightbox.
     * @private
     */
    setupInline: function() {
        this.inline = this.jq.children('.ui-lightbox-inline');
        this.inline.appendTo(this.content).show();
        var $this = this;

        this.links.on("click", function(e) {
            $this.show();

            var title = $(this).attr('title');
            if(title) {
                $this.captionText.text(title);
                $this.caption.stop().slideDown();
            }

            e.preventDefault();
        });
    },

    /**
     * Sets up the DOM elements and events handlers for showing an external page inside an iframe in the lightbox.
     * @private
     */
    setupIframe: function() {
        var $this = this;
        this.iframeLoaded = false;
        this.cfg.width = this.cfg.width||'640px';
        this.cfg.height = this.cfg.height||'480px';

        this.iframe = $('<iframe frameborder="0" style="width:' + this.cfg.width + ';height:'
                        + this.cfg.height + ';border:0 none; display: block;"></iframe>').appendTo(this.content);

        if(this.cfg.iframeTitle) {
            this.iframe.attr('title', this.cfg.iframeTitle);
        }

        this.links.on("click", function(e) {
            if(!$this.iframeLoaded) {
                $this.content.addClass('ui-lightbox-loading').css({
                    width: $this.cfg.width
                    ,height: $this.cfg.height
                });
                $this.show();

                $this.iframe.on('load', function() {
                                $this.iframeLoaded = true;
                                $this.content.removeClass('ui-lightbox-loading');
                            })
                            .attr('src', $this.links.eq(0).attr('href'));
            }
            else {
                $this.show();
            }

            var title = $this.links.eq(0).attr('title');
            if(title) {
                $this.captionText.text(title);
                $this.caption.slideDown();
            }

            e.preventDefault();
        });
    },

    /**
     * Sets up some common event handlers required independent of the content shown in the lightbox.
     * @private
     */
    bindCommonEvents: function() {
        var $this = this;

        this.closeIcon.on("mouseover", function() {
            $(this).addClass('ui-state-hover');
        })
        .on("mouseout", function() {
            $(this).removeClass('ui-state-hover');
        });

        this.closeIcon.on("click", function(e) {
            $this.hide();
            e.preventDefault();
        });

        var hideEvent = PrimeFaces.env.ios ? 'touchstart' : 'click';
        PrimeFaces.utils.registerHideOverlayHandler(this, hideEvent + '.' + this.id + '_hide', $this.panel,
            function() { return $this.links.add($this.closeIcon); },
            function(e, eventTarget) {
                if(!($this.panel.is(eventTarget) || $this.panel.has(eventTarget).length > 0)) {
                    e.preventDefault();
                    $this.hide();
                }
            });

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.panel, function() {
            $(document.body).children('.ui-widget-overlay').css({
                'width': $(document).width() + 'px'
                ,'height': $(document).height() + 'px'
            });
        });
    },

    /**
     * Brings up this lightbox and shows it to the user.
     */
    show: function() {
        this.center();

        this.panel.css('z-index', PrimeFaces.nextZindex()).show();

        if(!PrimeFaces.utils.isModalActive(this.id)) {
            this.enableModality();
        }

        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    /**
     * Closes this lightbox and hides it from view.
     */
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

    /**
     * Centers this lightbox so that is moved to the center of the browser viewport.
     */
    center: function() {
        var win = $(window),
        left = (win.width() / 2 ) - (this.panel.width() / 2),
        top = (win.height() / 2 ) - (this.panel.height() / 2);

        this.panel.css({
            'left': left + 'px',
            'top': top + 'px'
        });
    },

    /**
     * Makes this  lightbox a modal dialog so that the user cannot interact with other content on the page.
     */
    enableModality: function() {
        PrimeFaces.utils.addModal(this, this.panel);
    },

    /**
     * Makes this  lightbox a non-modal dialog so that the user can interact with other content on the page.
     */
    disableModality: function() {
        PrimeFaces.utils.removeModal(this, this.panel);
    },

    /**
     * Displays the navigator buttons for switching to the previous or next slide.
     */
    showNavigators: function() {
        this.navigators.zIndex(this.imageDisplay.zIndex() + 1).show();
    },

    /**
     * Hides the navigator buttons for switching to the previous or next slide.
     */
    hideNavigators: function() {
        this.navigators.hide();
    },

    /**
     * Adds a callback that is invoked when the lightbox is displayed.
     * @param {() => void} fn A callback that is invoked when the lightbox is shown. 
     * @private
     */
    addOnshowHandler: function(fn) {
        this.onshowHandlers.push(fn);
    },

    /**
     * Checks whether this light is currently being displayed.
     * @return {boolean} `true` if this lightbox is currently hidden, or `false` otherwise.
     */
    isHidden: function() {
        return this.panel.is(':hidden');
    },

    /**
     * Shows the given URL in an IFRAME inside this lightbox.
     * @param {PrimeFaces.widget.LightBox.UrlSettings} opt Options for how the URL is shown.
     */
    showURL: function(opt) {
        if(opt.width)
            this.iframe.attr('width', opt.width);
        if(opt.height)
            this.iframe.attr('height', opt.height);

        this.iframe.attr('src', opt.src);

        this.captionText.text(opt.title||'');
        this.caption.slideDown();

        this.show();
    }

});
