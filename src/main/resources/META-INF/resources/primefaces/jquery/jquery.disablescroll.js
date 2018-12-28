/**
 * $.disablescroll
 * Author: Josh Harrison - aloof.co
 * URL: https://github.com/realjoshharrison/jquery-disablescroll
 *
 * Disables scroll events from mousewheels, touchmoves and keypresses.
 * Use while jQuery is animating the scroll position for a guaranteed super-smooth ride!
 */

;(function($) {

    "use strict";

    var instance, proto;

    function UserScrollDisabler($container, options) {
        // spacebar: 32, pageup: 33, pagedown: 34, end: 35, home: 36
        // left: 37, up: 38, right: 39, down: 40
        this.opts = $.extend({
            handleWheel : true,
            handleScrollbar: true,
            handleKeys : true,
            scrollEventKeys : [32, 33, 34, 35, 36, 37, 38, 39, 40]
        }, options);
        
        this.$container = $container;
        this.$document = $(document);
        this.lockToScrollPos = [0, 0];

        this.disable();
    }

    proto = UserScrollDisabler.prototype;

    proto.disable = function() {
        var t = this;

        if(t.opts.handleWheel) {
            t.$container.on(
                "mousewheel.disablescroll DOMMouseScroll.disablescroll touchmove.disablescroll",
                t._handleWheel
            );
        }
        
        if(t.opts.handleScrollbar) {
            t.lockToScrollPos = [
                t.$container.scrollLeft(),
                t.$container.scrollTop()
            ];
            t.$container.on("scroll.disablescroll", function() {
                t._handleScrollbar.call(t);
            });
        }

        if(t.opts.handleKeys) {
            t.$document.on("keydown.disablescroll", function(event) {
                t._handleKeydown.call(t, event);
            });
        }
    };
        
    proto.undo = function() {
        var t = this;
        t.$container.off(".disablescroll");
        if(t.opts.handleKeys) {
            t.$document.off(".disablescroll");
        }
    };
    
    proto._handleWheel = function(event) {
        event.preventDefault();
    };
    
    proto._handleScrollbar = function() {
        this.$container.scrollLeft(this.lockToScrollPos[0]);
        this.$container.scrollTop(this.lockToScrollPos[1]);
    };
    
    proto._handleKeydown = function(event) {
        for (var i = 0; i < this.opts.scrollEventKeys.length; i++) {
            if (event.keyCode === this.opts.scrollEventKeys[i]) {
                event.preventDefault();
                return;
            }
        }
    };
        

    // Plugin wrapper for object
    $.fn.disablescroll = function(method) {

        // If calling for the first time, instantiate the object and save
        // reference. The plugin can therefore only be instantiated once per
        // page. You can pass options object in through the method parameter.
        if( ! instance && (typeof method === "object" || ! method)) {
            instance = new UserScrollDisabler(this, method);
        }

        // Instance created, no method specified. Call disable again
        if(instance && typeof method === "undefined") {
            instance.disable();
        }

        // Instance already created, and a method is being explicitly called,
        // e.g. .disablescroll('undo');
        else if(instance && instance[method]) {
            instance[method].call(instance);
        }

        return this;
    };

    // Global access
    window.UserScrollDisabler = UserScrollDisabler;

})(jQuery);