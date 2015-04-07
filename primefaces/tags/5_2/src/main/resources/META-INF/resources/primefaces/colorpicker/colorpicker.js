/**
 *
 * Color picker
 * Author: Stefan Petre www.eyecon.ro
 *
 * Dual licensed under the MIT and GPL licenses
 *
 */
(function ($) {
	var ColorPicker = function () {
		var
			ids = {},
			inAction,
			charMin = 65,
			visible,
			tpl = '<div class="ui-colorpicker-container"><div class="ui-colorpicker_color"><div><div></div></div></div><div class="ui-colorpicker_hue"><div></div></div><div class="ui-colorpicker_new_color"></div><div class="ui-colorpicker_hex"><input type="text" maxlength="6" size="6" /></div><div class="ui-colorpicker_rgb_r ui-colorpicker_field"><input type="text" maxlength="3" size="3" /><span></span></div><div class="ui-colorpicker_rgb_g ui-colorpicker_field"><input type="text" maxlength="3" size="3" /><span></span></div><div class="ui-colorpicker_rgb_b ui-colorpicker_field"><input type="text" maxlength="3" size="3" /><span></span></div><div class="ui-colorpicker_hsb_h ui-colorpicker_field"><input type="text" maxlength="3" size="3" /><span></span></div><div class="ui-colorpicker_hsb_s ui-colorpicker_field"><input type="text" maxlength="3" size="3" /><span></span></div><div class="ui-colorpicker_hsb_b ui-colorpicker_field"><input type="text" maxlength="3" size="3" /><span></span></div></div>',
			defaults = {
				eventName: 'click',
				onShow: function () {},
				onBeforeShow: function(){},
				onHide: function () {},
				onChange: function () {},
				onSubmit: function () {},
				color: 'ff0000',
				livePreview: true,
				flat: false
			},
			fillRGBFields = function  (hsb, cal) {
				var rgb = HSBToRGB(hsb);
				$(cal).data('colorpicker').fields
					.eq(1).val(rgb.r).end()
					.eq(2).val(rgb.g).end()
					.eq(3).val(rgb.b).end();
			},
			fillHSBFields = function  (hsb, cal) {
				$(cal).data('colorpicker').fields
					.eq(4).val(hsb.h).end()
					.eq(5).val(hsb.s).end()
					.eq(6).val(hsb.b).end();
			},
			fillHexFields = function (hsb, cal) {
				$(cal).data('colorpicker').fields
					.eq(0).val(HSBToHex(hsb)).end();
			},
			setSelector = function (hsb, cal) {
				$(cal).data('colorpicker').selector.css('backgroundColor', '#' + HSBToHex({h: hsb.h, s: 100, b: 100}));
				$(cal).data('colorpicker').selectorIndic.css({
					left: parseInt(150 * hsb.s/100, 10),
					top: parseInt(150 * (100-hsb.b)/100, 10)
				});
			},
			setHue = function (hsb, cal) {
				$(cal).data('colorpicker').hue.css('top', parseInt(150 - 150 * hsb.h/360, 10));
			},
			setCurrentColor = function (hsb, cal) {
				$(cal).data('colorpicker').currentColor.css('backgroundColor', '#' + HSBToHex(hsb));
			},
			setNewColor = function (hsb, cal) {
				$(cal).data('colorpicker').newColor.css('backgroundColor', '#' + HSBToHex(hsb));
			},
			keyDown = function (ev) {
				var pressedKey = ev.charCode || ev.keyCode || -1;
				if ((pressedKey > charMin && pressedKey <= 90) || pressedKey == 32) {
					return false;
				}
				var cal = $(this).parent().parent();
				if (cal.data('colorpicker').livePreview === true) {
					change.apply(this);
				}
			},
			change = function (ev) {
				var cal = $(this).parent().parent(), col;
				if (this.parentNode.className.indexOf('_hex') > 0) {
					cal.data('colorpicker').color = col = HexToHSB(fixHex(this.value));
				} else if (this.parentNode.className.indexOf('_hsb') > 0) {
					cal.data('colorpicker').color = col = fixHSB({
						h: parseInt(cal.data('colorpicker').fields.eq(4).val(), 10),
						s: parseInt(cal.data('colorpicker').fields.eq(5).val(), 10),
						b: parseInt(cal.data('colorpicker').fields.eq(6).val(), 10)
					});
				} else {
					cal.data('colorpicker').color = col = RGBToHSB(fixRGB({
						r: parseInt(cal.data('colorpicker').fields.eq(1).val(), 10),
						g: parseInt(cal.data('colorpicker').fields.eq(2).val(), 10),
						b: parseInt(cal.data('colorpicker').fields.eq(3).val(), 10)
					}));
				}
				if (ev) {
					fillRGBFields(col, cal.get(0));
					fillHexFields(col, cal.get(0));
					fillHSBFields(col, cal.get(0));
				}
				setSelector(col, cal.get(0));
				setHue(col, cal.get(0));
				setNewColor(col, cal.get(0));
				cal.data('colorpicker').onChange.apply(cal, [col, HSBToHex(col), HSBToRGB(col)]);
			},
			blur = function (ev) {
				var cal = $(this).parent().parent();
				cal.data('colorpicker').fields.parent().removeClass('colorpicker_focus');
			},
			focus = function () {
				charMin = this.parentNode.className.indexOf('_hex') > 0 ? 70 : 65;
				$(this).parent().parent().data('colorpicker').fields.parent().removeClass('colorpicker_focus');
				$(this).parent().addClass('colorpicker_focus');
			},
			downIncrement = function (ev) {
				var field = $(this).parent().find('input').focus();
				var current = {
					el: $(this).parent().addClass('colorpicker_slider'),
					max: this.parentNode.className.indexOf('_hsb_h') > 0 ? 360 : (this.parentNode.className.indexOf('_hsb') > 0 ? 100 : 255),
					y: ev.pageY,
					field: field,
					val: parseInt(field.val(), 10),
					preview: $(this).parent().parent().data('colorpicker').livePreview
				};
				$(document).bind('mouseup', current, upIncrement);
				$(document).bind('mousemove', current, moveIncrement);
			},
			moveIncrement = function (ev) {
				ev.data.field.val(Math.max(0, Math.min(ev.data.max, parseInt(ev.data.val + ev.pageY - ev.data.y, 10))));
				if (ev.data.preview) {
					change.apply(ev.data.field.get(0), [true]);
				}
				return false;
			},
			upIncrement = function (ev) {
				change.apply(ev.data.field.get(0), [true]);
				ev.data.el.removeClass('ui-colorpicker_slider').find('input').focus();
				$(document).unbind('mouseup', upIncrement);
				$(document).unbind('mousemove', moveIncrement);
				return false;
			},
			downHue = function (ev) {
				var current = {
					cal: $(this).parent(),
					y: $(this).offset().top
				};
				current.preview = current.cal.data('colorpicker').livePreview;
				$(document).bind('mouseup', current, upHue);
				$(document).bind('mousemove', current, moveHue);

				ev.data = current;

				moveHue(ev);
			},
			moveHue = function (ev) {
				change.apply(
					ev.data.cal.data('colorpicker')
						.fields
						.eq(4)
						.val(parseInt(360*(150 - Math.max(0,Math.min(150,(ev.pageY - ev.data.y))))/150, 10))
						.get(0),
					[ev.data.preview]
				);
				return false;
			},
			upHue = function (ev) {
				fillRGBFields(ev.data.cal.data('colorpicker').color, ev.data.cal.get(0));
				fillHexFields(ev.data.cal.data('colorpicker').color, ev.data.cal.get(0));
				$(document).unbind('mouseup', upHue);
				$(document).unbind('mousemove', moveHue);
				return false;
			},
			downSelector = function (ev) {
				var current = {
					cal: $(this).parent(),
					pos: $(this).offset()
				};
				current.preview = current.cal.data('colorpicker').livePreview;
				$(document).bind('mouseup', current, upSelector);
				$(document).bind('mousemove', current, moveSelector);

				ev.data = current;

				moveSelector(ev);
			},
			moveSelector = function (ev) {
				change.apply(
					ev.data.cal.data('colorpicker')
						.fields
						.eq(6)
						.val(parseInt(100*(150 - Math.max(0,Math.min(150,(ev.pageY - ev.data.pos.top))))/150, 10))
						.end()
						.eq(5)
						.val(parseInt(100*(Math.max(0,Math.min(150,(ev.pageX - ev.data.pos.left))))/150, 10))
						.get(0),
					[ev.data.preview]
				);
				return false;
			},
			upSelector = function (ev) {
				fillRGBFields(ev.data.cal.data('colorpicker').color, ev.data.cal.get(0));
				fillHexFields(ev.data.cal.data('colorpicker').color, ev.data.cal.get(0));
				$(document).unbind('mouseup', upSelector);
				$(document).unbind('mousemove', moveSelector);
				return false;
			},
			enterSubmit = function (ev) {
				$(this).addClass('ui-colorpicker_focus');
			},
			leaveSubmit = function (ev) {
				$(this).removeClass('ui-colorpicker_focus');
			},
			clickSubmit = function (ev) {
				var cal = $(this).parent();
				var col = cal.data('colorpicker').color;
				cal.data('colorpicker').origColor = col;
				setCurrentColor(col, cal.get(0));
				cal.data('colorpicker').onSubmit(col, HSBToHex(col), HSBToRGB(col), cal.data('colorpicker').el);
			},
			show = function (ev) {
				var cal = $('#' + $(this).data('colorpickerId'));
				cal.data('colorpicker').onBeforeShow.apply(this, [cal.get(0)]);
				var pos = $(this).offset();
				var viewPort = getViewport();
				var top = pos.top + this.offsetHeight;
				var left = pos.left;
				if (top + 176 > viewPort.t + viewPort.h) {
					top -= this.offsetHeight + 176;
				}
				if (left + 356 > viewPort.l + viewPort.w) {
					left -= 356;
				}
				cal.css({left: left + 'px', top: top + 'px'});
				if (cal.data('colorpicker').onShow.apply(this, [cal.get(0)]) != false) {
					cal.show();
				}
				$(document).bind('mousedown', {cal: cal}, hide);
				return false;
			},
			hide = function (ev) {
				if (!isChildOf(ev.data.cal.get(0), ev.target, ev.data.cal.get(0))) {
					if (ev.data.cal.data('colorpicker').onHide.apply(this, [ev.data.cal.get(0)]) != false) {
						ev.data.cal.hide();
					}
					$(document).unbind('mousedown', hide);
				}
			},
			isChildOf = function(parentEl, el, container) {
				if (parentEl == el) {
					return true;
				}
				if (parentEl.contains) {
					return parentEl.contains(el);
				}
				if ( parentEl.compareDocumentPosition ) {
					return !!(parentEl.compareDocumentPosition(el) & 16);
				}
				var prEl = el.parentNode;
				while(prEl && prEl != container) {
					if (prEl == parentEl)
						return true;
					prEl = prEl.parentNode;
				}
				return false;
			},
			getViewport = function () {
				var m = document.compatMode == 'CSS1Compat';
				return {
					l : window.pageXOffset || (m ? document.documentElement.scrollLeft : document.body.scrollLeft),
					t : window.pageYOffset || (m ? document.documentElement.scrollTop : document.body.scrollTop),
					w : window.innerWidth || (m ? document.documentElement.clientWidth : document.body.clientWidth),
					h : window.innerHeight || (m ? document.documentElement.clientHeight : document.body.clientHeight)
				};
			},
			fixHSB = function (hsb) {
				return {
					h: Math.min(360, Math.max(0, hsb.h)),
					s: Math.min(100, Math.max(0, hsb.s)),
					b: Math.min(100, Math.max(0, hsb.b))
				};
			},
			fixRGB = function (rgb) {
				return {
					r: Math.min(255, Math.max(0, rgb.r)),
					g: Math.min(255, Math.max(0, rgb.g)),
					b: Math.min(255, Math.max(0, rgb.b))
				};
			},
			fixHex = function (hex) {
				var len = 6 - hex.length;
				if (len > 0) {
					var o = [];
					for (var i=0; i<len; i++) {
						o.push('0');
					}
					o.push(hex);
					hex = o.join('');
				}
				return hex;
			},
			HexToRGB = function (hex) {
				var hex = parseInt(((hex.indexOf('#') > -1) ? hex.substring(1) : hex), 16);
				return {r: hex >> 16, g: (hex & 0x00FF00) >> 8, b: (hex & 0x0000FF)};
			},
			HexToHSB = function (hex) {
				return RGBToHSB(HexToRGB(hex));
			},
			RGBToHSB = function (rgb) {
				var hsb = {
					h: 0,
					s: 0,
					b: 0
				};
				var min = Math.min(rgb.r, rgb.g, rgb.b);
				var max = Math.max(rgb.r, rgb.g, rgb.b);
				var delta = max - min;
				hsb.b = max;
				if (max != 0) {

				}
				hsb.s = max != 0 ? 255 * delta / max : 0;
				if (hsb.s != 0) {
					if (rgb.r == max) {
						hsb.h = (rgb.g - rgb.b) / delta;
					} else if (rgb.g == max) {
						hsb.h = 2 + (rgb.b - rgb.r) / delta;
					} else {
						hsb.h = 4 + (rgb.r - rgb.g) / delta;
					}
				} else {
					hsb.h = -1;
				}
				hsb.h *= 60;
				if (hsb.h < 0) {
					hsb.h += 360;
				}
				hsb.s *= 100/255;
				hsb.b *= 100/255;
				return hsb;
			},
			HSBToRGB = function (hsb) {
				var rgb = {};
				var h = Math.round(hsb.h);
				var s = Math.round(hsb.s*255/100);
				var v = Math.round(hsb.b*255/100);
				if(s == 0) {
					rgb.r = rgb.g = rgb.b = v;
				} else {
					var t1 = v;
					var t2 = (255-s)*v/255;
					var t3 = (t1-t2)*(h%60)/60;
					if(h==360) h = 0;
					if(h<60) {rgb.r=t1;rgb.b=t2;rgb.g=t2+t3}
					else if(h<120) {rgb.g=t1;rgb.b=t2;rgb.r=t1-t3}
					else if(h<180) {rgb.g=t1;rgb.r=t2;rgb.b=t2+t3}
					else if(h<240) {rgb.b=t1;rgb.r=t2;rgb.g=t1-t3}
					else if(h<300) {rgb.b=t1;rgb.g=t2;rgb.r=t2+t3}
					else if(h<360) {rgb.r=t1;rgb.g=t2;rgb.b=t1-t3}
					else {rgb.r=0;rgb.g=0;rgb.b=0}
				}
				return {r:Math.round(rgb.r), g:Math.round(rgb.g), b:Math.round(rgb.b)};
			},
			RGBToHex = function (rgb) {
				var hex = [
					rgb.r.toString(16),
					rgb.g.toString(16),
					rgb.b.toString(16)
				];
				$.each(hex, function (nr, val) {
					if (val.length == 1) {
						hex[nr] = '0' + val;
					}
				});
				return hex.join('');
			},
			HSBToHex = function (hsb) {
				return RGBToHex(HSBToRGB(hsb));
			},
			restoreOriginal = function () {
				var cal = $(this).parent();
				var col = cal.data('colorpicker').origColor;
				cal.data('colorpicker').color = col;
				fillRGBFields(col, cal.get(0));
				fillHexFields(col, cal.get(0));
				fillHSBFields(col, cal.get(0));
				setSelector(col, cal.get(0));
				setHue(col, cal.get(0));
				setNewColor(col, cal.get(0));
			};
		return {
			init: function (opt) {
				opt = $.extend({}, defaults, opt||{});
				if (typeof opt.color == 'string') {
					opt.color = HexToHSB(opt.color);
				} else if (opt.color.r != undefined && opt.color.g != undefined && opt.color.b != undefined) {
					opt.color = RGBToHSB(opt.color);
				} else if (opt.color.h != undefined && opt.color.s != undefined && opt.color.b != undefined) {
					opt.color = fixHSB(opt.color);
				} else {
					return this;
				}
				return this.each(function () {
					if (!$(this).data('colorpickerId')) {
						var options = $.extend({}, opt);
						options.origColor = opt.color;
						var id = 'collorpicker_' + parseInt(Math.random() * 1000);
						$(this).data('colorpickerId', id);
						var cal = $(tpl).attr('id', id);
						if (options.flat) {
							cal.appendTo(this).show();
						} else {
							cal.appendTo(document.body);
						}
						options.fields = cal
											.find('input')
												.bind('keyup', keyDown)
												.bind('change', change)
												.bind('blur', blur)
												.bind('focus', focus);
						cal
							.find('span').bind('mousedown', downIncrement).end()
							.find('>div.ui-colorpicker_current_color').bind('click', restoreOriginal);
						options.selector = cal.find('div.ui-colorpicker_color').bind('mousedown', downSelector);
						options.selectorIndic = options.selector.find('div div');
						options.el = this;
						options.hue = cal.find('div.ui-colorpicker_hue div');
						cal.find('div.ui-colorpicker_hue').bind('mousedown', downHue);
						options.newColor = cal.find('div.ui-colorpicker_new_color');
						options.currentColor = cal.find('div.ui-colorpicker_current_color');
						cal.data('colorpicker', options);
						cal.find('div.ui-colorpicker_submit')
							.bind('mouseenter', enterSubmit)
							.bind('mouseleave', leaveSubmit)
							.bind('click', clickSubmit);
						fillRGBFields(options.color, cal.get(0));
						fillHSBFields(options.color, cal.get(0));
						fillHexFields(options.color, cal.get(0));
						setHue(options.color, cal.get(0));
						setSelector(options.color, cal.get(0));
						setCurrentColor(options.color, cal.get(0));
						setNewColor(options.color, cal.get(0));
						if (options.flat) {
							cal.css({
								position: 'relative',
								display: 'block'
							});
						} else {
							$(this).bind(options.eventName, show);
						}
					}
				});
			},
			showPicker: function() {
				return this.each( function () {
					if ($(this).data('colorpickerId')) {
						show.apply(this);
					}
				});
			},
			hidePicker: function() {
				return this.each( function () {
					if ($(this).data('colorpickerId')) {
						$('#' + $(this).data('colorpickerId')).hide();
					}
				});
			},
			setColor: function(col) {
				if (typeof col == 'string') {
					col = HexToHSB(col);
				} else if (col.r != undefined && col.g != undefined && col.b != undefined) {
					col = RGBToHSB(col);
				} else if (col.h != undefined && col.s != undefined && col.b != undefined) {
					col = fixHSB(col);
				} else {
					return this;
				}
				return this.each(function(){
					if ($(this).data('colorpickerId')) {
						var cal = $('#' + $(this).data('colorpickerId'));
						cal.data('colorpicker').color = col;
						cal.data('colorpicker').origColor = col;
						fillRGBFields(col, cal.get(0));
						fillHSBFields(col, cal.get(0));
						fillHexFields(col, cal.get(0));
						setHue(col, cal.get(0));
						setSelector(col, cal.get(0));
						setCurrentColor(col, cal.get(0));
						setNewColor(col, cal.get(0));
					}
				});
			}
		};
	}();
	$.fn.extend({
		ColorPicker: ColorPicker.init,
		ColorPickerHide: ColorPicker.hidePicker,
		ColorPickerShow: ColorPicker.showPicker,
		ColorPickerSetColor: ColorPicker.setColor
	});
})(jQuery);

/**
 *
 * Zoomimage
 * Author: Stefan Petre www.eyecon.ro
 *
 */
(function($){
	var EYE = window.EYE = function() {
		var _registered = {
			init: []
		};
		return {
			init: function() {
				$.each(_registered.init, function(nr, fn){
					fn.call();
				});
			},
			extend: function(prop) {
				for (var i in prop) {
					if (prop[i] != undefined) {
						this[i] = prop[i];
					}
				}
			},
			register: function(fn, type) {
				if (!_registered[type]) {
					_registered[type] = [];
				}
				_registered[type].push(fn);
			}
		};
	}();
	$(EYE.init);
})(jQuery);

/**
 *
 * Utilities
 * Author: Stefan Petre www.eyecon.ro
 *
 */
(function($) {
EYE.extend({
	getPosition : function(e, forceIt)
	{
		var x = 0;
		var y = 0;
		var es = e.style;
		var restoreStyles = false;
		if (forceIt && jQuery.curCSS(e,'display') == 'none') {
			var oldVisibility = es.visibility;
			var oldPosition = es.position;
			restoreStyles = true;
			es.visibility = 'hidden';
			es.display = 'block';
			es.position = 'absolute';
		}
		var el = e;
		if (el.getBoundingClientRect) { // IE
			var box = el.getBoundingClientRect();
			x = box.left + Math.max(document.documentElement.scrollLeft, document.body.scrollLeft) - 2;
			y = box.top + Math.max(document.documentElement.scrollTop, document.body.scrollTop) - 2;
		} else {
			x = el.offsetLeft;
			y = el.offsetTop;
			el = el.offsetParent;
			if (e != el) {
				while (el) {
					x += el.offsetLeft;
					y += el.offsetTop;
					el = el.offsetParent;
				}
			}
			if (jQuery.browser.safari && jQuery.curCSS(e, 'position') == 'absolute' ) {
				x -= document.body.offsetLeft;
				y -= document.body.offsetTop;
			}
			el = e.parentNode;
			while (el && el.tagName.toUpperCase() != 'BODY' && el.tagName.toUpperCase() != 'HTML')
			{
				if (jQuery.curCSS(el, 'display') != 'inline') {
					x -= el.scrollLeft;
					y -= el.scrollTop;
				}
				el = el.parentNode;
			}
		}
		if (restoreStyles == true) {
			es.display = 'none';
			es.position = oldPosition;
			es.visibility = oldVisibility;
		}
		return {x:x, y:y};
	},
	getSize : function(e)
	{
		var w = parseInt(jQuery.curCSS(e,'width'), 10);
		var h = parseInt(jQuery.curCSS(e,'height'), 10);
		var wb = 0;
		var hb = 0;
		if (jQuery.curCSS(e, 'display') != 'none') {
			wb = e.offsetWidth;
			hb = e.offsetHeight;
		} else {
			var es = e.style;
			var oldVisibility = es.visibility;
			var oldPosition = es.position;
			es.visibility = 'hidden';
			es.display = 'block';
			es.position = 'absolute';
			wb = e.offsetWidth;
			hb = e.offsetHeight;
			es.display = 'none';
			es.position = oldPosition;
			es.visibility = oldVisibility;
		}
		return {w:w, h:h, wb:wb, hb:hb};
	},
	getClient : function(e)
	{
		var h, w;
		if (e) {
			w = e.clientWidth;
			h = e.clientHeight;
		} else {
			var de = document.documentElement;
			w = window.innerWidth || self.innerWidth || (de&&de.clientWidth) || document.body.clientWidth;
			h = window.innerHeight || self.innerHeight || (de&&de.clientHeight) || document.body.clientHeight;
		}
		return {w:w,h:h};
	},
	getScroll : function (e)
	{
		var t=0, l=0, w=0, h=0, iw=0, ih=0;
		if (e && e.nodeName.toLowerCase() != 'body') {
			t = e.scrollTop;
			l = e.scrollLeft;
			w = e.scrollWidth;
			h = e.scrollHeight;
		} else  {
			if (document.documentElement) {
				t = document.documentElement.scrollTop;
				l = document.documentElement.scrollLeft;
				w = document.documentElement.scrollWidth;
				h = document.documentElement.scrollHeight;
			} else if (document.body) {
				t = document.body.scrollTop;
				l = document.body.scrollLeft;
				w = document.body.scrollWidth;
				h = document.body.scrollHeight;
			}
			if (typeof pageYOffset != 'undefined') {
				t = pageYOffset;
				l = pageXOffset;
			}
			iw = self.innerWidth||document.documentElement.clientWidth||document.body.clientWidth||0;
			ih = self.innerHeight||document.documentElement.clientHeight||document.body.clientHeight||0;
		}
		return {t: t, l: l, w: w, h: h, iw: iw, ih: ih};
	},
	getMargins : function(e, toInteger)
	{
		var t = jQuery.curCSS(e,'marginTop') || '';
		var r = jQuery.curCSS(e,'marginRight') || '';
		var b = jQuery.curCSS(e,'marginBottom') || '';
		var l = jQuery.curCSS(e,'marginLeft') || '';
		if (toInteger)
			return {
				t: parseInt(t, 10)||0,
				r: parseInt(r, 10)||0,
				b: parseInt(b, 10)||0,
				l: parseInt(l, 10)
			};
		else
			return {t: t, r: r,	b: b, l: l};
	},
	getPadding : function(e, toInteger)
	{
		var t = jQuery.curCSS(e,'paddingTop') || '';
		var r = jQuery.curCSS(e,'paddingRight') || '';
		var b = jQuery.curCSS(e,'paddingBottom') || '';
		var l = jQuery.curCSS(e,'paddingLeft') || '';
		if (toInteger)
			return {
				t: parseInt(t, 10)||0,
				r: parseInt(r, 10)||0,
				b: parseInt(b, 10)||0,
				l: parseInt(l, 10)
			};
		else
			return {t: t, r: r,	b: b, l: l};
	},
	getBorder : function(e, toInteger)
	{
		var t = jQuery.curCSS(e,'borderTopWidth') || '';
		var r = jQuery.curCSS(e,'borderRightWidth') || '';
		var b = jQuery.curCSS(e,'borderBottomWidth') || '';
		var l = jQuery.curCSS(e,'borderLeftWidth') || '';
		if (toInteger)
			return {
				t: parseInt(t, 10)||0,
				r: parseInt(r, 10)||0,
				b: parseInt(b, 10)||0,
				l: parseInt(l, 10)||0
			};
		else
			return {t: t, r: r,	b: b, l: l};
	},
	traverseDOM : function(nodeEl, func)
	{
		func(nodeEl);
		nodeEl = nodeEl.firstChild;
		while(nodeEl){
			EYE.traverseDOM(nodeEl, func);
			nodeEl = nodeEl.nextSibling;
		}
	},
	getInnerWidth :  function(el, scroll) {
		var offsetW = el.offsetWidth;
		return scroll ? Math.max(el.scrollWidth,offsetW) - offsetW + el.clientWidth:el.clientWidth;
	},
	getInnerHeight : function(el, scroll) {
		var offsetH = el.offsetHeight;
		return scroll ? Math.max(el.scrollHeight,offsetH) - offsetH + el.clientHeight:el.clientHeight;
	},
	getExtraWidth : function(el) {
		if($.boxModel)
			return (parseInt($.curCSS(el, 'paddingLeft'))||0)
				+ (parseInt($.curCSS(el, 'paddingRight'))||0)
				+ (parseInt($.curCSS(el, 'borderLeftWidth'))||0)
				+ (parseInt($.curCSS(el, 'borderRightWidth'))||0);
		return 0;
	},
	getExtraHeight : function(el) {
		if($.boxModel)
			return (parseInt($.curCSS(el, 'paddingTop'))||0)
				+ (parseInt($.curCSS(el, 'paddingBottom'))||0)
				+ (parseInt($.curCSS(el, 'borderTopWidth'))||0)
				+ (parseInt($.curCSS(el, 'borderBottomWidth'))||0);
		return 0;
	},
	isChildOf: function(parentEl, el, container) {
		if (parentEl == el) {
			return true;
		}
		if (!el || !el.nodeType || el.nodeType != 1) {
			return false;
		}
		if (parentEl.contains && !$.browser.safari) {
			return parentEl.contains(el);
		}
		if ( parentEl.compareDocumentPosition ) {
			return !!(parentEl.compareDocumentPosition(el) & 16);
		}
		var prEl = el.parentNode;
		while(prEl && prEl != container) {
			if (prEl == parentEl)
				return true;
			prEl = prEl.parentNode;
		}
		return false;
	},
	centerEl : function(el, axis)
	{
		var clientScroll = EYE.getScroll();
		var size = EYE.getSize(el);
		if (!axis || axis == 'vertically')
			$(el).css(
				{
					top: clientScroll.t + ((Math.min(clientScroll.h,clientScroll.ih) - size.hb)/2) + 'px'
				}
			);
		if (!axis || axis == 'horizontally')
			$(el).css(
				{
					left: clientScroll.l + ((Math.min(clientScroll.w,clientScroll.iw) - size.wb)/2) + 'px'
				}
			);
	}
});
if (!$.easing.easeout) {
	$.easing.easeout = function(p, n, firstNum, delta, duration) {
		return -delta * ((n=n/duration-1)*n*n*n - 1) + firstNum;
	};
}

})(jQuery);


/**
 * PrimeFaces Color Picker Widget
 */
 PrimeFaces.widget.ColorPicker = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = $(this.jqId + '_input');
        this.cfg.popup = this.cfg.mode == 'popup';    
        this.jqEl = this.cfg.popup ? $(this.jqId + '_button') : $(this.jqId + '_inline');
        this.cfg.flat = !this.cfg.popup;
        this.cfg.livePreview = false;
        this.cfg.nestedInDialog = this.jqEl.parents('.ui-dialog:first').length == 1;

        this.bindCallbacks();

        //ajax update check
        if(this.cfg.popup) {
            this.clearOrphanOverlay();
        }

        //create colorpicker
        this.jqEl.ColorPicker(this.cfg);

        //popup ui
        if(this.cfg.popup) {
            PrimeFaces.skinButton(this.jqEl);
            this.overlay = $(PrimeFaces.escapeClientId(this.jqEl.data('colorpickerId')));
            this.livePreview = $(this.jqId + '_livePreview');
        }
    },
    
    bindCallbacks: function() {
        var _self = this;

        this.cfg.onChange = function(hsb, hex, rgb) {
            _self.input.val(hex);

            if(_self.cfg.popup) {
                _self.livePreview.css('backgroundColor', '#' + hex);
            }
        };

        this.cfg.onShow = function() {
            if(_self.cfg.popup) {
                _self.overlay.css('z-index', ++PrimeFaces.zindex);
            }

            var win = $(window),
            positionOffset = _self.cfg.nestedInDialog ? '-' + win.scrollLeft() + ' -' + win.scrollTop() : null;

            if(_self.cfg.nestedInDialog) {
                _self.overlay.css('position', 'fixed');
            }

            //position the overlay relative to the button
            _self.overlay.css({
                        left:'',
                        top:''
                })
                .position({
                    my: 'left top'
                    ,at: 'left bottom'
                    ,of: _self.jqEl,
                    offset : positionOffset
                });
        }

        this.cfg.onHide = function(cp) {
            _self.overlay.css('z-index', ++PrimeFaces.zindex);
            $(cp).fadeOut('fast');
            return false;
        }
    },
    
    /**
     * When a popup colorpicker is updated with ajax, a new overlay is appended to body and old overlay
     * would be orphan. We need to remove the old overlay to prevent memory leaks.
     */
    clearOrphanOverlay: function() {
        var _self = this;

        $(document.body).children('.ui-colorpicker-container').each(function(i, element) {
            var overlay = $(element),
            options = overlay.data('colorpicker');

            if(options.id == _self.id) {
                overlay.remove();
                return false;   //break;
            }
        });
    }
    
});