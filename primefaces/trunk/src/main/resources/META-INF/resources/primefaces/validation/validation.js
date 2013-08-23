/**
 * PrimeFaces Client Side Validation Framework
 */
PrimeFaces.locales['en_US'] = {
    decimalSeparator: '.',
    groupingSeparator: ',',
    messages: {
        'javax.faces.component.UIInput.REQUIRED': '{0}: Validation Error: Value is required.',
        'javax.faces.converter.IntegerConverter.INTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'javax.faces.converter.IntegerConverter.INTEGER_detail': '{2}: \'{0}\' must be a number between -2147483648 and 2147483647 Example: {1}',
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
        'javax.faces.converter.NumberConverter.NUMBER': '{2}: \'{0}\' could not be understood as a date.',
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
};

PrimeFaces.validator = {

    'javax.faces.Length': {

        MINIMUM_MESSAGE_ID: 'javax.faces.validator.LengthValidator.MINIMUM',

        MAXIMUM_MESSAGE_ID: 'javax.faces.validator.LengthValidator.MAXIMUM',

        validate: function(element) {
            var length = element.val().length,
            min = element.data('p-minlength'),
            max = element.data('p-maxlength'),
            mc = PrimeFaces.util.MessageContext;

            if(max !== undefined && length > max) {
                throw mc.getMessage(this.MAXIMUM_MESSAGE_ID, max, mc.getLabel(element));
            }

            if(min !== undefined && length < min) {
                throw mc.getMessage(this.MINIMUM_MESSAGE_ID, min, mc.getLabel(element));
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
                mc = PrimeFaces.util.MessageContext;

                if(!this.regex.test(element.val())) {
                    throw mc.getMessage(this.TYPE_MESSAGE_ID, mc.getLabel(element));
                }

                if((max !== undefined && min !== undefined) && (value < min || value > max)) {
                    throw mc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, mc.getLabel(element));
                }
                else if((max !== undefined && min === undefined) && (value > max)) {
                    throw mc.getMessage(this.MAXIMUM_MESSAGE_ID, max, mc.getLabel(element));
                }
                else if((min !== undefined && max === undefined) && (value < min)) {
                    throw mc.getMessage(this.MINIMUM_MESSAGE_ID, min, mc.getLabel(element));
                }
            }
        }
    },

    'javax.faces.DoubleRange': {

        MINIMUM_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.MINIMUM',
        MAXIMUM_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.MAXIMUM',
        NOT_IN_RANGE_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.NOT_IN_RANGE',
        TYPE_MESSAGE_ID: 'javax.faces.validator.DoubleRangeValidator.TYPE',
        regex: /^[-+]?\d+(\.\d+)?[d]?$/,

        validate: function(element, value) {
            if(value !== null) {
                var min = element.data('p-minvalue'),
                max = element.data('p-maxvalue'),
                mc = PrimeFaces.util.MessageContext;

                if(!this.regex.test(element.val())) {
                    throw mc.getMessage(this.TYPE_MESSAGE_ID, mc.getLabel(element));
                }

                if((max !== undefined && min !== undefined) && (value < min || value > max)) {
                    throw mc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, mc.getLabel(element));
                }
                else if((max !== undefined && min === undefined) && (value > max)) {
                    throw mc.getMessage(this.MAXIMUM_MESSAGE_ID, max, mc.getLabel(element));
                }
                else if((min !== undefined && max === undefined) && (value < min)) {
                    throw mc.getMessage(this.MINIMUM_MESSAGE_ID, min, mc.getLabel(element));
                }
            }
        }
    },

    'javax.faces.RegularExpression': {

        PATTERN_NOT_SET_MESSAGE_ID: 'javax.faces.validator.RegexValidator.PATTERN_NOT_SET',
        NOT_MATCHED_MESSAGE_ID: 'javax.faces.validator.RegexValidator.NOT_MATCHED',
        MATCH_EXCEPTION_MESSAGE_ID: 'javax.faces.validator.RegexValidator.MATCH_EXCEPTION',

        validate: function(element, value) {
            if(!value) {
                return;
            }

            var pattern = element.data('p-regex'),
            mc = PrimeFaces.util.MessageContext;

            if(!pattern) {
                throw mc.getMessage(this.PATTERN_NOT_SET_MESSAGE_ID);
            }

            var regex = new RegExp(pattern);
            if(!regex.test(value)) {
                throw mc.getMessage(this.NOT_MATCHED_MESSAGE_ID, pattern);
            }
        }
    }
};

PrimeFaces.converter = {

    'javax.faces.Integer': {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.IntegerConverter.INTEGER',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 9346, mc.getLabel(element));
            }

            return parseInt(value);
        }
    },

    'javax.faces.Double': {

        regex: /^[-+]?\d+(\.\d+)?[d]?$/,

        MESSAGE_ID: 'javax.faces.converter.DoubleConverter.DOUBLE',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 1999999, mc.getLabel(element));
            }

            return parseFloat(value);
        }
    },

    'javax.faces.Float': {

        regex: /^[-+]?\d+(\.\d+)?[f]?$/,

        MESSAGE_ID: 'javax.faces.converter.FloatConverter.FLOAT',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 2000000000, mc.getLabel(element));
            }

            return parseFloat(value);
        }
    },

    'javax.faces.Short': {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.ShortConverter.SHORT',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 32456, mc.getLabel(element));
            }

            return parseInt(value);
        }
    },

    'javax.faces.BigInteger': {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.BigIntegerConverter.BIGINTEGER',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 9876, mc.getLabel(element));
            }

            return parseInt(value);
        }
    },

    'javax.faces.BigDecimal': {

        regex: /^[-+]?\d+(\.\d+)?[d]?$/,

        MESSAGE_ID: 'javax.faces.converter.BigDecimalConverter.DECIMAL',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 198.23, mc.getLabel(element));
            }

            return parseFloat(value);
        }
    },

    'javax.faces.Byte': {

        regex: /^\d+$/,

        MESSAGE_ID: 'javax.faces.converter.ByteConverter.BYTE',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            if(!this.regex.test(value)) {
                throw mc.getMessage(this.MESSAGE_ID, value, 9346, mc.getLabel(element));
            }
            else {
                var byteValue = parseInt(value);

                if(byteValue < 0 || byteValue > 255)
                    throw mc.getMessage(this.MESSAGE_ID, value, 9346, mc.getLabel(element));
                else
                    return byteValue;
            }
        }
    },

    'javax.faces.Character': {

        MESSAGE_ID: 'javax.faces.converter.CharacterConverter.CHARACTER',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            try {
                return value.charAt(0);
            }
            catch(exception) {
                throw mc.getMessage(this.MESSAGE_ID, value, mc.getLabel(element));
            }
        }
    },

    'javax.faces.Boolean': {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.BooleanConverter.BOOLEAN',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext;

            if($.trim(value).length === 0) {
                return null;
            }

            try {
                return (value === 'true' ? true : false);  
            }
            catch(exception) {
                throw mc.getMessage(this.MESSAGE_ID, value, mc.getLabel(element));
            }
        }
    },

    'javax.faces.DateTime': {

        DATE_ID: 'javax.faces.converter.DateTimeConverter.DATE',
        TIME_ID: 'javax.faces.converter.DateTimeConverter.TIME',
        DATETIME_ID: 'javax.faces.converter.DateTimeConverter.DATETIME',

        convert: function(element) {
            var value = element.val(),
            mc = PrimeFaces.util.MessageContext,
            pattern = element.data('p-pattern'),
            type = element.data('p-dttype');

            if($.trim(value).length === 0) {
                return null;
            }

            var locale = PrimeFaces.locales[PrimeFaces.settings.locale];

            try {
                return $.datepicker.parseDate(pattern, value, locale);
            }
            catch(exception) {
                var now = $.datepicker.formatDate(pattern, new Date(), locale);

                if(type === 'date')
                    throw mc.getMessage(this.DATE_ID, value, now, mc.getLabel(element));
                else if(type === 'time')
                    throw mc.getMessage(this.TIME_ID, value, now, mc.getLabel(element));
                else if(type === 'both')
                    throw mc.getMessage(this.DATETIME_ID, value, now, mc.getLabel(element));
            }
        }
    },
            
    'javax.faces.Number': {

        CURRENCY_ID: 'javax.faces.converter.NumberConverter.CURRENCY',
        NUMBER_ID: 'javax.faces.converter.NumberConverter.NUMBER',
        PATTERN_ID: 'javax.faces.converter.NumberConverter.PATTERN',
        PERCENT_ID: 'javax.faces.converter.NumberConverter.PERCENT',
        REGEX: /^[-+]?\d+(\,\d+)?(\.\d+)?[d]?$/,

        convert: function(element) {
            var value = $.trim(element.val()),
            mc = PrimeFaces.util.MessageContext,
            locale = PrimeFaces.locales[PrimeFaces.settings.locale],
            type = element.data('p-notype'),
            maxIntegerDigits = element.data('p-maxint'),
            minFractionDigits = element.data('p-minfrac'),
            integerOnly = element.data('p-intonly');
            
            if(value.length === 0) {
                return null;
            }
            
            if(type === 'currency') {
                var currencySymbol = element.data('p-curs');
        
                if(currencySymbol) {
                    if(value.indexOf(currencySymbol) === -1)
                        throw mc.getMessage(this.CURRENCY_ID, value, currencySymbol + '100', mc.getLabel(element));
                    else
                        value = value.substring(currencySymbol.length);
                }
            }
            else if(type === 'percent') {
                if(value.lastIndexOf('%') !== (value.length - 1))
                    throw mc.getMessage(this.PERCENT_ID, value, '50%', mc.getLabel(element));
                else
                    value = value.replace(/%/g, '');
            }
            
            if(!this.REGEX.test(value)) {
                throw mc.getMessage(this.NUMBER_ID, value, 50, mc.getLabel(element));
            }
            
            var tokens = value.split(locale.decimalSeparator),
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
}

PrimeFaces.vi = function(element) {
    this.validateInstant(element);
}

PrimeFaces.validate = function(cfg) {
    var mc = PrimeFaces.util.MessageContext,
    form = $(cfg.s).closest('form');

    if(cfg.a && cfg.p) {
        var clientIds = PrimeFaces.Expressions.resolveComponents(cfg.p),
        inputs = $();

        for(var i = 0; i < clientIds.length; i++) {
            if(clientIds[i]) {
                var component = $(PrimeFaces.escapeClientId(clientIds[i]));
                if(component.is(':input'))
                    inputs = inputs.add(component);
                else
                    inputs = inputs.add(component.find(':input:visible:enabled:not(:button)'));
            }
        }

        this.validateInputs(inputs);
    }
    else {
        var inputs = form.find(':input:visible:enabled:not(:button)');
        this.validateInputs(inputs);
    }

    if(mc.isEmpty()) {
        return true;
    }
    else {
        mc.renderMessages(form.find('div.ui-messages'), form.find('div.ui-message'));
        return false;
    }
}

PrimeFaces.validateInputs = function(inputs) {
    for(var i = 0; i < inputs.length; i++) {
        this.validateInput(inputs.eq(i));
    }
}

PrimeFaces.validateInput = function(element) {
    var mc = PrimeFaces.util.MessageContext,
    submittedValue = element.val(),
    clientId = element.attr('id'),
    value = submittedValue,
    valid = true,
    converterId = element.data('p-con');

    if(converterId) {
        try {
            value = PrimeFaces.converter[converterId].convert(element);
        }
        catch(ce) {
            var converterMessageStr = element.data('p-cmsg'),
            converterMsg = (converterMessageStr) ? {summary:converterMessageStr,detail:converterMessageStr} : ce;
            valid = false;
            mc.addMessage(clientId, converterMsg);
        }
    }

    if(valid && element.data('p-required') && submittedValue === '') {
        var requiredMessageStr = element.data('p-rmsg'),
        requiredMsg = (requiredMessageStr) ? {summary:requiredMessageStr,detail:requiredMessageStr} : mc.getMessage('javax.faces.component.UIInput.REQUIRED', mc.getLabel(element));
        mc.addMessage(clientId, requiredMsg);

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
                        validator.validate(element, value);
                    }
                    catch(ve) {
                        var validatorMessageStr = element.data('p-vmsg'),
                        validatorMsg = (validatorMessageStr) ? {summary:validatorMessageStr,detail:validatorMessageStr} : ve;
                        valid = false;
                        mc.addMessage(clientId, validatorMsg);
                    }
                }
            }
        }
    }

    if(!valid)
        element.addClass('ui-state-error');
    else
        element.removeClass('ui-state-error');
}

PrimeFaces.validateInstant = function(id) {
    var mc = PrimeFaces.util.MessageContext,
    element = $(PrimeFaces.escapeClientId(id)),
    clientId = element.attr('id'),
    uiMessageId = element.data('uiMessageId'),
    uiMessage = null;

    if(uiMessageId) {
        uiMessage = $(PrimeFaces.escapeClientId(uiMessageId));
    }
    else {
        uiMessage = mc.findUIMessage(clientId, element.closest('form').find('div.ui-message'));
        element.data('uiMessageId', uiMessage.attr('id'));
    }

    if(uiMessage) {
        uiMessage.html('').removeClass('ui-message-error ui-message-icon-only ui-widget ui-corner-all ui-helper-clearfix');
    }

    this.validateInput(element);

    if(!mc.isEmpty()) {
        mc.renderUIMessage(uiMessage, mc.messages[clientId][0]);
    }

    mc.clear();
}

PrimeFaces.util.MessageContext = {

    messages: {},

    addMessage: function(id, msg) {
        if(!this.messages[id]) {
            this.messages[id] = [];
        }

        this.messages[id].push(msg);
    },

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
    },

    renderMessages: function(uiMessages, uiMessageCollection) {
        uiMessageCollection.html('').removeClass('ui-message-error ui-message-icon-only ui-widget ui-corner-all ui-helper-clearfix');

        var shouldRenderUIMessages = uiMessages&&uiMessages.length&&!uiMessages.data('global');
        if(shouldRenderUIMessages) {
            uiMessages.html('');
            uiMessages.append('<div class="ui-messages-error ui-corner-all"><span class="ui-messages-error-icon"></span><ul></ul></div>');

            var messageList = uiMessages.find('> .ui-messages-error > ul'),
            showSummary = uiMessages.data('summary'),
            showDetail = uiMessages.data('detail');
        }

        for(var clientId in this.messages) {
            var msgs = this.messages[clientId],
            uiMessage = this.findUIMessage(clientId, uiMessageCollection);

            for(var i = 0; i < msgs.length; i++) {
                var msg = msgs[i];

                if(shouldRenderUIMessages) {        
                    var msgItem = $('<li></li>');

                    if(showSummary)
                        msgItem.append('<span class="ui-messages-error-summary">' + msg.summary + '</span>');

                    if(showDetail)
                        msgItem.append('<span class="ui-messages-error-detail">' + msg.detail + '</span>');

                    messageList.append(msgItem);                    
                }

                if(uiMessage.length) {
                    this.renderUIMessage(uiMessage, msg);
                }
            }
        }

        this.clear();
    },

    renderUIMessage: function(uiMessage, msg) {
        uiMessage.addClass('ui-message-error ui-widget ui-corner-all ui-helper-clearfix');
        var display = uiMessage.data('display');

        if(display === 'both') {
            uiMessage.append('<span class="ui-message-error-icon"></span>')
                .append('<span class="ui-message-error-detail">' + msg.detail + '</span>');
        } 
        else if(display === 'text') {
            uiMessage.append('<span class="ui-message-error-detail">' + msg.detail + '</span>');
        } 
        else if(display === 'icon') {
            uiMessage.addClass('ui-message-icon-only')
                    .append('<span class="ui-message-error-icon" title="' + msg.detail + '"></span>');
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
    }
}

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
            throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID);
        }
    }
};

PrimeFaces.validator['Null'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Null.message',
  
    validate: function(element, value) {
        if(value !== null || value !== undefined) {
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
        
        if(length < min || length > max) {
            var msgStr = element.data('p-size-msg'),
            msg = msgStr === null ? {summary:msgStr, detail: msgStr} : mc.getMessage(this.MESSAGE_ID, min, max);
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

PrimeFaces.validator['AssertTrue'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.AssertTrue.message',
  
    validate: function(element, value) {
        if(value === false) {
            throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID);
        }
    }
};

PrimeFaces.validator['AssertFalse'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.AssertFalse.message',
  
    validate: function(element, value) {
        if(value === true) {
            throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID);
            
        }
    }
};

PrimeFaces.validator['Past'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Past.message',
  
    validate: function(element, value) {
        if(value !== null) {
            value = $.datepicker.formatDate("yymmdd", value);
            var now = $.datepicker.formatDate("yymmdd", new Date());

            if(ParseInt(value)> ParseInt(now))
                throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID);
        }
    }
};

PrimeFaces.validator['Future'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Future.message',
  
    validate: function(element, value) {
        if(value !== null) {
            value = $.datepicker.formatDate("yymmdd", value);
            var now = $.datepicker.formatDate("yymmdd", new Date());

            if(ParseInt(value)<= ParseInt(now))
                throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID);
        }
    }
};

PrimeFaces.validator['Pattern'] = {
  
    MESSAGE_ID: 'javax.validation.constraints.Pattern.message',
     
    validate: function(element, value) {
        if(value !== null) {
            var pattern = element.data('p-pattern').val();
            
            if(!pattern.test(value))
                throw PrimeFaces.util.MessageContext.getMessage(this.MESSAGE_ID, pattern);
        }
    }
};

PrimeFaces.validator['Digits'] = {
    
    MESSAGE_ID: 'javax.validation.constraints.Digits.message',
    regex: /^[-+]?\d+(\.\d+)?[d]?$/,
    
    validate: function(element, value) {
        if(value !== null) {
            var digitsInteger = element.data('p-dintvalue'),
            digitsFraction = element.data('p-dfracvalue'),
            mc = PrimeFaces.util.MessageContext,
            bundle = PrimeFaces.locales['en_US'];

            if(bundle){
                value = $.trim(value.replace(',', ''));
                if(!this.regex.test(value))
                    throw mc.getMessage(this.MESSAGE_ID, digitsInteger, digitsFraction);
                
                var valueSplitArray = value.split(bundle.decimalSeparator);
                var a =(valueSplitArray[0].length > digitsInteger) || (valueSplitArray[1].length > digitsFraction);
                if((valueSplitArray[0].length > digitsInteger)||(valueSplitArray[1].length > digitsFraction))
                    throw mc.getMessage(this.MESSAGE_ID, digitsInteger, digitsFraction);
            }
        }
    }
};