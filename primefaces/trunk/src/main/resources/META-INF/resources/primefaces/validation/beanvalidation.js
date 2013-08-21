/**
 * Bean Validation Integration for PrimeFaces Client Side Validation Framework
 */
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.AssertFalse.message'] = 'must be false';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.AssertTrue.message'] = 'must be true';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.DecimalMax.message'] = 'must be less than or equal to {0}';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.DecimalMin.message'] = 'must be greater than or equal to {0}';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Digits.message'] = 'numeric value out of bounds (<{0} digits>.<{1} digits> expected)';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Future.message'] = 'must be in the future';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Max.message'] = 'must be less than or equal to {0}';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Min.message'] = 'must be greater than or equal to {0}';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.NotNull.message'] = 'may not be null';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Null.message'] = 'must be null';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Past.message'] = 'must be false';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Pattern.message'] = 'must match "{0}"';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Size.message'] = 'size must be between {0} and {1}';
            
PrimeFaces.validator['NotNull'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.NotNull.message',
  
    validate: function(element, value) {
        if(value === null || value === undefined) {
            throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID);
        }
    }
};

PrimeFaces.validator['Size'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.Size.message',
  
    validate: function(element, value) {
        var length = element.val().length,
        min = element.data('p-minlength'),
        max = element.data('p-maxlength'),
        mc = PrimeFaces.util.MessageContext;

        if(length < min||length > max) {
            var msgStr = element.data('p-size-msg'),
            msg = msgStr ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, min, max);
    
            throw msg;
        }
    }
};

PrimeFaces.validator['Min'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.Min.message',
  
    validate: function(element, value) {
        if(value !== null) {
            var min = element.data('p-minvalue'),
            mc = PrimeFaces.util.MessageContext;
            
            if(value < min) {
                throw mc.getMessage(this.MESSAGE_ID, min);
            }
        }
    }
};

PrimeFaces.validator['Max'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.Max.message',
  
    validate: function(element, value) {
        if(value !== null) {
            var max = element.data('p-maxvalue'),
            mc = PrimeFaces.util.MessageContext;
            
            if(value > max) {
                throw mc.getMessage(this.MESSAGE_ID, max);
            }
        }
    }
};

PrimeFaces.validator['DecimalMin'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.DecimalMin.message',
  
    validate: function(element, value) {
        if(value !== null) {
            var min = element.data('p-minvalue'),
            mc = PrimeFaces.util.MessageContext;
            
            if(value < min) {
                throw mc.getMessage(this.MESSAGE_ID, min);
            }
        }
    }
};

PrimeFaces.validator['DecimalMax'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.DecimalMax.message',
  
    validate: function(element, value) {
        if(value !== null) {
            var max = element.data('p-maxvalue'),
            mc = PrimeFaces.util.MessageContext;
            
            if(value > max) {
                throw mc.getMessage(this.MESSAGE_ID, max);
            }
        }
    }
};

PrimeFaces.validator['PrimeFaces'] = {
      
    validate: function(element, value) {
        if(value !== 'PrimeFaces') {
            throw {summary: 'Must be PrimeFaces', detail: 'Must be PrimeFaces'};
        }
    }
};