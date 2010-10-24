/*!
 * elRTE WYSIWYG HTML-editor
 * Version 1.1 (2010-09-20)
 * http://elrte.org
 *
 * Copyright 2010, Studio 42 Ltd.
 * Licensed under a 3 clauses BSD license
 */

/**
 * @class eli18n
 * Javascript applications localization
 *
 * @param Object o - class options. Object. {textdomain : 'имя_группы_сообщений', messages : {textdomain1 : {}[, textdomain2 : {}]...}}
 *
 * Usage:
 *
 * var msgs = { Hello : 'Превэд', 'Hello %user' : 'Превед %user' };
 * //load messages and set default textdomain
 * var translator = new eli18n( {textdomain : 'test', messages : {test : msgs}} )
 * window.console.log(translator.translate('Hello'));
 * window.console.log(translator.format('Hello %user', {user : 'David Blain'}))
 * // create new textdomain
 * translator.load({test2 : {'Goodbye' : 'Ja, deva mata!'} })
 * // and use it, without changing default one
 * window.console.log(translator.translate('Goodbye', 'test2'));
 *
 * @author:    Dmitry (dio) Levashov dio@std42.ru
 * license:   BSD license
 **/
function eli18n(o) {

	/**
	 * Get/set default textdomain
	 *
	 * @param String d new textdomain name
	 * @return String  default textdomain
	 **/
	this.textdomain = function(d) {
		return this.messages[d] ? this._domain = d : this._domain;
	}

	o && o.messages   && this.load(o.messages);
	o && o.textdomain && this.textdomain(o.textdomain);
}

eli18n.prototype = new function() {

	/**
	 * @var Object messages (key - messages in English or message handler, value - message in selected language)
	 **/
	this.messages = {};
	/**
	 * @var String default textdomain
	 **/
	this._domain   = '';

	/**
	 * Load new messages
	 *
	 * @param Object msgs - messages (key - textdomain name, value - messages Object)
	 * @return Object this
	 **/
	this.load = function(msgs) {
		if (typeof(msgs) == 'object') {
			for (var d in msgs) {
				var _msgs = msgs[d];
				if (typeof(_msgs) == 'object') {
					if (!this.messages[d]) {
						this.messages[d] = {};
					}
					for (var k in _msgs) {
						if (typeof(_msgs[k]) == 'string') {
							this.messages[d][k] = _msgs[k];
						}
					}
				}
			}
		}
		return this;
	}

	/**
	 * Return translated message, if message exists in required or default textdomain, otherwise returns original message
	 *
	 * @param  String msg - message
	 * @param  String d - textdomain. If empty, default textdomain will be used
	 * @return String translated message
	 **/
	this.translate = function(msg, d) {
		var d = d && this.messages[d] ? d : this._domain;
		return this.messages[d] && this.messages[d][msg] ? this.messages[d][msg] : msg;

	}

	/**
	 * Translate message and replace placeholders (%placeholder)
	 *
	 * @param  String  msg - message
	 * @param  Object  replacement for placeholders (keys - placeholders name without leading %, values - replacements)
	 * @param  String  d - textdomain. If empty, default textdomain will be used
	 * @return String  translated message
	 **/
	this.format = function(msg, data, d) {
		msg = this.translate(msg, d);
		if (typeof(data) == 'object') {
			for (var i in data) {
				msg = msg.replace('%'+i, this.translate(data[i], d));
			}
		}
		return msg;
	}
}
/**
 * @class elDialogForm
 * Wraper for jquery.ui.dialog and jquery.ui.tabs
 *  Create form in dialog. You can decorate it as you wish - with tabs or/and tables
 *
 * Usage:
 *   var d = new elDialogForm(opts)
 *   d.append(['Field name: ', $('<input type="text" name="f1" />')])
 *		.separator()
 *		.append(['Another field name: ', $('<input type="text" name="f2" />')])
 *      .open()
 * will create dialog with pair text field separated by horizontal rule
 * Calling append() with 2 additional arguments ( d.append([..], null, true))
 *  - will create table in dialog and put text inputs and labels in table cells
 *
 * Dialog with tabs:
 *   var d = new elDialogForm(opts)
 *   d.tab('first', 'First tab label)
 * 	  .tab('second', 'Second tab label)
 *    .append(['Field name: ', $('<input type="text" name="f1" />')], 'first', true)  - add label and input to first tab in table (table will create automagicaly)
 *    .append(['Field name 2: ', $('<input type="text" name="f2" />')], 'second', true)  - same in secon tab
 *
 * Options:
 *   class     - css class for dialog
 *   submit    - form submit event callback. Accept 2 args - event and this object
 *   ajaxForm  - arguments for ajaxForm, if needed (dont forget include jquery.form.js)
 *   tabs      - arguments for ui.tabs
 *   dialog    - arguments for ui.dialog
 *   name      - hidden text field in wich selected value will saved
 *
 * Notice!
 * When close dialog, it will destroing insead of dialog('close'). Reason - strange bug with tabs in dialog on secondary opening.
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 *
 **/

function elDialogForm(o) {
	var self = this;

	var defaults = {
		'class'   : 'el-dialogform',
		submit    : function(e, d) { window.console && window.console.log && window.console.log('submit called'); d.close(); },
		form      : { action : window.location.href,	method : 'post'	},
		ajaxForm  : null,
		validate  : null,
		spinner   : 'Loading',
		tabs      : { active: 0 },
		tabPrefix : 'el-df-tab-',
		dialog    : {
			title     : 'dialog',
			autoOpen  : false,
			modal     : true,
			resizable : false,
			buttons  : {
				Cancel : function() { self.close(); },
				Ok     : function() { self.form.trigger('submit'); }
			}
		}
	};

	this.opts = jQuery.extend(true, defaults, o, {dialog : { autoOpen : false, close : function() { self.close(); } }});

	if (this.opts.rtl) {
		this.opts['class'] += ' el-dialogform-rtl';
	}

	if (o && o.dialog && o.dialog.buttons && typeof(o.dialog.buttons) == 'object') {
		this.opts.dialog.buttons = o.dialog.buttons;
	}
	this.ul     = null;
	this.tabs   = {};
	this._table = null;
	this.dialog = jQuery('<div />').appendTo(document.body).addClass(this.opts['class']).dialog(this.opts.dialog);
	this.message = jQuery('<div class="el-dialogform-message rounded-5" />').hide().appendTo(this.dialog);
	this.error   = jQuery('<div class="el-dialogform-error rounded-5" />').hide().appendTo(this.dialog);
	this.spinner = jQuery('<div class="spinner" />').hide().appendTo(this.dialog);
	this.content = jQuery('<div class="el-dialogform-content" />').appendTo(this.dialog)
	this.form   = jQuery('<form />').attr(this.opts.form).appendTo(this.content);

	if (this.opts.submit) {
		this.form.bind('submit', function(e) { self.opts.submit(e, self) })
	}
	if (this.opts.ajaxForm && jQuery.fn.ajaxForm) {
		this.form.ajaxForm(this.opts.ajaxForm);
	}
	if (this.opts.validate) {
		this.form.validate(this.opts.validate);
	}

	this.option = function(name, value) {
		return this.dialog.dialog('option', name, value)
	}

	this.showError = function(msg, hideContent) {
		this.hideMessage();
		this.hideSpinner();
		this.error.html(msg).show();
		hideContent && this.content.hide();
		return this;
	}

	this.hideError= function() {
		this.error.text('').hide();
		this.content.show();
		return this;
	}

	this.showSpinner = function(txt) {
		this.error.hide();
		this.message.hide();
		this.content.hide();
		this.spinner.text(txt||this.opts.spinner).show();
		this.option('buttons', {});
		return this;
	}

	this.hideSpinner = function() {
		this.content.show();
		this.spinner.hide();
		return this;
	}

	this.showMessage = function(txt, hideContent) {
		this.hideError();
		this.hideSpinner();
		this.message.html(txt||'').show();
		hideContent && this.content.hide();
		return this;
	}

	this.hideMessage = function() {
		this.message.hide();
		this.content.show();
		return this;
	}

	/**
	 * Create new tab
	 * @param string id    - tab id
	 * @param string title - tab name
	 * @return elDialogForm
	**/
	this.tab = function(id, title) {
		id = this.opts.tabPrefix+id;

		if (!this.ul) {
			this.ul = jQuery('<ul />').prependTo(this.form);
		}
		jQuery('<li />').append(jQuery('<a />').attr('href', '#'+id).html(title)).appendTo(this.ul);
		this.tabs[id] = {tab : jQuery('<div />').attr('id', id).addClass('tab').appendTo(this.form), table : null};
		return this;
	}

	/**
	 * Create new table
	 * @param string id  tab id, if set - table will create in tab, otherwise - in dialog
	 * @return elDialogForm
	**/
	this.table = function(id) {
		id = id && id.indexOf(this.opts.tabPrefix) == -1 ? this.opts.tabPrefix+id : id;
		if (id && this.tabs && this.tabs[id]) {
			this.tabs[id].table = jQuery('<table />').appendTo(this.tabs[id].tab);
		} else {
			this._table = jQuery('<table />').appendTo(this.form);
		}
		return this;
	}

	/**
	 * Append html, dom nodes or jQuery objects to dialog or tab
	 * @param array|object|string  data object(s) to append to dialog
	 * @param string               tid  tab id, if adding to tab
	 * @param bool                 t    if true - data will added in table (creating automagicaly)
	 * @return elDialogForm
	**/
	this.append = function(data, tid, t) {
		tid = tid ? 'el-df-tab-'+tid : '';

		if (!data) {
			return this;
		}

		if (tid && this.tabs[tid]) {
			if (t) {
				!this.tabs[tid].table && this.table(tid);
				var tr = jQuery('<tr />').appendTo(this.tabs[tid].table);
				if (!jQuery.isArray(data)) {
					tr.append(jQuery('<td />').append(data));
				} else {
					for (var i=0; i < data.length; i++) {
						tr.append(jQuery('<td />').append(data[i]));
					};
				}
			} else {
				if (!jQuery.isArray(data)) {
					this.tabs[tid].tab.append(data)
				} else {
					for (var i=0; i < data.length; i++) {
						this.tabs[tid].tab.append(data[i]);
					};
				}
			}

		} else {
			if (!t) {
				if (!jQuery.isArray(data)) {
					this.form.append(data);
				} else {
					for (var i=0; i < data.length; i++) {
						this.form.append(data[i]);
					};
				}
			} else {
				if (!this._table) {
					this.table();
				}
				var tr = jQuery('<tr />').appendTo(this._table);
				if (!jQuery.isArray(data)) {
					tr.append(jQuery('<td />').append(data));
				} else {
					for (var i=0; i < data.length; i++) {
						tr.append(jQuery('<td />').append(data[i]));
					};
				}
			}
		}
		return this;
	}

	/**
	 * Append separator (div class="separator") to dialog or tab
	 * @param  string tid  tab id, if adding to tab
	 * @return elDialogForm
	**/
	this.separator = function(tid) {
		tid = 'el-df-tab-'+tid;
		if (this.tabs && this.tabs[tid]) {
			this.tabs[tid].tab.append(jQuery('<div />').addClass('separator'));
			this.tabs[tid].table && this.table(tid);
		} else {
			this.form.append(jQuery('<div />').addClass('separator'));
		}
		return this;
	}

	/**
	 * Open dialog window
	 * @return elDialogForm
	**/
	this.open = function() {
		this.ul && this.form.tabs(this.opts.tabs);
		this.form.find(':text').keyup(function(e) {
			if (e.keyCode == 13) {
				self.form.submit();
			}
		});

		this.dialog.attr('unselectable', 'on').dialog('open');
		var self = this;
		if (this.form && this.form.find(':text').length) {
			setTimeout(function() { self.form.find(':text')[0].focus(); }, 20);
		}

		return this;
	}

	/**
	 * Close dialog window and destroy content
	 * @return void
	**/
	this.close = function() {
		if (typeof(this.opts.close) == 'function') {
			this.opts.close();
		}
		this.dialog.dialog('destroy').remove();
	}

}

/**
 * elColorPicker. JQuery plugin
 * Create drop-down colors palette.
 *
 * Usage:
 * $(selector).elColorPicker(opts)
 *
 * set color after init:
 * var c = $(selector).elColorPicker(opts)
 * c.val('#ffff99)
 *
 * Get selected color:
 * var color = c.val();
 *
 * Notice!
 *   Palette created only after first click on element (lazzy loading)
 *
 * Options:
 *   colors - colors array (by default display 256 web safe colors)
 *   color  - current (selected) color
 *   class - css class for display "button" (element on wich plugin was called)
 *   paletteClass - css class for colors palette
 *   palettePosition - string indicate where palette will created:
 *      'inner' - palette will attach to element (acceptable in most cases)
 *      'outer' - palette will attach to document.body.
 *                Use, when create color picker inside element with overflow == 'hidden', for example in ui.dialog
 *   update - function wich update button view on select color (by default set selected color as background)
 *   change - callback, called when color was selected (by default write color to console.log)
 *   name   - hidden text field in wich selected color value will saved
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 *
 **/
(function($) {

	$.fn.elColorPicker = function(o) {
		var self     = this;
		var opts     = $.extend({}, $.fn.elColorPicker.defaults, o);
		this.hidden  = $('<input type="hidden" />').attr('name', opts.name).val(opts.color||'').appendTo(this);
		this.palette = null;
		this.preview = null;
		this.input   = null;

		function setColor(c) {
			self.val(c);
			opts.change && opts.change(self.val());
			self.palette.slideUp();
		}

		function init() {
			self.palette  = $('<div />').addClass(opts.paletteClass+' rounded-3');
			for (var i=0; i < opts.colors.length; i++) {
				$('<div />')
					.addClass('color')
					.css('background-color', opts.colors[i])
					.attr({title : opts.colors[i], unselectable : 'on'})
					.appendTo(self.palette)
					.mouseenter(function() {
						var v = $(this).attr('title');
						self.input.val(v);
						self.preview.css('background-color', v);
					})
					.click(function(e) {
						e.stopPropagation();
						setColor($(this).attr('title'));
					});
			};
			self.input = $('<input type="text" />')
				.addClass('rounded-3')
				.attr('size', 8)
				.click(function(e) {
					e.stopPropagation();
				})
				.keydown(function(e) {
					if (e.ctrlKey || e.metaKey) {
						return true;
					}
					var k = e.keyCode;
					// on esc - close palette
					if (k == 27) {
						return self.mouseleave();
					}
					// allow input only hex color value
					if (k!=8 && k != 13 && k!=46 && k!=37 && k != 39 && (k<48 || k>57) && (k<65 || k > 70)) {
						return false;
					}
					var c = $(this).val();
					if (c.length == 7 || c.length == 0) {
						if (k == 13) {
							e.stopPropagation();
							e.preventDefault();
							setColor(c);
							self.palette.slideUp();
						}
						if (e.keyCode != 8 && e.keyCode != 46 && k!=37 && k != 39) {
							return false;
						}
					}
				})
				.keyup(function(e) {
					var c = $(this).val();
					c.length == 7 && /^#[0-9abcdef]{6}$/i.test(c) && self.val(c);
				});

			self.preview = $('<div />')
				.addClass('preview rounded-3')
				.click(function(e) {
					e.stopPropagation();
					setColor(self.input.val());
				});

			self.palette
				.append($('<div />').addClass('clearfix'))
				.append($('<div />').addClass('panel').append(self.input).append(self.preview));

			if (opts.palettePosition == 'outer') {
				self.palette.hide()
					.appendTo(self.parents('body').eq(0))
					.mouseleave(function() {
						$(this).slideUp();
						self.val(self.val());
					})
				self.mouseleave(function(e) {
					if (e.relatedTarget != self.palette.get(0)) {
						self.palette.slideUp();
						self.val(self.val());
					}
				})
			} else {
				self.append(self.palette.hide())
					.mouseleave(function(e) {
						self.palette.slideUp();
						self.val(self.val());
					});
			}
			self.val(self.val());
		}

		this.empty().addClass(opts['class']+' rounded-3')
			.css({'position' : 'relative', 'background-color' : opts.color||''})
		.click(function(e) {
			if (!self.hasClass('disabled')) {
				!self.palette && init();
				if (opts.palettePosition == 'outer' && self.palette.css('display') == 'none') {
					var o = $(this).offset();
					var w = self.palette.width();
					var l = self.parents('body').width() - o.left >= w ? o.left : o.left + $(this).outerWidth() - w;
					self.palette.css({left : l+'px', top : o.top+$(this).height()+1+'px'});
				}
				self.palette.slideToggle();
			}
		});

		this.val = function(v) {
			if (!v && v!=='') {
				return this.hidden.val();
			} else {
				this.hidden.val(v);
				if (opts.update) {
					opts.update(this.hidden.val());
				} else {
					this.css('background-color', v);
				}

				if (self.palette) {
					self.preview.css('background-color', v);
					self.input.val(v);
				}
			}
			return this;
		}

		return this;
	}

	$.fn.elColorPicker.defaults = {
		'class'         : 'el-colorpicker',
		paletteClass    : 'el-palette',
		palettePosition : 'inner',
		name            : 'color',
		color           : '',
		update          : null,
		change          : function(c) {  },
		colors          : [
			'#ffffff', '#cccccc', '#999999', '#666666', '#333333', '#000000',
			'#ffcccc', '#cc9999', '#996666', '#663333', '#330000',
			'#ff9999', '#cc6666', '#cc3333', '#993333', '#660000',
			'#ff6666', '#ff3333', '#ff0000', '#cc0000', '#990000',
			'#ff9966', '#ff6633', '#ff3300', '#cc3300', '#993300',
			'#ffcc99', '#cc9966', '#cc6633', '#996633', '#663300',
			'#ff9933', '#ff6600', '#ff9900', '#cc6600', '#cc9933',
			'#ffcc66', '#ffcc33', '#ffcc00', '#cc9900', '#996600',
			'#ffffcc', '#cccc99', '#999966', '#666633', '#333300',
			'#ffff99', '#cccc66', '#cccc33', '#999933', '#666600',
			'#ffff66', '#ffff33', '#ffff00', '#cccc00', '#999900',
			'#ccff66', '#ccff33', '#ccff00', '#99cc00', '#669900',
			'#ccff99', '#99cc66', '#99cc33', '#669933', '#336600',
			'#99ff33', '#99ff00', '#66ff00', '#66cc00', '#66cc33',
			'#99ff66', '#66ff33', '#33ff00', '#33cc00', '#339900',
			'#ccffcc', '#99cc99', '#669966', '#336633', '#003300',
			'#99ff99', '#66cc66', '#33cc33', '#339933', '#006600',
			'#66ff66', '#33ff33', '#00ff00', '#00cc00', '#009900',
			'#66ff99', '#33ff66', '#00ff33', '#00cc33', '#009933',
			'#99ffcc', '#66cc99', '#33cc66', '#339966', '#006633',
			'#33ff99', '#00ff66', '#00ff99', '#00cc66', '#33cc99',
			'#66ffcc', '#33ffcc', '#00ffcc', '#00cc99', '#009966',
			'#ccffff', '#99cccc', '#669999', '#336666', '#003333',
			'#99ffff', '#66cccc', '#33cccc', '#339999', '#006666',
			'#66cccc', '#33ffff', '#00ffff', '#00cccc', '#009999',
			'#66ccff', '#33ccff', '#00ccff', '#0099cc', '#006699',
			'#99ccff', '#6699cc', '#3399cc', '#336699', '#003366',
			'#3399ff', '#0099ff', '#0066ff', '#066ccc', '#3366cc',
			'#6699ff', '#3366ff', '#0033ff', '#0033cc', '#003399',
			'#ccccff', '#9999cc', '#666699', '#333366', '#000033',
			'#9999ff', '#6666cc', '#3333cc', '#333399', '#000066',
			'#6666ff', '#3333ff', '#0000ff', '#0000cc', '#009999',
			'#9966ff', '#6633ff', '#3300ff', '#3300cc', '#330099',
			'#cc99ff', '#9966cc', '#6633cc', '#663399', '#330066',
			'#9933ff', '#6600ff', '#9900ff', '#6600cc', '#9933cc',
			'#cc66ff', '#cc33ff', '#cc00ff', '#9900cc', '#660099',
			'#ffccff', '#cc99cc', '#996699', '#663366', '#330033',
			'#ff99ff', '#cc66cc', '#cc33cc', '#993399', '#660066',
			'#ff66ff', '#ff33ff', '#ff00ff', '#cc00cc', '#990099',
			'#ff66cc', '#ff33cc', '#ff00cc', '#cc0099', '#990066',
			'#ff99cc', '#cc6699', '#cc3399', '#993366', '#660033',
			'#ff3399', '#ff0099', '#ff0066', '#cc0066', '#cc3366',
			'#ff6699', '#ff3366', '#ff0033', '#cc0033', '#990033'
			]
	};

})(jQuery);
/**
 * jQuery plugin. Create group of text input, elSelect and elColorPicker.
 * Allow input border-width, border-style and border-color. Used in elRTE
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 **/
(function($) {

	$.fn.elBorderSelect = function(o) {

		var $self = this;
		var self  = this.eq(0);
		var opts  = $.extend({}, $.fn.elBorderSelect.defaults, o);
		var width = $('<input type="text" />')
			.attr({'name' : opts.name+'[width]', size : 3}).css('text-align', 'right')
			.change(function() { $self.change(); });

		var color = $('<div />').css('position', 'relative')
			.elColorPicker({
				'class'         : 'el-colorpicker ui-icon ui-icon-pencil',
				name            : opts.name+'[color]',
				palettePosition : 'outer',
				change          : function() { $self.change(); }
			});


		var style = $('<div />').elSelect({
			tpl       : '<div style="border-bottom:4px %val #000;width:100%;margin:7px 0"> </div>',
			tpls      : { '' : '%label'},
			maxHeight : opts.styleHeight || null,
			select    : function() { $self.change(); },
			src       : {
				''       : 'none',
				solid    : 'solid',
				dashed   : 'dashed',
				dotted   : 'dotted',
				'double' : 'double',
				groove   : 'groove',
				ridge    : 'ridge',
				inset    : 'inset',
				outset   : 'outset'
			}
		});

		self.empty()
			.addClass(opts['class'])
			.attr('name', opts.name||'')
			.append(
				$('<table />').attr('cellspacing', 0).append(
					$('<tr />')
						.append($('<td />').append(width).append(' px'))
						.append($('<td />').append(style))
						.append($('<td />').append(color))
				)
			);

		function rgb2hex(str) {
		    function hex(x)  {
		    	hexDigits = ["0", "1", "2", "3", "4", "5", "6", "7", "8","9", "a", "b", "c", "d", "e", "f"];
		        return !x  ? "00" : hexDigits[(x - x % 16) / 16] + hexDigits[x% 16];
		    }
			var rgb = (str||'').match(/\(([0-9]{1,3}),\s*([0-9]{1,3}),\s*([0-9]{1,3})\)/);
			return rgb ? "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]) : '';
		}

		function toPixels(num) {
			if (!num) {
				return num;
			}
			var m = num.match(/([0-9]+\.?[0-9]*)\s*(px|pt|em|%)/);
			if (m) {
				num  = m[1];
				unit = m[2];
			}
			if (num[0] == '.') {
				num = '0'+num;
			}
			num = parseFloat(num);

			if (isNaN(num)) {
				return '';
			}
			var base = parseInt($(document.body).css('font-size')) || 16;
			switch (unit) {
				case 'em': return parseInt(num*base);
				case 'pt': return parseInt(num*base/12);
				case '%' : return parseInt(num*base/100);
			}
			return num;
		}

		this.change = function() {
			opts.change && opts.change(this.val());
		}

		this.val = function(v) {
			if (!v && v !== '') {
				var w = parseInt(width.val());
				return {width : !isNaN(w) ? w+'px' : '', style : style.val(), color : color.val()};
			} else {
				var m, w, s, c, b = '';
				if (v.nodeName || v.css) {
					if (!v.css) {
						v = $(v);
					}
					var b = v.css('border')
					if ((b = v.css('border'))) {
						w = s = c = b;
					} else {
						w = v.css('border-width');
						s = v.css('border-style');
						c = v.css('border-color');
					}

				} else {
					w = v.width||'';
					s = v.style||'';
					c = v.color||'';
				}

				width.val(toPixels(w));
				var m = s ? s.match(/(solid|dashed|dotted|double|groove|ridge|inset|outset)/i) :'';
				style.val(m ? m[1] : '');
				color.val(rgb2hex(c));
				return this;
			}
		}

		this.val(opts.value);
		return this;
	}

	$.fn.elBorderSelect.defaults = {
		name      : 'el-borderselect',
		'class'   : 'el-borderselect',
		value     : {},
		change    : null
	}

})(jQuery);
/**
 * jQuery plugin. Create group of text input fields and selects for setting padding/margin. Used in elRTE
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 **/
(function($) {

	$.fn.elPaddingInput = function(o) {
		var self = this;
		var opts = $.extend({}, $.fn.elPaddingInput.defaults, {name : this.attr('name')}, o);
		this.regexps = {
			main   : new RegExp(opts.type == 'padding' ? 'padding\s*:\s*([^;"]+)'        : 'margin\s*:\s*([^;"]+)',       'im'),
			left   : new RegExp(opts.type == 'padding' ? 'padding-left\s*:\s*([^;"]+)'   : 'margin-left\s*:\s*([^;"]+)',  'im'),
			top    : new RegExp(opts.type == 'padding' ? 'padding-top\s*:\s*([^;"]+)'    : 'margin-top\s*:\s*([^;"]+)',    'im'),
			right  : new RegExp(opts.type == 'padding' ? 'padding-right\s*:\s*([^;"]+)'  : 'margin-right\s*:\s*([^;"]+)',  'im'),
			bottom : new RegExp(opts.type == 'padding' ? 'padding-bottom\s*:\s*([^;"]+)' : 'margin-bottom\s*:\s*([^;"]+)', 'im')
		};

		$.each(['left', 'top', 'right', 'bottom'], function() {

			self[this] = $('<input type="text" />')
				.attr('size', 3)
				.css('text-align', 'right')
				.css('border-'+this, '2px solid red')
				.bind('change', function() { $(this).val(parseNum($(this).val())); change(); })
				.attr('name', opts.name+'['+this+']');
		});
		$.each(['uleft', 'utop', 'uright', 'ubottom'], function() {
			self[this] = $('<select />')
				.append('<option value="px">px</option>')
				.append('<option value="em">em</option>')
				.append('<option value="pt">pt</option>')
				.bind('change', function() { change(); })
				.attr('name', opts.name+'['+this+']');
			if (opts.percents) {
				self[this].append('<option value="%">%</option>');
			}
		});

		this.empty().addClass(opts['class'])
			.append(this.left).append(this.uleft).append(' x ')
			.append(this.top).append(this.utop).append(' x ')
			.append(this.right).append(this.uright).append(' x ')
			.append(this.bottom).append(this.ubottom);

		this.val = function(v) {
			if (!v && v!=='') {
				var l = parseNum(this.left.val());
				var t = parseNum(this.top.val());
				var r = parseNum(this.right.val());
				var b = parseNum(this.bottom.val());
				var ret = {
					left   : l=='auto' || l==0 ? l : (l!=='' ? l+this.uleft.val()   : ''),
					top    : t=='auto' || t==0 ? t : (t!=='' ? t+this.utop.val()    : ''),
					right  : r=='auto' || r==0 ? r : (r!=='' ? r+this.uright.val()  : ''),
					bottom : b=='auto' || b==0 ? b : (b!=='' ? b+this.ubottom.val() : ''),
					css    : ''
				};
				if (ret.left!=='' && ret.right!=='' && ret.top!=='' && ret.bottom!=='') {
					if (ret.left == ret.right && ret.top == ret.bottom) {
						ret.css = ret.top+' '+ret.left;
					} else{
						ret.css = ret.top+' '+ret.right+' '+ret.bottom+' '+ret.left;
					}
				}

				return ret;
			} else {

				if (v.nodeName || v.css) {
					if (!v.css) {
						v = $(v);
					}
					var val   = {left : '', top : '', right: '', bottom : ''};
					var style = (v.attr('style')||'').toLowerCase();

					if (style) {
						style   = $.trim(style);
						var m = style.match(this.regexps.main);
						if (m) {
							var tmp    = $.trim(m[1]).replace(/\s+/g, ' ').split(' ', 4);
							val.top    = tmp[0];
							val.right  = tmp[1] && tmp[1]!=='' ? tmp[1] : val.top;
							val.bottom = tmp[2] && tmp[2]!=='' ? tmp[2] : val.top;
							val.left   = tmp[3] && tmp[3]!=='' ? tmp[3] : val.right;
						} else {
							$.each(['left', 'top', 'right', 'bottom'], function() {
								var name = this.toString();
								m = style.match(self.regexps[name]);
								if (m) {
									val[name] = m[1];
								}
							});
						}
					}
					var v = val;
				}

				$.each(['left', 'top', 'right', 'bottom'], function() {
					var name = this.toString();
					if (typeof(v[name]) != 'undefined' && v[name] !== null) {
						v[name] = v[name].toString();
						var _v = parseNum(v[name]);
						self[name].val(_v);
						var m = v[name].match(/(px|em|pt|%)/i);
						self['u'+name].val(m ? m[1] : 'px');
					}
				});
				return this;
			}
		}

		function parseNum(num) {
			num = $.trim(num.toString());
			if (num[0] == '.') {
				num = '0'+num;
			}
			n = parseFloat(num);
			return !isNaN(n) ? n : (num == 'auto' ? num : '');
		}

		function change() {
			opts.change && opts.change(self);
		}

		this.val(opts.value);

		return this;
	}

	$.fn.elPaddingInput.defaults = {
		name     : 'el-paddinginput',
		'class'  : 'el-paddinginput',
		type     : 'padding',
		value    : {},
		percents : true,
		change   : null
	}

})(jQuery);
/**
 * elSelect JQuery plugin
 * Replacement for select input
 * Allow to put any html and css decoration in drop-down list
 *
 * Usage:
 *   $(selector).elSelect(opts)
 *
 * set value after init:
 *   var c = $(selector).elSelect(opts)
 *   c.val('some value')
 *
 * Get selected value:
 *   var val = c.val();
 *
 * Notice!
 *   1. When called on multiply elements, elSelect create drop-down list only for fist element
 *   2. Elements list created only after first click on element (lazzy loading)
 *
 * Options:
 *   src       - object with pairs value:label to create drop-down list
 *   value     - current (selected) value
 *   class     - css class for display "button" (element on wich plugin was called)
 *   listClass - css class for drop down elements list
 *   select    - callback, called when value was selected (by default write value to console.log)
 *   name      - hidden text field in wich selected value will saved
 *   maxHeight - elements list max height (if height greater - scroll will appear)
 *   tpl       - template for element in list (contains 2 vars: %var - for src key, %label - for src[val] )
 *   labelTpl  - template for label (current selected element) (contains 2 placeholders: %var - for src key, %label - for src[val] )
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 **/
(function($) {

	$.fn.elSelect = function(o) {
		var $self    = this;
		var self     = this.eq(0);
		var opts     = $.extend({}, $.fn.elSelect.defaults, o);
		var hidden   = $('<input type="hidden" />').attr('name', opts.name);
		var label    = $('<label />').attr({unselectable : 'on'}).addClass('rounded-left-3');
		var list     = null;
		var ieWidth  = null;

		if (self.get(0).nodeName == 'SELECT') {
			opts.src = {};
			self.children('option').each(function() {
				opts.src[$(this).val()] = $(this).text();
			});
			opts.value = self.val();
			opts.name  = self.attr('name');
			self.replaceWith((self = $('<div />')));
		}

		if (!opts.value || !opts.src[opts.val]) {
			opts.value = null;
			var i = 0;
			for (var v in opts.src) {
				if (i++ == 0) {
					opts.value = v;
				}
			}
		}

		this.val = function(v) {
			if (!v && v!=='') {
				return hidden.val();
			} else {
				if (opts.src[v]) {
					hidden.val(v);
					updateLabel(v);
					if (list) {
						list.children().each(function() {
							if ($(this).attr('name') == v) {
								$(this).addClass('active');
							} else {
								$(this).removeClass('active');
							}
						});
					}
				}
				return this;
			}
		}

		// update label content
		function updateLabel(v) {
			var tpl = opts.labelTpl || opts.tpls[v] || opts.tpl;
			label.html(tpl.replace(/%val/g, v).replace(/%label/, opts.src[v])).children().attr({unselectable : 'on'});
		}

		// init "select"
		self.empty()
			.addClass(opts['class']+' rounded-3')
			.attr({unselectable : 'on'})
			.append(hidden)
			.append(label)
			.hover(
				function() { $(this).addClass('hover') },
				function() { $(this).removeClass('hover') }
			)
			.click(function(e) {
				!list && init();
				list.slideToggle();
				// stupid ie inherit width from parent
				if ($.browser.msie && !ieWidth) {
					list.children().each(function() {
						ieWidth = Math.max(ieWidth, $(this).width());
					});
					if (ieWidth > list.width()) {
						list.width(ieWidth+40);
					}
				}
			});

		this.val(opts.value);

		// create drop-down list
		function init() {
			// not ul because of ie is stupid with mouseleave in it :(
			list = $('<div />')
				.addClass(opts.listClass+' rounded-3')
				.hide()
				.appendTo(self.mouseleave(function(e) { list.slideUp(); }));

			for (var v in opts.src) {
				var tpl = opts.tpls[v] || opts.tpl;
				$('<div />')
					.attr('name', v)
					.append( $(tpl.replace(/%val/g, v).replace(/%label/g, opts.src[v])).attr({unselectable : 'on'}) )
					.appendTo(list)
					.hover(
						function() { $(this).addClass('hover') },
						function() { $(this).removeClass('hover') }
					)
					.click(function(e) {
						e.stopPropagation();
						e.preventDefault();

						var v = $(this).attr('name');
						$self.val(v);
						opts.select(v);
						list.slideUp();
					});
			};

			var w = self.outerWidth();
			if (list.width() < w) {
				list.width(w);
			}

			var h = list.height();
			if (opts.maxHeight>0 && h>opts.maxHeight) {
				list.height(opts.maxHeight);
			}

			$self.val(hidden.val());
		}

		return this;
	}

	$.fn.elSelect.defaults = {
		name      : 'el-select',
		'class'   : 'el-select',
		listClass : 'list',
		labelTpl  : null,
		tpl       : '<%val>%label</%val>',
		tpls      : {},
		value     : null,
		src       : {},
		select    : function(v) {  window.console &&  window.console.log && window.console.log('selected: '+v); },
		maxHeight : 410
	}

})(jQuery);
/*
 * elRTE - WSWING editor for web
 *
 * Usage:
 * var opts = {
 *	.... // see elRTE.options.js
 * }
 * var editor = new elRTE($('#my-id').get(0), opts)
 * or
 * $('#my-id').elrte(opts)
 *
 * $('#my-id) may be textarea or any DOM Element with text
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 */
(function($) {

elRTE = function(target, opts) {
	if (!target || !target.nodeName) {
		return alert('elRTE: argument "target" is not DOM Element');
	}
	var self     = this, html;
	this.version = '1.1';
	this.build   = '2010-09-20';
	this.options = $.extend(true, {}, this.options, opts);
	this.browser = $.browser;
	this.target  = $(target);

	this.lang      = (''+this.options.lang).toLowerCase();
	this._i18n     = new eli18n({textdomain : 'rte', messages : { rte : this.i18Messages[this.lang] || {}} });
	this.rtl = !!(/^(ar|fa|he)$/.test(this.lang) && this.i18Messages[this.lang]);

	if (this.rtl) {
		this.options.cssClass += ' el-rte-rtl';
	}
	this.toolbar   = $('<div class="toolbar"/>');
	this.iframe    = document.createElement('iframe');
	// this.source    = $('<textarea />').hide();
	this.workzone  = $('<div class="workzone"/>').append(this.iframe).append(this.source);
	this.statusbar = $('<div class="statusbar"/>');
	this.tabsbar   = $('<div class="tabsbar"/>');
	this.editor    = $('<div class="'+this.options.cssClass+'" />').append(this.toolbar).append(this.workzone).append(this.statusbar).append(this.tabsbar);

	this.doc     = null;
	this.$doc    = null;
	this.window  = null;

	this.utils  = new this.utils(this);
	this.dom    = new this.dom(this);
	this.filter = new this.filter(this)

	/**
	 * Sync iframes/textareas height with workzone height
	 *
	 * @return void
	 */
	this.updateHeight = function() {
		self.workzone.add(self.iframe).add(self.source).height(self.workzone.height());
	}

	/**
	 * Turn editor resizable on/off if allowed
	 *
	 * @param  Boolean
	 * @return void
	 **/
	this.resizable = function(r) {
		var self = this;
		if (this.options.resizable && $.fn.resizable) {
			if (r) {
				this.editor.resizable({handles : 'se', alsoResize : this.workzone, minWidth :300, minHeight : 200 }).bind('resize', self.updateHeight);
			} else {
				this.editor.resizable('destroy').unbind('resize', self.updateHeight);
			}
		}
	}

	/* attach editor to document */
	this.editor.insertAfter(target);
	/* init editor textarea */
	var content = '';
	if (target.nodeName == 'TEXTAREA') {
		this.source = this.target;
		this.source.insertAfter(this.iframe).hide();
		content = this.target.val();
	} else {
		this.source = $('<textarea />').insertAfter(this.iframe).hide();
		content = this.target.hide().html();
	}
	this.source.attr('name', this.target.attr('name')||this.target.attr('id'));
	content = $.trim(content);
	if (!content) {
		content = ' ';
	}

	/* add tabs */
	if (this.options.allowSource) {
		this.tabsbar.append('<div class="tab editor rounded-bottom-7 active">'+self.i18n('Editor')+'</div><div class="tab source rounded-bottom-7">'+self.i18n('Source')+'</div><div class="clearfix" style="clear:both"/>')
			.children('.tab').click(function(e) {
				if (!$(this).hasClass('active')) {
					self.tabsbar.children('.tab').toggleClass('active');
					self.workzone.children().toggle();

					if ($(this).hasClass('editor')) {
						self.updateEditor();
						self.window.focus();
						self.ui.update(true);
					} else {
						self.updateSource();
						self.source.focus();
						if ($.browser.msie) {
							// @todo
						} else {
							self.source[0].setSelectionRange(0, 0);
						}
						self.ui.disable();
						self.statusbar.empty();

					}
				}

			});
	}

	this.window = this.iframe.contentWindow;
	this.doc    = this.iframe.contentWindow.document;
	this.$doc   = $(this.doc);

	/* put content into iframe */
	html = '<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />';
	$.each(self.options.cssfiles, function() {
		html += '<link rel="stylesheet" type="text/css" href="'+this+'" />';
	});
	this.doc.open();
	var s = this.filter.wysiwyg(content),
		cl = this.rtl ? ' class="el-rte-rtl"' : '';
	this.doc.write(self.options.doctype+html+'</head><body'+cl+'>'+(s)+'</body></html>');
	this.doc.close();

	/* make iframe editable */
	if ($.browser.msie) {
		this.doc.body.contentEditable = true;
	} else {
		try { this.doc.designMode = "on"; }
		catch(e) { }
		this.doc.execCommand('styleWithCSS', false, this.options.styleWithCSS);
	}

	if (this.options.height>0) {
		this.workzone.height(this.options.height);

	}
	this.updateHeight();
	this.resizable(true);
	this.window.focus();

	this.history = new this.history(this);

	/* init selection object */
	this.selection = new this.selection(this);
	/* init buttons */
	this.ui = new this.ui(this);


	/* bind updateSource to parent form submit */
	this.target.parents('form').bind('submit', function(e) {
		self.source.parents('form').find('[name="el-select"]').remove()
		self.beforeSave();
	});

	// on tab press - insert \t and prevent move focus
	this.source.bind('keydown', function(e) {
		if (e.keyCode == 9) {
			e.preventDefault();

			if ($.browser.msie) {
				var r = document.selection.createRange();
				r.text = "\t"+r.text;
				this.focus();
			} else {
				var before = this.value.substr(0, this.selectionStart),
					after = this.value.substr(this.selectionEnd);
				this.value = before+"\t"+after;
				this.setSelectionRange(before.length+1, before.length+1);
			}
		}
	});

	/* update buttons on click and keyup */
	this.$doc.bind('mouseup', function() {
		self.ui.update();
	})
	.bind('dragstart', function(e) {
		e.preventDefault();
		e.stopPropagation();
	})
	.bind('keyup', function(e) {
		if ((e.keyCode >= 8 && e.keyCode <= 13) || (e.keyCode>=32 && e.keyCode<= 40) || e.keyCode == 46 || (e.keyCode >=96 && e.keyCode <= 111)) {
			// self.log('keyup '+e.keyCode)
			self.ui.update();
		}
	})
	.bind('keydown', function(e) {
		if ((e.metaKey || e.ctrlKey) && e.keyCode == 65) {
			self.ui.update();
		} else if (e.keyCode == 13) {
			var n = self.selection.getNode();
			// self.log(n)
			if (self.dom.selfOrParent(n, /^PRE$/)) {
				self.selection.insertNode(self.doc.createTextNode("\r\n"));
				return false;
			} else if ($.browser.safari && e.shiftKey) {
				self.selection.insertNode(self.doc.createElement('br'))
				return false;
			}
		}
	})

	this.typing = false;
	this.lastKey = null;

	this.$doc.bind('keydown', function(e) {
		if ((e.keyCode>=48 && e.keyCode <=57) || e.keyCode==61 || e.keyCode == 109 || (e.keyCode>=65 && e.keyCode<=90) || e.keyCode==188 ||e.keyCode==190 || e.keyCode==191 || (e.keyCode>=219 && e.keyCode<=222)) {
			if (!self.typing) {
				self.history.add(true);
			}
			self.typing = true;
			self.lastKey = null;
		} else if (e.keyCode == 8 || e.keyCode == 46 || e.keyCode == 32 || e.keyCode == 13) {
			if (e.keyCode != self.lastKey) {
				self.history.add(true);
			}
			self.lastKey = e.keyCode;
			self.typing = false;
		}

	})
	.bind('mouseup', function() {
		self.typing = false;
		self.lastKey = null;
	})
	.bind('paste', function(e) {
		if (!self.options.allowPaste) {
			// paste denied
			e.stopPropagation();
			e.preventDefault();
		} else {
			var n = $(self.dom.create('div'))[0],
				r = self.doc.createTextNode('_');
			self.history.add(true);
			self.typing = true;
			self.lastKey = null;
			n.appendChild(r);
			self.selection.deleteContents().insertNode(n);
			self.selection.select(r);
			setTimeout(function() {
				if (n.parentNode) {
					// clean sandbox content
					$(n).html(self.filter.proccess('paste', $(n).html()));
					r = n.lastChild;
					self.dom.unwrap(n);
					if (r) {
						self.selection.select(r);
						self.selection.collapse(false);
					}
				} else {
					// smth wrong - clean all doc
					n.parentNode && n.parentNode.removeChild(n);
					self.val(self.filter.proccess('paste', self.filter.wysiwyg2wysiwyg($(self.doc.body).html())));
					self.selection.select(self.doc.body.firstChild);
					self.selection.collapse(true);
				}
				$(self.doc.body).mouseup(); // to activate history buutons
			}, 15);
		}
	});

	if ($.browser.msie) {
		this.$doc.bind('keyup', function(e) {
			if (e.keyCode == 86 && (e.metaKey||e.ctrlKey)) {
				self.history.add(true);
				self.typing = true;
				self.lastKey = null;
				self.selection.saveIERange();
				self.val(self.filter.proccess('paste', self.filter.wysiwyg2wysiwyg($(self.doc.body).html())));
				self.selection.restoreIERange();
				$(self.doc.body).mouseup();
				this.ui.update();
			}
		});
	}

	if ($.browser.safari) {
		this.$doc.bind('click', function(e) {
			$(self.doc.body).find('.elrte-webkit-hl').removeClass('elrte-webkit-hl');
			if (e.target.nodeName == 'IMG') {
				$(e.target).addClass('elrte-webkit-hl');
			}
		}).bind('keyup', function(e) {
			$(self.doc.body).find('.elrte-webkit-hl').removeClass('elrte-webkit-hl');
		})
	}

	// this.resizable(true)
	this.window.focus();
	// this.log(this.editor.parents('form').find('[name="el-select"]'))


}

/**
 * Return message translated to selected language
 *
 * @param  string  msg  message text in english
 * @return string
 **/
elRTE.prototype.i18n = function(msg) {
	return this._i18n.translate(msg);
}



/**
 * Display editor
 *
 * @return void
 **/
elRTE.prototype.open = function() {
	this.editor.show();
}

/**
 * Hide editor and display elements on wich editor was created
 *
 * @return void
 **/
elRTE.prototype.close = function() {
	this.editor.hide();
}

elRTE.prototype.updateEditor = function() {
	this.val(this.source.val());
}

elRTE.prototype.updateSource = function() {
	this.source.val(this.filter.source($(this.doc.body).html()));
}

/**
 * Return edited text
 *
 * @return String
 **/
elRTE.prototype.val = function(v) {
	if (typeof(v) == 'string') {
		v = ''+v;
		if (this.source.is(':visible')) {
			this.source.val(this.filter.source2source(v));
		} else {
			if ($.browser.msie) {
				this.doc.body.innerHTML = '<br />'+this.filter.wysiwyg(v);
				this.doc.body.removeChild(this.doc.body.firstChild);
			} else {
				this.doc.body.innerHTML = this.filter.wysiwyg(v);
			}

		}
	} else {
		if (this.source.is(':visible')) {
			return this.filter.source2source(this.source.val());
		} else {
			return this.filter.source($(this.doc.body).html());
		}
	}
}

elRTE.prototype.beforeSave = function() {
	this.source.val(this.val()||'');
}

/**
 * Submit form
 *
 * @return void
 **/
elRTE.prototype.save = function() {
	this.beforeSave();
	this.editor.parents('form').submit();
}

elRTE.prototype.log = function(msg) {
	if (window.console && window.console.log) {
		window.console.log(msg);
	}

}

elRTE.prototype.i18Messages = {};

$.fn.elrte = function(o, v) {
	var cmd = typeof(o) == 'string' ? o : '', ret;

	this.each(function() {
		if (!this.elrte) {
			this.elrte = new elRTE(this, typeof(o) == 'object' ? o : {});
		}
		switch (cmd) {
			case 'open':
			case 'show':
				this.elrte.open();
				break;
			case 'close':
			case 'hide':
				this.elrte.close();
				break;
			case 'updateSource':
				this.elrte.updateSource();
				break;
		}
	});

	if (cmd == 'val') {
		if (!this.length) {
			return '';
		} else if (this.length == 1) {
			return v ? this[0].elrte.val(v) : this[0].elrte.val();
		} else {
			ret = {}
			this.each(function() {
				ret[this.elrte.source.attr('name')] = this.elrte.val();
			});
			return ret;
		}
	}
	return this;
}

})(jQuery);
/*
 * DOM utilites for elRTE
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 */
(function($) {
elRTE.prototype.dom = function(rte) {
	this.rte = rte;
	var self = this;
	this.regExp = {
		textNodes         : /^(A|ABBR|ACRONYM|ADDRESS|B|BDO|BIG|BLOCKQUOTE|CAPTION|CENTER|CITE|CODE|DD|DEL|DFN|DIV|DT|EM|FIELDSET|FONT|H[1-6]|I|INS|KBD|LABEL|LEGEND|LI|MARQUEE|NOBR|NOEMBED|P|PRE|Q|SAMP|SMALL|SPAN|STRIKE|STRONG|SUB|SUP|TD|TH|TT|VAR)$/,
		textContainsNodes : /^(A|ABBR|ACRONYM|ADDRESS|B|BDO|BIG|BLOCKQUOTE|CAPTION|CENTER|CITE|CODE|DD|DEL|DFN|DIV|DL|DT|EM|FIELDSET|FONT|H[1-6]|I|INS|KBD|LABEL|LEGEND|LI|MARQUEE|NOBR|NOEMBED|OL|P|PRE|Q|SAMP|SMALL|SPAN|STRIKE|STRONG|SUB|SUP|TABLE|THEAD|TBODY|TFOOT|TD|TH|TR|TT|UL|VAR)$/,
		block             : /^(APPLET|BLOCKQUOTE|BR|CAPTION|CENTER|COL|COLGROUP|DD|DIV|DL|DT|H[1-6]|EMBED|FIELDSET|LI|MARQUEE|NOBR|OBJECT|OL|P|PRE|TABLE|THEAD|TBODY|TFOOT|TD|TH|TR|UL)$/,
		selectionBlock    : /^(APPLET|BLOCKQUOTE|BR|CAPTION|CENTER|COL|COLGROUP|DD|DIV|DL|DT|H[1-6]|EMBED|FIELDSET|LI|MARQUEE|NOBR|OBJECT|OL|P|PRE|TD|TH|TR|UL)$/,
		header            : /^H[1-6]$/,
		formElement       : /^(FORM|INPUT|HIDDEN|TEXTAREA|SELECT|BUTTON)$/
	};

	/********************************************************/
	/*                      Утилиты                         */
	/********************************************************/

	/**
	 * Возвращает body редактируемого документа
	 *
	 * @return Element
	 **/
	this.root = function() {
		return this.rte.body;
	}

	this.create = function(t) {
		return this.rte.doc.createElement(t);
	}

	/**
	 * Return node for bookmark with unique ID
	 *
	 * @return DOMElement
	 **/
	this.createBookmark = function() {
		var b = this.rte.doc.createElement('span');
		b.id = 'elrte-bm-'+Math.random().toString().substr(2);
		$(b).addClass('elrtebm');
		return b;
	}

	/**
	 * Вовращает индекс элемента внутри родителя
	 *
	 * @param  Element n  нода
	 * @return integer
	 **/
	this.indexOf = function(n) {
		var ndx = 0;
		n = $(n);
		while ((n = n.prev()) && n.length) {
			ndx++;
		}
		return ndx;
	}

	/**
	 * Вовращает значение аттрибута в нижнем регистре (ох уж этот IE)
	 *
	 * @param  Element n  нода
	 * @param  String  attr имя аттрибута
	 * @return string
	 **/
	this.attr = function(n, attr) {
		var v = '';
		if (n.nodeType == 1) {
			v = $(n).attr(attr);
			if (v && attr != 'src' && attr != 'href') {
				v = v.toString().toLowerCase();
			}
		}
		return v||'';
	}

	/**
	 * Вовращает ближайший общий контейнер для 2-х эл-тов
	 *
	 * @param  Element n  нода1
	 * @param  Element n  нода2
	 * @return Element
	 **/
	this.findCommonAncestor = function(n1, n2) {
		if (!n1 || !n2) {
			return this.rte.log('dom.findCommonAncestor invalid arguments');
		}
		if (n1 == n2) {
			return n1;
		} else if (n1.nodeName == 'BODY' || n2.nodeName == 'BODY') {
			return this.rte.doc.body;
		}
		var p1 = $(n1).parents(), p2 = $(n2).parents(), l  = p2.length-1, c  = p2[l];
		for (var i = p1.length - 1; i >= 0; i--, l--){
			if (p1[i] == p2[l]) {
				c = p1[i];
			} else {
				break;
			}
		};
		return c;
	}
	/**
	 * Вовращает TRUE, если нода пустая
	 * пустой считаем ноды:
	 *  - текстовые эл-ты, содержащие пустую строку или тег br
	 *  - текстовые ноды с пустой строкой
	 *
	 * @param  DOMElement n  нода
	 * @return bool
	 **/
	this.isEmpty = function(n) {
		if (n.nodeType == 1) {
			return this.regExp.textNodes.test(n.nodeName) ? $.trim($(n).text()).length == 0 : false;
		} else if (n.nodeType == 3) {
			return /^(TABLE|THEAD|TFOOT|TBODY|TR|UL|OL|DL)$/.test(n.parentNode.nodeName)
				|| n.nodeValue == ''
				|| ($.trim(n.nodeValue).length== 0 && !(n.nextSibling && n.previousSibling && n.nextSibling.nodeType==1 && n.previousSibling.nodeType==1 && !this.regExp.block.test(n.nextSibling.nodeName) && !this.regExp.block.test(n.previousSibling.nodeName) ));
		}
		return true;
	}

	/********************************************************/
	/*                  Перемещение по DOM                  */
	/********************************************************/

	/**
	 * Вовращает следующую соседнюю ноду (не включаются текстовые ноды не создающие значимые пробелы между инлайн элементами)
	 *
	 * @param  DOMElement n  нода
	 * @return DOMElement
	 **/
	this.next = function(n) {
		while (n.nextSibling && (n = n.nextSibling)) {
			if (n.nodeType == 1 || (n.nodeType == 3 && !this.isEmpty(n))) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Вовращает предыдующую соседнюю ноду (не включаются текстовые ноды не создающие значимые пробелы между инлайн элементами)
	 *
	 * @param  DOMElement n  нода
	 * @return DOMElement
	 **/
	this.prev = function(n) {
		while (n.previousSibling && (n = n.previousSibling)) {
			if (n.nodeType == 1 || (n.nodeType ==3 && !this.isEmpty(n))) {
				return n;
			}
		}
		return null;
	}

	this.isPrev = function(n, prev) {
		while ((n = this.prev(n))) {
			if (n == prev) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Вовращает все следующие соседнии ноды (не включаются текстовые ноды не создающие значимые пробелы между инлайн элементами)
	 *
	 * @param  DOMElement n  нода
	 * @return Array
	 **/
	this.nextAll = function(n) {
		var ret = [];
		while ((n = this.next(n))) {
			ret.push(n);
		}
		return ret;
	}

	/**
	 * Вовращает все предыдующие соседнии ноды (не включаются текстовые ноды не создающие значимые пробелы между инлайн элементами)
	 *
	 * @param  DOMElement n  нода
	 * @return Array
	 **/
	this.prevAll = function(n) {
		var ret = [];
		while ((n = this.prev(n))) {
			ret.push(n);
		}
		return ret;
	}

	/**
	 * Вовращает все следующие соседнии inline ноды (не включаются текстовые ноды не создающие значимые пробелы между инлайн элементами)
	 *
	 * @param  DOMElement n  нода
	 * @return Array
	 **/
	this.toLineEnd = function(n) {
		var ret = [];
		while ((n = this.next(n)) && n.nodeName != 'BR' && n.nodeName != 'HR' && this.isInline(n)) {
			ret.push(n);
		}
		return ret;
	}

	/**
	 * Вовращает все предыдующие соседнии inline ноды (не включаются текстовые ноды не создающие значимые пробелы между инлайн элементами)
	 *
	 * @param  DOMElement n  нода
	 * @return Array
	 **/
	this.toLineStart = function(n) {
		var ret = [];
		while ((n = this.prev(n)) && n.nodeName != 'BR' && n.nodeName != 'HR' && this.isInline(n) ) {
			ret.unshift(n);
		}
		return ret;
	}

	/**
	 * Вовращает TRUE, если нода - первый непустой эл-т внутри родителя
	 *
	 * @param  Element n  нода
	 * @return bool
	 **/
	this.isFirstNotEmpty = function(n) {
		while ((n = this.prev(n))) {
			if (n.nodeType == 1 || (n.nodeType == 3 && $.trim(n.nodeValue)!='' ) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Вовращает TRUE, если нода - последний непустой эл-т внутри родителя
	 *
	 * @param  Element n  нода
	 * @return bool
	 **/
	this.isLastNotEmpty = function(n) {
		while ((n = this.next(n))) {
			if (!this.isEmpty(n)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Вовращает TRUE, если нода - единственный непустой эл-т внутри родителя
	 *
	 * @param  DOMElement n  нода
	 * @return bool
	 **/
	this.isOnlyNotEmpty = function(n) {
		return this.isFirstNotEmpty(n) && this.isLastNotEmpty(n);
	}

	/**
	 * Вовращает последний непустой дочерний эл-т ноды или FALSE
	 *
	 * @param  Element n  нода
	 * @return Element
	 **/
	this.findLastNotEmpty = function(n) {
		this.rte.log('findLastNotEmpty Who is here 0_o');
		if (n.nodeType == 1 && (l = n.lastChild)) {
			if (!this.isEmpty(l)) {
				return l;
			}
			while (l.previousSibling && (l = l.previousSibling)) {
				if (!this.isEmpty(l)) {
					return l;
				}
			}
		}
		return false;
	}

	/**
	 * Возвращает TRUE, если нода "inline"
	 *
	 * @param  DOMElement n  нода
	 * @return bool
	 **/
	this.isInline = function(n) {
		if (n.nodeType == 3) {
			return true;
		} else if (n.nodeType == 1) {
			n = $(n);
			var d = n.css('display');
			var f = n.css('float');
			return d == 'inline' || d == 'inline-block' || f == 'left' || f == 'right';
		}
		return true;
	}


	/********************************************************/
	/*                  Поиск элементов                     */
	/********************************************************/

	this.is = function(n, f) {
		if (n && n.nodeName) {
			if (typeof(f) == 'string') {
				f = this.regExp[f]||/.?/;
			}
			if (f instanceof RegExp && n.nodeName) {
				return f.test(n.nodeName);
			} else if (typeof(f) == 'function') {
				return f(n);
			}
		}

		return false;
	}

	/**
	 * Вовращает элемент(ы) отвечающие условиям поиска
	 *
	 * @param  DOMElement||Array  n       нода
	 * @param  RegExp||String     filter  фильтр условия поиска (RegExp или имя ключа this.regExp или *)
	 * @return DOMElement||Array
	 **/
	this.filter = function(n, filter) {
		var ret = [], i;
		if (!n.push) {
			return this.is(n, filter) ? n : null;
		}
		for (i=0; i < n.length; i++) {
			if (this.is(n[i], filter)) {
				ret.push(n[i]);
			}
		};
		return ret;
	}


	/**
	 * Вовращает массив родительских элементов, отвечающих условиям поиска
	 *
	 * @param  DOMElement      n  нода, родителей, которой ищем
	 * @param  RegExp||String  filter   фильтр условия поиска (RegExp или имя ключа this.regExp или *)
	 * @return Array
	 **/
	this.parents = function(n, filter) {
		var ret = [];

		while (n && (n = n.parentNode) && n.nodeName != 'BODY' && n.nodeName != 'HTML') {
			if (this.is(n, filter)) {
				ret.push(n);
			}
		}
		return ret;
	}

	/**
	 * Вовращает ближайший родительский эл-т, отвечающий условиям поиска
	 *
	 * @param  DOMElement     n  нода, родителя, которой ищем
	 * @param  RegExp||String f   фильтр условия поиска (RegExp или имя ключа this.regExp или *)
	 * @return DOMElement
	 **/
	this.parent = function(n, f) {
		return this.parents(n, f)[0] || null;
	}

	/**
	 * Вовращает или саму ноду или ее ближайшего родителя, если выполняются условия sf для самой ноды или pf для родителя
	 *
	 * @param  DOMElement     n  нода, родителя, которой ищем
	 * @param  RegExp||String sf   фильтр условия для самой ноды
	* @param  RegExp||String  pf   фильтр условия для родителя
	 * @return DOMElement
	 **/
	this.selfOrParent = function(n, sf, pf) {
		return this.is(n, sf) ? n : this.parent(n, pf||sf);
	}

	/**
	 * Вовращает родительскую ноду - ссылку
	 *
	 * @param  Element n  нода
	 * @return Element
	 **/
	this.selfOrParentLink = function(n) {
		n = this.selfOrParent(n, /^A$/);
		return n && n.href ? n : null;
	}

	/**
	 * Вовращает TRUE, если нода -  anchor
	 *
	 * @param  Element n  нода
	 * @return bool
	 **/
	this.selfOrParentAnchor = function(n) {
		n = this.selfOrParent(n, /^A$/);
		return n && !n.href && n.name ? n : null;
	}

	/**
	 * Вовращает массив дочерних ссылок
	 *
	 * @param  DOMElement n  нода
	 * @return Array
	 **/
	this.childLinks = function(n) {
		var res = [];
		$('a[href]', n).each(function() { res.push(this); });
		return res;
	}

	this.selectionHas = function(f) {
		var n = this.rte.selection.cloneContents(), i;
		if (n && n.childNodes && n.childNodes.length) {
			for (i=0; i < n.childNodes.length; i++) {
				if (typeof(f) == 'function') {
					if (f(n.childNodes[i])) {
						return true;
					}
				} else if (n instanceof RegExp) {
					if (f.test(n.childNodes[i].nodeName)) {
						return true;
					}
				}
			};
		}

		return false;
	}
	/********************************************************/
	/*                    Изменения DOM                     */
	/********************************************************/

	/**
	 * Оборачивает одну ноду другой
	 *
	 * @param  DOMElement n  оборачиваемая нода
	 * @param  DOMElement w  нода обертка или имя тега
	 * @return DOMElement
	 **/
	this.wrap = function(n, w) {
		n = $.isArray(n) ? n : [n];
		w = w.nodeName ? w : this.create(w);

		if (n[0] && n[0].nodeType && n[0].parentNode) {
			w = n[0].parentNode.insertBefore(w, n[0]);
			$(n).each(function() {
				if (this!=w) {
					w.appendChild(this);
				}
			});
		}

		return w;
	}

	/**
	 * Replace node with its contents
	 *
	 * @param  DOMElement n node
	 * @return void
	 **/
	this.unwrap = function(n) {
		while (n.firstChild) {
			n.parentNode.insertBefore(n.firstChild, n);
		}
		n.parentNode.removeChild(n);
	}

	/**
	 * Оборачивает все содержимое ноды
	 *
	 * @param  DOMElement n  оборачиваемая нода
	 * @param  DOMElement w  нода обертка или имя тега
	 * @return DOMElement
	 **/
	this.wrapContents = function(n, w) {
		w = w.nodeName ? w : this.create(w);
		for (var i=0; i < n.childNodes.length; i++) {
			w.appendChild(n.childNodes[i]);
		};
		n.appendChild(w);
		return w;
	}

	this.cleanNode = function(n) {

		if (n.nodeType != 1) {
			return;
		}
		if (/^(P|LI)$/.test(n.nodeName) && (l = this.findLastNotEmpty(n)) && l.nodeName == 'BR') {
			$(l).remove();
		}
		$n = $(n);
		$n.children().each(function() {
			this.cleanNode(this);
		});
		if (n.nodeName != 'BODY' && !/^(TABLE|TR|TD)$/.test(n) && this.isEmpty(n)) {
			return $n.remove();
		}
		if ($n.attr('style') === '') {
			$n.removeAttr('style');
		}
		if (this.rte.browser.safari && $n.hasClass('Apple-span')) {
			$n.removeClass('Apple-span');
		}
		if (n.nodeName == 'SPAN' && !$n.attr('style') && !$n.attr('class') && !$n.attr('id')) {
			$n.replaceWith($n.html());
		}
	}

	this.cleanChildNodes = function(n) {
		var cmd = this.cleanNode;
		$(n).children().each(function() { cmd(this); });
	}

	/********************************************************/
	/*                       Таблицы                        */
	/********************************************************/

	this.tableMatrix = function(n) {
		var mx = [];
		if (n && n.nodeName == 'TABLE') {
			var max = 0;
			function _pos(r) {
				for (var i=0; i<=max; i++) {
					if (!mx[r][i]) {
						return i;
					}
				};
			}

			$(n).find('tr').each(function(r) {
				if (!$.isArray(mx[r])) {
					mx[r] = [];
				}

				$(this).children('td,th').each(function() {
					var w = parseInt($(this).attr('colspan')||1);
					var h = parseInt($(this).attr('rowspan')||1);
					var i = _pos(r);
					for (var y=0; y<h; y++) {
						for (var x=0; x<w; x++) {
							var _y = r+y;
							if (!$.isArray(mx[_y])) {
								mx[_y] = [];
							}
							var d = x==0 && y==0 ? this : (y==0 ? x : "-");
							mx[_y][i+x] = d;
						}
					};
					max= Math.max(max, mx[r].length);
				});
			});
		}
		return mx;
	}

	this.indexesOfCell = function(n, tbm) {
		for (var rnum=0; rnum < tbm.length; rnum++) {
			for (var cnum=0; cnum < tbm[rnum].length; cnum++) {
				if (tbm[rnum][cnum] == n) {
					return [rnum, cnum];
				}

			};
		};
	}

	this.fixTable = function(n) {
		if (n && n.nodeName == 'TABLE') {
			var tb = $(n);
			//tb.find('tr:empty').remove();
			var mx = this.tableMatrix(n);
			var x  = 0;
			$.each(mx, function() {
				x = Math.max(x, this.length);
			});
			if (x==0) {
				return tb.remove();
			}
			// for (var i=0; i<mx.length; i++) {
			// 	this.rte.log(mx[i]);
			// }

			for (var r=0; r<mx.length; r++) {
				var l = mx[r].length;
				//this.rte.log(r+' : '+l)

				if (l==0) {
					//this.rte.log('remove: '+tb.find('tr').eq(r))
					tb.find('tr').eq(r).remove();
//					tb.find('tr').eq(r).append('<td>remove</td>')
				} else if (l<x) {
					var cnt = x-l;
					var row = tb.find('tr').eq(r);
					for (i=0; i<cnt; i++) {
						row.append('<td>&nbsp;</td>');
					}
				}
			}

		}
	}

	this.tableColumn = function(n, ext, fix) {
		n      = this.selfOrParent(n, /^TD|TH$/);
		var tb = this.selfOrParent(n, /^TABLE$/);
		ret    = [];
		info   = {offset : [], delta : []};
		if (n && tb) {
			fix && this.fixTable(tb);
			var mx = this.tableMatrix(tb);
			var _s = false;
			var x;
			for (var r=0; r<mx.length; r++) {
				for (var _x=0; _x<mx[r].length; _x++) {
					if (mx[r][_x] == n) {
						x = _x;
						_s = true;
						break;
					}
				}
				if (_s) {
					break;
				}
			}

			// this.rte.log('matrix');
			// for (var i=0; i<mx.length; i++) {
			// 	this.rte.log(mx[i]);
			// }
			if (x>=0) {
				for(var r=0; r<mx.length; r++) {
					var tmp = mx[r][x]||null;
					if (tmp) {
						if (tmp.nodeName) {
							ret.push(tmp);
							if (ext) {
								info.delta.push(0);
								info.offset.push(x);
							}
						} else {
							var d = parseInt(tmp);
							if (!isNaN(d) && mx[r][x-d] && mx[r][x-d].nodeName) {
								ret.push(mx[r][x-d]);
								if (ext) {
									info.delta.push(d);
									info.offset.push(x);
								}
							}
						}
					}
				}
			}
		}
		return !ext ? ret : {column : ret, info : info};
	}
}

})(jQuery);
(function($) {
	/**
	 * @class Filter - clean editor content
	 * @param elRTE editor instance
	 * @author Dmitry (dio) Levashov, dio@std42.ru
	 */
	elRTE.prototype.filter = function(rte) {
		var self = this,
			n = $('<span/>').addClass('elrtetesturl').appendTo(document.body)[0];
		// media replacement image base url
		this.url = (typeof(n.currentStyle )!= "undefined" ? n.currentStyle['backgroundImage'] : document.defaultView.getComputedStyle(n, null)['backgroundImage']).replace(/^url\((['"]?)([\s\S]+\/)[\s\S]+\1\)$/i, "$2");
		$(n).remove();

		this.rte = rte;
		// flag - return xhtml tags?
		this.xhtml = /xhtml/i.test(rte.options.doctype);
		// boolean attributes
		this.boolAttrs = rte.utils.makeObject('checked,compact,declare,defer,disabled,ismap,multiple,nohref,noresize,noshade,nowrap,readonly,selected'.split(','));
		// tag regexp
		this.tagRegExp = /<(\/?)([\w:]+)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*\/?>/g;
		// opened tag regexp
		this.openTagRegExp = /<([\w:]+)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*\/?>/g;
		// attributes regexp
		this.attrRegExp = /(\w+)(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^\s]+))?/g;
		// script tag regexp
		this.scriptRegExp = /<script([^>]*)>([\s\S]*?)<\/script>/gi;
		// style tag regexp
		this.styleRegExp = /(<style([^>]*)>[\s\S]*?<\/style>)/gi;
		// link tag regexp
		this.linkRegExp = /(<link([^>]+)>)/gi;
		// cdata regexp
		this.cdataRegExp = /<!\[CDATA\[([\s\S]+)\]\]>/g;
		// object tag regexp
		this.objRegExp = /<object([^>]*)>([\s\S]*?)<\/object>/gi;
		// embed tag regexp
		this.embRegExp = /<(embed)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*>/gi;
		// param tag regexp
		this.paramRegExp = /<(param)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*>/gi;
		this.vimeoRegExp = /<(iframe)\s+([^>]*src\s*=\s*"http:\/\/[^"]+vimeo\.com\/\w+[^>]*)>([\s\S]*?)<\/iframe>/gi;
		// yandex maps regexp
		this.yMapsRegExp = /<div\s+([^>]*id\s*=\s*('|")?YMapsID[^>]*)>/gi;
		// google maps regexp
		this.gMapsRegExp = /<iframe\s+([^>]*src\s*=\s*"http:\/\/maps\.google\.\w+[^>]*)>([\s\S]*?)<\/iframe>/gi;
		// video hostings url regexp
		this.videoHostRegExp = /^(http:\/\/[\w\.]*)?(youtube|vimeo|rutube).*/i;
		// elrte services classes regexp
		this.serviceClassRegExp = /<(\w+)([^>]*class\s*=\s*"[^>]*elrte-[^>]*)>\s*(<\/\1>)?/gi;
		this.pagebreakRegExp = /<(\w+)([^>]*style\s*=\s*"[^>]*page-break[^>]*)>\s*(<\/\1>)?/gi;

		this.pbRegExp = new RegExp('<!-- pagebreak -->', 'gi');
		// allowed tags
		this.allowTags = rte.options.allowTags.length ? rte.utils.makeObject(rte.options.allowTags) : null;
		// denied tags
		this.denyTags = rte.options.denyTags.length ? rte.utils.makeObject(rte.options.denyTags) : null;
		// deny attributes
		this.denyAttr = rte.options.denyAttr ? rte.utils.makeObject(rte.options.denyAttr) : null;
		// deny attributes for pasted html
		this.pasteDenyAttr = rte.options.pasteDenyAttr ? rte.utils.makeObject(rte.options.pasteDenyAttr) : null;
		// font sizes to convert size attr into css property
		this.fontSize = ['medium', 'xx-small', 'small', 'medium','large','x-large','xx-large' ];
		// font families regexp to detect family by font name
		this.fontFamily = {
			'sans-serif' : /^(arial|tahoma|verdana)$/i,
			'serif'      : /^(times|times new roman)$/i,
			'monospace'  : /^courier$/i
		}
		// scripts storage
		this.scripts = {};
		// cached chains of rules
		this._chains = {};

		// cache chains
		$.each(this.chains, function(n) {
			self._chains[n] = [];
			$.each(this, function(i, r) {
				typeof(self.rules[r]) == 'function' && self._chains[n].push(self.rules[r]);
			});
		});

		/**
		 * filtering through required chain
		 *
		 * @param  String  chain name
		 * @param  String  html-code
		 * @return String
		 **/
		this.proccess = function(chain, html) {
			// remove whitespace at the begin and end
			html = $.trim(html).replace(/^\s*(&nbsp;)+/gi, '').replace(/(&nbsp;|<br[^>]*>)+\s*$/gi, '');
			// pass html through chain
			$.each(this._chains[chain]||[], function() {
				html = this.call(self, html);
			});
			return html.replace(/\t/g, '  ').replace(/\r/g, '').replace(/\s*\n\s*\n+/g, "\n")+'  ';
		}

		/**
		 * wrapper for "wysiwyg" chain filtering
		 *
		 * @param  String
		 * @return String
		 **/
		this.wysiwyg = function(html) {
			return this.proccess('wysiwyg', html);
		}

		/**
		 * wrapper for "source" chain filtering
		 *
		 * @param  String
		 * @return String
		 **/
		this.source = function(html) {
			return this.proccess('source', html);
		}

		/**
		 * wrapper for "source2source" chain filtering
		 *
		 * @param  String
		 * @return String
		 **/
		this.source2source = function(html) {
			return this.proccess('source2source', html);
		}

		/**
		 * wrapper for "wysiwyg2wysiwyg" chain filtering
		 *
		 * @param  String
		 * @return String
		 **/
		this.wysiwyg2wysiwyg = function(html) {
			return this.proccess('wysiwyg2wysiwyg', html);
		}

		/**
		 * Parse attributes from string into object
		 *
		 * @param  String  string of attributes
		 * @return Object
		 **/
		this.parseAttrs = function(s) {
			var a = {},
				b = this.boolAttrs,
				m = s.match(this.attrRegExp),
				t, n, v;

			m && $.each(m, function(i, s) {
				t = s.split('=');
				n = $.trim(t[0]).toLowerCase();

				if (t.length>2) {
					t.shift();
					v = t.join('=');
				} else {
					v = b[n] ||t[1]||'';
				}
				a[n] = $.trim(v).replace(/^('|")(.*)(\1)$/, "$2");
			});

			a.style = this.rte.utils.parseStyle(a.style);
			a['class'] = this.rte.utils.parseClass(a['class']||'')
			return a;
		}

		/**
		 * Restore attributes string from hash
		 *
		 * @param  Object  attributes hash
		 * @return String
		 **/
		this.serializeAttrs = function(a, c) {
			var s = [], self = this;

			$.each(a, function(n, v) {
				if (n=='style') {
					v = self.rte.utils.serializeStyle(v, c);
				} else if (n=='class') {
					v = self.rte.utils.serializeClass(v);
				}
				v && s.push(n+'="'+v+'"');
			});
			return s.join(' ');
		}

		/**
		 * Remove/replace denied attributes/style properties
		 *
		 * @param  Object  attributes hash
		 * @param  String  tag name to wich attrs belongs
		 * @return Object
		 **/
		this.cleanAttrs = function(a, t) {
			var self = this, ra = this.replaceAttrs;

			// remove safari and mso classes
			$.each(a['class'], function(n) {
				/^(Apple-style-span|mso\w+)$/i.test(n) && delete a['class'][n];
			});

			function value(v) {
				return v+(/\d$/.test(v) ? 'px' : '');
			}

			$.each(a, function(n, v) {
				// replace required attrs with css
				ra[n] && ra[n].call(self, a, t);
				// remove/fix mso styles
				if (n == 'style') {
					$.each(v, function(sn, sv) {
						switch (sn) {
							case "mso-padding-alt":
							case "mso-padding-top-alt":
							case "mso-padding-right-alt":
							case "mso-padding-bottom-alt":
							case "mso-padding-left-alt":
							case "mso-margin-alt":
							case "mso-margin-top-alt":
							case "mso-margin-right-alt":
							case "mso-margin-bottom-alt":
							case "mso-margin-left-alt":
							case "mso-table-layout-alt":
							case "mso-height":
							case "mso-width":
							case "mso-vertical-align-alt":
								a.style[sn.replace(/^mso-|-alt$/g, '')] = value(sv);
								delete a.style[sn];
								break;

							case "horiz-align":
								a.style['text-align'] = sv;
								delete a.style[sn];
								break;

							case "vert-align":
								a.style['vertical-align'] = sv;
								delete a.style[sn];
								break;

							case "font-color":
							case "mso-foreground":
								a.style.color = sv;
								delete a.style[sn];
							break;

							case "mso-background":
							case "mso-highlight":
								a.style.background = sv;
								delete a.style[sn];
								break;

							case "mso-default-height":
								a.style['min-height'] = value(sv);
								delete a.style[sn];
								break;

							case "mso-default-width":
								a.style['min-width'] = value(sv);
								delete a.style[sn];
								break;

							case "mso-padding-between-alt":
								a.style['border-collapse'] = 'separate';
								a.style['border-spacing'] = value(sv);
								delete a.style[sn];
								break;

							case "text-line-through":
								if (sv.match(/(single|double)/i)) {
									a.style['text-decoration'] = 'line-through';
								}
								delete a.style[sn];
								break;

							case "mso-zero-height":
								if (sv == 'yes') {
									a.style.display = 'none';
								}
								delete a.style[sn];
								break;

							case 'font-weight':
								if (sv == 700) {
									a.style['font-weight'] = 'bold';
								}
								break;

							default:
								if (sn.match(/^(mso|column|font-emph|lang|layout|line-break|list-image|nav|panose|punct|row|ruby|sep|size|src|tab-|table-border|text-(?!align|decor|indent|trans)|top-bar|version|vnd|word-break)/)) {
									delete a.style[sn]
								}
						}
					});
				}
			});
			return a;
		}

	}

	// rules to replace tags
	elRTE.prototype.filter.prototype.replaceTags = {
		b         : { tag : 'strong' },
		big       : { tag : 'span', style : {'font-size' : 'large'} },
		center    : { tag : 'div',  style : {'text-align' : 'center'} },
		i         : { tag : 'em' },
		font      : { tag : 'span' },
		nobr      : { tag : 'span', style : {'white-space' : 'nowrap'} },
		menu      : { tag : 'ul' },
		plaintext : { tag : 'pre' },
		s         : { tag : 'strike' },
		small     : { tag : 'span', style : {'font-size' : 'small'}},
		u         : { tag : 'span', style : {'text-decoration' : 'underline'} },
		xmp       : { tag : 'pre' }
	}

	// rules to replace attributes
	elRTE.prototype.filter.prototype.replaceAttrs = {
		align : function(a, n) {
			switch (n) {
				case 'img':
					a.style[a.align.match(/(left|right)/) ? 'float' : 'vertical-align'] = a.align;
					break;

				case 'table':
					if (a.align == 'center') {
						a.style['margin-left'] = a.style['margin-right'] = 'auto';
					} else {
						a.style['float'] = a.align;
					}
					break;

				default:
					a.style['text-align'] = a.align;
			}
			delete a.align;
		},
		border : function(a) {
			!a.style['border-width'] && (a.style['border-width'] = (parseInt(a.border)||1)+'px');
			!a.style['border-style'] && (a.style['border-style'] = 'solid');
			delete a.border;
		},
		bordercolor : function(a) {
			!a.style['border-color'] && (a.style['border-color'] = a.bordercolor);
			delete a.bordercolor;
		},
		background : function(a) {
			!a.style['background-image'] && (a.style['background-image'] = 'url('+a.background+')');
			delete a.background;
		},
		bgcolor : function(a) {
			!a.style['background-color'] && (a.style['background-color'] = a.bgcolor);
			delete a.bgcolor;
		},
		clear : function(a) {
			a.style.clear = a.clear == 'all' ? 'both' : a.clear;
			delete a.clear;
		},
		color : function(a) {
			!a.style.color && (a.style.color = a.color);
			delete a.color;
		},
		face : function(a) {
			var f = a.face.toLowerCase();
			$.each(this.fontFamily, function(n, r) {
				if (f.match(r)) {
					a.style['font-family'] = f+','+n;
				}
			});
			delete a.face;
		},
		hspace : function(a, n) {
			if (n == 'img') {
				var v = parseInt(a.hspace)||0;
				!a.style['margin-left'] && (a.style['margin-left'] = v+'px');
				!a.style['margin-right'] && (a.style['margin-right'] = v+'px')
				delete a.hspace;
			}
		},
		size : function(a, n) {
			if (n != 'input') {
				a.style['font-size'] = this.fontSize[parseInt(a.size)||0]||'medium';
				delete a.size;
			}
		},
		valign : function(a) {
			if (!a.style['vertical-align']) {
				a.style['vertical-align'] = a.valign;
			}
			delete a.valign;
		},
		vspace : function(a, n) {
			if (n == 'img') {
				var v = parseInt(a.vspace)||0;
				!a.style['margin-top'] && (a.style['margin-top'] = v+'px');
				!a.style['margin-bottom'] && (a.style['margin-bottom'] = v+'px')
				delete a.hspace;
			}
		}
	}

	// rules collection
	elRTE.prototype.filter.prototype.rules = {
		/**
		 * If this.rte.options.allowTags is set - remove all except this ones
		 *
		 * @param String  html code
		 * @return String
		 **/
		allowedTags : function(html) {
			var a = this.allowTags;
			return a ? html.replace(this.tagRegExp, function(t, c, n) { return a[n.toLowerCase()] ? t : ''; }) : html;
		},
		/**
		 * If this.rte.options.denyTags is set - remove all deny tags
		 *
		 * @param String  html code
		 * @return String
		 **/
		deniedTags : function(html) {
			var d = this.denyTags;
			return d ? html.replace(this.tagRegExp, function(t, c, n) { return d[n.toLowerCase()] ? '' : t }) : html;
		},

		/**
		 * Replace not allowed tags/attributes
		 *
		 * @param String  html code
		 * @return String
		 **/
		clean : function(html) {
			var self = this,
				rt   = this.replaceTags,
				ra   = this.replaceAttrs,
				da   = this.denyAttr,
				n;

			return html.replace(/<!DOCTYPE([\s\S]*)>/gi, '')
				.replace(/<p [^>]*class="?MsoHeading"?[^>]*>(.*?)<\/p>/gi, "<p><strong>$1</strong></p>")
				.replace(/<span\s+style\s*=\s*"\s*mso-spacerun\s*:\s*yes\s*;?\s*"\s*>([\s&nbsp;]*)<\/span>/gi, "$1")
				.replace(/(<p[^>]*>\s*<\/p>|<p[^>]*\/>)/gi, '<br>')
				.replace(this.tagRegExp, function(t, c, n, a) {
					n = n.toLowerCase();

					if (c) {
						return '</'+(rt[n] ? rt[n].tag : n)+'>';
					}

					// create attributes hash and clean it
					a = self.cleanAttrs(self.parseAttrs(a||''), n);

					if (rt[n]) {
						rt[n].style && $.extend(a.style, rt[n].style);
						n = rt[n].tag;
					}

					da && $.each(a, function(na) {
						if (da[na]) {
							delete a[na];
						}
					});
					a = self.serializeAttrs(a);
					return '<'+n+(a?' ':'')+a+'>';
				});
		},

		/**
		 * Clean pasted html
		 *
		 * @param String  html code
		 * @return String
		 **/
		cleanPaste : function(html) {
			var self = this, d = this.pasteDenyAttr;

			html = html
				.replace(this.scriptRegExp, '')
				.replace(this.styleRegExp, '')
				.replace(this.linkRegExp, '')
				.replace(this.cdataRegExp, '')
				.replace(/\<\!--[\s\S]*?--\>/g, '');

			if (this.rte.options.pasteOnlyText) {
				html = html.replace(this.tagRegExp, function(t, c, n) {
					return /br/i.test(n) || (c && /h[1-6]|p|ol|ul|li|div|blockquote|tr/i) ? '<br>' : '';
				}).replace(/(&nbsp;|<br[^>]*>)+\s*$/gi, '');
			} else if (d) {
				html = html.replace(this.openTagRegExp, function(t, n, a) {
					a = self.parseAttrs(a);
					$.each(a, function(an) {
						if (d[an]) {
							delete a[an];
						}
					});
					a = self.serializeAttrs(a, true);
					return '<'+n+(a?' ':'')+a+'>';
				});
			}
			return html;
		},

		/**
		 * Replace script/style/media etc with placeholders
		 *
		 * @param String  html code
		 * @return String
		 **/
		replace : function(html) {
			var self = this, r = this.rte.options.replace||[], n;

			// custom replaces if set
			if (r.length) {
				$.each(r, function(i, f) {
					if (typeof(f) == 'function') {
						html = f.call(self, html);
					}
				});
			}

			/**
			 * Return media replacement - img html code
			 *
			 * @param Object  object to store in rel attr
			 * @param String  media mime-type
			 * @return String
			 **/
			function img(o, t) {
				var s = src(),
					c = s && self.videoHostRegExp.test(s) ? s.replace(self.videoHostRegExp, "$2") : t.replace(/^\w+\/(.+)/, "$1"),
					w = parseInt((o.obj ? o.obj.width || o.obj.style.width : 0)||(o.embed ? o.embed.width || o.embed.style.width : 0))||150,
					h = parseInt((o.obj ? o.obj.height || o.obj.style.height : 0)||(o.embed ? o.embed.height || o.embed.style.height : 0))||100,
					id = 'media'+Math.random().toString().substring(2),
					style ='',
					l;

				// find media src
				function src() {
					if (o.embed && o.embed.src) {
						return o.embed.src;
					}
					if (o.params && o.params.length) {
						l = o.params.length;
						while (l--) {
							if (o.params[l].name == 'src' || o.params[l].name == 'movie') {
								return o.params[l].value;
							}
						}
					}
				}
				if (o.obj && o.obj.style && o.obj.style['float']) {
					style = ' style="float:'+o.obj.style['float']+'"';
				}
				self.scripts[id] = o;
				return '<img src="'+self.url+'pixel.gif" class="elrte-media elrte-media-'+c+' elrte-protected" title="'+(s ? self.rte.utils.encode(s) : '')+'" rel="'+id+'" width="'+w+'" height="'+h+'"'+style+'>';
			}

			html = html
				.replace(this.styleRegExp, "<!-- ELRTE_COMMENT$1 -->")
				.replace(this.linkRegExp,  "<!-- ELRTE_COMMENT$1-->")
				.replace(this.cdataRegExp, "<!--[CDATA[$1]]-->")
				.replace(this.scriptRegExp, function(t, a, s) {
					var id;
					if (self.denyTags.script) {
						return '';
					}
					id = 'script'+Math.random().toString().substring(2);
					a = self.parseAttrs(a);
					!a.type && (a.type = 'text/javascript');
					self.scripts[id] = '<script '+self.serializeAttrs(a)+">"+s+"</script>";
					return '<!-- ELRTE_SCRIPT:'+(id)+' -->';
				})
				.replace(this.yMapsRegExp, function(t, a) {
					a = self.parseAttrs(a);
					a['class']['elrte-yandex-maps'] = 'elrte-yandex-maps';
					a['class']['elrte-protected'] = 'elrte-protected';
					return '<div '+self.serializeAttrs(a)+'>';
				})
				.replace(this.gMapsRegExp, function(t, a) {
					var id = 'gmaps'+Math.random().toString().substring(2), w, h;
					a = self.parseAttrs(a);
					w = parseInt(a.width||a.style.width||100);
					h = parseInt(a.height||a.style.height||100);
					self.scripts[id] = t;
					return '<img src="'+self.url+'pixel.gif" class="elrte-google-maps elrte-protected" id="'+id+'" style="width:'+w+'px;height:'+h+'px">';
				})
				.replace(this.objRegExp, function(t, a, c) {
					var m = c.match(self.embRegExp),
						o = { obj : self.parseAttrs(a), embed : m && m.length ? self.parseAttrs(m[0].substring(7)) : null, params : [] },
						i = self.rte.utils.mediaInfo(o.embed ? o.embed.type||'' : '', o.obj.classid||'');

					if (i) {
						if ((m = c.match(self.paramRegExp))) {
							$.each(m, function(i, p) {
								o.params.push(self.parseAttrs(p.substring(6)));
							});
						}
						!o.obj.classid  && (o.obj.classid  = i.classid[0]);
						!o.obj.codebase && (o.obj.codebase = i.codebase);
						o.embed && !o.embed.type && (o.embed.type = i.type);
						// ie bug with empty attrs
						o.obj.width == '1' && delete o.obj.width;
						o.obj.height == '1' && delete o.obj.height;
						if (o.embed) {
							o.embed.width == '1' && delete o.embed.width;
							o.embed.height == '1' && delete o.embed.height;
						}
						return img(o, i.type);
					}
					return t;
				})
				.replace(this.embRegExp, function(t, n, a) {
					var a = self.parseAttrs(a),
						i = self.rte.utils.mediaInfo(a.type||'');
					// ie bug with empty attrs
					a.width == '1' && delete a.width;
					a.height == '1' && delete a.height;
					return i ? img({ embed : a }, i.type) : t;
				})
				.replace(this.vimeoRegExp, function(t, n, a) {
					a = self.parseAttrs(a);
					delete a.frameborder;
					a.width == '1' && delete a.width;
					a.height == '1' && delete a.height;
					a.type = 'application/x-shockwave-flash';
					return img({ embed : a }, 'application/x-shockwave-flash');
				})
				.replace(/<\/(embed|param)>/gi, '')
				.replace(this.pbRegExp, function() {
					return '<img src="'+self.url+'pixel.gif" class="elrte-protected elrte-pagebreak">';
				});


			n = $('<div>'+html+'</div>');
			// remove empty spans and merge nested spans
			n.find('span:not([id]):not([class])').each(function() {
				var t = $(this);

				if (!t.attr('style')) {
					t.children().length ? self.rte.dom.unwrap(this) : t.remove();
				}
			}).end().find('span span:only-child').each(function() {
				var t   = $(this),
					p   = t.parent().eq(0),
					tid = t.attr('id'),
					pid = p.attr('id'), id, s, c;

				if (self.rte.dom.is(this, 'onlyChild') && (!tid || !pid)) {
					c = $.trim(p.attr('class')+' '+t.attr('class'))
					c && p.attr('class', c);
					s = self.rte.utils.serializeStyle($.extend(self.rte.utils.parseStyle($(this).attr('style')||''), self.rte.utils.parseStyle($(p).attr('style')||'')));
					s && p.attr('style', s);
					id = tid||pid;
					id && p.attr('id', id);
					this.firstChild ? $(this.firstChild).unwrap() : t.remove();
				}
			});



			if (!this.rte.options.allowTextNodes) {
				// wrap inline nodes with p
				var dom = this.rte.dom,
					nodes = [],
					w = [];

				if ($.browser.msie) {
					for (var i = 0; i<n[0].childNodes.length; i++) {
						nodes.push(n[0].childNodes[i])
					}
				} else {
					nodes = Array.prototype.slice.call(n[0].childNodes);
				}


				function wrap() {
					if (w.length && dom.filter(w, 'notEmpty').length) {
						dom.wrap(w, document.createElement('p'));
					}
					w = [];
				}
				$.each(nodes, function(i, n) {
					if (dom.is(n, 'block')) {
						wrap();
					} else {
						if (w.length && n.previousSibling != w[w.length-1]) {
							wrap();
						}
						w.push(n);
					}
				});
				wrap();
			}

			return n.html();
		},
		/**
		 * Restore script/style/media etc from placeholders
		 *
		 * @param String  html code
		 * @return String
		 **/
		restore : function(html) {
			var self =this, r = this.rte.options.restore|[];

			// custom restore if set
			if (r.length) {
				$.each(r, function(i, f) {
					if (typeof(f) == 'function') {
						html = f.call(self, html);
					}
				});
			}

			html = html
				.replace(/\<\!--\[CDATA\[([\s\S]*?)\]\]--\>/gi, "<![CDATA[$1]]>")
				.replace(/\<\!--\s*ELRTE_SCRIPT\:\s*(script\d+)\s*--\>/gi, function(t, n) {
					if (self.scripts[n]) {
						t = self.scripts[n];
						delete self.scripts[n];
					}
					return t||'';
				})
				.replace(/\<\!-- ELRTE_COMMENT([\s\S]*?) --\>/gi, "$1")
				.replace(this.serviceClassRegExp, function(t, n, a, e) {
					var a = self.parseAttrs(a), j, o = '';
					// alert(t)
					if (a['class']['elrte-media']) {
						// alert(a.rel)
						// return ''
						// j = a.rel ? JSON.parse(self.rte.utils.decode(a.rel)) : {};
						j = self.scripts[a.rel]||{};
						// alert(j)
						// j = a.rel ? $.parseJSON(self.rte.utils.decode(a.rel)) : {};
						j.params && $.each(j.params, function(i, p) {
							o += '<param '+self.serializeAttrs(p)+">\n";
						});
						j.embed && (o+='<embed '+self.serializeAttrs(j.embed)+">");
						j.obj && (o = '<object '+self.serializeAttrs(j.obj)+">\n"+o+"\n</object>\n");
						return o||t;
					} else if (a['class']['elrte-google-maps']) {
						var t = '';
						if (self.scripts[a.id]) {
							t = self.scripts[a.id];
							delete self.scripts[a.id]
						}
						return t;
					} else if (a['class']['elrte-pagebreak']) {
						return '<!-- pagebreak -->';
					}
					$.each(a['class'], function(n) {
						/^elrte-\w+/i.test(n) && delete(a['class'][n]);
					});
					return '<'+n+' '+self.serializeAttrs(a)+'>'+(e||'');

				});

			return html;
		},
		/**
		 * compact styles and move tags and attributes names in lower case(for ie&opera)
		 *
		 * @param String  html code
		 * return String
		 **/
		compactStyles : function(html) {
			var self = this;

			return html.replace(this.tagRegExp, function(t, c, n, a) {
				a = !c && a ? self.serializeAttrs(self.parseAttrs(a), true) : '';
				return '<'+c+n.toLowerCase()+(a?' ':'')+a+'>';
			});
		},
		/**
		 * return xhtml tags
		 *
		 * @param String  html code
		 * return String
		 **/
		xhtmlTags : function(html) {
			return this.xhtml ? html.replace(/<(img|hr|br|embed|param|link)([^>]*\/*)>/gi, "<$1$2 />") : html;
		}
	}

	/**
	 * Chains configuration
	 * Default chains
	 * wysiwyg - proccess html from source for wysiwyg editor mode
	 * source  - proccess html from wysiwyg for source editor mode
	 * paste   - clean pasted html
	 * wysiwyg2wysiwyg - ciclyc rule to clean html from wysiwyg for wysiwyg paste
	 * source2source - ciclyc rule to clean html from source for source paste
	 * deniedTags is in the end of chain to protect google maps iframe from removed
	 **/
	elRTE.prototype.filter.prototype.chains = {
		wysiwyg         : ['clean','allowedTags', 'replace', 'deniedTags', 'compactStyles'],
		source          : ['clean','allowedTags', 'restore', 'compactStyles', 'xhtmlTags'],
		paste           : ['clean','allowedTags', 'cleanPaste', 'replace', 'deniedTags', 'compactStyles'],
		wysiwyg2wysiwyg : ['clean','allowedTags', 'restore', 'replace', 'deniedTags', 'compactStyles'],
		source2source   : ['clean','allowedTags', 'replace', 'deniedTags', 'restore', 'compactStyles', 'xhtmlTags']
	}



})(jQuery);
(function($) {
elRTE.prototype.history = function(rte) {
	this.rte = rte;
	this._prev = []
	this._next = [];


	this.add = function() {
		if (this.rte.options.historyLength>0 && this._prev.length>= this.rte.options.historyLength) {
			this._prev.slice(this.rte.options.historyLength);
		}
		var b = this.rte.selection.getBookmark();
		this._prev.push([$(this.rte.doc.body).html(), b]);
		this.rte.selection.moveToBookmark(b);
		// this._prev.push($(this.rte.doc.body).html());
		this._next = [];
	}

	this.back = function() {

		if (this._prev.length) {
			var b = this.rte.selection.getBookmark(),
				data = this._prev.pop();
			this._next.push([$(this.rte.doc.body).html(), b]);

			$(this.rte.doc.body).html(data[0]);
			this.rte.selection.moveToBookmark(data[1]);
		}
	}

	this.fwd = function() {
		if (this._next.length) {
			var b = this.rte.selection.getBookmark(),
				data = this._next.pop();
			this._prev.push([$(this.rte.doc.body).html(), b]);

			$(this.rte.doc.body).html(data[0]);
			this.rte.selection.moveToBookmark(data[1]);
		}
	}

	this.canBack = function() {
		return this._prev.length;
	}

	this.canFwd = function() {
		return this._next.length;
	}

}
})(jQuery);/*
 * elRTE configuration
 *
 * @param doctype         - doctype for editor iframe
 * @param cssClass        - css class for editor
 * @param cssFiles        - array of css files, witch will inlude in iframe
 * @param height          - not used now (may be deleted in future)
 * @param lang            - interface language (requires file in i18n dir)
 * @param toolbar         - name of toolbar to load
 * @param absoluteURLs    - convert files and images urls to absolute or not
 * @param allowSource     - is source editing allowing
 * @param stripWhiteSpace - strip лишние whitespaces/tabs or not
 * @param styleWithCSS    - use style=... instead of strong etc.
 * @param fmAllow         - allow using file manger (elFinder)
 * @param fmOpen          - callback for open file manager
 * @param buttons         - object with pairs of buttons classes names and titles (when create new button, you have to add iys name here)
 * @param panels          - named groups of buttons
 * @param panelNames      - title of panels (required for one planned feature)
 * @param toolbars        - named redy to use toolbals (you may combine your own toolbar)
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 */
(function($) {
elRTE.prototype.options   = {
	doctype         : '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">',
	cssClass        : 'el-rte',
	cssfiles        : [],
	height          : null,
	resizable       : true,
	lang            : 'en',
	toolbar         : 'normal',
	absoluteURLs    : true,
	allowSource     : true,
	stripWhiteSpace : true,
	styleWithCSS    : false,
	fmAllow         : true,
	fmOpen          : null,
	/* if set all other tag will be removed */
	allowTags : [],
	/* if set this tags will be removed */
	denyTags : ['applet', 'base', 'basefont', 'bgsound', 'blink', 'body', 'col', 'colgroup', 'iframe', 'isindex', 'frameset', 'html', 'head', 'meta', 'marquee', 'noframes', 'noembed', 'o:p', 'title', 'xml'],
	denyAttr : [],
	/* on paste event this attributes will removed from pasted html */
	pasteDenyAttr : ['id', 'name', 'class', 'style', 'language', 'onclick', 'ondblclick', 'onhover', 'onkeup', 'onkeydown', 'onkeypress'],
	/* If false - all text nodes will be wrapped by paragraph tag */
	allowTextNodes : false,
	/* allow browser specific styles like -moz|-webkit|-o */
	allowBrowsersSpecStyles : false,
	/* allow paste content into editor */
	allowPaste : true,
	/* if true - only text will be pasted (not in ie) */
	pasteOnlyText : false,
	/* user replacement rules */
	replace : [],
	/* user restore rules */
	restore : [],
	pagebreak : '<div style="page-break-after: always;"></div>', //'<!-- pagebreak -->',
	buttons         : {
		'save'                : 'Save',
		'copy'                : 'Copy',
		'cut'                 : 'Cut',
		'css'                 : 'Css style and class',
		'paste'               : 'Paste',
		'pastetext'           : 'Paste only text',
		'pasteformattext'     : 'Paste formatted text',
		'removeformat'        : 'Clean format',
		'undo'                : 'Undo last action',
		'redo'                : 'Redo previous action',
		'bold'                : 'Bold',
		'italic'              : 'Italic',
		'underline'           : 'Underline',
		'strikethrough'       : 'Strikethrough',
		'superscript'         : 'Superscript',
		'subscript'           : 'Subscript',
		'justifyleft'         : 'Align left',
		'justifyright'        : 'Ailgn right',
		'justifycenter'       : 'Align center',
		'justifyfull'         : 'Align full',
		'indent'              : 'Indent',
		'outdent'             : 'Outdent',
		'rtl' : 'Right to left',
		'ltr' : 'Left to right',
		'forecolor'           : 'Font color',
		'hilitecolor'         : 'Background color',
		'formatblock'         : 'Format',
		'fontsize'            : 'Font size',
		'fontname'            : 'Font',
		'insertorderedlist'   : 'Ordered list',
		'insertunorderedlist' : 'Unordered list',
		'horizontalrule'      : 'Horizontal rule',
		'blockquote'          : 'Blockquote',
		'div'                 : 'Block element (DIV)',
		'link'                : 'Link',
		'unlink'              : 'Delete link',
		'anchor'              : 'Bookmark',
		'image'               : 'Image',
		'pagebreak'           : 'Page break',
		'smiley'              : 'Smiley',
		'flash'               : 'Flash',
		'table'               : 'Table',
		'tablerm'             : 'Delete table',
		'tableprops'          : 'Table properties',
		'tbcellprops'         : 'Table cell properties',
		'tbrowbefore'         : 'Insert row before',
		'tbrowafter'          : 'Insert row after',
		'tbrowrm'             : 'Delete row',
		'tbcolbefore'         : 'Insert column before',
		'tbcolafter'          : 'Insert column after',
		'tbcolrm'             : 'Delete column',
		'tbcellsmerge'        : 'Merge table cells',
		'tbcellsplit'         : 'Split table cell',
		'docstructure'        : 'Toggle display document structure',
		'elfinder'            : 'Open file manager',
		'fullscreen'          : 'Toggle full screen mode',
		'nbsp'                : 'Non breakable space',
		'stopfloat'           : 'Stop element floating',
		'about'               : 'About this software'
	},
	panels      : {
		save       : ['save'],
		copypaste  : ['copy', 'cut', 'paste', 'pastetext', 'pasteformattext', 'removeformat', 'docstructure'],
		undoredo   : ['undo', 'redo'],
		style      : ['bold', 'italic', 'underline', 'strikethrough', 'subscript', 'superscript'],
		colors     : ['forecolor', 'hilitecolor'],
		alignment  : ['justifyleft', 'justifycenter', 'justifyright', 'justifyfull'],
		indent     : ['outdent', 'indent'],
		format     : ['formatblock', 'fontsize', 'fontname'],
		lists      : ['insertorderedlist', 'insertunorderedlist'],
		elements   : ['horizontalrule', 'blockquote', 'div', 'stopfloat', 'css', 'nbsp', 'smiley', 'pagebreak'],
		direction  : ['ltr', 'rtl'],
		links      : ['link', 'unlink', 'anchor'],
		images     : ['image'],
		media      : ['image', 'flash'],
		tables     : ['table', 'tableprops', 'tablerm',  'tbrowbefore', 'tbrowafter', 'tbrowrm', 'tbcolbefore', 'tbcolafter', 'tbcolrm', 'tbcellprops', 'tbcellsmerge', 'tbcellsplit'],
		elfinder   : ['elfinder'],
		fullscreen : ['fullscreen', 'about']
	},
	toolbars    : {
		tiny     : ['style'],
		compact  : ['save', 'undoredo', 'style', 'alignment', 'lists', 'links', 'fullscreen'],
		normal   : ['save', 'copypaste', 'undoredo', 'style', 'alignment', 'colors', 'indent', 'lists', 'links', 'elements', 'images', 'fullscreen'],
		complete : ['save', 'copypaste', 'undoredo', 'style', 'alignment', 'colors', 'format', 'indent', 'lists', 'links', 'elements', 'media', 'fullscreen'],
		maxi     : ['save', 'copypaste', 'undoredo', 'elfinder', 'style', 'alignment', 'direction', 'colors', 'format', 'indent', 'lists', 'links', 'elements', 'media', 'tables', 'fullscreen'],
		eldorado : ['save', 'copypaste', 'elfinder', 'undoredo', 'style', 'alignment', 'colors', 'format', 'indent', 'lists', 'links', 'elements', 'media', 'tables', 'fullscreen']

	},
	panelNames : {
		save      : 'Save',
		copypaste : 'Copy/Pase',
		undoredo  : 'Undo/Redo',
		style     : 'Text styles',
		colors    : 'Colors',
		alignment : 'Alignment',
		indent    : 'Indent/Outdent',
		format    : 'Text format',
		lists     : 'Lists',
		elements  : 'Misc elements',
		direction : 'Script direction',
		links     : 'Links',
		images    : 'Images',
		media     : 'Media',
		tables    : 'Tables',
		elfinder  : 'File manager (elFinder)'
	}
};
})(jQuery);
/**
 * @class selection  - elRTE utils for working with text selection
 *
 * @param  elRTE  rte  объект-редактор
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 **/
(function($) {
elRTE.prototype.selection = function(rte) {
	this.rte      = rte;
	var self      = this;
	this.w3cRange = null;
	var start, end, node, bm;

	$(this.rte.doc)
		.keyup(function(e) {
			if (e.ctrlKey || e.metaKey || (e.keyCode >= 8 && e.keyCode <= 13) || (e.keyCode>=32 && e.keyCode<= 40) || e.keyCode == 46 || (e.keyCode >=96 && e.keyCode <= 111)) {
				self.cleanCache();
			}
		})
		.mousedown(function(e) {
			// self.rte.log(e)
			if (e.target.nodeName == 'HTML') {
				start = self.rte.doc.body;
			} else {
				start = e.target;
			}
			end   = node = null;
		})
		.mouseup(function(e) {
			if (e.target.nodeName == 'HTML') {
				end = self.rte.doc.body;
			} else {
				end = e.target;
			}
			end  = e.target;
			node = null;
		}).click();

	/**
	 * возвращает selection
	 *
	 * @return  Selection
	 **/
	function selection() {
		return self.rte.window.getSelection ? self.rte.window.getSelection() : self.rte.window.document.selection;
	}

	/**
	 * Вспомогательная функция
	 * Возвращает самого верхнего родителя, отвечающего условию - текущая нода - его единственная непустая дочерняя нода
	 *
	 * @param   DOMElement  n нода, для которой ищем родителя
	 * @param   DOMElement  p если задана - нода, выше которой не поднимаемся
	 * @param   String      s строна поиска (left||right||null)
	 * @return  DOMElement
	 **/
	function realSelected(n, p, s) {
		while (n.nodeName != 'BODY' && n.parentNode && n.parentNode.nodeName != 'BODY' && (p ? n!== p && n.parentNode != p : 1) && ((s=='left' && self.rte.dom.isFirstNotEmpty(n)) || (s=='right' && self.rte.dom.isLastNotEmpty(n)) || (self.rte.dom.isFirstNotEmpty(n) && self.rte.dom.isLastNotEmpty(n))) ) {
			n = n.parentNode;
		}
		return n;
	}

	/**
	 * Возвращает TRUE, если выделение "схлопнуто"
	 *
	 * @return  bool
	 **/
	this.collapsed = function() {
		return this.getRangeAt().isCollapsed();
	}

	/**
	 * "Схлопывает" выделение
	 *
	 * @param   bool  toStart  схлопнуть к начальной точке
	 * @return  void
	 **/
	this.collapse = function(st) {
		var s = selection(),
			r = this.getRangeAt();
		r.collapse(st?true:false);
		if (!$.browser.msie) {
			s.removeAllRanges();
			s.addRange(r);

		}
		return this;
	}

	/**
	 * Возвращает TextRange
	 * Для нормальных браузеров - нативный range
	 * для "самизнаетечего" - эмуляцию w3c range
	 *
	 * @return  range|w3cRange
	 **/
	this.getRangeAt = function(updateW3cRange) {
		if (this.rte.browser.msie) {
			if (!this.w3cRange) {
				this.w3cRange = new this.rte.w3cRange(this.rte);
			}
			updateW3cRange && this.w3cRange.update();
			return this.w3cRange;
		}

		var s = selection();
		var r = s.rangeCount > 0 ? s.getRangeAt(0) : this.rte.doc.createRange();
		r.getStart = function() {
			return this.startContainer.nodeType==1
				? this.startContainer.childNodes[Math.min(this.startOffset, this.startContainer.childNodes.length-1)]
				: this.startContainer;
		}

		r.getEnd = function() {
			return this.endContainer.nodeType==1
				? this.endContainer.childNodes[ Math.min(this.startOffset == this.endOffset ? this.endOffset : this.endOffset-1, this.endContainer.childNodes.length-1)]
				: this.endContainer;
		}
		r.isCollapsed = function() {
			return this.collapsed;
		}
		return r;
	}

	this.saveIERange = function() {
		if ($.browser.msie) {
			bm = this.getRangeAt().getBookmark();
		}
	}

	this.restoreIERange = function() {
		$.browser.msie && bm && this.getRangeAt().moveToBookmark(bm);
	}

	this.cloneContents = function() {
		var n = this.rte.dom.create('div'), r, c, i;
		if ($.browser.msie) {
			try {
				r = this.rte.window.document.selection.createRange();
			} catch(e) {
				r = this.rte.doc.body.createTextRange();
			}
			$(n).html(r.htmlText);
		} else {
			c = this.getRangeAt().cloneContents();
			for (i=0; i<c.childNodes.length; i++) {
				n.appendChild(c.childNodes[i].cloneNode(true));
			}
		}
		return n;
	}

	/**
	 * Выделяет ноды
	 *
	 * @param   DOMNode  s  нода начала выделения
	 * @param   DOMNode  e  нода конца выделения
	 * @return  selection
	 **/
	this.select = function(s, e) {
		e = e||s;

		if (this.rte.browser.msie) {
			var r  = this.rte.doc.body.createTextRange(),
				r1 = r.duplicate(),
				r2 = r.duplicate();

			r1.moveToElementText(s);
			r2.moveToElementText(e);
			r.setEndPoint('StartToStart', r1);
			r.setEndPoint('EndToEnd',     r2);
			r.select();
		} else {

			var sel = selection(),
				r = this.getRangeAt();
			r.setStartBefore(s);
			r.setEndAfter(e);
			sel.removeAllRanges();
			sel.addRange(r);
		}
		return this.cleanCache();
	}

	/**
	 * Выделяет содержимое ноды
	 *
	 * @param   Element  n  нода
	 * @return  selection
	 **/
	this.selectContents = function(n) {
		var r = this.getRangeAt();
		if (n && n.nodeType == 1) {
			if (this.rte.browser.msie) {
				r.range();
				r.r.moveToElementText(n.parentNode);
				r.r.select();
			} else {
				try {
					r.selectNodeContents(n);
				} catch (e) {
					return this.rte.log('unable select node contents '+n);
				}
				var s = selection();
				s.removeAllRanges();
				s.addRange(r);
			}
		}
		return this;
	}

	this.deleteContents = function() {
		if (!$.browser.msie) {
			this.getRangeAt().deleteContents();
		}
		return this;
	}

	/**
	 * Вставляет ноду в текущее выделение
	 *
	 * @param   Element  n  нода
	 * @return  selection
	 **/
	this.insertNode = function(n, collapse) {
		if (collapse && !this.collapsed()) {
			this.collapse();
		}

		if (this.rte.browser.msie) {
			var html = n.nodeType == 3 ? n.nodeValue : $(this.rte.dom.create('span')).append($(n)).html();
			 var r = this.getRangeAt();
			r.insertNode(html);
		} else {
			var r = this.getRangeAt();
			r.insertNode(n);
			r.setStartAfter(n);
			r.setEndAfter(n);
			var s = selection();
			s.removeAllRanges();
			s.addRange(r);
		}
		return this.cleanCache();
	}

	/**
	 * Вставляет html в текущее выделение
	 *
	 * @param   Element  n  нода
	 * @return  selection
	 **/
	this.insertHtml = function(html, collapse) {
		if (collapse && !this.collapsed()) {
			this.collapse();
		}

		if (this.rte.browser.msie) {
			this.getRangeAt().range().pasteHTML(html);
		} else {
			var n = $(this.rte.dom.create('span')).html(html||'').get(0);
			this.insertNode(n);
			$(n).replaceWith($(n).html());
		}
		return this.cleanCache();
	}

	/**
	 * Вставляет ноду в текущее выделение
	 *
	 * @param   Element  n  нода
	 * @return  selection
	 **/
	this.insertText = function(text, collapse) {
		var n = this.rte.doc.createTextNode(text);
		return this.insertHtml(n.nodeValue);
	}

	this.getBookmark = function() {
		this.rte.window.focus();
		var r, r1, r2, _s, _e,
			s = this.rte.dom.createBookmark(),
			e = this.rte.dom.createBookmark();



		if ($.browser.msie) {
			try {
				r = this.rte.window.document.selection.createRange();
			} catch(e) {
				r = this.rte.doc.body.createTextRange();
			}

			if (r.item) {
				var n = r.item(0);
				r = this.rte.doc.body.createTextRange();
				r.moveToElementText(n);
			}

			r1 = r.duplicate();
			r2 = r.duplicate();
			_s = this.rte.dom.create('span');
			_e = this.rte.dom.create('span');

			_s.appendChild(s);
			_e.appendChild(e);

			r1.collapse(true);
			r1.pasteHTML(_s.innerHTML);
			r2.collapse(false);
			r2.pasteHTML(_e.innerHTML);
		} else {
				var sel = selection();
				var r = sel.rangeCount > 0 ? sel.getRangeAt(0) : this.rte.doc.createRange();

			// r  = this.getRangeAt();
			r1 = r.cloneRange();
			r2 = r.cloneRange();

			// this.insertNode(this.rte.dom.create('hr'))
			// return
			r2.collapse(false);
			r2.insertNode(e);
			r1.collapse(true);
			r1.insertNode(s);
			this.select(s, e);
		}

		return [s.id, e.id];
	}

	this.moveToBookmark = function(b) {
		this.rte.window.focus();

		if (b && b.length==2) {
			var s = this.rte.doc.getElementById(b[0]),
				e = this.rte.doc.getElementById(b[1]),
				sel, r;
			if (s && e) {
				this.select(s, e);
				if (this.rte.dom.next(s) == e) {
					this.collapse(true);
				}
				if (!$.browser.msie) {
					sel = selection();
					r = sel.rangeCount > 0 ? sel.getRangeAt(0) : this.rte.doc.createRange();
					sel.removeAllRanges();
					sel.addRange(r);
				}

				s.parentNode.removeChild(s);
				e.parentNode.removeChild(e);
			}
		}
		return this;
	}

	this.removeBookmark = function(b) {
		this.rte.window.focus();
		if (b.length==2) {
			var s = this.rte.doc.getElementById(b[0]),
				e = this.rte.doc.getElementById(b[1]);
			if (s && e) {
				s.parentNode.removeChild(s);
				e.parentNode.removeChild(e);
			}
		}
	}

	/**
	 * Очищает кэш
	 *
	 * @return  selection
	 **/
	this.cleanCache = function() {
		start = end = node = null;
		return this;
	}


	/**
	 * Возвращает ноду начала выделения
	 *
	 * @return  DOMElement
	 **/
	this.getStart = function() {
		if (!start) {
			var r = this.getRangeAt();
			start = r.getStart();
		}
		return start;
	}

	/**
	 * Возвращает ноду конца выделения
	 *
	 * @return  DOMElement
	 **/
	this.getEnd = function() {
		if (!end) {
			var r = this.getRangeAt();
			end = r.getEnd();
		}
		return end;
	}

	/**
	 * Возвращает выбраную ноду (общий контейнер всех выбранных нод)
	 *
	 * @return  Element
	 **/
	this.getNode = function() {
		if (!node) {
			node = this.rte.dom.findCommonAncestor(this.getStart(), this.getEnd());
		}
		return node;
	}


	/**
	 * Возвращает массив выбранных нод
	 *
	 * @param   Object  o  параметры получения и обработки выбраных нод
	 * @return  Array
	 **/
	this.selected = function(o) {
		var opts = {
			collapsed : false,  // вернуть выделение, даже если оно схлопнуто
			blocks    : false,  // блочное выделение
			filter    : false,  // фильтр результатов
			wrap      : 'text', // что оборачиваем
			tag       : 'span'  // во что оборачиваем
		}
		opts = $.extend({}, opts, o);

		// блочное выделение - ищем блочную ноду, но не таблицу
		if (opts.blocks) {
			var n  = this.getNode(), _n = null;
			if (_n = this.rte.dom.selfOrParent(n, 'selectionBlock') ) {
				return [_n];
			}
		}

		var sel    = this.selectedRaw(opts.collapsed, opts.blocks);
		var ret    = [];
		var buffer = [];
		var ndx    = null;

		// оборачиваем ноды в буффере
		function wrap() {

			function allowParagraph() {
				for (var i=0; i < buffer.length; i++) {
					if (buffer[i].nodeType == 1 && (self.rte.dom.selfOrParent(buffer[i], /^P$/) || $(buffer[i]).find('p').length>0)) {
						return false;
					}
				};
				return true;
			}

			if (buffer.length>0) {
				var tag  = opts.tag == 'p' && !allowParagraph() ? 'div' : opts.tag;
				var n    = self.rte.dom.wrap(buffer, tag);
				ret[ndx] = n;
				ndx      = null;
				buffer   = [];
			}
		}

		// добавляем ноды в буффер
		function addToBuffer(n) {
			if (n.nodeType == 1) {
				if (/^(THEAD|TFOOT|TBODY|COL|COLGROUP|TR)$/.test(n.nodeName)) {
					$(n).find('td,th').each(function() {
						var tag = opts.tag == 'p' && $(this).find('p').length>0 ? 'div' : opts.tag;
						var n = self.rte.dom.wrapContents(this, tag);
						return ret.push(n);
					})
				} else if (/^(CAPTION|TD|TH|LI|DT|DD)$/.test(n.nodeName)) {
					var tag = opts.tag == 'p' && $(n).find('p').length>0 ? 'div' : opts.tag;
					var n = self.rte.dom.wrapContents(n, tag);
					return ret.push(n);
				}
			}
			var prev = buffer.length>0 ? buffer[buffer.length-1] : null;
			if (prev && prev != self.rte.dom.prev(n)) {
				wrap();
			}
			buffer.push(n);
			if (ndx === null) {
				ndx = ret.length;
				ret.push('dummy'); // заглушка для оборачиваемых элементов
			}
		}

		if (sel.nodes.length>0) {

			for (var i=0; i < sel.nodes.length; i++) {
				var n = sel.nodes[i];
					// первую и посл текстовые ноды разрезаем, если необходимо
					 if (n.nodeType == 3 && (i==0 || i == sel.nodes.length-1) && $.trim(n.nodeValue).length>0) {
						if (i==0 && sel.so>0) {
							n = n.splitText(sel.so);
						}
						if (i == sel.nodes.length-1 && sel.eo>0) {
							n.splitText(i==0 && sel.so>0 ? sel.eo - sel.so : sel.eo);
						}
					}

					switch (opts.wrap) {
						// оборачиваем только текстовые ноды с br
						case 'text':
							if ((n.nodeType == 1 && n.nodeName == 'BR') || (n.nodeType == 3 && $.trim(n.nodeValue).length>0)) {
								addToBuffer(n);
							} else if (n.nodeType == 1) {
								ret.push(n);
							}
							break;
						// оборачиваем все инлайн элементы
						case 'inline':
							if (this.rte.dom.isInline(n)) {
								addToBuffer(n);
							} else if (n.nodeType == 1) {

								ret.push(n);
							}
							break;
						// оборачиваем все
						case 'all':
							if (n.nodeType == 1 || !this.rte.dom.isEmpty(n)) {
								addToBuffer(n);
							}
							break;
						// ничего не оборачиваем
						default:
							if (n.nodeType == 1 || !this.rte.dom.isEmpty(n)) {
								ret.push(n);
							}
					}
			};
			wrap();
		}

		if (ret.length) {
			this.rte.window.focus();

			this.select(ret[0], ret[ret.length-1]);
		}
		return opts.filter ? this.rte.dom.filter(ret, opts.filter) : ret;
	}

	this.dump = function(ca, s, e, so, eo) {
		var r = this.getRangeAt();
		this.rte.log('commonAncestorContainer');
		this.rte.log(ca || r.commonAncestorContainer);
		// this.rte.log('commonAncestorContainer childs num')
		// this/rte.log((ca||r.commonAncestorContainer).childNodes.length)
		this.rte.log('startContainer');
		this.rte.log(s || r.startContainer);
		this.rte.log('startOffset: '+(so>=0 ? so : r.startOffset));
		this.rte.log('endContainer');
		this.rte.log(e||r.endContainer);
		this.rte.log('endOffset: '+(eo>=0 ? eo : r.endOffset));
	}

	/**
	 * Возвращает массив выбранных нод, как есть
	 *
	 * @param   bool           возвращать если выделение схлопнуто
	 * @param   bool           "блочное" выделение (текстовые ноды включаются полностью, не зависимо от offset)
	 * @return  Array
	 **/
	this.selectedRaw = function(collapsed, blocks) {
		var res = {so : null, eo : null, nodes : []};
		var r   = this.getRangeAt(true);
		var ca  = r.commonAncestorContainer;
		var s, e;  // start & end nodes
		var sf  = false; // start node fully selected
		var ef  = false; // end node fully selected

		// возвращает true, если нода не текстовая или выделена полностью
		function isFullySelected(n, s, e) {
			if (n.nodeType == 3) {
				e = e>=0 ? e : n.nodeValue.length;
				return (s==0 && e==n.nodeValue.length) || $.trim(n.nodeValue).length == $.trim(n.nodeValue.substring(s, e)).length;
			}
			return true;
		}

		// возвращает true, если нода пустая или в ней не выделено ни одного непробельного символа
		function isEmptySelected(n, s, e) {
			if (n.nodeType == 1) {
				return self.rte.dom.isEmpty(n);
			} else if (n.nodeType == 3) {
				return $.trim(n.nodeValue.substring(s||0, e>=0 ? e : n.nodeValue.length)).length == 0;
			}
			return true;
		}


		//this.dump()
		// начальная нода
		if (r.startContainer.nodeType == 1) {
			if (r.startOffset<r.startContainer.childNodes.length) {
				s = r.startContainer.childNodes[r.startOffset];
				res.so = s.nodeType == 1 ? null : 0;
			} else {
				s = r.startContainer.childNodes[r.startOffset-1];
				res.so = s.nodeType == 1 ? null : s.nodeValue.length;
			}
		} else {
			s = r.startContainer;
			res.so = r.startOffset;
		}

		// выделение схлопнуто
		if (r.collapsed) {
			if (collapsed) {
				//  блочное выделение
				if (blocks) {
					s = realSelected(s);
					if (!this.rte.dom.isEmpty(s) || (s = this.rte.dom.next(s))) {
						res.nodes = [s];
					}

					// добавляем инлайн соседей
					if (this.rte.dom.isInline(s)) {
						res.nodes = this.rte.dom.toLineStart(s).concat(res.nodes, this.rte.dom.toLineEnd(s));
					}

					// offset для текстовых нод
					if (res.nodes.length>0) {
						res.so = res.nodes[0].nodeType == 1 ? null : 0;
						res.eo = res.nodes[res.nodes.length-1].nodeType == 1 ? null : res.nodes[res.nodes.length-1].nodeValue.length;
					}

				} else if (!this.rte.dom.isEmpty(s)) {
					res.nodes = [s];
				}

			}
			return res;
		}

		// конечная нода
		if (r.endContainer.nodeType == 1) {
			e = r.endContainer.childNodes[r.endOffset-1];
			res.eo = e.nodeType == 1 ? null : e.nodeValue.length;
		} else {
			e = r.endContainer;
			res.eo = r.endOffset;
		}
		// this.rte.log('select 1')
		//this.dump(ca, s, e, res.so, res.eo)

		// начальная нода выделена полностью - поднимаемся наверх по левой стороне
		if (s.nodeType == 1 || blocks || isFullySelected(s, res.so, s.nodeValue.length)) {
//			this.rte.log('start text node is fully selected')
			s = realSelected(s, ca, 'left');
			sf = true;
			res.so = s.nodeType == 1 ? null : 0;
		}
		// конечная нода выделена полностью - поднимаемся наверх по правой стороне
		if (e.nodeType == 1 || blocks || isFullySelected(e, 0,  res.eo)) {
//			this.rte.log('end text node is fully selected')
			e = realSelected(e, ca, 'right');
			ef = true;
			res.eo = e.nodeType == 1 ? null : e.nodeValue.length;
		}

		// блочное выделение - если ноды не элементы - поднимаемся к родителю, но ниже контейнера
		if (blocks) {
			if (s.nodeType != 1 && s.parentNode != ca && s.parentNode.nodeName != 'BODY') {
				s = s.parentNode;
				res.so = null;
			}
			if (e.nodeType != 1 && e.parentNode != ca && e.parentNode.nodeName != 'BODY') {
				e = e.parentNode;
				res.eo = null;
			}
		}

		// если контенер выделен полностью, поднимаемся наверх насколько можно
		if (s.parentNode == e.parentNode && s.parentNode.nodeName != 'BODY' && (sf && this.rte.dom.isFirstNotEmpty(s)) && (ef && this.rte.dom.isLastNotEmpty(e))) {
//			this.rte.log('common parent')
			s = e = s.parentNode;
			res.so = s.nodeType == 1 ? null : 0;
			res.eo = e.nodeType == 1 ? null : e.nodeValue.length;
		}
		// начальная нода == конечной ноде
		if (s == e) {
//			this.rte.log('start is end')
			if (!this.rte.dom.isEmpty(s)) {
				res.nodes.push(s);
			}
			return res;
		}
		 // this.rte.log('start 2')
		  //this.dump(ca, s, e, res.so, res.eo)

		// находим начальную и конечную точки - ноды из иерархии родителей начальной и конечно ноды, у которых родитель - контейнер
		var sp = s;
		while (sp.nodeName != 'BODY' && sp.parentNode !== ca && sp.parentNode.nodeName != 'BODY') {
			sp = sp.parentNode;
		}
		//this.rte.log(s.nodeName)
		// this.rte.log('start point')
		// this.rte.log(sp)

		var ep = e;
//		this.rte.log(ep)
		while (ep.nodeName != 'BODY' && ep.parentNode !== ca && ep.parentNode.nodeName != 'BODY') {
			// this.rte.log(ep)
			ep = ep.parentNode;
		}
		// this.rte.log('end point')
		// this.rte.log(ep)


		//  если начальная нода не пустая - добавляем ее
		if (!isEmptySelected(s, res.so, s.nodeType==3 ? s.nodeValue.length : null)) {
			res.nodes.push(s);
		}
		// поднимаемся от начальной ноды до начальной точки
		var n = s;
		while (n !== sp) {
			var _n = n;
			while ((_n = this.rte.dom.next(_n))) {
					res.nodes.push(_n);
			}
			n = n.parentNode;
		}
		// от начальной точки до конечной точки
		n = sp;
		while ((n = this.rte.dom.next(n)) && n!= ep ) {
//			this.rte.log(n)
			res.nodes.push(n);
		}
		// поднимаемся от конечной ноды до конечной точки, результат переворачиваем
		var tmp = [];
		n = e;
		while (n !== ep) {
			var _n = n;
			while ((_n = this.rte.dom.prev(_n))) {
				tmp.push(_n);
			}
			n = n.parentNode;
		}
		if (tmp.length) {
			res.nodes = res.nodes.concat(tmp.reverse());
		}
		//  если конечная нода не пустая и != начальной - добавляем ее
		if (!isEmptySelected(e, 0, e.nodeType==3 ? res.eo : null)) {
			res.nodes.push(e);
		}

		if (blocks) {
			// добавляем инлайн соседей слева
			if (this.rte.dom.isInline(s)) {
				res.nodes = this.rte.dom.toLineStart(s).concat(res.nodes);
				res.so    = res.nodes[0].nodeType == 1 ? null : 0;
			}
			// добавляем инлайн соседей справа
			if (this.rte.dom.isInline(e)) {
				res.nodes = res.nodes.concat(this.rte.dom.toLineEnd(e));
				res.eo    = res.nodes[res.nodes.length-1].nodeType == 1 ? null : res.nodes[res.nodes.length-1].nodeValue.length;
			}
		}

		// все радуются! :)
		return res;
	}

}

})(jQuery);/**
 * @class elRTE User interface controller
 *
 * @param  elRTE  rte объект-редактор
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @todo: this.domElem.removeClass('disabled') - move to ui.update;
 * @todo: add dom and selection as button members
 * Copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui = function(rte) {
	this.rte      = rte;
	this._buttons = [];
	var self      = this,
		tb        = this.rte.options.toolbars[rte.options.toolbar && rte.options.toolbars[rte.options.toolbar] ? rte.options.toolbar : 'normal'],
		tbl       = tb.length,
		p, pname, pl, n, c, b, i;

	// add prototype to all buttons
	for (i in this.buttons) {
		if (this.buttons.hasOwnProperty(i) && i != 'button') {
			this.buttons[i].prototype = this.buttons.button.prototype;
		}
	}

	// create buttons and put on toolbar
	while (tbl--) {
		pname = tb[tbl];
		p = $('<ul class="panel-'+pname+(tbl == 0 ? ' first' : '')+'" />').prependTo(this.rte.toolbar);
		p.bind('mousedown', function(e) {
			e.preventDefault();
		})
		pl = this.rte.options.panels[pname].length;
		while (pl--) {
			n = this.rte.options.panels[pname][pl];
			c = this.buttons[n] || this.buttons.button;
			this._buttons.push((b = new c(this.rte, n)));
			p.prepend(b.domElem);
		}
	}

	this.update();

	this.disable = function() {
		$.each(self._buttons, function() {
			!this.active && this.domElem.addClass('disabled');
		});
	}

}

/**
 * Обновляет кнопки - вызывает метод update() для каждой кнопки
 *
 * @return void
 **/
elRTE.prototype.ui.prototype.update = function(cleanCache) {
	cleanCache && this.rte.selection.cleanCache();
	var n    = this.rte.selection.getNode(),
		p    = this.rte.dom.parents(n, '*'),
		rtl = this.rte.rtl,
		sep  = rtl ? ' &laquo; ' : ' &raquo; ',
		path = '', name, i;

	function _name(n) {
		var name = n.nodeName.toLowerCase();
		n = $(n)
		if (name == 'img') {
			if (n.hasClass('elrte-media')) {
				name = 'media';
			} else if (n.hasClass('elrte-google-maps')) {
				name = 'google map';
			} else if (n.hasClass('elrte-yandex-maps')) {
				name = 'yandex map';
			} else if (n.hasClass('elrte-pagebreak')) {
				name = 'pagebreak';
			}
		}
		return name;
	}

	if (n && n.nodeType == 1 && n.nodeName != 'BODY') {
		p.unshift(n);
	}

	if (!rtl) {
		p = p.reverse();
	}

	for (i=0; i < p.length; i++) {
		path += (i>0 ? sep : '')+_name(p[i]);
	}

	this.rte.statusbar.html(path);
	$.each(this._buttons, function() {
		this.update();
	});
	this.rte.window.focus();
}



elRTE.prototype.ui.prototype.buttons = {

	/**
	 * @class кнопка на toolbar редактора
	 * реализует поведение по умолчанию и является родителем для других кнопок
	 *
	 * @param  elRTE  rte   объект-редактор
	 * @param  String name  название кнопки (команда исполняемая document.execCommand())
	 **/
	button : function(rte, name) {
		var self     = this;
		this.rte     = rte;
		this.active = false;
		this.name    = name;
		this.val     = null;
		this.domElem = $('<li style="-moz-user-select:-moz-none" class="'+name+' rounded-3" name="'+name+'" title="'+this.rte.i18n(this.rte.options.buttons[name] || name)+'" unselectable="on" />')
			.hover(
				function() { $(this).addClass('hover'); },
				function() { $(this).removeClass('hover'); }
			)
			.click( function(e) {
				e.stopPropagation();
				e.preventDefault();
				if (!$(this).hasClass('disabled')) {
					self.command();
				}
				self.rte.window.focus();
			});
	}
}

/**
 * Обработчик нажатия на кнопку на тулбаре. Выполнение команды или открытие окна|меню и тд
 *
 * @return void
 **/
elRTE.prototype.ui.prototype.buttons.button.prototype.command = function() {
	this.rte.history.add();
	try {
		this.rte.doc.execCommand(this.name, false, this.val);
	} catch(e) {
		return this.rte.log('commands failed: '+this.name);
	}

	this.rte.ui.update(true);
}

/**
 * Обновляет состояние кнопки
 *
 * @return void
 **/
elRTE.prototype.ui.prototype.buttons.button.prototype.update = function() {
	try {
		if (!this.rte.doc.queryCommandEnabled(this.name)) {
			return this.domElem.addClass('disabled');
		} else {
			this.domElem.removeClass('disabled');
		}
	} catch (e) {
		return;
	}
	try {
		if (this.rte.doc.queryCommandState(this.name)) {
			this.domElem.addClass('active');
		} else {
			this.domElem.removeClass('active');
		}
	} catch (e) { }
}

})(jQuery);/*
 * Misc utils for elRTE
 *
 * @param Object rte - editor
 * @todo Подумать, что из этого реально нужно и навести порядок. Возможно часть перенести в ellib
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 */
(function($) {
elRTE.prototype.utils = function(rte) {
	this.rte     = rte;
	this.url     = null;
	// domo arigato, Steave, http://blog.stevenlevithan.com/archives/parseuri
	this.reg     = /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?)(((\/(?:[^?#](?![^?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/;
	this.baseURL = '';
	this.path    = '';
	/**
	 * entities map
	 **/
	this.entities = {'&' : '&amp;', '"' : '&quot;', '<' : '&lt;', '>' : '&gt;'};
	/**
	 * entities regexp
	 **/
	this.entitiesRegExp = /[<>&\"]/g;
	/**
	 * media info
	 **/
	this.media = [{
			type     : 'application/x-shockwave-flash',
			classid  : ['clsid:d27cdb6e-ae6d-11cf-96b8-444553540000'],
			codebase : 'http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0'
		}, {
			type     : 'application/x-director',
			classid  : ['clsid:166b1bca-3f9c-11cf-8075-444553540000'],
			codebase : 'http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=8,5,1,0'
		}, {
			type     : 'application/x-mplayer2',
			classid  : ['clsid:6bf52a52-394a-11d3-b153-00c04f79faa6', 'clsid:22d6f312-b0f6-11d0-94ab-0080c74c7e95', 'clsid:05589fa1-c356-11ce-bf01-00aa0055595a'],
			codebase : 'http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=5,1,52,701'
		}, {
			type     : 'video/quicktime',
			classid  : ['clsid:02bf25d5-8c17-4b23-bc80-d3488abddc6b'],
			codebase : 'http://www.apple.com/qtactivex/qtplugin.cab#version=6,0,2,0'
		}, {
			type     : 'audio/x-pn-realaudio-plugin',
			classid  : ['clsid:cfcdaa03-8be4-11cf-b84b-0020afbbccfa'],
			codebase : 'http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0'
		}];

	// rgb color regexp
	this.rgbRegExp = /\s*rgb\s*?\(\s*?([0-9]+)\s*?,\s*?([0-9]+)\s*?,\s*?([0-9]+)\s*?\)\s*/i;
	// regexp to detect color in border/background properties
	this.colorsRegExp = /aqua|black|blue|fuchsia|gray|green|lime|maroon|navy|olive|orange|purple|red|silver|teal|white|yellow|rgb\s*\([^\)]+\)/i;
	// web safe colors
	this.colors = {
		aqua    : '#00ffff',
		black   : '#000000',
		blue    : '#0000ff',
		fuchsia : '#ff00ff',
		gray    : '#808080',
		green   : '#008000',
		lime    : '#00ff00',
		maroon  : '#800000',
		navy    : '#000080',
		olive   : '#808000',
		orange  : '#ffa500',
		purple  : '#800080',
		red     : '#ff0000',
		silver  : '#c0c0c0',
		teal    : '#008080',
		white   : '#fffffff',
		yellow  : '#ffff00'
	}

	var self     = this;

	this.rgb2hex = function(str) {
		return this.color2Hex(''+str)
	}

	this.toPixels = function(num) {
		var m = num.match(/([0-9]+\.?[0-9]*)\s*(px|pt|em|%)/);
		if (m) {
			num  = m[1];
			unit = m[2];
		}
		if (num[0] == '.') {
			num = '0'+num;
		}
		num = parseFloat(num);

		if (isNaN(num)) {
			return '';
		}
		var base = parseInt($(document.body).css('font-size')) || 16;
		switch (unit) {
			case 'em': return parseInt(num*base);
			case 'pt': return parseInt(num*base/12);
			case '%' : return parseInt(num*base/100);
		}
		return num;
	}

	// TODO: add parse rel path ../../etc
	this.absoluteURL = function(url) {
		!this.url && this._url();
		url = $.trim(url);
		if (!url) {
			return '';
		}
		// ссылки на якоря не переводим в абс
		if (url[0] == '#') {
			return url;
		}
		var u = this.parseURL(url);

		if (!u.host && !u.path && !u.anchor) {
			//this.rte.log('Invalid URL: '+url)
			return '';
		}
		if (!this.rte.options.absoluteURLs) {
			return url;
		}
		if (u.protocol) {
			//this.rte.log('url already absolute: '+url);
			return url;
		}
		if (u.host && (u.host.indexOf('.')!=-1 || u.host == 'localhost')) {
			//this.rte.log('no protocol');
			return this.url.protocol+'://'+url;
		}
		if (url[0] == '/') {
			url = this.baseURL+url;
		} else {
			if (url.indexOf('./') == 0) {
				url = url.substring(2);
			}
			url = this.baseURL+this.path+url;
		}
		return url;
	}

	this.parseURL = function(url) {
		var u   = url.match(this.reg);
		var ret = {};
		$.each(["source","protocol","authority","userInfo","user","password","host","port","relative","path","directory","file","query","anchor"], function(i) {
			ret[this] = u[i];
		});
		if (!ret.host.match(/[a-z0-9]/i)) {
			ret.host = '';
		}
		return ret;
	}

	this.trimEventCallback = function(c) {
		c = c ? c.toString() : '';
		return $.trim(c.replace(/\r*\n/mg, '').replace(/^function\s*on[a-z]+\s*\(\s*event\s*\)\s*\{(.+)\}$/igm, '$1'));
	}

	this._url = function() {
		this.url     = this.parseURL(window.location.href);
		this.baseURL = this.url.protocol+'://'+(this.url.userInfo ?  parts.userInfo+'@' : '')+this.url.host+(this.url.port ? ':'+this.url.port : '');
		this.path    = !this.url.file ? this.url.path : this.url.path.substring(0, this.url.path.length - this.url.file.length);
	}


	/**
	 * Create object (map) from array
	 *
	 * @param   Array
	 * @return  Object
	 **/
	this.makeObject = function(o) {
		var m = {};
		$.each(o, function(i, e) {
			m[e] = e;
		});
		return m;
	}

	/**
	 * Encode entities in string
	 *
	 * @param   String
	 * @return  String
	 **/
	this.encode = function(s) {
		var e = this.entities;
		return (''+s).replace(this.entitiesRegExp, function(c) {
			return e[c];
		});
	}

	/**
	 * Decode entities in string
	 *
	 * @param   String
	 * @return  String
	 **/
	this.decode = function(s) {
		return $('<div/>').html(s||'').text();
	}

	/**
	 * Parse style string into object
	 *
	 * @param   String
	 * @return  Object
	 **/
	this.parseStyle = function(s) {
		var st = {}, a = this.rte.options.allowBrowsersSpecStyles, t, n, v;

		if (typeof(s) == 'string' && s.length) {
			$.each(s.replace(/&quot;/gi, "'").split(';'), function() {
				t = this.toString().split(':');
				if (t[0] && t[1]) {
					n = $.trim(t[0]).toLowerCase();
					v = $.trim(t[1]);
					if (n == 'color' || n == 'background-color') {
						v = v.toLowerCase();
					}
					if (n && v && (a || n.substring(0, 1) != '-')) {
						st[n] = v;
					}
				}
			});
		}
		return st;
	}


	/**
	 * Compact some style properties and convert colors in hex
	 *
	 * @param   Object
	 * @return  Object
	 **/
	this.compactStyle = function(s) {
		var self = this;

		if (s.border == 'medium none') {
			delete s.border;
		}

		$.each(s, function(n, v) {
			if (/color$/i.test(n)) {
				s[n] = self.color2Hex(v);
			} else if (/^(border|background)$/i.test(n)) {
				s[n] = v.replace(self.colorsRegExp, function(m) {
					return self.color2Hex(m);
				});
			}
		});

		if (s['border-width']) {
			s.border = s['border-width']+' '+(s['border-style']||'solid')+' '+(s['border-color']||'#000');
			delete s['border-width'];
			delete s['border-style'];
			delete s['border-color'];
		}

		if (s['background-image']) {
			s.background = (s['background-color']+' ')||''+s['background-image']+' '+s['background-position']||'0 0'+' '+s['background-repeat']||'repeat';
			delete s['background-image'];
			delete['background-image'];
			delete['background-position'];
			delete['background-repeat'];
		}

		if (s['margin-top'] && s['margin-right'] && s['margin-bottom'] && s['margin-left']) {
			s.margin = s['margin-top']+' '+s['margin-right']+' '+s['margin-bottom']+' '+s['margin-left'];
			delete s['margin-top'];
			delete s['margin-right'];
			delete s['margin-bottom'];
			delete s['margin-left'];
		}

		if (s['padding-top'] && s['padding-right'] && s['padding-bottom'] && s['padding-left']) {
			s.padding = s['padding-top']+' '+s['padding-right']+' '+s['padding-bottom']+' '+s['padding-left'];
			delete s['padding-top'];
			delete s['padding-right'];
			delete s['padding-bottom'];
			delete s['padding-left'];
		}

		if (s['list-style-type'] || s['list-style-position'] || s['list-style-image']) {
			s['list-style'] = $.trim(s['list-style-type']||' '+s['list-style-position']||''+s['list-style-image']||'');
			delete s['list-style-type'];
			delete s['list-style-position'];
			delete s['list-style-image'];
		}

		return s;
	}

	/**
	 * Serialize style object into string
	 *
	 * @param   Object  style map
	 * @param   Boolean flag - compact style?
	 * @return  String
	 **/
	this.serializeStyle = function(o, c) {
		var s = [];
		// c=true
		$.each(c ? this.compactStyle(o) : o, function(n, v) {
			v && s.push(n+':'+v);
		});
		return s.join(';');
	}

	/**
	 * Parse class string into object
	 *
	 * @param   String
	 * @return  Object
	 **/
	this.parseClass = function(c) {
		c = $.trim(c);
		// this.rte.log(c)
		return c.length ? this.makeObject(c.split(/\s+/)) : {};
		return c.length ? c.split(/\s+/) : [];
	}

	/**
	 * Serialize class object into string
	 *
	 * @param   Object
	 * @return  String
	 **/
	this.serializeClass = function(c) {
		// return c.join(' ')
		var s = [];
		$.each(c, function(n) {
			s.push(n);
		});
		return s.join(' ');
	}

	/**
	 * Return required media type info
	 *
	 * @param   String  mimetype
	 * @param   String  classid
	 * @return  Object
	 **/
	this.mediaInfo = function(t, c) {
		var l = this.media.length;

		while (l--) {
			if (t === this.media[l].type || (c && $.inArray(c, this.media[l].classid) != -1)) {
				return this.media[l];
			}
		}
	}

	/**
	 * Return color hex value
	 *
	 * @param   String   color name or rgb
	 * @return  String
	 **/
	this.color2Hex = function(c) {
		var m;

		if (c.indexOf('#') === 0) {
			return c;
		}

		c = c||'';
		function hex(s) {
			s = parseInt(s).toString(16);
			return s.length > 1 ? s : '0' + s;
		};

		if (this.colors[c]) {
			return this.colors[c];
		}
		if ((m = c.match(this.rgbRegExp))) {
			return '#'+hex(m[1])+hex(m[2])+hex(m[3]);
		}
		return '';
	}




}

})(jQuery);/**
 * @class w3cRange  - w3c text range emulation for "strange" browsers
 *
 * @param  elRTE  rte  объект-редактор
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.w3cRange = function(rte) {
	var self                     = this;
	this.rte                     = rte;
	this.r                       = null;
	this.collapsed               = true;
	this.startContainer          = null;
	this.endContainer            = null;
	this.startOffset             = 0;
	this.endOffset               = 0;
	this.commonAncestorContainer = null;

	this.range = function() {
		try {
			this.r = this.rte.window.document.selection.createRange();
		} catch(e) {
			this.r = this.rte.doc.body.createTextRange();
		}
		return this.r;
	}

	this.insertNode = function(html) {
		this.range();
		self.r.collapse(false)
		var r = self.r.duplicate();
		r.pasteHTML(html);
	}

	this.getBookmark = function() {
		this.range();
		if (this.r.item) {
			var n = this.r.item(0);
			this.r = this.rte.doc.body.createTextRange();
			this.r.moveToElementText(n);
		}
		return this.r.getBookmark();
	}

	this.moveToBookmark = function(bm) {
		this.rte.window.focus();
		this.range().moveToBookmark(bm);
		this.r.select();
	}

	/**
	 * Обновляет данные о выделенных нодах
	 *
	 * @return void
	 **/
	this.update = function() {

		function _findPos(start) {
			var marker = '\uFEFF';
			var ndx = offset = 0;
			var r = self.r.duplicate();
			r.collapse(start);
			var p = r.parentElement();
			if (!p || p.nodeName == 'HTML') {
				return {parent : self.rte.doc.body, ndx : ndx, offset : offset};
			}

			r.pasteHTML(marker);

			childs = p.childNodes;
			for (var i=0; i < childs.length; i++) {
				var n = childs[i];
				if (i>0 && (n.nodeType!==3 || childs[i-1].nodeType !==3)) {
					ndx++;
				}
				if (n.nodeType !== 3) {
					offset = 0;
				} else {
					var pos = n.nodeValue.indexOf(marker);
					if (pos !== -1) {
						offset += pos;
						break;
					}
					offset += n.nodeValue.length;
				}
			};
			r.moveStart('character', -1);
			r.text = '';
			return {parent : p, ndx : Math.min(ndx, p.childNodes.length-1), offset : offset};
		}

		this.range();
		this.startContainer = this.endContainer = null;

		if (this.r.item) {
			this.collapsed = false;
			var i = this.r.item(0);
			this.setStart(i.parentNode, this.rte.dom.indexOf(i));
			this.setEnd(i.parentNode, this.startOffset+1);
		} else {
			this.collapsed = this.r.boundingWidth == 0;
			var start = _findPos(true);
			var end   = _findPos(false);

			start.parent.normalize();
			end.parent.normalize();
			start.ndx = Math.min(start.ndx, start.parent.childNodes.length-1);
			end.ndx = Math.min(end.ndx, end.parent.childNodes.length-1);
			if (start.parent.childNodes[start.ndx].nodeType && start.parent.childNodes[start.ndx].nodeType == 1) {
				this.setStart(start.parent, start.ndx);
			} else {
				this.setStart(start.parent.childNodes[start.ndx], start.offset);
			}
			if (end.parent.childNodes[end.ndx].nodeType && end.parent.childNodes[end.ndx].nodeType == 1) {
				this.setEnd(end.parent, end.ndx);
			} else {
				this.setEnd(end.parent.childNodes[end.ndx], end.offset);
			}
			// this.dump();
			this.select();
		}
		return this;
	}

	this.isCollapsed = function() {
		this.range();
		this.collapsed = this.r.item ? false : this.r.boundingWidth == 0;
		return this.collapsed;
	}

	/**
	 * "Схлопывает" выделение
	 *
	 * @param  bool  toStart - схлопывать выделение к началу или к концу
	 * @return void
	 **/
	this.collapse = function(toStart) {
		this.range();
		if (this.r.item) {
			var n = this.r.item(0);
			this.r = this.rte.doc.body.createTextRange();
			this.r.moveToElementText(n);
		}
		this.r.collapse(toStart);
		this.r.select();
		this.collapsed = true;
	}

	this.getStart = function() {
		this.range();
		if (this.r.item) {
			return this.r.item(0);
		}
		var r = this.r.duplicate();
		r.collapse(true);
		var s = r.parentElement();
		return s && s.nodeName == 'BODY' ? s.firstChild : s;
	}


	this.getEnd = function() {
		this.range();
		if (this.r.item) {
			return this.r.item(0);
		}
		var r = this.r.duplicate();
		r.collapse(false);
		var e = r.parentElement();
		return e && e.nodeName == 'BODY' ? e.lastChild : e;
	}


	/**
	 * Устанавливает начaло выделения на указаную ноду
	 *
	 * @param  Element  node    нода
	 * @param  Number   offset  отступ от начала ноды
	 * @return void
	 **/
	this.setStart = function(node, offset) {
		this.startContainer = node;
		this.startOffset    = offset;
		if (this.endContainer) {
			this.commonAncestorContainer = this.rte.dom.findCommonAncestor(this.startContainer, this.endContainer);
		}
	}

	/**
	 * Устанавливает конец выделения на указаную ноду
	 *
	 * @param  Element  node    нода
	 * @param  Number   offset  отступ от конца ноды
	 * @return void
	 **/
	this.setEnd = function(node, offset) {
		this.endContainer = node;
		this.endOffset    = offset;
		if (this.startContainer) {
			this.commonAncestorContainer = this.rte.dom.findCommonAncestor(this.startContainer, this.endContainer);
		}
	}

	/**
	 * Устанавливает начaло выделения перед указаной нодой
	 *
	 * @param  Element  node    нода
	 * @return void
	 **/
	this.setStartBefore = function(n) {
		if (n.parentNode) {
			this.setStart(n.parentNode, this.rte.dom.indexOf(n));
		}
	}

	/**
	 * Устанавливает начaло выделения после указаной ноды
	 *
	 * @param  Element  node    нода
	 * @return void
	 **/
	this.setStartAfter = function(n) {
		if (n.parentNode) {
			this.setStart(n.parentNode, this.rte.dom.indexOf(n)+1);
		}
	}

	/**
	 * Устанавливает конец выделения перед указаной нодой
	 *
	 * @param  Element  node    нода
	 * @return void
	 **/
	this.setEndBefore = function(n) {
		if (n.parentNode) {
			this.setEnd(n.parentNode, this.rte.dom.indexOf(n));
		}
	}

	/**
	 * Устанавливает конец выделения после указаной ноды
	 *
	 * @param  Element  node    нода
	 * @return void
	 **/
	this.setEndAfter = function(n) {
		if (n.parentNode) {
			this.setEnd(n.parentNode, this.rte.dom.indexOf(n)+1);
		}
	}

	/**
	 * Устанавливает новое выделение после изменений
	 *
	 * @return void
	 **/
	this.select = function() {
		// thanks tinymice authors
		function getPos(n, o) {
			if (n.nodeType != 3) {
				return -1;
			}
			var c   ='\uFEFF';
			var val = n.nodeValue;
			var r   = self.rte.doc.body.createTextRange();
			n.nodeValue = val.substring(0, o) + c + val.substring(o);
			r.moveToElementText(n.parentNode);
			r.findText(c);
			var p = Math.abs(r.moveStart('character', -0xFFFFF));
			n.nodeValue = val;
			return p;
		};

		this.r = this.rte.doc.body.createTextRange();
		var so = this.startOffset;
		var eo = this.endOffset;
		var s = this.startContainer.nodeType == 1
			? this.startContainer.childNodes[Math.min(so, this.startContainer.childNodes.length - 1)]
			: this.startContainer;
		var e = this.endContainer.nodeType == 1
			? this.endContainer.childNodes[Math.min(so == eo ? eo : eo - 1, this.endContainer.childNodes.length - 1)]
			: this.endContainer;

		if (this.collapsed) {
			if (s.nodeType == 3) {
				var p = getPos(s, so);
				this.r.move('character', p);
			} else {
				this.r.moveToElementText(s);
				this.r.collapse(true);
			}
		} else {
			var r  = this.rte.doc.body.createTextRange();
			var sp = getPos(s, so);
			var ep = getPos(e, eo);
			if (s.nodeType == 3) {
				this.r.move('character', sp);
			} else {
				this.r.moveToElementText(s);
			}
			if (e.nodeType == 3) {
				r.move('character', ep);
			} else {
				r.moveToElementText(e);
			}
			this.r.setEndPoint('EndToEnd', r);
		}

		try {
			this.r.select();
		} catch(e) {

		}
		if (r) {
			r = null;
		}
	}

	this.dump = function() {
		this.rte.log('collapsed: '+this.collapsed);
		//this.rte.log('commonAncestorContainer: '+this.commonAncestorContainer.nodeName||'#text')
		this.rte.log('startContainer: '+(this.startContainer ? this.startContainer.nodeName : 'non'));
		this.rte.log('startOffset: '+this.startOffset);
		this.rte.log('endContainer: '+(this.endContainer ? this.endContainer.nodeName : 'none'));
		this.rte.log('endOffset: '+this.endOffset);
	}

}
})(jQuery);
(function($) {
	elRTE.prototype.ui.prototype.buttons.about = function(rte, name)  {

		this.constructor.prototype.constructor.call(this, rte, name);
		this.active  = true;
		this.command = function() {
			var opts, d, txt;

			opts = {
				rtl : rte.rtl,
				submit : function(e, d) {  d.close(); },
				dialog : {
					width : 560,
					title : this.rte.i18n('About this software'),
					buttons     : {
						Ok : function() { $(this).dialog('destroy'); }
					}
				}
			}

			txt = '<div class="elrte-logo"></div><h3>'+this.rte.i18n('About elRTE')+'</h3><br clear="all"/>'
				+'<div class="elrte-ver">'+this.rte.i18n('Version')+': '+this.rte.version+' ('+this.rte.build+')</div>'
				+'<div class="elrte-ver">jQuery: '+$('<div/>').jquery+'</div>'
				+'<div class="elrte-ver">jQueryUI: '+$.ui.version+'</div>'
				+'<div class="elrte-ver">'+this.rte.i18n('Licence')+': BSD Licence</div>'
				+'<p>'
				+this.rte.i18n('elRTE is an open-source JavaScript based WYSIWYG HTML-editor.')+'<br/>'
				+this.rte.i18n('Main goal of the editor - simplify work with text and formating (HTML) on sites, blogs, forums and other online services.')+'<br/>'
				+this.rte.i18n('You can use it in any commercial or non-commercial projects.')
				+'</p>'
				+'<h4>'+this.rte.i18n('Authors')+'</h4>'
				+'<table class="elrte-authors">'
				+'<tr><td>Dmitry (dio) Levashov &lt;dio@std42.ru&gt;</td><td>'+this.rte.i18n('Chief developer')+'</td></tr>'
				+'<tr><td>Troex Nevelin &lt;troex@fury.scancode.ru&gt;</td><td>'+this.rte.i18n('Developer, tech support')+'</td></tr>'
				+'<tr><td>Valentin Razumnyh &lt;content@std42.ru&gt;</td><td>'+this.rte.i18n('Interface designer')+'</td></tr>'
				+'<tr><td>Evgeny eSabbath &lt;sabbath.codemg@gmail.com&gt;</td><td>'+this.rte.i18n('Developer')+'</td></tr>'
				+'<tr><td>Andrzej Borowicz &lt;eltre@borowicz.info&gt;</td><td>'+this.rte.i18n('Polish localization')+'</td></tr>'
				+'<tr><td>Artem Vasiliev</td><td>'+this.rte.i18n('Ukranian localization')+'</td></tr>'
				+'<tr><td>Francois Mazerolle &lt;fmaz008@gmail.com&gt;</td><td>'+this.rte.i18n('French localization')+'</td></tr>'
				+'<tr><td>Kurt Aerts</td><td>'+this.rte.i18n('Dutch localization')+'</td></tr>'
				+'<tr><td>Michal Marek &lt;fmich.marek@gmail.com&gt;</td><td>'+this.rte.i18n('Czech localization')+'</td></tr>'
				+'<tr><td>Ricardo Obregón &lt;robregonm@gmail.com&gt;</td><td>'+this.rte.i18n('Spanish localization')+'</td></tr>'
				+'<tr><td>Saleh Souzanchi &lt;saleh.souzanchi@gmail.com&gt;</td><td>'+this.rte.i18n('Persian (farsi) localization')+'</td></tr>'
				+'<tr><td>Tawfek Daghistani &lt;tawfekov@gmail.com&gt;</td><td>'+this.rte.i18n('Arabic localization')+', '+this.rte.i18n('RTL support')+'</td></tr>'
				+'<tr><td>Tad &lt;tad0616@gmail.com&gt;</td><td>'+this.rte.i18n('Traditional Chinese localization')+'</td></tr>'
				+'<tr><td>Tomoaki Yoshida &lt;info@yoshida-studio.jp&gt;</td><td>'+this.rte.i18n('Japanese localization')+'</td></tr>'
				+'<tr><td>Uldis Plotiņš &lt;uldis.plotins@gmail.com&gt;</td><td>'+this.rte.i18n('Latvian localization')+'</td></tr>'
				+'<tr><td>Vasiliy Razumnyh &lt;rvn@std42.ru&gt;</td><td>'+this.rte.i18n('German localization')+'</td></tr>'
				+'<tr><td>Viktor Tamas &lt;tamas.viktor@totalstudio.hu&gt;</td><td>'+this.rte.i18n('Hungarian localization')+'</td></tr>'
				+'<tr><td>Ugo Punzolo, &lt;sadraczerouno@gmail.com&gt;</td><td>'+this.rte.i18n('Italian localization')+'</td></tr>'
				+'</table>'
				+'<div class="elrte-copy">Copyright &copy; 2009-2010, <a href="http://www.std42.ru">Studio 42 LTD</a></div>'
				+'<div class="elrte-copy">'+this.rte.i18n('For more information about this software visit the')+' <a href="http://elrte.org">'+this.rte.i18n('elRTE website')+'.</a></div>'
				+'<div class="elrte-copy">Twitter: <a href="http://twitter.com/elrte_elfinder">elrte_elfinder</a></div>';

			d = new elDialogForm(opts);
			d.append(txt);
			d.open();
		}

		this.update = function() {
			this.domElem.removeClass('disabled');
		}

	}

})(jQuery);
/**
 * @class кнопка - Закладка (открывает диалоговое окно)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.anchor = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	this.input = $('<input type="text" />').attr('name', 'anchor').attr('size', '16')
	var self = this;

	this.command = function() {
		var opts = {
			rtl : this.rte.rtl,
			submit : function(e, d) { e.stopPropagation(); e.preventDefault(); d.close(); self.set();  },
			dialog : {
				title : this.rte.i18n('Bookmark')
			}
		}


		this.anchor = this.rte.dom.selfOrParentAnchor(this.rte.selection.getEnd()) || rte.dom.create('a');
		!this.rte.selection.collapsed() && this.rte.selection.collapse(false);
		this.input.val($(this.anchor).addClass('el-rte-anchor').attr('name'));
		this.rte.selection.saveIERange();
		var d = new elDialogForm(opts);
		d.append([this.rte.i18n('Bookmark name'), this.input], null, true).open();
		setTimeout(function() { self.input.focus()}, 20);
	}

	this.update = function() {
		var n = this.rte.selection.getNode();
		if (this.rte.dom.selfOrParentLink(n)) {
			this.domElem.addClass('disabled');
		} else if (this.rte.dom.selfOrParentAnchor(n)) {
			this.domElem.removeClass('disabled').addClass('active');
		} else {
			this.domElem.removeClass('disabled').removeClass('active');
		}
	}

	this.set = function() {
		var n = $.trim(this.input.val());

		if (n) {
			this.rte.history.add();
			if (!this.anchor.parentNode) {
				this.rte.selection.insertHtml('<a name="'+n+'" title="'+this.rte.i18n('Bookmark')+': '+n+'" class="el-rte-anchor"></a>');
			} else {
				this.anchor.name = n;
				this.anchor.title = this.rte.i18n('Bookmark')+': '+n;
			}
		} else if (this.anchor.parentNode) {
			this.rte.history.add();
			this.anchor.parentNode.removeChild(this.anchor);
		}
	}
}
})(jQuery);
/**
 * @class кнопка - Цитата
 * Если выделение схлопнуто и находится внутри цитаты - она удаляется
 * Новые цитаты создаются только из несхлопнутого выделения
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.blockquote = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		var n, nodes;
		this.rte.history.add();
		if (this.rte.selection.collapsed() && (n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^BLOCKQUOTE$/))) {
			$(n).replaceWith($(n).html());
		} else {
			nodes = this.rte.selection.selected({wrap : 'all', tag : 'blockquote'});
			nodes.length && this.rte.selection.select(nodes[0], nodes[nodes.length-1]);
		}
		this.rte.ui.update(true);
	}

	this.update = function() {
		if (this.rte.selection.collapsed()) {
			if (this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^BLOCKQUOTE$/)) {
				this.domElem.removeClass('disabled').addClass('active');
			} else {
				this.domElem.addClass('disabled').removeClass('active');
			}
		} else {
			this.domElem.removeClass('disabled active');
		}
	}
}
})(jQuery);
/**
 * @class кнопки "копировать/вырезать/вставить"
 * в firefox показывает предложение нажать Ctl+c, в остальных - копирует
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.copy = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {

		if (this.rte.browser.mozilla) {
			try {
				this.rte.doc.execCommand(this.name, false, null);
			} catch (e) {
				var s = ' Ctl + C';
				if (this.name == 'cut') {
					s = ' Ctl + X';
				} else if (this.name == 'paste') {
					s = ' Ctl + V';
				}
				var opts = {
					dialog : {
						title   : this.rte.i18n('Warning'),
						buttons : { Ok : function() { $(this).dialog('close'); } }
					}
				}

				var d = new elDialogForm(opts);
				d.append(this.rte.i18n('This operation is disabled in your browser on security reason. Use shortcut instead.')+': '+s).open();
			}
		} else {
			this.constructor.prototype.command.call(this);
		}
	}
}

elRTE.prototype.ui.prototype.buttons.cut   = elRTE.prototype.ui.prototype.buttons.copy;
elRTE.prototype.ui.prototype.buttons.paste = elRTE.prototype.ui.prototype.buttons.copy;

})(jQuery);(function($) {

	elRTE.prototype.ui.prototype.buttons.css = function(rte, name) {
		var self = this;
		this.constructor.prototype.constructor.call(this, rte, name);
		this.cssStyle = $('<input type="text" size="42" name="style" />');
		this.cssClass = $('<input type="text" size="42" name="class" />');


		this.command = function() {
			var n = this.node(), opts;
			this.rte.selection.saveIERange();
			if (n) {
				var opts = {

					submit : function(e, d) { e.stopPropagation(); e.preventDefault(); d.close(); self.set();  },
					dialog : {
						title : this.rte.i18n('Style'),
						width : 450,
						resizable : true,
						modal : true
					}
				}
				this.cssStyle.val($(n).attr('style'));
				this.cssClass.val($(n).attr('class'));
				var d = new elDialogForm(opts);
				d.append([this.rte.i18n('Css style'), this.cssStyle], null, true)
				d.append([this.rte.i18n('Css class'), this.cssClass], null, true).open();
				setTimeout(function() { self.cssStyle.focus() }, 20)
			}
		}

		this.set = function() {
			var n = this.node();
			this.rte.selection.restoreIERange();
			if (n) {
				$(n).attr('style', this.cssStyle.val());
				$(n).attr('class', this.cssClass.val());
				this.rte.ui.update();
			}
		}

		this.node = function() {
			var n = this.rte.selection.getNode();
			if (n.nodeType == 3) {
				n = n.parentNode;
			}
			return n.nodeType == 1 && n.nodeName != 'BODY' ? n : null;
		}

		this.update = function() {
			this.domElem.toggleClass('disabled', this.node()?false:true);
		}

	}

})(jQuery);
(function($) {
	/**
	 * @class button - right to left direction (not work yet with text nodes in body)
	 *
	 * @param  elRTE  rte   объект-редактор
	 * @param  String name  название кнопки
	 *
	 * @author:    Dmitry Levashov (dio) dio@std42.ru
	 * Copyright: Studio 42, http://www.std42.ru
	 **/
	elRTE.prototype.ui.prototype.buttons.rtl = function(rte, name)  {
		this.constructor.prototype.constructor.call(this, rte, name);
		var self = this;

		this.command = function() {
			var n = this.rte.selection.getNode(), self = this;
			if ($(n).attr('dir') == 'rtl' || $(n).parents('[dir="rtl"]').length || $(n).find('[dir="rtl"]').length) {
				$(n).removeAttr('dir');
				$(n).parents('[dir="rtl"]').removeAttr('dir');
				$(n).find('[dir="rtl"]').removeAttr('dir');
			} else {
				if (this.rte.dom.is(n, 'textNodes') && this.rte.dom.is(n, 'block')) {
					$(n).attr('dir', 'rtl');
				} else {
					$.each(this.rte.dom.parents(n, 'textNodes'), function(i, n) {
						if (self.rte.dom.is(n, 'block')) {
							$(n).attr('dir', 'rtl');
							return false;
						}
					});
				}
			}
			this.rte.ui.update();
		}

		this.update = function() {
			var n = this.rte.selection.getNode();
			this.domElem.removeClass('disabled');
			if ($(n).attr('dir') == 'rtl' || $(n).parents('[dir="rtl"]').length || $(n).find('[dir="rtl"]').length) {
				this.domElem.addClass('active');
			} else {
				this.domElem.removeClass('active');
			}
		}
	}

	/**
	 * @class button - left to right direction (not work yet with text nodes in body)
	 *
	 * @param  elRTE  rte   объект-редактор
	 * @param  String name  название кнопки
	 *
	 * @author:    Dmitry Levashov (dio) dio@std42.ru
	 * Copyright: Studio 42, http://www.std42.ru
	 **/
	elRTE.prototype.ui.prototype.buttons.ltr = function(rte, name)  {
		this.constructor.prototype.constructor.call(this, rte, name);
		var self = this;

		this.command = function() {
			var n = this.rte.selection.getNode(), self = this;
			if ($(n).attr('dir') == 'ltr' || $(n).parents('[dir="ltr"]').length || $(n).find('[dir="ltr"]').length) {
				$(n).removeAttr('dir');
				$(n).parents('[dir="ltr"]').removeAttr('dir');
				$(n).find('[dir="ltr"]').removeAttr('dir');
			} else {
				if (this.rte.dom.is(n, 'textNodes') && this.rte.dom.is(n, 'block')) {
					$(n).attr('dir', 'ltr');
				} else {
					$.each(this.rte.dom.parents(n, 'textNodes'), function(i, n) {
						if (self.rte.dom.is(n, 'block')) {
							$(n).attr('dir', 'ltr');
							return false;
						}
					});
				}
			}
			this.rte.ui.update();
		}

		this.update = function() {
			var n = this.rte.selection.getNode();
			this.domElem.removeClass('disabled');
			if ($(n).attr('dir') == 'ltr' || $(n).parents('[dir="ltr"]').length || $(n).find('[dir="ltr"]').length) {
				this.domElem.addClass('active');
			} else {
				this.domElem.removeClass('active');
			}
		}
	}

})(jQuery);/**
 * @class кнопка - DIV
 * Если выделение схлопнуто и находится внутри div'a - он удаляется
 * Новые div'ы создаются только из несхлопнутого выделения
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.div = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		var n, nodes;
		this.rte.history.add();

		if (this.rte.selection.collapsed()) {
			n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^DIV$/);
			if (n) {
				$(n).replaceWith($(n).html());
			}
		} else {
			nodes = this.rte.selection.selected({wrap : 'all', tag : 'div'});
			nodes.length && this.rte.selection.select(nodes[0], nodes[nodes.length-1]);
		}
		this.rte.ui.update(true);
	}

	this.update = function() {
		if (this.rte.selection.collapsed()) {
			if (this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^DIV$/)) {
				this.domElem.removeClass('disabled').addClass('active');
			} else {
				this.domElem.addClass('disabled active');
			}
		} else {
			this.domElem.removeClass('disabled active');
		}
	}
}
})(jQuery);
/**
 * @class кнопка - Включение/выключение показа структуры документа
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.docstructure = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		this.domElem.toggleClass('active');
		$(this.rte.doc.body).toggleClass('el-rte-structure');
	}
	this.command();

	this.update = function() {
		this.domElem.removeClass('disabled');
	}
}
})(jQuery);
/**
 * @class button - open elfinder window (not needed for image or link buttons).Used in ELDORADO.CMS for easy file manipulations.
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.elfinder = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.command = function() {
		if (self.rte.options.fmAllow && typeof(self.rte.options.fmOpen) == 'function') {
			self.rte.options.fmOpen( function(url) { self.rte.log(url) } );
		}
	}

	this.update = function() {
		if (self.rte.options.fmAllow && typeof(self.rte.options.fmOpen) == 'function') {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}

})(jQuery);(function($) {

	elRTE.prototype.ui.prototype.buttons.flash = function(rte, name) {
		this.constructor.prototype.constructor.call(this, rte, name);
		var self = this;
		this.swf = null;
		this.placeholder = null;
		this.src = {
			url    : $('<input type="text" name="url" />').css('width', '99%'),
			type   : $('<select name="type"/>')
						.append('<option value="application/x-shockwave-flash">Flash</option>')
						.append('<option value="video/quicktime">Quicktime movie</option>')
						.append('<option value="application/x-mplayer2">Windows media</option>'),
			width  : $('<input type="text" />').attr('size', 5).css('text-align', 'right'),
			height : $('<input type="text" />').attr('size', 5).css('text-align', 'right'),
			wmode  : $('<select />')
						.append($('<option />').val('').text(this.rte.i18n('Not set', 'dialogs')))
						.append($('<option />').val('transparent').text(this.rte.i18n('Transparent'))),
			align  : $('<select />')
						.append($('<option />').val('').text(this.rte.i18n('Not set', 'dialogs')))
						.append($('<option />').val('left'       ).text(this.rte.i18n('Left')))
						.append($('<option />').val('right'      ).text(this.rte.i18n('Right')))
						.append($('<option />').val('top'        ).text(this.rte.i18n('Top')))
						.append($('<option />').val('text-top'   ).text(this.rte.i18n('Text top')))
						.append($('<option />').val('middle'     ).text(this.rte.i18n('middle')))
						.append($('<option />').val('baseline'   ).text(this.rte.i18n('Baseline')))
						.append($('<option />').val('bottom'     ).text(this.rte.i18n('Bottom')))
						.append($('<option />').val('text-bottom').text(this.rte.i18n('Text bottom'))),
			margin : $('<div />')
		}

		this.command = function() {

			var n = this.rte.selection.getEnd(), opts, url='', w='', h='', f, a, d, mid, o, wm;
			this.rte.selection.saveIERange();
			this.src.margin.elPaddingInput({ type : 'margin' });
			this.placeholder = null;
			this.swf = null;
			if ($(n).hasClass('elrte-media') && (mid = $(n).attr('rel')) &&  this.rte.filter.scripts[mid]) {
				this.placeholder = $(n);
				o = this.rte.filter.scripts[mid];
				url = '';
				if (o.embed && o.embed.src) {
					url = o.embed.src;
				}
				if (o.params && o.params.length) {
					l = o.params.length;
					while (l--) {
						if (o.params[l].name == 'src' || o.params[l].name == 'movie') {
							url =  o.params[l].value;
						}
					}
				}

				if (o.embed) {
					w = o.embed.width||parseInt(o.embed.style.width)||'';
					h = o.embed.height||parseInt(o.embed.style.height)||'';
					wm = o.embed.wmode||'';
				} else if (o.obj) {
					w = o.obj.width||parseInt(o.obj.style.width)||'';
					h = o.obj.height||parseInt(o.obj.style.height)||'';
					wm = o.obj.wmode||'';
				}

				if (o.obj) {
					f = o.obj.style['float']||'';
					a = o.obj.style['vertical-align']||'';
				} else if (o.embed) {
					f = o.embed.style['float']||'';
					a = o.embed.style['vertical-align']||'';
				}
				this.src.margin.val(n);
				this.src.type.val(o.embed ? o.embed.type : '');
			}
			if ($(n).hasClass('elrte-swf-placeholder')) {
				this.placeholder = $(n);
				url = $(n).attr('rel');
				w = parseInt($(n).css('width'))||'';
				h = parseInt($(n).css('height'))||'';
				f = $(n).css('float');
				a = $(n).css('vertical-align');
				this.src.margin.val(n);
				this.src.wmode.val($(n).attr('wmode'));
			}
			this.src.url.val(url);
			this.src.width.val(w);
			this.src.height.val(h);
			this.src.align.val(f||a);
			this.src.wmode.val(wm);




			var opts = {
				rtl : this.rte.rtl,
				submit : function(e, d) { e.stopPropagation(); e.preventDefault(); self.set(); d.close(); },
				dialog : {
					width    : 580,
					position : 'top',
					title    : this.rte.i18n('Flash')
				}
			}
			var d = new elDialogForm(opts);

			if (this.rte.options.fmAllow && this.rte.options.fmOpen) {
				var src = $('<span />').append(this.src.url.css('width', '85%'))
						.append(
							$('<span />').addClass('ui-state-default ui-corner-all')
								.css({'float' : 'right', 'margin-right' : '3px'})
								.attr('title', self.rte.i18n('Open file manger'))
								.append($('<span />').addClass('ui-icon ui-icon-folder-open'))
									.click( function() {
										self.rte.options.fmOpen( function(url) { self.src.url.val(url).change(); } );
									})
									.hover(function() {$(this).addClass('ui-state-hover')}, function() { $(this).removeClass('ui-state-hover')})
							);
			} else {
				var src = this.src.url;
			}

			d.append([this.rte.i18n('URL'), src], null, true);
			d.append([this.rte.i18n('Type'), this.src.type], null, true);
			d.append([this.rte.i18n('Size'), $('<span />').append(this.src.width).append(' x ').append(this.src.height).append(' px')], null, true)
			d.append([this.rte.i18n('Wmode'), this.src.wmode], null, true);
			d.append([this.rte.i18n('Alignment'), this.src.align], null, true);
			d.append([this.rte.i18n('Margins'), this.src.margin], null, true);



			d.open();
			setTimeout( function() {self.src.url.focus()}, 100)


			var fs = $('<fieldset />').append($('<legend />').text(this.rte.i18n('Preview')))
			d.append(fs, 'main');
			var frame = document.createElement('iframe');
			$(frame).attr('src', '#').addClass('el-rte-preview').appendTo(fs);
			html = this.rte.options.doctype+'<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /></head><body style="padding:0;margin:0;font-size:9px"> Proin elit arcu, rutrum commodo, vehicula tempus, commodo a, risus. Curabitur nec arcu. Donec sollicitudin mi sit amet mauris. Nam elementum quam ullamcorper ante. Etiam aliquet massa et lorem. Mauris dapibus lacus auctor risus. Aenean tempor ullamcorper leo. Vivamus sed magna quis ligula eleifend adipiscing. Duis orci. Aliquam sodales tortor vitae ipsum. Aliquam nulla. Duis aliquam molestie erat. Ut et mauris vel pede varius sollicitudin</body></html>';
			frame.contentWindow.document.open();
			frame.contentWindow.document.write(html);
			frame.contentWindow.document.close();
			this.frame = frame.contentWindow.document;
			this.preview = $(frame.contentWindow.document.body);

			this.src.type.change(function() {
				self.src.url.change();
			});

			this.src.width.change(function() {
				if (self.swf) {
					var w = parseInt($(this).val())||'';
					$(this).val(w);
					self.swf.css('width', w);
					self.swf.children('embed').css('width', w);
				} else {
					$(this).val('');
				}
			});

			this.src.height.change(function() {
				if (self.swf) {
					var h = parseInt($(this).val())||'';
					$(this).val(h);
					self.swf.css('height', h);
					self.swf.children('embed').css('height', h);
				} else {
					$(this).val('');
				}
			});

			this.src.wmode.change(function() {
				if (self.swf) {
					var wm = $(this).val();
					if (wm) {
						self.swf.attr('wmode', wm);
						self.swf.children('embed').attr('wmode', wm);
					} else {
						self.swf.removeAttr('wmode');
						self.swf.children('embed').removeAttr('wmode');
					}
				}
			});

			this.src.align.change(function() {
				var v = $(this).val(), f = v=='left' || v=='right';
				if (self.swf) {
					self.swf.css({
						'float' : f ? v : '',
						'vertical-align' : f ? '' : v
					});
				} else {
					$(this).val('');
				}
			});

			this.src.margin.change(function() {
				if (self.swf) {
					var m = self.src.margin.val();
					if (m.css) {
						self.swf.css('margin', m.css);
					} else {
						self.swf.css('margin-top', m.top);
						self.swf.css('margin-right', m.right);
						self.swf.css('margin-bottom', m.bottom);
						self.swf.css('margin-left', m.left);
					}
				}
			});

			this.src.url.change(function() {
				var url = self.rte.utils.absoluteURL($(this).val()), i, swf;
				if (url) {
					i = self.rte.utils.mediaInfo(self.src.type.val());
					if (!i) {
						i = self.rte.util.mediaInfo('application/x-shockwave-flash');
					}
					swf = '<object classid="'+i.classid+'" codebase="'+i.codebase+'"><param name="src" value="'+url+'" /><embed quality="high" src="'+url+'" type="'+i.type+'"></object>';
					self.preview.children('object').remove().end().prepend(swf);
					self.swf = self.preview.children('object').eq(0);
				} else if (self.swf){
					self.swf.remove();
					self.swf = null;
				}
				self.src.width.trigger('change');
				self.src.height.trigger('change');
				self.src.align.trigger('change');

			}).trigger('change');
		};

		this.set = function() {
			self.swf = null
			var url = this.rte.utils.absoluteURL(this.src.url.val()),
				w = parseInt(this.src.width.val()) || '',
				h = parseInt(this.src.height.val()) || '',
				wm = this.src.wmode.val(),
				a = this.src.align.val(),
				f = a == 'left' || a == 'right' ? a : '',
				mid = this.placeholder ? this.placeholder.attr('rel') : '', o, _o, c,
				m = this.src.margin.val(), margin;



			if (!url) {
				if (this.placeholder) {
					this.placeholder.remove();
					delete this.rte.filter.scripts[mid];
				}
			} else {
				i = self.rte.utils.mediaInfo(self.src.type.val());
				if (!i) {
					i = self.rte.util.mediaInfo('application/x-shockwave-flash');
				}
				c = this.rte.filter.videoHostRegExp.test(url) ? url.replace(this.rte.filter.videoHostRegExp, "$2") : i.type.replace(/^\w+\/(.+)/, "$1");

				o = {
					obj : {
						classid : i.classid[0],
						codebase : i.codebase,
						style : {}
					},
					params :[ { name : 'src', value : url } ],
					embed :{
						src : url,
						type : i.type,
						quality : 'high',
						wmode : wm,
						style : {}
					}
				};

				if (w) {
					o.obj.width = w;
					o.embed.width = w;
				}
				if (h) {
					o.obj.height = h;
					o.embed.height = h;
				}
				if (f) {
					o.obj.style['float'] = f;
				} else if (a) {
					o.obj.style['vertical-align'] = a;
				}

				if (m.css) {
					margin = { margin : m.css };
				} else {
					margin = {
						'margin-top' : m.top,
						'margin-right' : m.right,
						'margin-bottom' : m.bottom,
						'margin-left' : m.left
					};
				}

				o.obj.style = $.extend({}, o.obj.style, margin);

				if (this.placeholder && mid) {
					_o = this.rte.filter.scripts[mid]||{};

					o = $.extend(true, _o, o);
					delete o.obj.style.width;
					delete o.obj.style.height;
					delete o.embed.style.width;
					delete o.embed.style.height;
					this.rte.filter.scripts[mid] = o;
					this.placeholder.removeAttr('class');
				} else {
					var id = 'media'+Math.random().toString().substring(2);
					this.rte.filter.scripts[id] = o;
					this.placeholder = $(this.rte.dom.create('img')).attr('rel', id).attr('src', this.rte.filter.url+'pixel.gif');
					var ins = true;
				}
				this.placeholder.attr('title', this.rte.utils.encode(url)).attr('width', w||150).attr('height', h||100).addClass('elrte-protected elrte-media elrte-media-'+c).css(o.obj.style);
				if (f) {
					this.placeholder.css('float', f).css('vertical-align', '');
				} else if (a) {
					this.placeholder.css('float', '').css('vertical-align', a);
				} else {
					this.placeholder.css('float', '').css('vertical-align', '');
				}

				if (ins) {
					this.rte.window.focus();
					this.rte.selection.restoreIERange();
					this.rte.selection.insertNode(this.placeholder.get(0));
				}
			}
		}

		this.update = function() {
			this.domElem.removeClass('disabled');
			var n = this.rte.selection.getNode();
			this.domElem.toggleClass('active', n && n.nodeName == 'IMG' && $(n).hasClass('elrte-media'))

		}


	}
})(jQuery);/**
 * @class drop-down menu - font-family for selected text
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.fontname = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	var opts = {
		tpl      : '<span style="font-family:%val">%label</span>',
		select   : function(v) { self.set(v); },
		src      : {
			''                                              : this.rte.i18n('Font'),
			'andale mono,sans-serif'                        : 'Andale Mono',
			'arial,helvetica,sans-serif'                    : 'Arial',
			'arial black,gadget,sans-serif'                 : 'Arial Black',
			'book antiqua,palatino,sans-serif'              : 'Book Antiqua',
			'comic sans ms,cursive'                         : 'Comic Sans MS',
			'courier new,courier,monospace'                 : 'Courier New',
			'georgia,palatino,serif'                        : 'Georgia',
			'helvetica,sans-serif'                          : 'Helvetica',
			'impact,sans-serif'                             : 'Impact',
			'lucida console,monaco,monospace'               : 'Lucida console',
			'lucida sans unicode,lucida grande,sans-serif'  : 'Lucida grande',
			'tahoma,sans-serif'                             : 'Tahoma',
			'times new roman,times,serif'                   : 'Times New Roman',
			'trebuchet ms,lucida grande,verdana,sans-serif' : 'Trebuchet MS',
			'verdana,geneva,sans-serif'                     : 'Verdana'
		}
	}

	this.select = this.domElem.elSelect(opts);

	this.command = function() {
	}

	this.set = function(size) {
		this.rte.history.add();
		var nodes = this.rte.selection.selected({filter : 'textContainsNodes'});
		$.each(nodes, function() {
			$this = /^(THEAD|TFOOT|TBODY|COL|COLGROUP|TR)$/.test(this.nodeName) ? $(this).find('td,th') : $(this);
			$(this).css('font-family', size).find('[style]').css('font-family', '');
		});
		this.rte.ui.update();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		var n = this.rte.selection.getNode();
		if (n.nodeType != 1) {
			n = n.parentNode;
		}
		var v = $(n).css('font-family');
		v = v ? v.toString().toLowerCase().replace(/,\s+/g, ',').replace(/'|"/g, '') : '';
		this.select.val(opts.src[v] ? v : '');
	}
}

})(jQuery);/**
 * @class drop-down menu - font size for selected text
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.fontsize = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	var opts = {
		labelTpl : '%label',
		tpl      : '<span style="font-size:%val;line-height:1.2em">%label</span>',
		select   : function(v) { self.set(v); },
		src      : {
			''         : this.rte.i18n('Font size'),
			'xx-small' : this.rte.i18n('Small (8pt)'),
			'x-small'  : this.rte.i18n('Small (10px)'),
			'small'    : this.rte.i18n('Small (12pt)'),
			'medium'   : this.rte.i18n('Normal (14pt)'),
			'large'    : this.rte.i18n('Large (18pt)'),
			'x-large'  : this.rte.i18n('Large (24pt)'),
			'xx-large' : this.rte.i18n('Large (36pt)')
		}
	}

	this.select = this.domElem.elSelect(opts);

	this.command = function() {
	}

	this.set = function(size) {
		this.rte.history.add();
		var nodes = this.rte.selection.selected({filter : 'textContainsNodes'});
		$.each(nodes, function() {
			$this = /^(THEAD|TFOOT|TBODY|COL|COLGROUP|TR)$/.test(this.nodeName) ? $(this).find('td,th') : $(this);
			$this.css('font-size', size).find("[style]").css('font-size', '');
		});
		this.rte.ui.update();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		var n = this.rte.selection.getNode();
		this.select.val((m = this.rte.dom.attr(n, 'style').match(/font-size:\s*([^;]+)/i)) ? m[1] : '');
	}
}

})(jQuery);/**
 * @class color pallete for text color and background
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.forecolor = function(rte, name) {
	var self = this;
	this.constructor.prototype.constructor.call(this, rte, name);
	var opts = {
		'class' : '',
		color   : this.defaultColor,
		update  : function(c) { self.indicator.css('background-color', c); },
		change  : function(c) { self.set(c) }
	}

	this.defaultColor = this.name == 'forecolor' ? '#000000' : '#ffffff';
	this.picker       = this.domElem.elColorPicker(opts);
	this.indicator    = $('<div />').addClass('color-indicator').prependTo(this.domElem);

	this.command = function() {
	}

	this.set = function(c) {
		if (!this.rte.selection.collapsed()) {
			this.rte.history.add();
			var nodes = this.rte.selection.selected({collapse : false, wrap : 'text'}),
				css   = this.name == 'forecolor' ? 'color' : 'background-color';
			$.each(nodes, function() {
				if (/^(THEAD|TBODY|TFOOT|TR)$/.test(this.nodeName)) {
					$(this).find('td,th').each(function() {
						$(this).css(css, c).find('*').css(css, '');
					})
				} else {
					$(this).css(css, c).find('*').css(css, '');
				}
			});
			this.rte.ui.update(true);
		}
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		var n = this.rte.selection.getNode();
		this.picker.val(this.rte.utils.rgb2hex($(n.nodeType != 1 ? n.parentNode : n).css(this.name == 'forecolor' ? 'color' : 'background-color'))||this.defaultColor)
	}
}

elRTE.prototype.ui.prototype.buttons.hilitecolor = elRTE.prototype.ui.prototype.buttons.forecolor;

})(jQuery);/**
 * @class drop-down menu - formatting text block
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.formatblock = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	var cmd = this.rte.browser.msie
		? function(v) { self.val = v; self.constructor.prototype.command.call(self); }
		: function(v) { self.ieCommand(v); }
	var self = this;
	var opts = {
		labelTpl : '%label',
		tpls     : {'' : '%label'},
		select   : function(v) { self.formatBlock(v); },
		src      : {
			'span'    : this.rte.i18n('Format'),
			'h1'      : this.rte.i18n('Heading 1'),
			'h2'      : this.rte.i18n('Heading 2'),
			'h3'      : this.rte.i18n('Heading 3'),
			'h4'      : this.rte.i18n('Heading 4'),
			'h5'      : this.rte.i18n('Heading 5'),
			'h6'      : this.rte.i18n('Heading 6'),
			'p'       : this.rte.i18n('Paragraph'),
			'address' : this.rte.i18n('Address'),
			'pre'     : this.rte.i18n('Preformatted'),
			'div'     : this.rte.i18n('Normal (DIV)')
		}
	}

	this.select = this.domElem.elSelect(opts);

	this.command = function() {

	}

	this.formatBlock = function(v) {

		function format(n, tag) {

			function replaceChilds(p) {
				$(p).find('h1,h2,h3,h4,h5,h6,p,address,pre').each(function() {
					$(this).replaceWith($(this).html());
				});
				return p;
			}

			if (/^(LI|DT|DD|TD|TH|CAPTION)$/.test(n.nodeName)) {
				!self.rte.dom.isEmpty(n) && self.rte.dom.wrapContents(replaceChilds(n), tag);
			} else if (/^(UL|OL|DL|TABLE)$/.test(n.nodeName)) {
				self.rte.dom.wrap(n, tag);
			} else {
				!self.rte.dom.isEmpty(n) && $(replaceChilds(n)).replaceWith( $(self.rte.dom.create(tag)).html($(n).html()));
			}

		}
		this.rte.history.add();

		var tag = v.toUpperCase(),
			i, n, $n,
			c = this.rte.selection.collapsed(),
			bm = this.rte.selection.getBookmark(),
			nodes = this.rte.selection.selected({
				collapsed : true,
				blocks    : true,
				filter    : 'textContainsNodes',
				wrap      : 'inline',
				tag       : 'span'
			})
			l = nodes.length,
			s = $(nodes[0]).prev(),
			e = $(nodes[nodes.length-1]).next();

		while (l--) {
			n = nodes[l];
			$n = $(n);
			if (tag == 'DIV' || tag == 'SPAN') {
				if (/^(H[1-6]|P|ADDRESS|PRE)$/.test(n.nodeName)) {
					$n.replaceWith($(this.rte.dom.create('div')).html($n.html()||''));
				}
			} else {
				if (/^(THEAD|TBODY|TFOOT|TR)$/.test(n.nodeName)) {
					$n.find('td,th').each(function() { format(this, tag); });
				} else if (n.nodeName != tag) {
					format(n, tag);
				}
			}
		}

		this.rte.selection.moveToBookmark(bm);

		this.rte.ui.update(true);
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		var n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(H[1-6]|P|ADDRESS|PRE)$/);
		this.select.val(n ? n.nodeName.toLowerCase() : 'span');
	}
}
})(jQuery);
/**
 * @class button - switch to fullscreen mode and back
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.fullscreen = function(rte, name) {
	var self     = this;
	this.constructor.prototype.constructor.call(this, rte, name);
	this.active  = true;
	this.editor = rte.editor;
	this.wz = rte.workzone;
	this.height  = 0;
	this.delta = 0;
	this._class = 'el-fullscreen';

	setTimeout(function() {
		self.height  = self.wz.height();
		self.delta   = self.editor.outerHeight()-self.height;
	}, 50);


	/**
	 * Update editor height on window resize in fullscreen view
	 *
	 **/
	function resize() {
		self.wz.height($(window).height()-self.delta);
		self.rte.updateHeight();
	}

	this.command = function() {
		var w = $(window),
			e = this.editor,
			p = e.parents().filter(function(i, n) { return  !/^(html|body)$/i.test(n.nodeName) && $(n).css('position') == 'relative'; }),
			wz = this.wz,
			c = this._class,
			f = e.hasClass(c),
			rte = this.rte,
			s = this.rte.selection,
			m = $.browser.mozilla,
			b, h;

		function save() {
			if (m) {
				b = s.getBookmark();
			}
		}

		function restore() {
			if (m) {
				self.wz.children().toggle();
				self.rte.source.focus();
				self.wz.children().toggle();
				s.moveToBookmark(b);
			}
		}

		save();
		p.css('position', f ? 'relative' : 'static');

		if (f) {
			e.removeClass(c);
			wz.height(this.height);
			w.unbind('resize', resize);
			this.domElem.removeClass('active');
		} else {
			e.addClass(c).removeAttr('style');
			wz.height(w.height() - this.delta).css('width', '100%');
			w.bind('resize', resize);
			this.domElem.addClass('active');
		}
		rte.updateHeight();
		rte.resizable(f);
		restore();

	}

	this.update = function() {
		this.domElem.removeClass('disabled');
	}
}
})(jQuery);
/**
 * @class button - horizontal rule (open dialog window)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.horizontalrule = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.src = {
		width   : $('<input type="text" />').attr({'name' : 'width', 'size' : 4}).css('text-align', 'right'),
		wunit   : $('<select />').attr('name', 'wunit')
					.append($('<option />').val('%').text('%'))
					.append($('<option />').val('px').text('px'))
					.val('%'),
		height  : $('<input type="text" />').attr({'name' : 'height', 'size' : 4}).css('text-align', 'right'),
		bg      : $('<div />'),
		border  : $('<div />'),
		'class' : $('<input type="text" />').css('width', '100%'),
		style   : $('<input type="text" />').css('width', '100%')
	}

	this.command = function() {
		this.src.bg.elColorPicker({palettePosition : 'outer', 'class' : 'el-colorpicker ui-icon ui-icon-pencil'});

		var n   = this.rte.selection.getEnd();
		this.hr = n.nodeName == 'HR' ? $(n) : $(rte.doc.createElement('hr')).css({width : '100%', height : '1px'});
		this.src.border.elBorderSelect({styleHeight : 73, value : this.hr});

		var _w  = this.hr.css('width') || this.hr.attr('width');
		this.src.width.val(parseInt(_w) || 100);
		this.src.wunit.val(_w.indexOf('px') != -1 ? 'px' : '%');

		this.src.height.val( this.rte.utils.toPixels(this.hr.css('height') || this.hr.attr('height')) || 1) ;

		this.src.bg.val(this.rte.utils.color2Hex(this.hr.css('background-color')));
		this.src['class'].val(this.rte.dom.attr(this.hr, 'class'));
		this.src.style.val(this.rte.dom.attr(this.hr, 'style'));

		var opts = {
			rtl : this.rte.rtl,
			submit : function(e, d) { e.stopPropagation(); e.preventDefault(); self.set(); d.close(); },
			dialog : {
				title : this.rte.i18n('Horizontal rule')
			}
		}

		var d = new elDialogForm(opts);
		d.append([this.rte.i18n('Width'),          $('<span />').append(this.src.width).append(this.src.wunit) ], null, true)
			.append([this.rte.i18n('Height'),      $('<span />').append(this.src.height).append(' px')], null, true)
			.append([this.rte.i18n('Border'),      this.src.border], null, true)
			.append([this.rte.i18n('Background'),  this.src.bg], null, true)
			.append([this.rte.i18n('Css class'),   this.src['class']], null, true)
			.append([this.rte.i18n('Css style'),   this.src.style], null, true)
			.open();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		if (this.rte.selection.getEnd().nodeName == 'HR') {
			this.domElem.addClass('active');
		} else {
			this.domElem.removeClass('active');
		}
	}

	this.set = function() {
		this.rte.history.add();
		!this.hr.parentNode && this.rte.selection.insertNode(this.hr.get(0));
		var attr = {
			noshade : true,
			style   : this.src.style.val()
		}
		var b = this.src.border.val();
		var css = {
			width  : (parseInt(this.src.width.val()) || 100)+this.src.wunit.val(),
			height : parseInt(this.src.height.val()) || 1,
			'background-color' : this.src.bg.val(),
			border : b.width && b.style ? b.width+' '+b.style+' '+b.color : ''
		}

		this.hr.removeAttr('class')
			.removeAttr('style')
			.removeAttr('width')
			.removeAttr('height')
			.removeAttr('align')
			.attr(attr)
			.css(css);

		if (this.src['class'].val()) {
			this.hr.attr('class', this.src['class'].val());
		}
		this.rte.ui.update()
	}

}
})(jQuery);
/**
 * @class button - insert/edit image (open dialog window)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.image = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.img = null
	this.init = function() {

		this.labels = {
			main   : 'Properies',
			link   : 'Link',
			adv    : 'Advanced',
			events : 'Events',
			id       : 'ID',
			'class'  : 'Css class',
			style    : 'Css style',
			longdesc : 'Detail description URL',
			href    : 'URL',
			target  : 'Open in',
			title   : 'Title'
		}

		this.src = {
			main : {
				src    : $('<input type="text" />').css('width', '100%'),
				title  : $('<input type="text" />').css('width', '100%'),
				alt    : $('<input type="text" />').css('width', '100%'),
				width  : $('<input type="text" />').attr('size', 5).css('text-align', 'right'),
				height : $('<input type="text" />').attr('size', 5).css('text-align', 'right'),
				margin : $('<div />'),
				align  : $('<select />').css('width', '100%')
							.append($('<option />').val('').text(this.rte.i18n('Not set', 'dialogs')))
							.append($('<option />').val('left'       ).text(this.rte.i18n('Left')))
							.append($('<option />').val('right'      ).text(this.rte.i18n('Right')))
							.append($('<option />').val('top'        ).text(this.rte.i18n('Top')))
							.append($('<option />').val('text-top'   ).text(this.rte.i18n('Text top')))
							.append($('<option />').val('middle'     ).text(this.rte.i18n('middle')))
							.append($('<option />').val('baseline'   ).text(this.rte.i18n('Baseline')))
							.append($('<option />').val('bottom'     ).text(this.rte.i18n('Bottom')))
							.append($('<option />').val('text-bottom').text(this.rte.i18n('Text bottom'))),
				border : $('<div />')
			},

			adv : {
				id       : $('<input type="text" />').css('width', '100%'),
				'class'  : $('<input type="text" />').css('width', '100%'),
				style    : $('<input type="text" />').css('width', '100%'),
				longdesc : $('<input type="text" />').css('width', '100%')
			},
			events : {}
		}

		$.each(
			['onblur', 'onfocus', 'onclick', 'ondblclick', 'onmousedown', 'onmouseup', 'onmouseover', 'onmouseout', 'onmouseleave', 'onkeydown', 'onkeypress', 'onkeyup'],
			function() {
				self.src.events[this] = $('<input type="text" />').css('width', '100%');
		});

		$.each(self.src, function() {
			for (var n in this) {
				this[n].attr('name', n);
			}
		});

	}

	this.command = function() {
		!this.src && this.init();
		this.rte.selection.saveIERange();
		this.src.main.border.elBorderSelect({ change : function() { self.updateImg(); }, name : 'border' });
		this.src.main.margin.elPaddingInput({ type : 'margin' });

		this.cleanValues();
		this.src.main.src.val('');

		var n = this.rte.selection.getEnd(),
			$n = $(n);
		this.preview = null;
		this.prevImg = null;
		this.link    = null;
		if (n.nodeName == 'IMG' && !$n.hasClass('elrte-protected')) {
			this.img     = $(n);
		} else {
			this.img = $(this.rte.doc.createElement('img'));

		}

		var opts = {
			rtl : this.rte.rtl,
			submit : function(e, d) { e.stopPropagation(); e.preventDefault(); self.set(); d.close(); },
			dialog : {
				width    : 570,
				position : 'top',
				title    : this.rte.i18n('Image')
			}
		}
		var d = new elDialogForm(opts);

		if (this.rte.options.fmAllow && this.rte.options.fmOpen) {
			var src = $('<span />').append(this.src.main.src.css('width', '88%'))
					.append(
						$('<span />').addClass('ui-state-default ui-corner-all')
							.css({'float' : 'right', 'margin-right' : '3px'})
							.attr('title', self.rte.i18n('Open file manger'))
							.append($('<span />').addClass('ui-icon ui-icon-folder-open'))
								.click( function() {
									self.rte.options.fmOpen( function(url) { self.src.main.src.val(url).change() } );
								})
								.hover(function() {$(this).addClass('ui-state-hover')}, function() { $(this).removeClass('ui-state-hover')})
						);
		} else {
			var src = this.src.main.src;
		}

		d.tab('main', this.rte.i18n('Properies'))
			.append([this.rte.i18n('Image URL'), src],                 'main', true)
			.append([this.rte.i18n('Title'),     this.src.main.title], 'main', true)
			.append([this.rte.i18n('Alt text'),  this.src.main.alt],   'main', true)
			.append([this.rte.i18n('Size'), $('<span />').append(this.src.main.width).append(' x ').append(this.src.main.height).append(' px')], 'main', true)
			.append([this.rte.i18n('Alignment'), this.src.main.align],  'main', true)
			.append([this.rte.i18n('Margins'),   this.src.main.margin], 'main', true)
			.append([this.rte.i18n('Border'),    this.src.main.border], 'main', true)

		for (var tab in this.src) {
			if (tab != 'main') {
				d.tab(tab, this.rte.i18n(this.labels[tab]));
				for (var name in this.src[tab]) {
					var l = this.rte.i18n(this.labels[name] ? this.labels[name] : name);
					if (tab == 'events') {
						this.src[tab][name].val(this.rte.utils.trimEventCallback(this.img.attr(name)));
					} else if (tab == 'link') {
						if (this.link) {
							this.src[tab][name].val(name == 'href' ? this.rte.utils.absoluteURL(this.link.attr(name)) : this.link.attr(name));
						}
					} else {
						this.src[tab][name].val(this.img.attr(name)||'');
					}
					d.append([l, this.src[tab][name]], tab, true);
				}
			}
		};

		d.open();

		var fs = $('<fieldset />').append($('<legend />').text(this.rte.i18n('Preview')))
		d.append(fs, 'main');
		var frame = document.createElement('iframe');
		$(frame).attr('src', '#').addClass('el-rte-preview').appendTo(fs);

		html = this.rte.options.doctype+'<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /></head><body style="padding:0;margin:0;font-size:9px"> </body></html>';
		frame.contentWindow.document.open();
		frame.contentWindow.document.write(html);
		frame.contentWindow.document.close();
		this.frame = frame.contentWindow.document
		this.preview = $(frame.contentWindow.document.body)
		 				 .text('Proin elit arcu, rutrum commodo, vehicula tempus, commodo a, risus. Curabitur nec arcu. Donec sollicitudin mi sit amet mauris. Nam elementum quam ullamcorper ante. Etiam aliquet massa et lorem. Mauris dapibus lacus auctor risus. Aenean tempor ullamcorper leo. Vivamus sed magna quis ligula eleifend adipiscing. Duis orci. Aliquam sodales tortor vitae ipsum. Aliquam nulla. Duis aliquam molestie erat. Ut et mauris vel pede varius sollicitudin');

		if (this.img.attr('src')) {

			this.prevImg = $(this.frame.createElement('img'))
				.attr('src',  this.rte.utils.absoluteURL(this.img.attr('src')))

			this.prevImg.attr('width', this.img.attr('width'))
				.attr('height', this.img.attr('height'))
				.attr('title', this.img.attr('title')||'')
				.attr('alt', this.img.attr('alt')||'')
				.attr('style', this.img.attr('style')||'')
			for (var n in this.src.adv) {
				var a = this.img.attr(n);
				if (a) {
					this.prevImg.attr(n, a)
				}
			}

			this.preview.prepend(this.prevImg);
			this.updateValues();
		}

		$.each(this.src, function() {
			$.each(this, function() {
				if (this === self.src.main.src) {
					this.bind('change', function() { self.updatePreview(); });
				} else if (this == self.src.main.width || this == self.src.main.height) {
					this.bind('change', function(e) {self.updateDimesions(e);});
				} else {
					this.bind('change', function() { self.updateImg(); });
				}
			});
		});

		// this.src.link.href.change(function() {
		// 	var $this = $(this);
		// 	$this.val(self.rte.utils.absoluteURL($this.val()));
		// });

	}



	/**
	 * Устанавливает значения полей формы из аттрибутов prevImg
	 * Вызывается после загрузки prevImg
	 *
	 **/
	this.updateValues = function() {

		var i = this.prevImg.get(0);

		this.origW = this.prevImg.attr('width');
		this.origH = this.prevImg.attr('height');

		this.src.main.src.val(this.rte.dom.attr(i, 'src'));
		this.src.main.title.val(this.rte.dom.attr(i, 'title'));
		this.src.main.alt.val(this.rte.dom.attr(i, 'alt'));
		this.src.main.width.val(this.origW);
		this.src.main.height.val(this.origH);
		this.src.adv['class'].val(this.rte.dom.attr(i, 'class'));
		this.src.main.margin.val(this.prevImg)
		var f = this.prevImg.css('float');
		this.src.main.align.val(f == 'left' || f == 'right' ? f : (this.prevImg.css('vertical-align')||''));
		this.src.main.border.val(this.prevImg)
		this.src.adv.style.val(this.rte.dom.attr(i, 'style'));
	}

	/**
	 * Очищает поля формы
	 *
	 **/
	this.cleanValues = function() {
		$.each(this.src, function() {
			$.each(this, function() {
				var $this = $(this);
				if ($this.attr('name') != 'src') {
					$this.val('');
				}
			});
		});
	}

	/**
	 * Устанавливает аттрибуты prevImg из полей формы
	 *
	 **/
	this.updateImg = function() {
		this.prevImg.attr({
				style  : $.trim(this.src.adv.style.val()),
				title  : $.trim(this.src.main.title.val()),
				alt    : $.trim(this.src.main.alt.val()),
				width  : parseInt(this.src.main.width.val()),
				height : parseInt(this.src.main.height.val())
			});

		var a = this.src.main.align.val();
		var f = a == 'left' || a == 'right' ? a : '';

		var b = this.src.main.border.val();
		var m = this.src.main.margin.val();
		this.prevImg.css('float', f);
		this.prevImg.css('vertical-align', f ? '' : a);
		this.prevImg.css('border', $.trim(b.width+' '+b.style+' '+b.color));
		if (m.css) {
			this.prevImg.css('margin', m.css);
		} else {
			this.prevImg.css('margin-top', m.top);
			this.prevImg.css('margin-right', m.right);
			this.prevImg.css('margin-bottom', m.bottom);
			this.prevImg.css('margin-left', m.left);
		}

		$.each([this.src.events, this.src.adv], function() {
			$.each(this, function() {
				var $this = $(this);
				var n = $this.attr('name');
				if (n != 'style') {
					var v = $.trim($this.val());
					if (v) {
						self.prevImg.attr(n, v);
					} else {
						self.prevImg.removeAttr(n);
					}
				}
			});
		});

	}

	/**
	 * Обновляет форму выбора изображения
	 *
	 **/
	this.updatePreview = function() {

		var imgsrc = this.prevImg ? this.prevImg.attr('src') : '';
		var src    = $.trim(this.src.main.src.val());
		if (!src || src !=imgsrc) { // new image or empty src
			if (this.prevImg) {
				this.prevImg.remove();
				this.prevImg = null;
			}
			this.cleanValues();
			if (src) {  // new image

				this.prevImg = $(this.frame.createElement('img'))
					.attr('src',  this.rte.utils.absoluteURL(src))
					.bind('load', function() {
						self.updateValues();
					})
				this.preview.prepend(this.prevImg);
				self.updateValues();
			}
		} else { // update existsed image
			this.updateImg();
		}
	}

	this.updateDimesions = function(e) {

		var w = parseInt(this.src.main.width.val())  || 0;
		var h = parseInt(this.src.main.height.val()) || 0;
		if (w > 0 && h > 0) {
			if (e.currentTarget == this.src.main.width.get(0)) {

				this.src.main.height.val(parseInt(w*this.origH/this.origW));
			} else {
				this.src.main.width.val(parseInt(h*this.origW/this.origH));
			}
		} else {
			this.src.main.width.val(this.origW);
			this.src.main.height.val(this.origH);
		}

		this.updateImg();

	}

	this.set = function() {
		this.rte.history.add();
		this.rte.selection.restoreIERange();
		if (!this.prevImg || !this.prevImg.attr('width')) {
			this.img  && this.img.remove();
			this.link && this.rte.doc.execCommand('unlink', false, null);
		} else {
			if (!this.img[0].parentNode) {
				this.img = $(this.rte.doc.createElement('img'));
			}

			this.img.attr({
					src    : this.rte.utils.absoluteURL($.trim(this.src.main.src.val())),
					style  : $.trim(this.rte.dom.attr(this.prevImg.get(0), 'style')),
					title  : $.trim(this.src.main.title.val()),
					alt    : $.trim(this.src.main.alt.val()),
					width  : parseInt(this.src.main.width.val()),
					height : parseInt(this.src.main.height.val())
				});

			for (var _n in this.src.adv) {
				if (_n != 'style') {
					var val = this.src.adv[_n].val();
					if (val) {
						this.img.attr(_n, val);
					} else {
						this.img.removeAttr(_n)
					}

				}
			}
			for (var _n in this.src.events) {
				var val = this.src.events[_n].val();
				if (val) {
					this.img.attr(_n, val);
				} else {
					this.img.removeAttr(_n)
				}
			}

			if (!this.img[0].parentNode) {
				this.rte.selection.insertNode(this.img.get(0))
			}
		}
		this.rte.ui.update();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		var n = this.rte.selection.getEnd(),
			$n = $(n);
		if (n.nodeName == 'IMG' && !$n.hasClass('elrte-protected')) {
			this.domElem.addClass('active');
		} else {
			this.domElem.removeClass('active');
		}
	}

}
})(jQuery);
/**
 * @class Увеличение отступа
 * списки - если выделен один элемент - увеличивается вложенность списка, в остальных случаях - padding у родительского ul|ol
 * Если таблица выделена полностью - ей добавляется margin, если частично - увеличивается padding для ячеек
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.indent = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;

	this.command = function() {
		this.rte.history.add();
		var nodes = this.rte.selection.selected({collapsed : true, blocks : true, wrap : 'inline', tag : 'p'});

		function indent(n) {
			var css = /(IMG|HR|TABLE|EMBED|OBJECT)/.test(n.nodeName) ? 'margin-left' : 'padding-left';
			var val = self.rte.dom.attr(n, 'style').indexOf(css) != -1 ? parseInt($(n).css(css))||0 : 0;
			$(n).css(css, val+40+'px');
		}

		for (var i=0; i < nodes.length; i++) {
			if (/^(TABLE|THEAD|TFOOT|TBODY|COL|COLGROUP|TR)$/.test(nodes[i].nodeName)) {
				$(nodes[i]).find('td,th').each(function() {
					indent(this);
				});
			} else if (/^LI$/.test(nodes[i].nodeName)) {
				var n = $(nodes[i]);
				$(this.rte.dom.create(nodes[i].parentNode.nodeName))
					.append($(this.rte.dom.create('li')).html(n.html()||'')).appendTo(n.html('&nbsp;'));
			} else {
				indent(nodes[i]);
			}
		};
		this.rte.ui.update();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
	}

}
})(jQuery);
/**
 * @class button - justify text
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.justifyleft = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	this.align = this.name == 'justifyfull' ? 'justify' : this.name.replace('justify', '');

	this.command = function() {
		var s = this.rte.selection.selected({collapsed:true, blocks : true, tag : 'div'}),
			l = s.length;
		l && this.rte.history.add();
		while (l--) {
			this.rte.dom.filter(s[l], 'textNodes') && $(s[l]).css('text-align', this.align);
		}
		this.rte.ui.update();
	}

	this.update = function() {
		var s = this.rte.selection.getNode(),
			n = s.nodeName == 'BODY' ? s : this.rte.dom.selfOrParent(s, 'textNodes')||(s.parentNode && s.parentNode.nodeName == 'BODY' ? s.parentNode : null);
		if (n) {
			this.domElem.removeClass('disabled').toggleClass('active', $(n).css('text-align') == this.align);
		} else {
			this.domElem.addClass('disabled');
		}
	}

}

elRTE.prototype.ui.prototype.buttons.justifycenter = elRTE.prototype.ui.prototype.buttons.justifyleft;
elRTE.prototype.ui.prototype.buttons.justifyright  = elRTE.prototype.ui.prototype.buttons.justifyleft;
elRTE.prototype.ui.prototype.buttons.justifyfull   = elRTE.prototype.ui.prototype.buttons.justifyleft;

})(jQuery);
/**
 * @class button - insert/edit link (open dialog window)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.link = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.img = false;

	function init() {
		self.labels = {
			id        : 'ID',
			'class'   : 'Css class',
			style     : 'Css style',
			dir       : 'Script direction',
			lang      : 'Language',
			charset   : 'Charset',
			type      : 'Target MIME type',
			rel       : 'Relationship page to target (rel)',
			rev       : 'Relationship target to page (rev)',
			tabindex  : 'Tab index',
			accesskey : 'Access key'
		}
		self.src = {
			main : {
				href   : $('<input type="text" />'),
				title  : $('<input type="text" />'),
				anchor : $('<select />').attr('name', 'anchor'),
				target : $('<select />')
					.append($('<option />').text(self.rte.i18n('In this window')).val(''))
					.append($('<option />').text(self.rte.i18n('In new window (_blank)')).val('_blank'))
				// 	.append($('<option />').text(self.rte.i18n('In new parent window (_parent)')).val('_parent'))
				// 	.append($('<option />').text(self.rte.i18n('In top frame (_top)')).val('_top'))
			},

			popup : {
				use        : $('<input type="checkbox />"'),
				url        : $('<input type="text" />'    ).val('http://'),
				name       : $('<input type="text" />'    ),
				width      : $('<input type="text" />'    ).attr({size : 6, title : self.rte.i18n('Width')} ).css('text-align', 'right'),
				height     : $('<input type="text" />'    ).attr({size : 6, title : self.rte.i18n('Height')}).css('text-align', 'right'),
				left       : $('<input type="text" />'    ).attr({size : 6, title : self.rte.i18n('Left')}  ).css('text-align', 'right'),
				top        : $('<input type="text" />'    ).attr({size : 6, title : self.rte.i18n('Top')}   ).css('text-align', 'right'),
				location   : $('<input type="checkbox" />'),
				menubar    : $('<input type="checkbox" />'),
				toolbar    : $('<input type="checkbox" />'),
				scrollbars : $('<input type="checkbox" />'),
				status     : $('<input type="checkbox" />'),
				resizable  : $('<input type="checkbox" />'),
				dependent  : $('<input type="checkbox" />'),
				retfalse   : $('<input type="checkbox" />').attr('checked', true)
			},

			adv : {
				id        : $('<input type="text" />'),
				'class'   : $('<input type="text" />'),
				style     : $('<input type="text" />'),
				dir       : $('<select />')
							.append($('<option />').text(self.rte.i18n('Not set')).val(''))
							.append($('<option />').text(self.rte.i18n('Left to right')).val('ltr'))
							.append($('<option />').text(self.rte.i18n('Right to left')).val('rtl')),
				lang      : $('<input type="text" />'),
				charset   : $('<input type="text" />'),
				type      : $('<input type="text" />'),
				rel       : $('<input type="text" />'),
				rev       : $('<input type="text" />'),
				tabindex  : $('<input type="text" />'),
				accesskey : $('<input type="text" />')
			},
			events : {}
		}

		$.each(
			['onblur', 'onfocus', 'onclick', 'ondblclick', 'onmousedown', 'onmouseup', 'onmouseover', 'onmouseout', 'onmouseleave', 'onkeydown', 'onkeypress', 'onkeyup'],
			function() {
				self.src.events[this] = $('<input type="text" />');
		});

		$.each(self.src, function() {
			for (var n in this) {
				this[n].attr('name', n);
				var t = this[n].attr('type');
				if (!t || (t == 'text'  && !this[n].attr('size')) ) {
					this[n].css('width', '100%');
				}
			}
		});

	}

	this.command = function() {
		var n = this.rte.selection.getNode(),
			sel, i, v, opts, l, r, link, href, s;

		!this.src && init();
		this.rte.selection.saveIERange();

		function isLink(n) { return n.nodeName == 'A' && n.href; }

		this.link = this.rte.dom.selfOrParentLink(n);

		if (!this.link) {
			sel = $.browser.msie ? this.rte.selection.selected() : this.rte.selection.selected({wrap : false});
			if (sel.length) {
				for (i=0; i < sel.length; i++) {
					if (isLink(sel[i])) {
						this.link = sel[i];
						break;
					}
				};
				if (!this.link) {
					this.link = this.rte.dom.parent(sel[0], isLink) || this.rte.dom.parent(sel[sel.length-1], isLink);
				}
			}
		}

		this.link = this.link ? $(this.link) : $(this.rte.doc.createElement('a'));
		this.img = n.nodeName == 'IMG' ? n : null;
		this.updatePopup();

		this.src.main.anchor.empty();
		$('a[href!=""][name]', this.rte.doc).each(function() {
			var n = $(this).attr('name');
			self.src.main.anchor.append($('<option />').val(n).text(n));
		});
		if (this.src.main.anchor.children().length) {
			this.src.main.anchor.prepend($('<option />').val('').text(this.rte.i18n('Select bookmark')) )
				.change(function() {
					var v = $(this).val();
					if (v) {
						self.src.main.href.val('#'+v);
					}
				});
		}

		opts = {
			rtl : this.rte.rtl,
			submit : function(e, d) { e.stopPropagation(); e.preventDefault(); self.set(); d.close(); },
			tabs : { show : function(e, ui) { if (ui.index==3) { self.updateOnclick(); } } },
			close : function() {self.rte.browser.msie && self.rte.selection.restoreIERange(); },
			dialog : {
				width : 'auto',
				width : 430,
				title : this.rte.i18n('Link')

			}
		}
		d = new elDialogForm(opts);

		l = $('<div />')
			.append( $('<label />').append(this.src.popup.location).append(this.rte.i18n('Location bar')))
			.append( $('<label />').append(this.src.popup.menubar).append(this.rte.i18n('Menu bar')))
			.append( $('<label />').append(this.src.popup.toolbar).append(this.rte.i18n('Toolbar')))
			.append( $('<label />').append(this.src.popup.scrollbars).append(this.rte.i18n('Scrollbars')));
		r = $('<div />')
			.append( $('<label />').append(this.src.popup.status).append(this.rte.i18n('Status bar')))
			.append( $('<label />').append(this.src.popup.resizable).append(this.rte.i18n('Resizable')))
			.append( $('<label />').append(this.src.popup.dependent).append(this.rte.i18n('Depedent')))
			.append( $('<label />').append(this.src.popup.retfalse).append(this.rte.i18n('Add return false')));

		d.tab('main', this.rte.i18n('Properies'))
			.tab('popup',  this.rte.i18n('Popup'))
			.tab('adv',    this.rte.i18n('Advanced'))
			.tab('events', this.rte.i18n('Events'))
			.append($('<label />').append(this.src.popup.use).append(this.rte.i18n('Open link in popup window')), 'popup')
			.separator('popup')
			.append([this.rte.i18n('URL'),  this.src.popup.url],  'popup', true)
			.append([this.rte.i18n('Window name'), this.src.popup.name], 'popup', true)
			.append([this.rte.i18n('Window size'), $('<span />').append(this.src.popup.width).append(' x ').append(this.src.popup.height).append(' px')], 'popup', true)
			.append([this.rte.i18n('Window position'), $('<span />').append(this.src.popup.left).append(' x ').append(this.src.popup.top).append(' px')], 'popup', true)
			.separator('popup')
			.append([l, r], 'popup', true);

		link = this.link.get(0);
		href = this.rte.dom.attr(link, 'href');
		this.src.main.href.val(href).change(function() {
			$(this).val(self.rte.utils.absoluteURL($(this).val()));
		});

		if (this.rte.options.fmAllow && this.rte.options.fmOpen) {
			var s = $('<span />').append(this.src.main.href.css('width', '87%'))
				.append(
					$('<span />').addClass('ui-state-default ui-corner-all')
						.css({'float' : 'right', 'margin-right' : '3px'})
						.attr('title', self.rte.i18n('Open file manger'))
						.append($('<span />').addClass('ui-icon ui-icon-folder-open'))
							.click( function() {
								self.rte.options.fmOpen( function(url) { self.src.main.href.val(url).change(); } );
							})
							.hover(function() {$(this).addClass('ui-state-hover')}, function() { $(this).removeClass('ui-state-hover')})
				);
			d.append([this.rte.i18n('Link URL'), s], 'main', true);
		} else {
			d.append([this.rte.i18n('Link URL'), this.src.main.href], 'main', true);
		}
		this.src.main.href.change();

		d.append([this.rte.i18n('Title'), this.src.main.title.val(this.rte.dom.attr(link, 'title'))], 'main', true);
		if (this.src.main.anchor.children().length) {
			d.append([this.rte.i18n('Bookmark'), this.src.main.anchor.val(href)], 'main', true)
		}
		if (!this.rte.options.doctype.match(/xhtml/)) {
			d.append([this.rte.i18n('Target'), this.src.main.target.val(this.link.attr('target')||'')], 'main', true);
		}



		for (var n in this.src.adv) {
			this.src.adv[n].val(this.rte.dom.attr(link, n));
			d.append([this.rte.i18n(this.labels[n] ? this.labels[n] : n), this.src.adv[n]], 'adv', true);
		}
		for (var n in this.src.events) {
			var v = this.rte.utils.trimEventCallback(this.rte.dom.attr(link, n));
			this.src.events[n].val(v);
			d.append([this.rte.i18n(this.labels[n] ? this.labels[n] : n), this.src.events[n]], 'events', true);
		}

		this.src.popup.use.change(function() {
			var c = $(this).attr('checked');
			$.each(self.src.popup, function() {
				if ($(this).attr('name') != 'use') {
					if (c) {
						$(this).removeAttr('disabled');
					} else {
						$(this).attr('disabled', true);
					}
				}
			})
		});
		this.src.popup.use.change();
		d.open();
	}

	this.update = function() {
		var n = this.rte.selection.getNode();

		// var t = this.rte.dom.selectionHas(function(n) { return n.nodeName == 'A' && n.href; });
		// this.rte.log(t)

		if (this.rte.dom.selfOrParentLink(n)) {
			this.domElem.removeClass('disabled').addClass('active');
		} else if (this.rte.dom.selectionHas(function(n) { return n.nodeName == 'A' && n.href; })) {
			this.domElem.removeClass('disabled').addClass('active');
		} else if (!this.rte.selection.collapsed() || n.nodeName == 'IMG') {
			this.domElem.removeClass('disabled active');
		} else {
			this.domElem.addClass('disabled').removeClass('active');
		}
	}

	this.updatePopup = function() {
		var onclick = this.rte.dom.attr(this.link.get(0), 'onclick');
		onclick = onclick ? $.trim(onclick.toString()) : ''
		if ( onclick.length>0 && (m = onclick.match(/window.open\("([^"]+)",\s*"([^"]*)",\s*"([^"]*)"\s*.*\);\s*(return\s+false)?/))) {
			this.src.popup.use.attr('checked', 'on')
			this.src.popup.url.val(m[1]);
			this.src.popup.name.val(m[2]);

			if ( /location=yes/.test(m[3]) ) {
				this.src.popup.location.attr('checked', true);
			}
			if ( /menubar=yes/.test(m[3]) ) {
				this.src.popup.menubar.attr('checked', true);
			}
			if ( /toolbar=yes/.test(m[3]) ) {
				this.src.popup.toolbar.attr('checked', true);
			}
			if ( /scrollbars=yes/.test(m[3]) ) {
				this.src.popup.scrollbars.attr('checked', true);
			}
			if ( /status=yes/.test(m[3]) ) {
				this.src.popup.status.attr('checked', true);
			}
			if ( /resizable=yes/.test(m[3]) ) {
				this.src.popup.resizable.attr('checked', true);
			}
			if ( /dependent=yes/.test(m[3]) ) {
				this.src.popup.dependent.attr('checked', true);
			}
			if ((_m = m[3].match(/width=([^,]+)/))) {
				this.src.popup.width.val(_m[1]);
			}
			if ((_m = m[3].match(/height=([^,]+)/))) {
				this.src.popup.height.val(_m[1]);
			}
			if ((_m = m[3].match(/left=([^,]+)/))) {
				this.src.popup.left.val(_m[1]);
			}
			if ((_m = m[3].match(/top=([^,]+)/))) {
				this.src.popup.top.val(_m[1]);
			}
			if (m[4]) {
				this.src.popup.retfalse.attr('checked', true);
			}
		} else {
			$.each(this.src.popup, function() {
				var $this = $(this);
				if ($this.attr('type') == 'text') {
					$this.val($this.attr('name') == 'url' ? 'http://' : '');
				} else {
					if ($this.attr('name') == 'retfalse') {
						this.attr('checked', true);
					} else {
						$this.removeAttr('checked');
					}
				}
			});
		}

	}

	this.updateOnclick = function () {
		var url = this.src.popup.url.val();
		if (this.src.popup.use.attr('checked') && url) {
			var params = '';
			if (this.src.popup.location.attr('checked')) {
				params += 'location=yes,';
			}
			if (this.src.popup.menubar.attr('checked')) {
				params += 'menubar=yes,';
			}
			if (this.src.popup.toolbar.attr('checked')) {
				params += 'toolbar=yes,';
			}
			if (this.src.popup.scrollbars.attr('checked')) {
				params += 'scrollbars=yes,';
			}
			if (this.src.popup.status.attr('checked')) {
				params += 'status=yes,';
			}
			if (this.src.popup.resizable.attr('checked')) {
				params += 'resizable=yes,';
			}
			if (this.src.popup.dependent.attr('checked')) {
				params += 'dependent=yes,';
			}
			if (this.src.popup.width.val()) {
				params += 'width='+this.src.popup.width.val()+',';
			}
			if (this.src.popup.height.val()) {
				params += 'height='+this.src.popup.height.val()+',';
			}
			if (this.src.popup.left.val()) {
				params += 'left='+this.src.popup.left.val()+',';
			}
			if (this.src.popup.top.val()) {
				params += 'top='+this.src.popup.top.val()+',';
			}
			if (params.length>0) {
				params = params.substring(0, params.length-1)
			}
			var retfalse = this.src.popup.retfalse.attr('checked') ? 'return false;' : '';
			var onclick = 'window.open("'+url+'", "'+$.trim(this.src.popup.name.val())+'", "'+params+'");'+retfalse;
			this.src.events.onclick.val(onclick);
			if (!this.src.main.href.val()) {
				this.src.main.href.val('#');
			}
		} else {
			var v = this.src.events.onclick.val();
			v = v.replace(/window\.open\([^\)]+\)\s*;?\s*return\s*false\s*;?/i, '');
			this.src.events.onclick.val(v);
		}
	}

	this.set = function() {
		var href, fakeURL;
		this.updateOnclick();
		this.rte.selection.restoreIERange();
		this.rte.history.add();
		href = this.rte.utils.absoluteURL(this.src.main.href.val());
		if (!href) {
			// this.link.parentNode && this.rte.doc.execCommand('unlink', false, null);
			var bm = this.rte.selection.getBookmark();
			this.rte.dom.unwrap(this.link[0]);
			this.rte.selection.moveToBookmark(bm)
		} else {
			if (!this.link[0].parentNode) {
				if (this.img && this.img.parentNode) {
					this.link = $(this.rte.dom.create('a')).attr('href', href);
					this.rte.dom.wrap(this.img, this.link[0]);
				} else {
					fakeURL = '#--el-editor---'+Math.random();
					this.rte.doc.execCommand('createLink', false, fakeURL);
					this.link = $('a[href="'+fakeURL+'"]', this.rte.doc);
					this.link.each(function() {
						var $this = $(this);
						// удаляем ссылки вокруг пустых элементов
						if (!$.trim($this.html()) && !$.trim($this.text())) {
							$this.replaceWith($this.text()); //  сохраняем пробелы :)
						}
					});
				}
			}

			this.src.main.href.val(href);
			for (var tab in this.src) {
				if (tab != 'popup') {
					for (var n in this.src[tab]) {
						if (n != 'anchors') {
							var v = $.trim(this.src[tab][n].val());
							if (v) {
								this.link.attr(n, v);
							} else {
								this.link.removeAttr(n);
							}
						}
					}
				}
			};
			this.img && this.rte.selection.select(this.img);
		}
		this.rte.ui.update(true);
	}

}

})(jQuery);
/**
 * @class button - insert non breakable space
 * Если выделение схлопнуто и находится внутри div'a - он удаляется
 * Новые div'ы создаются только из несхлопнутого выделения
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.nbsp = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		this.rte.history.add();
		this.rte.selection.insertHtml('&nbsp;', true);
		this.rte.window.focus();
		this.rte.ui.update();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
	}
}
})(jQuery);

/**
 * @class button - outdent text
 * уменьшает padding/margin/самомнение ;)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 * @todo decrease lists nesting level!
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.outdent = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;

	this.command = function() {
		var v = this.find();
		if (v.node) {
			this.rte.history.add();
			$(v.node).css(v.type, (v.val>40 ? v.val-40 : 0)+'px');
			this.rte.ui.update();
		}
	}

	this.find = function(n) {
		function checkNode(n) {
			var ret = {type : '', val : 0};
			var s;
			if ((s = self.rte.dom.attr(n, 'style'))) {
				ret.type = s.indexOf('padding-left') != -1
					? 'padding-left'
					: (s.indexOf('margin-left') != -1 ? 'margin-left' : '');
				ret.val = ret.type ? parseInt($(n).css(ret.type))||0 : 0;
			}
			return ret;
		}

		var n = this.rte.selection.getNode();
		var ret = checkNode(n);
		if (ret.val) {
			ret.node = n;
		} else {
			$.each(this.rte.dom.parents(n, '*'), function() {
				ret = checkNode(this);
				if (ret.val) {
					ret.node = this;
					return ret;
				}
			})
		}
		return ret;
	}

	this.update = function() {
		var v = this.find();
		if (v.node) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}


}

})(jQuery);
(function($) {
	elRTE.prototype.ui.prototype.buttons.pagebreak = function(rte, name) {
		this.constructor.prototype.constructor.call(this, rte, name);

		// prevent resize
		$(this.rte.doc.body).bind('mousedown', function(e) {
			if ($(e.target).hasClass('elrte-pagebreak')) {
				e.preventDefault();
			}
		})

		this.command = function() {
			this.rte.selection.insertHtml('<img src="'+this.rte.filter.url+'pixel.gif" class="elrte-protected elrte-pagebreak"/>', false);
		}

		this.update = function() {
			this.domElem.removeClass('disabled');
		}
	}

})(jQuery);/**
 * @class button - insert formatted text (open dialog window)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.pasteformattext = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	this.iframe = $(document.createElement('iframe')).addClass('el-rte-paste-input');
	this.doc    = null;
	var self    = this;

	this.command = function() {
		this.rte.selection.saveIERange();
		var self = this,
			opts = {
			submit : function(e, d) {
				e.stopPropagation();
				e.preventDefault();
				self.paste();
				d.close();
			},
			dialog : {
				width : 500,
				title : this.rte.i18n('Paste formatted text')
			}
		},
		d = new elDialogForm(opts);
		d.append(this.iframe).open();
		this.doc = this.iframe.get(0).contentWindow.document;
		html = this.rte.options.doctype
			+'<html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />';
		html += '</head><body> <br /> </body></html>';

		this.doc.open();
		this.doc.write(html);
		this.doc.close();

		if (!this.rte.browser.msie) {
			try { this.doc.designMode = "on"; }
			catch(e) { }
		} else {
			this.doc.body.contentEditable = true;
		}
		setTimeout(function() { self.iframe[0].contentWindow.focus(); }, 50);
	}

	this.paste = function() {
		$(this.doc.body).find('[class]').removeAttr('class');
		var html = $.trim($(this.doc.body).html());
		if (html) {
			this.rte.history.add();
			this.rte.selection.restoreIERange();
			this.rte.selection.insertHtml(this.rte.filter.wysiwyg2wysiwyg(this.rte.filter.proccess('paste', html)));
			this.rte.ui.update(true);
		}
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
	}
}
})(jQuery);

/**
 * @class кнопка "вставить только текст"
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.pastetext = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	this.input = $('<textarea />').addClass('el-rte-paste-input');
	var self   = this;

	this.command = function() {
		this.rte.browser.msie && this.rte.selection.saveIERange();
		var opts = {
			submit : function(e, d) {
				e.stopPropagation();
				e.preventDefault();
				self.paste();
				d.close();
			},
			dialog : {
				width : 500,
				title : this.rte.i18n('Paste only text')
			}
		}
		var d = new elDialogForm(opts);
		d.append(this.input).open();
	}

	this.paste = function() {
		var txt = $.trim(this.input.val());
		if (txt) {
			this.rte.history.add();
			this.rte.browser.msie && this.rte.selection.restoreIERange();
			this.rte.selection.insertText(txt.replace(/\r?\n/g, '<br />'), true);
			this.rte.ui.update(true);
		}
		this.input.val('');
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
	}

}
})(jQuery);
/**
 * @class button - save editor content (submit form)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.save = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	this.active = true;

	this.command = function() {
		this.rte.save();
	}

	this.update = function() { }
}
})(jQuery);
/**
 * @class button - insert smiley (open dialog window)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    eSabbath
 *
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.smiley = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.img = null;
	this.url = this.rte.filter.url+'smileys/';
	this.smileys = {
		'smile' : 'smile.png',
		'happy' : 'happy.png',
		'tongue' : 'tongue.png',
		'surprised' : 'surprised.png',
		'waii' : 'waii.png',
		'wink' : 'wink.png',
		'evilgrin' : 'evilgrin.png',
		'grin' : 'grin.png',


		'unhappy' : 'unhappy.png'
	};
	this.width = 120;

	this.command = function() {
		var self = this, url = this.url, d, opts, img;

		this.rte.browser.msie && this.rte.selection.saveIERange();

		opts = {
			dialog : {
				height  : 120,
				width   : this.width,
				title   : this.rte.i18n('Smiley'),
				buttons : {}
			}
		}
		d = new elDialogForm(opts);
		$.each(this.smileys, function(name, img) {
			d.append($('<img src="'+url+img+'" title="'+name+'" id="'+name+'" class="el-rte-smiley"/>').click(function() { self.set(this.id, d);	}));
		});
		d.open();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		this.domElem.removeClass('active');
	}

	this.set = function(s, d) {
		this.rte.browser.msie && this.rte.selection.restoreIERange();
		if (this.smileys[s]) {
			this.img = $(this.rte.doc.createElement('img'));
			this.img.attr({
				src    : this.url + this.smileys[s],
				title  : s,
				alt    : s
			});
			this.rte.selection.insertNode(this.img.get(0));
			this.rte.ui.update();
		}
		d.close();
	}
}
})(jQuery);
/**
 * @class button - stops elements floating. Insert div with style="clear:all"
 * Если выделение схлопнуто и находится внутри div'a с аттрибутом или css clear - он удаляется
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.stopfloat = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.find = function() {
		if (this.rte.selection.collapsed()) {
			var n = this.rte.dom.selfOrParent(this.rte.selection.getEnd(), /^DIV$/);
			if (n && (this.rte.dom.attr(n, 'clear') || $(n).css('clear') != 'none')) {
				return n;
			}
		}
	}

	this.command = function() {
		var n;
		if ((n = this.find())) {
			var n = $(n);
			this.rte.history.add();
			if (!n.children().length && !$.trim(n.text()).length) {
				n.remove();
			} else {
				n.removeAttr('clear').css('clear', '');
			}
		} else {
			this.rte.history.add();
			this.rte.selection.insertNode($(this.rte.dom.create('div')).css('clear', 'both').get(0), true);
		}
		this.rte.ui.update(true);
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		if (this.find()) {
			this.domElem.addClass('active');
		} else {
			this.domElem.removeClass('active');
		}
	}
}

})(jQuery);/**
 * @class button - create/edit table (open dialog window)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * Copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.table = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self    = this;
	this.src    = null;
	this.labels = null;

	function init() {
		self.labels = {
			main      : 'Properies',
			adv       : 'Advanced',
			events    : 'Events',
			id        : 'ID',
			'class'   : 'Css class',
			style     : 'Css style',
			dir       : 'Script direction',
			summary   : 'Summary',
			lang      : 'Language',
			href      : 'URL'
		}

		self.src = {
			main : {
				caption : $('<input type="text" />'),
				rows    : $('<input type="text" />').attr('size', 5).val(2),
				cols    : $('<input type="text" />').attr('size', 5).val(2),
				width   : $('<input type="text" />').attr('size', 5),
				wunit   : $('<select />')
							.append($('<option />').val('%').text('%'))
							.append($('<option />').val('px').text('px')),
				height  : $('<input type="text" />').attr('size', 5),
				hunit   : $('<select />')
							.append($('<option />').val('%').text('%'))
							.append($('<option />').val('px').text('px')),
				align   : $('<select />')
							.append($('<option />').val('').text(self.rte.i18n('Not set')))
							.append($('<option />').val('left').text(self.rte.i18n('Left')))
							.append($('<option />').val('center').text(self.rte.i18n('Center')))
							.append($('<option />').val('right').text(self.rte.i18n('Right'))),
				spacing : $('<input type="text" />').attr('size', 5),
				padding : $('<input type="text" />').attr('size', 5),
				border  : $('<div />'),
				// frame   : $('<select />')
				// 			.append($('<option />').val('void').text(self.rte.i18n('No')))
				// 			.append($('<option />').val('border').text(self.rte.i18n('Yes'))),
				rules   : $('<select />')
							.append($('<option />').val('none').text(self.rte.i18n('No')))
							.append($('<option />').val('all').text(self.rte.i18n('Cells')))
							.append($('<option />').val('groups').text(self.rte.i18n('Groups')))
							.append($('<option />').val('rows').text(self.rte.i18n('Rows')))
							.append($('<option />').val('cols').text(self.rte.i18n('Columns'))),
				margin  : $('<div />'),
				bg      : $('<div />'),
				bgimg   : $('<input type="text" />').css('width', '90%')
			},

			adv : {
				id        : $('<input type="text" />'),
				summary   : $('<input type="text" />'),
				'class'   : $('<input type="text" />'),
				style     : $('<input type="text" />'),
				dir       : $('<select />')
								.append($('<option />').text(self.rte.i18n('Not set')).val(''))
								.append($('<option />').text(self.rte.i18n('Left to right')).val('ltr'))
								.append($('<option />').text(self.rte.i18n('Right to left')).val('rtl')),
				lang      : $('<input type="text" />')
			},

			events : {}
		}

		$.each(self.src, function() {
			for (var n in this) {
				this[n].attr('name', n);
				var t = this[n].get(0).nodeName;
				if (t == 'INPUT' && n != 'bgimg') {
					this[n].css(this[n].attr('size') ? {'text-align' : 'right'} : {width : '100%'});
				} else if (t == 'SELECT' && n!='wunit' && n!='hunit') {
					this[n].css('width', '100%');
				}
			}
		});

		$.each(
			['onblur', 'onfocus', 'onclick', 'ondblclick', 'onmousedown', 'onmouseup', 'onmouseover', 'onmouseout', 'onmouseleave', 'onkeydown', 'onkeypress', 'onkeyup'],
			function() {
				self.src.events[this] = $('<input type="text" />').attr('name', this).css('width', '100%');
		});

		self.src.main.align.change(function() {
			var v = $(this).val();
			if (v == 'center') {
				self.src.main.margin.val({left : 'auto', right : 'auto'});
			} else {
				var m = self.src.main.margin.val();
				if (m.left == 'auto' && m.right == 'auto') {
					self.src.main.margin.val({left : '', right : ''});
				}
			}
		});

		self.src.main.bgimg.change(function() {
			var t = $(this);
			t.val(self.rte.utils.absoluteURL(t.val()));
		})

	}

	this.command = function() {
		var n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^TABLE$/);

		if (this.name == 'table') {
			this.table = $(this.rte.doc.createElement('table'));
		} else {
			this.table = n ? $(n) : $(this.rte.doc.createElement('table'));
		}

		!this.src && init();
		this.src.main.border.elBorderSelect({styleHeight : 117});
		this.src.main.bg.elColorPicker({palettePosition : 'outer', 'class' : 'el-colorpicker ui-icon ui-icon-pencil'});
		this.src.main.margin.elPaddingInput({ type : 'margin', value : this.table});

		if (this.table.parents().length) {
			this.src.main.rows.val('').attr('disabled', true);
			this.src.main.cols.val('').attr('disabled', true);
		} else {
			this.src.main.rows.val(2).removeAttr('disabled');
			this.src.main.cols.val(2).removeAttr('disabled');
		}

		var w = this.table.css('width') || this.table.attr('width');
		this.src.main.width.val(parseInt(w)||'');
		this.src.main.wunit.val(w.indexOf('px') != -1 ? 'px' : '%');

		var h = this.table.css('height') || this.table.attr('height');
		this.src.main.height.val(parseInt(h)||'');
		this.src.main.hunit.val(h && h.indexOf('px') != -1 ? 'px' : '%');

		var f = this.table.css('float');
		this.src.main.align.val('');
		if (f == 'left' || f == 'right') {
			this.src.main.align.val(f);
		} else {
			var ml = this.table.css('margin-left');
			var mr = this.table.css('margin-right');
			if (ml == 'auto' && mr == 'auto') {
				this.src.main.align.val('center');
			}
		}

		this.src.main.border.val(this.table);
		//this.src.main.frame.val(this.table.attr('frame'));
		this.src.main.rules.val(this.rte.dom.attr(this.table.get(0), 'rules'));

		this.src.main.bg.val(this.table.css('background-color'));
		var bgimg = (this.table.css('background-image')||'').replace(/url\(([^\)]+)\)/i, "$1");
		this.src.main.bgimg.val(bgimg!='none' ? bgimg : '');

		var opts = {
			rtl : this.rte.rtl,
			submit : function(e, d) { e.stopPropagation(); e.preventDefault(); self.set(); d.close(); },
			dialog : {
				width : 530,
				title : this.rte.i18n('Table')
			}
		}
		var d = new elDialogForm(opts);

		for (var tab in this.src) {
			d.tab(tab, this.rte.i18n(this.labels[tab]));
			if (tab == 'main') {
				var t1 = $('<table />')
					.append($('<tr />').append('<td>'+this.rte.i18n('Rows')+'</td>').append($('<td />').append(this.src.main.rows)))
					.append($('<tr />').append('<td>'+this.rte.i18n('Columns')+'</td>').append($('<td />').append(this.src.main.cols)));
				var t2 = $('<table />')
					.append($('<tr />').append('<td>'+this.rte.i18n('Width')+'</td>').append($('<td />').append(this.src.main.width).append(this.src.main.wunit)))
					.append($('<tr />').append('<td>'+this.rte.i18n('Height')+'</td>').append($('<td />').append(this.src.main.height).append(this.src.main.hunit)));
				var t3 = $('<table />')
					.append($('<tr />').append('<td>'+this.rte.i18n('Spacing')+'</td>').append($('<td />').append(this.src.main.spacing.val(this.table.attr('cellspacing')||''))))
					.append($('<tr />').append('<td>'+this.rte.i18n('Padding')+'</td>').append($('<td />').append(this.src.main.padding.val(this.table.attr('cellpadding')||''))));

				d.append([this.rte.i18n('Caption'), this.src.main.caption.val(this.table.find('caption').eq(0).text() || '')], 'main', true)
					.separator('main')
					.append([t1, t2, t3], 'main', true)
					.separator('main')
					.append([this.rte.i18n('Border'),        this.src.main.border], 'main', true)
					//.append([this.rte.i18n('Frame'),       this.src.main.frame], 'main', true)
					.append([this.rte.i18n('Inner borders'), this.src.main.rules], 'main', true)
					.append([this.rte.i18n('Alignment'),     this.src.main.align], 'main', true)
					.append([this.rte.i18n('Margins'),       this.src.main.margin], 'main', true)
					.append([this.rte.i18n('Background'),    $('<span />').append($('<span />').css({'float' : 'left', 'margin-right' : '3px'}).append(this.src.main.bg)).append(this.src.main.bgimg)], 'main', true)
			} else {
				for (var name in this.src[tab]) {
					var v = this.rte.dom.attr(this.table, name);
					if (tab == 'events') {
						v = this.rte.utils.trimEventCallback(v);
					}
					d.append([this.rte.i18n(this.labels[name] ? this.labels[name] : name), this.src[tab][name].val(v)], tab, true);
				}
			}
		}

		d.open();
	}

	this.set = function() {

		if (!this.table.parents().length) {
			var r = parseInt(this.src.main.rows.val()) || 0;
			var c = parseInt(this.src.main.cols.val()) || 0;
			if (r<=0 || c<=0) {
				return;
			}
			this.rte.history.add();
			var b = $(this.rte.doc.createElement('tbody')).appendTo(this.table);

			for (var i=0; i < r; i++) {
				var tr = '<tr>';
				for (var j=0; j < c; j++) {
					tr += '<td>&nbsp;</td>';
				}
				b.append(tr+'</tr>');
			};

			// var tr = $(this.rte.doc.createElement('tr'));
			//
			// for (var i=0; i < c; i++) {
			// 	tr.append($(this.rte.doc.createElement('td')).html('&nbsp;'));
			// };
			//
			// for (var i=0; i<r; i++) {
			// 	b.append(tr.clone(true));
			// };

			// this.rte.selection.insertNode(this.table.get(0), true);
		} else {
			this.table
				.removeAttr('width')
				.removeAttr('height')
				.removeAttr('border')
				.removeAttr('align')
				.removeAttr('bordercolor')
				.removeAttr('bgcolor')
				.removeAttr('cellspacing')
				.removeAttr('cellpadding')
				.removeAttr('frame')
				.removeAttr('rules')
				.removeAttr('style');
		}

		var cap = $.trim(this.src.main.caption.val());
		if (cap) {
			if (!this.table.children('caption').length) {
				this.table.prepend('<caption />' );
			}
			this.table.children('caption').text(cap);
		} else {
			this.table.children('caption').remove();
		}


		for (var tab in this.src) {
			if (tab != 'main') {
				for (var n in this.src[tab]) {
					var v = $.trim(this.src[tab][n].val());
					if (v) {
						this.table.attr(n, v);
					} else {
						this.table.removeAttr(n);
					}
				}
			}
		}
		var spacing, padding, rules;

		if ((spacing = parseInt(this.src.main.spacing.val())) && spacing>=0) {
			this.table.attr('cellspacing', spacing);
		}

		if ((padding = parseInt(this.src.main.padding.val())) && padding>=0) {
			this.table.attr('cellpadding', padding);
		}

		if ((rules = this.src.main.rules.val())) {
			this.table.attr('rules', rules);
		}

		var
			w = parseInt(this.src.main.width.val()) || '',
			h = parseInt(this.src.main.height.val()) || '',
			i = $.trim(this.src.main.bgimg.val()),
			b = this.src.main.border.val(),
			m = this.src.main.margin.val(),
			f = this.src.main.align.val();
		this.table.css({
			width              : w ? w+this.src.main.wunit.val() : '',
			height             : h ? h+this.src.main.hunit.val() : '',
			border             : $.trim(b.width+' '+b.style+' '+b.color),
			'background-color' : this.src.main.bg.val(),
			'background-image' : i ? 'url('+i+')' : ''
		});
		if (m.css) {
			this.table.css('margin', m.css);
		} else {
			this.table.css({
				'margin-top'    : m.top,
				'margin-right'  : m.right,
				'margin-bottom' : m.bottom,
				'margin-left'   : m.left
			});
		}
		if ((f=='left' || f=='right') && this.table.css('margin-left')!='auto'  && this.table.css('margin-right')!='auto') {
			this.table.css('float', f);
		}
		if (!this.table.attr('style')) {
			this.table.removeAttr('style');
		}
		if (!this.table.parents().length) {
			this.rte.selection.insertNode(this.table.get(0), true);
		}
		this.rte.ui.update();
	}

	this.update = function() {
		this.domElem.removeClass('disabled');
		if (this.name == 'tableprops' && !this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^TABLE$/)) {
			this.domElem.addClass('disabled').removeClass('active');
		}
	}

}

elRTE.prototype.ui.prototype.buttons.tableprops = elRTE.prototype.ui.prototype.buttons.table;

})(jQuery);

/**
 * @class button - remove table
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tablerm = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		var t = this.rte.dom.parent(this.rte.selection.getNode(), /^TABLE$/);
		// t && $(t).remove();
		if (t) {
			this.rte.history.add();
			$(t).remove();
		}
		this.rte.ui.update(true);
	}

	this.update = function() {
		if (this.rte.dom.parent(this.rte.selection.getNode(), /^TABLE$/)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}
})(jQuery);
/**
 * @class button - table cell properties
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tbcellprops = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.src = null;
	this.labels = null;

	function init() {
		self.labels = {
			main    : 'Properies',
			adv     : 'Advanced',
			events  : 'Events',
			id      : 'ID',
			'class' : 'Css class',
			style   : 'Css style',
			dir     : 'Script direction',
			lang    : 'Language'
		}

		self.src = {
			main : {
				type    : $('<select />').css('width', '100%')
							.append($('<option />').val('td').text(self.rte.i18n('Data')))
							.append($('<option />').val('th').text(self.rte.i18n('Header'))),
				width   : $('<input type="text" />').attr('size', 4),
				wunit   : $('<select />')
							.append($('<option />').val('%').text('%'))
							.append($('<option />').val('px').text('px')),
				height  : $('<input type="text" />').attr('size', 4),
				hunit   : $('<select />')
							.append($('<option />').val('%').text('%'))
							.append($('<option />').val('px').text('px')),
				align   : $('<select />').css('width', '100%')
							.append($('<option />').val('').text(self.rte.i18n('Not set')))
							.append($('<option />').val('left').text(self.rte.i18n('Left')))
							.append($('<option />').val('center').text(self.rte.i18n('Center')))
							.append($('<option />').val('right').text(self.rte.i18n('Right')))
							.append($('<option />').val('justify').text(self.rte.i18n('Justify'))),
				border  : $('<div />'),
				padding  : $('<div />'),
				bg      : $('<div />'),
				bgimg   : $('<input type="text" />').css('width', '90%'),
				apply   : $('<select />').css('width', '100%')
							.append($('<option />').val('').text(self.rte.i18n('Current cell')))
							.append($('<option />').val('row').text(self.rte.i18n('All cells in row')))
							.append($('<option />').val('column').text(self.rte.i18n('All cells in column')))
							.append($('<option />').val('table').text(self.rte.i18n('All cells in table')))
			},

			adv : {
				id        : $('<input type="text" />'),
				'class'   : $('<input type="text" />'),
				style     : $('<input type="text" />'),
				dir       : $('<select />').css('width', '100%')
								.append($('<option />').text(self.rte.i18n('Not set')).val(''))
								.append($('<option />').text(self.rte.i18n('Left to right')).val('ltr'))
								.append($('<option />').text(self.rte.i18n('Right to left')).val('rtl')),
				lang      : $('<input type="text" />')
			},

			events : {}
		}

		$.each(self.src, function() {
			for (var n in this) {
				this[n].attr('name', n);
				if (this[n].attr('type') == 'text' && !this[n].attr('size') && n!='bgimg') {
					this[n].css('width', '100%')
				}
			}
		});

		$.each(
			['onblur', 'onfocus', 'onclick', 'ondblclick', 'onmousedown', 'onmouseup', 'onmouseover', 'onmouseout', 'onmouseleave', 'onkeydown', 'onkeypress', 'onkeyup'],
			function() {
				self.src.events[this] = $('<input type="text" />').attr('name', this).css('width', '100%');
		});

	}

	this.command = function() {
		!this.src && init();
		this.cell = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(TD|TH)$/);
		if (!this.cell) {
			return;
		}
		this.src.main.type.val(this.cell.nodeName.toLowerCase());
		this.cell = $(this.cell);
		this.src.main.border.elBorderSelect({styleHeight : 117, value : this.cell});
		this.src.main.bg.elColorPicker({palettePosition : 'outer', 'class' : 'el-colorpicker ui-icon ui-icon-pencil'});
		this.src.main.padding.elPaddingInput({ value : this.cell});

		var w = this.cell.css('width') || this.cell.attr('width');
		this.src.main.width.val(parseInt(w)||'');
		this.src.main.wunit.val(w.indexOf('px') != -1 ? 'px' : '%');

		var h = this.cell.css('height') || this.cell.attr('height');
		this.src.main.height.val(parseInt(h)||'');
		this.src.main.hunit.val(h.indexOf('px') != -1 ? 'px' : '%');

		this.src.main.align.val(this.cell.attr('align') || this.cell.css('text-align'));
		this.src.main.bg.val(this.cell.css('background-color'));
		var bgimg = this.cell.css('background-image');
		this.src.main.bgimg.val(bgimg && bgimg!='none' ? bgimg.replace(/url\(([^\)]+)\)/i, "$1") : '');
		this.src.main.apply.val('');

		var opts = {
			rtl : this.rte.rtl,
			submit : function(e, d) { e.stopPropagation(); e.preventDefault(); self.set(); d.close(); },
			dialog : {
				width : 520,
				title : this.rte.i18n('Table cell properties')
			}
		}
		var d = new elDialogForm(opts);
		for (var tab in this.src) {
			d.tab(tab, this.rte.i18n(this.labels[tab]));

			if (tab == 'main') {
				d.append([this.rte.i18n('Width'),              $('<span />').append(this.src.main.width).append(this.src.main.wunit)],  'main', true)
					.append([this.rte.i18n('Height'),          $('<span />').append(this.src.main.height).append(this.src.main.hunit)], 'main', true)
					.append([this.rte.i18n('Table cell type'), this.src.main.type],    'main', true)
					.append([this.rte.i18n('Border'),          this.src.main.border],  'main', true)
					.append([this.rte.i18n('Alignment'),       this.src.main.align],   'main', true)
					.append([this.rte.i18n('Paddings'),        this.src.main.padding], 'main', true)
					.append([this.rte.i18n('Background'),      $('<span />').append($('<span />').css({'float' : 'left', 'margin-right' : '3px'}).append(this.src.main.bg)).append(this.src.main.bgimg)],  'main', true)
					.append([this.rte.i18n('Apply to'),        this.src.main.apply],   'main', true);
			} else {
				for (var name in this.src[tab]) {
					var v = this.cell.attr(name) || '';
					if (tab == 'events') {
						v = this.rte.utils.trimEventCallback(v);
					}
					d.append([this.rte.i18n(this.labels[name] ? this.labels[name] : name), this.src[tab][name].val(v)], tab, true);
				}
			}
		}
		d.open()
	}

	this.set = function() {
		// $(t).remove();
		var target = this.cell,
			apply  = this.src.main.apply.val();
		switch (this.src.main.apply.val()) {
			case 'row':
				target = this.cell.parent('tr').children('td,th');
				break;

			case 'column':
				target = $(this.rte.dom.tableColumn(this.cell.get(0)));
				break;

			case 'table':
				target = this.cell.parents('table').find('td,th');
				break;
		}

		for (var tab in this.src) {
			if (tab != 'main') {
				for (var n in this.src[tab]) {
					var v = $.trim(this.src[tab][n].val());
					if (v) {
						target.attr(n, v);
					} else {
						target.removeAttr(n);
					}
				}
			}
		}

		target.removeAttr('width')
			.removeAttr('height')
			.removeAttr('border')
			.removeAttr('align')
			.removeAttr('bordercolor')
			.removeAttr('bgcolor');

		var t = this.src.main.type.val();
		var w = parseInt(this.src.main.width.val()) || '';
		var h = parseInt(this.src.main.height.val()) || '';
		var i = $.trim(this.src.main.bgimg.val());
		var b = this.src.main.border.val();
		var css = {
			'width'            : w ? w+this.src.main.wunit.val() : '',
			'height'           : h ? h+this.src.main.hunit.val() : '',
			'background-color' : this.src.main.bg.val(),
			'background-image' : i ? 'url('+i+')' : '',
			'border'           : $.trim(b.width+' '+b.style+' '+b.color),
			'text-align'       : this.src.main.align.val() || ''
		};
		var p = this.src.main.padding.val();
		if (p.css) {
			css.padding = p.css;
		} else {
			css['padding-top']    = p.top;
			css['padding-right']  = p.right;
			css['padding-bottom'] = p.bottom;
			css['padding-left']   = p.left;
		}

		target = target.get();

		$.each(target, function() {
			var type = this.nodeName.toLowerCase();
			var $this = $(this);
			if (type != t) {

				var attr = {}
				for (var i in self.src.adv) {
					var v = $this.attr(i)
					if (v) {
						attr[i] = v.toString();
					}
				}
				for (var i in self.src.events) {
					var v = $this.attr(i)
					if (v) {
						attr[i] = v.toString();
					}
				}
				var colspan = $this.attr('colspan')||1;
				var rowspan = $this.attr('rowspan')||1;
				if (colspan>1) {
					attr.colspan = colspan;
				}
				if (rowspan>1) {
					attr.rowspan = rowspan;
				}

				$this.replaceWith($('<'+t+' />').html($this.html()).attr(attr).css(css) );

			} else {
				$this.css(css);
			}
		});

		this.rte.ui.update();
	}

	this.update = function() {
		if (this.rte.dom.parent(this.rte.selection.getNode(), /^TABLE$/)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}

}

})(jQuery);/**
 * @class button - table cells merge
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tbcellsmerge = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;

	function selectedCells() {
		var c1 = self.rte.dom.selfOrParent(self.rte.selection.getStart(), /^(TD|TH)$/);
		var c2 = self.rte.dom.selfOrParent(self.rte.selection.getEnd(), /^(TD|TH)$/);
		if (c1 && c2 && c1!=c2 && $(c1).parents('table').get(0) == $(c2).parents('table').get(0)) {
			return [c1, c2];
		}
		return null;
	}

	this.command = function() {
		var cells = selectedCells();

		if (cells) {

			var _s  = this.rte.dom.indexOf($(cells[0]).parent('tr').get(0));
			var _e  = this.rte.dom.indexOf($(cells[1]).parent('tr').get(0));
			var ro  = Math.min(_s, _e); // row offset
			var rl  = Math.max(_s, _e) - ro + 1; // row length
			var _c1 = this.rte.dom.tableColumn(cells[0], true, true);
			var _c2 = this.rte.dom.tableColumn(cells[1], true);
			var _i1 = $.inArray(cells[0], _c1.column);
			var _i2 = $.inArray(cells[1], _c2.column);

			var colBegin = _c1.info.offset[_i1] < _c2.info.offset[_i2]  ? _c1 : _c2;
			var colEnd   = _c1.info.offset[_i1] >= _c2.info.offset[_i2] ? _c1 : _c2;
			var length   = 0;
			var target   = null;
			var html     = '';
			this.rte.history.add();
			var rows = $($(cells[0]).parents('table').eq(0).find('tr').get().slice(ro, ro+rl))
				.each( function(i) {
					var _l = html.length;
					var accept = false;
					$(this).children('td,th').each(function() {
						var $this   = $(this);
						var inBegin = $.inArray(this, colBegin.column);
						var inEnd   = $.inArray(this, colEnd.column);

						if (inBegin!=-1 || inEnd!=-1) {
							accept = inBegin!=-1 && inEnd==-1;
							var len = parseInt($this.attr('colspan')||1)
							if (i == 0) {
								length += len;
							}

							if (inBegin!=-1 && i>0) {
								var delta = colBegin.info.delta[inBegin];
								if (delta>0) {
									if ($this.css('text-align') == 'left') {
										var cell = $this.clone(true);
										$this.html('&nbsp;');
									} else {
										var cell = $this.clone().html('&nbsp;');
									}
									cell.removeAttr('colspan').removeAttr('id').insertBefore(this);
									if (delta>1) {
										cell.attr('colspan', delta);
									}
								}
							}

							if (inEnd!=-1) {
								var delta = colEnd.info.delta[inEnd];
								if (len-delta>1) {
									var cp = len-delta-1;
									if ($this.css('text-align') == 'right') {
										var cell = $this.clone(true);
										$this.html('&nbsp;');
									} else {
										var cell = $this.clone().html('&nbsp;');
									}
									cell.removeAttr('colspan').removeAttr('id').insertAfter(this);
									if (cp>1) {
										cell.attr('colspan', cp);
									}
								}
							}
							if (!target) {
								target = $this;
							} else {
								html += $this.html();
								$this.remove();
							}
						} else if (accept) {
							if (i == 0) {
								length += parseInt($this.attr('colspan')||1);
							}
							html += $this.html();
							$this.remove();


						}
					})
					html += _l!=html.length ? '<br />' : '';
				});

			target.removeAttr('colspan').removeAttr('rowspan').html(target.html()+html)
			if (length>1) {
				target.attr('colspan', length);
			}
			if (rl>1) {
				target.attr('rowspan', rl);
			}
			// sometimes when merge cells with different rowspans we get "lost" cells in rows
			// this add cells if needed
			this.rte.dom.fixTable($(cells[0]).parents('table').get(0));
		}
	}

	this.update = function() {
		if (selectedCells()) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}
})(jQuery);
/**
 * @class button - split merged cell
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 * @todo split not merged cell
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tbcellsplit = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		var n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(TD|TH)$/);
		if (n) {
			this.rte.history.add();
			var colspan = parseInt(this.rte.dom.attr(n, 'colspan'));
			var rowspan = parseInt(this.rte.dom.attr(n, 'rowspan'));
			if (colspan>1 || rowspan>1) {
				var cnum = colspan-1;
				var rnum = rowspan-1;
				var tb   = this.rte.dom.parent(n, /^TABLE$/);
				var tbm  = this.rte.dom.tableMatrix(tb);

				// ячейки в текущем ряду
				if (cnum) {
					for (var i=0; i<cnum; i++) {
						$(this.rte.dom.create(n.nodeName)).html('&nbsp;').insertAfter(n);
					}
				}
				if (rnum) {
					var ndx  = this.rte.dom.indexesOfCell(n, tbm)
					var rndx = ndx[0];
					var cndx = ndx[1];
					// ячейки в следущих рядах
					for (var r=rndx+1; r < rndx+rnum+1; r++) {
						var cell;

						if (!tbm[r][cndx].nodeName) {
							if (tbm[r][cndx-1].nodeName) {
								cell = tbm[r][cndx-1];
							} else {
								for (var i=cndx-1; i>=0; i--) {
									if (tbm[r][i].nodeName) {
										cell =tbm[r][i];
										break;
									}
								}
							}
							if (cell) {
								for (var i=0; i<= cnum; i++) {
									$(this.rte.dom.create(cell.nodeName)).html('&nbsp;').insertAfter(cell);
								}
							}
						}
					};
				}
				$(n).removeAttr('colspan').removeAttr('rowspan');
				this.rte.dom.fixTable(tb);
			}
		}
		this.rte.ui.update(true);
	}

	this.update = function() {
		var n = this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(TD|TH)$/);
		if (n && (parseInt(this.rte.dom.attr(n, 'colspan'))>1 || parseInt(this.rte.dom.attr(n, 'rowspan'))>1)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}

})(jQuery);
/**
 * @class button - Insert new column in table(before or after current)
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tbcolbefore = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;

	this.command = function() {
		var cells = this.rte.dom.tableColumn(this.rte.selection.getNode(), false, true);
		if (cells.length) {
			this.rte.history.add();
			$.each(cells, function() {
				var $this = $(this);
				var cp = parseInt($this.attr('colspan')||1)
				if (cp >1) {
					$this.attr('colspan', cp+1);
				} else {
					var c = $this.clone().html('&nbsp;').removeAttr('colspan').removeAttr('width').removeAttr('id');
					if (self.name == 'tbcolbefore') {
						c.insertBefore(this);
					} else {
						c.insertAfter(this);
					}
				}
			});
			this.rte.ui.update();
		}
	}

	this.update = function() {
		if (this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(TD|TH)$/)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}

elRTE.prototype.ui.prototype.buttons.tbcolafter = elRTE.prototype.ui.prototype.buttons.tbcolbefore;

})(jQuery);
/**
 * @class button - remove table colunm
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tbcolrm = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;

	this.command = function() {
		var n     = this.rte.selection.getNode();
		var c     = this.rte.dom.selfOrParent(n, /^(TD|TH)$/);
		var prev  = $(c).prev('td,th').get(0);
		var next  = $(c).next('td,th').get(0);
		var tb    = this.rte.dom.parent(n, /^TABLE$/);
		var cells = this.rte.dom.tableColumn(n, false, true);

		if (cells.length) {
			this.rte.history.add();
			$.each(cells, function() {
				var $this = $(this);
				var cp    = parseInt($this.attr('colspan')||1);
				if ( cp>1 ) {
					$this.attr('colspan', cp-1);
				} else {
					$this.remove();
				}
			});
			this.rte.dom.fixTable(tb);
			if (prev || next) {
				this.rte.selection.selectContents(prev ? prev : next).collapse(true);
			}
			this.rte.ui.update(true);
		}
	}

	this.update = function() {
		if (this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^(TD|TH)$/)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}

})(jQuery);
/**
 * @class меню - Новый ряд в таблице
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 **/
elRTE.prototype.ui.prototype.buttons.tbrowbefore = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);

	this.command = function() {
		var n  = this.rte.selection.getNode();
		var c  = this.rte.dom.selfOrParent(n, /^(TD|TH)$/);
		var r  = this.rte.dom.selfOrParent(c, /^TR$/);
		var mx = this.rte.dom.tableMatrix(this.rte.dom.selfOrParent(c, /^TABLE$/));
		if (c && r && mx) {
			this.rte.history.add();
			var before = this.name == 'tbrowbefore';
			var ro     = $(r).prevAll('tr').length;
			var cnt    = 0;
			var mdf    = [];

			function _find(x, y) {
				while (y>0) {
					y--;
					if (mx[y] && mx[y][x] && mx[y][x].nodeName) {
						return mx[y][x];
					}
				}
			}

			for (var i=0; i<mx[ro].length; i++) {
				if (mx[ro][i] && mx[ro][i].nodeName) {
					var cell    = $(mx[ro][i]);
					var colspan = parseInt(cell.attr('colspan')||1);
					if (parseInt(cell.attr('rowspan')||1) > 1) {
						if (before) {
							cnt += colspan;
						} else {
							mdf.push(cell);
						}
					} else {
						cnt += colspan;
					}
				} else if (mx[ro][i] == '-') {
					cell = _find(i, ro);
					cell && mdf.push($(cell));
				}
			}
			var row = $('<tr />');
			for (var i=0; i<cnt; i++) {
				row.append('<td>&nbsp;</td>');
			}
			if (before) {
				row.insertBefore(r);
			} else {
				row.insertAfter(r);
			}
			$.each(mdf, function() {
				$(this).attr('rowspan', parseInt($(this).attr('rowspan')||1)+1);
			});
			this.rte.ui.update();
		}
	}

	this.update = function() {
		if (this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^TR$/)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}

elRTE.prototype.ui.prototype.buttons.tbrowafter = elRTE.prototype.ui.prototype.buttons.tbrowbefore;
/**
 * @class button - remove table row
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
elRTE.prototype.ui.prototype.buttons.tbrowrm = function(rte, name) {
	this.constructor.prototype.constructor.call(this, rte, name);
	var self = this;
	this.command = function() {

		var n  = this.rte.selection.getNode(),
			c  = this.rte.dom.selfOrParent(n, /^(TD|TH)$/),
			r  = this.rte.dom.selfOrParent(c, /^TR$/),
			tb = this.rte.dom.selfOrParent(c, /^TABLE$/),
			mx = this.rte.dom.tableMatrix(tb);

		if (c && r && mx.length) {
			this.rte.history.add();
			if (mx.length==1) {
				$(tb).remove();
				return this.rte.ui.update();
			}
			var mdf = [];
			var ro  = $(r).prevAll('tr').length;

			function _find(x, y) {
				while (y>0) {
					y--;
					if (mx[y] && mx[y][x] && mx[y][x].nodeName) {
						return mx[y][x];
					}
				}
			}

			// move cell with rowspan>1 to next row
			function _move(cell, x) {
				y = ro+1;
				var sibling= null;
				if (mx[y]) {
					for (var _x=0; _x<x; _x++) {
						if (mx[y][_x] && mx[y][_x].nodeName) {
							sibling = mx[y][_x];
						}
					};

					cell = cell.remove();
					if (sibling) {
						cell.insertAfter(sibling);
					} else {
						cell.prependTo($(r).next('tr').eq(0));
					}
				}
			}

			function _cursorPos(column) {
				for (var i = 0; i<column.length; i++) {
					if (column[i] == c) {
						return i<column.length-1 ? column[i+1] : column[i-1];
					}
				}
			}

			for (var i=0; i<mx[ro].length; i++) {
				var cell = null;
				var move = false;
				if (mx[ro][i] && mx[ro][i].nodeName) {
					cell = mx[ro][i];
					move = true;
				} else if (mx[ro][i] == '-' && (cell = _find(i, ro))) {
					move = false;
				}
				if (cell) {
					cell = $(cell);
					var rowspan = parseInt(cell.attr('rowspan')||1);
					if (rowspan>1) {
						cell.attr('rowspan', rowspan-1);
						move && _move(cell, i, ro);
					}
				}
			};

			var _c = _cursorPos(this.rte.dom.tableColumn(c));
			if (_c) {
				this.rte.selection.selectContents(_c).collapse(true);
			}

			$(r).remove();
		}
		this.rte.ui.update();
	}

	this.update = function() {
		if (this.rte.dom.selfOrParent(this.rte.selection.getNode(), /^TR$/)) {
			this.domElem.removeClass('disabled');
		} else {
			this.domElem.addClass('disabled');
		}
	}
}

})(jQuery);/**
 * @class кнопка - отмена повтор действий
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {
	elRTE.prototype.ui.prototype.buttons.undo = function(rte, name) {
		this.constructor.prototype.constructor.call(this, rte, name);

		this.command = function() {
			if (this.name == 'undo' && this.rte.history.canBack()) {
				this.rte.history.back();
				this.rte.ui.update();
			} else if (this.name == 'redo' && this.rte.history.canFwd()) {
				this.rte.history.fwd();
				this.rte.ui.update();
			}
		}

		this.update = function() {
			this.domElem.toggleClass('disabled', this.name == 'undo' ? !this.rte.history.canBack() : !this.rte.history.canFwd());
		}
	}

	elRTE.prototype.ui.prototype.buttons.redo = elRTE.prototype.ui.prototype.buttons.undo;

})(jQuery);/**
 * @class button - remove link
 *
 * @param  elRTE  rte   объект-редактор
 * @param  String name  название кнопки
 *
 * @author:    Dmitry Levashov (dio) dio@std42.ru
 * @copyright: Studio 42, http://www.std42.ru
 **/
(function($) {

	elRTE.prototype.ui.prototype.buttons.unlink = function(rte, name) {
		this.constructor.prototype.constructor.call(this, rte, name);

		this.command = function() {

			var n = this.rte.selection.getNode(),
				l = this.rte.dom.selfOrParentLink(n);

			function isLink(n) { return n.nodeName == 'A' && n.href; }

			if (!l) {

				var sel = $.browser.msie ? this.rte.selection.selected() : this.rte.selection.selected({wrap : false});
				if (sel.length) {
					for (var i=0; i < sel.length; i++) {
						if (isLink(sel[i])) {
							l = sel[i];
							break;
						}
					};
					if (!l) {
						l = this.rte.dom.parent(sel[0], isLink) || this.rte.dom.parent(sel[sel.length-1], isLink);
					}
				}
			}

			if (l) {
				this.rte.history.add();
				this.rte.selection.select(l);
				this.rte.doc.execCommand('unlink', false, null);
				this.rte.ui.update(true);
			}

		}

		this.update = function() {
			var n = this.rte.selection.getNode();
			if (this.rte.dom.selfOrParentLink(n)) {
				this.domElem.removeClass('disabled').addClass('active');
			} else if (this.rte.dom.selectionHas(function(n) { return n.nodeName == 'A' && n.href; })) {
				this.domElem.removeClass('disabled').addClass('active');
			} else {
				this.domElem.addClass('disabled').removeClass('active');
			}
		}
	}

})(jQuery);

/**
 * PrimeFaces Editor Widget
 */
PrimeFaces.widget.Editor = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.cfg.allowSource = false;

    if(this.cfg.toolbarTemplate) {
        elRTE.prototype.options.panels.customPanel = this.cfg.toolbarTemplate;
        elRTE.prototype.options.toolbars.customToolbar = ['customPanel'];

        this.cfg.toolbar = 'customToolbar';
    }

    this.editor = new elRTE(document.getElementById(id), this.cfg);
    
    jQuery('.el-rte .panel-save').hide();
    jQuery('.el-rte .smiley').hide();
    jQuery('.el-rte .about').hide();
}

PrimeFaces.widget.Editor.prototype.saveHTML = function() {
    this.editor.beforeSave();
}