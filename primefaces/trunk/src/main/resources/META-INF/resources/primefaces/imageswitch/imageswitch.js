/*
 * jQuery.ImageSwitch
 * Version: 1.0.2
 * http://www.hieu.co.uk/ImageSwitch/
 *
 * Copyright (c) 2009 Hieu Pham - http://www.hieu.co.uk
 * COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL)
 * http://www.opensource.org/licenses/cddl1.php
 *
 * Date: 13/03/2009
 * Revision: 50
 */
 //This function to preload the image before using it in the function
 var Debug = function(mess, line){
	if(!$("#Debug"))
	{
		$("body").append("<div id='Debug'></div>");
	}
	if(line){
		$("#Debug").html($("#Debug").html()+"<br/>"+mess);
	}
	else{
		$("#Debug").html($("#Debug").html()+mess);
	}
};
(function($){
	$.fn.ImageSwitch = function(Arguements, FuntionHandle) {
		var defaults = {
			Type: "FadeIn", // Type of effect to run the function
			NewImage: "", //The new image will be loaded in
			EffectOriginal: true,
			Speed: 1000, //Speed of the effect
			StartLeft: 50, //The position the effect start compare to the original position could be (-)
			StartTop: 0,
			StartOpacity: 0, //Which start opacity it would be
			EndLeft: -50, //The position the effect end compare to the original position could be (-)
			EndTop: 0,
			EndOpacity: 0, //Which start opacity it would be
			Direction: "RightLeft", //Which Way the image will be sroll
			Door1: "", //The image for the door 1
			Door2: "" //The image for the door 2
		};
		
		var Args = $.extend(defaults, Arguements);
		var Obj = this; // Just a way to reference to this obj in case we need to pass in another event handle
		//To specific which obj ID the effect will associate to
		var EffectID = 0;
		
		var EffectImageId;//The id of effect image layer : #GrpEffectImg + EffectID
		var EffectDivId1;//The id of effect div layer : #GrpEffectDiv2 + EffectID
		var EffectDivId2;//The id of effect div layer : #GrpEffectDiv2 + EffectID
		
        var EndFunction = function(){
			Obj.data("imageswitch", -1);
		};		
		if($.isFunction(FuntionHandle)){
		    EndFunction = function(){		
				FuntionHandle();	
				Obj.data("imageswitch", -1);
			};
		}
		//-----------------------------------------------------------------------------------------------------------
		//The original image will be fade out when the new image will fade in
		var FadeImage = function(){
			//Generate the effect map, move the effect map overlay the original map
			Obj.parent().append("<img class='GrpEffectImg' id='"+EffectImageId.replace("#","")+"'/>");
			$(EffectImageId).attr("src", Obj.attr("src"));
			$(EffectImageId).css("position", "absolute");
			$(EffectImageId).css("top", Obj.position().top);
			$(EffectImageId).css("left", Obj.position().left);
			$(EffectImageId).css("opacity", 1);
			
			//Change image of the original map
			Obj.attr("src", Args.NewImage);
			
			//Need something special when user want to keep no effect for the orignal
			if(Args.EffectOriginal)
			{
				//Set the start opacity, as the effect will fade out we set in start at 1, vice versa for the original
				Obj.css("opacity", Args.StartOpacity);		
				
				//Fade in the original image
				Obj.animate({"opacity":1}, Args.Speed);			
			}
			
			//Start effect animation
			$(EffectImageId).animate({"opacity":0}, Args.Speed, function(){
					//Remove the effect image when finish the effect
					$(EffectImageId).remove();
					EndFunction();
			});			
		};
		//-----------------------------------------------------------------------------------------------------------
		//The new image will fly from the startPosition with the StartOpacity
		
		var Fly = function(FlyIn){
			//Generate the effect map, move the effect map overlay the original map
			$("body").append("<img class='GrpEffectImg' id='"+EffectImageId.replace("#","")+"'/>");
			$(EffectImageId).css("position", "absolute");
			if(FlyIn){
				//As the new image will fly in, so we set the effect image src = new image
				$(EffectImageId).attr("src", Args.NewImage);			
				$(EffectImageId).css("top", Obj.offset().top + Args.StartTop);
				$(EffectImageId).css("left", Obj.offset().left + Args.StartLeft);
				$(EffectImageId).css("opacity", Args.StartOpacity);
				EndTop = Obj.offset().top;
				EndLeft = Obj.offset().left;
				//Change the opacity base on the input				
				EndOpacity = 1; 			
			}else{
				//As the old image will fly out, so we set the effect image src = new image
				//The effect image will be on top of the old image and hide the old image
				//So we could set the old image with the new src
				$(EffectImageId).attr("src", Obj.attr("src"));						
				Obj.attr("src", Args.NewImage);
				$(EffectImageId).css("top", Obj.offset().top);
				$(EffectImageId).css("left", Obj.offset().left);
				$(EffectImageId).css("opacity", 1);
				EndTop = Obj.offset().top + Args.EndTop;
				EndLeft = Obj.offset().left + Args.EndLeft;
				//Change the opacity base on the input				
				EndOpacity = Args.EndOpacity; 				
			}
			//Let the effect start fly in
			$(EffectImageId).animate({"opacity":EndOpacity, "top":EndTop, 
										"left": EndLeft}, Args.Speed,
				function(){
					Obj.attr("src", Args.NewImage);
					$(EffectImageId).remove();
					EndFunction();
			});
		};
		//-----------------------------------------------------------------------------------------------------------
		//The new image will scoll in and kick the old image out.
		//With the setting ScollIn = false, The original image will scroll out and reveal the new image
		var Scroll = function(ScrollIn){
			//Save the original status so we could set it in the end
			var backup = Obj.clone(true);		
			//Create a viewport for it
			Obj.wrap("<div id='GrpViewport-"+ EffectID+"'></div>");
			$("#GrpViewport-" + EffectID).css("overflow","hidden");
			$("#GrpViewport-" + EffectID).width(Obj.width());
			$("#GrpViewport-" + EffectID).height(Obj.height());								
			//Generate the effect map, move the effect map overlay the original map				
			$("#GrpViewport-" + EffectID).append("<img class='GrpEffectImg' id='"+EffectImageId.replace("#","")+"'/>");
			$(EffectImageId).css("position", "absolute");
			//Find where the Effect Image start
			var StartTop = 0;
			var StartLeft = 0;				
			switch(Args.Direction){
				case "RightLeft":	StartLeft = -Obj.width();	break;
				case "LeftRight":	StartLeft = Obj.width();	break;
				case "TopDown":		StartTop = -Obj.height();	break;
				case "DownTop":		StartTop = Obj.height();	break;
			}
			//In scroll in using the Start position, else, Set it to 0 so it could scroll out
			//Also need o set the destination of the animate different
			if(ScrollIn){
				$(EffectImageId).attr("src", Args.NewImage);
				$(EffectImageId).css("top", StartTop);
				$(EffectImageId).css("left", StartLeft);
				$(EffectImageId).css("opacity", Args.StartOpacity);
				EndTop = 0;
				EndLeft = 0;
				//Don't change the opacity if it scroll in
				EndOpacity = 1; 
			}else{
				$(EffectImageId).attr("src", Obj.attr("src"));
				$(EffectImageId).css("left", 0);
				$(EffectImageId).css("top", 0);
				Obj.attr("src", Args.NewImage);
				EndTop = StartTop;
				EndLeft = StartLeft;
				//Change the opacity base on the input				
				EndOpacity = Args.EndOpacity; 
			}
			//We need to treat absolute position different					
			//In some case there're text arround the image, it could be a bit mess up
			if(Obj.css("position")!="absolute")
			{
				$("#GrpViewport-" + EffectID).css("position","relative");					
				Obj.css("position","absolute");
			}
			else
			{			
				$("#GrpViewport-" + EffectID).css("position","absolute");
				$("#GrpViewport-" + EffectID).css("left",Obj.css("left"));
				$("#GrpViewport-" + EffectID).css("top",Obj.css("top"));		
				Obj.css("top",0);
				Obj.css("left",0);
			}
			//if effect the original image, then move it as well
			if(Args.EffectOriginal && ScrollIn)
			{			
				//Move the original image along
				Obj.animate({"top": - StartTop,
							"left": - StartLeft}, Args.Speed);									
			}			
			//Start the effect
			$(EffectImageId).animate({"opacity":EndOpacity,"top":EndTop,"left":EndLeft}, Args.Speed, 
					function(){
						//Finish the effect, and replace the viewport with this area
						backup.attr("src",Args.NewImage);
						//Also remove the Attr for imageswitch
						backup.removeAttr("imageswitch");
						backup.data("imageswitch", -1);
						$("#GrpViewport-" + EffectID).replaceWith(backup);
						EndFunction();
				});	
		};
		//-----------------------------------------------------------------------------------------------------------
		//A door come out create an effect door close.then open the new image
		var SingleDoor = function(){
			//Save the original status so we could set it in the end
			var backup = Obj.clone(true);
			//Create a viewport for it
			Obj.wrap("<div id='GrpViewport'></div>");
			$("#GrpViewport").css("overflow","hidden");
			$("#GrpViewport").width(Obj.width());
			$("#GrpViewport").height(Obj.height());								
			//Generate the effect map, move the effect map overlay the original map				
			$("#GrpViewport").append("<div class='GrpEffectDiv' id='"+EffectDivId1.replace("#","")+"'/>");
			$(EffectDivId1).attr("src", Args.NewImage);
			$(EffectDivId1).css("position", "absolute");
			$(EffectDivId1).css("background-color", "#FFF");
            if(Args.Door1.length>0){
			    $(EffectDivId1).css("background", Args.Door1);
			}
			$(EffectDivId1).width(Obj.width());
			$(EffectDivId1).height(Obj.height());								
			//Find where the Effect Image start
			var StartTop = 0;
			var StartLeft = 0;				
			switch(Args.Direction){
				case "RightLeft":	StartLeft = -Obj.width();	break;
				case "LeftRight":	StartLeft = Obj.width();	break;
				case "TopDown":		StartTop = -Obj.height();	break;
				case "DownTop":		StartTop = Obj.height();	break;
			}				
			$(EffectDivId1).css("top", StartTop);
			$(EffectDivId1).css("left", StartLeft);	
			
			//We need to treat absolute position different	
			if(Obj.css("position")!="absolute")
			{
				$("#GrpViewport").css("position","relative");					
				Obj.css("position","absolute");			
			}
			else
			{
				$("#GrpViewport").css("position","absolute");
				$("#GrpViewport").css("left",Obj.css("left"));
				$("#GrpViewport").css("top",Obj.css("top"));		
				Obj.css("top",0);
				Obj.css("left",0);
			}
			//Start Close the Door
			$(EffectDivId1).animate({"top":0,"left":0}, Args.Speed, function(){
				//Finish the first effect change the image and open the door
				Obj.attr("src", Args.NewImage);
				//Start open the door
				$(EffectDivId1).animate({"top":StartTop,"left":StartLeft}, Args.Speed, function(){
					//Reset style
					backup.attr("src",Args.NewImage);
					//Also remove the Attr for imageswitch
					backup.removeAttr("imageswitch");
					backup.data("imageswitch", -1);
					$("#GrpViewport").replaceWith(backup);
					EndFunction();
				});
			});	
		};
		//-----------------------------------------------------------------------------------------------------------
		//Same with single door but with this effect, there will be 2 door
		var DoubleDoor = function(){
			//Save the original status so we could set it in the end
			var orgPosition = Obj.css("position");
			var orgLeft = Obj.css("left");
			var orgTop = Obj.css("top");
			//Create a viewport for it
			Obj.wrap("<div id='GrpViewport'></div>");
			$("#GrpViewport").css("overflow","hidden");
			$("#GrpViewport").width(Obj.width());
			$("#GrpViewport").height(Obj.height());								
			//Generate the effect map, move the effect map overlay the original map				
			$("#GrpViewport").append("<div class='GrpEffectDiv' id='"+EffectDivId1.replace("#","")+"'/>");
			$(EffectDivId1).css("position", "absolute");
			$(EffectDivId1).css("background-color", "#FFF");
			if(Args.Door1.length>0){
                $(EffectDivId1).css("background", Args.Door1);			
			}
			$(EffectDivId1).width(Obj.width());
			$(EffectDivId1).height(Obj.height());								
			//We need the second door
			$("#GrpViewport").append("<div class='GrpEffectDiv1' id='"+EffectDivId2.replace("#","")+"'/>");
			$(EffectDivId2).css("position", "absolute");
			$(EffectDivId2).css("background-color", "#FFF");
			if(Args.Door2.length>0){
                $(EffectDivId2).css("background", Args.Door2);				
			}
			$(EffectDivId2).width(Obj.width());
			$(EffectDivId2).height(Obj.height());								
			
			//Find where the Effect Image start
			var StartTop = 0;
			var StartLeft = 0;				
			switch(Args.Direction){
				case "RightLeft":	StartLeft = -Obj.width();	break;
				case "LeftRight":	StartLeft = Obj.width();	break;
				case "TopDown":		StartTop = -Obj.height();	break;
				case "DownTop":		StartTop = Obj.height();	break;
			}				
			$(EffectDivId1).css("top", StartTop);
			$(EffectDivId1).css("left", StartLeft);	
			$(EffectDivId2).css("top", -StartTop);
			$(EffectDivId2).css("left", -StartLeft);	
			
			//set the background for the door effect so it look different
			if(!Args.EffectOriginal){
				$(EffectDivId1).css("background","#FFF url("+Args.NewImage+") no-repeat "+ -StartLeft/2 +"px "+ -StartTop/2+"px");
				$(EffectDivId2).css("background","#FFF url("+Args.NewImage+") no-repeat "+ StartLeft/2+"px "+ StartTop/2 +"px");
			}			
			
			//We need to treat absolute position different					
			if(Obj.css("position")!="absolute")
			{
				$("#GrpViewport").css("position","relative");					
				Obj.css("position","absolute");			
			}
			else
			{
				$("#GrpViewport").css("position","absolute");
				$("#GrpViewport").css("left",orgLeft);
				$("#GrpViewport").css("top",orgTop);
				Obj.css("position","absolute");			
				Obj.css("top",0);
				Obj.css("left",0);
			}
			//Start Close the Door
			$(EffectDivId1).animate({"top":StartTop/2,"left":StartLeft/2}, Args.Speed, function(){
				//Finish the first effect change the image and open the door
				Obj.attr("src", Args.NewImage);
				//If EffectOriginal isn't on mean two door stick into the new image, then stop here. Else carry on
				if(!Args.EffectOriginal){
					Obj.css("position", orgPosition);
					Obj.css("top", orgTop);
					Obj.css("left", orgLeft);				
					$("#GrpViewport").replaceWith(Obj);
				}else{
					//Start open the door
					$(EffectDivId1).animate({"top":StartTop,"left":StartLeft}, Args.Speed, function(){
						//Reset style
						Obj.css("position", orgPosition);
						Obj.css("top", orgTop);
						Obj.css("left", orgLeft);
						$("#GrpViewport").replaceWith(Obj);
					});
				}
			});	
			$(EffectDivId2).animate({"top":-StartTop/2,"left":-StartLeft/2}, Args.Speed, function(){
				//Finish the first effect change the image and open the door
				Obj.attr("src", Args.NewImage);
				//If EffectOriginal isn't on mean two door stick into the new image, then stop here. Else carry on
				if(!Args.EffectOriginal){
					EndFunction();
				}else{
					//Start open the door
					$(EffectDivId2).animate({"top":-StartTop,"left":-StartLeft}, Args.Speed, function(){
						//Run the end effect
						EndFunction();
					});
				}
			});					
		};
		//-----------------------------------------------------------------------------------------------------------
		//The new image will flip from the back of the old one to the top
		//If FlipIn is false, then the old image will flip to the back reveal the new one
		var Flip = function(FlipIn){
		    var backup = Obj.clone(true);	
			if(Obj.css("z-index") == 'auto')	{
				Obj.css("z-index", 100);
			}
			//if (position different then absolute and relative then it should be relative)
			if(Obj.css("position") != "absolute"){
				Obj.css("position", "relative");
			}
			//Generate the effect map, move the effect map overlay the original map
			$("body").append("<img class='GrpEffectImg'  id='"+EffectImageId.replace("#","")+"'/>");
			$(EffectImageId).css("position", "absolute");
			$(EffectImageId).css("top", Obj.offset().top);
			$(EffectImageId).css("left", Obj.offset().left);
			
			if(FlipIn){
				$(EffectImageId).css("opacity", Args.StartOpacity);
				//So this layer will be under the original image
				$(EffectImageId).css("z-index", Obj.css("z-index")-1);
				$(EffectImageId).attr("src", Args.NewImage);
			}else{
				$(EffectImageId).css("opacity", 1);
				//This layer will be on top the original image
				$(EffectImageId).css("z-index", Obj.css("z-index")+1);			
				//Turn in to the fake old image
				$(EffectImageId).attr("src", Obj.attr("src"));
				Obj.attr("src", Args.NewImage);
				
			}
			
			//Find where the effect layer stop
			if(Math.abs(Args.EndTop)<Obj.height() && Math.abs(Args.EndLeft)<Obj.width()){
				EndTop = Obj.offset().top;
				EndLeft = Obj.offset().left + Obj.width();
			}else{
				EndTop = Obj.offset().top + Args.EndTop;
				EndLeft = Obj.offset().left + Args.EndLeft;				
			}
			EndOpacity = 1; 
			
			//Let the effect start, 
			$(EffectImageId).animate({"opacity":EndOpacity, "top":EndTop, 
										"left": EndLeft}, Args.Speed,
				function(){
					//Now the effect image is out, move it back again
					if(FlipIn) {
						$(EffectImageId).css("z-index", 101);
					}else{
						EndOpacity = Args.EndOpacity;
						$(EffectImageId).css("z-index", 2);						
					}
					$(EffectImageId).animate({"opacity":EndOpacity, "top":Obj.offset().top, 
												"left": Obj.offset().left}, Args.Speed,
						function(){
							//Restore the image to the original
							backup.attr("src", Args.NewImage);
							//Also remove the Attr for imageswitch
							backup.removeAttr("imageswitch");	
							backup.data("imageswitch", -1);
							Obj.replaceWith(backup);
							$(EffectImageId).remove();
							EndFunction();						
						});
			});
		};
		
		return this.each(function(){
			Obj = $(this);		
			if(!Obj.ImageAnimating())
			{
				EffectID = Obj.attr('id').replace(':', '_');
				
				//Mark the effect is running				
				Obj.data("imageswitch", 1);
				EffectImageId = "#GrpEffectImg-" + EffectID;//The id of effect image layer : #GrpEffectImg- + EffectID
				EffectDivId1 = "#GrpEffectDiv1-" + EffectID;//The id of effect div layer : #GrpEffectDiv1- + EffectID
				EffectDivId2 = "#GrpEffectDiv2-" + EffectID;//The id of effect div layer : #GrpEffectDiv2- + EffectID
				
				var TempImg = new Image();
				TempImg.src = Args.NewImage;
				$.ImagePreload(Args.NewImage,function(){
					switch(Args.Type){
						case "FadeIn":		FadeImage();	break;
						case "FlyIn": 		Fly(true);		break;
						case "FlyOut":		Fly(false);		break;
						case "FlipIn": 		Flip(true);		break;
						case "FlipOut":		Flip(false);	break;				
						case "ScrollIn":	Scroll(true);	break;
						case "ScrollOut":	Scroll(false);	break;
						case "SingleDoor":	SingleDoor();	break;
						case "DoubleDoor":	DoubleDoor();	break;
					}
				});
			}
		});	
	};	
})(jQuery);

//Check if a IS effect is running
(function($){
	$.fn.ImageAnimating = function(){
		if(this.data("imageswitch")>0){
			return true;
		}else{
			return false;
		}
	};
})(jQuery);
//Stop a specific the IS effect if it's running
(function($){
	$.fn.ImageStop = function(clearQueue, gotoEnd, EndFunction){
		return this.each(function(){
			if($(this).ImageAnimating()){
				var EffectID = $.data(this,"imageswitch");
				$("#GrpEffectImg-"+EffectID).stop(clearQueue, gotoEnd);
				$("#GrpEffectDiv-"+EffectID).stop(clearQueue, gotoEnd);
				$("#GrpEffectDiv1-"+EffectID).stop(clearQueue, gotoEnd);
				$(this).stop(clearQueue, gotoEnd);
				$("#GrpEffectImg-"+EffectID).remove();
				$("#GrpEffectDiv-"+EffectID).remove();
				$("#GrpEffectDiv1-"+EffectID).remove();
				if($.isFunction(EndFunction)){
					EndFunction();
				}
			}
		});
	};
})(jQuery);
//Stop all the IS effect running
(function($){
	$.ImageStopAll = function(clearQueue, gotoEnd, EndFunction){
		$(".GrpEffectImg").stop(clearQueue, gotoEnd);
		$(".GrpEffectDiv").stop(clearQueue, gotoEnd);
		$(".GrpEffectDiv1").stop(clearQueue, gotoEnd);
		$(this).stop(clearQueue, gotoEnd);
		$(".GrpEffectImg").remove();
		$(".GrpEffectDiv").remove();
		$(".GrpEffectDiv1").remove();
		$.data(this, "imageswitch", -1);
		if($.isFunction(EndFunction)){
			EndFunction();
		}
	};
})(jQuery);
//Preload a specific image
(function($){
	$.ImagePreload = function(FileName, EndFunction){
		var TempImage = new Image();
		TempImage.src = FileName;
		if($.isFunction(EndFunction)){
			$(TempImage).load(EndFunction());
		}
	};
})(jQuery);

PrimeFaces.widget.ImageSwitch = function(clientId, cfg, images) {
	this.imgClientId = PrimeFaces.escapeClientId(clientId);
	this.cfg = cfg;
	this.images = images;
	this.imgIdx = 0;

	for(var i=0; i<images.length;i++) {
		jQuery.ImagePreload(images[i]);
	}
	
	if(this.cfg.slideshowAuto)
		this.startSlideshow();
}

PrimeFaces.widget.ImageSwitch.prototype.switchImage = function() { 
	jQuery(this.imgClientId).ImageSwitch(
			{
				Type: this.cfg.effect,  
				NewImage:this.images[this.imgIdx], 
				Speed:this.cfg.speed  
			});
}

PrimeFaces.widget.ImageSwitch.prototype.startSlideshow = function() {
	var imageSwitch = this;
	this.animation = setInterval(
							function(){
								imageSwitch.next();
							}, this.cfg.slideshowSpeed);
}

PrimeFaces.widget.ImageSwitch.prototype.stopSlideshow = function() {
	clearInterval(this.animation); 
}

PrimeFaces.widget.ImageSwitch.prototype.next = function() {
	if(this.imgIdx == (this.images.length - 1))
		this.imgIdx = 0;
	else
		this.imgIdx++;
	
	this.switchImage();
}

PrimeFaces.widget.ImageSwitch.prototype.previous = function() {
	if(this.imgIdx == 0)
		this.imgIdx = this.images.length - 1;
	else
		this.imgIdx--;
	
	this.switchImage();
}