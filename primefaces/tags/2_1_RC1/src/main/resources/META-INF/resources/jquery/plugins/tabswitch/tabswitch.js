/*
 * .tabSwitch
 * Version: 1.0
 * http://www.hieu.co.uk/blog/index.php/tabswitch/
 *
 * Copyright (c) 2009 Hieu Pham - http://www.hieu.co.uk
 * COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL)
 * http://www.opensource.org/licenses/cddl1.php
 *
 * Date: 14/04/2009
 */
(function($){
	$.fn.tabSwitch = function(command, Arguements, EndFunction) {
		//Default value should be set here
		var defaults = {
			type	:	"slide", //Type of effect
			cols	:	2, //This only used when you're using type = table
			toggle	:	"fade", //This specific which type of toggle effect
			ease	: 	40, 
			easeType:	"linear", //This isn't work for this version yet
			loopback:	1, //If it's 1 it will loop when it reach the ends
			width	: 	400, // Size of the viewport
			height	: 	400,
			index	: 	0, //The current tab index
			speed	: 	500, //Speed of the animation
			interval:	5000, //The interval of auto-animate
			step 	: 	1, //How many step you want to use in moveStep
			wrapperClass : "", //You could add extra class for the wraper
			viewportClass : "" //You could add extra class for the viewport
		};

		var Args = $.extend(defaults, Arguements);
		var Obj = this; 
		//For quicker access
		var jFirstObj = Obj.eq(1);
		var DOMFirstObj = Obj.eq(1).get(0);	
		if(!$.isFunction(EndFunction)){
			//Set the index in the cache
			var Callback = function(){
				$.data(DOMFirstObj, "index", Args.index);
			};
		}
		else{
			var Callback = function(){
				$.data(DOMFirstObj, "index", Args.index);
				EndFunction();
			};
		}
		//Back up orginal information
		StoreToCache = function(){
			//Now store the tab type in the cache for further use
			$.data(DOMFirstObj, "type", Args.type);
			$.data(DOMFirstObj, "toggle", Args.toggle);
			$.data(DOMFirstObj, "cols", Args.cols);
			$.data(DOMFirstObj, "ease", Args.ease);
			$.data(DOMFirstObj, "easeType", Args.easeType);
			$.data(DOMFirstObj, "index", Args.index);
			$.data(DOMFirstObj, "loopback", Args.loopback);
			$.data(DOMFirstObj, "clientId", Args.clientId);
			//Before do anything to the object, keep a backup so we could revert it
			if(jFirstObj.attr('style')){
				$.data(DOMFirstObj, "orgAttr", jFirstObj.attr('style'));
			}else{
				$.data(DOMFirstObj, "orgAttr", "");
			}
		}
		//Remove all the data in cache and reset the object back to original
		backFromCache = function(){
			Obj.attr('style',$.data(DOMFirstObj, "orgAttr"));
			var ViewPortObj = $("#ViewPort"+$.data(DOMFirstObj, "clientId"));
			ViewPortObj.replaceWith(Obj);
			//Remove auto if it's running
			stopAuto();
			//Clear cacke
			$.removeData(DOMFirstObj);
		}
		//Execute when input comment is create
		var createTab = function(){
			//Back up orginal information
			StoreToCache();	
			//Construct the form
			//Set all the CSS for the list div, this;s the common setting for all type of tab
			if(Args.width)	Obj.width(Args.width);
			if(Args.height)	Obj.height(Args.height);			
			//A big wraper around and change some CSS of the wrap
			var WraperSelector = "WideDiv" + $.data(DOMFirstObj, "clientId");
			Obj.wrapAll("<div id='"+WraperSelector +"'></div>");
			var WraperObj = $("#"+WraperSelector);
			WraperObj.addClass(Args.wraperClass);
			WraperObj.css({"position":"relative"});
			WraperObj.wrap("<div id='" +WraperSelector.replace("WideDiv","ViewPort") + "'></div>")
			//Now create the viewport with the input size
			var ViewPortObj = $("#" + WraperSelector.replace("WideDiv","ViewPort"));
			ViewPortObj.width(Args.width);
			ViewPortObj.height(Args.height);
			ViewPortObj.css({"display":"block","overflow":"hidden","position":"relative"});					
			ViewPortObj.addClass(Args.viewportClass);
			switch(Args.type)
			{
				case "slide":
					Obj.css({"float":"left"});
					//A big wraper around and change some CSS of the wrap
					WraperObj.width((Args.width+2) * Obj.length);
					//Now create the viewport with the input size
					break;
				case "scroll":
					//A big wraper around and change some CSS of the wrap				
					WraperObj.width(Args.width);
					WraperObj.height((Args.height+2) * Obj.length);
					break;
				case "toggle":
					WraperObj.width(Args.width);
					WraperObj.height(Args.height);
					Obj.css({"position":"absolute", "left": "-999px"});
					Obj.eq(Args.index).css({"left":"0px", "top":"0px"});
					Obj.eq(Args.index).css("opacity", 1);
					break;
				case "table":
					WraperObj.width(Args.width*Args.cols);
					Obj.css("float", "left");
			}
			moveTo();
		}
		// Move object to a position set by Args.Index
		var moveTo = function(){
			//get the easeLevel from the cache
			var ease = 0;
			//Check if the next idx is out of the limit or not
			if(!Args.easeType){
				Args.easeType = (DOMFirstObj, "easeType");
			}
			if(Args.index > Obj.length -1 ){
				if($.data(DOMFirstObj, "loopback")!=0){
					Args.index = 0;
					ease = -$.data(DOMFirstObj, "ease");
				}
				else return;
			}
			if(Args.index < 0 ){
				if($.data(DOMFirstObj, "loopback")!=0){					
					Args.index = Obj.length-1;
					ease = $.data(DOMFirstObj, "ease");
				} else return;
			}	
			var WraperSelector = "WideDiv" + $.data(DOMFirstObj, "clientId");				
			var WraperObj = $("#"+WraperSelector);
			//See what type of effect we stimulate
			switch($.data(DOMFirstObj, "type")){
				case 'slide':
					//Get how much ease we set and start the animation
					if(ease!=0){
						var easeLevel = (parseInt(WraperObj.css("left").replace("px",""))+ease);
						WraperObj.animate({left: easeLevel+"px"}, Args.speed, function(){
							WraperObj.animate({left:-(Obj.outerWidth(true) * Args.index)+"px"}, Args.speed, Args.easeType, Callback());			
						});
					}else{
						WraperObj.animate({left:-(Obj.outerWidth(true) * Args.index)+"px"}, Args.speed, Args.easeType, Callback());			
					}				
				break;
				case 'scroll':
					//Get how much ease we set and start the animation
					if(ease!=0){
						var easeLevel = (parseInt(WraperObj.css("top").replace("px",""))+ease);
						WraperObj.animate({top: easeLevel+"px"}, Args.speed, function(){
							WraperObj.animate({top:-(Obj.outerHeight(true) * Args.index)+"px"}, Args.speed, Args.easeType, Callback());			
						});
					}else{
						WraperObj.animate({top:-(Obj.outerHeight(true) * Args.index)+"px"}, Args.speed, Args.easeType, Callback());			
					}
				break;
				case 'toggle':
					//move the new one on top of the old div
					Obj.eq(Args.index).css({"left":"0px", "top":"0px"});
					switch($.data(DOMFirstObj, "toggle"))
					{
						case "fade":
							Obj.eq(Args.index).css({"opacity":0});												
							Obj.eq(Args.index).animate({"opacity":1},Args.speed);
							if($.data(DOMFirstObj, "index")!=Args.index){
								Obj.eq($.data(DOMFirstObj, "index")).animate({"opacity":0},Args.speed, function(){
									$(this).css("left",-999);
									Callback();
								});
							}
							break;
						case "toggle":
							if($.data(DOMFirstObj, "index")!=Args.index){							
								Obj.eq(Args.index).css({"display":"none"});
								Obj.eq($.data(DOMFirstObj, "index")).slideUp(Args.speed,  function(){
									Obj.eq(Args.index).slideDown(Args.speed, function(){Callback();});							
									$(this).css("left",-999);													
								});
							}
							break;
						case "show":
							if($.data(DOMFirstObj, "index")!=Args.index){										
								Obj.eq(Args.index).css({"display":"none"});
								Obj.eq($.data(DOMFirstObj, "index")).hide(Args.speed, function(){
									Obj.eq(Args.index).show(Args.speed,function(){Callback();});							
									$(this).css("left",-999);													
								});
							}
							break;
						case "noeffect":
							if($.data(DOMFirstObj, "index")!=Args.index){				
								Obj.eq($.data(DOMFirstObj, "index")).css("left", -999);
								Callback();
							}
							break;
					}
					break;
				case "table":
					var cols = $.data(DOMFirstObj, "cols");
					//Where the next idx in the table
					var nextX = -(Args.index % cols) * Obj.width();
					var nextY = -Math.floor(Args.index / cols) * Obj.height();
					//Move horizontal first
					WraperObj.animate({"left": nextX}, Args.speed, Args.easeType, function(){
						WraperObj.animate({"top": nextY}, Args.speed, Args.easeType, Callback());
					});
					break;
				default:
					$('html,body').animate({"scrollTop":Obj.eq(Args.index).offset().top},Args.speed);
				break;
				
			}
		};
		
		//Move by steps
		moveStep = function(){
			var currentIdx = $.data(DOMFirstObj, "index");
			//Calculate the next index
			Args.index = parseInt(currentIdx) + parseInt(Args.step);
			//Then move to it
			moveTo();
		}
		//Set it run auto
		startAuto = function(){
			//Save the autoswitch into memory and start it
			$.data(DOMFirstObj, "AutoSwitch", setInterval(moveStep, Args.interval));
		}
		//Stop the auto
		stopAuto = function(){
			//Stop the interval and clear the cache
			clearInterval($.data(DOMFirstObj, "AutoSwitch"));
			$.removeData(DOMFirstObj, "AutoSwitch");
		}
		//Toggle auto
		toggleAuto = function(){
			if(isAuto()){
				stopAuto();
			}else{
				startAuto();
			}
		}
		//Return if this is auto or not
		isAuto = function(){
			if($.data(DOMFirstObj, "AutoSwitch")){
				return true;
			}else{
				return false;
			}		
		}
		if(!command) command = "";
		//Check what user want
		switch(command.toLowerCase()){
			case "index": 
				if($.data(DOMFirstObj, "index")){
					return $.data(DOMFirstObj, "index");
				}else{
					return 0;
				}
				break;
			case "moveto":
				moveTo();
				break;
			case "movestep":
				moveStep();
				break;
			case "destroy":
				backFromCache();
				break;
			case "create":
				createTab();
				break;
			case "isauto":
				return isAuto();
				break;
			case "toggleauto":
				toggleAuto();
				break;
			case "startauto":
				startAuto();
				break;
			case "stopauto":
				stopAuto();
				break;
		}
	};
})(jQuery);