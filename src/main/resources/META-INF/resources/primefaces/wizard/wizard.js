if(PrimeFaces == undefined) var PrimeFaces = {};
if(PrimeFaces.widget == undefined) PrimeFaces.widget = {};

PrimeFaces.widget.Wizard = function(clientId, cfg) {
	this.tabSelector = PrimeFaces.escapeClientId(clientId) + " div.pf-wizard-tab";
	this.clientId = clientId;
	this.cfg = cfg;
	this.step = 0;
	this.navPrev = jQuery(PrimeFaces.escapeClientId(clientId) + "_prev");
	this.navNext = jQuery(PrimeFaces.escapeClientId(clientId) + "_next");
	
	jQuery(this.tabSelector).tabSwitch('create', this.cfg);
}

PrimeFaces.widget.Wizard.prototype.next = function() {
	if(this.getStep() != (this.cfg.size-1)) {
		this.move(1);
	}
}

PrimeFaces.widget.Wizard.prototype.previous = function() {
	if(this.getStep() != 0) {
		this.move(-1);
	}
}

PrimeFaces.widget.Wizard.prototype.move = function(stepFactor) {
	var wizard = this,
	stepToGo = this.getStep() + stepFactor,
	params = "ajaxSource=" + this.clientId;
	
	params = params + "&primefacesAjaxRequest=true";
	params = params + "&stepToGo=" + stepToGo;
	params = params + "&currentStep=" + this.getStep();
	params = params + "&" + jQuery(PrimeFaces.escapeClientId(this.cfg.formClientId)).serialize();
	
	jQuery.ajax({
		type: "POST",
		url: this.cfg.actionURL,
		data: params,
		dataType: "xml",
		success: function(responseXML) {
			var xmlDoc = responseXML.documentElement,
			wizardTab = xmlDoc.getElementsByTagName("wizardtab")[0],
			success = wizardTab.childNodes[0].firstChild.data,
			tabContent = wizardTab.childNodes[1].firstChild.data,
			state = xmlDoc.getElementsByTagName("state")[0].firstChild.data,
			tabToUpdate;
			
			if(success == "true") {
				tabToUpdate = PrimeFaces.escapeClientId(wizard.clientId) + "_tab" + stepToGo;
				jQuery(tabToUpdate).html(tabContent);
				
				wizard.cfg.step = stepFactor;
				jQuery(wizard.tabSelector).tabSwitch('moveStep', wizard.cfg);
				wizard.step = wizard.step + stepFactor;
				
				if(wizard.step == 0)
					wizard.navPrev.hide();
				else if(wizard.step == 1)
					wizard.navPrev.show();
				else if(wizard.step == (wizard.cfg.size - 1))
					wizard.navNext.hide();
				else if(wizard.step == (wizard.cfg.size - 2))
					wizard.navNext.show();
				
			} else {
				tabToUpdate = PrimeFaces.escapeClientId(wizard.clientId) + "_tab" + wizard.getStep();
				jQuery(tabToUpdate).html(tabContent);
			}
			
			//update state
			PrimeFaces.ajax.AjaxUtils.updateState(state);
		}
	});
}

PrimeFaces.widget.Wizard.prototype.getStep = function() {
	return this.step;
}

PrimeFaces.widget.Wizard.prototype.getSize = function() {
	return this.cfg.size;
}