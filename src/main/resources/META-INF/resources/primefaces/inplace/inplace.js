PrimeFaces.widget.Inplace = function(clientId, options) {
	var jqId = PrimeFaces.escapeClientId(clientId),
	displayId = jqId + "_display",
	contentId = jqId + "_content";
	
	if(!options.disabled) { 
		jQuery(displayId).click(function(){
			if(options.effect == 'fade') {
				jQuery(displayId).fadeOut(options.effectSpeed, 
						function(){
							jQuery(contentId).fadeIn(options.effectSpeed);
						});
			} else if(options.effect == 'slide') {
					jQuery(displayId).slideUp(options.effectSpeed, 
						function(){
							jQuery(contentId).slideDown(options.effectSpeed);
					});
			} else if(options.effect == 'none') {
					jQuery(displayId).hide(); 
					jQuery(contentId).show();
			}
			
			});
	
		 	jQuery(displayId).mouseover(function(){
				jQuery(this).toggleClass("pf-inplace-highlight");
			}).mouseout(function(){
				jQuery(this).toggleClass("pf-inplace-highlight");		
			 });
	}
}