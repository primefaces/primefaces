// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {

    PrimeFaces.converter['javax.faces.Integer'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.IntegerConverter.INTEGER',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 9346, vc.getLabel(element));
            }

            return parseInt(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.Long'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.LongConverter.LONG',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 98765432, vc.getLabel(element));
            }

            return parseInt(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.Double'] = {

        regex: /^[-+]?\d*(\.\d+)?[d]?$/,

        MESSAGE_ID: 'javax.faces.converter.DoubleConverter.DOUBLE',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 1999999, vc.getLabel(element));
            }

            return parseFloat(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.Float'] = {

        regex: /^[-+]?\d+(\.\d+)?[f]?$/,

        MESSAGE_ID: 'javax.faces.converter.FloatConverter.FLOAT',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 2000000000, vc.getLabel(element));
            }

            return parseFloat(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.Short'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.ShortConverter.SHORT',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 32456, vc.getLabel(element));
            }

            return parseInt(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.BigInteger'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.BigIntegerConverter.BIGINTEGER',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 9876, vc.getLabel(element));
            }

            return parseInt(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.BigDecimal'] = {

        regex: /^[-+]?\d+(\.\d+)?[d]?$/,

        MESSAGE_ID: 'javax.faces.converter.BigDecimalConverter.DECIMAL',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, 198.23, vc.getLabel(element));
            }

            return parseFloat(submittedValue);
        }
    };

    PrimeFaces.converter['javax.faces.Byte'] = {

        regex: /^-?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.ByteConverter.BYTE',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            if(!this.regex.test(submittedValue)) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, -12, vc.getLabel(element));
            }
            else {
                var byteValue = parseInt(submittedValue);

                if(byteValue < -128 || byteValue > 127)
                    throw vc.getMessage(this.MESSAGE_ID, submittedValue, -12, vc.getLabel(element));
                else
                    return byteValue;
            }
        }
    };

    PrimeFaces.converter['javax.faces.Character'] = {

        MESSAGE_ID: 'javax.faces.converter.CharacterConverter.CHARACTER',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            try {
                return submittedValue.charAt(0);
            }
            catch(exception) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, vc.getLabel(element));
            }
        }
    };

    PrimeFaces.converter['javax.faces.Boolean'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'javax.faces.converter.BooleanConverter.BOOLEAN',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext;

            try {
                return ((submittedValue === 'true' || submittedValue === 'on' || submittedValue === 'yes') ? true : false);
            }
            catch(exception) {
                throw vc.getMessage(this.MESSAGE_ID, submittedValue, vc.getLabel(element));
            }
        }
    };

    PrimeFaces.converter['javax.faces.DateTime'] = {

        DATE_ID: 'javax.faces.converter.DateTimeConverter.DATE',
        TIME_ID: 'javax.faces.converter.DateTimeConverter.TIME',
        DATETIME_ID: 'javax.faces.converter.DateTimeConverter.DATETIME',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext,
            pattern = element.data('p-pattern'),
            type = element.data('p-dttype'),
            datePattern = null,
            timePattern = null;

            var locale = PrimeFaces.getLocaleSettings();

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
    };

    PrimeFaces.converter['javax.faces.Number'] = {

        CURRENCY_ID: 'javax.faces.converter.NumberConverter.CURRENCY',
        NUMBER_ID: 'javax.faces.converter.NumberConverter.NUMBER',
        PATTERN_ID: 'javax.faces.converter.NumberConverter.PATTERN',
        PERCENT_ID: 'javax.faces.converter.NumberConverter.PERCENT',
        REGEX: /^[-+]?\d+(\,\d+)?(\.\d+)?[d]?$/,

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext,
            locale = PrimeFaces.getLocaleSettings(),
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
    };
}