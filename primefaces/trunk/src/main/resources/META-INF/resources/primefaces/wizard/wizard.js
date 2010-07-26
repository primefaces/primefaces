PrimeFaces.widget.Wizard = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	this.jqId = PrimeFaces.escapeClientId(this.id);
	this.content = this.jqId + '_content';
	this.backNav = this.jqId + '_back';
	this.nextNav = this.jqId + '_next';
	this.currentStep = this.cfg.initialStep;
	var currentStepIndex = this.getStepIndex(this.currentStep);

	/*
	 * Navigation controls
	 */
	jQuery(this.backNav).button({icons:{primary: 'ui-icon-arrowthick-1-w'}});
	jQuery(this.nextNav).button({icons:{primary: 'ui-icon-arrowthick-1-e'}});

	jQuery(this.backNav).mouseout(function() {jQuery(this).removeClass('ui-state-focus');});
	jQuery(this.nextNav).mouseout(function() {jQuery(this).removeClass('ui-state-focus');});
	
	if(currentStepIndex == 0)
		jQuery(this.backNav).hide();
	else if(currentStepIndex == this.cfg.steps.length - 1)
		jQuery(this.nextNav).hide();
}

PrimeFaces.widget.Wizard.prototype.back = function() {
	var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) - 1];
	
	this.loadStep(stepToGo, true);
}

PrimeFaces.widget.Wizard.prototype.next = function() {
	var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) + 1];
	
	this.loadStep(stepToGo, false);
}

PrimeFaces.widget.Wizard.prototype.loadStep = function(stepToGo, isBack) {
	var requestParams = jQuery(PrimeFaces.escapeClientId(this.cfg.formId)).serialize(),
	_self = this,
	params = {};
	
	params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.id;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
	params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
	params[this.id + '_wizardRequest'] = true;
	params[this.id + '_currentStep'] = this.currentStep;
	params[this.id + '_stepToGo'] = stepToGo;
	
	if(isBack) {
		params[this.id + '_backRequest'] = true;
	}
	
	requestParams = requestParams + PrimeFaces.ajax.AjaxUtils.serialize(params); 
		
	jQuery.ajax({
		url: this.cfg.url,
		type: "POST",
		cache: false,
		dataType: "xml",
		data: requestParams,
		success : function(data, status, xhr) {
			var xmlDoc = data.documentElement,
			content = xmlDoc.getElementsByTagName("content")[0].firstChild.data,
			state = xmlDoc.getElementsByTagName("state")[0].firstChild.data,
			success = xmlDoc.getElementsByTagName("success")[0].firstChild.data;
			_self.currentStep = xmlDoc.getElementsByTagName("current-step")[0].firstChild.data;

			PrimeFaces.ajax.AjaxUtils.updateState(state);
			
			if(success == 'true') {
				//update content
				if(_self.cfg.effect) {
					jQuery(_self.content).fadeOut(_self.cfg.effectSpeed, function() {
						jQuery(_self.content).html(content);
						jQuery(_self.content).fadeIn();
					});
				} else {
					jQuery(_self.content).html(content);
				}
				
				//update navigation controls
				var currentStepIndex = _self.getStepIndex(_self.currentStep);
				if(currentStepIndex == _self.cfg.steps.length - 1) {
					_self.hideNextNav();
					_self.showBackNav();
				} else if(currentStepIndex == 0) {
					_self.hideBackNav();
					_self.showNextNav();
				} else {
					_self.showBackNav();
					_self.showNextNav();
				}
				
			} else {
				//update content
				jQuery(_self.content).html(content);
			}
		}
	});
}

PrimeFaces.widget.Wizard.prototype.getStepIndex = function(step) {
	for(var i=0; i < this.cfg.steps.length; i++) {
		if(this.cfg.steps[i] == step)
			return i;
	}
	
	return -1;
}

PrimeFaces.widget.Wizard.prototype.showNextNav = function() {
	jQuery(this.nextNav).fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideNextNav = function() {
	jQuery(this.nextNav).fadeOut();
}

PrimeFaces.widget.Wizard.prototype.showBackNav = function() {
	jQuery(this.backNav).fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideBackNav = function() {
	jQuery(this.backNav).fadeOut();
}
