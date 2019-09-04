/*
 * This plugin filters keyboard input by specified regular expression.
 * Version 1.8
 * https://github.com/akzhan/jquery-keyfilter
 *
 * Source code inspired by Ext.JS (Ext.form.TextField, Ext.EventManager)
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

(function($)
{
	// $.browser fallback for jQuery 1.9+.
	if ($.browser === undefined) {
		$.browser = (function () {
			var ua_match = function (ua) {
				ua = ua.toLowerCase();
				var match = /(chrome)[ \/]([\w.]+)/.exec(ua) ||
				/(webkit)[ \/]([\w.]+)/.exec(ua) ||
				/(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) ||
				/(msie) ([\w.]+)/.exec(ua) ||
				ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) ||
				[];
				
				return { browser:match[ 1 ] || "", version:match[ 2 ] || "0" };
			},
			matched = ua_match(navigator.userAgent),
			browser = {};
			
			if (matched.browser) {
				browser[ matched.browser ] = true;
				browser.version = matched.version;
			}
			
			if (browser.chrome) {
				browser.webkit = true;
			} else if (browser.webkit) {
				browser.safari = true;
			}
			return browser;
		})();
	}

	var defaultMasks = {
		pint:     /[\d]/,
		'int':    /[\d\-]/,
		pnum:     /[\d\.]/,
		money:    /[\d\.\s,]/,
		num:      /[\d\-\.]/,
		hex:      /[0-9a-f]/i,
		email:    /[a-z0-9_\.\-@]/i,
		alpha:    /[a-z_]/i,
		alphanum: /[a-z0-9_]/i
	};

	var Keys = {
		TAB: 9,
		RETURN: 13,
		ESC: 27,
		BACKSPACE: 8,
		DELETE: 46
	};

	// safari keypress events for special keys return bad keycodes
	var SafariKeys = {
		63234 : 37, // left
		63235 : 39, // right
		63232 : 38, // up
		63233 : 40, // down
		63276 : 33, // page up
		63277 : 34, // page down
		63272 : 46, // delete
		63273 : 36, // home
		63275 : 35  // end
	};

    var isSpecialKey = function(e)
	{
		var k = e.keyCode;
		var c = e.charCode;
		return k == 9 || k == 13 || k == 27 ||
			k == 16 || k == 17 ||
			(k >= 18 && k <= 20) ||
			($.browser.opera && !e.shiftKey && (k == 8 || (k >= 33 && k <= 35) || (k >= 36 && k <= 39) || (k >= 44 && k <= 45)))
			;

        };

        /**
         * Returns a normalized keyCode for the event.
         * @return {Number} The key code
         */
        var getKey = function(e)
	{
		var k = e.keyCode || e.charCode;
		return $.browser.safari ? (SafariKeys[k] || k) : k;
        };

        var getCharCode = function(e)
	{
		return e.charCode || e.keyCode || e.which;
	};

	$.fn.keyfilter = function(re)
	{
		return this.on('keypress',function(e)
		{
			var keyNo = e.which;
			if ($.browser.mozilla && (e.ctrlKey || e.altKey)) // PF GitHub #3785
			{
				return;
			} 
			else if (keyNo == 17 || keyNo == 18) // PF GitHub #1852 keyCode.CONTROL and keyCode.ALT
			{
				return;
			}
			
			var c = getCharCode(e), cc = String.fromCharCode(c), ok = true;
			if(!$.browser.mozilla && (isSpecialKey(e) || !cc))
			{
				return;
			}
			if ($.isFunction(re))
			{
				ok = re.call(this, cc);
			}
			else
			{
				ok = re.test(cc);
			}
			if(!ok)
			{
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

	/* 
	 * This will be manually done by our wrapper
	 *  
	$(document).ready(function()
	{
		var tags = $('input[class*=mask],textarea[class*=mask]');
		for (var key in $.fn.keyfilter.defaults.masks)
		{
			tags.filter('.mask-' + key).keyfilter($.fn.keyfilter.defaults.masks[key]);
		}
	}); */

})(jQuery);