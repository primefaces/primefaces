/* PrimeFaces Extensions */
(function () {
    $.fn.extend({
        focus: (function (orig) {
            /**
             * Focuses the elements, with a number as first argument after the given delay, otherwise like the jQuery
             * `focus` method.
             * @param {number | unknown} delay The delay in milliseconds, or the arguments of the jQuery `focus` method.
             * @param {(this: HTMLElement) => void} [fn] Called with each element after it was focused.
             * @return {JQuery} this for chaining.
             */
            return function (delay, fn) {
                return typeof delay === "number" ?
                        this.each(function () {
                            var elem = this;
                            setTimeout(function () {
                                $(elem).trigger('focus');
                                if (fn) {
                                    fn.call(elem);
                                }
                            }, delay);
                        }) :
                        orig.apply(this, arguments);
            };
        })($.fn.focus),

        /**
         * Sets the z-index of the elements, or gets the z-index of the first element: the one of the element or of its
         * closest positioned parent with a z-index (formerly jQuery UI).
         * @param {number | string} [zIndex] The z-index to set, omitted to get it.
         * @return {JQuery | number} this for chaining when setting, the z-index (0 if none) when getting.
         */
        zIndex: function (zIndex) {
            if (zIndex !== undefined) {
                return this.css("zIndex", String(zIndex));
            }

            if (this.length) {
                var elem = $(this[ 0 ]), position, value;
                while (elem.length && elem[ 0 ] !== document) {
                    // Ignore z-index if position is set to a value where z-index is ignored by the browser
                    // This makes behavior of this function consistent across browsers
                    // WebKit always returns auto if the element is positioned
                    position = elem.css("position");
                    if (position === "absolute" || position === "relative" || position === "fixed") {
                        // IE returns 0 when zIndex is not specified
                        // other browsers return a string
                        // we ignore the case of nested elements with an explicit value of 0
                        // <div style="z-index: -10;"><div style="z-index: 0;"></div></div>
                        value = parseInt(elem.css("zIndex"), 10);
                        if (!isNaN(value) && value !== 0) {
                            return value;
                        }
                    }
                    elem = elem.parent();
                }
            }

            return 0;
        }
    });

    /**
     * Checks whether an element can get the focus, for the `:focusable` and `:tabbable` selectors (formerly jQuery UI):
     * enabled form elements, links with href, areas of a visible image map and elements with a tabindex, if visible.
     * @param {Element} element The element to check.
     * @param {boolean} hasTabindex Whether the element has a tabindex attribute.
     * @return {boolean} `true` if the element can get the focus.
     */
    function focusable(element, hasTabindex) {
        var nodeName = element.nodeName.toLowerCase(),
            focusableIfVisible;

        if ("area" === nodeName) {
            var map = element.parentNode,
                mapName = map.name;
            if (!element.href || !mapName || map.nodeName.toLowerCase() !== "map") {
                return false;
            }
            var img = $("img[usemap='#" + mapName + "']");
            return img.length > 0 && img.is(":visible");
        }

        if (/^(input|select|textarea|button|object)$/.test(nodeName)) {
            focusableIfVisible = !element.disabled;
            if (focusableIfVisible) {
                var fieldset = $(element).closest("fieldset")[0];
                if (fieldset) {
                    focusableIfVisible = !fieldset.disabled;
                }
            }
        }
        else if ("a" === nodeName) {
            focusableIfVisible = element.href || hasTabindex;
        }
        else {
            focusableIfVisible = hasTabindex;
        }

        return !!focusableIfVisible && $(element).is(":visible") && $(element).css("visibility") === "visible";
    }

    $.extend($.expr.pseudos, {
        /**
         * The `:focusable` selector: elements that can get the focus, by mouse or script.
         * @param {Element} element The element to check.
         * @return {boolean} `true` if the element can get the focus.
         */
        focusable: function(element) {
            return focusable(element, $.attr(element, "tabindex") != null);
        },
        /**
         * The `:tabbable` selector: focusable elements reachable with the Tab key (no negative tabindex).
         * @param {Element} element The element to check.
         * @return {boolean} `true` if the element can be reached with the Tab key.
         */
        tabbable: function(element) {
            var tabIndex = $.attr(element, "tabindex"),
                hasTabindex = tabIndex != null;
            return (!hasTabindex || tabIndex >= 0) && focusable(element, hasTabindex);
        }
    });

    // "remove" event on removed elements, e.g. on .remove(), .html() or .replaceWith(), formerly provided by the jQuery UI
    // widget factory. The widgets and interactions use it to detect that they were removed by an AJAX update.
    var cleanData = $.cleanData;
    /**
     * Triggers the `remove` event on each removed element that has a handler for it, then cleans the data like jQuery.
     * @param {ArrayLike<Element>} elems The removed elements.
     */
    $.cleanData = function(elems) {
        for (var i = 0; i < elems.length; i++) {
            var elem = elems[i],
                events = elem ? $._data(elem, "events") : undefined;
            if (events && events.remove) {
                $(elem).triggerHandler("remove");
            }
        }
        cleanData(elems);
    };
})();
