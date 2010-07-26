PrimeFaces.widget.Stack = function(id, cfg) {
	var elId = PrimeFaces.escapeClientId(id),
	openSpeed = cfg.openSpeed,
	closeSpeed = cfg.closeSpeed;
	
	jQuery(elId + '.pf-stack>img').toggle(function(){
		var vertical = 0;
		var horizontal = 0;
		var el=jQuery(this);
		el.next().children().each(function(){
			jQuery(this).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, openSpeed);
			vertical = vertical + 55;
			horizontal = (horizontal+.75)*2;
		});
		el.next().animate({top: '-50px', left: '10px'}, openSpeed).addClass('openStack')
		   .find('li a>img').animate({width: '50px', marginLeft: '9px'}, openSpeed);
		el.animate({paddingTop: '0'});
	}, function(){
		var el=jQuery(this);
		el.next().removeClass('openStack').children('li').animate({top: '55px', left: '-10px'}, closeSpeed);
		el.next().find('li a>img').animate({width: '79px', marginLeft: '0'}, closeSpeed);
		el.animate({paddingTop: '35'});
	});	
}
