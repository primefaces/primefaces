/**
 * PrimeFaces Wizard Component
 */
PrimeFaces.widget.Wizard = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.content = this.jqId + '_content';
        this.backNav = $(this.jqId + '_back');
        this.nextNav = $(this.jqId + '_next');
        this.cfg.formId = this.jq.parents('form:first').attr('id');
        var _self = this;

        this.currentStep = this.cfg.initialStep;
        var currentStepIndex = this.getStepIndex(this.currentStep);

        //Step controls
        if(this.cfg.showStepStatus) {
            this.stepControls = $(this.jqId + ' .ui-wizard-step-titles li.ui-wizard-step-title');
        }

        //Navigation controls
        if(this.cfg.showNavBar) {
            //visuals
            PrimeFaces.skinButton(this.backNav);
            PrimeFaces.skinButton(this.nextNav);

            //events
            this.backNav.click(function() {_self.back();});
            this.nextNav.click(function() {_self.next();});

            if(currentStepIndex == 0)
                this.backNav.hide();
            else if(currentStepIndex == this.cfg.steps.length - 1)
                this.nextNav.hide();
        }
    },
    
    back: function() {
        if(this.cfg.onback) {
            this.cfg.onback.call(this);
        }

        var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) - 1];

        this.loadStep(stepToGo, true);
    },
    
    next: function() {
        if(this.cfg.onnext) {
            this.cfg.onnext.call(this);
        }

        var stepToGo = this.cfg.steps[this.getStepIndex(this.currentStep) + 1];

        this.loadStep(stepToGo, false);
    },
    
    loadStep: function(stepToGo, isBack) {
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
    },
    
    getStepIndex: function(step) {
        for(var i=0; i < this.cfg.steps.length; i++) {
            if(this.cfg.steps[i] == step)
                return i;
        }

        return -1;
    },
    
    showNextNav: function() {
        this.nextNav.fadeIn();
    },
    
    hideNextNav: function() {
        this.nextNav.fadeOut();
    },
    
    showBackNav: function() {
        this.backNav.fadeIn();
    },
    
    hideBackNav: function() {
        this.backNav.fadeOut();
    }
    
});
