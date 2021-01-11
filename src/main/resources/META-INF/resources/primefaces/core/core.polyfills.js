if (!Element.prototype.matches) {
    Element.prototype.matches = Element.prototype.msMatchesSelector ||
            Element.prototype.webkitMatchesSelector;
}

if (!Element.prototype.closest) {
    Element.prototype.closest = function (s) {
        var el = this;
        do {
            if (el.matches(s))
                return el;
            el = el.parentElement || el.parentNode;
        } while (el !== null && el.nodeType === 1);
        return null;
    };
}

if (!String.prototype.includes) {
    String.prototype.includes = function(search, start) {
      'use strict';

      if (search instanceof RegExp) {
        throw TypeError('first argument must not be a RegExp');
      } 
      if (start === undefined) { start = 0; }
      return this.indexOf(search, start) !== -1;
    };
}

if (!Array.prototype.includes) {
    Object.defineProperty(Array.prototype, 'includes', {
        value: function(searchElement, fromIndex) {
            if (this == null) {
                throw new TypeError('"this" is null or not defined');
            }

            var o = Object(this);
            var len = o.length >>> 0;

            if (len === 0) {
                return false;
            }

            var n = fromIndex | 0;
            var k = Math.max(n >= 0 ? n : len - Math.abs(n), 0);

            while (k < len) {
                if (o[k] === searchElement) {
                    return true;
                }
                k++;
            }

            return false;
        }
    });
}