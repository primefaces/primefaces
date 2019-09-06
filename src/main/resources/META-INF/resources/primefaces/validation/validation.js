// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {
    /**
     * PrimeFaces Client Side Validation Framework
     */
    $.extend(PrimeFaces.locales['en_US'],{
        decimalSeparator: '.',
        groupingSeparator: ',',
        messages: {
            'javax.faces.component.UIInput.REQUIRED': '{0}: Validation Error: Value is required.',
            'javax.faces.converter.IntegerConverter.INTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.IntegerConverter.INTEGER_detail': '{2}: \'{0}\' must be a number between -2147483648 and 2147483647 Example: {1}',
            'javax.faces.converter.LongConverter.LONG': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.LongConverter.LONG_detail': '{2}: \'{0}\' must be a number between -9223372036854775808 to 9223372036854775807 Example: {1}',
            'javax.faces.converter.DoubleConverter.DOUBLE': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.DoubleConverter.DOUBLE_detail': '{2}: \'{0}\' must be a number between 4.9E-324 and 1.7976931348623157E308  Example: {1}',
            'javax.faces.converter.BigDecimalConverter.DECIMAL': '{2}: \'{0}\' must be a signed decimal number.',
            'javax.faces.converter.BigDecimalConverter.DECIMAL_detail': '{2}: \'{0}\' must be a signed decimal number consisting of zero or more digits, that may be followed by a decimal point and fraction.  Example: {1}',
            'javax.faces.converter.BigIntegerConverter.BIGINTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.BigIntegerConverter.BIGINTEGER_detail': '{2}: \'{0}\' must be a number consisting of one or more digits. Example: {1}',
            'javax.faces.converter.ByteConverter.BYTE': '{2}: \'{0}\' must be a number between 0 and 255.',
            'javax.faces.converter.ByteConverter.BYTE_detail': '{2}: \'{0}\' must be a number between 0 and 255.  Example: {1}',
            'javax.faces.converter.CharacterConverter.CHARACTER': '{1}: \'{0}\' must be a valid character.',
            'javax.faces.converter.CharacterConverter.CHARACTER_detail': '{1}: \'{0}\' must be a valid ASCII character.',
            'javax.faces.converter.ShortConverter.SHORT': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.ShortConverter.SHORT_detail': '{2}: \'{0}\' must be a number between -32768 and 32767 Example: {1}',
            'javax.faces.converter.BooleanConverter.BOOLEAN': '{1}: \'{0}\' must be \'true\' or \'false\'',
            'javax.faces.converter.BooleanConverter.BOOLEAN_detail': '{1}: \'{0}\' must be \'true\' or \'false\'.  Any value other than \'true\' will evaluate to \'false\'.',
            'javax.faces.validator.LongRangeValidator.MAXIMUM': '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
            'javax.faces.validator.LongRangeValidator.MINIMUM': '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
            'javax.faces.validator.LongRangeValidator.NOT_IN_RANGE': '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}.',
            'javax.faces.validator.LongRangeValidator.TYPE={0}': 'Validation Error: Value is not of the correct type.',
            'javax.faces.validator.DoubleRangeValidator.MAXIMUM': '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
            'javax.faces.validator.DoubleRangeValidator.MINIMUM': '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
            'javax.faces.validator.DoubleRangeValidator.NOT_IN_RANGE': '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}',
            'javax.faces.validator.DoubleRangeValidator.TYPE={0}': 'Validation Error: Value is not of the correct type',
            'javax.faces.converter.FloatConverter.FLOAT': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.FloatConverter.FLOAT_detail': '{2}: \'{0}\' must be a number between 1.4E-45 and 3.4028235E38  Example: {1}',
            'javax.faces.converter.DateTimeConverter.DATE': '{2}: \'{0}\' could not be understood as a date.',
            'javax.faces.converter.DateTimeConverter.DATE_detail': '{2}: \'{0}\' could not be understood as a date. Example: {1}',
            'javax.faces.converter.DateTimeConverter.TIME': '{2}: \'{0}\' could not be understood as a time.',
            'javax.faces.converter.DateTimeConverter.TIME_detail': '{2}: \'{0}\' could not be understood as a time. Example: {1}',
            'javax.faces.converter.DateTimeConverter.DATETIME': '{2}: \'{0}\' could not be understood as a date and time.',
            'javax.faces.converter.DateTimeConverter.DATETIME_detail': '{2}: \'{0}\' could not be understood as a date and time. Example: {1}',
            'javax.faces.converter.DateTimeConverter.PATTERN_TYPE': '{1}: A \'pattern\' or \'type\' attribute must be specified to convert the value \'{0}\'',
            'javax.faces.converter.NumberConverter.CURRENCY': '{2}: \'{0}\' could not be understood as a currency value.',
            'javax.faces.converter.NumberConverter.CURRENCY_detail': '{2}: \'{0}\' could not be understood as a currency value. Example: {1}',
            'javax.faces.converter.NumberConverter.PERCENT': '{2}: \'{0}\' could not be understood as a percentage.',
            'javax.faces.converter.NumberConverter.PERCENT_detail': '{2}: \'{0}\' could not be understood as a percentage. Example: {1}',
            'javax.faces.converter.NumberConverter.NUMBER': '{2}: \'{0}\' is not a number.',
            'javax.faces.converter.NumberConverter.NUMBER_detail': '{2}: \'{0}\' is not a number. Example: {1}',
            'javax.faces.converter.NumberConverter.PATTERN': '{2}: \'{0}\' is not a number pattern.',
            'javax.faces.converter.NumberConverter.PATTERN_detail': '{2}: \'{0}\' is not a number pattern. Example: {1}',
            'javax.faces.validator.LengthValidator.MINIMUM': '{1}: Validation Error: Length is less than allowable minimum of \'{0}\'',
            'javax.faces.validator.LengthValidator.MAXIMUM': '{1}: Validation Error: Length is greater than allowable maximum of \'{0}\'',
            'javax.faces.validator.RegexValidator.PATTERN_NOT_SET': 'Regex pattern must be set.',
            'javax.faces.validator.RegexValidator.PATTERN_NOT_SET_detail': 'Regex pattern must be set to non-empty value.',
            'javax.faces.validator.RegexValidator.NOT_MATCHED': 'Regex Pattern not matched',
            'javax.faces.validator.RegexValidator.NOT_MATCHED_detail': 'Regex pattern of \'{0}\' not matched',
            'javax.faces.validator.RegexValidator.MATCH_EXCEPTION': 'Error in regular expression.',
            'javax.faces.validator.RegexValidator.MATCH_EXCEPTION_detail': 'Error in regular expression, \'{0}\''
        }
    });

    PrimeFaces.validator = {

        'javax.faces.Length': {

            MINIMUM_MESSAGE_ID: 'javax.faces.validator.LengthValidator.MINIMUM',

            MAXIMUM_MESSAGE_ID: 'javax.faces.validator.LengthValidator.MAXIMUM',

            validate: function(element) {
                var length = element.val().length,
                min = element.data('p-minlength'),
                max = element.data('p-maxlength'),
                vc = PrimeFaces.util.ValidationContext;

                if(max !== undefined && length > max) {
                    throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
                }

                if(min !== undefined && length < min) {
                    throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
                }
            }
        },

        'javax.faces.LongRange': {

            MINIMUM_MESSAGE_ID: 'javax.faces.validator.LongRangeValidator.MINIMUM',
            MAXIMUM_MESSAGE_ID: 'javax.faces.validator.LongRangeValidator.MAXIMUM',
            NOT_IN_RANGE_MESSAGE_ID: 'javax.faces.validator.LongRangeValidator.NOT_IN_RANGE',
            TYPE_MESSAGE_ID: 'javax.faces.validator.LongRangeValidator.TYPE',
            regex: /^-?\d+$/,

            validate: function(element, value) {
                if(value !== null) {
                    var min = element.data('p-minvalue'),
                    max = element.data('p-maxvalue'),
                    vc = PrimeFaces.util.ValidationContext;

                    if(!this.regex.test(element.val())) {
                        throw vc.getMessage(this.TYPE_MESSAGE_ID, vc.getLabel(element));
                    }

                    if((max !== undefined && min !== undefined) && (value < min || value > max)) {
                        throw vc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, vc.getLabel(element));
                    }
                    else if((max !== undefined && min === undefined) && (value > max)) {
                        throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
                    }
                    else if((min !== undefined && max === undefined) && (value < min)) {
                        throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
                    }
                }
            }
        },

        'javax.faces.DoubleRange': {

            MINIMUM_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.MINIMUM',
            MAXIMUM_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.MAXIMUM',
            NOT_IN_RANGE_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.NOT_IN_RANGE',
            TYPE_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.TYPE',
            regex: /^[-+]?\d*(\.\d+)?[d]?$/,

            validate: function(element, value) {
                if(value !== null) {
                    var min = element.data('p-minvalue'),
                    max = element.data('p-maxvalue'),
                    vc = PrimeFaces.util.ValidationContext;

                    if(!this.regex.test(element.val())) {
                        throw vc.getMessage(this.TYPE_MESSAGE_ID, vc.getLabel(element));
                    }

                    if((max !== undefined && min !== undefined) && (value < min || value > max)) {
                        throw vc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, vc.getLabel(element));
                    }
                    else if((max !== undefined && min === undefined) && (value > max)) {
                        throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
                    }
                    else if((min !== undefined && max === undefined) && (value < min)) {
                        throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
                    }
                }
            }
        },

        'javax.faces.RegularExpression': {

            PATTERN_NOT_SET_MESSAGE_ID: 'javax.faces.validator.RegexValidator.PATTERN_NOT_SET',
            NOT_MATCHED_MESSAGE_ID: 'javax.faces.validator.RegexValidator.NOT_MATCHED',
            MATCH_EXCEPTION_MESSAGE_ID: 'javax.faces.validator.RegexValidator.MATCH_EXCEPTION',

            validate: function(element, value) {
                if(value !== null) {
                    var pattern = element.data('p-regex'),
                    vc = PrimeFaces.util.ValidationContext;

                    if(!pattern) {
                        throw vc.getMessage(this.PATTERN_NOT_SET_MESSAGE_ID);
                    }

                    var regex = new RegExp(pattern);
                    if(!regex.test(value)) {
                        throw vc.getMessage(this.NOT_MATCHED_MESSAGE_ID, pattern);
                    }
                }
            }
        }
    };

    PrimeFaces.converter = {

        'javax.faces.Integer': {

            regex: /^[-+]?\d+$/,

            MESSAGE_ID: 'javax.faces.converter.IntegerConverter.INTEGER',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 9346, vc.getLabel(element));
                }

                return parseInt(submittedValue);
            }
        },

        'javax.faces.Long': {

            regex: /^[-+]?\d+$/,

            MESSAGE_ID: 'javax.faces.converter.LongConverter.LONG',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 98765432, vc.getLabel(element));
                }

                return parseInt(submittedValue);
            }
        },

        'javax.faces.Double': {

            regex: /^[-+]?\d*(\.\d+)?[d]?$/,

            MESSAGE_ID: 'javax.faces.converter.DoubleConverter.DOUBLE',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 1999999, vc.getLabel(element));
                }

                return parseFloat(submittedValue);
            }
        },

        'javax.faces.Float': {

            regex: /^[-+]?\d+(\.\d+)?[f]?$/,

            MESSAGE_ID: 'javax.faces.converter.FloatConverter.FLOAT',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 2000000000, vc.getLabel(element));
                }

                return parseFloat(submittedValue);
            }
        },

        'javax.faces.Short': {

            regex: /^[-+]?\d+$/,

            MESSAGE_ID: 'javax.faces.converter.ShortConverter.SHORT',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 32456, vc.getLabel(element));
                }

                return parseInt(submittedValue);
            }
        },

        'javax.faces.BigInteger': {

            regex: /^[-+]?\d+$/,

            MESSAGE_ID: 'javax.faces.converter.BigIntegerConverter.BIGINTEGER',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 9876, vc.getLabel(element));
                }

                return parseInt(submittedValue);
            }
        },

        'javax.faces.BigDecimal': {

            regex: /^[-+]?\d+(\.\d+)?[d]?$/,

            MESSAGE_ID: 'javax.faces.converter.BigDecimalConverter.DECIMAL',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 198.23, vc.getLabel(element));
                }

                return parseFloat(submittedValue);
            }
        },

        'javax.faces.Byte': {

            regex: /^\d+$/,

            MESSAGE_ID: 'javax.faces.converter.ByteConverter.BYTE',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                if(!this.regex.test(submittedValue)) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, 9346, vc.getLabel(element));
                }
                else {
                    var byteValue = parseInt(submittedValue);

                    if(byteValue < 0 || byteValue > 255)
                        throw vc.getMessage(this.MESSAGE_ID, submittedValue, 9346, vc.getLabel(element));
                    else
                        return byteValue;
                }
            }
        },

        'javax.faces.Character': {

            MESSAGE_ID: 'javax.faces.converter.CharacterConverter.CHARACTER',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                try {
                    return submittedValue.charAt(0);
                }
                catch(exception) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, vc.getLabel(element));
                }
            }
        },

        'javax.faces.Boolean': {

            regex: /^[-+]?\d+$/,

            MESSAGE_ID: 'javax.faces.converter.BooleanConverter.BOOLEAN',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext;

                try {
                    return ((submittedValue === 'true' || submittedValue === 'on' || submittedValue === 'yes') ? true : false);
                }
                catch(exception) {
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, vc.getLabel(element));
                }
            }
        },

        'javax.faces.DateTime': {

            DATE_ID: 'javax.faces.converter.DateTimeConverter.DATE',
            TIME_ID: 'javax.faces.converter.DateTimeConverter.TIME',
            DATETIME_ID: 'javax.faces.converter.DateTimeConverter.DATETIME',

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext,
                pattern = element.data('p-pattern'),
                type = element.data('p-dttype'),
                datePattern = null,
                timePattern = null;

                var locale = vc.getLocaleSettings();

                try {
                    if(pattern) {
                        var patternArr = pattern.split(" ");
                        for(var i = 0; i < patternArr.length; i++) {
                            if(patternArr[i].toLowerCase().indexOf('h') !== -1) {
                                timePattern = patternArr[i];
                            }
                            else if(patternArr[i].toLowerCase().indexOf('t') !== -1 && timePattern) {
                                timePattern = timePattern + " " + patternArr[i];
                            }
                            else {
                                datePattern = patternArr[i];
                            }
                        }
                    }
                    else {
                         datePattern = element.data('p-dspattern');
                         timePattern = element.data('p-tspattern');
                    }

                    if(timePattern && datePattern) {
                        return $.datepicker.parseDateTime(datePattern, timePattern, submittedValue, locale, {timeFormat:timePattern});
                    }
                    else if(timePattern) {
                        return $.datepicker.parseTime(timePattern, submittedValue, locale);
                    }
                    else {
                        return $.datepicker.parseDate(datePattern, submittedValue, locale);
                    }
                }
                catch(exception) {
                    var now = $.datepicker.formatDate(pattern, new Date(), locale);

                    if(type === 'date')
                        throw vc.getMessage(this.DATE_ID, submittedValue, now, vc.getLabel(element));
                    else if(type === 'time')
                        throw vc.getMessage(this.TIME_ID, submittedValue, now, vc.getLabel(element));
                    else if(type === 'both')
                        throw vc.getMessage(this.DATETIME_ID, submittedValue, now, vc.getLabel(element));
                }
            }
        },

        'javax.faces.Number': {

            CURRENCY_ID: 'javax.faces.converter.NumberConverter.CURRENCY',
            NUMBER_ID: 'javax.faces.converter.NumberConverter.NUMBER',
            PATTERN_ID: 'javax.faces.converter.NumberConverter.PATTERN',
            PERCENT_ID: 'javax.faces.converter.NumberConverter.PERCENT',
            REGEX: /^[-+]?\d+(\,\d+)?(\.\d+)?[d]?$/,

            convert: function(element, submittedValue) {
                if(submittedValue === null) {
                    return null;
                }

                if($.trim(submittedValue).length === 0) {
                    return null;
                }

                var vc = PrimeFaces.util.ValidationContext,
                locale = vc.getLocaleSettings(),
                type = element.data('p-notype'),
                maxIntegerDigits = element.data('p-maxint'),
                minFractionDigits = element.data('p-minfrac'),
                integerOnly = element.data('p-intonly');

                if(type === 'currency') {
                    var currencySymbol = element.data('p-curs');

                    if(currencySymbol) {
                        if(submittedValue.indexOf(currencySymbol) === -1)
                            throw vc.getMessage(this.CURRENCY_ID, submittedValue, currencySymbol + '100', vc.getLabel(element));
                        else
                            submittedValue = submittedValue.substring(currencySymbol.length);
                    }
                }
                else if(type === 'percent') {
                    if(submittedValue.lastIndexOf('%') !== (submittedValue.length - 1))
                        throw vc.getMessage(this.PERCENT_ID, submittedValue, '50%', vc.getLabel(element));
                    else
                        submittedValue = submittedValue.replace(/%/g, '');
                }

                if(!this.REGEX.test(submittedValue)) {
                    throw vc.getMessage(this.NUMBER_ID, submittedValue, 50, vc.getLabel(element));
                }

                var tokens = submittedValue.split(locale.decimalSeparator),
                intValue = tokens[0].replace(new RegExp(locale.groupingSeparator, 'g'), ''),
                decimalValue = tokens[1];

                if(maxIntegerDigits && intValue.length > maxIntegerDigits)
                    intValue = intValue.substring(intValue.length - maxIntegerDigits);

                if(decimalValue && minFractionDigits && decimalValue.length > minFractionDigits)
                    decimalValue = decimalValue.substring(0, minFractionDigits);

                if(integerOnly) {
                    return parseInt(intValue);
                }
                else {
                    return parseInt(intValue) + parseFloat('.' + decimalValue);
                }
            }
        }
    };

    PrimeFaces.vb = function(cfg) {
        return this.validate(cfg);
    };

    PrimeFaces.vi = function(element) {
        return this.validateInstant(element);
    };

    PrimeFaces.validate = function(cfg) {
        var vc = PrimeFaces.util.ValidationContext,
        form = $(cfg.s).closest('form');

        if(cfg.a && cfg.p) {
            var processIds = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(cfg.p),
            inputs = $();

            for(var i = 0; i < processIds.length; i++) {
                if(processIds[i]) {
                    var component = $(PrimeFaces.escapeClientId(processIds[i]));
                    if(component.is(':input'))
                        inputs = inputs.add(component);
                    else
                        inputs = inputs.add(component.find(':input:visible:enabled:not(:button)[name]'));
                }
            }

            this.validateInputs(inputs);
        }
        else {
            var inputs = form.find(':input:visible:enabled:not(:button)[name]');
            this.validateInputs(inputs);
        }

        if(vc.isEmpty()) {
            return true;
        }
        else {
            if(cfg.a && cfg.u) {
                var updateIds = PrimeFaces.expressions.SearchExpressionFacade.resolveComponents(cfg.u);
                for(var i = 0; i < updateIds.length; i++) {
                    if(updateIds[i]) {
                        var component = $(PrimeFaces.escapeClientId(updateIds[i]));
                        vc.renderMessages(component);
                    }
                }
            }
            else {
                vc.renderMessages(form);
            }

            //focus first element
            for(var key in vc.messages) {
                if(vc.messages.hasOwnProperty(key)) {
                    var el = $(PrimeFaces.escapeClientId(key));
                    if(!el.is(':focusable'))
                        el.find(':focusable:first').focus();
                    else
                        el.focus();
                    break;
                }
            }

            vc.clear();

            return false;
        }
    };

    PrimeFaces.validateInputs = function(inputs) {
        for(var i = 0; i < inputs.length; i++) {
            this.validateInput(inputs.eq(i));
        }
    };

    PrimeFaces.validateInput = function(element) {
        var vc = PrimeFaces.util.ValidationContext;

        if(element.is(':checkbox,:radio') && element.data('p-grouped')) {
            var groupName = element.attr('name');

            if(!vc.isGroupValidated(groupName)) {
                vc.addElementGroup(groupName);
            } else {
                return;
            }
        }
        
        if (element.parent().hasClass('ui-inputnumber')) {
            element = element.parent().children('input:hidden');
        }

        var submittedValue = vc.getSubmittedValue(element),
        valid = true,
        converterId = element.data('p-con');

        if(PrimeFaces.settings.considerEmptyStringNull && ((!submittedValue) || submittedValue.length === 0)) {
            submittedValue = null;
        }

        var newValue = null;
        if(converterId) {
            try {
                newValue = PrimeFaces.converter[converterId].convert(element, submittedValue);
            }
            catch(ce) {
                var converterMessageStr = element.data('p-cmsg'),
                converterMsg = (converterMessageStr) ? {summary:converterMessageStr,detail:converterMessageStr} : ce;
                valid = false;
                vc.addMessage(element, converterMsg);
            }
        }
        else {
            newValue = submittedValue;
        }

        var required = element.data('p-required');
        if (required) {
           element.attr('aria-required', true);
        }

        if(valid && required && (newValue === null || newValue === '')) {
            var requiredMessageStr = element.data('p-rmsg'),
            requiredMsg = (requiredMessageStr) ? {summary:requiredMessageStr,detail:requiredMessageStr} : vc.getMessage('javax.faces.component.UIInput.REQUIRED', vc.getLabel(element));
            vc.addMessage(element, requiredMsg);

            valid = false;
        }

        if(valid && ((submittedValue !== null && $.trim(submittedValue).length > 0)||PrimeFaces.settings.validateEmptyFields)) {
            var validatorIds = element.data('p-val');
            if(validatorIds) {
                validatorIds = validatorIds.split(',');

                for(var j = 0; j < validatorIds.length; j++) {
                    var validatorId = validatorIds[j],
                    validator = PrimeFaces.validator[validatorId];

                    if(validator) {
                        try {
                            validator.validate(element, newValue);
                        }
                        catch(ve) {
                            var validatorMessageStr = element.data('p-vmsg'),
                            validatorMsg = (validatorMessageStr) ? {summary:validatorMessageStr,detail:validatorMessageStr} : ve;
                            valid = false;
                            vc.addMessage(element, validatorMsg);
                        }
                    }
                }
            }
        }

        var highlighterType = element.data('p-hl')||'default',
        highlighter = PrimeFaces.validator.Highlighter.types[highlighterType];

        if(valid) {
            highlighter.unhighlight(element);
            element.attr('aria-invalid', false);
        }
        else {
            highlighter.highlight(element);
            element.attr('aria-invalid', true);
        }
    };

    PrimeFaces.validateInstant = function(el) {
        var vc = PrimeFaces.util.ValidationContext,
        element = (typeof el === 'string') ? $(PrimeFaces.escapeClientId(el)) : $(el),
        clientId = element.data(PrimeFaces.CLIENT_ID_DATA)||element.attr('id'),
        uiMessageId = element.data('uimessageid'),
        uiMessage = null;

        if(uiMessageId) {
            uiMessage = (uiMessageId === 'p-nouimessage') ? null: $(PrimeFaces.escapeClientId(uiMessageId));
        }
        else {
            uiMessage = vc.findUIMessage(clientId, element.closest('form').find('div.ui-message'));

            if(uiMessage)
                element.data('uimessageid', uiMessage.attr('id'));
            else
                element.data('uimessageid', 'p-nouimessage');
        }

        if(uiMessage) {
            uiMessage.html('').removeClass('ui-message-error ui-message-icon-only ui-widget ui-corner-all ui-helper-clearfix');
        }

        this.validateInput(element);

        if(!vc.isEmpty()) {
            if(uiMessage) {
                vc.renderUIMessage(uiMessage, vc.messages[clientId][0]);
            }

            vc.clear();
            return false;
        }
        else {
            vc.clear();
            return true;
        }
    };

    PrimeFaces.util.ValidationContext = {

        messages: {},

        elementGroups: [],

        addMessage: function(element, msg) {
            var clientId = element.data(PrimeFaces.CLIENT_ID_DATA)||element.attr('id');

            if(!this.messages[clientId]) {
                this.messages[clientId] = [];
            }

            this.messages[clientId].push(msg);
        },

        getMessage: function(key) {
            var locale = this.getLocaleSettings(),
            bundle = (locale.messages && locale.messages[key]) ? locale : PrimeFaces.locales['en_US'];

            var s = bundle.messages[key],
            d = bundle.messages[key + '_detail'];

            if(s) {
                s = this.format(s, arguments);
                d = (d) ? this.format(d, arguments) : s;

                return {
                    summary: s,
                    detail: d
                };
            }
            else {
                return null;
            }
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
            return (element.data('p-label')||element.attr('id'));
        },

        renderMessages: function(container) {
            var uiMessagesAll = container.is('div.ui-messages') ? container : container.find('div.ui-messages:not(.ui-fileupload-messages)'),
                uiMessages = uiMessagesAll.filter(function(idx) { return $(uiMessagesAll[idx]).data('severity').indexOf('error') !== -1; }),
                uiMessageCollection = container.find('div.ui-message'),
                growlPlaceholderAll = container.is('.ui-growl-pl') ? container : container.find('.ui-growl-pl'),
                growlComponents = growlPlaceholderAll.filter(function(idx) { return $(growlPlaceholderAll[idx]).data('severity').indexOf('error') !== -1; });

            uiMessageCollection.html('').removeClass('ui-message-error ui-message-icon-only ui-widget ui-corner-all ui-helper-clearfix');

            for(var i = 0; i < uiMessages.length; i++) {
                var uiMessagesComponent = uiMessages.eq(i),
                globalOnly = uiMessagesComponent.data('global'),
                redisplay = uiMessagesComponent.data('redisplay'),
                showSummary = uiMessagesComponent.data('summary'),
                showDetail = uiMessagesComponent.data('detail');

                uiMessagesComponent.html('');

                for(var clientId in this.messages) {
                    var msgs = this.messages[clientId];

                    for(var j = 0; j < msgs.length; j++) {
                        var msg = msgs[j];

                        if(globalOnly || (msg.rendered && !redisplay)) {
                            continue;
                        }

                        if(uiMessagesComponent.children().length === 0) {
                            uiMessagesComponent.append('<div class="ui-messages-error ui-corner-all"><span class="ui-messages-error-icon"></span><ul></ul></div>');
                        }

                        var msgItem = $('<li></li>');

                        if(showSummary) {
                            msgItem.append('<span class="ui-messages-error-summary">' + PrimeFaces.escapeHTML(msg.summary) + '</span>');
                        }

                        if(showDetail) {
                            msgItem.append('<span class="ui-messages-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span>');
                        }

                        uiMessagesComponent.find('> .ui-messages-error > ul').append(msgItem);
                        msg.rendered = true;
                    }
                }
            }

            for(var i = 0; i < growlComponents.length; i++) {
                var growl = growlComponents.eq(i),
                redisplay = growl.data('redisplay'),
                globalOnly = growl.data('global'),
                showSummary = growl.data('summary'),
                showDetail = growl.data('detail'),
                growlWidget = PF(growl.data('widget'));

                growlWidget.removeAll();

                for(var clientId in this.messages) {
                    var msgs = this.messages[clientId];

                    for(var j = 0; j < msgs.length; j++) {
                        var msg = msgs[j];

                        if(globalOnly || (msg.rendered && !redisplay)) {
                            continue;
                        }

                        if (!showSummary) {
                            msg.summary = '';
                        }

                        if (!showDetail) {
                            msg.detail = '';
                        }

                        growlWidget.renderMessage(msg);
                        msg.rendered = true;
                    }
                }
            }

            for(var i = 0; i < uiMessageCollection.length; i++) {
                var uiMessage = uiMessageCollection.eq(i),
                target = uiMessage.data('target'),
                redisplay = uiMessage.data('redisplay');

                for(var clientId in this.messages) {
                    if(target === clientId) {
                        var msgs = this.messages[clientId];

                        for(var j = 0; j < msgs.length; j++) {
                            var msg = msgs[j];
                            if(msg.rendered && !redisplay) {
                                continue;
                            }

                            this.renderUIMessage(uiMessage, msg);
                            msg.rendered = true;
                        }
                    }
                }
            }
        },

        renderUIMessage: function(uiMessage, msg) {
            var display = uiMessage.data('display');

            if(display !== 'tooltip') {
                uiMessage.addClass('ui-message-error ui-widget ui-corner-all ui-helper-clearfix');

                if(display === 'both') {
                    uiMessage.append('<div><span class="ui-message-error-icon"></span><span class="ui-message-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span></div>');
                }
                else if(display === 'text') {
                    uiMessage.append('<span class="ui-message-error-detail">' + PrimeFaces.escapeHTML(msg.detail) + '</span>');
                }
                else if(display === 'icon') {
                    uiMessage.addClass('ui-message-icon-only')
                            .append('<span class="ui-message-error-icon" title="' + PrimeFaces.escapeHTML(msg.detail) + '"></span>');
                }
            }
            else {
                uiMessage.hide();
                $(PrimeFaces.escapeClientId(uiMessage.data('target'))).attr('title', PrimeFaces.escapeHTML(msg.detail));
            }
        },

        findUIMessage: function(clientId, uiMessageCollection) {
            for(var i = 0; i < uiMessageCollection.length; i++) {
                var uiMessage = uiMessageCollection.eq(i);
                if(uiMessage.data('target') === clientId)
                    return uiMessage;
            }

            return null;
        },

        getMessagesLength: function() {
            var length = 0, key;

            for(key in this.messages) {
                if(this.messages.hasOwnProperty(key))
                    length++;
            }

            return length;
        },

        isEmpty: function() {
            return this.getMessagesLength() === 0;
        },

        clear: function() {
            this.messages = {};
            this.elementGroups = [];
        },

        getLocaleSettings: function() {
            var localeKey = PrimeFaces.settings.locale,
            localeSettings = PrimeFaces.locales[localeKey];

            if(!localeSettings) {
                localeSettings = PrimeFaces.locales[localeKey.split('_')[0]];

                if(!localeSettings)
                    localeSettings = PrimeFaces.locales['en_US'];
            }

            return localeSettings;
        },

        isGroupValidated: function(name) {
            for(var i = 0; i < this.elementGroups.length; i++) {
                if(this.elementGroups[i] === name) {
                    return true;
                }
            }
            return false;
        },

        addElementGroup: function(name) {
            this.elementGroups.push(name);
        },

        getSubmittedValue: function(element) {
            var value;

            if(element.is(':radio')) {
                value = $('input:radio[name="' + element.attr('name') + '"]:checked').val();
            }
            else if(element.is(':checkbox')) {
                value = element.data('p-grouped') ? $('input:checkbox[name="' + element.attr('name') + '"]:checked').val(): element.prop('checked').toString();
            }
            else {
                value = element.val();
            }

            return (value === undefined) ? '': value;
        }
    };

    PrimeFaces.validator.Highlighter = {

        highlightLabel: function(forElement) {
            var label = $("label[for='" + forElement.attr('id') + "']");
            if (label.hasClass('ui-outputlabel')) {
                label.addClass('ui-state-error');
            }
        },

        unhighlightLabel: function(forElement) {
            var label = $("label[for='" + forElement.attr('id') + "']");
            if (label.hasClass('ui-outputlabel')) {
                label.removeClass('ui-state-error');
            }
        },

        types : {

            'default': {

                highlight: function(element) {
                    element.addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element);
                },

                unhighlight: function(element) {
                    element.removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                }
            },

            'booleanchkbox': {

                highlight: function(element) {
                    element.parent().next().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element);
                },

                unhighlight: function(element) {
                    element.parent().next().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                }

            },

            'manychkbox': {

                highlight: function(element) {
                    var custom = element.hasClass('ui-chkbox-clone'),
                    chkboxes;
                    
                    if(custom) {
                        var groupedInputs = $('input[name="' + element.attr('name') + '"].ui-chkbox-clone');
                        chkboxes = groupedInputs.parent().next();
                    }
                    else {
                        var container = element.closest('.ui-selectmanycheckbox');
                        chkboxes = container.find('div.ui-chkbox-box');
                    }

                    for(var i = 0; i < chkboxes.length; i++) {
                        chkboxes.eq(i).addClass('ui-state-error');
                    }
                },

                unhighlight: function(element) {
                    var custom = element.hasClass('ui-chkbox-clone'),
                    chkboxes;
                    
                    if(custom) {
                        var groupedInputs = $('input[name="' + element.attr('name') + '"].ui-chkbox-clone');
                        chkboxes = groupedInputs.parent().next();
                    }
                    else {
                        var container = element.closest('.ui-selectmanycheckbox');
                        chkboxes = container.find('div.ui-chkbox-box');
                    }

                    for(var i = 0; i < chkboxes.length; i++) {
                        chkboxes.eq(i).removeClass('ui-state-error');
                    }
                }

            },

            'listbox': {

                highlight: function(element) {
                    element.closest('.ui-inputfield').addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element.closest('.ui-inputfield'));
                },

                unhighlight: function(element) {
                    element.closest('.ui-inputfield').removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element.closest('.ui-inputfield'));
                }

            },

            'onemenu': {

                highlight: function(element) {
                    element.parent().siblings('.ui-selectonemenu-trigger').addClass('ui-state-error').parent().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(this.getFocusElement(element));
                },

                unhighlight: function(element) {
                    element.parent().siblings('.ui-selectonemenu-trigger').removeClass('ui-state-error').parent().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(this.getFocusElement(element));
                },

                getFocusElement: function(element) {
                    return element.closest('.ui-selectonemenu').find('.ui-helper-hidden-accessible > input');
                }
            },

            'spinner': {

                highlight: function(element) {
                    element.parent().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element.parent());
                },

                unhighlight: function(element) {
                    element.parent().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element.parent());
                }

            },

            'oneradio': {

                highlight: function(element) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        radios.eq(i).addClass('ui-state-error');
                    }
                    PrimeFaces.validator.Highlighter.highlightLabel(container);
                },

                unhighlight: function(element) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        radios.eq(i).removeClass('ui-state-error');
                    }
                    PrimeFaces.validator.Highlighter.unhighlightLabel(container);
                }

            },

            'booleanbutton': {

                highlight: function(element) {
                    element.parent().parent().addClass('ui-state-error');
                },

                unhighlight: function(element) {
                    element.parent().parent().removeClass('ui-state-error');
                }

            },

            'inputnumber': {

                highlight: function(element) {
                    var orginalInput = element.prev('input');
                    orginalInput.addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(orginalInput);

                    // see #3706
                    orginalInput.parent().addClass('ui-state-error');
                },

                unhighlight: function(element) {
                    var orginalInput = element.prev('input');
                    orginalInput.removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(orginalInput);

                    // see #3706
                    orginalInput.parent().removeClass('ui-state-error');
                }

            }
        }
    };
}