PrimeFaces.widget.Password = function(id, cfg) {
	cfg.lang = {
		length : "",
		number: "",
		uppercase: "",
		lowercase: "",
		punctuation: "",
		special: ""
	};
	
	if(cfg.promptLabel != undefined) cfg.lang.please = cfg.promptLabel;
	if(cfg.weakLabel != undefined) cfg.lang.low = cfg.weakLabel;
	if(cfg.goodLabel != undefined) cfg.lang.correct = cfg.goodLabel;
	if(cfg.strongLabel != undefined) cfg.lang.high = cfg.strongLabel;
	
	if(cfg.flat) {
		if(cfg.onShow == undefined)
			cfg.onShow = function(input, container){ container.slideDown(); };
		if(cfg.onHide == undefined)
			cfg.onHide = function(input, container){ container.slideUp(); };
		
		cfg.onComplete = function(input, container){ container.hide(); };
	}
	
	jQuery(PrimeFaces.escapeClientId(id)).jpassword(cfg);
}