/*
 * jQuery beforeafter plugin
 * @author admin@catchmyfame.com - http://www.catchmyfame.com
 * @version 1.1
 * @date January 12, 2010
 * @category jQuery plugin
 * @copyright (c) 2009 admin@catchmyfame.com (www.catchmyfame.com)
 * @license CC Attribution-No Derivative Works 3.0 - http://creativecommons.org/licenses/by-nd/3.0/
 */
// before image : $('div:eq(2)', obj)
// after image  : $('div:eq(3)', obj)
(function($){
	$.fn.extend({ 
		beforeAfter: function(options)
		{
			var defaults = 
			{
				animateIntro : false,
				introDelay : 1000,
				introDuration : 1000,
				showFullLinks : true,
				imagePath : '/js/beforeAfter/'
			};
		var options = $.extend(defaults, options);

		var randID =  Math.round(Math.random()*100000000);
	
    		return this.each(function() {
			var o=options;
			var obj = $(this);

			var imgWidth = $('img:first', obj).width();
			var imgHeight = $('img:first', obj).height();
			
			$(obj)
			.width(imgWidth)
			.height(imgHeight)
			.css({'overflow':'hidden','position':'relative','padding':'0'});
			
			// Preload images and assign them IDs
			var image1 = $('<img />').attr('src', $('img:first', obj).attr('src'));
			var image2 = $('<img />').attr('src', $('img:last', obj).attr('src'));
			$('img:first', obj).attr('id','beforeimage'+randID);
			$('img:last', obj).attr('id','afterimage'+randID);
			
			$('div', obj).css('float','left'); // Float all divs within the container left
			
			// Create an inner div wrapper (dragwrapper) to hold the images.
			$(obj).prepend('<div id="dragwrapper'+randID+'"><div id="drag'+randID+'"><img width="8" height="56" alt="handle" src="' + o.handle + '" title="Drag me left or right to see the before and after images" id="handle'+randID+'" /></div></div>'); // Create drag handle
			$('#dragwrapper'+randID).css({'position':'absolute','padding':'0','left':(imgWidth/2)-($('#handle'+randID).width()/2)+'px','z-index':'20'}).width($('#handle'+randID).width()).height(imgHeight);
			$('#dragwrapper'+randID).css({'opacity':.25}); // Sets the dragwrapper and contents to .25 opacity
				
			$('div:eq(2)', obj).height(imgHeight).width(imgWidth/2).css({'position':'absolute','overflow':'hidden','left':'0px','z-index':'10'}); // Set CSS properties of the before image div
			$('div:eq(3)', obj).height(imgHeight).width(imgWidth).css({'position':'absolute','overflow':'hidden','right':'0px'});	// Set CSS properties of the after image div
			$('#drag'+randID).width(2).height(imgHeight).css({'background':'#888','position':'absolute','left':'3px'});	// Set drag handle CSS properties
			$('#beforeimage'+randID).css({'position':'absolute','top':'0px','left':'0px'});
			$('#afterimage'+randID).css({'position':'absolute','top':'0px','right':'0px'});
			$('#handle'+randID).css({'position':'relative','cursor':'pointer','top':(imgHeight/2)-($('#handle'+randID).height()/2)+'px','left':'-3px'})
			
			$(obj).append('<img src="'+o.lt+'" width="7" height="15" id="lt-arrow'+randID+'"><img src="'+o.rt+'" width="7" height="15" id="rt-arrow'+randID+'">');

			if(o.showFullLinks)
			{	
				$(obj).after('<div class="balinks" id="links'+randID+'" style="position:relative"><span class="bflinks"><a id="showleft'+randID+'" href="javascript:void(0)">Show only before</a></span><span class="bflinks"><a id="showright'+randID+'" href="javascript:void(0)">Show only after</a></span></div>');
				$('#links'+randID).width(imgWidth);
				$('#showleft'+randID).css({'position':'relative','left':'0px'}).click(function(){
					$('div:eq(2)', obj).animate({width:imgWidth},200);
					$('#dragwrapper'+randID).animate({left:imgWidth-$('#dragwrapper'+randID).width()+'px'},200);
				});
				$('#showright'+randID).css({'position':'absolute','right':'0px'}).click(function(){
					$('div:eq(2)', obj).animate({width:0},200);
					$('#dragwrapper'+randID).animate({left:'0px'},200);
				});
			}

			var barOffset = $('#dragwrapper'+randID).offset(); // The coordinates of the dragwrapper div
			var startBarOffset = barOffset.left; // The left coordinate of the dragwrapper div
			var originalLeftWidth = $('div:eq(2)', obj).width();
			var originalRightWidth = $('div:eq(3)', obj).width();

			$('#dragwrapper'+randID).draggable({handle:$('#handle'+randID),containment:obj,axis:'x',drag: function(e, ui){
				var offset = $(this).offset();
				var barPosition = offset.left - startBarOffset;
				$('div:eq(2)', obj).width(originalLeftWidth + barPosition);
				$('#lt-arrow'+randID).stop().animate({opacity:0},50);
				$('#rt-arrow'+randID).stop().animate({opacity:0},50);
				}
			});

			if(o.animateIntro)
			{
				$('div:eq(2)', obj).width(imgWidth);
				$('#dragwrapper'+randID).css('left',imgWidth-($('#dragwrapper'+randID).width()/2)+'px');
				setTimeout(function(){
					$('#dragwrapper'+randID).css({'opacity':1}).animate({'left':(imgWidth/2)-($('#dragwrapper'+randID).width()/2)+'px'},o.introDuration,function(){$('#dragwrapper'+randID).animate({'opacity':.25},1000)});
					// The callback function at the end of the last line is there because Chrome seems to forget that the divs have overlay  hidden applied earlier
					$('div:eq(2)', obj).width(imgWidth).animate({'width':imgWidth/2+'px'},o.introDuration,function(){$('div:eq(2)', obj).css('overflow','hidden');clickit();});
				},o.introDelay);
			}
			else
			{
				clickit();
			}

			function clickit()
			{
				$(obj).hover(function(){
						$('#lt-arrow'+randID).stop().css({'z-index':'20','position':'absolute','top':imgHeight/2-$('#lt-arrow'+randID).height()/2+'px','left':parseInt($('#dragwrapper'+randID).css('left'))-10+'px'}).animate({opacity:1,left:parseInt($('#lt-arrow'+randID).css('left'))-6+'px'},500);
						$('#rt-arrow'+randID).stop().css({'position':'absolute','top':imgHeight/2-$('#lt-arrow'+randID).height()/2+'px','left':parseInt($('#dragwrapper'+randID).css('left'))+10+'px'}).animate({opacity:1,left:parseInt($('#rt-arrow'+randID).css('left'))+6+'px'},500);
						$('#dragwrapper'+randID).animate({'opacity':1},500);
					},function(){
						$('#lt-arrow'+randID).animate({opacity:0,left:parseInt($('#lt-arrow'+randID).css('left'))-6+'px'},500);
						$('#rt-arrow'+randID).animate({opacity:0,left:parseInt($('#rt-arrow'+randID).css('left'))+6+'px'},500);
						$('#dragwrapper'+randID).animate({'opacity':.25},500);
					}
				);

				// When clicking in the container, move the bar and imageholder divs
				$(obj).click(function(e){
					
					var clickX = e.pageX - this.offsetLeft;
					var img2Width = imgWidth-clickX;
					$('#dragwrapper'+randID).stop().animate({'left':clickX-($('#dragwrapper'+randID).width()/2)+'px'},600);
					$('div:eq(2)', obj).stop().animate({'width':clickX+'px'},600,function(){$('div:eq(2)', obj).css('overflow','hidden');}); // webkit fix for forgotten overflow
					$('#lt-arrow'+randID).stop().animate({opacity:0},50);
					$('#rt-arrow'+randID).stop().animate({opacity:0},50);
				});
				$(obj).one('mousemove', function(){$('#dragwrapper'+randID).stop().animate({'opacity':1},500);}); // If the mouse is over the container and we animate the intro, we run this to change the opacity since the hover event doesnt get triggered yet
			}
  		});
    	}
	});
})(jQuery);

/**
 * PrimeFaces ImageCompare Widget 
 */
 PrimeFaces.widget.ImageCompare = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.showFullLinks = false;
    
        this.jq.beforeAfter(this.cfg);
    }
    
});