/* PrimeFaces Extensions */
(function () {
    $.fn.extend({
        focus: (function (orig) {
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

        disableSelection: (function () {
            var eventType = "onselectstart" in document.createElement("div") ?
                    "selectstart" :
                    "mousedown";

            return function () {
                return this.on(eventType + ".ui-disableSelection", function (event) {
                    event.preventDefault();
                });
            };
        })(),

        enableSelection: function () {
            return this.off(".ui-disableSelection");
        },

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

    // :focusable and :tabbable selectors, formerly provided by jQuery UI
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
        focusable: function(element) {
            return focusable(element, $.attr(element, "tabindex") != null);
        },
        tabbable: function(element) {
            var tabIndex = $.attr(element, "tabindex"),
                hasTabindex = tabIndex != null;
            return (!hasTabindex || tabIndex >= 0) && focusable(element, hasTabindex);
        }
    });
})();
