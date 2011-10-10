PrimeFaces.widget.Wizard = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = PrimeFaces.escapeClientId(this.id);
    this.content = this.jqId + '_content';
    this.backNav = this.jqId + '_back';
    this.nextNav = this.jqId + '_next';
    this.cfg.formId = $(this.jqId).parents('form:first').attr('id');
    
    this.currentStep = this.cfg.initialStep;
    var currentStepIndex = this.getStepIndex(this.currentStep);

    //Step controls
    if(this.cfg.showStepStatus) {
        this.stepControls = $(this.jqId + ' .ui-wizard-step-titles li.ui-wizard-step-title');
    }

    //Navigation controls
    if(this.cfg.showNavBar) {
        $(this.backNav).button({icons:{primary: 'ui-icon-arrowthick-1-w'}});
        $(this.nextNav).button({icons:{primary: 'ui-icon-arrowthick-1-e'}});

        $(this.backNav).mouseout(function() {$(this).removeClass('ui-state-focus');});
        $(this.nextNav).mouseout(function() {$(this).removeClass('ui-state-focus');});

        if(currentStepIndex == 0)
            $(this.backNav).hide();
        else if(currentStepIndex == this.cfg.steps.length - 1)
            $(this.nextNav).hide();
    }
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

    var options = {
        source:this.id,
        process:this.id,
        update:this.id,
        formId:this.cfg.formId,
        onsuccess: function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find('update');

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            _self.currentStep = this.args.currentStep;

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    if(!this.args.validationFailed) {
                        //update content
                        $(_self.content).html(content);

                        //update navigation controls
                        var currentStepIndex = _self.getStepIndex(_self.currentStep);

                        if(_self.cfg.showNavBar) {
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
                        }

                        if(_self.cfg.showStepStatus) {
                            _self.stepControls.removeClass('ui-state-hover');
                            $(_self.stepControls.get(currentStepIndex)).addClass('ui-state-hover');
                        }

                    } else {
                        //update content
                        $(_self.content).html(content);
                    }

                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            return true;
        },
        error: function() {
            alert('Error in loading dynamic tab content');
        }
    };
    
    var params = {};
    params[this.id + '_wizardRequest'] = true;
    params[this.id + '_currentStep'] = this.currentStep;
    params[this.id + '_stepToGo'] = stepToGo;

    if(isBack) {
        params[this.id + '_backRequest'] = true;
    }

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.Wizard.prototype.getStepIndex = function(step) {
    for(var i=0; i < this.cfg.steps.length; i++) {
        if(this.cfg.steps[i] == step)
            return i;
    }
	
    return -1;
}

PrimeFaces.widget.Wizard.prototype.showNextNav = function() {
    $(this.nextNav).fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideNextNav = function() {
    $(this.nextNav).fadeOut();
}

PrimeFaces.widget.Wizard.prototype.showBackNav = function() {
    $(this.backNav).fadeIn();
}

PrimeFaces.widget.Wizard.prototype.hideBackNav = function() {
    $(this.backNav).fadeOut();
}
