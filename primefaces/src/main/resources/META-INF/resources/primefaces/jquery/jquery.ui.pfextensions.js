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
})();

// GitHub PrimeFaces #3675 performance
$.widget( "ui.sortable", $.ui.sortable, {
    _setHandleClassName: function() {
        this._removeClass( this.element.find( ".ui-sortable-handle" ), "ui-sortable-handle" );
        $.each( this.items, function() {
                        (this.instance.options.handle 
                        ? this.item.find( this.instance.options.handle ) 
                        : this.item
                        ).addClass('ui-sortable-handle');
        } );
    }
});


// GitHub #8619 fix slider dealing with odd min and max settings that don't align with step'
(function() {
    $.ui.slider.prototype._trimAlignValue = function(val) {
        if (val <= this._valueMin()) {
            return this._valueMin();
        }
        if (val >= this._valueMax()) {
            return this._valueMax();
        }
        var step = (this.options.step > 0) ? this.options.step : 1,
            valModStep = (val - this._valueMin()) % step;

        var alignValue = (valModStep > 0) ? Math.floor(val / step) * step : Math.ceil(val / step) * step;

        if (alignValue <= this._valueMin()) {
            return this._valueMin();
        }
        if (alignValue >= this._valueMax()) {
            return this._valueMax();
        }

        // Since JavaScript has problems with large floats, round
        // the final value to 5 digits after the decimal point (see #4124)
        return parseFloat(alignValue.toFixed(5));
    };

    $.ui.slider.prototype._calculateNewMax = function() {
        var max = this.options.max;
        this.max = parseFloat(max.toFixed(this._precision()));
    };
})();

