/*
	Watermark plugin for jQuery
	Version: 3.0.6
	http://jquery-watermark.googlecode.com/

	Copyright (c) 2009-2010 Todd Northrop
	http://www.speednet.biz/

	June 21, 2010

	Requires:  jQuery 1.2.3+

	Dual licensed under the MIT or GPL Version 2 licenses.
	See mit-license.txt and gpl2-license.txt in the project root for details.
------------------------------------------------------*/

(function ($) {

var
	// Will speed up references to undefined
	undefined,

	// String constants for data names
	dataFlag = "watermark",
	dataClass = "watermarkClass",
	dataFocus = "watermarkFocus",
	dataFormSubmit = "watermarkSubmit",
	dataMaxLen = "watermarkMaxLength",
	dataPassword = "watermarkPassword",
	dataText = "watermarkText",

	// Includes only elements with watermark defined
	selWatermarkDefined = ":data(" + dataFlag + ")",

	// Includes only elements capable of having watermark
	selWatermarkAble = ":text,:password,:search,textarea",

	// triggerFns:
	// Array of function names to look for in the global namespace.
	// Any such functions found will be hijacked to trigger a call to
	// hideAll() any time they are called.  The default value is the
	// ASP.NET function that validates the controls on the page
	// prior to a postback.
	//
	// Am I missing other important trigger function(s) to look for?
	// Please leave me feedback:
	// http://code.google.com/p/jquery-watermark/issues/list
	triggerFns = [
		"Page_ClientValidate"
	],

	// Holds a value of true if a watermark was displayed since the last
	// hideAll() was executed. Avoids repeatedly calling hideAll().
	pageDirty = false;

// Extends jQuery with a custom selector - ":data(...)"
// :data(<name>)  Includes elements that have a specific name defined in the jQuery data collection. (Only the existence of the name is checked; the value is ignored.)
// :data(<name>=<value>)  Includes elements that have a specific jQuery data name defined, with a specific value associated with it.
// :data(<name>!=<value>)  Includes elements that have a specific jQuery data name defined, with a value that is not equal to the value specified.
// :data(<name>^=<value>)  Includes elements that have a specific jQuery data name defined, with a value that starts with the value specified.
// :data(<name>$=<value>)  Includes elements that have a specific jQuery data name defined, with a value that ends with the value specified.
// :data(<name>*=<value>)  Includes elements that have a specific jQuery data name defined, with a value that contains the value specified.
$.extend($.expr[":"], {
	"search": function (elem) {
		return "search" === (elem.type || "");
	},

	"data": function (element, index, matches, set) {
		var data, parts = /^((?:[^=!^$*]|[!^$*](?!=))+)(?:([!^$*]?=)(.*))?$/.exec(matches[3]);

		if (parts) {
			data = $(element).data(parts[1]);

			if (data !== undefined) {

				if (parts[2]) {
					data = "" + data;

					switch (parts[2]) {
						case "=":
							return (data == parts[3]);
						case "!=":
							return (data != parts[3]);
						case "^=":
							return (data.slice(0, parts[3].length) == parts[3]);
						case "$=":
							return (data.slice(-parts[3].length) == parts[3]);
						case "*=":
							return (data.indexOf(parts[3]) !== -1);
					}
				}

				return true;
			}
		}

		return false;
	}
});

$.watermark = {

	// Current version number of the plugin
	version: "3.0.6",

	// Default options used when watermarks are instantiated.
	// Can be changed to affect the default behavior for all
	// new or updated watermarks.
	// BREAKING CHANGE:  The $.watermark.className
	// property that was present prior to version 3.0.2 must
	// be changed to $.watermark.options.className
	options: {

		// Default class name for all watermarks
		className: "watermark",

		// If true, plugin will detect and use native browser support for
		// watermarks, if available. (e.g., WebKit's placeholder attribute.)
		useNative: true
	},

	// Hide one or more watermarks by specifying any selector type
	// i.e., DOM element, string selector, jQuery matched set, etc.
	hide: function (selector) {
		$(selector).filter(selWatermarkDefined).each(
			function () {
				$.watermark._hide($(this));
			}
		);
	},

	// Internal use only.
	_hide: function ($input, focus) {
		var inputVal = $input.val() || "",
			inputWm = $input.data(dataText) || "",
			maxLen = $input.data(dataMaxLen) || 0,
			className = $input.data(dataClass);

		if ((inputWm.length) && (inputVal == inputWm)) {
			$input.val("");

			// Password type?
			if ($input.data(dataPassword)) {

				if (($input.attr("type") || "") === "text") {
					var $pwd = $input.data(dataPassword) || [],
						$wrap = $input.parent() || [];

					if (($pwd.length) && ($wrap.length)) {
						$wrap[0].removeChild($input[0]); // Can't use jQuery methods, because they destroy data
						$wrap[0].appendChild($pwd[0]);
						$input = $pwd;
					}
				}
			}

			if (maxLen) {
				$input.attr("maxLength", maxLen);
				$input.removeData(dataMaxLen);
			}

			if (focus) {
				$input.attr("autocomplete", "off");  // Avoid NS_ERROR_XPC_JS_THREW_STRING error in Firefox

				window.setTimeout(
					function () {
						$input.select();  // Fix missing cursor in IE
					}
				, 1);
			}
		}

		className && $input.removeClass(className);
	},

	// Display one or more watermarks by specifying any selector type
	// i.e., DOM element, string selector, jQuery matched set, etc.
	// If conditions are not right for displaying a watermark, ensures that watermark is not shown.
	show: function (selector) {
		$(selector).filter(selWatermarkDefined).each(
			function () {
				$.watermark._show($(this));
			}
		);
	},

	// Internal use only.
	_show: function ($input) {
		var val = $input.val() || "",
			text = $input.data(dataText) || "",
			type = $input.attr("type") || "",
			className = $input.data(dataClass);

		if (((val.length == 0) || (val == text)) && (!$input.data(dataFocus))) {
			pageDirty = true;

			// Password type?
			if ($input.data(dataPassword)) {

				if (type === "password") {
					var $pwd = $input.data(dataPassword) || [],
						$wrap = $input.parent() || [];

					if (($pwd.length) && ($wrap.length)) {
						$wrap[0].removeChild($input[0]); // Can't use jQuery methods, because they destroy data
						$wrap[0].appendChild($pwd[0]);
						$input = $pwd;
						$input.attr("maxLength", text.length);
					}
				}
			}

			// Ensure maxLength big enough to hold watermark (input of type="text" or type="search" only)
			if ((type === "text") || (type === "search")) {
				var maxLen = $input.attr("maxLength") || 0;

				if ((maxLen > 0) && (text.length > maxLen)) {
					$input.data(dataMaxLen, maxLen);
					$input.attr("maxLength", text.length);
				}
			}

			className && $input.addClass(className);
			$input.val(text);
		}
		else {
			$.watermark._hide($input);
		}
	},

	// Hides all watermarks on the current page.
	hideAll: function () {
		if (pageDirty) {
			$.watermark.hide(selWatermarkAble);
			pageDirty = false;
		}
	},

	// Displays all watermarks on the current page.
	showAll: function () {
		$.watermark.show(selWatermarkAble);
	}
};

$.fn.watermark = function (text, options) {
	///	<summary>
	///		Set watermark text and class name on all input elements of type="text/password/search" and
	/// 	textareas within the matched set. If className is not specified in options, the default is
	/// 	"watermark". Within the matched set, only input elements with type="text/password/search"
	/// 	and textareas are affected; all other elements are ignored.
	///	</summary>
	///	<returns type="jQuery">
	///		Returns the original jQuery matched set (not just the input and texarea elements).
	/// </returns>
	///	<param name="text" type="String">
	///		Text to display as a watermark when the input or textarea element has an empty value and does not
	/// 	have focus. The first time watermark() is called on an element, if this argument is empty (or not
	/// 	a String type), then the watermark will have the net effect of only changing the class name when
	/// 	the input or textarea element's value is empty and it does not have focus.
	///	</param>
	///	<param name="options" type="Object" optional="true">
	///		Provides the ability to override the default watermark options ($.watermark.options). For backward
	/// 	compatibility, if a string value is supplied, it is used as the class name that overrides the class
	/// 	name in $.watermark.options.className. Properties include:
	/// 		className: When the watermark is visible, the element will be styled using this class name.
	/// 		useNative (Boolean or Function): Specifies if native browser support for watermarks will supersede
	/// 			plugin functionality. If useNative is a function, the return value from the function will
	/// 			determine if native support is used. The function is passed one argument -- a jQuery object
	/// 			containing the element being tested as the only element in its matched set -- and the DOM
	/// 			element being tested is the object on which the function is invoked (the value of "this").
	///	</param>
	/// <remarks>
	///		The effect of changing the text and class name on an input element is called a watermark because
	///		typically light gray text is used to provide a hint as to what type of input is required. However,
	///		the appearance of the watermark can be something completely different: simply change the CSS style
	///		pertaining to the supplied class name.
	///
	///		The first time watermark() is called on an element, the watermark text and class name are initialized,
	///		and the focus and blur events are hooked in order to control the display of the watermark.  Also, as
	/// 	of version 3.0, drag and drop events are hooked to guard against dropped text being appended to the
	/// 	watermark.  If native watermark support is provided by the browser, it is detected and used, unless
	/// 	the useNative option is set to false.
	///
	///		Subsequently, watermark() can be called again on an element in order to change the watermark text
	///		and/or class name, and it can also be called without any arguments in order to refresh the display.
	///
	///		For example, after changing the value of the input or textarea element programmatically, watermark()
	/// 	should be called without any arguments to refresh the display, because the change event is only
	/// 	triggered by user actions, not by programmatic changes to an input or textarea element's value.
	///
	/// 	The one exception to programmatic updates is for password input elements:  you are strongly cautioned
	/// 	against changing the value of a password input element programmatically (after the page loads).
	/// 	The reason is that some fairly hairy code is required behind the scenes to make the watermarks bypass
	/// 	IE security and switch back and forth between clear text (for watermarks) and obscured text (for
	/// 	passwords).  It is *possible* to make programmatic changes, but it must be done in a certain way, and
	/// 	overall it is not recommended.
	/// </remarks>

	if (!this.length) {
		return this;
	}

	var hasClass = false,
		hasText = (typeof(text) === "string");

	if (typeof(options) === "object") {
		hasClass = (typeof(options.className) === "string");
		options = $.extend({}, $.watermark.options, options);
	}
	else if (typeof(options) === "string") {
		hasClass = true;
		options = $.extend({}, $.watermark.options, {className: options});
	}
	else {
		options = $.watermark.options;
	}

	if (typeof(options.useNative) !== "function") {
		options.useNative = options.useNative? function () { return true; } : function () { return false; };
	}

	return this.each(
		function () {
			var $input = $(this);

			if (!$input.is(selWatermarkAble)) {
				return;
			}

			// Watermark already initialized?
			if ($input.data(dataFlag)) {

				// If re-defining text or class, first remove existing watermark, then make changes
				if (hasText || hasClass) {
					$.watermark._hide($input);

					if (hasText) {
						$input.data(dataText, text);
					}

					if (hasClass) {
						$input.data(dataClass, options.className);
					}
				}
			}
			else {

				// Detect and use native browser support, if enabled in options
				if (options.useNative.call(this, $input)) {

					// Placeholder attribute (WebKit)
					// Big thanks to Opera for the wacky test required
					if ((("" + $input.css("-webkit-appearance")).replace("undefined", "") !== "") && (($input.attr("tagName") || "") !== "TEXTAREA")) {

						// className is not set because WebKit doesn't appear to have
						// a separate class name property for placeholders (watermarks).
						if (hasText) {
							$input.attr("placeholder", text);
						}

						// Only set data flag for non-native watermarks (purposely commented-out)
						// $input.data(dataFlag, 1);
						return;
					}
				}

				$input.data(dataText, hasText? text : "");
				$input.data(dataClass, options.className);
				$input.data(dataFlag, 1); // Flag indicates watermark was initialized

				// Special processing for password type
				if (($input.attr("type") || "") === "password") {
					var $wrap = $input.wrap("<span>").parent(),
						$wm = $($wrap.html().replace(/type=["']?password["']?/i, 'type="text"'));

					$wm.data(dataText, $input.data(dataText));
					$wm.data(dataClass, $input.data(dataClass));
					$wm.data(dataFlag, 1);
					$wm.attr("maxLength", text.length);

					$wm.focus(
						function () {
							$.watermark._hide($wm, true);
						}
					).bind("dragenter",
						function () {
							$.watermark._hide($wm);
						}
					).bind("dragend",
						function () {
							window.setTimeout(function () { $wm.blur(); }, 1);
						}
					);
					$input.blur(
						function () {
							$.watermark._show($input);
						}
					).bind("dragleave",
						function () {
							$.watermark._show($input);
						}
					);

					$wm.data(dataPassword, $input);
					$input.data(dataPassword, $wm);
				}
				else {

					$input.focus(
						function () {
							$input.data(dataFocus, 1);
							$.watermark._hide($input, true);
						}
					).blur(
						function () {
							$input.data(dataFocus, 0);
							$.watermark._show($input);
						}
					).bind("dragenter",
						function () {
							$.watermark._hide($input);
						}
					).bind("dragleave",
						function () {
							$.watermark._show($input);
						}
					).bind("dragend",
						function () {
							window.setTimeout(function () { $.watermark._show($input); }, 1);
						}
					).bind("drop",
						// Firefox makes this lovely function necessary because the dropped text
						// is merged with the watermark before the drop event is called.
						function (evt) {
							var dropText = evt.originalEvent.dataTransfer.getData("Text");

							if ($input.val().replace(dropText, "") === $input.data(dataText)) {
								$input.val(dropText);
							}

							$input.focus();
						}
					);
				}

				// In order to reliably clear all watermarks before form submission,
				// we need to replace the form's submit function with our own
				// function.  Otherwise watermarks won't be cleared when the form
				// is submitted programmatically.
				if (this.form) {
					var form = this.form,
						$form = $(form);

					if (!$form.data(dataFormSubmit)) {
						$form.submit($.watermark.hideAll);

						// form.submit exists for all browsers except Google Chrome
						// (see "else" below for explanation)
						if (form.submit) {
							$form.data(dataFormSubmit, form.submit);

							form.submit = (function (f, $f) {
								return function () {
									var nativeSubmit = $f.data(dataFormSubmit);

									$.watermark.hideAll();

									if (nativeSubmit.apply) {
										nativeSubmit.apply(f, Array.prototype.slice.call(arguments));
									}
									else {
										nativeSubmit();
									}
								};
							})(form, $form);
						}
						else {
							$form.data(dataFormSubmit, 1);

							// This strangeness is due to the fact that Google Chrome's
							// form.submit function is not visible to JavaScript (identifies
							// as "undefined").  I had to invent a solution here because hours
							// of Googling (ironically) for an answer did not turn up anything
							// useful.  Within my own form.submit function I delete the form's
							// submit function, and then call the non-existent function --
							// which, in the world of Google Chrome, still exists.
							form.submit = (function (f) {
								return function () {
									$.watermark.hideAll();
									delete f.submit;
									f.submit();
								};
							})(form);
						}
					}
				}
			}

			$.watermark._show($input);
		}
	);
};

// Hijack any functions found in the triggerFns list
if (triggerFns.length) {

	// Wait until DOM is ready before searching
	$(function () {
		var i, name, fn;

		for (i=triggerFns.length-1; i>=0; i--) {
			name = triggerFns[i];
			fn = window[name];

			if (typeof(fn) === "function") {
				window[name] = (function (origFn) {
					return function () {
						$.watermark.hideAll();
						return origFn.apply(null, Array.prototype.slice.call(arguments));
					};
				})(fn);
			}
		}
	});
}

})(jQuery);