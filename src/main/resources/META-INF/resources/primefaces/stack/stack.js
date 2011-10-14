PrimeFaces.widget.Stack = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.jq = $(this.jqId);

	var openSpeed = cfg.openSpeed,
	closeSpeed = cfg.closeSpeed;
	
	$(this.jqId + '.ui-stack>img').toggle(function(){
		var vertical = 0;
		var horizontal = 0;
		var el=$(this);
		el.next().children().each(function(){
			$(this).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, openSpeed);
			vertical = vertical + 55;
			horizontal = (horizontal+.75)*2;
		});
		el.next().animate({top: '-50px', left: '10px'}, openSpeed).addClass('openStack')
		   .find('li a>img').animate({width: '50px', marginLeft: '9px'}, openSpeed);
		el.animate({paddingTop: '0'});
	}, function(){
		var el=$(this);
		el.next().removeClass('openStack').children('li').animate({top: '55px', left: '-10px'}, closeSpeed);
		el.next().find('li a>img').animate({width: '79px', marginLeft: '0'}, closeSpeed);
		el.animate({paddingTop: '35'});
	});

    if(this.cfg.expanded) {
        this.jq.children('img').click();
    }
    
    this.postConstruct();
}

PrimeFaces.extend(PrimeFaces.widget.Stack, PrimeFaces.widget.BaseWidget);