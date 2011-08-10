/*! Copyright (c) 2010 Brandon Aaron (http://brandonaaron.net)
 * Licensed under the MIT License (LICENSE.txt).
 *
 * Thanks to: http://adomas.org/javascript-mouse-wheel/ for some pointers.
 * Thanks to: Mathias Bank(http://www.mathias-bank.de) for a scope bug fix.
 * Thanks to: Seamus Leahy for adding deltaX and deltaY
 *
 * Version: 3.0.4
 *
 * Requires: 1.2.2+
 */

(function($) {

var types = ['DOMMouseScroll', 'mousewheel'];

$.event.special.mousewheel = {
    setup: function() {
        if ( this.addEventListener ) {
            for ( var i=types.length; i; ) {
                this.addEventListener( types[--i], handler, false );
            }
        } else {
            this.onmousewheel = handler;
        }
    },

    teardown: function() {
        if ( this.removeEventListener ) {
            for ( var i=types.length; i; ) {
                this.removeEventListener( types[--i], handler, false );
            }
        } else {
            this.onmousewheel = null;
        }
    }
};

$.fn.extend({
    mousewheel: function(fn) {
        return fn ? this.bind("mousewheel", fn) : this.trigger("mousewheel");
    },

    unmousewheel: function(fn) {
        return this.unbind("mousewheel", fn);
    }
});


function handler(event) {
    var orgEvent = event || window.event, args = [].slice.call( arguments, 1 ), delta = 0, returnValue = true, deltaX = 0, deltaY = 0;
    event = $.event.fix(orgEvent);
    event.type = "mousewheel";

    // Old school scrollwheel delta
    if ( event.wheelDelta ) { delta = event.wheelDelta/120; }
    if ( event.detail     ) { delta = -event.detail/3; }

    // New school multidimensional scroll (touchpads) deltas
    deltaY = delta;

    // Gecko
    if ( orgEvent.axis !== undefined && orgEvent.axis === orgEvent.HORIZONTAL_AXIS ) {
        deltaY = 0;
        deltaX = -1*delta;
    }

    // Webkit
    if ( orgEvent.wheelDeltaY !== undefined ) { deltaY = orgEvent.wheelDeltaY/120; }
    if ( orgEvent.wheelDeltaX !== undefined ) { deltaX = -1*orgEvent.wheelDeltaX/120; }

    // Add event and delta to the front of the arguments
    args.unshift(event, delta, deltaX, deltaY);

    return $.event.handle.apply(this, args);
}

})(jQuery);

/*
 *
 * Wijmo Library 1.3.0
 * http://wijmo.com/
 *
 * Copyright(c) ComponentOne, LLC.  All rights reserved.
 * 
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * licensing@wijmo.com
 * http://www.wijmo.com/license
 *
 * * Wijmo Common utility.
 *
 * Depends:
 *  jquery.ui.core.js
 *
 */
/*Replace inner content by iframe and load content using given url*/
(function ($) {
	$.fn.extend({
		wijContent: function (url) {
			return this.each(function () {
				this.innerHTML = '<iframe frameborder="0" style="width: 100%; height: 100%;" src="' + url + '">"';
			});
		}

		/*,
		wijAddVisibilityObserver: function (h, namespace) {
		return this.each(function () {
		$(this).addClass("wijmo-wijobserver-visibility");
		$(this).bind("wijmovisibilitychanged"
		+ (namespace ? ("." + namespace) : ""), h);
		});
		},
		wijRemoveVisibilityObserver: function (h) {
		return this.each(function () {
		$(this).removeClass("wijmo-wijobserver-visibility");
		if (!h) {
		$(this).unbind("wijmovisibilitychanged");
		}
		else if (jQuery.isFunction(h)) {
		$(this).unbind("wijmovisibilitychanged", h);
		} else {
		$(this).unbind("wijmovisibilitychanged." + h);
		}
		});
		},
		wijTriggerVisibility: function () {
		return this.each(function () {
		var $el = $(this);
		if ($el.hasClass("wijmo-wijobserver-visibility")) {
		$el.trigger("wijmovisibilitychanged");
		}
		$el.find(".wijmo-wijobserver-visibility").trigger("wijmovisibilitychanged");
		});
		}
		*/
	});

	var naNTest = function (num) {
		return isNaN(num) ? 0 : num;
	};

	$.fn.leftBorderWidth = function () {
		var blw = parseFloat($(this).css("borderLeftWidth"));
		var pl = parseFloat($(this).css("padding-left"));
		var ml = 0;
		if ($(this).css("margin-left") != "auto") {
			ml = parseFloat($(this).css("margin-left"));
		}

		return naNTest(blw) + naNTest(pl) + naNTest(ml);
	};

	$.fn.rightBorderWidth = function () {
		var brw = parseFloat($(this).css("borderRightWidth"));
		var pr = parseFloat($(this).css("padding-right"));
		var mr = 0;
		if ($(this).css("margin-right") != "auto") {
			mr = parseFloat($(this).css("margin-right"));
		}
		return naNTest(brw) + naNTest(pr) + naNTest(mr);
	};

	$.fn.topBorderWidth = function () {
		var blw = parseFloat($(this).css("borderTopWidth"));
		var pl = parseFloat($(this).css("padding-top"));
		var ml = 0;
		if ($(this).css("margin-top") != "auto") {
			ml = parseFloat($(this).css("margin-top"));
		}
		return naNTest(blw) + naNTest(pl) + naNTest(ml);
	};

	$.fn.bottomBorderWidth = function () {
		var brw = parseFloat($(this).css("borderBottomWidth"));
		var pr = parseFloat($(this).css("padding-bottom"));
		var mr = 0;
		if ($(this).css("margin-bottom") != "auto") {
			mr = parseFloat($(this).css("margin-bottom"));
		}
		return naNTest(brw) + naNTest(pr) + naNTest(mr);
	};

	$.fn.borderSize = function () {
		var bw = $(this).leftBorderWidth() + $(this).rightBorderWidth();
		var bh = $(this).topBorderWidth() + $(this).bottomBorderWidth();
		var b = { width: bw, height: bh };
		return b;
	};

	$.fn.setOutWidth = function (width) {
		var bw = $(this).leftBorderWidth() + $(this).rightBorderWidth();
		$(this).width(width - bw);
		return this;
	};

	$.fn.setOutHeight = function (height) {
		var bh = $(this).topBorderWidth() + $(this).bottomBorderWidth();
		$(this).height(height - bh);
		return this;
	};

	$.fn.getWidget = function () {
		var widgetName = this.data("widgetName");

		if (widgetName && widgetName != "") {
			return this.data(widgetName);
		}

		return null;
	};

	$.fn.wijshow = function (animation, customAnimations, customAnimationOptions, showing, shown) {
		var animated = animation.animated || false,
			duration = animation.duration || 400,
			easing = animation.easing,
			option = animation.option || {};

		if (showing && $.isFunction(showing)) {
			showing.call(this);
		}

		if (animated) {
			if ($.effects && $.effects[animated]) {
				this.show(animated, $.extend(option, { easing: easing }), duration, shown);
				return;
			}

			if (customAnimations && customAnimations[animated]) {
				customAnimations[animated](animation, $.extend(customAnimationOptions, { complete: shown }));
				return;
			}
		}

		this.show();
		if (shown && $.isFunction(shown)) {
			shown.call(this);
		}
	};

	$.fn.wijhide = function (animation, customAnimations, customAnimationOptions, hiding, hidden) {
		var animated = animation.animated || false,
			duration = animation.duration || 400,
			easing = animation.easing,
			option = animation.option || {};

		if (hiding && $.isFunction(hiding)) {
			hiding.call(this);
		}

		if (animated) {
			if ($.effects && $.effects[animated]) {
				this.hide(animated, $.extend(option,
				{ easing: easing }), duration, hidden);
				return;
			}
			if (customAnimations && customAnimations[animated]) {
				customAnimations[animated](newAnimations,
					$.extend(customAnimationOptions, { complete: hidden }));
				return;
			}
		}

		this.hide();
		if (hidden && $.isFunction(hidden)) {
			hidden.call(this);
		}
	};

	var wijCharValidator = function () { };
	$.extend(wijCharValidator.prototype, {
		_UTFPunctuationsString: ' ! \" # % & \' ( ) * , - . / : ; ? @ [ \\ ] { } \u00a1 \u00ab \u00ad \u00b7 \u00bb \u00bf \u037e \u0387 \u055a \u055b \u055c \u055d \u055e \u055f \u0589 \u058a \u05be \u05c0 \u05c3 \u05f3 \u05f4 \u060c \u061b \u061f \u066a \u066b \u066c \u066d \u06d4 \u0700 \u0701 \u0702 \u0703 \u0704 \u0705 \u0706 \u0707 \u0708 \u0709 \u070a \u070b \u070c \u070d \u0964 \u0965 \u0970 \u0df4 \u0e4f \u0e5a \u0e5b \u0f04 \u0f05 \u0f06 \u0f07 \u0f08 \u0f09 \u0f0a \u0f0b \u0f0c \u0f0d \u0f0e \u0f0f \u0f10 \u0f11 \u0f12 \u0f3a \u0f3b \u0f3c \u0f3d \u0f85 \u104a \u104b \u104c \u104d \u104e \u104f \u10fb \u1361 \u1362 \u1363 \u1364 \u1365 \u1366 \u1367 \u1368 \u166d \u166e \u169b \u169c \u16eb \u16ec \u16ed \u17d4 \u17d5 \u17d6 \u17d7 \u17d8 \u17d9 \u17da \u17dc \u1800 \u1801 \u1802 \u1803 \u1804 \u1805 \u1806 \u1807 \u1808 \u1809 \u180a \u2010 \u2011 \u2012 \u2013 \u2014 \u2015 \u2016 \u2017 \u2018 \u2019 \u201a \u201b \u201c \u201d \u201e \u201f \u2020 \u2021 \u2022 \u2023 \u2024 \u2025 \u2026 \u2027 \u2030 \u2031 \u2032 \u2033 \u2034 \u2035 \u2036 \u2037 \u2038 \u2039 \u203a \u203b \u203c \u203d \u203e \u2041 \u2042 \u2043 \u2045 \u2046 \u2048 \u2049 \u204a \u204b \u204c \u204d \u207d \u207e \u208d \u208e \u2329 \u232a \u3001 \u3002 \u3003 \u3008 \u3009 \u300a \u300b \u300c \u300d \u300e \u300f \u3010 \u3011 \u3014 \u3015 \u3016 \u3017 \u3018 \u3019 \u301a \u301b \u301c \u301d \u301e \u301f \u3030 \ufd3e \ufd3f \ufe30 \ufe31 \ufe32 \ufe35 \ufe36 \ufe37 \ufe38 \ufe39 \ufe3a \ufe3b \ufe3c \ufe3d \ufe3e \ufe3f \ufe40 \ufe41 \ufe42 \ufe43 \ufe44 \ufe49 \ufe4a \ufe4b \ufe4c \ufe50 \ufe51 \ufe52 \ufe54 \ufe55 \ufe56 \ufe57 \ufe58 \ufe59 \ufe5a \ufe5b \ufe5c \ufe5d \ufe5e \ufe5f \ufe60 \ufe61 \ufe63 \ufe68 \ufe6a \ufe6b \uff01 \uff02 \uff03 \uff05 \uff06 \uff07 \uff08 \uff09 \uff0a \uff0c \uff0d \uff0e \uff0f \uff1a \uff1b \uff1f \uff20 \uff3b \uff3c \uff3d \uff5b \uff5d \uff61 \uff62 \uff63 \uff64\';this.UTFWhitespacesString_=\'\t \u000b \u000c \u001f   \u00a0 \u1680 \u2000 \u2001 \u2002 \u2003 \u2004 \u2005 \u2006 \u2007 \u2008 \u2009 \u200a \u200b \u2028 \u202f \u3000',

		isDigit: function (c) {
			return (c >= '0' && c <= '9');
		},

		isLetter: function (c) {
			return !!((c + '').match(new RegExp('[A-Za-z\u00aa\u00b5\u00ba\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u021f\u0222-\u0233\u0250-\u02ad\u02b0-\u02b8\u02bb-\u02c1\u02d0\u02d1\u02e0-\u02e4\u02ee\u037a\u0386\u0388-\u038a\u038c\u038e-\u03a1\u03a3-\u03ce\u03d0-\u03d7\u03da-\u03f3\u0400-\u0481\u048c-\u04c4\u04c7\u04c8\u04cb\u04cc\u04d0-\u04f5\u04f8\u04f9\u0531-\u0556\u0559\u0561-\u0587\u05d0-\u05ea\u05f0-\u05f2\u0621-\u063a\u0640-\u064a\u0671-\u06d3\u06d5\u06e5\u06e6\u06fa-\u06fc\u0710\u0712-\u072c\u0780-\u07a5\u0905-\u0939\u093d\u0950\u0958-\u0961\u0985-\u098c\u098f\u0990\u0993-\u09a8\u09aa-\u09b0\u09b2\u09b6-\u09b9\u09dc\u09dd\u09df-\u09e1\u09f0\u09f1\u0a05-\u0a0a\u0a0f\u0a10\u0a13-\u0a28\u0a2a-\u0a30\u0a32\u0a33\u0a35\u0a36\u0a38\u0a39\u0a59-\u0a5c\u0a5e\u0a72-\u0a74\u0a85-\u0a8b\u0a8d\u0a8f-\u0a91\u0a93-\u0aa8\u0aaa-\u0ab0\u0ab2\u0ab3\u0ab5-\u0ab9\u0abd\u0ad0\u0ae0\u0b05-\u0b0c\u0b0f\u0b10\u0b13-\u0b28\u0b2a-\u0b30\u0b32\u0b33\u0b36-\u0b39\u0b3d\u0b5c\u0b5d\u0b5f-\u0b61\u0b85-\u0b8a\u0b8e-\u0b90\u0b92-\u0b95\u0b99\u0b9a\u0b9c\u0b9e\u0b9f\u0ba3\u0ba4\u0ba8-\u0baa\u0bae-\u0bb5\u0bb7-\u0bb9\u0c05-\u0c0c\u0c0e-\u0c10\u0c12-\u0c28\u0c2a-\u0c33\u0c35-\u0c39\u0c60\u0c61\u0c85-\u0c8c\u0c8e-\u0c90\u0c92-\u0ca8\u0caa-\u0cb3\u0cb5-\u0cb9\u0cde\u0ce0\u0ce1\u0d05-\u0d0c\u0d0e-\u0d10\u0d12-\u0d28\u0d2a-\u0d39\u0d60\u0d61\u0d85-\u0d96\u0d9a-\u0db1\u0db3-\u0dbb\u0dbd\u0dc0-\u0dc6\u0e01-\u0e30\u0e32\u0e33\u0e40-\u0e46\u0e81\u0e82\u0e84\u0e87\u0e88\u0e8a\u0e8d\u0e94-\u0e97\u0e99-\u0e9f\u0ea1-\u0ea3\u0ea5\u0ea7\u0eaa\u0eab\u0ead-\u0eb0\u0eb2\u0eb3\u0ebd\u0ec0-\u0ec4\u0ec6\u0edc\u0edd\u0f00\u0f40-\u0f47\u0f49-\u0f6a\u0f88-\u0f8b\u1000-\u1021\u1023-\u1027\u1029\u102a\u1050-\u1055\u10a0-\u10c5\u10d0-\u10f6\u1100-\u1159\u115f-\u11a2\u11a8-\u11f9\u1200-\u1206\u1208-\u1246\u1248\u124a-\u124d\u1250-\u1256\u1258\u125a-\u125d\u1260-\u1286\u1288\u128a-\u128d\u1290-\u12ae\u12b0\u12b2-\u12b5\u12b8-\u12be\u12c0\u12c2-\u12c5\u12c8-\u12ce\u12d0-\u12d6\u12d8-\u12ee\u12f0-\u130e\u1310\u1312-\u1315\u1318-\u131e\u1320-\u1346\u1348-\u135a\u13a0-\u13f4\u1401-\u166c\u166f-\u1676\u1681-\u169a\u16a0-\u16ea\u1780-\u17b3\u1820-\u1877\u1880-\u18a8\u1e00-\u1e9b\u1ea0-\u1ef9\u1f00-\u1f15\u1f18-\u1f1d\u1f20-\u1f45\u1f48-\u1f4d\u1f50-\u1f57\u1f59\u1f5b\u1f5d\u1f5f-\u1f7d\u1f80-\u1fb4\u1fb6-\u1fbc\u1fbe\u1fc2-\u1fc4\u1fc6-\u1fcc\u1fd0-\u1fd3\u1fd6-\u1fdb\u1fe0-\u1fec\u1ff2-\u1ff4\u1ff6-\u1ffc\u207f\u2102\u2107\u210a-\u2113\u2115\u2119-\u211d\u2124\u2126\u2128\u212a-\u212d\u212f-\u2131\u2133-\u2139\u3005\u3006\u3031-\u3035\u3041-\u3094\u309d\u309e\u30a1-\u30fa\u30fc-\u30fe\u3105-\u312c\u3131-\u318e\u31a0-\u31b7\u3400-\u4db5\u4e00-\u9fa5\ua000-\ua48c\uac00-\ud7a3\uf900-\ufa2d\ufb00-\ufb06\ufb13-\ufb17\ufb1d\ufb1f-\ufb28\ufb2a-\ufb36\ufb38-\ufb3c\ufb3e\ufb40\ufb41\ufb43\ufb44\ufb46-\ufbb1\ufbd3-\ufd3d\ufd50-\ufd8f\ufd92-\ufdc7\ufdf0-\ufdfb\ufe70-\ufe72\ufe74\ufe76-\ufefc\uff21-\uff3a\uff41-\uff5a\uff66-\uffbe\uffc2-\uffc7\uffca-\uffcf\uffd2-\uffd7\uffda-\uffdc]')));
		},

		isLetterOrDigit: function (c) {
			return this.isLetter(c) || this.isDigit(c);
		},

		isSymbol: function (c) {
			var re = new RegExp('[$+<->^`|~\u00a2-\u00a9\u00ac\u00ae-\u00b1\u00b4\u00b6\u00b8\u00d7\u00f7\u02b9\u02ba\u02c2-\u02cf\u02d2-\u02df\u02e5-\u02ed\u0374\u0375\u0384\u0385\u0482\u06e9\u06fd\u06fe\u09f2\u09f3\u09fa\u0b70\u0e3f\u0f01-\u0f03\u0f13-\u0f17\u0f1a-\u0f1f\u0f34\u0f36\u0f38\u0fbe-\u0fc5\u0fc7-\u0fcc\u0fcf\u17db\u1fbd\u1fbf-\u1fc1\u1fcd-\u1fcf\u1fdd-\u1fdf\u1fed-\u1fef\u1ffd\u1ffe\u2044\u207a-\u207c\u208a-\u208c\u20a0-\u20af\u2100\u2101\u2103-\u2106\u2108\u2109\u2114\u2116-\u2118\u211e-\u2123\u2125\u2127\u2129\u212e\u2132\u213a\u2190-\u21f3\u2200-\u22f1\u2300-\u2328\u232b-\u237b\u237d-\u239a\u2400-\u2426\u2440-\u244a\u249c-\u24e9\u2500-\u2595\u25a0-\u25f7\u2600-\u2613\u2619-\u2671\u2701-\u2704\u2706-\u2709\u270c-\u2727\u2729-\u274b\u274d\u274f-\u2752\u2756\u2758-\u275e\u2761-\u2767\u2794\u2798-\u27af\u27b1-\u27be\u2800-\u28ff\u2e80-\u2e99\u2e9b-\u2ef3\u2f00-\u2fd5\u2ff0-\u2ffb\u3004\u3012\u3013\u3020\u3036\u3037\u303e\u303f\u309b\u309c\u3190\u3191\u3196-\u319f\u3200-\u321c\u322a-\u3243\u3260-\u327b\u327f\u328a-\u32b0\u32c0-\u32cb\u32d0-\u32fe\u3300-\u3376\u337b-\u33dd\u33e0-\u33fe\ua490-\ua4a1\ua4a4-\ua4b3\ua4b5-\ua4c0\ua4c2-\ua4c4\ua4c6\ufb29\ufe62\ufe64-\ufe66\ufe69\uff04\uff0b\uff1c-\uff1e\uff3e\uff40\uff5c\uff5e\uffe0-\uffe6\uffe8-\uffee\ufffc\ufffd]');
			return re.test(c + '');
		},

		isPunctuation: function (c) {
			return this._UTFPunctuationsString.indexOf(c) >= 0;
		},

		isPrintableChar: function (c) {
			if ((!this.isLetterOrDigit(c) && !this.isPunctuation(c)) && !this.isSymbol(c)) {
				return (c === ' ');
			}
			return true;
		},

		isAscii: function (c) {
			return (c >= '!') && (c <= '~');
		},

		isAsciiLetter: function (c) {
			return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
		},

		isUpper: function (c) {
			return c.toUpperCase() === c;
		},

		isLower: function (c) {
			return c.toLowerCase() === c;
		},

		isAlphanumeric: function (c) {
			return !this.isLetter(c) ? this.isDigit(c) : true;
		},

		isAciiAlphanumeric: function (c) {
			if (((c < '0') || (c > '9')) && ((c < 'A') || (c > 'Z'))) {
				if (c >= 'a') {
					return (c <= 'z');
				}
				return false;
			}
			return true;
		},

		setChar: function (input, ch, pos) {
			if (pos >= input.length || pos < 0) {
				return input;
			}
			return '' || input.substr(0, pos) + ch + input.substr(pos + 1);
		}
	});

	var c__escapeArr1 = ['\n', '\r', '"', '@', '+', '\'', '<', '>', '%', '{', '}'],
		c__escapeArr2 = ["!ESC!NN!", "!ESC!RR!", "!ESC!01!", "!ESC!02!", "!ESC!03!", "!ESC!04!", "!ESC!05!", "!ESC!06!", "!ESC!07!", "!ESC!08!", "!ESC!09!"],
		c__escapeArr3 = ["(\n)", "(\r)", "(\")", "(@)", "(\\+)", "(')", "(\\<)", "(\\>)", "(%)", "(\\{)", "(\\})"];

	if (!$.wij) {
		$.extend({ wij: {
			charValidator: new wijCharValidator(),
			encodeString: function (s) {
				for (var i = 0; i < c__escapeArr1.lemgth; i++) {
					var r = /c__escapeArr3[i]/g;
					s = s.replace(r, c__escapeArr2[i]);
				}
				return s;
			},
			decodeString: function (s) {
				if (s === "") {
					return;
				}
				for (var i = 0; i < c__escapeArr2.length; i++) {
					var r = /c__escapeArr2[i]/g;
					s = s.replace(r, c__escapeArr1[i]);
				}
				return s;
			}
		}
		});
	};

})(jQuery);

__wijReadOptionEvents = function (eventsArr, widgetInstance) {
	// handle option events
	for (var k = 0; k < eventsArr.length; k++) {
		if (widgetInstance.options[eventsArr[k]] != null)
			widgetInstance.element.bind(eventsArr[k], widgetInstance.options[eventsArr[k]]);
	}
	//handle option event names separated by space, like: "afterexpand aftercollapse"
	for (k in widgetInstance.options) {
		if (k.indexOf(" ") != -1) {
			// possible multiple events separated by space:
			var arr = k.split(" ");
			for (var j = 0; j < arr.length; j++) {
				if (arr[j].length > 0)
					widgetInstance.element.bind(arr[j], widgetInstance.options[k]);
			}
		}
	}
};

/*globals window document jQuery */
/*
*
* Wijmo Library 1.3.0
* http://wijmo.com/
*
* Copyright(c) ComponentOne, LLC.  All rights reserved.
* 
* Dual licensed under the MIT or GPL Version 2 licenses.
* licensing@wijmo.com
* http://www.wijmo.com/license
*
* * Wijmo SuperPanel widget.
* 
* Depends:
*	jquery.ui.core.js
*	jquery.ui.widget.js
*	jquery.ui.resizable.js
*	jquery.ui.draggable.js
*	jquery.effects.core.js
*	jquery.mousewheel.js
*
*/
(function ($) {
	"use strict";
	var uiSuperPanelClasses = "wijmo-wijsuperpanel " + "ui-widget " + "ui-widget-content",
		rounderClass = "ui-corner-all",
		uiStateDisabled = "ui-state-disabled",
		uiStateHover = "ui-state-hover",
		uiStateActive = "ui-state-active",
		uiStateDefault = "ui-state-default",
		scrollerHandle = "wijmo-wijsuperpanel-handle",
		hbarContainerCSS = "wijmo-wijsuperpanel-hbarcontainer",
		vbarContainerCSS = "wijmo-wijsuperpanel-vbarcontainer",
		innerElementHtml =
				"<div class='wijmo-wijsuperpanel-statecontainer'>" +
				"<div class='wijmo-wijsuperpanel-contentwrapper'>" +
				"<div class='wijmo-wijsuperpanel-templateouterwrapper'></div>" +
				"</div>" +
				"</div>",
		hbarHtml = "<div class='wijmo-wijsuperpanel-hbarcontainer ui-widget-header'>" +
				"<div class='wijmo-wijsuperpanel-handle ui-state-default ui-corner-" +
				"all'><span class='ui-icon ui-icon-grip-solid-vertical'></span></div>" +
				"<div class='wijmo-wijsuperpanel-hbar-buttonleft ui-state-default " +
				"ui-corner-bl'><span class='ui-icon ui-icon-triangle-1-w'></span></div>" +
				"<div class='wijmo-wijsuperpanel-hbar-buttonright ui-state-default " +
				"ui-corner-br'><span class='ui-icon ui-icon-triangle-1-e'></span></div>" +
				"</div>",
		vbarHtml = "<div class='wijmo-wijsuperpanel-vbarcontainer ui-widget-header'>" +
				"<div class='wijmo-wijsuperpanel-handle ui-state-default ui-corner-all'" +
				"><span class='ui-icon ui-icon-grip-solid-horizontal'></span></div>" +
				"<div class='wijmo-wijsuperpanel-vbar-buttontop ui-state-default " +
				"ui-corner-tr'><span class='ui-icon ui-icon-triangle-1-n'></span></div>" +
				"<div class='wijmo-wijsuperpanel-vbar-buttonbottom ui-state-default " +
				"ui-corner-br'><span class='ui-icon ui-icon-triangle-1-s'></span></div>" +
				"</div>",
		hButtons = "<div class='ui-state-default wijmo-wijsuperpanel-button " +
				"wijmo-wijsuperpanel-buttonleft'><span class='ui-icon " +
				"ui-icon-carat-1-w'></span></div><div class='ui-state-default" +
				" wijmo-wijsuperpanel-button wijmo-wijsuperpanel-buttonright'>" +
				"<span class='ui-icon ui-icon-carat-1-e'></span></div>",
		vButtons = "<div class='ui-state-default wijmo-wijsuperpanel-button" +
		" wijmo-wijsuperpanel-buttontop'><span class='ui-icon ui-icon-carat-1-n'>" +
				"</span></div><div class='ui-state-default wijmo-wijsuperpanel-button" +
				" wijmo-wijsuperpanel-buttonbottom'><span class='ui-icon" +
				" ui-icon-carat-1-s'></span></div>";

	$.widget("wijmo.wijsuperpanel", {
		options: {
			/// <summary>
			/// This value determines whether the wijsuperpanel can be resized. 
			/// Default: false.
			/// Type: Boolean.
			/// </summary>
			allowResize: false,
			/// <summary>
			/// This value determines whether wijsuperpanel to automatically refresh 
			/// when content size or wijsuperpanel size are changed.
			/// Default: false.
			/// Type: Boolean.
			/// </summary>
			autoRefresh: false,
			/// <summary>
			/// The animation properties of wijsuperpanel scrolling.
			/// Type: Object.
			/// </summary>
			/// <remarks>
			/// Set this options to null to disable animation.
			/// </remarks>
			animationOptions: {
				/// <summary>
				/// This value determines whether to queue animation operations.
				/// Default: false.
				/// Type: Boolean.
				/// </summary>
				queue: false,
				/// <summary>
				/// This value determines whether to disable animation operations.
				/// Default: false.
				/// Type: Boolean.
				/// </summary>
				disabled: false,
				/// <summary>
				/// This value sets the animation duration of the scrolling animation.
				/// Default: 250.
				/// Type: Number.
				/// </summary>
				duration: 250,
				/// <summary>
				/// This value sets the animation easing of the scrolling animation.
				/// Default: undefined.
				/// Type: string.
				/// </summary>
				easing: undefined
			},
			/// <summary>
			/// The hScrollerActivating event handler. 
			/// A function called when horizontal scrollbar is activating.
			/// Default: null
			/// Type: Function
			/// Code example: 
			/// Supply a function as an option.
			/// $("#selector").wijsuperpanel({ hScrollerActivating: function (e, data) { } });
			/// Bind to the event by type: wijtreenodeClick
			/// $("#selector").bind("wijsuperpanelhScrollerActivating", function(e, data) { } );
			/// </summary>
			/// <param name="e" type="Object">
			/// jQuery.Event object.
			/// </param>
			/// <param name="data" type="Object">
			/// The data that relates to this event.
			/// data.direction: the direction of the scrollbar("horizontal" or "vertical").
			/// data.targetBarLen: the height of the horizontal scrollbar.
			/// data.contentLength: the height of the content.
			/// </param>
			hScrollerActivating: null,
			/// <summary>
			/// This option contains horizontal scroller settings.
			/// </summary>
			hScroller: {
				/// <summary>
				/// This value determines the position of the horizontal scroll bar. 
				/// Default: "bottom".
				/// Type: String.
				/// </summary>
				/// <remarks>
				/// Possible options are "bottom" and "top".
				/// "bottom" - The horizontal scroll bar is placed at the bottom of 
				/// the content area.
				/// "top" - The horizontal scroll bar is placed at the top of the 
				///content area.
				/// </remarks>
				scrollBarPosition: "bottom",
				/// <summary>
				/// This value determines the visibility of the horizontal scroll bar.
				/// Default: "auto".
				/// Type: String
				/// </summary>
				/// <remarks>
				/// Possible options are "auto", "visible" and "hidden".
				/// "auto" - Shows the scroll when needed.
				/// "visible" - Scroll bar will always be visible. It"s disabled 
				/// when not needed.
				/// "hidden" - Scroll bar will be hidden.
				/// </remarks>
				scrollBarVisibility: "auto",
				/// <summary>
				/// This value determines the scroll mode of horizontal scrolling. 
				/// Default: "scrollbar".
				/// Type: String.
				/// </summary>
				/// <remarks>
				/// Possible options are "scrollBar", "buttons", "buttonsHover" 
				/// and "edge".
				/// "scrollBar" - Scroll bars are used for scrolling.
				/// "buttons" - Scroll buttons are used for scrolling. 
				/// Scrolling occurs only when scroll buttons are clicked.
				/// "buttonsHover" - Scroll buttons are used for scrolling. 
				/// Scrolling occurs only when scroll buttons are hovered.
				/// "edge" - Scrolling occurs when the mouse is moving to the edge
				/// of the content area.
				/// Scroll modes can be combined with each other. 
				/// For example, scrollMode: "scrollbar,scrollbuttons" will enable 
				/// both a scrollbar and scroll buttons.
				/// </remarks>
				scrollMode: "scrollBar",
				/// <summary>
				/// This value determines the horizontal scrolling position of
				/// wijsuperpanel.
				/// Default: null.
				/// Type: Number.
				/// </summary>
				scrollValue: null,
				/// <summary>
				/// This value sets the maximum value of horizontal scroller.
				/// Default: 100.
				/// Type: Number.
				/// </summary>
				scrollMax: 100,
				/// <summary>
				/// This value sets the minimum value of horizontal scroller.
				/// Default: 0.
				/// Type: Number.
				/// </summary>
				scrollMin: 0,
				/// <summary>
				/// This value sets the large change value of horizontal scroller.
				/// Default: null.
				/// Type: Number.
				/// </summary>
				/// <remarks>
				/// wijsuperpanel will scroll a large change when a user clicks on the 
				/// tracks of scroll bars or presses left or right arrow keys on the 
				/// keyboard with the shift key down.
				/// When scrollLargeChange is null, wijsuperpanel will scroll 
				/// the width of content.
				/// </remarks>
				scrollLargeChange: null,
				/// <summary>
				/// This value sets the small change value of horizontal scroller.
				/// Default: null. 
				/// Type: Number.
				/// </summary>
				/// <remarks>
				/// wijsuperpanel will scroll a small change when a user clicks on 
				/// the arrows of scroll bars, clicks or hovers scroll buttons, 
				/// presses left or right arrow keys on keyboard, 
				/// and hovers on the edge of wijsuperpanel.
				/// When scrollSmallChange is null, wijsuperpanel will scroll half of 
				/// the width of content.
				/// </remarks>
				scrollSmallChange: null,
				/// <summary>
				/// This value sets the minimum length, in pixel, of the horizontal 
				/// scroll bar thumb button.
				/// Default: 6.
				/// Type: Number.
				/// </summary>
				scrollMinDragLength: 6,
				/// <summary>
				/// This object determines the increase button position. 
				/// Default: null.
				/// Type: Object.
				/// </summary>
				/// <remarks>
				/// Please look at the options for jquery.ui.position.js for more info.
				/// </remarks>
				increaseButtonPosition: null,
				/// <summary>
				/// This object determines the decrease button position.
				/// Default: 0.
				/// Type: Object.
				/// </summary>
				decreaseButtonPosition: null,
				/// <summary>
				/// This value sets the width of horizontal hovering edge 
				/// which will trigger the horizontal scrolling.
				/// Default: 20.
				/// Type: Number.
				/// </summary>
				hoverEdgeSpan: 20,
				/// <summary>
				/// The number specifies the value to add to smallchange or largechange
				/// when scrolling the first step(scrolling from scrollMin).
				/// Default: 0.
				/// Type: Number.
				/// </summary>
				firstStepChangeFix: 0

			},
			/// <summary>
			/// A value determins whether wijsuperpanel provides 
			/// keyboard scrolling support.
			/// Default: false.
			/// Type: Boolean.
			/// </summary>
			keyboardSupport: false,
			/// <summary>
			/// This value determines the time interval to call the scrolling
			/// function when doing continuous scrolling.
			/// Default: 100.
			/// Type: Number.
			/// </summary>
			keyDownInterval: 100,
			/// <summary>
			/// This value determines whether wijsuperpanel has mouse wheel support.
			/// Default: true.
			/// Type: Boolean.
			/// </summary>
			/// <remarks>
			/// Mouse wheel plugin is needed to support this feature.
			/// </remarks>
			mouseWheelSupport: true,
			/// <summary>
			/// This value determines whether to fire the mouse wheel event 
			/// when wijsuperpanel is scrolled to the end.
			/// Default: true.
			/// Type: Boolean.
			/// </summary>
			bubbleScrollingEvent: true,
			/// <summary>
			/// This option determines the behavior of resizable widget. 
			/// See JQuery UI resizable options document.
			/// Type: Object.
			/// </summary>
			resizableOptions: {
				handles: "all",
				helper: "ui-widget-content wijmo-wijsuperpanel-helper"
			},
			/// <summary>
			/// Resized event handler. A function gets called when resized event is fired.
			/// Default: null.
			/// Type: Function.
			/// code example:
			/// Supply a callback function to handle the resized event:
			/// $("#element").wijsuperpanel({ resized: funtion() { dosometing } });
			/// Bind to the event by type:
			/// $("#element").bind("wijsuperpanelresized", funtion() { dosometing });
			/// </summary>
			resized: null,
			/// <summary>
			/// This function gets called when thumb buttons of scrollbars dragging stops.
			/// Default: null.
			/// Type: Function.
			/// code example:
			/// Supply a callback function to handle the dragstop event:
			/// $("#element").wijsuperpanel({ dragStop: funtion(e, data) { dosometing } });
			/// Bind to the event by type:
			/// $("#element").bind("wijsuperpaneldragstop", funtion(e, data) { dosometing });
			/// <param name="e" type="EventObj">
			/// EventObj relates to this event.
			/// </param>
			/// <param name="data" type="Object">
			/// The data with this event.
			/// data.dir: data.draghandle is the direction of the scrolling action. 
			/// Possible values: "v"(vertical) and "h"(horizontal).	
			/// </param>
			/// </summary>
			dragStop: null,
			/// <summary>
			/// This function gets called after panel is painted.
			/// Default: null.
			/// Type: Function.
			/// code example:
			/// Supply a callback function to handle the painted event:
			/// $("#element").wijsuperpanel({ painted: funtion() { dosometing } });
			/// Bind to the event by type:
			/// $("#element").bind("wijsuperpanelpainted", funtion() { dosometing });
			/// </summary>
			painted: null,
			/// <summary>
			/// Scrolling event handler. A function called before scrolling occurs.
			/// Default: null.
			/// Type: Function.
			/// code example:
			/// Supply a callback function to handle the scrolling event:
			/// $("#element").wijsuperpanel({ scrolling: funtion(e, data) { dosometing } });
			/// Bind to the event by type:
			/// $("#element").bind("wijsuperpanelscrolling", funtion(e, data) { dosometing });
			/// </summary>
			/// <param name="e" type="Object">
			/// jQuery.Event object.
			/// </param>
			/// <param name="data" type="Object">
			/// The data with this event.
			/// data.oldValue: The scrollValue before scrolling occurs.
			/// data.newValue: The scrollValue after scrolling occurs.
			/// data.dir: The direction of the scrolling action. 
			/// Possible values: "v"(vertical) and "h"(horizontal).
			/// data.beforePosition: The position of content before scrolling occurs.
			/// </param>
			scrolling: null,
			/// <summary>
			/// Scrolled event handler.  A function called after scrolling occurs.
			/// Default: null.
			/// Type: Function.
			/// code example:
			/// Supply a callback function to handle the scrolled event:
			/// $("#element").wijsuperpanel({ scrolled: funtion(e, data) { dosometing } });
			/// Bind to the event by type:
			/// $("#element").bind("wijsuperpanelscrolled", funtion(e, data) { dosometing });
			/// </summary>
			/// <param name="e" type="Object">
			/// jQuery.Event object.
			/// </param>
			/// <param name="data" type="Object">
			/// The data with this event.
			/// data.dir: The direction of the scrolling action. 
			/// Possible values: "v"(vertical) and "h"(horizontal).
			/// data.beforePosition: The position of content before scrolling occurs.
			/// data.afterPosition: The position of content after scrolling occurs.
			/// </param>
			scrolled: null,
			/// <summary>
			/// This value determines whether to show the rounded corner of wijsuperpanel.
			/// Default: true.
			/// Type: Boolean.
			/// </summary>
			showRounder: true,
			/// <summary>
			/// The vScrollerActivating event handler. 
			/// A function called when vertical scrollbar is activating.
			/// Default: null
			/// Type: Function
			/// Code example: 
			/// Supply a function as an option.
			/// $("#selector").wijsuperpanel({ vScrollerActivating: function (e, data) { } });
			/// Bind to the event by type: wijtreenodeClick
			/// $("#selector").bind("wijsuperpanelvScrollerActivating", function(e, data) { } );
			/// </summary>
			/// <param name="e" type="Object">
			/// jQuery.Event object.
			/// </param>
			/// <param name="data" type="Object">
			/// The data that relates to this event.
			/// data.direction: the direction of the scrollbar("horizontal" or "vertical").
			/// data.targetBarLen: the width of the vertical scrollbar.
			/// data.contentLength: the width of the content.
			/// </param>
			vScrollerActivating: null,
			/// <summary>
			/// This option contains vertical scroller settings.
			/// </summary>			
			vScroller: {
				/// <summary>
				/// This value determines the position of vertical scroll bar. 
				/// Default: "right".
				/// Type: String.
				/// </summary>
				/// <remarks>
				/// Possible options are: "left", "right".
				/// "left" - The vertical scroll bar is placed at the 
				/// left side of the content area.
				/// "right" - The vertical scroll bar is placed at the 
				/// right side of the content area.
				/// </remarks>
				scrollBarPosition: "right",
				/// <summary>
				/// This value determines the visibility of the vertical scroll bar.
				/// Default.: "auto". 
				/// Type: String.
				/// </summary>
				/// <remarks>
				/// Possible options are "auto", "visible" and "hidden".
				/// "auto" - Shows the scroll bar when needed.
				/// "visible" - Scroll bar will always be visible. 
				/// It"s disabled when not needed.
				/// "hidden" - Scroll bar will be shown.
				/// </remarks>
				scrollBarVisibility: "auto",
				/// <summary>
				/// This value determines the scroll mode of vertical scrolling. 
				/// Default: "scrollbar".
				/// Type: String.
				/// </summary>
				/// <remarks>
				/// Possible options are: "scrollBar", "buttons", 
				/// "buttonsHover" and "edge".
				/// "scrollBar" - Scroll bars are used for scrolling.
				/// "buttons" - Scroll buttons are used for scrolling. 
				/// Scrolling occurs only when scroll buttons are clicked.
				/// "buttonsHover" - Scroll buttons are used for scrolling. 
				/// Scrolling occurs only when scroll buttons are hovered.
				/// "edge" - Scrolling occurs when the mouse is moving to 
				/// the edge of the content area.
				/// Scroll modes can be combined with each other. 
				/// For example, vScrollMode: "scrollbar,scrollbuttons" will enable 
				/// both a scrollbar and scroll buttons.
				/// </remarks>
				scrollMode: "scrollBar",
				/// <summary>
				/// This value determines the vertical scrolling position of
				/// wijsuperpanel.
				/// Default: null.
				/// Type: Number.
				/// </summary>
				scrollValue: null,
				/// <summary>
				/// This value sets the maximum value of vertical scroller.
				/// Default: 100.
				/// Type: Number.
				/// </summary>
				scrollMax: 100,
				/// <summary>
				/// This value sets the minimum value of vertical scroller.
				/// Default: 0.
				/// Type: Number.
				/// </summary>
				scrollMin: 0,
				/// <summary>
				/// This value sets the large change value of vertical scroller. 
				/// Default: null.
				/// Type: Number.
				/// </summary>
				/// <remarks>
				/// wijsuperpanel will scroll a large change when a user clicks 
				/// on the tracks of scroll bars or presses left or right arrow keys 
				/// on the keyboard with the shift key down.
				/// When scrollLargeChange is null, wijsuperpanel 
				/// will scroll the height of content.
				/// </remarks>
				scrollLargeChange: null,
				/// <summary>
				/// This value sets the small change value of vertical scroller. 
				/// Default: null.
				/// Type: Number.
				/// </summary>
				/// <remarks>
				/// wijsuperpanel will scroll a small change when a user clicks on the 
				/// arrows of scroll bars, clicks or hovers scroll buttons, presses left
				/// or right arrow keys on keyboard, and hovers on the edge of 
				/// wijsuperpanel.
				/// When scrollSmallChange is null, wijsuperpanel will scroll half of 
				/// the height of content.
				/// </remarks>
				scrollSmallChange: null,
				/// <summary>
				/// This value sets the minimum length, in pixel, of the vertical 
				/// scroll bar thumb button.
				/// Default: 6.
				/// Type: Number
				/// </summary>
				scrollMinDragLength: 6,
				/// <summary>
				/// This object determines the increase button position. 
				/// Default: null.
				/// Type: Object.
				/// </summary>
				/// <remarks>
				/// Please look at the options for jquery.ui.position.js for more info.
				/// </remarks>
				increaseButtonPosition: null,
				/// <summary>
				/// This object determines the decrease button position.
				/// Default: 0.
				/// Type: Object.
				/// </summary>
				/// <remarks>
				/// Please look at the options for jquery.ui.position.js for more info.
				/// </remarks>
				decreaseButtonPosition: null,
				/// <summary>
				/// This value sets the width of horizontal hovering edge 
				/// which will trigger the vertical scrolling.
				/// Default: 20.
				/// Type: Number.
				/// </summary>
				hoverEdgeSpan: 20,
				/// <summary>
				/// The value to add to small change or largechange when scrolling 
				/// the first step(scrolling from value 0).
				/// Default: 0.
				/// Type: Number.
				/// </summary>
				firstStepChangeFix: 0
			}
		},

		_setOption: function (key, value) {

			var self = this,
			o = self.options,
			f = self._fields(),
			hd = f.hbarDrag,
			vd = f.vbarDrag,
			r = f.resizer;

			// override existing 
			if (key === "animationOptions") {
				value = $.extend(o.animationOptions, value);
			}
			else if (key === "hScroller") {
				if (value.scrollLargeChange !== undefined &&
				value.scrollLargeChange !== null) {
					self._autoHLarge = false;
				}
				value = $.extend(o.hScroller, value);
			}
			else if (key === "vScroller") {
				if (value.scrollLargeChange !== undefined &&
				value.scrollLargeChange !== null) {
					self._autoVLarge = false;
				}
				value = $.extend(o.vScroller, value);
			}
			else if (key === "resizableOptions") {
				value = $.extend(self.resizableOptions, value);
			}
			$.Widget.prototype._setOption.apply(self, arguments);
			if ($.isPlainObject(value)) {
				self.options[key] = value;
			}
			switch (key) {
			case "allowResize":
				self._initResizer();
				break;
			case "disabled":
				if (value) {
					if (hd !== undefined) {
						hd.draggable("disable");
					}
					if (vd !== undefined) {
						vd.draggable("disable");
					}
					if (r !== undefined) {
						r.resizable("disable");
					}
				}
				else {
					if (hd !== undefined) {
						hd.draggable("enable");
					}
					if (vd !== undefined) {
						vd.draggable("enable");
					}
					if (r !== undefined) {
						r.resizable("enable");
					}
				}
				break;
			case "mouseWheelSupport":
			case "keyboardSupport":
				self._bindElementEvents(self, f, self.element, o);
				break;
			}
			return self;
		},

		_create: function () {
			var self = this, o = self.options;
			o.vScroller.dir = "v";
			o.hScroller.dir = "h";
			self.paintPanel();
			self._initResizer();
			if (self.options.disabled) {
				self.disable();
			}
			self._detectAutoRefresh();
		},

		_detectAutoRefresh: function () {
			// register with auto fresh.
			var self = this, panels = $.wijmo.wijsuperpanel.panels;
			if (panels === undefined) {
				panels = [];
				$.wijmo.wijsuperpanel.panels = panels;
			}
			panels.push(self);
			// start timer to monitor content.
			if (self.options.autoRefresh) {
				if (!$.wijmo.wijsuperpanel.setAutoRefreshInterval) {
					$.wijmo.wijsuperpanel.setAutoRefreshInterval =
					self._setAutoRefreshInterval;
					$.wijmo.wijsuperpanel.setAutoRefreshInterval();
				}
			}
		},

		_setAutoRefreshInterval: function () {
			var interval = $.wijmo.wijsuperpanel.autoRereshInterval,
			panels = $.wijmo.wijsuperpanel.panels,
			intervalID = window.setInterval(function () {
				window.clearInterval(intervalID);
				var count = panels.length, toContinue = false, i, panel,
				mainElement, autoRefresh, ele, mark;
				for (i = 0; i < count; i++) {
					panel = panels[i];
					mainElement = panel.element[0];
					autoRefresh = panel.options.autoRefresh;
					if (autoRefresh) {
						toContinue = true;
					}
					ele = panel.getContentElement();
					mark = panel._paintedMark;
					if (panel.options.autoRefresh && ele.is(":visible") &&
					(mark === undefined ||
					mark.width !== ele[0].offsetWidth ||
					mark.height !== ele[0].offsetHeight ||
					mark.mainWidth !== mainElement.offsetWidth ||
					mark.mainHeight !== mainElement.offsetHeight)) {
						panel.paintPanel();
					}
				}
				if (toContinue) {
					window.setTimeout($.wijmo.wijsuperpanel.setAutoRefreshInterval, 0);
				}
			}, interval === undefined ? 500 : interval);
		},

		destroy: function () {
			/// <summary>
			/// Destroys wijsuperpanel widget and reset the DOM element.
			/// </summary>

			var self = this, f = self._fields(), ele = self.element,
			buttons, templateWrapper;
			// remove this widget from panels array.
			$.wijmo.wijsuperpanel.panels =
			$.grep($.wijmo.wijsuperpanel.panels, function (value) {
				return value !== self;
			});
			if (!f.initialized) {
				return;
			}
			if (self._radiusKey) {
				self.element.css(self._radiusKey, "");
			}
			if (f.intervalID !== undefined) {
				window.clearInterval(f.intervalID);
				f.intervalID = undefined;
			}
			// destory widgets
			if (f.resizer !== undefined) {
				f.resizer.resizable("destroy");
			}
			if (f.hbarContainer !== undefined) {
				f.hbarDrag.remove();
				f.hbarContainer.unbind("." + self.widgetName);
			}
			if (f.vbarContainer !== undefined) {
				f.vbarDrag.remove();
				f.vbarContainer.unbind("." + self.widgetName);
			}
			ele.unbind("." + self.widgetName);
			f.contentWrapper.unbind("." + self.widgetName);
			buttons = f.stateContainer.find(">.wijmo-wijsuperpanel-button");
			buttons.unbind("." + self.widgetName);
			templateWrapper = f.templateWrapper;
			templateWrapper.contents().each(function (index, e) {
				ele.append(e);
			});
			f.stateContainer.remove();
			if (f.tabindex) {
				ele.removeAttr("tabindex");
			}
			ele.removeClass(uiSuperPanelClasses + " " + rounderClass);
			$.Widget.prototype.destroy.apply(self, arguments);
		},

		_fields: function () {
			var self = this, ele = self.element, key = self.widgetName + "-fields",
			d = self._fieldsStore;
			if (d === undefined) {
				d = {};
				ele.data(key, d);
				self._fieldsStore = d;
			}
			return d;
		},

		_hasMode: function (scroller, mode) {
			var modes = scroller.scrollMode.split(",");
			modes = $.map(modes, function (n) {
				return $.trim(n).toLowerCase();
			});

			return $.inArray(mode.toLowerCase(), modes) > -1;
		},

		_bindElementEvents: function (self, f, ele, o) {
			// mouse move only edge mode is used.
			var hEdge = self._hasMode(o.hScroller, "edge"),
			vEdge = self._hasMode(o.vScroller, "edge"),
			wn = self.widgetName;

			if (hEdge || vEdge) {
				if (self._mousemoveBind === undefined) {
					self._mousemoveBind = true;
					ele.bind("mousemove." + wn, self, self._contentMouseMove);
					ele.bind("mouseleave." + wn, null, function () {
						self._clearInterval();
					});
				}
			}
			else {
				ele.unbind("mousemove", self._contentMouseMove);
				self._mousemoveBind = undefined;
			}
			if (o.mouseWheelSupport) {
				if (self._mouseWheelBind === undefined) {
					self._mouseWheelBind = true;
					ele.bind("mousewheel." + wn, self, self._panelMouseWheel);
				}
			}
			else {
				self.element.unbind("mousewheel", self._panelMouseWheel);
				self._mouseWheelBind = undefined;
			}
			if (o.keyboardSupport) {
				if (self._keyboardBind === undefined) {
					self._keyboardBind = true;
					ele.bind("keydown." + wn, self, self._panelKeyDown);
				}
			}
			else {
				ele.unbind("keydown", self._panelKeyDown);
				self._keyboardBind = undefined;
			}
		},

		_dragStop: function (e, self, dir) {
			// Handles mouse drag stop event of thumb button.

			var data = {
				dragHandle: dir
			};
			self._trigger("dragStop", e, data);
		},

		_contentMouseMove: function (e) {
			// Handles mouse move event of content area.
			// Edge hover scrolling is handled in this method.

			var self = e.data, o = self.options, hScroller, vScroller,
			contentWrapper, f, hMode, vMode, mousePagePosition, off, left, top,
			hEdge, vEdge, innerHeight, innerWidth, dir;
			if (o.disabled) {
				return;
			}
			hScroller = o.hScroller;
			vScroller = o.vScroller;
			contentWrapper = $(e.currentTarget);
			f = self._fields();
			hMode = self._hasMode(hScroller, "edge");
			vMode = self._hasMode(vScroller, "edge");
			self._clearInterval();
			mousePagePosition = {
				X: e.pageX,
				Y: e.pageY
			};
			off = contentWrapper.offset();
			left = off.left;
			top = off.top;
			left = mousePagePosition.X - left;
			top = mousePagePosition.Y - top;
			hEdge = hScroller.hoverEdgeSpan;
			vEdge = vScroller.hoverEdgeSpan;
			innerHeight = contentWrapper.innerHeight();
			innerWidth = contentWrapper.innerWidth();
			dir = "";
			if (hMode) {
				if (left < hEdge) {
					dir = "left";
				}
				if (left > (innerWidth - hEdge)) {
					dir = "right";
				}
			}
			if (vMode) {
				if (top < vEdge) {
					dir = "top";
				}
				if (top > (innerHeight - vEdge)) {
					dir = "bottom";
				}
			}
			self._setScrollingInterval(f, dir, self, false);
		},

		_setScrollingInterval: function (f, dir, self, large) {
			var o = self.options;
			if (dir.length > 0) {
				f.internalFuncID = window.setInterval(function () {
					self._doScrolling(dir, self, large);
				}, o.keyDownInterval);
			}
		},

		_scrollButtonMouseOver: function (e) {
			// Scroll buttons mouse over event handler.

			var self = e.data, button;
			if (self.options.disabled) {
				return;
			}
			button = $(e.currentTarget);
			if (!button.hasClass(uiStateDisabled)) {
				button.bind("mouseout." + self.widgetName, self, self._buttonMouseOut)
				.bind("mousedown." + self.widgetName, self, self._buttonMouseDown)
				.bind("mouseup." + self.widgetName, self, self._buttonMouseUp)
				.addClass(uiStateHover);
				self._buttonScroll(button, self, "buttonshover");
			}
		},

		_buttonScroll: function (button, self, mode) {
			// Do button scroll.

			var dir = "", o = self.options,
			f = self._fields(),
			hMode = self._hasMode(o.hScroller, mode),
			vMode = self._hasMode(o.vScroller, mode);

			if (button.hasClass("wijmo-wijsuperpanel-buttonleft") && hMode) {
				dir = "left";
			}
			else if (button.hasClass("wijmo-wijsuperpanel-buttonright") && hMode) {
				dir = "right";
			}
			else if (button.hasClass("wijmo-wijsuperpanel-buttontop") && vMode) {
				dir = "top";
			}
			else if (button.hasClass("wijmo-wijsuperpanel-buttonbottom") && vMode) {
				dir = "bottom";
			}
			if (dir.length > 0) {
				self._clearInterval();
				self._doScrolling(dir, self, true);
				self._setScrollingInterval(f, dir, self, true);
			}
		},

		_buttonMouseDown: function (e) {
			var self = e.data, button;
			if (self.options.disabled) {
				return;
			}
			button = $(e.currentTarget);
			if (!button.hasClass(uiStateDisabled)) {
				button.addClass(uiStateActive);
				self._buttonScroll(button, self, "buttons");
			}
		},

		_buttonMouseUp: function (e) {
			var self = e.data, button = $(e.currentTarget);
			button.removeClass("ui-state-active");
			self._clearInterval();
		},

		_buttonMouseOut: function (e) {
			var self = e.data, button = $(e.currentTarget);
			button.unbind("mouseout", self._buttonMouseOut)
			.unbind("mousedown", self._buttonMouseDown)
			.unbind("mouseup", self._buttonMouseUp)
			.removeClass(uiStateHover)
			.removeClass(uiStateActive);
			self._clearInterval();
		},

		_panelKeyDown: function (e) {
			// Key down handler.

			var self = e.data, o = self.options, shift, keycode;
			if (!o.keyboardSupport || o.disabled) {
				return;
			}
			shift = e.shiftKey;
			keycode = e.keyCode;
			if (keycode === $.ui.keyCode.LEFT) {
				self._doScrolling("left", self, shift);
			}
			else if (keycode === $.ui.keyCode.RIGHT) {
				self._doScrolling("right", self, shift);
			}
			else if (keycode === $.ui.keyCode.UP) {
				self._doScrolling("top", self, shift);
			}
			else if (keycode === $.ui.keyCode.DOWN) {
				self._doScrolling("bottom", self, shift);
			}
			e.stopPropagation();
			e.preventDefault();
		},

		_draggingInternal: function (data, self, scroller, originalElement) {
			var dir = scroller.dir, h = dir === "h",
			key = h ? "left" : "top",
			//the parameter from draggable widget is supposed to be used instead of the style property of html element
			//left = parseFloat(originalElement[0].style[key].replace("px", "")) -
			left = data.position[key] -
			self._getScrollContainerPadding(key),
			track = self._getTrackLen(dir) -
			//originalElement[h ? "outerWidth" : "outerHeight"](),
			originalElement[h ? "outerWidth" : "outerHeight"](true),
			proportion = left / track,
			topValue = (scroller.scrollMax - scroller.scrollLargeChange + 1),
			v = proportion * topValue, data;
			if (v < scroller.scrollMin) {
				v = scroller.scrollMin;
			}
			if (v > topValue) {
				v = topValue;
			}
			data = {
				oldValue: scroller.scrollValue,
				newValue: v,
				dir: dir
			};
			if (!self._scrolling(true, self, data)) {
				// event is canceled in scrolling.
				return;
			}
			scroller.scrollValue = v;
			self._setDragAndContentPosition(true, false, dir, "dragging");
		},

		_dragging: function (e, data, self) {
			var o = self.options, originalElement = $(e.target),
			p = originalElement.parent();
			if (p.hasClass(hbarContainerCSS)) {
				self._draggingInternal(data, self, o.hScroller, originalElement);
			}
			else {
				self._draggingInternal(data, self, o.vScroller, originalElement);
			}
		},

		_panelMouseWheel: function (e, delta) {
			var self = e.data, o = self.options, originalElement, dir, onHbar,
			hScroller, vScroller, scrollEnd;
			if (!o.mouseWheelSupport || o.disabled) {
				return;
			}
			//var f = self._fields();
			//var scrollerWrapper = f.stateContainer;
			//var hbarContainer = f.hbarContainer;
			originalElement = $(e.srcElement || e.originalEvent.target);
			dir = "";
			onHbar = originalElement.closest("." + hbarContainerCSS, self.element)
			.size() > 0;
			hScroller = o.hScroller;
			vScroller = o.vScroller;
			if (delta > 0) {
				dir = onHbar ? "left" : "top";
			}
			else {
				dir = onHbar ? "right" : "bottom";
			}

			if (dir.length > 0) {
				self._doScrolling(dir, self);
			}
			scrollEnd = false;
			if (dir === "left") {
				scrollEnd = !self.hNeedScrollBar ||
				Math.abs(hScroller.scrollValue - hScroller.scrollMin) < 0.001;
			}
			if (dir === "right") {
				scrollEnd = !self.hNeedScrollBar || Math.abs(hScroller.scrollValue -
				(hScroller.scrollMax - self._getHScrollBarLargeChange() + 1)) < 0.001;
			}
			if (dir === "top") {
				scrollEnd = !self.vNeedScrollBar ||
				Math.abs(vScroller.scrollValue - vScroller.scrollMin) < 0.001;
			}
			if (dir === "bottom") {
				scrollEnd = !self.vNeedScrollBar || Math.abs(vScroller.scrollValue -
				(vScroller.scrollMax - self._getVScrollBarLargeChange() + 1)) < 0.001;
			}
			if (!scrollEnd || !o.bubbleScrollingEvent || dir === "left" ||
			 dir === "right") {
				e.stopPropagation();
				e.preventDefault();
			}
		},

		_documentMouseUp: function (e) {
			var self = e.data.self, ele = e.data.ele;
			ele.removeClass(uiStateActive);
			self._clearInterval();
			$(document).unbind("mouseup", self._documentMouseUp);
		},

		_scrollerMouseOver: function (e) {
			var self = e.data, originalElement, ele, addhover;
			if (self.options.disabled) {
				return;
			}
			originalElement = $(e.srcElement || e.originalEvent.target);
			ele = null;
			addhover = false;

			if (originalElement.hasClass(uiStateDefault)) {
				ele = originalElement;
				addhover = true;
			}
			else if (originalElement.parent().hasClass(uiStateDefault)) {
				ele = originalElement.parent();
				addhover = true;
			}
			else if (originalElement.hasClass(vbarContainerCSS) ||
			originalElement.hasClass(hbarContainerCSS)) {
				ele = originalElement;
			}

			if (ele !== undefined) {
				if (addhover) {
					ele.addClass(uiStateHover);
				}
				ele.bind("mouseout." + self.widgetName, self, self._elementMouseOut);
				ele.bind("mousedown." + self.widgetName, self, self._elementMouseDown);
				ele.bind("mouseup." + self.widgetName, self, self._elementMouseUp);
			}
		},

		_elementMouseUp: function (e) {
			var ele = $(e.currentTarget);
			//var self = e.data;
			ele.removeClass("ui-state-active");
		},

		_elementMouseDown: function (e) {
			var ele = $(e.currentTarget), self = e.data,
			scrollDirection, large, active, hbarDrag, pos, vbarDrag, pos2, f;
			if (self.options.disabled) {
				return;
			}
			scrollDirection = "";
			large = false;
			active = false;
			if (ele.hasClass("wijmo-wijsuperpanel-vbar-buttontop")) {
				scrollDirection = "top";
				active = true;
			}
			else if (ele.hasClass("wijmo-wijsuperpanel-vbar-buttonbottom")) {
				scrollDirection = "bottom";
				active = true;
			}
			else if (ele.hasClass("wijmo-wijsuperpanel-hbar-buttonleft")) {
				scrollDirection = "left";
				active = true;
			}
			else if (ele.hasClass("wijmo-wijsuperpanel-hbar-buttonright")) {
				scrollDirection = "right";
				active = true;
			}
			else if (ele.hasClass(scrollerHandle)) {
				ele.addClass("ui-state-active");
				return;
			}
			else if (ele.hasClass(hbarContainerCSS)) {
				hbarDrag = ele.find("." + scrollerHandle);
				pos = hbarDrag.offset();
				if (e.pageX < pos.left) {
					scrollDirection = "left";
				}
				else {
					scrollDirection = "right";
				}
				large = true;
			}
			else if (ele.hasClass(vbarContainerCSS)) {
				vbarDrag = ele.find("." + scrollerHandle);
				pos2 = vbarDrag.offset();
				if (e.pageY < pos2.top) {
					scrollDirection = "top";
				}
				else {
					scrollDirection = "bottom";
				}
				large = true;
			}
			self._clearInterval();
			self._doScrolling(scrollDirection, self, large);
			f = self._fields();
			self._setScrollingInterval(f, scrollDirection, self, large);
			if (active) {
				ele.addClass("ui-state-active");
			}
			$(document).bind("mouseup." + self.widgetName, {
				self: self,
				ele: ele
			}, self._documentMouseUp);
		},

		doScrolling: function (dir, large) {
			/// <summary>
			/// Do scrolling.
			/// </summary>
			/// <param name="dir" type="string">
			///   Scrolling direction. Options are: "left", "right", "top" and "bottom".
			/// </param>
			/// <param name="large" type="Boolean">
			/// Whether to scroll a large change.
			/// </param>

			this._doScrolling(dir, this, large);
		},

		_setScrollerValue: function (dir, scroller, smallChange, largeChange,
		isAdd, isLarge, self) {
			//var o = self.options;
			var vMin = scroller.scrollMin,
			change = isLarge ? largeChange : smallChange,
			value = scroller.scrollValue, t, vTopValue, firstStepChangeFix, data;
			if (!value) {
				value = vMin;
			}
			t = 0;
			if (isAdd) {
				vTopValue = scroller.scrollMax - largeChange + 1;
				if (Math.abs(value - vTopValue) < 0.001) {
					self._clearInterval();
					return false;
				}
				firstStepChangeFix = scroller.firstStepChangeFix;
				t = value + change;
				if (!isLarge && Math.abs(value - vMin) < 0.0001 &&
				!isNaN(firstStepChangeFix)) {
					t += firstStepChangeFix;
				}
				if (t > vTopValue) {
					t = vTopValue;
				}
			}
			else {
				if (Math.abs(value - vMin) < 0.001) {
					self._clearInterval();
					return false;
				}
				t = value - change;
				if (t < 0) {
					t = vMin;
				}
			}
			data = {
				oldValue: scroller.scrollValue,
				newValue: t,
				direction: dir,
				dir: scroller.dir
			};
			if (!self._scrolling(true, self, data)) {
				return false;
			}
			scroller.scrollValue = t;
			return true;
		},

		_doScrolling: function (dir, self, large) {
			// Does wijsuperpanel scrolling.
			// <param name="dir" type="String">
			// Scroll direction. 
			// Options are: "left", "right", "top" and "bottom".
			// </param>
			// <param name="self" type="jQuery">
			// Pointer to the wijsuperpanel widget instance.
			// </param>
			// <param name="large" type="Boolean">
			// Whether to scroll a large change.
			// </param>

			var o = self.options,
			vScroller = o.vScroller,
			hScroller = o.hScroller,
			vSmall = self._getVScrollBarSmallChange(),
			vLarge = self._getVScrollBarLargeChange(),
			hLarge = self._getHScrollBarLargeChange(),
			hSmall = self._getHScrollBarSmallChange();

			if (dir === "top" || dir === "bottom") {
				if (!self._setScrollerValue(dir, vScroller, vSmall, vLarge,
				dir === "bottom", large, self)) {
					return;
				}
				dir = "v";
			}
			else if (dir === "left" || dir === "right") {
				if (!self._setScrollerValue(dir, hScroller, hSmall, hLarge,
				dir === "right", large, self)) {
					return;
				}
				dir = "h";
			}
			self._setDragAndContentPosition(true, true, dir);
		},

		_disableButtonIfNeeded: function (self) {
			// Disables scrolling buttons.

			var f = self._fields(), o, buttonLeft, buttonRight, buttonTop, buttonBottom,
			hLargeChange, hMax, hValue, hScrollMin, vLargeChange, vMax, vValue,
			vScrollMin;
			if (f.intervalID > 0) {
				window.clearInterval(f.intervalID);
			}
			o = self.options;
			buttonLeft = f.buttonLeft;
			buttonRight = f.buttonRight;
			buttonTop = f.buttonTop;
			buttonBottom = f.buttonBottom;

			if (buttonLeft !== undefined) {
				hLargeChange = self._getHScrollBarLargeChange();

				hMax = o.hScroller.scrollMax - hLargeChange + 1;
				hValue = o.hScroller.scrollValue;
				hScrollMin = o.hScroller.scrollMin;

				if (hValue === undefined) {
					hValue = hScrollMin;
				}
				if (Math.abs(hValue - hScrollMin) < 0.001 || !f.hScrolling) {
					buttonLeft.addClass(uiStateDisabled);
				}
				else {
					buttonLeft.removeClass(uiStateDisabled);
				}
				if (Math.abs(hValue - hMax) < 0.001 || !f.hScrolling) {
					buttonRight.addClass(uiStateDisabled);
				}
				else {
					buttonRight.removeClass(uiStateDisabled);
				}
			}
			if (buttonTop !== undefined) {
				vLargeChange = self._getVScrollBarLargeChange();
				vMax = o.vScroller.scrollMax - vLargeChange + 1;
				vValue = o.vScroller.scrollValue;
				vScrollMin = o.vScroller.scrollMin;
				if (vValue === undefined) {
					vValue = vScrollMin;
				}
				if (Math.abs(vValue - vScrollMin) < 0.001 || !f.vScrolling) {
					buttonTop.addClass(uiStateDisabled);
				}
				else {
					buttonTop.removeClass(uiStateDisabled);
				}
				if (Math.abs(vValue - vMax) < 0.001 || !f.vScrolling) {
					buttonBottom.addClass(uiStateDisabled);
				}
				else {
					buttonBottom.removeClass(uiStateDisabled);
				}
			}
		},

		_clearInterval: function () {
			var f = this._fields(), intervalID = f.internalFuncID;
			if (intervalID > 0) {
				window.clearInterval(intervalID);
				f.internalFuncID = -1;
			}
		},

		_elementMouseOut: function (event) {
			var ele = $(event.currentTarget), self = event.data;

			ele.unbind("mouseout", self._elementMouseOut);
			ele.unbind("mousedown", self._elementMouseDown);
			ele.unbind("mouseup", self._elementMouseUp);

			ele.removeClass(uiStateHover);
		},

		scrollChildIntoView: function (child1) {
			/// <summary>
			/// Scroll children DOM element to view. 
			/// </summary>
			/// <param name="child" type="DOMElement/JQueryObj">
			/// The child to scroll to.
			/// </param>

			var child = $(child1), f, cWrapper, tempWrapper, left, top,
			childOffset, templateOffset, cWrapperOffset;

			if (child.size() === 0) {
				return;
			}
			f = this._fields();
			cWrapper = f.contentWrapper;
			tempWrapper = f.templateWrapper;
			childOffset = child.offset();
			templateOffset = tempWrapper.offset();

			childOffset.leftWidth = childOffset.left + child.outerWidth();
			childOffset.topHeight = childOffset.top + child.outerHeight();
			cWrapperOffset = cWrapper.offset();
			cWrapperOffset.leftWidth = cWrapperOffset.left + cWrapper.outerWidth();
			cWrapperOffset.topHeight = cWrapperOffset.top + cWrapper.outerHeight();

			if (childOffset.left < cWrapperOffset.left) {
				left = childOffset.left - templateOffset.left;
			}
			else if (childOffset.leftWidth > cWrapperOffset.leftWidth) {
				left = childOffset.leftWidth - templateOffset.left -
				cWrapper.innerWidth();
			}
			if (childOffset.top < cWrapperOffset.top) {
				top = childOffset.top - templateOffset.top;
			}
			else if (childOffset.topHeight > cWrapperOffset.topHeight) {
				top = childOffset.topHeight - templateOffset.top -
				cWrapper.innerHeight();
			}
			if (left !== undefined) {
				this.hScrollTo(left);
			}
			if (top !== undefined) {
				this.vScrollTo(top);
			}
		},

		hScrollTo: function (x) {
			/// <summary>
			/// Scroll to horizontal position.
			/// </summary>
			/// <param name="x" type="Number">
			/// The position to scroll to.
			/// </param>
			var o = this.options;
			//var f = this._fields();
			o.hScroller.scrollValue = this.scrollPxToValue(x, "h");
			//this._setDragAndContentPosition(false, true, "h", "nonestop");
			this._setDragAndContentPosition(true, true, "h", "nonestop");
		},

		vScrollTo: function (y) {
			/// <summary>
			/// Scroll to vertical position.
			/// </summary>
			/// <param name="y" type="Number">
			/// The position to scroll to.
			/// </param>

			var o = this.options;
			o.vScroller.scrollValue = this.scrollPxToValue(y, "v");
			//this._setDragAndContentPosition(false, true, "v", "nonestop");
			this._setDragAndContentPosition(true, true, "v", "nonestop");
		},

		scrollPxToValue: function (px, dir) {
			/// <summary>
			/// Convert pixel to scroll value.
			/// For example, wijsuperpanel scrolled 50px 
			///which is value 1 after conversion.
			/// </summary>
			/// <param name="px" type="Number">
			/// Length of scrolling.
			/// </param>
			/// <param name="dir" type="String">
			/// Scrolling direction. Options are: "h" and "v".
			/// </param>

			var o = this.options,
			m = (dir === "h" ? "outerWidth" : "outerHeight"),
			m1 = (dir === "h" ? "contentWidth" : "contentHeight"),
			scroller = (dir === "h" ? "hScroller" : "vScroller"),
			f = this._fields(),
			cWrapper = f.contentWrapper,
			//var tempWrapper = f.templateWrapper;
			size = f[m1],
			contentHeight = cWrapper[m](),

			vMin = o[scroller].scrollMin,
			vMax = o[scroller].scrollMax,
			vRange = vMax - vMin,
			vLargeChange = (dir === "h" ?
			this._getHScrollBarLargeChange() : this._getVScrollBarLargeChange()),
			maxv = vRange - vLargeChange + 1,
			ret = maxv * (px / (size - contentHeight));
			if (ret < vMin) {
				ret = vMin;
			}
			if (ret > maxv) {
				ret = maxv;
			}
			return ret;
		},

		scrollTo: function (x, y) {
			/// <summary>
			/// Refreshes wijsuperpanel. 
			/// Needs to be called after content being changed.
			/// </summary>
			/// <param name="x" type="Number">
			/// Horizontal position to scroll to.	
			/// </param>
			/// <param name="y" type="Number">
			/// Vertical position to scroll to.
			/// </param>

			this.hScrollTo(x);
			this.vScrollTo(y);
		},

		paintPanel: function () {
			/// <summary>
			/// Refreshes wijsuperpanel. 
			/// Needs to be called after content being changed.
			/// </summary>
			/// <returns type="Boolean">
			/// Returns true if painting is successful, else returns false. 
			/// </returns>
			var self = this, ele = self.element, focused, o, f, templateWrapper;
			if (ele.is(":visible")) {
				focused = document.activeElement;
				o = self.options;
				f = self._fields();
				if (!f.initialized) {
					self._initialize(f, ele, self);
				}
				self._resetLargeChange(self, f, o);
				self._bindElementEvents(self, f, ele, o);
				templateWrapper = f.templateWrapper;
				templateWrapper.css({ "float": "left", left: "0px", top: "0px",
					width: "auto", height: "auto"
				});
				// hide and show wrapper div to force the width to change
				// for some browser.
				templateWrapper.hide();
				templateWrapper.show();
				f.contentWidth = templateWrapper.width();
				f.contentHeight = templateWrapper.height();
				templateWrapper.css("float", "");
				self._setRounder(self, ele);
				self._setInnerElementsSize(f, ele);
				if (self._testScroll(self, f, o) === false) {
					return false;
				}
				self._initScrollBars(self, f, o);
				self._initScrollButtons(self, f, o);
				self._trigger("painted");

				self._paintedMark = { date: new Date(), mainWidth: ele[0].offsetWidth,
					mainHeight: ele[0].offsetHeight, width: f.contentWidth,
					height: f.contentWidth
				};
				if (focused !== undefined) {
					$(focused).focus();
				}
				return true;
			}
			return false;
		},

		_resetLargeChange: function (self, f, o) {
			if (self._autoVLarge) {
				o.vScroller.scrollLargeChange = null;
			}
			if (self._autoHLarge) {
				o.hScroller.scrollLargeChange = null;
			}
			f.vTrackLen = undefined;
			f.hTrackLen = undefined;
			if (f.vbarContainer) {
				f.vbarContainer.remove();
				f.vbarContainer = undefined;
			}
			if (f.hbarContainer) {
				f.hbarContainer.remove();
				f.hbarContainer = undefined;
			}
		},

		_initialize: function (f, ele, self) {
			f.initialized = true;
			// ensure width and height
			ele.addClass(uiSuperPanelClasses);
			f.oldHeight = ele.css("height");
			var old = ele.css("overflow");
			ele.css("overflow", "");
			// set height to element
			ele.height(ele.height());
			ele.css("overflow", old);

			self._createAdditionalDom(self, f, ele);
		},

		getContentElement: function () {
			/// <summary>
			/// Gets the content element of wijsuperpanel.
			/// </summary>
			/// <returns type="JQueryObj" />

			return this._fields().templateWrapper;
		},

		_setButtonPosition: function (self, o, scroller, dir, target, f, state) {
			var h = dir === "h", mouseoverkey = "mouseover." + self.widgetName,
			decKey = h ? "buttonLeft" : "buttonTop",
			incKey = h ? "buttonRight" : "buttonBottom",
			decButton = f[decKey],
			incButton = f[incKey], html, buttons, defaultPosition;
			if (self._hasMode(scroller, "buttons") ||
			self._hasMode(scroller, "buttonshover")) {

				html = h ? hButtons : vButtons;
				if (decButton === undefined) {
					buttons = $(html).appendTo(state);
					buttons.bind(mouseoverkey, self, self._scrollButtonMouseOver);
					f[decKey] = decButton = state.children(h ?
					".wijmo-wijsuperpanel-buttonleft" : ".wijmo-wijsuperpanel-buttontop");
					f[incKey] = incButton = state.children(h ?
					".wijmo-wijsuperpanel-buttonright" :
					".wijmo-wijsuperpanel-buttonbottom");
				}
				defaultPosition = {
					my: h ? "left" : "top",
					of: target,
					at: h ? "left" : "top",
					collision: "none"
				};
				$.extend(defaultPosition, scroller.decreaseButtonPosition);
				decButton.position(defaultPosition);
				defaultPosition = {
					my: h ? "right" : "bottom",
					of: target,
					at: h ? "right" : "bottom",
					collision: "none"
				};
				$.extend(defaultPosition, scroller.increaseButtonPosition);
				incButton.position(defaultPosition);
			}
			else if (decButton !== undefined) {
				decButton.remove();
				incButton.remove();
				f[decKey] = f[incKey] = undefined;
			}
		},

		_initScrollButtons: function (self, f, o) {
			var a = f.contentWrapper,
			state = f.stateContainer;
			self._setButtonPosition(self, o, o.hScroller, "h", a, f, state);
			self._setButtonPosition(self, o, o.vScroller, "v", a, f, state);
		},

		_getVScrollBarSmallChange: function () {
			var o = this.options, va;
			if (!o.vScroller.scrollSmallChange) {
				va = this._getVScrollBarLargeChange();
				o.vScroller.scrollSmallChange = va / 2;
			}
			return o.vScroller.scrollSmallChange;
		},

		_getVScrollBarLargeChange: function () {
			return this._getLargeChange("v");
		},

		_getLargeChange: function (dir) {
			var self = this,
			o = self.options,
			f = self._fields(),
			v = dir === "v",
			scroller = v ? o.vScroller : o.hScroller,
			//clientKey = v ? "clientHeight" : "clientWidth",
			clientKey = v ? "innerHeight" : "innerWidth",
			offsetKey = v ? "contentHeight" : "contentWidth",
			autoKey = v ? "_autoVLarge" : "_autoHLarge",
			hMax, hMin, hRange, content, contentWidth, wrapperWidth, percent, large;

			if (scroller.scrollLargeChange) {
				return scroller.scrollLargeChange;
			}

			// calculate large change if empty
			hMax = scroller.scrollMax;
			hMin = scroller.scrollMin;
			hRange = hMax - hMin;

			content = f.contentWrapper;
			//contentWidth = content[0][clientKey];
			contentWidth = content[clientKey]();
			wrapperWidth = f[offsetKey];

			percent = contentWidth / (wrapperWidth - contentWidth);
			large = ((hRange + 1) * percent) / (1 + percent);
			if (isNaN(large)) {
				large = 0;
			}
			scroller.scrollLargeChange = large;

			self[autoKey] = true;
			return scroller.scrollLargeChange;
		},

		_getHScrollBarSmallChange: function () {
			var o = this.options, va;
			if (!o.hScroller.scrollSmallChange) {
				va = this._getHScrollBarLargeChange();
				o.hScroller.scrollSmallChange = va / 2;
			}
			return o.hScroller.scrollSmallChange;
		},

		_getHScrollBarLargeChange: function () {
			return this._getLargeChange("h");
		},

		_initScrollBars: function (self, f, o) {
			// Set scroll bar initial position.
			var hScroller = o.hScroller,
			hMax = hScroller.scrollMax,
			hMin = hScroller.scrollMin,
			hRange = hMax - hMin,

			vScroller = o.vScroller,
			vMax = vScroller.scrollMax,
			vMin = vScroller.scrollMin,
			vRange = vMax - vMin,

			hbarDrag = f.hbarDrag,
			vbarDrag = f.vbarDrag,
			hLargeChange, track, dragLen, difference, icon, vLargeChange,
			track1, dragLen1, difference1, icon1;
			if (self.hNeedScrollBar && hbarDrag.is(":visible")) {
				hLargeChange = self._getHScrollBarLargeChange();
				track = self._getTrackLen("h");
				dragLen = self._getDragLength(hRange, hLargeChange,
				track, o.hScroller.scrollMinDragLength);
				hbarDrag.width(dragLen);
				//difference = hbarDrag.outerWidth() - hbarDrag.width();
				difference = hbarDrag.outerWidth(true) - hbarDrag.width();
				hbarDrag.width(dragLen - difference);
				icon = hbarDrag.children("span");
				icon.css("margin-left", (hbarDrag.width() - icon[0].offsetWidth) / 2);
				//if (track <= hbarDrag.outerWidth()) {
				if (track <= hbarDrag.outerWidth(true)) {
					hbarDrag.hide();
				}
				else {
					hbarDrag.show();
				}
			}
			if (self.vNeedScrollBar && vbarDrag.is(":visible")) {
				vLargeChange = self._getVScrollBarLargeChange();
				track1 = self._getTrackLen("v");
				dragLen1 = self._getDragLength(vRange, vLargeChange, track1,
				o.vScroller.scrollMinDragLength);
				vbarDrag.height(dragLen1);
				//difference1 = vbarDrag.outerHeight() - vbarDrag.height();
				difference1 = vbarDrag.outerHeight(true) - vbarDrag.height();
				vbarDrag.height(dragLen1 - difference1);
				icon1 = vbarDrag.children("span");
				icon1.css("margin-top", (vbarDrag.height() - icon1[0].offsetHeight) / 2);
				//if (track1 <= vbarDrag.outerHeight()) {
				if (track1 <= vbarDrag.outerHeight(true)) {
					vbarDrag.hide();
				}
				else {
					vbarDrag.show();
				}
			}
			self._setDragAndContentPosition(false, false, "both");
		},

		_getTrackLen: function (dir) {
			// Get the length of the track.
			// <param name="dir" type="String">
			// Options are: "v" and "h".
			// "v" - Vertical scroll track.
			// "h" - Horizontal scroll track.
			// </param>

			var self = this,
			f = self._fields(),
			//var o = self.options;
			key = dir + "TrackLen",
			hbarContainer, vbarContainer, track, padding;
			if (f[key] !== undefined) {
				return f[key];
			}

			hbarContainer = f.hbarContainer;
			vbarContainer = f.vbarContainer;
			track = 0;
			padding = 0;
			if (dir === "h") {
				padding = self._getScrollContainerPadding("h");
				track = hbarContainer.innerWidth();
			}
			if (dir === "v") {
				padding = self._getScrollContainerPadding("v");
				track = vbarContainer.innerHeight();
			}
			f[key] = (track - padding);
			return f[key];
		},

		_getScrollContainerPadding: function (paddingType) {
			// Get the padding of the scroll bar container.
			var self = this,
			f = self._fields(),
			padding = 0, container, key;
			if (paddingType === "h") {
				padding = self._getScrollContainerPadding("left") +
				self._getScrollContainerPadding("right");
			}
			else if (paddingType === "v") {
				padding = self._getScrollContainerPadding("top") +
				self._getScrollContainerPadding("bottom");
			}
			else {
				if (paddingType === "left" || paddingType === "right") {
					container = f.hbarContainer;
				}
				else {
					container = f.vbarContainer;
				}
				key = paddingType + "Padding";
				if (f[key] !== undefined) {
					padding = f[key];
					return padding;
				}
				padding = parseFloat(container.css("padding-" +
				paddingType).replace("px", ""));
				f[key] = padding;
			}
			return padding;
		},

		_contentDragAnimate: function (dir, animated, hbarContainer, hbarDrag,
		stop, fireScrollEvent, dragging) {
			var self = this,
			o = self.options,
			v = dir === "v",
			scroller = v ? o.vScroller : o.hScroller,
			tempKey = v ? "outerHeight" : "outerWidth",
			wrapKey = v ? "innerHeight" : "innerWidth",
			contentKey = v ? "contentHeight" : "contentWidth",
			paddingKey = v ? "top" : "left",
			hMin = scroller.scrollMin,
			hMax = scroller.scrollMax,
			hRange = hMax - hMin,
			hValue = scroller.scrollValue === undefined ?
			hMin : (scroller.scrollValue - hMin),
			hLargeChange = self._getLargeChange(dir),
			max = hRange - hLargeChange + 1,
			f = self._fields(),
			cWrapper = f.contentWrapper,
			tempWrapper = f.templateWrapper,
			contentLeft, dragleft, track, drag, r, padding, dragAnimationOptions,
			properties, contentAnimationOptions, userComplete, properties1, key;

			if (hValue > max) {
				hValue = max;
			}
			contentLeft = (f[contentKey] - cWrapper[wrapKey]()) * (hValue / max);
			if (Math.abs(contentLeft) < 0.001) {
				contentLeft = 0;
			}
			contentLeft = Math.round(contentLeft);
			dragleft = -1;
			if (hbarContainer !== undefined) {
				if (animated && hbarDrag.is(":animated") && stop !== "nonestop") {
					hbarDrag.stop(true, false);
				}
				track = self._getTrackLen(dir);
				//drag = hbarDrag[tempKey]();
				drag = hbarDrag[tempKey](true);
				r = track - drag;
				padding = self._getScrollContainerPadding(paddingKey);
				dragleft = (hValue / max) * r + padding;
			}
			if (animated && o.animationOptions && !o.animationOptions.disabled) {
				if (dragleft >= 0 && dragging !== "dragging") {
					dragAnimationOptions = $.extend({}, o.animationOptions);
					// not trigger scrolled when stop
					dragAnimationOptions.complete = undefined;
					properties = v ? { top: dragleft} : { left: dragleft };
					hbarDrag.animate(properties, dragAnimationOptions);
				}
				contentAnimationOptions = $.extend({}, o.animationOptions);
				userComplete = o.animationOptions.complete;
				contentAnimationOptions.complete = function () {
					self._scrollEnd(fireScrollEvent, self, dir);
					if ($.isFunction(userComplete)) {
						userComplete(arguments);
					}

				};
				if (animated && tempWrapper.is(":animated") && stop !== "nonestop") {
					tempWrapper.stop(true, false);
				}
				properties1 = v ? { top: -contentLeft} : { left: -contentLeft };
				tempWrapper.animate(properties1, contentAnimationOptions);
			}
			else {
				key = v ? "top" : "left";
				if (dragleft >= 0 && dragging !== "dragging") {

					hbarDrag[0].style[key] = dragleft + "px";
				}
				tempWrapper[0].style[key] = -contentLeft + "px";
				self._scrollEnd(fireScrollEvent, self, dir);
			}
		},

		_setDragAndContentPosition: function (fireScrollEvent, animated, dir,
		stop, dragging) {
			var self = this,
			f = self._fields(),
			hbarContainer = f.hbarContainer,
			hbarDrag = f.hbarDrag,
			vbarContainer = f.vbarContainer,
			vbarDrag = f.vbarDrag;
			if ((dir === "both" || dir === "h") && f.hScrolling) {
				self._contentDragAnimate("h", animated, hbarContainer, hbarDrag,
				stop, fireScrollEvent, dragging);
			}
			if ((dir === "both" || dir === "v") && f.vScrolling) {
				self._contentDragAnimate("v", animated, vbarContainer, vbarDrag,
				stop, fireScrollEvent, dragging);
			}
			if (f.intervalID > 0) {
				window.clearInterval(f.intervalID);
			}
			f.intervalID = window.setInterval(function () {
				self._disableButtonIfNeeded(self);
			}, 500);
		},

		_scrolling: function (fireEvent, self, d) {
			var r = true;

			if (fireEvent) {
				d.beforePosition = self.getContentElement().position();
				self._beforePosition = d.beforePosition;
				r = self._trigger("scrolling", null, d);
			}
			return r;
		},

		_scrollEnd: function (fireEvent, self, dir) {
			if (fireEvent) {
				// use settimeout to return to caller immediately.
				window.setTimeout(function () {
					var content = self.getContentElement(), after, d;
					if (!content.is(":visible")) {
						return;
					}
					after = self.getContentElement().position();
					d = {
						dir: dir,
						beforePosition: self._beforePosition,
						afterPosition: after
					};
					self._trigger("scrolled", null, d);
				}, 0);
			}
		},

		_getDragLength: function (range, largeChange, track, min) {
			var divide = range / largeChange,
			dragLength = track / divide,
			minidrag = min;
			if (dragLength < minidrag) {
				dragLength = minidrag;
			}
			else if ((dragLength + 1) >= track) {
				dragLength = track - 1;
			}
			return Math.round(dragLength);
		},

		_needScrollbar: function (scroller, needscroll) {
			var scrollbarMode = this._hasMode(scroller, "scrollbar"),
			barVisibility = scroller.scrollBarVisibility,
			needScrollBar = scrollbarMode && (barVisibility === "visible" ||
			(barVisibility === "auto" && needscroll));
			return needScrollBar;
		},

		_bindBarEvent: function (barContainer, barDrag, dir) {
			var self = this;
			barContainer.bind("mouseover." + self.widgetName, self,
			self._scrollerMouseOver);
			barDrag.draggable({
				axis: dir === "h" ? "x" : "y",
				drag: function (e, data) {
					self._dragging(e, data, self);
				},
				containment: "parent",
				stop: function (e) {
					self._dragStop(e, self, dir);
					$(e.target).removeClass("ui-state-active");
				}
			});
		},

		_createBarIfNeeded: function (hNeedScrollBar, scrollerWrapper,
		dir, html, content) {
			if (hNeedScrollBar) {
				var self = this, o = self.options, data,
				f = self._fields(),
				strBarContainer = dir + "barContainer",
				strBarDrag = dir + "barDrag",
				hbar = dir === "h",
				//contentLength = content[0][hbar ? "clientHeight" : "clientWidth"],
				contentLength = content[hbar ? "innerHeight" : "innerWidth"](),
				c = f[strBarContainer] = $(html), targetBarLen, d;

				scrollerWrapper.append(c);
				targetBarLen = c[0][hbar ? "offsetHeight" : "offsetWidth"];
				contentLength = contentLength - targetBarLen;

				data = {
					direction: hbar ? "horizontal" : "vertical",
					targetBarLen: targetBarLen,
					contentLength: contentLength
				};

				if (self._trigger(hbar ? "hScrollerActivating" : "vScrollerActivating",
				null, data) === false) {
					return false;
				}

				d = f[strBarDrag] = c.find("." + scrollerHandle);
				self._bindBarEvent(c, d, dir);

				content[hbar ? "height" : "width"](contentLength);
			}
		},

		_setScrollbarPosition: function (wrapper, self, content,
					targetBarContainer, referBarContainer,
					targetNeedScrollBar, referNeedScrollBar,
					targetScrollBarPosition, referScrollBarPosition, dir, scrollingNeed) {
			var hbar = dir === "h", targetBarLen, targetPadding, targetBarPosition,
			barPosition1, contentPosition1, barPosition2, contentPosition2,
			contentLength2, referBarWidth;
			if (targetNeedScrollBar) {
				targetBarLen = targetBarContainer[0][hbar ?
				"offsetHeight" : "offsetWidth"];
				targetPadding = self._getScrollContainerPadding(dir);
				targetBarPosition = hbar ? "top" : "left";
				barPosition1 = hbar ? { top: "0px", bottom: "auto", left: "auto",
					right: "auto"
				} : { left: "0px", right: "auto", top: "auto",
					bottom: "auto"
				};
				contentPosition1 = hbar ? { top: targetBarLen + "px"} :
				{ left: targetBarLen + "px" };

				barPosition2 = hbar ? { top: "auto", right: "auto", left: "auto",
					bottom: "0px"
				} : { left: "auto", right: "0px", top: "auto",
					bottom: "auto"
				};
				contentPosition2 = hbar ? { top: ""} : { left: "" };
				//var contentLength = content[0][hbar? "clientHeight":"clientWidth"];
				//contentLength2 = content[0][hbar? "clientWidth":"clientHeight"];
				contentLength2 = content[hbar ? "innerWidth" : "innerHeight"]();
				if (targetScrollBarPosition === targetBarPosition) {
					targetBarContainer.css(barPosition1);
					content.css(contentPosition1);
					if (hbar) {
						targetBarContainer
						.children(".wijmo-wijsuperpanel-hbar-buttonleft")
						.removeClass("ui-corner-bl").addClass("ui-corner-tl");
						targetBarContainer
						.children(".wijmo-wijsuperpanel-hbar-buttonright")
						.removeClass("ui-corner-br").addClass("ui-corner-tr");
						targetBarContainer.removeClass("ui-corner-bottom")
						.addClass("ui-corner-top");
					}
					else {
						targetBarContainer
						.children(".wijmo-wijsuperpanel-vbar-buttontop")
						.removeClass("ui-corner-tr").addClass("ui-corner-tl");
						targetBarContainer
						.children(".wijmo-wijsuperpanel-vbar-buttonbottom")
						.removeClass("ui-corner-br").addClass("ui-corner-bl");
						targetBarContainer.removeClass("ui-corner-right")
						.addClass("ui-corner-left");
					}
				}
				else {
					targetBarContainer.css(barPosition2);
					content.css(contentPosition2);
					if (hbar) {
						targetBarContainer
						.children(".wijmo-wijsuperpanel-hbar-buttonleft")
						.removeClass("ui-corner-tl").addClass("ui-corner-bl");
						targetBarContainer
						.children(".wijmo-wijsuperpanel-hbar-buttonright")
						.removeClass("ui-corner-bl").addClass("ui-corner-br");
						targetBarContainer.removeClass("ui-corner-top")
						.addClass("ui-corner-bottom");
					}
					else {
						targetBarContainer
						.children(".wijmo-wijsuperpanel-vbar-buttontop")
						.removeClass("ui-corner-tl").addClass("ui-corner-tr");
						targetBarContainer
						.children(".wijmo-wijsuperpanel-vbar-buttonbottom")
						.removeClass("ui-corner-bl").addClass("ui-corner-br");
						targetBarContainer.removeClass("ui-corner-left")
						.addClass("ui-corner-right");
					}
				}
				//content[hbar?"height":"width"](contentLength - targetBarLen);
				referBarWidth = 0;
				if (referNeedScrollBar) {
					referBarWidth = referBarContainer[0][hbar ?
					"offsetWidth" : "offsetHeight"];
					if (referScrollBarPosition === "left") {
						targetBarContainer.css("right", "0px");
					}
					else if (referScrollBarPosition === "right") {
						targetBarContainer.css("left", "0px");
					}
					else if (referScrollBarPosition === "top") {
						targetBarContainer.css("bottom", "0px");
					}
					else if (referScrollBarPosition === "bottom") {
						targetBarContainer.css("top", "0px");
					}
				}
				if (!hbar/*vbar*/ && referNeedScrollBar) {
					referBarWidth = 0;
				}

				targetBarContainer[hbar ? "width" : "height"](contentLength2 -
				targetPadding);
				self._enableDisableScrollBar(dir, targetBarContainer, !scrollingNeed);
			}
			else {
				wrapper.css(hbar ? "left" : "top", "");
			}
		},

		_testScroll: function (self, f, o) {
			var wrapper = f.templateWrapper,
			content = f.contentWrapper,
			scrollerWrapper = f.stateContainer,
			contentWidth = content.innerWidth(),
			contentHeight = content.innerHeight(),
			wrapperWidth = f.contentWidth,
			wrapperHeight = f.contentHeight,
			hNeedScrollBar, vNeedScrollBar, hbarContainer, vbarContainer,
			hbarPosition, vbarPosition;
			f.hScrolling = wrapperWidth > contentWidth;
			f.vScrolling = wrapperHeight > contentHeight;

			hNeedScrollBar = self.hNeedScrollBar =
			self._needScrollbar(o.hScroller, f.hScrolling);
			if (self._createBarIfNeeded(hNeedScrollBar, scrollerWrapper,
			"h", hbarHtml, content) === false) {
				return false;
			}
			// having h scroll bar, but no vscroll bar, we need to test vscrolling again.
			if (hNeedScrollBar && !f.vScrolling) {
				wrapper.css("float", "left");
				f.contentHeight = wrapper.height();
				f.vScrolling = f.contentHeight > (contentHeight -
				f.hbarContainer[0].offsetHeight);

				wrapper.css("float", "");
			}

			vNeedScrollBar = self.vNeedScrollBar =
			self._needScrollbar(o.vScroller, f.vScrolling);
			if (self._createBarIfNeeded(vNeedScrollBar, scrollerWrapper, "v",
			vbarHtml, content) === false) {
				return false;
			}

			if (vNeedScrollBar && !f.hScrolling) {
				wrapper.css("float", "left");
				f.contentWidth = wrapper.width();
				f.hScrolling = f.contentWidth > (contentWidth -
				f.vbarContainer[0].offsetWidth);
				wrapper.css("float", "");
				if (f.hScrolling && !hNeedScrollBar) {
					hNeedScrollBar = self.hNeedScrollBar =
					self._needScrollbar(o.hScroller, f.hScrolling);
					if (self._createBarIfNeeded(hNeedScrollBar, scrollerWrapper, "h",
					 hbarHtml, content) === false) {
						return false;
					}
				}
			}

			hbarContainer = f.hbarContainer;
			vbarContainer = f.vbarContainer;
			hbarPosition = o.hScroller.scrollBarPosition;
			vbarPosition = o.vScroller.scrollBarPosition;

			self._setScrollbarPosition(wrapper, self, content, hbarContainer,
			vbarContainer, hNeedScrollBar, vNeedScrollBar, hbarPosition,
			vbarPosition, "h", f.hScrolling);
			self._setScrollbarPosition(wrapper, self, content, vbarContainer,
			hbarContainer, vNeedScrollBar, hNeedScrollBar, vbarPosition,
			hbarPosition, "v", f.vScrolling);
		},

		_enableDisableScrollBar: function (bar, barContainer, disable) {
			// Disables scroll bar.
			// <param name="bar" type="String">
			// Scrollbar to disable. 
			// Options are: "h" and "v"
			// </param>
			// <param name="barContainer" type="jQuery">
			// The scroll bar container jQuery object.
			// </param>
			// <param name="disable" type="Boolean">
			// Whether to disable scroll bar.
			// </param>

			if (bar === "v") {
				barContainer[disable ? "addClass" :
				"removeClass"]("wijmo-wijsuperpanel-vbarcontainer-disabled");
				barContainer.find("." + uiStateDefault)[disable ? "addClass" :
				"removeClass"](uiStateDisabled);
			}
			else if (bar === "h") {
				barContainer[disable ? "addClass" :
				"removeClass"]("wijmo-wijsuperpanel-hbarcontainer-disabled");
				barContainer.find("." + uiStateDefault)[disable ? "addClass" :
				"removeClass"](uiStateDisabled);
			}
			barContainer.children("." + scrollerHandle)[disable ? "hide" : "show"]();
		},

		_initResizer: function () {
			// Initialize reseizer of wijsuperpanel.

			var self = this, o = self.options,
			f = self._fields(),
			resizer = f.resizer,
			resizableOptions, oldstop;

			if (!resizer && o.allowResize) {
				resizableOptions = o.resizableOptions;
				oldstop = resizableOptions.stop;
				resizableOptions.stop = function (e) {
					self._resizeStop(e, self);
					if ($.isFunction(oldstop)) {
						oldstop(e);
					}
				};
				f.resizer = resizer = self.element.resizable(resizableOptions);
			}
			if (!o.allowResize && f.resizer) {
				resizer.resizable("destroy");
				f.resizer = null;
			}
		},

		_resizeStop: function (e, self) {
			// give the chance to autoRefresh polling to repaint.
			if (!this.options.autoRefresh) {
				self.paintPanel();
			}
			self._trigger("resized");
		},

		_createAdditionalDom: function (self, f, ele) {

			// make sure the key pressing event work in FireFox.
			if (!ele.attr("tabindex")) {
				ele.attr("tabindex", "-1");
				f.tabindex = true;
			}
			var stateContainer = f.stateContainer = $(innerElementHtml),
			templateW;
			// move child element to content wrapper div of wijsuperpanel.
			f.contentWrapper = stateContainer.children();
			templateW = f.templateWrapper = f.contentWrapper.children();
			ele.contents().each(function (index, el) {
				var jel = $(el);
				if (jel.hasClass("wijmo-wijsuperpanel-header")) {
					f.header = jel;
					return;
				}
				if (jel.hasClass("wijmo-wijsuperpanel-footer")) {
					f.footer = jel;
					return;
				}
				templateW[0].appendChild(el);
			});

			// apeend header to first element.
			if (f.header !== undefined) {
				ele.prepend(f.header);
			}
			ele[0].appendChild(stateContainer[0]);
			// apeend footer to first element.
			if (f.footer !== undefined) {
				f.footer.insertAfter(stateContainer);
			}
		},

		_setRounder: function (self, ele) {
			if (this.options.showRounder) {
				ele.addClass(rounderClass);
				if (self._rounderAdded) {
					return;
				}
				if ($.browser.msie) {
					return;
				}
				var key1, key, value, border;
				key1 = key = "";

				if ($.browser.webkit) {
					key = "WebkitBorderTopLeftRadius";
					key1 = "WebkitBorderRadius";
				}
				else if ($.browser.mozilla) {
					key = "MozBorderRadiusBottomleft";
					key1 = "MozBorderRadius";
				}
				else {
					key = "border-top-left-radius";
					key1 = "border-radius";
				}
				value = ele.css(key);
				border = parseInt(value, 10);
				// adding 1 extra to out-most radius.

				ele.css(key1, border + 1);
				self._rounderAdded = true;
				self._radiusKey = key1;
			}
			else {
				ele.removeClass(rounderClass);
			}
		},

		_setInnerElementsSize: function (f, ele) {
			var state = f.stateContainer,
			content = f.contentWrapper,
			height = 0, style, clientHeight, clientWidth, style2;
			if (f.header !== undefined) {
				height += f.header.outerHeight();
			}
			if (f.footer !== undefined) {
				height += f.footer.outerHeight();
			}

			style = state[0].style;
			//clientHeight = ele[0].clientHeight - height;
			//clientWidth = ele[0].clientWidth;
			clientHeight = ele.innerHeight() - height;
			clientWidth = ele.innerWidth();
			// hide element before setting width and height to improve 
			//javascript performance in FF3.
			style.display = "none";
			style.height = clientHeight + "px";
			style.width = clientWidth + "px";
			style2 = content[0].style;
			style2.height = clientHeight + "px";
			style2.width = clientWidth + "px";
			style.display = "";
		}
	});
} (jQuery));

/*globals window,document,jQuery,clearTimeout,setTimeout*/

/*
*
* Wijmo Library 1.3.0
* http://wijmo.com/
*
* Copyright(c) ComponentOne, LLC.  All rights reserved.
* 
* Dual licensed under the MIT or GPL Version 2 licenses.
* licensing@wijmo.com
* http://www.wijmo.com/license
*
*
* Wijmo Menu widget.
*
* Depends:
*	jquery.ui.core.js
*	jquery.ui.widget.js
*	jquery.wijmo.wijutil.js
*	jquery.ui.position.js
*	jquery.ui.effects.core.js
*	jquery.wijmo.wijsuperpanel.js
*
*/
(function ($) {
    "use strict";
    var menuitemCss = "wijmo-wijmenu-item";
    $.widget("wijmo.wijmenu", {
        options: {
            /// <summary>
            /// An jQuery selector which handle to open the menu or submenu.
            /// Default: "".
            /// Type: String.
            /// Remark: If set to the menu item(the li element) then when it is clicked
            /// (if the triggerEvent set to 'click') show submenu.  If set to a element 
            /// out of the menu, click(if the triggerEvent set to 'click') it, open the 
            /// menu. 
            /// Code example: $(".selector").wijmenu("option", "trigger", "")
            /// </summary>
            trigger: '',
            /// <summary>
            /// Specifies the event to show the menu.
            /// Default: "click".
            /// Type: String.
            /// Remark: The value can be seted to 'click', 'mouseenter', 'dbclick', 
            /// 'rtclick'
            /// Code example: $(".selector").wijmenu("option", "triggerEvent", "click")
            /// </summary>
            triggerEvent: 'click',
            /// <summary>
            /// Location and Orientation of the menu,relative to the button/link userd
            /// to open it. Configuration for the Position Utility,Of option
            /// excluded(always configured by widget).  Collision also controls collision 
            /// detection automatically too.
            /// Default: {}.
            /// Type: Object.
            /// Code example: $(".selector").wijmenu("option", "position", {})
            /// </summary>
            position: {},
            /// <summary>
            /// Sets showAnimation and hideAnimation if not specified individually.
            /// Default: { animated: "slide", duration: 400, easing: null }.
            /// Type: Object.
            /// Remark: User's standard animation setting syntax from other widgets.
            /// Code example: $(".selector").wijmenu("option", "animation", {})
            /// </summary>
            animation: { animated: "slide", duration: 400, easing: null },
            /// <summary>
            /// Determine the animation used to show submenu.
            /// Default: {}.
            /// Type: Object.
            /// Code example: $(".selector").wijmenu("option", "showAnimation", {})
            /// </summary>
            showAnimation: {},
            /// <summary>
            /// Determine the animation used to hide submenu.
            /// Default: { animated: "fade", duration: 400, easing: null }.
            /// Type: Object.
            /// Code example: $(".selector").wijmenu("option", "hideAnimation", {})
            /// </summary>
            hideAnimation: { animated: "fade", duration: 400, easing: null },
            /// <summary>
            /// When the menu is flyout menu, determines how many milliseconds delay 
            /// to show submenu.
            /// Default: 400
            /// Type: Number
            /// Code example: $(".selector").wijmenu("option", "showDelay", 400);
            /// </summary>
            showDelay: 400,
            /// <summary>
            /// When the menu is flyout menu, determines how many milliseconds delay
            /// to hide submenu.
            /// Default: 400
            /// Type: Number
            /// Code exapmle: $(".selector").wijmenu("option", "hideDelay", 400).
            /// </summary>
            hideDelay: 400,
            /// <summary>
            /// Determine the animation used to slide submenu in sliding mode.
            /// Default: { duration: 400, easing: null }.
            /// Type: Object.
            /// Code example: $(".selector").wijmenu("option", "slidingAnimation", {})
            /// </summary>
            slidingAnimation: { duration: 400, easing: null },
            /// <summary>
            /// Defines the behavior of the submenu whether it is a popup menu or 
            /// an iPod-style navigation list.
            /// Default:"flyout".
            /// Type:String.
            /// Remarks: Possible values are "flyout" or "sliding".
            /// Code example: $(".selector").wijmenu("option", "mode", "sliding")
            /// </summary>
            mode: 'flyout',
            /// <summary>
            /// This option specifies a hash value that sets to superpanel options 
            /// when a superpanel is created.
            /// Default: null.
            /// Type: Object.
            /// Code example: $(".selector").wijmenu("option", "superPanelOptions", {})
            /// </summary>
            superPanelOptions: null,
            /// <summary>
            /// Defines whether the item can be checked.
            /// Default: false.
            /// Type: Boolean.
            /// Code example: $(".selector").wijmenu("option","chackable", true).
            /// </summary>
            checkable: false,
            /// <summary>
            /// Controls the root menus orientation. All submenus are vertical 
            /// regardless of the orientation of the root menu.
            /// Default: "horizontal".
            /// Type: String.
            /// Remark: The value should be "horizontal" or "vertical".
            /// Code example: $(".selector").wijmenu("option", "orientation", "vertical")
            /// </summary>
            orientation: 'horizontal',
            /// <summary>
            /// Determines the i-Pod-style menu's maximum height.
            /// Default: 200.
            /// Type: Number.
            /// Remark: This option only used in i-pod style menu.  When the menu's  
            /// heiget largger than this value, menu show scroll bar.
            /// Code example: $(".selector").wijmenu("option", "maxHeight", 200)
            /// </summary>
            maxHeight: 200,
            /// <summary>
            /// Determines whether the i-Pod menu shows a back link or a breadcrumb header
            /// in the menu.
            /// Default: true.
            /// Type: Boolean.
            /// Code example: $(".selector").wijmenu("option", "backLink", true)
            /// </summary>
            backLink: true,
            /// <summary>
            /// Sets the text of the back link.
            /// Default: "Back".
            /// Type: String.
            /// Code example: $(".selector").wijmenu("option", "backLinkText", "Back")
            /// </summary>
            backLinkText: 'Back',
            /// <summary>
            /// Sets the text of the top link.
            /// Default: "All".
            /// Type: String.
            /// Code example: $(".selector").wijmenu("option", "topLinkText", "All")
            /// </summary>
            topLinkText: 'All',
            /// <summary>
            /// Sets the top breadcrumb's default Text.
            /// Default: "Choose an option".
            /// Type: String.
            /// Code example: $(".selector").wijmenu("option", "crumbDefaultText", 
            /// "Choose")
            /// </summary>
            crumbDefaultText: 'Choose an option',
            /// <summary>
            /// Triggered when a menu item is selected.
            /// Default: null
            /// Type: Function
            /// code example:
            /// Supply a function as an option.
            /// $("#selector").wijmenu("select", function(e, data){})
            /// Bind to the event by type: wijmenuselect
            /// $(".selector").bind("wijmenuselect", function(e, data) { } );
            /// </summary>
            /// <param name="e" type="EventObj">jQuery.Event object.</param>
            /// <param name="data" type="Object">data.item is the avtive 
            /// item of the menu.</param>
            select: null,
            /// <summary>
            /// Triggered when a menu item gets the foucs, either when the mouse is
            /// used to hover over it (on hover) or when the cursor keys are used
            /// on the keyboard(navigation width cursor key) focus.
            /// Default: null.
            /// Type: Function
            /// Code example:
            /// Supply a function as an option.
            /// $("#selector").wijmenu("focus", function(e, data) {})
            /// Bind to the event by type: wijmenufocus
            /// $(".selector").bind("wijmenufocus", function(e, data) { } );
            /// </summary>
            /// <param name="e" type="Object">jQuery.Event object</param>
            /// <param name="data" type="Object">data.item is the item 
            /// which is focused.</param>
            focus: null,
            /// <summary>
            /// Triggered when a menu item loses focus.
            /// Default: null.
            /// Type: Function.
            /// Code example:
            /// Supply a function as an option.
            /// $("#selector").wijmenu("blur", function(e, data){})
            /// Bind to the event by type: wijmenublur
            /// $(".selector").bind("wijmenublur", function(e, data) { } );
            /// </summary>
            /// <param name="e" type="Object">jQuery.Event object.</param>
            /// <param name="data" type="Object">data.item is the a menu item
            /// which loses focus.</param>
            blur: null,
            /// <summary>
            /// Triggered before showing the submenu.
            /// Default: null.
            /// Type: Function.
            /// Code example:
            /// Supply a function as an option.
            /// $("#selector").wijmenu("showing", function(e, sublist){})
            /// Bind to the event by type: wijmenushowing
            /// $(".selector").bind("wijmenushowing", function(e, data) { } );
            /// </summary>
            /// <param name="e" type="Object">the event object relates to the 
            /// submenu's parent item.</param>
            /// <param name="sublist" type="Element">the submenu element.</param>
            showing: null
        },

        _preventEvent: function (event) {
            event.preventDefault();
            event.stopImmediatePropagation();
        },

        _create: function () {
            // Before crete menu items,hide the menu. To avoid show wild uls
            // in the page before init the menu.
            var self = this,
				o = self.options,
                orientation = o.orientation,
                mode = o.mode,
                ul, li, ele = self.element, sublist, breadcrumb,
				keycode = $.ui.keyCode;

            ele.hide();
            self.cssPre = "wijmo-wijmenu";
            self.nowIndex = 9999;
            self.activeItem = null;
            self.refresh();
            ele.attr("tabIndex", 0);
            ele.bind("keydown.wijmenu", function (event) {
                if (o.disabled) {
                    return;
                }
                var activeItem = self.activeItem,
                isRoot = self._isRoot(activeItem.parent());
                switch (event.keyCode) {
                case keycode.PAGE_UP:
                    self.previousPage(event);
                    self._preventEvent(event);
                    break;
                case keycode.PAGE_DOWN:
                    self.nextPage(event);
                    self._preventEvent(event);
                    break;
                case keycode.UP:
                    if (orientation === "vertical" || mode === "sliding" || !isRoot) {
                        self.previous(event);
                        self._preventEvent(event);
                    }
                    break;
                case keycode.DOWN:
                    if (orientation === "vertical" || mode === "sliding" || !isRoot) {
                        self.next(event);
                        self._preventEvent(event);
                    }
                    else {
                        if (activeItem) {
                            if (mode === "flyout" && activeItem.has("ul").length > 0) {
                                sublist = activeItem.find("ul:first");
                                if (sublist.is(":hidden")) {
                                    self._showFlyoutSubmenu(event, activeItem, sublist);
                                    self.activate(event, sublist
                                .children(".wijmo-wijmenu-item:first"));
                                }
                            }
                        }
                    }
                    break;
                case keycode.RIGHT:
                    if (orientation === "horizontal" && isRoot && mode === "flyout") {
                        self.next(event);
                        self._preventEvent(event);
                    }
                    else {
                        if (activeItem) {
                            if (mode === "flyout" && activeItem.has("ul").length > 0) {
                                sublist = activeItem.find("ul:first");
                                if (sublist.is(":hidden")) {
                                    self._showFlyoutSubmenu(event, activeItem, sublist);
                                    self.activate(event, sublist
                                .children(".wijmo-wijmenu-item:first"));
                                }
                            }
                            else if (mode === "sliding") {
                                sublist = activeItem.find("ul:first");
                                if (sublist.length > 0) {
                                    activeItem.children(":first").trigger("click");
                                    self.activate(event, sublist
                                .children(".wijmo-wijmenu-item:first"));
                                }
                            }
                        }
                    }

                    break;
                case keycode.LEFT:
                    if (orientation === "horizontal" && isRoot && mode === "flyout") {
                        self.previous(event);
                        self._preventEvent(event);
                    }
                    else {
                        ul = activeItem.parent();
                        li = ul.parent();
                        if (mode === "flyout") {
                            if (li.is("li")) {
                                self._hideCurrentSubmenu(li);
                                self.activate(event, li);
                            }
                        }
                        else {
                            if (o.backLink && self._backLink &&
                            self._backLink.is(":visible")) {
                                self._backLink.trigger("click");
                                self.activate(event, li);
                            }
                            breadcrumb = $(".wijmo-wijmenu-breadcrumb",
                            self.domObject.menucontainer).find("li a");
                            if (breadcrumb.length > 0) {
                                breadcrumb.eq(breadcrumb.length - 2).trigger("click");
                                self.activate(event, li);
                                ele.focus();
                            }
                        }
                    }
                    break;
                case keycode.ENTER:
                    if (mode === "flyout") {
                        activeItem.children(":first").trigger("click");
                    }
                    else {
                        self.select();
                    }
//                if (activeItem.length > 0) {
//                    if (o.mode === "flyout" && activeItem.has("ul").length > 0) {
//                        self._showFlyoutSubmenu(event, activeItem, activeItem
//			.find("ul:first"));
//                    }
//                    else {
//                        activeItem.children(":first").trigger("click");
//                    }
//                }
                    self._preventEvent(event);
                    break;
                }
            });
        },

        _isRoot: function (obj) {
            return this.rootMenu.get(0) === obj.get(0);
        },

        _destroy: function () {
            var self = this,
				o = self.options;

            self[o.mode === "flyout" ? "_killFlyout" : "_killDrilldown"]();
            self._killmenuItems();
            self._killtrigger();
            if (self.element.is("ul")) {
                self.element.unwrap().unwrap();
            }
            else {
                self.element.unwrap();
            }
            self.element.removeData("domObject").removeData("topmenu")
			.removeData("firstLeftValue");
        },

        destroy: function () {
            /// <summary>
            /// Removes the wijmenu functionality completely.
            /// This returns the element back to its pre-init state.
            /// </summary>
            this._destroy();
            $.Widget.prototype.destroy.apply(this);
        },

        activate: function (event, item) {
            /// <summary>Actives an menu item by deactivating the current item,
            /// scrolling the new one into view, if necessary,making it the active item,
            /// and triggering a focus event.
            /// </summary>
            /// <param name="event" type="Event">The javascript event.</param>
            /// <param name="item" type="jQuery object">a menu item to active</param>
            var self = this,
				scrollContainer = self.domObject.scrollcontainer,
				active = item.eq(0);

            self.deactivate(event);
            if (self.options.mode === "sliding") {
                scrollContainer.wijsuperpanel("scrollChildIntoView", item);
            }
            active.children(":first:not(.ui-state-disabled)")
			.addClass("ui-state-focus")
			.attr("id", "ui-active-menuitem")
			.end();
            self.activeItem = active;
            self._trigger("focus", event, { item: item });
        },

        deactivate: function (event) {
            /// <summary>Clears the current selection.This method is useful when reopening
            /// a menu which previously had an item selected.
            /// </summary>
            /// <param name="event" type="Event">The javascript event.  </param>
            var self = this,
				active = self.activeItem;

            if (!active) {
                return;
            }
            active.children(":first")
			.removeClass("ui-state-focus")
			.removeAttr("id");
            self._trigger("blur");
            self.activeItem = null;
        },

        next: function (event) {
            /// <summary>Selects the next item based on the active one. Selects the first
            /// item if none is active or if the last one is active.</summary>
            /// <param name="event" type="Event">The javascript event.</param>
            this._move("next", "." + menuitemCss + ":first", event);
        },

        previous: function (event) {
            /// <summary>Selects the previous item based on the active one. Selects the 
            ///last item if none is active or if the first one is active.</summary>
            /// <param name="event" type="Event">The javascript event.</param>
            this._move("prev", "." + menuitemCss + ":last", event);
        },

        first: function () {
            /// <summary>Determines whether the active item is the first
            /// menu item</summary>
            /// <returns type="Boolean" />
            var active = this.activeItem;
            return active && !active.prevAll("." + menuitemCss).length;
        },

        last: function () {
            /// <summary>Determines whether the active item is the 
            ///last menu item</summary>
            /// <returns type="Boolean" />
            var active = this.activeItem;
            return active && !active.nextAll("." + menuitemCss).length;
        },

        nextPage: function (event) {
            /// <summary>This event is similar to the next event,
            ///but it jumps a whole page.</summary>
            /// <param name="event" type="Event">The javascript event.</param>
            var self = this,
				activeItem = self.activeItem,
				parent = activeItem.parent(), base, height, result;

            if (self.options.mode === "sliding" && self._hasScroll()) {
                if (!activeItem || self.last()) {
                    self.activate(event, parent.children(":first"));
                    return;
                }
                base = activeItem.offset().top;
                height = self.options.maxHeight;
                result = parent.children("li").filter(function () {
                    var node = $(this),
					close = height - (node.offset().top - base + node.height()),
					lineheight = node.height();
                    return close < lineheight && close > -lineheight;
                });

                if (!result.length) {
                    result = parent.children(":last");
                }
                self.activate(event, result.last());
            } else {
                self.activate(event, parent
				.children(!activeItem || self.last() ? ":first" : ":last"));
            }
        },

        previousPage: function (event) {
            /// <summary>This event is silimlar to the previous event,
            ///but it jumps a whole page.</summary>
            /// <param name="event" type="Event">The javascript event.</param>
            var self = this,
				activeItem = self.activeItem,
				parent = activeItem.parent(), base, height, result;

            if (self.options.mode === "sliding" && self._hasScroll()) {
                if (!activeItem || self.first()) {
                    self.activate(event, parent.children(":last"));
                    return;
                }
                base = activeItem.offset().top;
                height = self.options.maxHeight;
                result = parent.children("li").filter(function () {
                    var node = $(this),
					close = node.offset().top - base + height - node.height(),
					lineheight = node.height();
                    return close < lineheight && close > -lineheight;
                });
                if (!result.length) {
                    result = parent.children(":first");
                }
                self.activate(event, result.first());
            } else {
                self.activate(event, parent
				.children(!activeItem || self.first() ? ":last" : ":first"));
            }
        },

        select: function (event) {
            /// <summary>Selects the active item,triggering the select event for that
            ///item. This event is useful for custom keyboard handling.</summary>
            /// <param name="event" type="Event">The javascript event.</param>
            var self = this;
            self._trigger("select", event, { item: self.activeItem });
            self._setCheckable();
        },

        _setCheckable: function () {
            if (this.options.checkable) {
                this.activeItem.children(":first").toggleClass("ui-state-active");
            }
        },

        ///set options
        _setOption: function (key, value) {
            if (this["_set_" + key]) {
                this["_set_" + key](value);
            }
            this.options[key] = value;
        },

        _set_mode: function (value) {
            this._destroy();
            this.options.mode = value;
            this.refresh();
        },

        _set_orientation: function (value) {
            var self = this,
				menuContainer = self.domObject.menucontainer;

            menuContainer
			.removeClass(self.cssPre + "-vertical " + self.cssPre + "-horizontal");
            if (self.options.mode === "flyout") {
                menuContainer.addClass(self.cssPre + "-" + value);
                $(">li:has(ul)", self.rootMenu).each(function () {
                    var cssPre = "ui-icon-triangle-1-",
					oldCss = value === "horizontal" ? "e" : "s",
					newCss = value === "horizontal" ? "s" : "e";
                    $(">.wijmo-wijmenu-link", this).find("." + cssPre + oldCss)
					.removeClass(cssPre + oldCss + " " + cssPre + newCss)
					.addClass(cssPre + newCss);
                });
            }
            else {
                menuContainer
				.addClass(self.cssPre + "-vertical");
            }
        },

        _getTriggerEle: function () {
            return $(this.options.trigger).filter(function () {
                return $(this).closest(".wijmo-wijmenu").length === 0;
            });
        },

        _set_triggerEvent: function (value) {
            var self = this,
				o = self.options,
				triggerEle = self._getTriggerEle();

            self._killtrigger();
            o.triggerEvent = value;
            if (triggerEle.length > 0) {
                self._initTrigger(triggerEle);
            }
            if (o.mode === "flyout") {
                self._killFlyout();
                self._flyout();
            }
        },

        _set_trigger: function (value) {
            var self = this,
				o = self.options,
				triggerEle = self._getTriggerEle();

            self._killtrigger();
            o.trigger = value;
            if (triggerEle.length > 0) {
                self._initTrigger(triggerEle);
            }
            if (o.mode === "flyout") {
                self._killFlyout();
                self._flyout();
            }
        },

        _initTrigger: function (triggerEle) {
            var o = this.options,
				event = o.triggerEvent,
				self = this,
				menuContainer = self.domObject.menucontainer,
				namespace = ".wijmenu";

            var triggers = $(this.options.trigger);
            
            if(typeof this.options.trigger == 'string') {
                
                switch(event) {
                    case "click":
                        triggers.die(event + namespace).live(event + namespace, function (e) {
                            if (o.mode !== "popup") {
                                self._displaySubmenu(e, triggerEle, menuContainer);
                            }
                        });
                        break;
                    case "mouseenter":
                        triggers.die(event + namespace).live(event + namespace, function (e) {
                            self._displaySubmenu(e, triggerEle, menuContainer);
                        });
                        break;
                    case "dblclick":
                        triggers.die(event + namespace).live(event + namespace, function (e) {
                            self._displaySubmenu(e, triggerEle, menuContainer);
                        });
                        break;
                    case "rtclick":
                        triggers.die("contextmenu" + namespace).live("contextmenu" + namespace, function (e) {
                            menuContainer.hide();
                            self._displaySubmenu(e, triggerEle, menuContainer);
                            e.preventDefault();
                        });
                        break;
                    }
            } 
            else {
                switch(event) {
                    case "click":
                        triggers.unbind(event + namespace).bind(event + namespace, function (e) {
                            if (o.mode !== "popup") {
                                self._displaySubmenu(e, triggerEle, menuContainer);
                            }
                        });
                        break;
                    case "mouseenter":
                        triggers.unbind(event + namespace).bind(event + namespace, function (e) {
                            self._displaySubmenu(e, triggerEle, menuContainer);
                        });
                        break;
                    case "dblclick":
                        triggers.unbind(event + namespace).bind(event + namespace, function (e) {
                            self._displaySubmenu(e, triggerEle, menuContainer);
                        });
                        break;
                    case "rtclick":
                        triggers.unbind(event + namespace).bind("contextmenu" + namespace, function (e) {
                            menuContainer.hide();
                            self._displaySubmenu(e, triggerEle, menuContainer);
                            e.preventDefault();
                        });
                        break;
                    }
            }

        },

        _killtrigger: function () {
            var o = this.options, triggerEle;

            if (o.trigger !== "") {
                triggerEle = $(o.trigger);
                if (triggerEle && triggerEle.length > 0) {
                    triggerEle.unbind(".wijmenu");
                    //$(document).unbind("click.wijmenudoc");
                }
            }
        },

        _move: function (direction, edge, event) {
            var active = this.activeItem, next, parent;

            if (!active) {
                this.activate(event, this.rootMenu.children(edge));
                return;
            }
            next = $(active)[direction + "All"]("." + menuitemCss).eq(0);
            parent = active.parent();
            if (next.length) {
                this.activate(event, next);
            } else {
                this.activate(event, parent.children(edge));
            }
        },

        refresh: function () {
            /// <summary>Renders all non-menu-items as menuitems,called once by _create.
            /// Call this method whenever adding or replaceing items in the menu via DOM
            /// operations,for example,via menu.append
            /// ("<li><a href='#'>new item</a></li>").wijmenu("refresh")</summary>
            var self = this,
				ele = self.element,
				menuCss = "wijmo-wijmenu",
				o = self.options,
				scrollcontainer, menucontainer, domObject, triggerEle, breadcrumb,
				seperatorCss = menuCss + "-separator ui-state-default ui-corner-all",
				headerCss = "ui-widget-header ui-corner-all",
				menuItemCss = "ui-widget " + menuitemCss +
							" ui-state-default ui-corner-all",
				menuLinkCss = menuCss + "-link ui-corner-all";

            if (self.domObject) {
                self._destroy();
            }
            if (ele.is("ul")) {
                self.rootMenu = ele;
                scrollcontainer = ele.wrap("<div></div>").parent();
                menucontainer = scrollcontainer.wrap("<div></div>").parent();
            }
            else if (ele.is("div")) {
                self.rootMenu = $("ul:first", ele);
                scrollcontainer = ele;
                menucontainer = ele.wrap("<div></div>").parent();
            }


            scrollcontainer.addClass("scrollcontainer checkablesupport");
            menucontainer.addClass("ui-widget ui-widget-content " + menuCss +
				" ui-corner-all ui-helper-clearfix")
			.attr("aria-activedescendant", "ui-active-menuitem");
            if (o.orientation === "horizontal" && o.mode === "flyout") {
                menucontainer.addClass(menuCss + "-" + o.orientation);
            }
            domObject = { scrollcontainer: scrollcontainer,
                menucontainer: menucontainer
            };
            self.domObject = domObject;
            self.rootMenu.data("topmenu", true);
            if (!self.rootMenu.hasClass(menuCss + "-list ui-helper-reset")) {
                self.rootMenu.addClass(menuCss + "-list ui-helper-reset");
            }
            $("li", self.rootMenu).each(function (i, n) {
                //var isFirstLevel = $(n).parent().parent().parent().is(".wijmo-wijmenu");
                var hasSubmenu = $(">ul:first", n).length > 0,
					li = $(n),
					icon, link = $(">:first", li);

                if (link.length === 0) {
                    li.addClass(seperatorCss);
                }
                else {
                    li.attr("role", "menuitem");
                    if (link.is("a")) {
                        link.bind("mouseenter.wijmenuitem", function () {
                            var element = $(this);
                            
                            if(!element.hasClass('ui-state-disabled'))
                                $(this).addClass("ui-state-hover");
                        }).bind("mouseleave.wijmenuitem", function () {
                            var element = $(this);
                            
                            if(!element.hasClass('ui-state-disabled'))
                                $(this).removeClass("ui-state-hover");

                            if (element.data("subMenuOpened")) {
                                element.addClass("ui-state-active");
                            }
                        });
                        if (!li.hasClass(menuitemCss)) {
                            li.addClass(menuItemCss);
                            link.addClass(menuLinkCss);
                            link.wrapInner("<span>").children("span")
							.addClass(menuCss + "-text");
                            if (hasSubmenu) {
                                icon = $("<span>")
								.addClass("ui-icon ui-icon-triangle-1-e");
                                link.append(icon);
                            }
                        }
                    }
                    else if (link.is("h1,h2,h3,h4,h5")) {
                        li.addClass(headerCss);
                    }
                    else {
                        li.addClass(menuItemCss);
                        link.addClass(menuLinkCss);
                        if (hasSubmenu) {
                            icon = $("<span>").addClass("ui-icon ui-icon-triangle-1-e");
                            link.append(icon);
                        }
                    }
                }
            });
            ele.show();
            $("ul", self.rootMenu).each(function () {
                $(this).addClass(menuCss + "-list ui-widget-content ui-corner-all " +
					"ui-helper-clearfix " + menuCss + "-child ui-helper-reset");
                $(this).hide();
            });
            this[o.mode === "flyout" ? "_flyout" : "_drilldown"]();
            if (o.trigger !== "") {
                triggerEle = self._getTriggerEle();
                if (triggerEle.length > 0) {
                    menucontainer.hide();
                    self._initTrigger(triggerEle);
                }
            }
            $(document).bind("click.wijmenudoc", function (e) {
                ///fixed when click the breadcrumb choose item link to show
                /// the root menu in sliding menu.
                if ($(e.target).parent().is(".wijmo-wijmenu-all-lists")) {
                    return;
                }

                var obj = $(e.target).closest(".wijmo-wijmenu");
                if (obj.length === 0) {
                    if (o.mode === "sliding") {
                        breadcrumb = $(".wijmo-wijmenu-breadcrumb", menucontainer);
                        // fixed a bug, when the trigger is not seted. 
                        // when click the document, trigger this method!
                        if (o.trigger === "") {
                            return;
                        }
                        self._resetDrilldownMenu(breadcrumb);
                    }
                    else if (o.mode === "flyout" && o.triggerEvent !== "mouseenter") {
                        self._hideAllMenus();
                        return;
                    }

                    if (triggerEle && triggerEle.length > 0) {
                        self._hideSubmenu(menucontainer);
                    }
                }
            });
        },

        _showFlyoutSubmenu: function (e, li, subList) {
            var self = this,
				curList = self.currentMenuList, i;

            if (curList !== undefined) {
                for (i = curList.length; i > 0; i--) {
                    if (curList[i - 1].get(0) === li.parent().get(0)) {
                        break;
                    }
                    else {
                        self._hideSubmenu(curList[i - 1]);
                    }
                }
            }
            self._displaySubmenu(e, li.find('.wijmo-wijmenu-link:eq(0)'), subList);
        },

        _getItemTriggerEvent: function (item) {
            var self = this,
				o = self.options,
				triggerEvent = "default", triggerEle;

            if (o.trigger !== "") {
                if (item.is(o.trigger) || self.element.is(o.trigger)) {
                    triggerEvent = o.triggerEvent;
                }
                else {
                    item.parents(".wijmo-wijmenu-parent").each(function (i, n) {
                        if ($(n).is(o.trigger)) {
                            triggerEvent = o.triggerEvent;
                            return false;
                        }
                    });
                    if (triggerEvent === "default") {
                        triggerEle = self._getTriggerEle();
                        if (triggerEle.length > 0) {
                            triggerEvent = o.triggerEvent;
                        }
                    }
                }
            }
            item.data("triggerEvent", triggerEvent);
            return triggerEvent;
        },

        _flyout: function () {
            var self = this,
				container = self.domObject.menucontainer,
				o = self.options,
				linkCss = "wijmo-wijmenu-link",
				eastIconCss = "ui-icon-triangle-1-e",
				southIconCss = "ui-icon-triangle-1-s",
				parentItemCss = "wijmo-wijmenu-parent";

            container.attr("role", "menu");
            if (o.orientation === "horizontal") {
                container.attr("role", "menubar");
                self.rootMenu.children("li:has(ul)").each(function () {
                    $(this).children("." + linkCss).find("." + eastIconCss)
					.removeClass(eastIconCss).addClass(southIconCss);
                });
            }
            container.find('li:has(ul)').each(function () {
                var nameSpace = ".wijmenu",
					li = $(this).attr("aria-haspopup", true), showTimer, hideTimer,
					triggerEvent = self._getItemTriggerEvent(li), link, subList;

                li.children("ul").attr("role", "menu")
				.attr("aria-activedescendant", "ui-active-menuitem")
                .bind("mouseleave." + nameSpace, function () {
                	var subel = $(this).parent();
                    hideTimer = setTimeout(function () {
                    	self._hideCurrentSubmenu(subel);
                    }, o.hideDelay);
                });
                if (triggerEvent !== "default" &&
				o.triggerEvent !== "mouseenter") {
                    li.removeClass(parentItemCss)
					.addClass(parentItemCss);
                    link = $(this).find("." + linkCss + ":eq(0)");
                    subList = link.next();

                    switch (o.triggerEvent) {
                    case "click":
                        link.bind("click" + nameSpace, function (e) {
                            self._showFlyoutSubmenu(e, li, subList);
                        });
                        break;
                    case "dblclick":
                        link.bind("dblclick" + nameSpace, function (e) {
                            self._showFlyoutSubmenu(e, li, subList);
                        });
                        break;
                    case "rtclick":
                        link.bind("contextmenu" + nameSpace, function (e) {
                            self._showFlyoutSubmenu(e, li, subList);
                            e.preventDefault();
                        });
                        break;
                    }
                    subList.data("notClose", true);
                }
                else {
                    li.removeClass(parentItemCss)
					.addClass(parentItemCss);
                    $(this).find("." + linkCss + ":eq(0)").bind("mouseenter.wijmenu",
					function (e) {
					    clearTimeout(hideTimer);
					    var subList = $(this).next(),
							link = $(this);

					    showTimer = setTimeout(function () {
					        self._displaySubmenu(e, link, subList);
					    }, o.showDelay);
					}).bind("mouseleave" + nameSpace,
					function () {
					    clearTimeout(showTimer);
					    var subList = $(this).next();
					    //In slide effects, before animation, 
					    //it wraped a div to the ul element.
					    if (!subList.is("ul")) {
					        subList = subList.children("ul:first");
					    }
					    hideTimer = setTimeout(function () {
					        self._hideSubmenu(subList);
					    }, o.hideDelay);
					});


                    $(this).find("ul ." + linkCss + ",ul >.ui-widget-header,ul " +
						'>.wijmo-wijmenu-separator').bind("mouseenter" + nameSpace,
					function (e) {
					    clearTimeout(hideTimer);
					});
                }
            });


            ///when click the menu item hide the submenus.
            container.find("." + linkCss).bind("click.wijmenu", function (e) {
                if ($(this).is("a")) {
                    if ($(this).parent().find("ul").length === 0) {
                        self._hideAllMenus();
                    }
                    else if (!(o.trigger !== "" &&
					$(this).parent().data("triggerEvent") !== "default" &&
					 o.triggerEvent !== "mouseenter")) {
                        self._hideAllMenus();
                    }
                    else {
                        var curList = self.currentMenuList, item, j;
                        if (curList !== undefined) {
                            item = $(this).parent();
                            if (item.has("ul").length === 0) {
                                for (j = curList.length; j > 0; j--) {
                                    if (curList[j - 1].get(0) === item.parent().get(0)) {
                                        break;
                                    }
                                    else {
                                        self._hideSubmenu(curList[j - 1]);
                                    }
                                }
                            }
                        }
                    }
                    self.activate(e, $(this).parent());
                }
                self.select(e);
                //self.focus();
                if ($(this).attr("href") === "#") {
                    e.preventDefault();
                }
            })
			.bind("focusin", function (e) {
			    if ($(this).is("a")) {
			        self.activate(e, $(this).parent());
			    }
			});
        },

        _hideAllMenus: function () {
            var self = this, container, outerTrigger, i, ul,
				ele = self.rootMenu;

            ul = ele.find("ul");
            for (i = ul.length - 1; i >= 0; i--) {
                self._hideSubmenu($(ul[i]));
            }
            if (self.options.trigger !== "") {
                container = self.domObject.menucontainer;
                if (container.is(":animated")) {
                    return;
                }
                // if the trigger is outer of the menu, 
                //when hide all menus hide the root menu.
                outerTrigger = self._getTriggerEle();
                if (outerTrigger.length === 0) {
                    return;
                }
                self._hideSubmenu(self.domObject.menucontainer);
            }
        },

        hideAllMenus: function () {
            this._hideAllMenus();
        },

        _killFlyout: function () {
            var container = this.domObject.menucontainer.attr("role", "");

            container.find("li").each(function () {
                $(this).removeClass("wijmo-wijmenu-parent").unbind(".wijmenu")
				.children(":first").unbind(".wijmenu").attr("aria-haspopup", "");
            });
        },

        _killmenuItems: function () {
            var ele = this.rootMenu;
            ele.removeClass("wijmo-wijmenu-list ui-helper-reset " +
				"wijmo-wijmenu-content ui-helper-clearfix");
            ele.find("li").each(function () {
                var item = $(this), link;
                item.removeClass("ui-widget " + menuitemCss + " ui-state-default " +
				"ui-corner-all wijmo-wijmenu-parent ui-widget-header " +
				"wijmo-wijmenu-separator");
                link = item.children(".wijmo-wijmenu-link");
                link.removeClass("wijmo-wijmenu-link ui-corner-all ui-state-focus " +
				"ui-state-hover ui-state-active")
				.html(link.children(".wijmo-wijmenu-text").html())
				.unbind(".wijmenu .wijmenuitem");
                item.children("ul").removeClass("wijmo-wijmenu-list ui-widget-content" +
				" ui-corner-all ui-helper-clearfix wijmo-wijmenu-child ui-helper-reset")
				.attr("role", "").attr("aria-activedescendant", "")
				.show().css({ left: "", top: "", position: "" }).attr("hidden", "");
            });
            this.domObject.menucontainer.removeClass("");
            $(document).unbind("click.wijmenudoc");
        },

        _sroll: function () {
            var scroll = this.domObject.scrollcontainer,
				options = this.options.superPanelOptions || {};

            scroll.height(this.options.maxHeight);
            scroll.wijsuperpanel(options);
        },

        _hasScroll: function () {
            var scroll = this.domObject.scrollcontainer;
            return scroll.data("wijsuperpanel").vNeedScrollBar;
        },


        _resetDrillChildMenu: function (el) {
            el.removeClass("wijmo-wijmenu-scroll wijmo-wijmenu-current").height("auto");
        },

        _checkDrillMenuHeight: function (el, mycontainer, scrollcontainer) {
            var self = this,
				fixPadding = 5;

            mycontainer.height(el.height());
            scrollcontainer.wijsuperpanel("option", "hScroller", { scrollValue: 0 });
            scrollcontainer.wijsuperpanel("option", "vScroller", { scrollValue: 0 });
            scrollcontainer.wijsuperpanel("paintPanel");
            if (self._hasScroll()) {
                if (el.prev().length > 0) {
                    fixPadding = el.prev().css("padding-left").replace(/px/g, "");
                }
                el.width(scrollcontainer.find(".wijmo-wijsuperpanel-contentwrapper" +
					":first").width() - fixPadding);
            }
        },

        _resetDrilldownMenu: function (breadcrumb) {
            var self = this,
				o = self.options,
				ele = self.rootMenu,
				container = self.domObject.menucontainer,
				crumbDefaultHeader = $('<li class="wijmo-wijmenu-breadcrumb-text">' +
					o.crumbDefaultText + '</li>'),
				mycontainer = ele.parent();

            $('.wijmo-wijmenu-current', container).removeClass('wijmo-wijmenu-current');
            ele.animate({ left: 0 }, o.showDuration, function () {
                $(this).find('ul').each(function () {
                    $(this).hide();
                    self._resetDrillChildMenu($(this));
                });
                ele.addClass('wijmo-wijmenu-current');
            });
            $('.wijmo-wijmenu-all-lists', container).find('span').remove();
            breadcrumb.empty().append(crumbDefaultHeader);
            $('.wijmo-wijmenu-footer', container).empty().hide();
            self._checkDrillMenuHeight(ele, mycontainer, self.domObject.scrollcontainer);
        },

        _drilldown: function () {
            var self = this,
				ele = self.rootMenu,
				mycontainer = ele.wrap("<div>").parent().css("position", "relative"),
				container = self.domObject.menucontainer.attr("role", "menu"),
				scrollcontainer = self.domObject.scrollcontainer,
				o = self.options, fixPadding,
				breadcrumb = $('<ul class="wijmo-wijmenu-breadcrumb ui-state-default' +
					' ui-corner-all ui-helper-clearfix"></ul>'),
				crumbDefaultHeader = $('<li class="wijmo-wijmenu-breadcrumb-text">' +
				o.crumbDefaultText + '</li>'),
				firstCrumbText = (o.backLink) ? o.backLinkText : o.topLinkText,
				firstCrumbClass = (o.backLink) ? 'wijmo-wijmenu-prev-list' :
					'wijmo-wijmenu-all-lists',
				firstCrumbLinkClass = (o.backLink) ?
					'ui-state-default ui-corner-all' : '',
				firstCrumbIcon = (o.backLink) ?
					'<span class="ui-icon ui-icon-triangle-1-w"></span>' : '',
				firstCrumb = $('<li class="' + firstCrumbClass +
					'"><a href="#" class="' + firstCrumbLinkClass + '">' +
					firstCrumbIcon + firstCrumbText + '</a></li>');

            container.addClass('wijmo-wijmenu-ipod wijmo-wijmenu-container');
            if (o.backLink) {
                breadcrumb.addClass('wijmo-wijmenu-footer').appendTo(container).hide();
            }
            else {
                breadcrumb.addClass('wijmo-wijmenu-header').prependTo(container);
            }
            if (!o.backLink) {
                breadcrumb.append(crumbDefaultHeader);
            }
            ele.addClass('wijmo-wijmenu-content wijmo-wijmenu-current ui-widget-content' +
				' ui-helper-clearfix').css({ width: container.width() })
			.find('ul').css({
			    width: container.width(),
			    left: container.width()
			}).attr("role", "menu").attr("aria-activedescendant", "ui-active-menuitem")
			.addClass('ui-widget-content');
            //.hide();
            mycontainer.height(self.rootMenu.height());
            self._sroll();
            if (self._hasScroll()) {
                fixPadding = 5;
                if (ele.children(":first").children(":first").length > 0) {
                    fixPadding = ele.children(":first").children(":first")
					.css("padding-left").replace(/px/g, "");
                }
                ele.width(scrollcontainer
				.find(".wijmo-wijsuperpanel-contentwrapper:first").width() - fixPadding);
            }

            self.element.data("firstLeftValue", parseFloat(ele.css('left')));
            $('li>.wijmo-wijmenu-link', ele).each(function () {
                // if the link opens a child menu:
                if ($(this).next().is('ul')) {
                    $(this).click(function (e) { // ----- show the next menu			
                        var nextList = $(this).next(),
							parentUl = $(this).parents('ul:eq(0)'),
							parentLeft = (parentUl.data("topmenu")) ?
							0 : parseFloat(ele.css('left')),
							crumbText, newCrumb,
							nextLeftVal = Math.round(parentLeft -
							parseFloat(container.width())),
							footer = $('.wijmo-wijmenu-footer', container),
							setPrevMenu = function (backlink) {
							    var b = backlink,
								c = $('.wijmo-wijmenu-current', container), prevList;
							    if (c.get(0) === self.rootMenu.get(0)) {
							        return;
							    }
							    prevList = c.parents('ul:eq(0)');
							    c.hide().attr('aria-expanded', 'false');
							    self._resetDrillChildMenu(c);
							    self._checkDrillMenuHeight(prevList, mycontainer,
								 scrollcontainer);
							    prevList.addClass('wijmo-wijmenu-current')
								.attr('aria-expanded', 'true');
							    if (prevList.hasClass('wijmo-wijmenu-content')) {
							        b.remove();
							        footer.hide();
							    }
							};

                        // show next menu	
                        self._resetDrillChildMenu(parentUl);
                        self._checkDrillMenuHeight(nextList, mycontainer,
						scrollcontainer);
                        self._slidingAnimation(ele, nextLeftVal, null);
                        nextList.show().addClass('wijmo-wijmenu-current')
						.attr('aria-expanded', 'true');

                        // initialize "back" link
                        if (o.backLink) {
                            if (footer.find('a').size() === 0) {
                                footer.show();
                                self._backLink = $('<a href="#"><span class="ui-icon ' +
								'ui-icon-triangle-1-w"></span> <span>' + o.backLinkText +
                                '</span></a>')
									.appendTo(footer)
									.click(function (e) { // ----- show the previous menu
									    var b = $(this), prevLeftVal;
									    ele.stop(true, true);
									    prevLeftVal = parseInt(ele.css('left'), 10) +
										parseInt(container.width(), 10);
									    ///to fix click the back button too quickly.
									    ///The menu display wrong.
									    if (prevLeftVal > parentLeft) {
									        return;
									    }
									    self._slidingAnimation(ele, prevLeftVal,
										function () {
										    setPrevMenu(b);
										});
										e.preventDefault();
									});
                            }
                        }
                        // or initialize top breadcrumb
                        else {
                            if (breadcrumb.find('li').size() === 1) {
                                breadcrumb.empty().append(firstCrumb);
                                firstCrumb.find('a').click(function (e) {
                                    self._resetDrilldownMenu(breadcrumb);
                                    e.preventDefault();
                                });
                            }
                            $('.wijmo-wijmenu-current-crumb', container)
							.removeClass('wijmo-wijmenu-current-crumb');
                            crumbText = $(this).find('span:eq(0)').text();
                            newCrumb = $('<li class="wijmo-wijmenu-current-crumb">' +
							'<a href="#" class="wijmo-wijmenu-crumb">' + crumbText +
							'</a></li>');
                            newCrumb.appendTo(breadcrumb).find('a').click(function (e) {
                                if (!$(this).parent()
									.is('.wijmo-wijmenu-current-crumb')) {
                                    var newLeftVal = -($('.wijmo-wijmenu-current')
										.parents('ul').size() - 1) * 180;

                                    self._slidingAnimation(ele, newLeftVal, function () {
                                        setPrevMenu();
                                    });
                                    //make this the current crumb, delete all  
                                    //breadcrumbs, and navigate to the relevant menu
                                    $(this).parent()
									.addClass('wijmo-wijmenu-current-crumb')
									.find('span').remove();
                                    $(this).parent().nextAll().remove();
                                    e.preventDefault();
                                }
                            });
                            newCrumb.prev()
							.append(' <span class="ui-icon ui-icon-carat-1-e"></span>');
                        }
                        if ($(this).attr("href") === "#") {
                            e.preventDefault();
                        }
                    });
                }
                // if the link is a leaf node (doesn't open a child menu)
                else {
                    $(this).click(function (e) {
                        self.activate(e, $(this).parent());
                        self.select(e);
                        if (o.trigger) {
                            var triggers = self._getTriggerEle();

                            if (triggers.length) {
                                self._hideSubmenu(container);
                                self._resetDrilldownMenu(breadcrumb);
                            }
                        }
                        if ($(this).attr("href") === "#") {
                            e.preventDefault();
                        }
                    });
                }
            });
        },

        _slidingAnimation: function (ele, left, callback) {
            var o = this.options.slidingAnimation;
            if (o && !o.disabled) {
                ele.stop(true, true)
				.animate({ left: left }, o.duration, o.easing, callback);
            } else {
                ele.css("left", left);
                callback.call(this);
            }
        },

        _killDrilldown: function () {
            var ele = this.rootMenu,
				domObject = this.domObject,
				style = { width: "", height: "" };

            ele.css(style).removeClass("ui-widget-content");
            domObject.scrollcontainer.css(style);
            domObject.scrollcontainer.wijsuperpanel("destroy");
            domObject.scrollcontainer.removeClass("wijmo-wijsuperpanel").append(ele);
            ele.prevAll().remove();
            domObject.menucontainer
			.removeClass("wijmo-wijmenu-ipod wijmo-wijmenu-container");
            $('.wijmo-wijmenu-current', domObject.menucontainer)
			.removeClass('wijmo-wijmenu-current');
            $(".wijmo-wijmenu-breadcrumb", domObject.menucontainer).remove();
            ele.find("li").each(function () {
                var obj = $(this).children(":first");
                obj.unbind("click");
            });
            $("ul", ele).css({ left: "", width: "" });
            ele.css("left", "");
            domObject.scrollcontainer = domObject.menucontainer.children(":first");
        },

        ///popup menu
        //		_popup: function () {
        //			var self = this;
        //			var o = self.options;
        //			var triggerElement = o.trigger;
        //			if (triggerElement && triggerElement !==
        // "" && $(triggerElement).length > 0) {
        //				triggerElement = $(triggerElement);
        //				self.element.data("domObject").menucontainer
        //.css("position", "relative");
        //				triggerElement.bind("click.wijmenu", function (e) {
        //					self._displaySubmenu(triggerElement, 
        //self.element.data("domObject").menucontainer, e);
        //				});
        //				self.element.find("a.wijmo-wijmenu-link")
        //.bind("click.wijmenu", function () {
        //					var value = $(this).text();
        //					triggerElement.val(value);
        //					self._hideAllMenus();
        //				});
        //			}
        //		},

        _getItemByValue: function (val) {
            var items = this.rootMenu.find("a.wijmo-wijmenu-link").filter(function () {
                return $(this).text() === val;
            });
            if (items.length > 0) {
                return items.eq(0).parent();
            }
            return null;
        },
        //now do not support the popup menu
        /*
        _setPopupPosition: function (e) {
        var self = this;
        var triggerElement = $(self.options.trigger);
        var val = triggerElement.val() || triggerElement.attr("value");
        if (val !== "") {
        var item = self._getItemByValue(val);
        if (item) {
        var offset = triggerElement.offset();
        var height = triggerElement.outerHeight(true);
        var position = item.position();
        var newOffset = {
        left: offset.left,
        top: offset.top - position.top
        };
        self.element.data("domObject").menucontainer.css({
        left: 0,
        top: 0
        }).offset(newOffset);
        self.activate(e, item);
        }
        else {
        self._setPosition(triggerElement, self.element
        //.data("domObject").menucontainer, false);
        }
        }
        else {
        self._setPosition(triggerElement, self.element
        //.data("domObject").menucontainer, false);
        }
        },
        */
        _displaySubmenu: function (e, item, sublist) {
            var self = this,
				o = self.options,
				animationOptions, direction, showAnimation,
				animations = $.wijmo.wijmenu.animations;

            //now do not support the popup menu and equal-height menu.
            /*
            var parentUl = null;
            if (item.is(".wijmo-wijmenu-link")) {
            parentUl = item.parent().parent();
            }
            var parentHeight = 0;
            if (parentUl) {
            parentHeight = parentUl.innerHeight();
            if (parentHeight === 0) {
            parentHeight = this.element.data("domObject").menucontainer.innerHeight();
            }
            }
            var tag = false;
            if (parentHeight > 0 && parentHeight === sublist.innerHeight()) {
            tag = true;
            }
			
            sublist.show();
            if (o.mode === "popup") {
            this._setPopupPosition(e);
            }
            else {
            //this._setPosition(item, sublist, tag);

            }
            */
            if (item.is("a.wijmo-wijmenu-link")) {
                item.data("subMenuOpened", true);
            }
            if (sublist.is(":visible")) {
                return;
            }
            sublist.show();
            self._setPosition(item, sublist);
            self.nowIndex++;
            self._setZindex(sublist, self.nowIndex);
            sublist.hide();
            self._trigger("showing", e, sublist);

            if ($.fn.wijshow) {
                animationOptions = {
                    context: sublist,
                    show: true
                };

                direction = "left";
                if (o.orientation === "horizontal") {
                    if (sublist.parent().closest("ul").get(0) === this.rootMenu.get(0)) {
                        direction = "up";
                    }
                }
                showAnimation = $.extend({}, { option: { direction: direction} },
					o.animation, o.showAnimation);
                sublist.wijshow(showAnimation, animations,
					animationOptions, null, function () {
					    var browser = $.browser;
					    if (browser.msie && browser.version === "9.0") {
					        sublist.wrap("<div></div>");
					        sublist.unwrap();
					    }
					    else if (browser.msie && browser.version === "6.0") {
					        sublist.css("overflow", "");
					    }
					    sublist.attr("aria-hidden", false);
					});
            }
            else {
                sublist.show().attr("aria-hidden", false);
            }

            self._isClickToOpen = o.triggerEvent === "click";

            if (!sublist.is(".wijmo-wijmenu")) {
                if (self.currentMenuList === undefined) {
                    self.currentMenuList = [];
                }
                self.currentMenuList.push(sublist);
            }
        },

        _hideCurrentSubmenu: function (aItem) {
            var self = this;
            aItem.find("ul").each(function () {
                if (!$(this).data("notClose")) {
                    self._hideSubmenu($(this));
                }
            });
        },
        _hideSubmenu: function (sublist) {
            var self = this,
				o = self.options,
				animations = $.wijmo.wijmenu.animations,
				animationOptions, list, hideAnimation;

            if (sublist.prev().is(".wijmo-wijmenu-link")) {
                sublist.prev().removeClass("ui-state-active");
            }

            if ($.fn.wijhide) {
                animationOptions = {
                    context: sublist,
                    show: false
                };
                hideAnimation = $.extend({}, o.animation, o.hideAnimation);
                sublist.wijhide(hideAnimation, animations,
				animationOptions, null, function () {
				    self._setZindex(sublist);
				    sublist.attr("aria-hidden", true);
				});
            }
            else {
                sublist.hide().attr("aria-hidden", true);
                self._setZindex(sublist);
            }
            this.element.data("shown", false);
            list = this.currentMenuList;
            if (list) {
                list = $.map(list, function (n) {
                    return n && (n.get(0) === sublist.get(0)) ? null : n;
                });
                this.currentMenuList = $.makeArray(list);
            }
        },

        _setZindex: function (ele, value) {
            var element = this.rootMenu,
				domObject = this.domObject, menucontainer;

            if (!domObject) {
                return;
            }
            menucontainer = domObject.menucontainer;
            if (ele.get(0) === menucontainer.get(0)) {
                return;
            }
            if (value) {
                ele.parent().css("z-index", 10);
                ele.css("z-index", value);
                if (menucontainer.css("z-index") === 0) {
                    menucontainer.css("z-index", 9950);
                }
            }
            else {
                ele.css("z-index", "");
                ele.parent().css("z-index", "");
                if ($.browser.msie && $.browser.version < 8 &&
				 $("ul:visible", element).length === 0) {
                    menucontainer.css("z-index", "");
                }
            }
        },

        _setPosition: function (item, sublist) {
            sublist.css({ left: '0', top: '0', position: 'absolute' });
            var pOption = this._getPosition(item),
				obj = { of: item };
            //now do not support the equal-height menu.
            /*
            if (tag) {
            var parentUl = item.parent().parent();
            if (!parentUl.is(".wijmo-wijmenu-child")) {
            parentUl = this.element.data("domObject").menucontainer;
            }
            obj = { of: parentUl };
            }
            */
            sublist.position($.extend(obj, pOption));
        },

        _getPosition: function (item) {
            var o = this.options,
				pOption = { my: 'left top',
				    at: 'right top'
				};

            //If the menu's orientation is horizontal, 
            //set the first level submenu's position to horizontal. 
            if (o.orientation === "horizontal") {
                if (item.closest("ul").get(0) === this.rootMenu.get(0)) {
                    pOption = { my: 'left top',
                        at: 'left bottom'
                    };
                }
            }
            //If the item is a element outer of the menu.
            if (!item.is(".wijmo-wijmenu-link")) {
                pOption = { my: 'left top',
                    at: 'left bottom'
                };
            }
            pOption = $.extend(pOption, o.position);
            return pOption;
        }
    });

    $.extend($.wijmo.wijmenu, {
        animations: {
            slide: function (options, addtions) {
                options = $.extend({
                    duration: 400,
                    easing: "swing"
                }, options, addtions);
                if (options.show) {
                    options.context.stop(true, true).animate({
                        height: 'show'
                    }, options).attr("aria-hidden", false);
                }
                else {
                    options.context.stop(true, true).animate({
                        height: 'hide'
                    }, options).attr("aria-hidden", true);
                }
            }
        }
    });
} (jQuery));