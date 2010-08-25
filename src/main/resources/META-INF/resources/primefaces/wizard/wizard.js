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
    if(this.cfg.onback) {
        this.cfg.onback.call(this);
    }

    var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) - 1];
	
    this.loadStep(stepToGo, true);
}

PrimeFaces.widget.Wizard.prototype.next = function() {
    if(this.cfg.onnext) {
        this.cfg.onnext.call(this);
    }

    var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) + 1];
	
    this.loadStep(stepToGo, false);
}

PrimeFaces.widget.Wizard.prototype.loadStep = function(stepToGo, isBack) {
    var _self = this;
    
    var params = {};
    params[this.id + '_wizardRequest'] = true;
    params[this.id + '_currentStep'] = this.currentStep;
    params[this.id + '_stepToGo'] = stepToGo;

    if(isBack) {
        params[this.id + '_backRequest'] = true;
    }

    PrimeFaces.ajax.AjaxRequest(
        this.cfg.url,
        {
            source:this.id,
            process:this.id,
            update:this.id,
            formId:this.cfg.formId,
            onsuccess: function(responseXML) {
                var xmlDoc = responseXML.documentElement,
                updates = xmlDoc.getElementsByTagName("update"),
                extensions = xmlDoc.getElementsByTagName("extension");

                //Retrieve validationFailed flag and currentStep id from callback params
                var args = {};
                for(var i=0; i < extensions.length; i++) {
                    var extension = extensions[i];

                    if(extension.getAttributeNode('primefacesCallbackParam')) {
                        var jsonObj = jQuery.parseJSON(extension.firstChild.data);

                        for(var paramName in jsonObj) {
                            if(paramName)
                                args[paramName] = jsonObj[paramName];
                        }
                    }
                }

                _self.currentStep = args.currentStep;


                for(i=0; i < updates.length; i++) {
                    var id = updates[i].attributes.getNamedItem("id").nodeValue,
                    content = updates[i].firstChild.data;

                    if(id == _self.id){
                        if(!args.validationFailed) {
                            //update content
                            if(_self.cfg.effect) {
                                var _content = content;
                                jQuery(_self.content).fadeOut(_self.cfg.effectSpeed, function() {
                                    jQuery(_self.content).html(_content);
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
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement(id, content, this.ajaxContext);
                    }
                }

                return false;
            },
            error: function() {
                alert('Error in loading dynamic tab content');
            }
        },
        params);
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
