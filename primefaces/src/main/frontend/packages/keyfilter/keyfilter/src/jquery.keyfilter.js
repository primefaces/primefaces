/*
 * PrimeFaces KeyFilter
 *
 * Procedural style:
 * $('#ggg').keyfilter(/[\dA-F]/);
 * Also you can pass test function instead of regexp. Its arguments:
   * this - HTML DOM Element (event target).
   * c - String that contains incoming character.
 * $('#ggg').keyfilter(function(c) { return c != 'a'; });
 *
 * Class style:
 * <input type="text" class="mask-num" />
 *
 * Available classes:
   * mask-pint:     /[\d]/
   * mask-int:      /[\d\-]/
   * mask-pnum:     /[\d\.]/
   * mask-money     /[\d\.\s,]/
   * mask-num:      /[\d\-\.]/
   * mask-hex:      /[0-9a-f]/i
   * mask-email:    /[a-z0-9_\.\-@]/i
   * mask-alpha:    /[a-z_]/i
   * mask-alphanum: /[a-z0-9_]/i
 */

(function($) {
    var defaultMasks = {
        pint: /[\d]/,
        'int': /[\d\-]/,
        pnum: /[\d\.]/,
        money: /[\d\.\s,]/,
        num: /[\d\-\.]/,
        hex: /[0-9a-f]/i,
        email: /[a-z0-9_\.\-@]/i,
        alpha: /[a-z_]/i,
        alphanum: /[a-z0-9_]/i
    };

    $.fn.keyfilter = function(re) {
        return this.on('keypress.keyfilter', function(e) {
            // PF GitHub #1852 / #3785 keyCode.CONTROL and keyCode.ALT
            var key = e.key;
            if (key === 'Control' || key === 'Alt' || key === 'Meta' || e.ctrlKey || e.altKey || e.metaKey)
            {
                return;
            }
            var isPrintableKey = key.length === 1 || key === 'Unidentified';
            if (!isPrintableKey) {
                return;
            }

            var ok = true;
            if (typeof re === "function") {
                ok = re.call(this, key);
            }
            else {
                ok = re.test(key);
            }
            if (!ok) {
                e.preventDefault();
            }
        });
    };

    $.extend($.fn.keyfilter, {
        defaults: {
            masks: defaultMasks
        },
        version: 1.8
    });
})(jQuery);