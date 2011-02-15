/*!
 * jQuery Animated Auto-Resizable Textarea Plugin v1.0
 *
 * Copyright (c) 2009 - 2010 Wayne Haffenden
 * http://www.waynehaffenden.com/Blog/jQuery-AutoResizable-Plugin
 *
 * $Id: jquery.autoResizable.js, v 1.0 2009-12-30 01:53:14Z whaffenden $
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
;(function($) {
    /**
     * The autoResizable() function is used to animate auto-resizable textareas on a given selector. To change the default options,
     * simply pass the options in an object as the only argument to the autoResizable() function.
     */
    $.fn.autoResizable = function(options) {

        // Defines the abstract settings.
        var settings = $.extend({
            animate: true,
            animateDuration: 200,
            maxHeight: 500,
            onBeforeResize: null,
            onAfterResize: null,
            padding: 20,
            paste: true,
            pasteInterval: 100
        }, options);

        // Filters the selectors to just textareas.
        return this.filter('textarea').each(function() {
            var textarea = $(this),
                originalHeight = textarea.height(),
                currentHeight = 0,
                pasteListener = null,
                animate = settings.animate,
                animateDuration = settings.animateDuration,
                maxHeight = settings.maxHeight,
                onBeforeResize = settings.onBeforeResize,
                onAfterResize = settings.onAfterResize,
                padding = settings.padding,
                paste = settings.paste,
                pasteInterval = settings.pasteInterval;

            // Creates a clone of the textarea, used to determine the textarea height.
            var clone = (function() {
                var cssKeys = ['height', 'letterSpacing', 'lineHeight', 'textDecoration', 'width'],
                    properties = {};

                $.each(cssKeys, function(i, key) {
                    properties[key] = textarea.css(key);
                });

                return textarea.clone().removeAttr('id').removeAttr('name').css({
                    left: -99999,
                    position: 'absolute',
                    top: -99999
                }).css(properties).attr('tabIndex', -1).insertBefore(textarea);
            })();

            /**
             * Automatically resizes the textarea.
             */
            var autoResize = function() {
                if (originalHeight <= 0) {
                    originalHeight = textarea.height();
                }

                // Prepares the clone.
                clone.height(0).val(textarea.val()).scrollTop(10000);

                // Determines the height of the text.
                var newHeight = Math.max((clone.scrollTop() + padding), originalHeight);
                if (newHeight === currentHeight || (newHeight >= maxHeight && currentHeight === maxHeight)) {
                    return;
                }

                if (newHeight >= maxHeight) {
                    newHeight = maxHeight;
                    textarea.css('overflow-y', 'auto');
                } else {
                    textarea.css({overflow: 'hidden', overflowY: 'hidden'});
                }

                // Fires off the onBeforeResize event.
                var resize = true;
                if (onBeforeResize !== null) {
                    resize = onBeforeResize.call(textarea, currentHeight, newHeight);
                }

                currentHeight = newHeight;

                // Determines if the resizing should actually take place.
                if (resize === false) {
                    return;
                }

                // Adjusts the height of the textarea.
                if (animate && textarea.css('display') === 'block') {
                    textarea.stop().animate({height: newHeight}, animateDuration, function() {
                        if (onAfterResize !== null) {
                            onAfterResize.call(textarea);
                        }
                    });
                } else {
                    textarea.height(newHeight);
                    if (onAfterResize !== null) {
                        onAfterResize.call(textarea);
                    }
                }
            };

            /**
             * Initialises the paste listener and invokes the autoResize method.
             */
            var init = function() {
                if (paste) {
                    pasteListener = setInterval(autoResize, pasteInterval);
                }

                autoResize();
            };

            /**
             * Uninitialises the paste listener.
             */
            var uninit = function() {
                if (pasteListener !== null) {
                    clearInterval(pasteListener);
                    pasteListener = null;
                }
            };

            // Hides scroll bars and disables WebKit resizing.
            textarea.css({overflow: 'hidden', resize: 'none'});

            // Binds the textarea event handlers.
            textarea.unbind('.autoResizable')
                    .bind('keydown.autoResizable', autoResize)
                    .bind('keyup.autoResizable', autoResize)
                    .bind('change.autoResizable', autoResize)
                    .bind('focus.autoResizable', init)
                    .bind('blur.autoResizable', uninit);
        });
    };
})(jQuery);


/**
 * PrimeFaces InputText Widget
 */
PrimeFaces.widget.InputText = function(cfg) {
    this.id = cfg.id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    PrimeFaces.skinInput(this.jq);
}

/**
 * PrimeFaces InputTextarea Widget
 */
PrimeFaces.widget.InputTextarea = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = jQuery(this.jqId);

    //Client behaviors
    if(this.cfg.behaviors) {
        PrimeFaces.attachBehaviors(this.jq, this.cfg.behaviors);
    }

    //Visuals
    PrimeFaces.skinInput(this.jq);

    //AutoResize
    if(this.cfg.autoResize) {
        this.jq.autoResizable({
            maxHeight: this.cfg.maxHeight
            ,animateDuration: this.cfg.effectDuration
        });
    }
}