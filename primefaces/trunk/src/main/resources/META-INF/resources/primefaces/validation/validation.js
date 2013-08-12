/**
 * PrimeFaces Client Side Validation Framework
 */
(function(PrimeFaces) {
    
    if(PrimeFaces.validator) {
        PrimeFaces.debug("PrimeFaces client side validation framework already loaded, ignoring duplicate execution.");
        return;
    }

    //package
    PrimeFaces.validator = {};
    PrimeFaces.converter = {};
    
    PrimeFaces.validator = {
        
        'javax.faces.Length': {
            
            MINIMUM_MESSAGE_ID: 'javax.faces.validator.LengthValidator.MINIMUM',
            
            MAXIMUM_MESSAGE_ID: 'javax.faces.validator.LengthValidator.MAXIMUM',
            
            validate: function(element) {
                var value = element.val(),
                length = value.length,
                min = element.data('p-minlength'),
                max = element.data('p-maxlength'),
                mf = PrimeFaces.util.MessageFactory;
        
                if(max !== undefined && length > max) {
                    throw mf.getMessage(this.MAXIMUM_MESSAGE_ID, max, mf.getLabel(element));
                }
                
                if(min !== undefined && length < min) {
                    throw mf.getMessage(this.MINIMUM_MESSAGE_ID, min, mf.getLabel(element));
                }
            }
        }
    }
        
    PrimeFaces.vb = function(cfg) {
        return this.validate(cfg);
    }
       
    PrimeFaces.validate = function(cfg) {
        var exceptions = [],
        mf = PrimeFaces.util.MessageFactory;
        
        if(cfg.ajax) {
            
        }
        else {
            var form = $(cfg.source).closest('form');
            
            if(form.length) {
                var inputs = form.find(':input:visible:enabled:not(:button)'),
                valid = true;
                
                for(var i = 0; i < inputs.length; i++) {
                    var inputElement = inputs.eq(i),
                    value = inputElement.val(),
                    required = inputElement.data('p-required');
                    
                    if(valid && required && inputElement.val() === '') {
                        exceptions.push(mf.getMessage('javax.faces.component.UIInput.REQUIRED', mf.getLabel(inputElement)));
                        
                        valid = false;
                    }
                                        
                    //validators
                    if(valid && ((value !== '')||PrimeFaces.settings.validateEmptyFields)) {
                        var validatorIds = inputElement.data('p-val');
                        if(validatorIds) {
                            validatorIds = validatorIds.split(',');

                            for(var j = 0; j < validatorIds.length; j++) {
                                var validatorId = validatorIds[j],
                                validator = PrimeFaces.validator[validatorId];

                                if(validator) {
                                    try {
                                        validator.validate(inputElement);
                                    }
                                    catch(exception) {
                                        valid = false;
                                        exceptions.push(exception);
                                    }
                                }
                            }
                        }
                    }
                    
                    if(!valid) {
                        inputElement.addClass('ui-state-error');
                    }
                }
            }
        }
        
        if(exceptions.length === 0) {
            return true;
        }
        else {
            var uimessages = form.find('.ui-messages');
            if(uimessages.length) {
                PrimeFaces.util.MessageRenderer.render(uimessages, exceptions);
            }
            
            return false;
        }
    }
    
    PrimeFaces.util.MessageRenderer = {
        
        render: function(element, exceptions) {
            element.html('');
            element.append('<div class="ui-messages-error ui-corner-all"><span class="ui-messages-error-icon"></span><ul></ul></div>');
            var messageList = element.find('> .ui-messages-error > ul');
            
            for(var i = 0; i < exceptions.length; i++) {
                var msg = exceptions[i];
                messageList.append('<li><span class="ui-messages-error-summary">' + msg.summary + '</span><span class="ui-messages-error-detail">' + msg.summary + '</span></li>');
            }
        }
    }
    
    PrimeFaces.util.MessageFactory = {
        
        getMessage: function(key) {
            var bundle = PrimeFaces.locales[PrimeFaces.settings.locale];
            if(bundle) {
                var s = bundle.messages[key],
                d = bundle.messages[key + '_detail'];
                
                s = this.format(s, arguments);
                
                if(d)
                    d = this.format(d, arguments);
                else
                    d = s;
                                
                return {
                    summary: s,
                    detail: d
                }
            }
            
            return null;
        },
        
        format: function(str, params) {
            var s = str;
            for(var i = 0; i < params.length - 1; i++) {       
                var reg = new RegExp('\\{' + i + '\\}', 'gm');             
                s = s.replace(reg, params[i + 1]);
            }
            
            return s;
        },
                
        getLabel: function(element) {
            return (element.data('p-label')||element.attr('id'))
        }
    };

})(PrimeFaces);