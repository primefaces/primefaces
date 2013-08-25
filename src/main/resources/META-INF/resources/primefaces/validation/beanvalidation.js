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
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Past.message'] = 'must be in the past';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Pattern.message'] = 'must match "{0}"';
PrimeFaces.locales['en_US'].messages['javax.validation.constraints.Size.message'] = 'size must be between {0} and {1}';
            
PrimeFaces.validator['NotNull'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.NotNull.message',
  
    validate: function(element, value) {
        if(value === null || value === undefined) {
            var mc = PrimeFaces.util.MessageContext,
            msgStr = element.data('p-notnull-msg'),
            msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};

PrimeFaces.validator['Null'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Null.message',
  
    validate: function(element, value) {
        if(value !== null) {
            var mc = PrimeFaces.util.MessageContext,
            msgStr = element.data('p-null-msg'),
            msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};

PrimeFaces.validator['Size'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.Size.message',
  
    validate: function(element, value) {
        if(value !== null){
            var length = element.val().length,
            min = element.data('p-minlength'),
            max = element.data('p-maxlength'),
            mc = PrimeFaces.util.MessageContext;

            if(length < min || length > max) {
                var msgStr = element.data('p-size-msg'),
                msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, min, max);
                throw msg;
            }
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
                var msgStr = element.data('p-min-msg'),
                msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, min);
                throw msg;
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
                var msgStr = element.data('p-max-msg'),
                msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, max);
                throw msg;
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
                var msgStr = element.data('p-decimalmin-msg'),
                msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, min);
                throw msg;
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
                var msgStr = element.data('p-decimalmax-msg'),
                msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, max);
                throw msg;
            }
        }
    }
};

PrimeFaces.validator['AssertTrue'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.AssertTrue.message',
  
    validate: function(element, value) {
        if(value === false) {
            var mc = PrimeFaces.util.MessageContext,
            msgStr = element.data('p-atrue-msg'),
            msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};

PrimeFaces.validator['AssertFalse'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.AssertFalse.message',
  
    validate: function(element, value) {
        if(value === true) {
            var mc = PrimeFaces.util.MessageContext,
            msgStr = element.data('p-afalse-msg'),
            msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};

PrimeFaces.validator['Past'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Past.message',
  
    validate: function(element, value) {
        if(value !== null && value >= new Date()) {
            var msgStr = element.data('p-past-msg'),
            mc = PrimeFaces.util.MessageContext,
            msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};

PrimeFaces.validator['Future'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Future.message',
  
    validate: function(element, value) {
        if(value !== null && value <= new Date()) {
            var msgStr = element.data('p-future-msg'),
            mc = PrimeFaces.util.MessageContext,
            msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID);
            throw msg;
        }
    }
};

PrimeFaces.validator['Pattern'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Pattern.message',
     
    validate: function(element, value) {
        if(value !== null) {
            var pattern = element.data('p-pattern'),
            pattern = pattern.substring(1, (pattern.length - 1)),
            mc = PrimeFaces.util.MessageContext,
            regex = new RegExp(pattern);
            
            if(!regex.test(value)) {
                var msgStr = element.data('p-pattern-msg'),
                msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, pattern);
                throw msg;
            }
        }
    }
};

PrimeFaces.validator['Digits'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.Digits.message',
    regex: /^[-+]?\d+$/,
    
    validate: function(element, value) {
        if(value !== null) {
            var digitsInteger = element.data('p-dintvalue'),
            digitsFraction = element.data('p-dfracvalue'),
            mc = PrimeFaces.util.MessageContext,
            bundle = PrimeFaces.locales[PrimeFaces.settings.locale];

            if(bundle){
                var valueSplitArray = $.trim(value.replace(bundle.groupingSeparator, '')).split(bundle.decimalSeparator);

                if((valueSplitArray.length > 2) ||(!this.regex.test(valueSplitArray[0]))||(!this.regex.test(valueSplitArray[1]))||(valueSplitArray[0].length > digitsInteger)||(valueSplitArray[1].length > digitsFraction))
                    var msgStr = element.data('p-digits-msg'),
                    msg = (msgStr) ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, digitsInteger, digitsFraction);
                    throw msg;
            }
        }
    }
};