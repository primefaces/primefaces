/**
 * Interface Elements for jQuery
 * utility function
 *
 * http://interface.eyecon.ro
 *
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 *
 */

jQuery.iUtil = {
	getPosition : function(e)
	{
		var x = 0;
		var y = 0;
		var es = e.style;
		var restoreStyles = false;
		if (jQuery(e).css('display') == 'none') {
			var oldVisibility = es.visibility;
			var oldPosition = es.position;
			restoreStyles = true;
			es.visibility = 'hidden';
			es.display = 'block';
			es.position = 'absolute';
		}
		var el = e;
		while (el){
			x += el.offsetLeft + (el.currentStyle && !jQuery.browser.opera ?parseInt(el.currentStyle.borderLeftWidth)||0:0);
			y += el.offsetTop + (el.currentStyle && !jQuery.browser.opera ?parseInt(el.currentStyle.borderTopWidth)||0:0);
			el = el.offsetParent;
		}
		el = e;
		while (el && el.tagName  && el.tagName.toLowerCase() != 'body')
		{
			x -= el.scrollLeft||0;
			y -= el.scrollTop||0;
			el = el.parentNode;
		}
		if (restoreStyles == true) {
			es.display = 'none';
			es.position = oldPosition;
			es.visibility = oldVisibility;
		}
		return {x:x, y:y};
	},
	getPositionLite : function(el)
	{
		var x = 0, y = 0;
		while(el) {
			x += el.offsetLeft || 0;
			y += el.offsetTop || 0;
			el = el.offsetParent;
		}
		return {x:x, y:y};
	},
	getSize : function(e)
	{
		var w = jQuery.css(e,'width');
		var h = jQuery.css(e,'height');
		var wb = 0;
		var hb = 0;
		var es = e.style;
		if (jQuery(e).css('display') != 'none') {
			wb = e.offsetWidth;
			hb = e.offsetHeight;
		} else {
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
	getSizeLite : function(el)
	{
		return {
			wb:el.offsetWidth||0,
			hb:el.offsetHeight||0
		};
	},
	getClient : function(e)
	{
		var h, w, de;
		if (e) {
			w = e.clientWidth;
			h = e.clientHeight;
		} else {
			de = document.documentElement;
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
			iw = 0;
			ih = 0;
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
			iw = self.innerWidth||document.documentElement.clientWidth||document.body.clientWidth||0;
			ih = self.innerHeight||document.documentElement.clientHeight||document.body.clientHeight||0;
		}
		return { t: t, l: l, w: w, h: h, iw: iw, ih: ih };
	},
	getMargins : function(e, toInteger)
	{
		var el = jQuery(e);
		var t = el.css('marginTop') || '';
		var r = el.css('marginRight') || '';
		var b = el.css('marginBottom') || '';
		var l = el.css('marginLeft') || '';
		if (toInteger)
			return {
				t: parseInt(t)||0,
				r: parseInt(r)||0,
				b: parseInt(b)||0,
				l: parseInt(l)
			};
		else
			return {t: t, r: r,	b: b, l: l};
	},
	getPadding : function(e, toInteger)
	{
		var el = jQuery(e);
		var t = el.css('paddingTop') || '';
		var r = el.css('paddingRight') || '';
		var b = el.css('paddingBottom') || '';
		var l = el.css('paddingLeft') || '';
		if (toInteger)
			return {
				t: parseInt(t)||0,
				r: parseInt(r)||0,
				b: parseInt(b)||0,
				l: parseInt(l)
			};
		else
			return {t: t, r: r,	b: b, l: l};
	},
	getBorder : function(e, toInteger)
	{
		var el = jQuery(e);
		var t = el.css('borderTopWidth') || '';
		var r = el.css('borderRightWidth') || '';
		var b = el.css('borderBottomWidth') || '';
		var l = el.css('borderLeftWidth') || '';
		if (toInteger)
			return {
				t: parseInt(t)||0,
				r: parseInt(r)||0,
				b: parseInt(b)||0,
				l: parseInt(l)||0
			};
		else
			return {t: t, r: r,	b: b, l: l};
	},
	getPointer : function(event)
	{
		var x = event.pageX || (event.clientX + (document.documentElement.scrollLeft || document.body.scrollLeft)) || 0;
		var y = event.pageY || (event.clientY + (document.documentElement.scrollTop || document.body.scrollTop)) || 0;
		return {x:x, y:y};
	},
	traverseDOM : function(nodeEl, func)
	{
		func(nodeEl);
		nodeEl = nodeEl.firstChild;
		while(nodeEl){
			jQuery.iUtil.traverseDOM(nodeEl, func);
			nodeEl = nodeEl.nextSibling;
		}
	},
	purgeEvents : function(nodeEl)
	{
		jQuery.iUtil.traverseDOM(
			nodeEl,
			function(el)
			{
				for(var attr in el){
					if(typeof el[attr] === 'function') {
						el[attr] = null;
					}
				}
			}
		);
	},
	centerEl : function(el, axis)
	{
		var clientScroll = jQuery.iUtil.getScroll();
		var windowSize = jQuery.iUtil.getSize(el);
		if (!axis || axis == 'vertically')
			jQuery(el).css(
				{
					top: clientScroll.t + ((Math.max(clientScroll.h,clientScroll.ih) - clientScroll.t - windowSize.hb)/2) + 'px'
				}
			);
		if (!axis || axis == 'horizontally')
			jQuery(el).css(
				{
					left:	clientScroll.l + ((Math.max(clientScroll.w,clientScroll.iw) - clientScroll.l - windowSize.wb)/2) + 'px'
				}
			);
	},
	fixPNG : function (el, emptyGIF) {
		var images = jQuery('img[@src*="png"]', el||document), png;
		images.each( function() {
			png = this.src;
			this.src = emptyGIF;
			this.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + png + "')";
		});
	}
};

// Helper function to support older browsers!
[].indexOf || (Array.prototype.indexOf = function(v, n){
	n = (n == null) ? 0 : n;
	var m = this.length;
	for (var i=n; i<m; i++)
		if (this[i] == v)
			return i;
	return -1;
});


/**
 * Interface Elements for jQuery
 * Fisheye menu
 *
 * http://interface.eyecon.ro
 *
 * Copyright (c) 2006 Stefan Petre
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 */

/**
 * Build a Fisheye menu from a list of links
 *
 * @name Fisheye
 * @description Build a Fisheye menu from a list of links
 * @param Hash hash A hash of parameters
 * @option String items items selection
 * @option String container container element
 * @option Integer itemWidth the minimum width for each item
 * @option Integer maxWidth the maximum width for each item
 * @option String itemsText selection of element that contains the text for each item
 * @option Integer proximity the distance from element that make item to interact
 * @option String valign vertical alignment
 * @option String halign horizontal alignment
 *
 * @type jQuery
 * @cat Plugins/Interface
 * @author Stefan Petre
 */
jQuery.iFisheye = {

	build : function(options)
	{

		return this.each(
			function()
			{
				var el = this;
				el.fisheyeCfg = {
					items : jQuery(options.items, this),
					container: jQuery(options.container, this),
					pos : jQuery.iUtil.getPosition(this),
					itemWidth: options.itemWidth,
					itemsText: options.itemsText,
					proximity: options.proximity,
					valign: options.valign,
					halign: options.halign,
					maxWidth : options.maxWidth
				};
				jQuery.iFisheye.positionContainer(el, 0);
				jQuery(window).bind(
					'resize',
					function()
					{
						el.fisheyeCfg.pos = jQuery.iUtil.getPosition(el);
						jQuery.iFisheye.positionContainer(el, 0);
						jQuery.iFisheye.positionItems(el);
					}
				);
				jQuery.iFisheye.positionItems(el);
				el.fisheyeCfg.items
					.bind(
						'mouseover',
						function()
						{
							jQuery(el.fisheyeCfg.itemsText, this).get(0).style.display = 'block';
						}
					)
					.bind(
						'mouseout',
						function()
						{
							jQuery(el.fisheyeCfg.itemsText, this).get(0).style.display = 'none';
						}
					);
				jQuery(document).bind(
					'mousemove',
					function(e)
					{
						var pointer = jQuery.iUtil.getPointer(e);
						var toAdd = 0;
						if (el.fisheyeCfg.halign && el.fisheyeCfg.halign == 'center')
							var posx = pointer.x - el.fisheyeCfg.pos.x - (el.offsetWidth - el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size())/2 - el.fisheyeCfg.itemWidth/2;
						else if (el.fisheyeCfg.halign && el.fisheyeCfg.halign == 'right')
							var posx = pointer.x - el.fisheyeCfg.pos.x - el.offsetWidth + el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size();
						else
							var posx = pointer.x - el.fisheyeCfg.pos.x;
						var posy = Math.pow(pointer.y - el.fisheyeCfg.pos.y - el.offsetHeight/2,2);
						el.fisheyeCfg.items.each(
							function(nr)
							{
								distance = Math.sqrt(
									Math.pow(posx - nr*el.fisheyeCfg.itemWidth, 2)
									+ posy
								);
								distance -= el.fisheyeCfg.itemWidth/2;

								distance = distance < 0 ? 0 : distance;
								distance = distance > el.fisheyeCfg.proximity ? el.fisheyeCfg.proximity : distance;
								distance = el.fisheyeCfg.proximity - distance;

								extraWidth = el.fisheyeCfg.maxWidth * distance/el.fisheyeCfg.proximity;

								this.style.width = el.fisheyeCfg.itemWidth + extraWidth + 'px';
								this.style.left = el.fisheyeCfg.itemWidth * nr + toAdd + 'px';
								toAdd += extraWidth;
							}
						);
						jQuery.iFisheye.positionContainer(el, toAdd);
					}
				);
			}
		)
	},

	positionContainer : function(el, toAdd)
	{
		if (el.fisheyeCfg.halign)
			if (el.fisheyeCfg.halign == 'center')
				el.fisheyeCfg.container.get(0).style.left = (el.offsetWidth - el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size())/2 - toAdd/2 + 'px';
			else if (el.fisheyeCfg.halign == 'left')
				el.fisheyeCfg.container.get(0).style.left =  - toAdd/el.fisheyeCfg.items.size() + 'px';
			else if (el.fisheyeCfg.halign == 'right')
				el.fisheyeCfg.container.get(0).style.left =  (el.offsetWidth - el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size()) - toAdd/2 + 'px';
		el.fisheyeCfg.container.get(0).style.width = el.fisheyeCfg.itemWidth * el.fisheyeCfg.items.size() + toAdd + 'px';
	},

	positionItems : function(el)
	{
		el.fisheyeCfg.items.each(
			function(nr)
			{
				this.style.width = el.fisheyeCfg.itemWidth + 'px';
				this.style.left = el.fisheyeCfg.itemWidth * nr + 'px';
			}
		);
	}
};

jQuery.fn.Fisheye = jQuery.iFisheye.build;

/**
 * PrimeFace Dock Widget
 */
PrimeFaces.widget.Dock = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        $(this.jqId).Fisheye(this.cfg);
    }
    
});