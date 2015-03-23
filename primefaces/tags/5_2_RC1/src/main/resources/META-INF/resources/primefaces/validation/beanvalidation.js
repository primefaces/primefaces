// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {
    /**
     * Bean Validation Integration for PrimeFaces Client Side Validation Framework
     */
    PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] = '{0}';
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
                var vc = PrimeFaces.util.ValidationContext;
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-notnull-msg'));
            }
        }
    };

    PrimeFaces.validator['Null'] = {

        MESSAGE_ID: 'javax.validation.constraints.Null.message',

        validate: function(element, value) {
            if(value !== null) {
                var vc = PrimeFaces.util.ValidationContext;
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-null-msg'));
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
                vc = PrimeFaces.util.ValidationContext;

                if(length < min || length > max) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-size-msg'), min, max);
                }
            }
        }
    };

    PrimeFaces.validator['Min'] = {

        MESSAGE_ID: 'javax.validation.constraints.Min.message',

        validate: function(element, value) {
            if(value !== null) {
                var min = element.data('p-minvalue'),
                vc = PrimeFaces.util.ValidationContext;

                if(value < min) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-min-msg'), min);
                }
            }
        }
    };

    PrimeFaces.validator['Max'] = {

        MESSAGE_ID: 'javax.validation.constraints.Max.message',

        validate: function(element, value) {
            if(value !== null) {
                var max = element.data('p-maxvalue'),
                vc = PrimeFaces.util.ValidationContext;

                if(value > max) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-max-msg'), max);
                }
            }
        }
    };

    PrimeFaces.validator['DecimalMin'] = {

        MESSAGE_ID: 'javax.validation.constraints.DecimalMin.message',

        validate: function(element, value) {
            if(value !== null) {
                var min = element.data('p-minvalue'),
                vc = PrimeFaces.util.ValidationContext;

                if(value < min) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-decimalmin-msg'), min);
                }
            }
        }
    };

    PrimeFaces.validator['DecimalMax'] = {

        MESSAGE_ID: 'javax.validation.constraints.DecimalMax.message',

        validate: function(element, value) {
            if(value !== null) {
                var max = element.data('p-maxvalue'),
                vc = PrimeFaces.util.ValidationContext;

                if(value > max) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-decimalmax-msg'), max);
                }
            }
        }
    };

    PrimeFaces.validator['AssertTrue'] = {

        MESSAGE_ID: 'javax.validation.constraints.AssertTrue.message',

        validate: function(element, value) {
            if(value === false) {
                var vc = PrimeFaces.util.ValidationContext;
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-atrue-msg'));
            }
        }
    };

    PrimeFaces.validator['AssertFalse'] = {

        MESSAGE_ID: 'javax.validation.constraints.AssertFalse.message',

        validate: function(element, value) {
            if(value === true) {
                var vc = PrimeFaces.util.ValidationContext;
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-afalse-msg'));
            }
        }
    };

    PrimeFaces.validator['Past'] = {

        MESSAGE_ID: 'javax.validation.constraints.Past.message',

        validate: function(element, value) {
            if(value !== null && value >= new Date()) {
                var vc = PrimeFaces.util.ValidationContext;
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-past-msg'));
            }
        }
    };

    PrimeFaces.validator['Future'] = {

        MESSAGE_ID: 'javax.validation.constraints.Future.message',

        validate: function(element, value) {
            if(value !== null && value <= new Date()) {
                var vc = PrimeFaces.util.ValidationContext;
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-future-msg'));
            }
        }
    };

    PrimeFaces.validator['Pattern'] = {

        MESSAGE_ID: 'javax.validation.constraints.Pattern.message',

        validate: function(element, value) {
            if(value !== null) {
                var pattern = element.data('p-pattern'),
                vc = PrimeFaces.util.ValidationContext,
                regex = new RegExp(pattern);

                if(!regex.test(value)) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-pattern-msg'), pattern);
                }
            }
        }
    };

    PrimeFaces.validator['Digits'] = {

        MESSAGE_ID: 'javax.validation.constraints.Digits.message',

        validate: function(element, value) {
            if(value !== null) {
                var digitsInteger = element.data('p-dintvalue'),
                digitsFraction = element.data('p-dfracvalue'),
                vc = PrimeFaces.util.ValidationContext,
                locale = vc.getLocaleSettings();

                var tokens = value.toString().split(locale.decimalSeparator),
                intValue = tokens[0].replace(new RegExp(locale.groupingSeparator, 'g'), ''),
                decimalValue = tokens[1];

                if(digitsInteger !== undefined && intValue && digitsInteger < intValue.length
                        ||digitsFraction !== undefined && decimalValue && decimalValue.length > digitsFraction) {
                    throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-digits-msg'), digitsInteger, digitsFraction);
                }
            }
        }
    };
    
    PrimeFaces.util.ValidationContext.getMessageBV = function(element, defaultKey, msg) {
        if (msg && msg.charAt(0) !== '{') {
            return { summary : msg, detail : msg };
        }
        else {
            var key = defaultKey;
            if (msg && msg.charAt(0) === '{') {
                key = msg.substring(1, msg.length - 1);
            }

            var locale = PrimeFaces.util.ValidationContext.getLocaleSettings();
            var bundle = (locale.messages && locale.messages[key]) ? locale : PrimeFaces.locales['en_US'];

            var summary = bundle.messages[key];
            var detail = bundle.messages[key + '_detail'];

            if(summary) {
                summary = this.formatBV(summary, arguments);
                detail = (detail) ? this.formatBV(detail, arguments) : summary;

                // see #7069
                // simulate the message handling of the server side BeanValidator
                var wrapperBundle = (locale.messages && locale.messages['javax.faces.validator.BeanValidator.MESSAGE']) ? locale : PrimeFaces.locales['en_US'];
                var wrapper = wrapperBundle.messages['javax.faces.validator.BeanValidator.MESSAGE'];
                var label = PrimeFaces.util.ValidationContext.getLabel(element);
                summary = wrapper.replace("{0}", summary).replace("{1}", label);
                detail = wrapper.replace("{0}", detail).replace("{1}", label);

                return { summary : summary, detail : detail };
            }
            else {
                return { summary : "### Message '" + key + "' not found ###", detail : "### Message '" + key + "' not found ###" };
            }
        }
    };
    
    PrimeFaces.util.ValidationContext.formatBV = function(str, params) {
        var s = str;
        for(var i = 3; i < params.length; i++) {       
            var reg = new RegExp('\\{' + (i - 3) + '\\}', 'gm');             
            s = s.replace(reg, params[i]);
        }

        return s;
    };
}