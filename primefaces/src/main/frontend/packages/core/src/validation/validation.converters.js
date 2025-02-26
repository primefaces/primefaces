// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {

    PrimeFaces.converter['jakarta.faces.Integer'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'jakarta.faces.converter.IntegerConverter.INTEGER',

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

    PrimeFaces.converter['jakarta.faces.Long'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'jakarta.faces.converter.LongConverter.LONG',

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

    PrimeFaces.converter['jakarta.faces.Double'] = {

        regex: /^[-+]?\d*(\.\d+)?[d]?$/,

        MESSAGE_ID: 'jakarta.faces.converter.DoubleConverter.DOUBLE',

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

    PrimeFaces.converter['jakarta.faces.Float'] = {

        regex: /^[-+]?\d+(\.\d+)?[f]?$/,

        MESSAGE_ID: 'jakarta.faces.converter.FloatConverter.FLOAT',

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

    PrimeFaces.converter['jakarta.faces.Short'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'jakarta.faces.converter.ShortConverter.SHORT',

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

    PrimeFaces.converter['jakarta.faces.BigInteger'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'jakarta.faces.converter.BigIntegerConverter.BIGINTEGER',

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

    PrimeFaces.converter['jakarta.faces.BigDecimal'] = {

        regex: /^[-+]?\d+(\.\d+)?[d]?$/,

        MESSAGE_ID: 'jakarta.faces.converter.BigDecimalConverter.DECIMAL',

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

    PrimeFaces.converter['jakarta.faces.Byte'] = {

        regex: /^-?\d+$/,

        MESSAGE_ID: 'jakarta.faces.converter.ByteConverter.BYTE',

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

    PrimeFaces.converter['jakarta.faces.Character'] = {

        MESSAGE_ID: 'jakarta.faces.converter.CharacterConverter.CHARACTER',

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

    PrimeFaces.converter['jakarta.faces.Boolean'] = {

        regex: /^[-+]?\d+$/,

        MESSAGE_ID: 'jakarta.faces.converter.BooleanConverter.BOOLEAN',

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

    PrimeFaces.converter['jakarta.faces.DateTime'] = {

        DATE_ID: 'jakarta.faces.converter.DateTimeConverter.DATE',
        TIME_ID: 'jakarta.faces.converter.DateTimeConverter.TIME',
        DATETIME_ID: 'jakarta.faces.converter.DateTimeConverter.DATETIME',

        convert: function(element, submittedValue) {
            if(submittedValue === null) {
                return null;
            }

            if(PrimeFaces.trim(submittedValue).length === 0) {
                return null;
            }

            var vc = PrimeFaces.validation.ValidationContext,
            javaPattern = element.data('p-pattern'),
            type = element.data('p-dttype'),
            datePattern = null,
            timePattern = null;
            
            if (typeof moment === 'undefined') {
                PrimeFaces.error("Moment.js is not loaded! Please enable 'primefaces.CLIENT_SIDE_VALIDATION' in web.xml!");
            }

            try {
                if (javaPattern) {
                    var patternTokens = javaPattern.split(" ");
                    for (var i = 0; i < patternTokens.length; i++) {
                        if (patternTokens[i].toLowerCase().indexOf('h') !== -1) {
                            timePattern = patternTokens[i];
                        }
                        else if (patternTokens[i].toLowerCase().indexOf('t') !== -1 && timePattern) {
                            timePattern = timePattern + " " + patternTokens[i];
                        }
                        else {
                            datePattern = patternTokens[i];
                        }
                    }
                }
                else {
                    datePattern = element.data('p-dspattern');
                    timePattern = element.data('p-tspattern');
                }

                // convert Java pattern into Moment pattern and return a Date()
                const convertDate = (submittedValue, format) => moment(submittedValue, moment().toMomentFormatString(format)).toDate();

                if (timePattern && datePattern) {
                    return convertDate(submittedValue, javaPattern);
                } else if (timePattern) {
                    return convertDate(submittedValue, timePattern);
                } else {
                    return convertDate(submittedValue, datePattern);
                }
            }
            catch(exception) {
                var now = moment().formatWithJDF(javaPattern);

                if(type === 'date')
                    throw vc.getMessage(this.DATE_ID, submittedValue, now, vc.getLabel(element));
                else if(type === 'time')
                    throw vc.getMessage(this.TIME_ID, submittedValue, now, vc.getLabel(element));
                else if(type === 'both')
                    throw vc.getMessage(this.DATETIME_ID, submittedValue, now, vc.getLabel(element));
            }
        }
    };

    PrimeFaces.converter['jakarta.faces.Number'] = {

        CURRENCY_ID: 'jakarta.faces.converter.NumberConverter.CURRENCY',
        NUMBER_ID: 'jakarta.faces.converter.NumberConverter.NUMBER',
        PATTERN_ID: 'jakarta.faces.converter.NumberConverter.PATTERN',
        PERCENT_ID: 'jakarta.faces.converter.NumberConverter.PERCENT',
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