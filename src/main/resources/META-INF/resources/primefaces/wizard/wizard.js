PrimeFaces.widget.Wizard = function(clientId, cfg) {
	this.id = clientId;
	this.cfg = cfg;
	this.cfg.back = '';
	this.cfg.next = '';
	this.formId = PrimeFaces.escapeClientId(this.cfg.formId);
	this.setupEffect();

	jQuery(this.formId).formwizard(this.cfg,{},{});
	
	if(this.cfg.step == undefined) {
		this.hideBackNav();
	} else {
		this.show(this.cfg.step);
		if(this.getStepIndex(this.cfg.step) == this.getStepLength() - 1)
			this.hideNextNav();
	}
}

PrimeFaces.widget.Wizard.prototype.back = function() {
	var currentStepIndex = this.getStepIndex(this.getState().currentStep),
	stepIndexToGo = currentStepIndex - 1;
	stepIdToGo = this.cfg.steps[stepIndexToGo];
	
	this.loadStep(stepIdToGo, stepIndexToGo, true);
}

PrimeFaces.widget.Wizard.prototype.next = function() {
	var currentStepIndex = this.getStepIndex(this.getState().currentStep),
	stepIndexToGo = currentStepIndex + 1;
	stepIdToGo = this.cfg.steps[stepIndexToGo];
	
	this.loadStep(stepIdToGo, stepIndexToGo, false);
}

PrimeFaces.widget.Wizard.prototype.loadStep = function(stepId, stepIndex, isBack) {
	var requestParams = jQuery(this.formId).serialize(),
	scope = this,
	params = {};
	
	params[PrimeFaces.PARTIAL_SOURCE_PARAM] = this.id;
	params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
	params[PrimeFaces.PARTIAL_REQUEST_PARAM] = true;
	params[this.id + '_currentStepId'] = this.getState().currentStep;
	params[this.id + '_stepIdToGo'] = stepId;
	params[this.id + '_wizardRequest'] = true;
	
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

			PrimeFaces.ajax.AjaxUtils.updateState(state);
			
			if(success == 'true') {
				jQuery('#' + stepId).html(content);
				
				scope.show(stepId);
				
				if(isBack) {
					if(stepIndex == (scope.getStepLength() - 2))
						scope.showNextNav();
					else if(stepIndex == 0)
						scope.hideBackNav();
					
					if(scope.cfg.onback)
						scope.cfg.onback.call(scope);
				} else {
					if(stepIndex == (scope.getStepLength() - 1))
						scope.hideNextNav();
					else if(stepIndex == 1)
						scope.showBackNav();
					
					if(scope.cfg.onnext)
						scope.cfg.onnext.call(scope);
				}
			} else {
				jQuery('#' + scope.getState().currentStep).html(content);
			}
		}
	});
}

PrimeFaces.widget.Wizard.prototype.setupEffect = function() {
	if(this.cfg.effect && this.cfg.effect != 'fade') {
		if(this.cfg.effect == 'slide') {
			this.cfg.inAnimation = 'slideDown';
			this.cfg.outAnimation = 'slideUp';
		} else if(this.cfg.effect == 'both') {
			this.cfg.inAnimation = 'show';
			this.cfg.outAnimation = 'hide';
		} else {
			alert('Invalid effect type:' + this.cfg.effect + ' for wizard ' + this.id);
		}
	}
}

PrimeFaces.widget.Wizard.prototype.handleWizardResponse = function(responseXML) {
	return jQuery(this.formId).formwizard('state');
}

PrimeFaces.widget.Wizard.prototype.getState = function() {
	return jQuery(this.formId).formwizard('state');
}

PrimeFaces.widget.Wizard.prototype.show = function(step) {
	jQuery(this.formId).formwizard('show', step);
}

PrimeFaces.widget.Wizard.prototype.getStepIndex = function(step) {
	for(var i=0; i < this.cfg.steps.length; i++) {
		if(this.cfg.steps[i] == step)
			return i;
	}
	
	return -1;
}

PrimeFaces.widget.Wizard.prototype.getStepLength = function() {
	return this.cfg.steps.length;
}

PrimeFaces.widget.Wizard.prototype.showNextNav = function() {
	jQuery(PrimeFaces.escapeClientId(this.cfg.nextNav)).fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideNextNav = function() {
	jQuery(PrimeFaces.escapeClientId(this.cfg.nextNav)).fadeOut();
}

PrimeFaces.widget.Wizard.prototype.showBackNav = function() {
	jQuery(PrimeFaces.escapeClientId(this.cfg.backNav)).fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideBackNav = function() {
	jQuery(PrimeFaces.escapeClientId(this.cfg.backNav)).fadeOut();
}
