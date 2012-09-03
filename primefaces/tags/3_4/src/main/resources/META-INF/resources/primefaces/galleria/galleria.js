/*
**
**	GalleryView - jQuery Content Gallery Plugin
**	Author: 		Jack Anderson
**	Version:		3.0b3 (March 15, 2011)
**
**	Please use this development script if you intend to make changes to the
**	plugin code.  For production sites, it is recommended you use jquery.galleryview-3.0.min.js.
**
**  See README.txt for instructions on how to markup your HTML
**
**	See CHANGELOG.txt for a review of changes and LICENSE.txt for the applicable
**	licensing information.
**
*/

//Global variable to check if window is already loaded
//Used for calling GalleryView after page has loaded
var window_loaded = false;
$(window).load(function(){
	window_loaded = true;
});
(function($){
	$.fn.galleryView = function(options) {
		var opts = $.extend($.fn.galleryView.defaults,options);

		var id;
		var iterator = 0;			// INT - Currently visible panel/frame
		var item_count = 0;			// INT - Total number of panels/frames
		var slide_method;			// STRING - indicator to slide entire filmstrip or just the pointer ('strip','pointer')
		var paused = false;			// BOOLEAN - flag to indicate whether automated transitions are active
		var pointer_speed = 0;		// INT - Speed (in milliseconds) of pointer animation
		var animate_panels = true;	// BOOLEAN - flag to indicate whether panels will animate during transitions
		var current = 1;			// INT - index of current panel/frame
		var gallery_images;			// OBJECT - container for images within UL passed to plugin
		var image_count = 0;		// INT - number of images within gallery
		var loaded_images = 0;		// INT - number of gallery images that have been loaded in the browser

	// Element dimensions
		var gallery_width;
		var gallery_height;
		var pointer_height;
		var pointer_width;
		var strip_width;
		var strip_height;
		var wrapper_width;
		var wrapper_height;
		var f_frame_width;
		var f_frame_height;
		var filmstrip_orientation;


	// Arrays used to scale frames and panels
		var frame_img_scale = {};
		var panel_img_scale = {};
		var img_h = {};
		var img_w = {};

	// Flag indicating whether to scale panel images
		var scale_panel_images = true;

		var panel_nav_displayed = false;

	// Define jQuery objects for reuse
		var j_gallery;
		var j_filmstrip;
		var j_frames;
		var j_frame_img_wrappers;
		var j_panels;
		var j_pointer;
		var j_panel_wrapper;


/*
**	Plugin Functions
*/

	/*
	**	showItem(int,boolean,function)
	**		Transition from current frame to frame i (1-based index)
	**		skip_animation flag let's us override transition speed to show an item instantly
	**		(useful when loading gallery for the first time)
	**		If provided, run callback function after transition completes
	*/
		function showItem(i,speed,callback) {
			// A scrolling filmstrip will contain three copies of each frame, we want to know the relative position of the target frame
			var mod_i = i%item_count;
			var distance;
			var diststr;

			// Disable next/prev buttons until transition is complete
			// This prevents overlapping of animations
			$('.ui-galleria-nav-next, .ui-galleria-panel-nav-next, .ui-galleria-nav-prev, .ui-galleria-panel-nav-prev',j_gallery).unbind('click');
			j_frames.unbind('click');

			// Use timer to rebind navigation buttons when transition ends
			$(document).oneTime(speed,'bindNavButtons',function(){
				$('.ui-galleria-nav-next, .ui-galleria-panel-nav-next',j_gallery).click(showNextItem);
				$('.ui-galleria-nav-prev, .ui-galleria-panel-nav-prev',j_gallery).click(showPrevItem);
				enableFrameClicking();
			});

			if(opts.show_filmstrip) {

				// Fade out all frames
				j_frames.removeClass('current').find('img').stop().animate({opacity:opts.frame_opacity},speed);

				// Fade in target frame
				j_frames.eq(i).addClass('current').find('img').stop().animate({opacity:1},speed);
			}

			//If necessary, transition between panels
			if(opts.show_panels) {
				if(animate_panels) {
					if(opts.panel_animation=='slide') {

						// Move target frame just to the right of the current frame
						j_panels.eq(mod_i).css({
							left: getInt($('.ui-galleria-panel.current').eq(0).css('left'))+opts.panel_width+'px',
							zIndex: 50
						}).show().animate({
							left: '-='+opts.panel_width+'px'
						},speed,opts.easing,function(){
							$(this).addClass('current');
						});

						// Slide current frame and target frame to the left
						$('.ui-galleria-panel.current').css({zIndex:49}).animate({
							left: '-='+opts.panel_width+'px'
						},speed,opts.easing,function(){
							$(this).removeClass('current').hide();
						});
					} else if(opts.panel_animation=='zoomOut') {

						// After zoom is complete, add 'current' class to now visible panel and move it to top of stack via z-index
						$(document).oneTime(speed,'setCurrentFrame',function(){
							j_panels.eq(mod_i).addClass('current').css('zIndex',50);
						});

						// Show target panel and place below current frame via z-index
						j_panels.eq(mod_i).show().css('zIndex',49);

						// Shrink panel container to center while moving image in opposite direction
						// End result is an image that remains static while borders of container shrink
						$('.ui-galleria-panel.current img').animate({
							top: '-='+opts.panel_height/2+'px',
							left:'-='+opts.panel_width/2+'px'
						},speed,'swing',function(){

							// After zoom is complete, immediately animate it back to its original position for next time
							$(this).animate({
								top: '+='+opts.panel_height/2+'px',
								left: '+='+opts.panel_width/2+'px'
							},0);
						});

						$('.ui-galleria-panel.current').animate({
							top:'+='+opts.panel_height/2+'px',
							left:'+='+opts.panel_width/2+'px',
							height:0,
							width:0
						},speed,'swing',function(){
							$(this).removeClass('current').hide().css({
								top:getPos(j_panels[mod_i]).top+'px',
								left:getPos(j_panels[mod_i]).left+'px',
								height:opts.panel_height+'px',
								width:opts.panel_width+'px'
							});
						});
					} else if(opts.panel_animation == 'crossfade') {

						// Default behavior is to fade panel into view
						j_panels.removeClass('current').fadeOut(speed,function(){$(this).css('filter','');}).eq(mod_i).addClass('current').fadeIn(speed,function(){$(this).css('filter','');});
					} else {

						// Fade out panels, and then fade in target panel
						// Use timer due to inconsistency in fadeIn/fadeOut callback reliability
						j_panels.removeClass('current').stop().fadeOut(speed/2);
						$(document).oneTime(speed/2,'fadeInPanel',function(){
							j_panels.eq(mod_i).addClass('current').stop().fadeIn(speed/2);
						});
					}
				} else {

					// If no animation style is chosen, simply show the new panel instantly
					$(document).oneTime(speed,'switch_panels',function(){j_panels.hide().eq(mod_i).show();});
				}
			}

			// If gallery has a filmstrip, handle animation of frames
			if(opts.show_filmstrip) {

				// Slide either pointer or filmstrip, depending on transition method
				if(opts.filmstrip_style == 'scroll' && slide_method=='strip') {

					// Stop filmstrip if it's currently in motion
					j_filmstrip.stop();

					if(filmstrip_orientation=='horizontal') {

						// Determine distance between pointer (eventual destination) and target frame
						distance = getPos(j_frames[i]).left - (getPos(j_pointer[0]).left+(pointer_width/2)-(f_frame_width/2));
						diststr = (distance>=0?'-=':'+=')+Math.abs(distance)+'px';

						// Animate filmstrip and slide target frame under pointer
						j_filmstrip.animate({left:diststr},speed,opts.easing,function(){
							var old_i = i;

							// After transition is complete, shift filmstrip so that a sufficient number of frames
							// remain on either side of the visible filmstrip
							if(i>item_count) {
								i = mod_i;
								iterator = i;
								j_filmstrip.css('left','-'+((f_frame_width+opts.frame_gap)*i)+'px');
							} else if (i<=(item_count-strip_size)) {
								i = (mod_i)+item_count;
								iterator = i;
								j_filmstrip.css('left','-'+((f_frame_width+opts.frame_gap)*i)+'px');
							}

							// If the target frame has changed due to filmstrip shifting,
							// make sure new target frame has 'current' class and correct size/opacity settings
							if(old_i != i) {
								j_frames.eq(old_i).removeClass('current').find('img').css({opacity:opts.frame_opacity});
								j_frames.eq(i).addClass('current').find('img').css({opacity:1});
							}
						});
					} else {

						//Determine distance between pointer (eventual destination) and target frame
						distance = getPos(j_frames[i]).top-getPos($('.gv-strip_wrapper',j_gallery)[0]).top;
						diststr = (distance>=0?'-=':'+=')+Math.abs(distance)+'px';

						// Animate filmstrip and slide target frame under pointer
						j_filmstrip.animate({
							'top':diststr
						},speed,opts.easing,function(){

							// After transition is complete, shift filmstrip so that a sufficient number of frames
							// remain on either side of the visible filmstrip
							var old_i = i;
							if(i>item_count) {
								i = mod_i;
								iterator = i;
								j_filmstrip.css('top','-'+((f_frame_height+opts.frame_gap)*i)+'px');
							} else if (i<=(item_count-strip_size)) {
								i = (mod_i)+item_count;
								iterator = i;
								j_filmstrip.css('top','-'+((f_frame_height+opts.frame_gap)*i)+'px');
							}

							//If the target frame has changed due to filmstrip shifting,
							//Make sure new target frame has 'current' class and correct size/opacity settings
							if(old_i != i) {
								j_frames.eq(old_i).removeClass('current').find('img').css({opacity:opts.frame_opacity});
								j_frames.eq(i).addClass('current').find('img').css({opacity:1});
							}

							// If panels are not set to fade in/out, simply hide all panels and show the target panel
							if(!animate_panels) {
								j_panels.hide().eq(mod_i).show();
							}
						});
					}
				} else if(slide_method=='pointer') {

					// Stop pointer if it's currently in motion
					j_pointer.stop();

					// Get screen position of target frame
					var pos = getPos(j_frames[i]);

					if(filmstrip_orientation=='horizontal') {

						// Slide the pointer over the target frame
						j_pointer.animate({
							left:pos.left+(f_frame_width/2)-(pointer_width/2)+'px'
						},pointer_speed,opts.easing,function(){
							if(!animate_panels) {j_panels.hide().eq(mod_i).show();}
						});
					} else {

						// Slide the pointer over the target frame
						j_pointer.animate({
							top:pos.top+(f_frame_height/2)-(pointer_height)+'px'
						},pointer_speed,opts.easing,function(){
							if(!animate_panels) {j_panels.hide().eq(mod_i).show();}
						});
					}
				}
			}
			if(callback) {$(document).oneTime(speed,'showItemCallback',callback);}
			current = i;
		};

	/*
	**	extraWidth(jQuery element)
	**		Return the combined width of the border and padding to the elements left and right.
	**		If the border is non-numerical, assume zero (not ideal, will fix later)
	**		RETURNS - int
	*/
		function extraWidth(el) {
			if(!el) {return 0;}
			if(el.length==0) {return 0;}
			el = el.eq(0);
			var ew = 0;
			ew += getInt(el.css('paddingLeft'));
			ew += getInt(el.css('paddingRight'));
			ew += getInt(el.css('borderLeftWidth'));
			ew += getInt(el.css('borderRightWidth'));
			return ew;
		};

	/*
	**	extraHeight(jQuery element)
	**		Return the combined height of the border and padding above and below the element
	**		If the border is non-numerical, assume zero (not ideal, will fix later)
	**		RETURN - int
	*/
		function extraHeight(el) {
			if(!el) {return 0;}
			if(el.length==0) {return 0;}
			el = el.eq(0);
			var eh = 0;
			eh += getInt(el.css('paddingTop'));
			eh += getInt(el.css('paddingBottom'));
			eh += getInt(el.css('borderTopWidth'));
			eh += getInt(el.css('borderBottomWidth'));
			return eh;
		};

	/*
	**	showNextItem()
	**		Transition from current frame to next frame
	*/
		function showNextItem() {

			// Cancel any transition timers until we have completed this function
			$(document).stopTime("transition");
			if(++iterator==j_frames.length) {iterator=0;}

			// We've already written the code to transition to an arbitrary panel/frame, so use it
			showItem(iterator,opts.transition_speed);

			// If automated transitions haven't been cancelled by an option or paused on hover, re-enable them
			if(!paused && opts.transition_interval > 0) {
				$(document).everyTime(opts.transition_interval,"transition",function(){showNextItem();});
			}
		};

	/*
	**	showPrevItem()
	**		Transition from current frame to previous frame
	*/
		function showPrevItem() {

			// Cancel any transition timers until we have completed this function
			$(document).stopTime("transition");
			if(--iterator<0) {iterator = item_count-1;}

			// We've already written the code to transition to an arbitrary panel/frame, so use it
			showItem(iterator,opts.transition_speed);

			// If automated transitions haven't been cancelled by an option or paused on hover, re-enable them
			if(!paused && opts.transition_interval > 0) {
				$(document).everyTime(opts.transition_interval,"transition",function(){showNextItem();});
			}
		};

	/*
	**	getPos(jQuery element)
	**		Calculate position of an element relative to top/left corner of gallery
	**		If the gallery bounding box itself is passed to the function, calculate position of gallery relative to top/left corner of browser window
	** 		RETURNS - JSON {left: int, top: int}
	*/
		function getPos(el) {
			if(!el) return {top:0,left:0};
			var left = 0, top = 0;
			var el_id = el.id;
			if(el.offsetParent) {
				do {
					left += el.offsetLeft;
					top += el.offsetTop;
				} while(el = el.offsetParent);
			}

			//If we want the position of the gallery itself, return it
			if(el_id == id) {return {'left':left,'top':top};}

			//Otherwise, get position of element relative to gallery
			else {
				var gPos = getPos(j_gallery[0]);
				var gLeft = gPos.left;
				var gTop = gPos.top;

				return {'left':left-gLeft,'top':top-gTop};
			}
		};

	/*
	**	enableFrameClicking()
	**		Add an onclick event handler to each frame
	**		Exception: if a frame has an anchor tag, do not add an onlick handler
	*/
		function enableFrameClicking() {
			j_frames.each(function(i){
				if($('a',this).length==0) {
					$(this).click(function(){

						// Prevent transitioning to the current frame (unnecessary)
						if(iterator!=i) {
							$(document).stopTime("transition");
							showItem(i,opts.transition_speed);
							iterator = i;
							if(!paused && opts.transition_interval > 0) {
								$(document).everyTime(opts.transition_interval,"transition",function(){showNextItem();});
							}
						}
					});
				}
			});
		};

	/*
	**	buildPanels()
	**		Construct gallery panels: <div class="ui-galleria-panel"> elements
	**		NOTE - These DIVs are generated automatically from the content of the UL passed to the plugin
	*/
		function buildPanels() {

			// If panel overlay content exists, add the necessary overlay background DIV
			// The overlay content and background are separate elements so the background's opacity isn't inherited by the content
			j_panels.each(function(i){
		   		if($('.ui-galleria-overlay',this).length>0) {
					$(this).append('<div class="ui-galleria-overlay-background"></div>');
				}
		   	});

			// If there is no filmstrip in this gallery, add navigation buttons to the panel itself
			if(opts.show_panel_nav) {
				$('<div></div>').addClass('ui-galleria-panel-nav-next').appendTo(j_gallery).css({
					position:'absolute',
					zIndex:'1100',
					top:((opts.filmstrip_position=='top'?opts.frame_gap+wrapper_height:0)+(opts.panel_height-22)/2)+'px',
					right:((opts.filmstrip_position=='right'?opts.frame_gap+wrapper_width:0)+10)+'px',
					display:'none'
				}).click(showNextItem);
				$('<div></div>').addClass('ui-galleria-panel-nav-prev').appendTo(j_gallery).css({
					position:'absolute',
					zIndex:'1100',
					top:((opts.filmstrip_position=='top'?opts.frame_gap+wrapper_height:0)+(opts.panel_height-22)/2)+'px',
					left:((opts.filmstrip_position=='left'?opts.frame_gap+wrapper_width:0)+10)+'px',
					display:'none'
				}).click(showPrevItem);
			}

			//Set size and position of panel container
			j_panel_wrapper.css({
				width:opts.panel_width+'px',
				height:opts.panel_height+'px',
				position:'absolute',
				overflow:'hidden'
			});

			if(opts.show_filmstrip) {
				switch(opts.filmstrip_position) {
					case 'top':j_panel_wrapper.css({top:wrapper_height+opts.frame_gap+'px'});break;
					case 'left':j_panel_wrapper.css({left:wrapper_width+opts.frame_gap+'px'});break;
					default:break;
				}
			}

			// Set the height and width of each panel, and position it appropriately within the gallery
			j_panels.each(function(i){
				$(this).css({
					width:(opts.panel_width-extraWidth(j_panels))+'px',
					height:(opts.panel_height-extraHeight(j_panels))+'px',
					position:'absolute',
					top:0,
					left:0,
					display:'none'
				});
			});

			// Position each panel overlay within panel
			$('.ui-galleria-overlay',j_panels).css({
				position:'absolute',
				zIndex:'999',
				width:(opts.panel_width-extraWidth($('.ui-galleria-overlay',j_panels)))+'px',
				left:0
			});
			$('.ui-galleria-overlay-background',j_panels).css({
				position:'absolute',
				zIndex:'998',
				width:opts.panel_width+'px',
				left:0,
				opacity:opts.overlay_opacity
			});
			if(opts.overlay_position=='top') {
				$('.ui-galleria-overlay',j_panels).css('top',0);
				$('.ui-galleria-overlay-background',j_panels).css('top',0);
			} else {
				$('.ui-galleria-overlay',j_panels).css('bottom',0);
				$('.ui-galleria-overlay-background',j_panels).css('bottom',0);
			}

			$('.ui-galleria-panel iframe',j_panels).css({
				width:opts.panel_width+'px',
				height:opts.panel_height+'px',
				border:0
			});

			// If panel images have to be scaled to fit within frame, do so and position them accordingly
			if(scale_panel_images) {
				$('img',j_panels).each(function(i){
					$(this).css({
						height:panel_img_scale[i%item_count]*img_h[i%item_count],
						width:panel_img_scale[i%item_count]*img_w[i%item_count],
						position:'relative',
						top:(opts.panel_height-(panel_img_scale[i%item_count]*img_h[i%item_count]))/2+'px',
						left:(opts.panel_width-(panel_img_scale[i%item_count]*img_w[i%item_count]))/2+'px'
					});
				});
			}
		};

	/*
	**	buildFilmstrip()
	**		Construct filmstrip from <ul class="ui-galleria-filmstrip"> element
	**		NOTE - 'filmstrip' class is automatically added to UL passed to plugin
	*/
		function buildFilmstrip() {

			// Add wrapper to filmstrip to hide extra frames
			j_filmstrip.wrap('<div class="gv-strip_wrapper"></div>');
			if(opts.filmstrip_style == 'scroll' && slide_method=='strip') {
				j_frames.clone().appendTo(j_filmstrip);
				j_frames.clone().appendTo(j_filmstrip);
				j_frames = $('li',j_filmstrip);
			}

			// If captions are enabled, add caption DIV to each frame and fill with the image titles
			if(opts.show_captions) {
				j_frames.append('<div class="ui-galleria-caption"></div>').each(function(i){
					$(this).find('.ui-galleria-caption').html($(this).find('img').attr('title'));
				});
			}

			// Position the filmstrip within the gallery
			j_filmstrip.css({
				listStyle:'none',
				margin:0,
				padding:0,
				width:strip_width+'px',
				position:'absolute',
				zIndex:'900',
				top:(filmstrip_orientation=='vertical' && opts.filmstrip_style == 'scroll' && slide_method=='strip'?-((f_frame_height+opts.frame_gap)*iterator):0)+'px',
				left:(filmstrip_orientation=='horizontal' && opts.filmstrip_style == 'scroll' && slide_method=='strip'?-((f_frame_width+opts.frame_gap)*iterator):0)+'px',
				height:strip_height+'px'
			});

			j_frames.css({
				position:'relative',
				height:f_frame_height+(opts.show_captions?f_caption_height:0)+'px',
				width:f_frame_width+'px',
				zIndex:'901',
				padding:0,
				cursor:'pointer',
				marginBottom:opts.frame_gap+'px',
				marginRight:opts.frame_gap+'px'
			});

			// Apply styling to individual image wrappers. These will eventually hide overflow in the case of cropped filmstrip images
			$('.ui-galleria-img-wrap',j_frames).each(function(i){
				$(this).css({
					height:Math.min(opts.frame_height,img_h[i%item_count]*frame_img_scale[i%item_count])+'px',
					width:Math.min(opts.frame_width,img_w[i%item_count]*frame_img_scale[i%item_count])+'px',
					position:'relative',
					top:(opts.show_captions && opts.filmstrip_position=='top'?f_caption_height:0)+Math.max(0,(opts.frame_height-(frame_img_scale[i%item_count]*img_h[i%item_count]))/2)+'px',
					left:Math.max(0,(opts.frame_width-(frame_img_scale[i%item_count]*img_w[i%item_count]))/2)+'px',
					overflow:'hidden'
				});
			});

			// Scale each filmstrip image if necessary and position it within the image wrapper
			$('img',j_frames).each(function(i){
				$(this).css({
					opacity:opts.frame_opacity,
					height:img_h[i%item_count]*frame_img_scale[i%item_count]+'px',
					width:img_w[i%item_count]*frame_img_scale[i%item_count]+'px',
					position:'relative',
					top:Math.min(0,(opts.frame_height-(frame_img_scale[i%item_count]*img_h[i%item_count]))/2)+'px',
					left:Math.min(0,(opts.frame_width-(frame_img_scale[i%item_count]*img_w[i%item_count]))/2)+'px'
				}).mouseover(function(){
					$(this).stop().animate({opacity:1},300);
				}).mouseout(function(){
					//Don't fade out current frame on mouseout
					if(!$(this).parent().parent().hasClass('current')){$(this).stop().animate({opacity:opts.frame_opacity},300);}
				});
			});

			// Set overflow of filmstrip wrapper to hidden so as to hide frames that don't fit within the gallery
			$('.gv-strip_wrapper',j_gallery).css({
				position:'absolute',
				overflow:'hidden'
			});

			// Position filmstrip within gallery based on user options
			if(filmstrip_orientation=='horizontal') {
				$('.gv-strip_wrapper',j_gallery).css({
					top:opts.show_panels ? opts.filmstrip_position=='top'?0:opts.panel_height+opts.frame_gap+'px' : 0,
					left:((gallery_width-wrapper_width)/2)+'px',
					width:wrapper_width+'px',
					height:wrapper_height+'px'
				});
			} else {
				$('.gv-strip_wrapper',j_gallery).css({
					left:opts.show_panels ? opts.filmstrip_position=='left'?0:opts.panel_width+opts.frame_gap+'px' : 0,
					top:0,
					width:wrapper_width+'px',
					height:wrapper_height+'px'
				});
			}

			// Style frame captions
			$('.ui-galleria-caption',j_gallery).css({
				position:'absolute',
				top:(opts.filmstrip_position=='bottom'?f_frame_height:0)+'px',
				left:0,
				margin:0,
				width:f_caption_width+'px',
				overflow:'hidden'
			});

			// Create pointer for current frame
			var pointer = $('<div></div>');
			pointer.addClass('ui-galleria-pointer').appendTo(j_gallery).css({
				 position:'absolute',
				 zIndex:'1000',
				 width:0,
				 fontSize:0,
				 lineHeight:0,
				 borderTopWidth:pointer_height+'px',
				 borderRightWidth:(pointer_width/2)+'px',
				 borderBottomWidth:pointer_height+'px',
				 borderLeftWidth:(pointer_width/2)+'px',
				 borderStyle:'solid'
			});

			// For IE6, use predefined color string in place of transparent (IE6 bug fix, see stylesheet)
			var transColor = $.browser.msie && $.browser.version.substr(0,1) == '6' ? 'pink' : 'transparent';

			// If there are no panels, make pointer transparent (nothing to point to)
			// This is not the optimal solution, but we need the pointer to exist as a reference for transition animations
			if(!opts.show_panels) {pointer.css('borderColor',transColor);}
			switch(opts.filmstrip_position) {
				case 'top':pointer.css({
								top:wrapper_height+'px',
								left:((gallery_width-wrapper_width)/2)+(slide_method=='strip'?0:((f_frame_width+opts.frame_gap)*iterator))+((f_frame_width/2)-(pointer_width/2))+'px',
								borderBottomColor:transColor,
								borderRightColor:transColor,
								borderLeftColor:transColor
							});break;
				case 'bottom':pointer.css({
									bottom:wrapper_height+'px',
									left:((gallery_width-wrapper_width)/2)+(slide_method=='strip'?0:((f_frame_width+opts.frame_gap)*iterator))+((f_frame_width/2)-(pointer_width/2))+'px',
									borderTopColor:transColor,
									borderRightColor:transColor,
									borderLeftColor:transColor
								});break;
				case 'left':pointer.css({
								left:wrapper_width+'px',
								top:(f_frame_height/2)-(pointer_height)+(slide_method=='strip'?0:((f_frame_height+opts.frame_gap)*iterator))+'px',
								borderBottomColor:transColor,
								borderRightColor:transColor,
								borderTopColor:transColor
							});break;
				case 'right':pointer.css({
								right:wrapper_width+'px',
								top:(f_frame_height/2)-(pointer_height)+(slide_method=='strip'?0:((f_frame_height+opts.frame_gap)*iterator))+'px',
								borderBottomColor:transColor,
								borderLeftColor:transColor,
								borderTopColor:transColor
							});break;
			}

			j_pointer = $('.ui-galleria-pointer',j_gallery);

			// Add navigation buttons
			if(opts.show_filmstrip_nav) {
				var navNext = $('<div></div>');
				navNext.addClass('ui-galleria-nav-next ui-icon ui-icon-circle-triangle-e').appendTo(j_gallery).css({
					position:'absolute'
				}).click(showNextItem);

				var navPrev = $('<div></div>');
				navPrev.addClass('ui-galleria-nav-prev ui-icon ui-icon-circle-triangle-w').appendTo(j_gallery).css({
					position:'absolute'
				}).click(showPrevItem);

				if(filmstrip_orientation=='horizontal') {
					navNext.css({
						top:(opts.show_panels ? (opts.filmstrip_position=='top'?0:opts.panel_height+opts.frame_gap) : 0)+((strip_height-22)/2)+'px',
						right:((gallery_width)/2)-(wrapper_width/2)-opts.frame_gap-22+'px'
					});
					navPrev.css({
						top:(opts.show_panels ? (opts.filmstrip_position=='top'?0:opts.panel_height+opts.frame_gap) : 0)+((strip_height-22)/2)+'px',
						left:((gallery_width)/2)-(wrapper_width/2)-opts.frame_gap-22+'px'
					 });
				} else {
					navNext.css({
						left:(opts.show_panels ? (opts.filmstrip_position=='left'?0:opts.panel_width+opts.frame_gap) : 0)+((strip_width-22)/2)+13+'px',
						top:wrapper_height+opts.frame_gap+'px'
					});
					navPrev.css({
						left:(opts.show_panels ? (opts.filmstrip_position=='left'?0:opts.panel_width+opts.frame_gap) : 0)+((strip_width-22)/2)-13+'px',
						top:wrapper_height+opts.frame_gap+'px'
					});
				}
			}
		};

	/*
	**	mouseIsOverGallery(int,int)
	**		Check to see if mouse coordinates lie within borders of gallery
	**		This is a more reliable method than using the mouseover event
	**		RETURN - boolean
	*/
		function mouseIsOverGallery(x,y) {
			var pos = getPos(j_gallery[0]);
			var top = pos.top;
			var left = pos.left;
			return x > left && x < left+j_gallery.outerWidth() && y > top && y < top+j_gallery.outerHeight();
		};

	/*
	**	mouseIsOverPanel(int,int)
	**		Check to see if mouse coordinates lie within borders of panel
	**		This is a more reliable method than using the mouseover event
	**		RETURN - boolean
	*/
		function mouseIsOverPanel(x,y) {

			// Get position of panel wrapper in relation to gallery
			var pos = getPos($('#'+id+' .ui-galleria-panel_wrap')[0]);
			var gPos = getPos(j_gallery[0]);

			// Add wrap position to gallery position
			var top = pos.top+gPos.top;
			var left = pos.left+gPos.left;

			return x > left && x < left+j_panels.outerWidth() && y > top && y < top+j_panels.outerHeight();
		};

	/*
	**	getInt(string)
	**		Parse a string to obtain the integer value contained
	**		If the string contains no number, return zero
	**		RETURN - int
	*/
		function getInt(i) {
			i = parseInt(i,10);
			if(isNaN(i)) {i = 0;}
			return i;
		};

	/*
	**	buildGallery()
	**		Construct HTML and CSS for the gallery, based on user options
	*/
		function buildGallery() {

			// Get gallery images
			var gallery_images = opts.show_filmstrip?$('img',j_frames):$('img',j_panels);

			// For each image in the gallery, add its original dimensions and scaled dimensions to the appropriate arrays for later reference
			gallery_images.each(function(i){
				img_h[i] = this.height;
				img_w[i] = this.width;

				if(opts.frame_scale=='nocrop') {
					frame_img_scale[i] = Math.min(opts.frame_height/img_h[i],opts.frame_width/img_w[i]);
				} else {
					frame_img_scale[i] = Math.max(opts.frame_height/img_h[i],opts.frame_width/img_w[i]);
				}

				if(opts.panel_scale=='nocrop') {
					panel_img_scale[i] = Math.min(opts.panel_height/img_h[i],opts.panel_width/img_w[i]);
				} else {
					panel_img_scale[i] = Math.max(opts.panel_height/img_h[i],opts.panel_width/img_w[i]);
				}
			});

			// Set gallery dimensions
			j_gallery.css({
				position:'relative',
				width:gallery_width+'px',
				height:gallery_height+'px'
			});

			// Build filmstrip if necessary
			if(opts.show_filmstrip) {
				buildFilmstrip();
				enableFrameClicking();
			}

			//Strip out panel overlays if the user does not want to display them
			if(!opts.show_overlays) {
				$('.ui-galleria-overlay',j_gallery).remove();
			}

			// Build panels if necessary
			if(opts.show_panels) {
				buildPanels();
			}

			// If user opts to pause on hover, or no filmstrip exists, add some mouseover functionality
			if(opts.pause_on_hover || opts.show_panel_nav) {
				$(document).mousemove(function(e){

					// If user wants to pause on hover and mouse is over the gallery, halt any animations
					if(opts.pause_on_hover) {
						if(mouseIsOverGallery(e.pageX,e.pageY) && !paused) {
							// Pause slideshow in 500ms. This allows for brief swipes of the mouse over the gallery without unnecessarily pausing it
							$(document).oneTime(500,"animation_pause",function(){
								$(document).stopTime("transition");
								paused=true;
							});
						} else {
							$(document).stopTime("animation_pause");
							if(paused && opts.transition_interval > 0) {
								$(document).everyTime(opts.transition_interval,"transition",function(){
									showNextItem();
								});
								paused = false;
							}
						}
					}

					// If panel navigation is turned on, display or hide it based on mouse position
					if(opts.show_panel_nav) {
						if(mouseIsOverPanel(e.pageX,e.pageY) && !panel_nav_displayed) {
							$('.ui-galleria-panel-nav-next, .ui-galleria-panel-nav-prev',j_gallery).show();
							panel_nav_displayed = true;
						} else if(!mouseIsOverPanel(e.pageX,e.pageY) && panel_nav_displayed) {
							$('.ui-galleria-panel-nav-next, .ui-galleria-panel-nav-prev',j_gallery).hide();
							panel_nav_displayed = false;
						}
					}
				});
			}

			// Hide loading box and display gallery
			j_filmstrip.css('visibility','visible');
			j_gallery.css('visibility','visible');

			// Show the 'first' item and then fade out the loader box and begin slideshow timer if necessary
			showItem(iterator,10,function(){
				$('.ui-galleria-loader',j_gallery).fadeOut('1000',function(){

					// If we have more than one item, begin automated transitions
					if(item_count > 1 && opts.transition_interval > 0) {
						$(document).everyTime(opts.transition_interval,"transition",function(){
							showNextItem();
						});
					}
				});
			});


		};

	/*
	**	MAIN PLUGIN CODE
	*/
		return this.each(function() {

			// Get UL passed to plugin
			var _t = $(this);

			// Hide the unstyled UL until we've created the gallery
			_t.css('visibility','hidden');

			// Reference images for later use (we'll wait for each to load before building gallery)
			gallery_images = $('img',_t);
			image_count = gallery_images.length;

			// Set the current frame index to that chosen by the user
			// current is 0-based and opts.start_frame is 1-based
			current = opts.start_frame-1;

			// Wrap UL in DIV and transfer ID to container DIV
			_t.wrap("<div></div>");
			j_gallery = _t.parent();
			j_gallery.css('visibility','hidden').attr('id',_t.attr('id')).addClass('ui-galleria ui-widget').addClass(_t.attr('class'));

			// Assign filmstrip class to the UL sent to the plugin
			_t.removeAttr('id').addClass('ui-galleria-filmstrip');

			// If the transition or pause timers exist for any reason, stop them now.
			$(document).stopTime("transition");
			$(document).stopTime("animation_pause");

			// Save the id of the UL passed to the plugin
			id = j_gallery.attr('id');

			// If the UL does not contain any <div class="ui-galleria-panel-content"> elements, we will scale the UL images to fill the panels
			scale_panel_images = $('.ui-galleria-panel-content',j_gallery).length==0;

			animate_panels = (opts.panel_animation != 'none');

			// Determine filmstrip orientation (vertical or horizontal)
			filmstrip_orientation = (opts.filmstrip_position=='top'||opts.filmstrip_position=='bottom'?'horizontal':'vertical');

			// Do not show captions on vertical filmstrips (override user set option)
			if(filmstrip_orientation=='vertical') {opts.show_captions = false;}


			// Make sure pointer does not extend past width of frame
			if(filmstrip_orientation == 'horizontal' && opts.pointer_size > opts.frame_width/2) {
				opts.pointer_size = opts.frame_width/2;
			}
			if(filmstrip_orientation == 'vertical' && opts.pointer_size > opts.frame_height/2) {
				opts.pointer_size = opts.frame_height/2;
			}


			// Assign elements to variables to minimize calls to jQuery
			j_filmstrip = $('.ui-galleria-filmstrip',j_gallery);
			j_frames = $('li',j_filmstrip);
			j_frames.addClass('ui-galleria-frame');
			j_panel_wrapper = $('<div>');
			j_panel_wrapper.addClass('ui-galleria-panel_wrap').prependTo(j_gallery);

			// If the user wants panels, generate them using the filmstrip images
			if(opts.show_panels) {
				for(i=j_frames.length-1;i>=0;i--) {
					jf = j_frames.eq(i);
					if(jf.find('.ui-galleria-panel-content').length>0) {
						jf.find('.ui-galleria-panel-content').remove().prependTo(j_panel_wrapper).addClass('ui-galleria-panel').addClass(jf.attr('class')).removeClass('ui-galleria-frame');
					} else {
						p = $('<div>');
						p.addClass('ui-galleria-panel');
						p.addClass(jf.attr('class')).removeClass('ui-galleria-frame');
						im = $('<img />');
						jfimg = jf.find('img').eq(0)
						im.attr('src',jfimg.attr('src'));
						if(jfimg.parent('a').length > 0){
							ima = $('<a></a>');
							ima.attr('href',jfimg.parent('a').eq(0).attr('href'));
							ima.attr('target',jfimg.parent('a').eq(0).attr('target'));
							ima.append(im);
							ima.appendTo(p);
						} else {
							im.appendTo(p);
						}
						p.prependTo(j_panel_wrapper);
						j_frames.eq(i).find('.ui-galleria-overlay').remove().appendTo(p);
					}
				}
			} else {
				$('.ui-galleria-overlay',j_frames).remove();
				$('.ui-galleria-panel-content',j_frames).remove();
			}

			// If the user doesn't want a filmstrip, delete it
			if(!opts.show_filmstrip) {j_filmstrip.remove();}
			else {
				// Wrap the frame images (and links, if applicable) in container divs
				// These divs will handle cropping and zooming of the images
				j_frames.each(function(i){
					if($(this).find('a').length>0) {
						$(this).find('a').wrap('<div class="ui-galleria-img-wrap"></div>');
					} else {
						$(this).find('img').wrap('<div class="ui-galleria-img-wrap"></div>');
					}
				});
				j_frame_img_wrappers = $('.ui-galleria-img-wrap',j_frames);
			}

			j_panels = $('.ui-galleria-panel',j_gallery);

			if(!opts.show_panels) {
				opts.panel_height = 0;
				opts.panel_width = 0;
			}

			// Briefly create a caption element so galleryView can determine any padding or borders applied by the user
			$('<div class="ui-galleria-caption"></div>').appendTo(j_frames);

			// Determine final frame dimensions, accounting for user-added padding and border
			f_frame_width = opts.frame_width+extraWidth(j_frame_img_wrappers);
			f_frame_height = opts.frame_height+extraHeight(j_frame_img_wrappers);
			frame_caption_size = getInt($('.ui-galleria-caption',j_gallery).css('height'));
			f_caption_width = f_frame_width - extraWidth($('.ui-galleria-caption',j_gallery));
			f_caption_height = frame_caption_size + extraHeight($('.ui-galleria-caption',j_gallery));

			// Delete the temporary caption element
			$('.ui-galleria-caption',j_gallery).remove();

			// Number of frames in filmstrip
			item_count = opts.show_panels?j_panels.length:j_frames.length;

			// Number of frames that can display within the gallery block
			// 64 = width of block for navigation button * 2 + 20
			if(filmstrip_orientation=='horizontal') {
				strip_size = opts.show_panels?Math.floor((opts.panel_width+opts.frame_gap-(!opts.show_filmstrip_nav?0:(opts.frame_gap+22)*2))/(f_frame_width+opts.frame_gap)):Math.min(item_count,opts.filmstrip_size);
			} else {
				strip_size = opts.show_panels?Math.floor((opts.panel_height+opts.frame_gap-(!opts.show_filmstrip_nav?0:opts.frame_gap+22))/(f_frame_height+opts.frame_gap)):Math.min(item_count,opts.filmstrip_size);
			}

			// Determine animation method for filmstrip
			// If more items than strip size, slide filmstrip
			// Otherwise, slide pointer
			if(strip_size >= item_count) {
				slide_method = 'pointer';
				strip_size = item_count;
			}
			else {
				slide_method = 'strip';
			}

			// Do not show pointer if there are multiple rows in the filmstrip
			if(Math.ceil(item_count/strip_size) > 1) {opts.pointer_size = 0;}

			// Define dimensions of pointer <div>
			pointer_height = opts.pointer_size;
			pointer_width = opts.pointer_size*2;

			iterator = opts.start_frame-1;
			if(opts.filmstrip_style == 'scroll' && strip_size < item_count) {
				iterator += item_count;
			}

			// Determine dimensions of various gallery elements
			//_filmstrip_margin = (opts.show_panels?getInt(j_filmstrip.css('marginTop')):0);
			j_filmstrip.css('margin',0);

			// Width of filmstrip
			if(filmstrip_orientation=='horizontal') {
				if(opts.filmstrip_style == 'show all' || (opts.filmstrip_style == 'scroll' && slide_method == 'pointer')) {
					strip_width = (f_frame_width*strip_size)+(opts.frame_gap*(strip_size));
				}
				else {
					strip_width = (f_frame_width*item_count*3)+(opts.frame_gap*((item_count*3)));
				}
			} else {
				if(opts.filmstrip_style == 'show all') {
					strip_width = (f_frame_width*Math.ceil(item_count/strip_size))+(opts.frame_gap*(Math.ceil(item_count/strip_size)));
				} else {
					strip_width = (f_frame_width);
				}
			}

			// Height of filmstrip
			if(filmstrip_orientation=='horizontal') {
				if(opts.filmstrip_style == 'show all') {
					strip_height = ((f_frame_height+(opts.show_captions?f_caption_height:0))*Math.ceil(item_count/strip_size))+(opts.frame_gap*(Math.ceil(item_count/strip_size)-1));
				} else {
					strip_height = (f_frame_height+(opts.show_captions?f_caption_height:0));
				}
			} else {
				if(opts.filmstrip_style =='show all' || (opts.filmstrip_style == 'scroll' && slide_method == 'pointer')) {
					strip_height = ((f_frame_height*strip_size)+opts.frame_gap*(strip_size-1));
				}
				else {
					strip_height = (f_frame_height*item_count*3)+(opts.frame_gap*((item_count*3)-1));
				}
			}

			// Width of filmstrip wrapper (to hide overflow)
			if(filmstrip_orientation=='horizontal') {
				wrapper_width = ((strip_size*f_frame_width)+((strip_size-1)*opts.frame_gap));
				if(opts.filmstrip_style == 'show all') {
					wrapper_height = ((f_frame_height+(opts.show_captions?f_caption_height:0))*Math.ceil(item_count/strip_size))+(opts.frame_gap*(Math.ceil(item_count/strip_size)-1));
				} else {
					wrapper_height = (f_frame_height+(opts.show_captions?f_caption_height:0));
				}
			} else {
				wrapper_height = ((strip_size*f_frame_height)+((strip_size-1)*opts.frame_gap));
				if(opts.filmstrip_style == 'show all') {
					wrapper_width = (f_frame_width*Math.ceil(item_count/strip_size))+(opts.frame_gap*(Math.ceil(item_count/strip_size)-1));
				} else {
					wrapper_width = f_frame_width;
				}
			}

			// There shouldn't be any padding on the gallery element
			j_gallery.css('padding',0);

			// Determine final dimensions of gallery
			if(filmstrip_orientation=='horizontal') {

				// Width of gallery block
				gallery_width = opts.show_panels?opts.panel_width:wrapper_width+44+(opts.frame_gap*2);

				// Height of gallery block = screen + filmstrip + captions (optional)
				gallery_height = (opts.show_panels?opts.panel_height+(opts.show_filmstrip?opts.frame_gap:0):0)+(opts.show_filmstrip?wrapper_height:0);
			} else {

				// Width of gallery block
				gallery_height = opts.show_panels?opts.panel_height:wrapper_height+22;

				// Height of gallery block = screen + filmstrip + captions (optional)
				gallery_width = (opts.show_panels?opts.panel_width+(opts.show_filmstrip?opts.frame_gap:0):0)+(opts.show_filmstrip?wrapper_width:0);
			}

			// Place loading box over gallery until page loads
			galleryPos = getPos(j_gallery[0]);
			$('<div>').addClass('ui-galleria-loader').css({
				position:'absolute',
				zIndex:'32666',
				opacity:1,
				top:0,
				left:0,
				width:gallery_width+'px',
				height:gallery_height+'px'
			}).appendTo(j_gallery);

			// We don't want to move to the next frame before the current one is done loading
			// If the animation speed is greater than the delay between animations, set them equal
			if(opts.transition_speed > opts.transition_interval && opts.transition_interval > 0) {
				opts.transition_speed = opts.transition_interval;
			}

			// Set pointer animation speed based on user options
			pointer_speed = opts.animate_pointer ? opts.transition_speed : 0;

			// Build gallery as soon as all images are loaded or pulled from cache
			if(!window_loaded) {
				gallery_images.each(function(i){

					// First, see if image is already loaded (gallery being built after window loads or image is pulled from cache)
					if($(this).attr('complete')) {
						loaded_images++;
						if(loaded_images==image_count) {
							buildGallery();
							window_loaded;
						}
					} else {

						// Otherwise, increment counter once image loads
						$(this).load(function(){
							loaded_images++;
							if(loaded_images==image_count) {
								buildGallery();
								window_loaded;
							}
						});
					}
				});
			} else {
				buildGallery();
			}
		});
	};

/*
**	GalleryView options and default values
*/
	$.fn.galleryView.defaults = {

		transition_speed: 800,				//INT - duration of panel/frame transition (in milliseconds)
		transition_interval: 4000,			//INT - delay between panel/frame transitions (in milliseconds)
		easing: 'swing',					//STRING - easing method to use for animations (jQuery provides 'swing' or 'linear', more available with jQuery UI or Easing plugin)
		pause_on_hover: false,				//BOOLEAN - flag to pause slideshow when user hovers over the gallery


		show_panels: true,					//BOOLEAN - flag to show or hide panel portion of gallery
		panel_width: 600,					//INT - width of gallery panel (in pixels)
		panel_height: 400,					//INT - height of gallery panel (in pixels)
		panel_animation: 'crossfade',		//STRING - animation method for panel transitions (crossfade,fade,slide,zoomOut,none)
		overlay_opacity: 0.7,				//FLOAT - transparency for panel overlay (1.0 = opaque, 0.0 = transparent)
		overlay_position: 'bottom',			//STRING - position of panel overlay (bottom, top)
		panel_scale: 'crop',				//STRING - cropping option for panel images (crop = scale image and fit to aspect ratio determined by panel_width and panel_height, nocrop = scale image and preserve original aspect ratio)
		show_panel_nav: true,				//BOOLEAN - flag to show or hide panel navigation buttons
		show_overlays: false,				//BOOLEAN - flag to show or hide panel overlays


		show_filmstrip: true,				//BOOLEAN - flag to show or hide filmstrip portion of gallery
		frame_width: 60,					//INT - width of filmstrip frames (in pixels)
		frame_height: 40,					//INT - width of filmstrip frames (in pixels)
		start_frame: 1,						//INT - index of panel/frame to show first when gallery loads
		filmstrip_size: 3,					//INT - number of frames to show in filmstrip-only gallery
		frame_opacity: 0.3,					//FLOAT - transparency of non-active frames (1.0 = opaque, 0.0 = transparent)
		filmstrip_style: 'scroll',			//STRING - type of filmstrip to use (scroll, show all)
		filmstrip_position: 'bottom',		//STRING - position of filmstrip within gallery (bottom, top, left, right)
		show_filmstrip_nav: true,			//BOOLEAN - flag indicating whether to display navigation buttons
		frame_scale: 'crop',				//STRING - cropping option for filmstrip images (same as above)
		frame_gap: 5,						//INT - spacing between frames within filmstrip (in pixels)
		show_captions: false,				//BOOLEAN - flag to show or hide frame captions


		pointer_size: 8,					//INT - Height of frame pointer (in pixels)
		animate_pointer: true				//BOOLEAN - flag to animate pointer or move it instantly to target frame

	};
})(jQuery);

/**
 * jQuery.timers - Timer abstractions for jQuery
 * Written by Blair Mitchelmore (blair DOT mitchelmore AT gmail DOT com)
 * Licensed under the WTFPL (http://sam.zoy.org/wtfpl/).
 * Date: 2009/10/16
 *
 * @author Blair Mitchelmore
 * @version 1.2
 *
 **/

jQuery.fn.extend({
	everyTime: function(interval, label, fn, times) {
		return this.each(function() {
			jQuery.timer.add(this, interval, label, fn, times);
		});
	},
	oneTime: function(interval, label, fn) {
		return this.each(function() {
			jQuery.timer.add(this, interval, label, fn, 1);
		});
	},
	stopTime: function(label, fn) {
		return this.each(function() {
			jQuery.timer.remove(this, label, fn);
		});
	}
});

jQuery.extend({
	timer: {
		global: [],
		guid: 1,
		dataKey: "jQuery.timer",
		regex: /^([0-9]+(?:\.[0-9]*)?)\s*(.*s)?$/,
		powers: {
			// Yeah this is major overkill...
			'ms': 1,
			'cs': 10,
			'ds': 100,
			's': 1000,
			'das': 10000,
			'hs': 100000,
			'ks': 1000000
		},
		timeParse: function(value) {
			if (value == undefined || value == null)
				return null;
			var result = this.regex.exec(jQuery.trim(value.toString()));
			if (result[2]) {
				var num = parseFloat(result[1]);
				var mult = this.powers[result[2]] || 1;
				return num * mult;
			} else {
				return value;
			}
		},
		add: function(element, interval, label, fn, times) {
			var counter = 0;

			if (jQuery.isFunction(label)) {
				if (!times)
					times = fn;
				fn = label;
				label = interval;
			}

			interval = jQuery.timer.timeParse(interval);

			if (typeof interval != 'number' || isNaN(interval) || interval < 0)
				return;

			if (typeof times != 'number' || isNaN(times) || times < 0)
				times = 0;

			times = times || 0;

			var timers = jQuery.data(element, this.dataKey) || jQuery.data(element, this.dataKey, {});

			if (!timers[label])
				timers[label] = {};

			fn.timerID = fn.timerID || this.guid++;

			var handler = function() {
				if ((++counter > times && times !== 0) || fn.call(element, counter) === false)
					jQuery.timer.remove(element, label, fn);
			};

			handler.timerID = fn.timerID;

			if (!timers[label][fn.timerID])
				timers[label][fn.timerID] = window.setInterval(handler,interval);

			this.global.push( element );

		},
		remove: function(element, label, fn) {
			var timers = jQuery.data(element, this.dataKey), ret;

			if ( timers ) {

				if (!label) {
					for ( label in timers )
						this.remove(element, label, fn);
				} else if ( timers[label] ) {
					if ( fn ) {
						if ( fn.timerID ) {
							window.clearInterval(timers[label][fn.timerID]);
							delete timers[label][fn.timerID];
						}
					} else {
						for ( var fn in timers[label] ) {
							window.clearInterval(timers[label][fn]);
							delete timers[label][fn];
						}
					}

					for ( ret in timers[label] ) break;
					if ( !ret ) {
						ret = null;
						delete timers[label];
					}
				}

				for ( ret in timers ) break;
				if ( !ret )
					jQuery.removeData(element, this.dataKey);
			}
		}
	}
});

jQuery(window).bind("unload", function() {
	jQuery.each(jQuery.timer.global, function(index, item) {
		jQuery.timer.remove(item);
	});
});


/**
 * PrimeFaces Galleria Widget
 */
PrimeFaces.widget.Galleria = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jq.galleryView(this.cfg);
    }
    
});