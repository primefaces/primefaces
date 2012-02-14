/*
 * Gritter for jQuery
 * http://www.boedesign.com/
 *
 * Copyright (c) 2009 Jordan Boesch
 * Dual licensed under the MIT and GPL licenses.
 *
 * Date: December 1, 2009
 * Version: 1.6
 */

(function($){
 	
	/**
	* Set it up as an object under the jQuery namespace
	*/
	$.gritter = {};
	
	/**
	* Set up global options that the user can over-ride
	*/
	$.gritter.options = {
		fade_in_speed: 'medium', // how fast notifications fade in
		fade_out_speed: 1000, // how fast the notices fade out
		time: 6000 // hang on the screen for...
	}
	
	/**
	* Add a gritter notification to the screen
	* @see Gritter#add();
	*/
	$.gritter.add = function(params){

		try {
			return Gritter.add(params || {});
		} catch(e) {
		
			var err = 'Gritter Error: ' + e;
			(typeof(console) != 'undefined' && console.error) ? 
				console.error(err, params) : 
				alert(err);
				
		}
		
	}
	
	/**
	* Remove a gritter notification from the screen
	* @see Gritter#removeSpecific();
	*/
	$.gritter.remove = function(id, params){
		Gritter.removeSpecific(id, params || {});
	}
	
	/**
	* Remove all notifications
	* @see Gritter#stop();
	*/
	$.gritter.removeAll = function(params){
		Gritter.stop(params || {});
	}
	
	/**
	* Big fat Gritter object
	* @constructor (not really since it's object literal)
	*/
	var Gritter = {
	    
		// Public - options to over-ride with $.gritter.options in "add"
		fade_in_speed: '',
		fade_out_speed: '',
		time: '',
	    
		// Private - no touchy the private parts
		_custom_timer: 0,
		_item_count: 0,
		_is_setup: 0,
		_tpl_close: '<div class="ui-growl-icon-close ui-icon ui-icon-closethick"></div>',
		_tpl_item: '<div id="gritter-item-[[number]]" class="ui-growl-item-container ui-widget-content ui-corner-all [[item_class]]" style="display:none"><div class="ui-growl-item">[[image]]<div class="[[class_name]]"><span class="ui-growl-title">[[username]]</span><p>[[text]]</p></div><div style="clear:both"></div></div></div>',
		_tpl_wrap: '<div class="ui-growl ui-widget"></div>',
	    
		/**
		* Add a gritter notification to the screen
		* @param {Object} params The object that contains all the options for drawing the notification
		* @return {Integer} The specific numeric id to that gritter notification
		*/
		add: function(params){
		
			// Check the options and set them once
			if(!this._is_setup){
				this._runSetup();
			}
	        
			// Basics
			var user = params.title, 
				text = params.text,
				image = params.image || '',
				sticky = params.sticky || false,
				item_class = params.class_name || '',
				time_alive = params.time || '';
			
			this._verifyWrapper();
			
			this._item_count++;
			var number = this._item_count, 
				tmp = this._tpl_item;
			
			// Assign callbacks
			$(['before_open', 'after_open', 'before_close', 'after_close']).each(function(i, val){
				Gritter['_' + val + '_' + number] = ($.isFunction(params[val])) ? params[val] : function(){}
			});

			// Reset
			this._custom_timer = 0;
			
			// A custom fade time set
			if(time_alive){
				this._custom_timer = time_alive;
			}
			
			var image_str = (image != '') ? '<img src="' + image + '" class="ui-growl-image" />' : '',
			class_name = 'ui-growl-message';
			
			// String replacements on the template
			tmp = this._str_replace(
				['[[username]]', '[[text]]', '[[image]]', '[[number]]', '[[class_name]]', '[[item_class]]'],
				[user, text, image_str, this._item_count, class_name, item_class], tmp
			);
	        
			this['_before_open_' + number]();
			$('.ui-growl').append(tmp);
			
			var item = $('#gritter-item-' + this._item_count);
			
			item.fadeIn(this.fade_in_speed, function(){
				Gritter['_after_open_' + number]($(this));
			});
	        
			if(!sticky){
				this._setFadeTimer(item, number);
			}
			
			// Bind the hover/unhover states
			$(item).bind('mouseenter mouseleave', function(event){
				if(event.type == 'mouseenter'){
					if(!sticky){ 
						Gritter._restoreItemIfFading($(this), number);
					}
				}
				else {
					if(!sticky){
						Gritter._setFadeTimer($(this), number);
					}
				}
				Gritter._hoverState($(this), event.type);
			});
			
			return number;
	    
		},
		
		/**
		* If we don't have any more gritter notifications, get rid of the wrapper using this check
		* @private
		* @param {Integer} unique_id The ID of the element that was just deleted, use it for a callback
		* @param {Object} e The jQuery element that we're going to perform the remove() action on
		*/
		_countRemoveWrapper: function(unique_id, e){
		    
			// Remove it then run the callback function
			e.remove();
			this['_after_close_' + unique_id](e);
			
			// Check if the wrapper is empty, if it is.. remove the wrapper
			if($('.ui-growl-item-container').length == 0){
				$('.ui-growl').remove();
			}
		
		},
		
		/**
		* Fade out an element after it's been on the screen for x amount of time
		* @private
		* @param {Object} e The jQuery element to get rid of
		* @param {Integer} unique_id The id of the element to remove
		* @param {Object} params An optional list of params to set fade speeds etc.
		* @param {Boolean} unbind_events Unbind the mouseenter/mouseleave events if they click (X)
		*/
		_fade: function(e, unique_id, params, unbind_events){
			
			var params = params || {},
				fade = (typeof(params.fade) != 'undefined') ? params.fade : true;
				fade_out_speed = params.speed || this.fade_out_speed;
			
			this['_before_close_' + unique_id](e);
			
			// If this is true, then we are coming from clicking the (X)
			if(unbind_events){
				e.unbind('mouseenter mouseleave');
			}
			
			// Fade it out or remove it
			if(fade){
			
				e.animate({
					opacity: 0
				}, fade_out_speed, function(){
					e.animate({height: 0}, 300, function(){
						Gritter._countRemoveWrapper(unique_id, e);
					})
				})
				
			}
			else {
				
				this._countRemoveWrapper(unique_id, e);
				
			}
					    
		},
		
		/**
		* Perform actions based on the type of bind (mouseenter, mouseleave) 
		* @private
		* @param {Object} e The jQuery element
		* @param {String} type The type of action we're performing: mouseenter or mouseleave
		*/
		_hoverState: function(e, type){
			
			// Change the border styles and add the (X) close button when you hover
			if(type == 'mouseenter'){
		    	
				e.addClass('hover');
				var find_img = e.find('img');
		    	
				// Insert the close button before what element
				(find_img.length) ? 
					find_img.before(this._tpl_close) : 
					e.find('span').before(this._tpl_close);
				
				// Clicking (X) makes the perdy thing close
				e.find('.ui-growl-icon-close').click(function(){
				
					var unique_id = e.attr('id').split('-')[2];
					Gritter.removeSpecific(unique_id, {}, e, true);
					
				});
			
			}
			// Remove the border styles and (X) close button when you mouse out
			else {
				
				e.removeClass('hover');
				e.find('.ui-growl-icon-close').remove();
				
			}
		    
		},
		
		/**
		* Remove a specific notification based on an ID
		* @param {Integer} unique_id The ID used to delete a specific notification
		* @param {Object} params A set of options passed in to determine how to get rid of it
		* @param {Object} e The jQuery element that we're "fading" then removing
		* @param {Boolean} unbind_events If we clicked on the (X) we set this to true to unbind mouseenter/mouseleave
		*/
		removeSpecific: function(unique_id, params, e, unbind_events){
			
			if(!e){
				var e = $('#gritter-item-' + unique_id);
			}
			
			// We set the fourth param to let the _fade function know to 
			// unbind the "mouseleave" event.  Once you click (X) there's no going back!
			this._fade(e, unique_id, params || {}, unbind_events);
			
		},
		
		/**
		* If the item is fading out and we hover over it, restore it!
		* @private
		* @param {Object} e The HTML element to remove
		* @param {Integer} unique_id The ID of the element
		*/
		_restoreItemIfFading: function(e, unique_id){
			
			clearTimeout(this['_int_id_' + unique_id]);
			e.stop().css({opacity: ''});
		    
		},
		
		/**
		* Setup the global options - only once
		* @private
		*/
		_runSetup: function(){
		
			for(opt in $.gritter.options){
				this[opt] = $.gritter.options[opt];
			}
			this._is_setup = 1;
		    
		},
		
		/**
		* Set the notification to fade out after a certain amount of time
		* @private
		* @param {Object} item The HTML element we're dealing with
		* @param {Integer} unique_id The ID of the element
		*/
		_setFadeTimer: function(e, unique_id){
			
			var timer_str = (this._custom_timer) ? this._custom_timer : this.time;
			this['_int_id_' + unique_id] = setTimeout(function(){ 
				Gritter._fade(e, unique_id); 
			}, timer_str);
		
		},
		
		/**
		* Bring everything to a halt
		* @param {Object} params A list of callback functions to pass when all notifications are removed
		*/  
		stop: function(params){
			
			// callbacks (if passed)
			var before_close = ($.isFunction(params.before_close)) ? params.before_close : function(){};
			var after_close = ($.isFunction(params.after_close)) ? params.after_close : function(){};
			
			var wrap = $('.ui-growl');
			before_close(wrap);
			wrap.fadeOut(function(){
				$(this).remove();
				after_close();
			});
		
		},
		
		/**
		* An extremely handy PHP function ported to JS, works well for templating
		* @private
		* @param {String/Array} search A list of things to search for
		* @param {String/Array} replace A list of things to replace the searches with
		* @return {String} sa The output
		*/  
		_str_replace: function(search, replace, subject, count){
		
			var i = 0, j = 0, temp = '', repl = '', sl = 0, fl = 0,
				f = [].concat(search),
				r = [].concat(replace),
				s = subject,
				ra = r instanceof Array, sa = s instanceof Array;
			s = [].concat(s);
			
			if(count){
				this.window[count] = 0;
			}
		
			for(i = 0, sl = s.length; i < sl; i++){
				
				if(s[i] === ''){
					continue;
				}
				
		        for (j = 0, fl = f.length; j < fl; j++){
					
					temp = s[i] + '';
					repl = ra ? (r[j] !== undefined ? r[j] : '') : r[0];
					s[i] = (temp).split(f[j]).join(repl);
					
					if(count && s[i] !== temp){
						this.window[count] += (temp.length-s[i].length) / f[j].length;
					}
					
				}
			}
			
			return sa ? s : s[0];
		    
		},
		
		/**
		* A check to make sure we have something to wrap our notices with
		* @private
		*/  
		_verifyWrapper: function(){
		  
			if($('.ui-growl').length == 0){
				$('body').append(this._tpl_wrap);
			}
		
		}
	    
	}
	
})(jQuery);

/**
 * PrimeFaces Growl Widget
 */
PrimeFaces.widget.Growl = function(cfg) {
    this.cfg = cfg;
    this.id = this.cfg.id
    this.jqId = PrimeFaces.escapeClientId(this.id);

    this.show(this.cfg.msgs);
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Growl, PrimeFaces.widget.BaseWidget);

PrimeFaces.widget.Growl.prototype.show = function(msgs) {
    if($('.ui-growl-item-container').length > 0) {
        $.gritter.removeAll({
            after_close:function() {
                $.each(msgs, function(index, msg) {
                    $.gritter.add(msg);
                });  
            }
        });
    } else {
        $.each(msgs, function(index, msg) {
            $.gritter.add(msg);
        })  
    }
    
}

PrimeFaces.widget.Growl.prototype.hideAll = function() {
    $.gritter.removeAll();
}